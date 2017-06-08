package kxd.engine.fileservice;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

import javax.naming.NamingException;

import kxd.engine.cache.CachedMap;
import kxd.engine.cache.beans.sts.CachedFileCategory;
import kxd.engine.cache.beans.sts.CachedFileHost;
import kxd.engine.cache.beans.sts.CachedFileItem;
import kxd.engine.cache.beans.sts.CachedFileOwner;
import kxd.engine.cache.beans.sts.CachedFileUser;
import kxd.engine.helper.CacheHelper;
import kxd.net.connection.CommonConnectionPool;
import kxd.net.connection.ConnectionPoolList;
import kxd.net.connection.ConnectionPoolListMap;
import kxd.net.connection.LoopConnectionPoolList;
import kxd.net.connection.ftp.FtpConnectionCreator;
import kxd.net.connection.ftp.PooledFtpConnection;
import kxd.net.connection.jndi.Lookuper;
import kxd.net.naming.LoopNamingContext;
import kxd.remote.scs.beans.BaseFileItem;
import kxd.remote.scs.interfaces.FileListBeanRemote;
import kxd.remote.scs.util.emun.FileCachedType;
import kxd.util.DataSecurity;
import kxd.util.DateTime;
import kxd.util.ExceptionCreator;
import kxd.util.memcached.MemCachedException;

import org.apache.log4j.Logger;

public class FileProcessor {
	static private final Logger logger = Logger.getLogger(FileProcessor.class);
	static final int MAX_FILESIZE = 10 * 1024 * 1024;

	static ConcurrentHashMap<String, CachedFile> cachedFileMap = new ConcurrentHashMap<String, CachedFile>();

	static final String FILEITEM_NAMESPACE = "$FILEITEM$";

	static ConnectionPoolListMap<ConnectionPoolList<PooledFtpConnection, IOException>> ftpMap = new ConnectionPoolListMap<ConnectionPoolList<PooledFtpConnection, IOException>>();
	static ConcurrentHashMap<Short, CachedMap<String, CachedFileItem>> fileItemsMap = new ConcurrentHashMap<Short, CachedMap<String, CachedFileItem>>();

	@SuppressWarnings({ "unchecked", "rawtypes" })
	static synchronized ConnectionPoolList<PooledFtpConnection, IOException> getConnectionPool(
			CachedFileHost fileHost) {
		ConnectionPoolList<PooledFtpConnection, IOException> r = ftpMap
				.get(fileHost.getFtpHost());
		if (r == null) {
			r = new LoopConnectionPoolList<PooledFtpConnection, IOException>(
					new ExceptionCreator<IOException>() {
						@Override
						public IOException createException(String msg,
								Throwable t) {
							return new IOException(msg, t);
						}
					});
			ftpMap.put(fileHost.getFtpHost(), r);
			String url = fileHost.getFtpHost();
			int port = 21;
			int index = url.indexOf(":");
			if (index > 0) {
				port = Integer.valueOf(url.substring(index + 1));
				url = url.substring(0, index);
			}
			r.add(new CommonConnectionPool<PooledFtpConnection, IOException>(
					new FtpConnectionCreator(PooledFtpConnection.class, "ftp",
							url, port, fileHost.getFtpUser(), fileHost
									.getFtpPasswd()), 20));
		}
		return r;
	}

	static PooledFtpConnection getFtpConnection(CachedFileHost fileHost)
			throws InterruptedException, IOException {
		return getConnectionPool(fileHost).get(0).getConnection();
	}

	CachedFileOwner userOwner;

	CachedFileCategory fileCategory;

	public FileProcessor(String userId, String userPwd, short fileCategoryId) {
		super();
		fileCategory = CacheHelper.fileCategoryMap.get(fileCategoryId);
		if (fileCategory == null)
			throw new FileException("文件分类ID[" + fileCategoryId + "]不存在");

		if (userId != null && userPwd != null) {
			CachedFileUser user = CacheHelper.fileUserMap.get(userId);
			if (user != null) {
				if (user.getFileUserPwd().equals(userPwd)) {
					userOwner = user.getFileOwner();
				}
			}
		}
	}

	protected String encodeFtpFilePath(CachedFileHost fh, String fileName) {
		fileName = fileName.replace("//", "/");
		String root = fh.getFileRootDir();
		if (root == null)
			root = "/";
		else {
			if (!root.endsWith("/")) {
				root += "/";
			}
			if (!root.startsWith("/"))
				root = "/" + root;
		}
		if (fileName.startsWith("/"))
			fileName = fileName.substring(1);
		return root + fileName;
	}

	public FileProperty getFileProperty(String fileName, boolean isTmpFile)
			throws InterruptedException, IOException, NamingException {
		try {
			String fileid = DataSecurity.md5(fileCategory.getIdString() + "/"
					+ fileName);
			CachedMap<String, CachedFileItem> map = CacheHelper
					.getFileItemMap(fileCategory);
			CachedFileItem item = map.get(fileid);
			if (item == null)
				throw new FileException("获取文件属性失败：找不到文件["
						+ fileCategory.getIdString() + "," + fileName
						+ "](fileid=" + fileid + ")");
			if (fileCategory.getCachedType().equals(FileCachedType.LOCAL)) { // 本地缓存模式
				if (DateTime.secondsBetween(item.getLastUpdateTime(),
						System.currentTimeMillis()) < 180) {
					logger.debug("从缓存内获取文件属性[" + fileCategory.getIdString()
							+ "," + fileName + "]");
					if (item.getLastModified() == null) {
						return new FileProperty(fileCategory.getId(), fileName,
								"", 0, "");
					} else
						return new FileProperty(fileCategory.getId(), fileName,
								new DateTime(item.getLastModified())
										.format("yyyy/MM/dd HH:mm:ss"),
								item.getSize(), item.getMd5());
				}
			}
			logger.debug("获取文件属性[" + fileCategory.getIdString() + ","
					+ fileName + "](fileid=" + fileid + ")");
			item.setLastUpdateTime(System.currentTimeMillis());
			CachedFileHost fh = CacheHelper.fileHostMap.get(item
					.getFileHostId());
			if (fh == null)
				throw new FileException("更新文件失败：找不到文件主机["
						+ item.getFileHostId() + "](fileid=" + fileid + ")");
			FileProperty ret;
			long lastModified = 0, size = 0;
			String savedFileName = item.getRealFileName();
			if (isTmpFile)
				savedFileName += ".tmp";
			if (fh.isLocalStored()) { // 本地存储
				File file = new File(fh.getFileRootDir() + "/"
						+ fileCategory.getIdString() + "/" + savedFileName);
				if (file.exists()) {
					ret = new FileProperty(fileCategory.getId(), fileName,
							new DateTime(file.lastModified())
									.format("yyyy/MM/dd HH:mm:ss"),
							file.length(), item.getMd5());
					lastModified = file.lastModified();
					size = file.length();
				} else
					ret = new FileProperty(fileCategory.getId(), fileName, "",
							0, "");
			} else { // 文件服务器存储
				PooledFtpConnection con = getFtpConnection(fh);
				try {

					long[] r = con.lastModifiedAndSize(encodeFtpFilePath(fh,
							fileCategory.getIdString() + "/" + savedFileName));
					if (r != null) {
						lastModified = r[0];
						size = r[1];
						ret = new FileProperty(fileCategory.getId(), fileName,
								new DateTime(r[0])
										.format("yyyy/MM/dd HH:mm:ss"), r[1],
								item.getMd5());
					} else
						ret = new FileProperty(fileCategory.getId(), fileName,
								"", 0, "");
				} finally {
					con.close();
				}
			}
			if (fileCategory.getCachedType().equals(FileCachedType.LOCAL)
					&& ((item.getLastModified() == null || item
							.getLastModified().getTime() != lastModified) || item
							.getSize() != size)) { // 本地缓存模式
				item.setLastModified(new Date(lastModified));
				item.setSize(size);
				map.put(fileid, item);
				logger.debug("更新缓存[" + fileCategory.getIdString() + ","
						+ fileName + "](fileid=" + fileid + ")");
			}
			return ret;
		} catch (NoSuchAlgorithmException e) {
			throw new FileException(e);
		}
	}

	public void deleteFile(long loginUserId, String fileName, boolean isTmpFile)
			throws IOException, NamingException, InterruptedException {
		try {
			String fileid = DataSecurity.md5(fileCategory.getIdString() + "/"
					+ fileName);
			LoopNamingContext context = new LoopNamingContext("db");
			CachedFileHost oldfh = null;
			BaseFileItem oldItem;
			try {
				FileListBeanRemote bean = context.lookup(
						Lookuper.JNDI_TYPE_EJB, "kxd-ejb-fileListBean",
						FileListBeanRemote.class);
				oldItem = bean.delete(loginUserId, fileid);
				if (oldItem != null)
					oldfh = CacheHelper.fileHostMap
							.get(oldItem.getFileHostId());
			} finally {
				context.close();
			}
			if (oldItem != null && oldfh != null) {
				deleteFile(oldfh, oldItem.getRealFileName()
						+ (isTmpFile ? ".tmp" : ""));
			}
			cachedFileMap.remove(fileid);
		} catch (NoSuchAlgorithmException e) {
			throw new FileException(e);
		}
	}

	protected void deleteFile(CachedFileHost fileHost, String fileName)
			throws IOException, InterruptedException {
		if (fileHost.isLocalStored()) {
			File file = new File(fileHost.getFileRootDir() + "/"
					+ fileCategory.getIdString() + "/" + fileName);
			if (file.exists())
				file.delete();
		} else {
			PooledFtpConnection con = getFtpConnection(fileHost);
			try {
				con.delete(encodeFtpFilePath(fileHost,
						fileCategory.getIdString() + "/" + fileName));
			} finally {
				con.close();
			}
		}
	}

	/**
	 * 断点续传
	 * 
	 * @param fileHost
	 * @param filePath
	 * @param stream
	 * @throws IOException
	 * @throws InterruptedException
	 */
	protected void appendFile(CachedFileHost fileHost, String filePath,
			byte[] data) throws IOException, InterruptedException {
		if (fileHost.isLocalStored()) {
			filePath = fileHost.getFileRootDir() + "/" + filePath;
			File file = new File(filePath);
			file.getParentFile().mkdirs();
			FileOutputStream is = new FileOutputStream(file, true);
			try {
				is.write(data);
			} finally {
				is.close();
			}
		} else {
			PooledFtpConnection con = getFtpConnection(fileHost);
			try {
				con.append(encodeFtpFilePath(fileHost, filePath), data);
			} finally {
				con.close();
			}
		}
	}

	public CachedFileCategory getFileCategory() {
		return fileCategory;
	}

	/**
	 * 上传文件
	 * 
	 * @param fileHost
	 * @param filePath
	 * @param data
	 * @throws IOException
	 * @throws InterruptedException
	 */
	protected void saveFile(CachedFileHost fileHost, String filePath,
			byte[] data) throws IOException, InterruptedException {
		if (fileHost.isLocalStored()) {
			filePath = fileHost.getFileRootDir() + "/" + filePath;
			File file = new File(filePath);
			file.getParentFile().mkdirs();
			FileOutputStream is = new FileOutputStream(file);
			try {
				is.write(data);
			} finally {
				is.close();
			}
		} else {
			PooledFtpConnection con = getFtpConnection(fileHost);
			try {
				con.upload(encodeFtpFilePath(fileHost, filePath),
						new ByteArrayInputStream(data));
			} finally {
				con.close();
			}
		}
	}

	static long lastExpiredCheckTime = 0;

	static synchronized void clearExpiredData() throws MemCachedException {
		if (DateTime.minutesBetween(System.currentTimeMillis(),
				lastExpiredCheckTime) >= 10) {
			lastExpiredCheckTime = System.currentTimeMillis();
			Enumeration<String> en = cachedFileMap.keys();
			long time = System.currentTimeMillis();
			while (en.hasMoreElements()) {
				String fileId = en.nextElement();
				CachedFile d = cachedFileMap.get(fileId);
				if (d.expireTime > 0 && d.expireTime < time) {
					cachedFileMap.remove(fileId);
					d.data = null;
				}
			}
		}
	}
}
