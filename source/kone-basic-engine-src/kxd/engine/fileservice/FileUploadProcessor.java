package kxd.engine.fileservice;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import javax.naming.NamingException;

import kxd.engine.cache.beans.sts.CachedFileHost;
import kxd.engine.helper.CacheHelper;
import kxd.net.connection.ftp.PooledFtpConnection;
import kxd.net.connection.jndi.Lookuper;
import kxd.net.naming.LoopNamingContext;
import kxd.remote.scs.beans.BaseFileItem;
import kxd.remote.scs.interfaces.FileListBeanRemote;
import kxd.util.DataSecurity;

public class FileUploadProcessor extends FileProcessor {
	private CachedFileHost fileHost;

	public FileUploadProcessor(String userId, String userPwd,
			short fileCategoryId) {
		super(userId, userPwd, fileCategoryId);
		if (userOwner == null)
			throw new FileException("非法用户或密码不正确");
		fileHost = CacheHelper.fileHostMap.get(fileCategory.getFileHost());
		if (fileHost == null)
			throw new FileException("文件主机ID[" + fileCategory.getFileHost()
					+ "]不存在");
	}

	/**
	 * 即时上传文件
	 * 
	 * @param loginUserId
	 * @param fileName
	 * @param stream
	 */
	public BaseFileItem appendFile(byte[] data, long loginUserId, int startPos,
			boolean complete, BaseFileItem item) throws IOException,
			NamingException {
		try {
			String savedFileName = item.getRealFileName();
			item.setFileItemId(DataSecurity.md5(fileCategory.getId() + "/"
					+ savedFileName));
			if (startPos <= 0) {
				saveFile(fileHost, fileCategory.getIdString() + "/"
						+ savedFileName + ".tmp", data);
			} else
				appendFile(fileHost, fileCategory.getIdString() + "/"
						+ savedFileName + ".tmp", data);
			if (startPos < 0) {
				LoopNamingContext context = new LoopNamingContext("db");
				try {
					FileListBeanRemote bean = context.lookup(
							Lookuper.JNDI_TYPE_EJB, "kxd-ejb-fileListBean",
							FileListBeanRemote.class);
					bean.add(loginUserId, item);
				} finally {
					context.close();
				}
			}
			if (complete) {
				if (fileHost.isLocalStored()) {
					File file = new File(fileHost.getFileRootDir() + "/"
							+ fileCategory.getIdString() + "/" + savedFileName
							+ ".tmp");
					if (file.exists()) {
						file.renameTo(new File(fileHost.getFileRootDir() + "/"
								+ fileCategory.getIdString() + "/"
								+ savedFileName));
					}
				} else {
					PooledFtpConnection con = getFtpConnection(fileHost);
					try {
						String fn = encodeFtpFilePath(fileHost,
								fileCategory.getIdString() + "/"
										+ savedFileName);
						con.rename(fn + ".tmp", fn);
					} finally {
						con.close();
					}
				}
			}
			if (startPos == 0 || complete)
				cachedFileMap.remove(item.getFileItemId());
			return item;
		} catch (NoSuchAlgorithmException e) {
			throw new FileException(e);
		} catch (InterruptedException e) {
			throw new IOException(e);
		}
	}

	public BaseFileItem uploadFile(long loginUserId, String fileName,
			byte[] data) throws IOException, NamingException {
		try {
			fileName = fileName.replace("\\", "/");
			fileName = fileName.replace("//", "/").trim();
			if (fileName.startsWith("/"))
				fileName = fileName.substring(1);
			BaseFileItem item = new BaseFileItem();
			item.setFileItemId(DataSecurity.md5(fileCategory.getId() + "/"
					+ fileName));
			int index = fileName.lastIndexOf("/");
			if (index > -1) {
				item.setSavePath(fileName.substring(0, index));
				item.setOriginalFileName(fileName.substring(index + 1));
			} else
				item.setOriginalFileName(fileName);
			item.setFileCategoryId(fileCategory.getId());
			item.setFileOwnerId(userOwner.getId());
			item.setFileHostId(fileHost.getId());
			LoopNamingContext context = new LoopNamingContext("db");
			try {
				FileListBeanRemote bean = context.lookup(
						Lookuper.JNDI_TYPE_EJB, "kxd-ejb-fileListBean",
						FileListBeanRemote.class);
				bean.add(loginUserId, item);
			} finally {
				context.close();
			}
			saveFile(fileHost,
					fileCategory.getIdString() + "/" + item.getRealFileName(),
					data);
			cachedFileMap.remove(item.getFileItemId());
			return item;
		} catch (NoSuchAlgorithmException e) {
			throw new FileException(e);
		} catch (InterruptedException e) {
			throw new FileException(e);
		}
	}

	public BaseFileItem updateFile(long loginUserId, String oldFileName,
			String fileName, byte[] data) throws IOException, NamingException {
		try {
			fileName = fileName.replace("\\", "/");
			fileName = fileName.replace("//", "/").trim();
			if (fileName.startsWith("/"))
				fileName = fileName.substring(1);
			BaseFileItem item = new BaseFileItem();
			String oldFileItemId = DataSecurity.md5(fileCategory.getId() + "/"
					+ oldFileName);
			item.setFileItemId(DataSecurity.md5(fileCategory.getId() + "/"
					+ fileName));
			item.setFileCategoryId(fileCategory.getId());
			item.setFileOwnerId(userOwner.getId());
			item.setFileHostId(fileHost.getId());
			int index = fileName.lastIndexOf("/");
			if (index > -1) {
				item.setSavePath(fileName.substring(0, index));
				item.setOriginalFileName(fileName.substring(index + 1));
			} else
				item.setOriginalFileName(fileName);
			CachedFileHost oldfh = fileHost;
			BaseFileItem oldItem = null;
			LoopNamingContext context = new LoopNamingContext("db");
			try {
				FileListBeanRemote bean = context.lookup(
						Lookuper.JNDI_TYPE_EJB, "kxd-ejb-fileListBean",
						FileListBeanRemote.class);
				oldItem = bean.edit(loginUserId, oldFileItemId, item);
				if (oldItem != null
						&& oldItem.getFileHostId() != fileHost.getId()) {
					oldfh = CacheHelper.fileHostMap
							.get(oldItem.getFileHostId());
				}
			} finally {
				context.close();
			}
			if (oldItem != null && !oldFileItemId.equals(item.getId())) {
				cachedFileMap.remove(oldFileItemId);
				if (oldItem != null && oldfh != null) {
					File file = new File(oldfh.getFileRootDir() + "/"
							+ fileCategory.getIdString() + "/"
							+ oldItem.getRealFileName());
					if (file.exists())
						file.delete();
				}
			}
			saveFile(fileHost,
					fileCategory.getIdString() + "/" + item.getRealFileName(),
					data);
			cachedFileMap.remove(item.getFileItemId());
			return item;
		} catch (NoSuchAlgorithmException e) {
			throw new FileException(e);
		} catch (InterruptedException e) {
			throw new FileException(e);
		}
	}

	public CachedFileHost getFileHost() {
		return fileHost;
	}

	public String getHttpUrl(String fileName) {
		String f = fileHost.getRealHttpUrlRoot();
		if (f != null)
			f = f.trim();
		if (f == null || f.isEmpty()) {
			return "/fileService?filecategory=" + fileCategory.getIdString()
					+ "&filename=" + fileName;
		}
		if (f.endsWith("/"))
			f = f.substring(0, f.length() - 1);
		if (fileHost.getHttpUrlPrefix() != null) {
			String u = fileHost.getHttpUrlPrefix().trim();
			if (u.startsWith("/"))
				f += u;
			else
				f += "/" + u;
		}
		if (f.endsWith("/"))
			f = f.substring(0, f.length() - 1);
		f += "/" + fileCategory.getIdString();
		if (fileName.startsWith("/"))
			f += fileName;
		else
			f += "/" + fileName;
		return f;
	}
}
