package kxd.engine.fileservice;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import jxl.common.Logger;
import kxd.engine.cache.beans.sts.CachedFileHost;
import kxd.engine.cache.beans.sts.CachedFileItem;
import kxd.engine.cache.beans.sts.CachedFileOwner;
import kxd.engine.helper.CacheHelper;
import kxd.remote.scs.util.emun.FileCachedType;
import kxd.remote.scs.util.emun.FileVisitRight;
import kxd.util.DataSecurity;
import kxd.util.DateTime;
import kxd.util.KeyValue;
import kxd.util.KoneUtil;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

class InsideDownloadMapItem {
	public String url, bakUrl;
	private boolean isMain;

	public synchronized String getUrl() {
		isMain = !isMain;
		if (isMain)
			return url;
		else
			return bakUrl;
	}
}

public class FileDownloadProcessor extends FileProcessor {
	static Logger logger = Logger.getLogger(FileDownloadProcessor.class);
	static ConcurrentHashMap<Short, InsideDownloadMapItem> insideMap = new ConcurrentHashMap<Short, InsideDownloadMapItem>();
	static {
		loadParamsConfig();
	}

	static private void loadParamsConfig() {
		try { // 装入参数配置
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			String path = KoneUtil.getConfigPath();
			Document doc = builder.parse(path + "kone-web.xml");
			NodeList list = doc.getElementsByTagName("insidefilehosts");
			for (int i = 0; i < list.getLength(); i++) {
				Element node = (Element) list.item(i);
				NodeList ls = node.getElementsByTagName("filehost");
				for (int j = 0; j < ls.getLength(); j++) {
					node = (Element) ls.item(j);
					InsideDownloadMapItem item = new InsideDownloadMapItem();
					item.url = node.getAttribute("url");
					item.bakUrl = node.getAttribute("bakurl");
					short hostid = Short.valueOf(node.getAttribute("hostid"));
					insideMap.put(hostid, item);
				}
			}

		} catch (Throwable e) {
			logger.error("init error:", e);
		}
	}

	boolean checkUser;

	public FileDownloadProcessor(String userId, String userPwd,
			short fileCategoryId) {
		super(userId, userPwd, fileCategoryId);
		checkUser = true;
	}

	public FileDownloadProcessor(short fileCategoryId) {
		super(null, null, fileCategoryId);
		checkUser = false;
	}

	public KeyValue<Date, byte[]> downloadFile(long lastModified,
			String fileName, int startPos, int endPos) throws IOException {
		if (startPos < 0 || endPos < 0)
			return new KeyValue<Date, byte[]>(new Date(), new byte[0]);
		KeyValue<Date, byte[]> ret = new KeyValue<Date, byte[]>();
		try {
			String fileid = DataSecurity.md5(fileCategory.getIdString() + "/"
					+ fileName);
			CachedFileItem item = CacheHelper.getFileItemMap(fileCategory).get(
					fileid);
			if (item == null)
				throw new FileException("下载文件失败：找不到文件["
						+ fileCategory.getIdString() + "," + fileName
						+ "][fileid=" + fileid + "]");
			CachedFileHost fh = CacheHelper.fileHostMap.get(item
					.getFileHostId());
			if (fh == null)
				throw new FileException("更新文件失败：找不到文件主机["
						+ item.getFileHostId() + "]");

			CachedFileOwner owner = CacheHelper.fileOwnerMap.get(item
					.getFileOwnerId());
			if (checkUser && owner != null
					&& !owner.getVisitRight().equals(FileVisitRight.ALL)) {
				if (userOwner == null)
					throw new FileException("下载文件失败：非法下载用户(fileid=" + fileid
							+ ")");
				switch (owner.getVisitRight()) {
				case OWNERUSER:
					if (userOwner.getId() != item.getFileOwnerId())
						throw new FileException("下载文件失败：无权下载(fileid=" + fileid
								+ ")");
					break;
				default:
					break;
				}
			}
			String file = item.getRealFileName();
			if (fh.isLocalStored()) {
				clearExpiredData();
				CachedFile data = cachedFileMap.get(fileid);
				if (data == null) {
					data = new CachedFile(new File(fh.getFileRootDir() + "/"
							+ fileCategory.getId() + "/" + file), new File(
							fh.getFileRootDir() + "_bak/"
									+ fileCategory.getId() + "/" + file));
					if (fileCategory.getCachedType().equals(
							FileCachedType.DEFAULT)) {
						data.setExpireTime(System.currentTimeMillis()
								+ CachedFile.EXPIRE_TIMES);
						cachedFileMap.put(fileid, data);
					} else
						data.setExpireTime(0L);
				}
				ret.setKey(new Date(data.getLastModified()));
				if (DateTime.secondsBetween(data.getLastModified(),
						lastModified) <= 3)
					return ret;
				byte[] buf = data.getData();
				if (endPos == 0 || endPos > buf.length)
					endPos = buf.length;
				if (startPos >= endPos)
					ret.setValue(new byte[0]);
				else if (startPos == 0 && endPos == buf.length)
					ret.setValue(buf);
				else {
					byte[] b = new byte[endPos - startPos];
					System.arraycopy(buf, startPos, b, 0, b.length);
					ret.setValue(b);
				}
			} else {
				InsideDownloadMapItem m = insideMap.get(fh.getId());
				if (m == null)
					downloadByHttp(ret, fh.getRealHttpUrlRoot().trim(), file,
							startPos, endPos);
				else {
					try {
						downloadByHttp(ret, m.getUrl(), file, startPos, endPos);
					} catch (Throwable e) {
						downloadByHttp(ret, m.getUrl(), file, startPos, endPos);
					}
				}
			}
			return ret;
		} catch (NoSuchAlgorithmException e) {
			throw new FileException(e);
		}
	}

	void downloadByHttp(KeyValue<Date, byte[]> r, String url, String file,
			int startPos, int endPos) throws MalformedURLException, IOException {
		if (!url.endsWith("/"))
			url += "/";
		url += fileCategory.getIdString() + "/" + file;
		HttpURLConnection con = (HttpURLConnection) new URL(url)
				.openConnection();
		try {
			con.setDoInput(true);
			if (startPos > 0) {
				if (endPos == 0)
					con.addRequestProperty("Range", "bytes=" + startPos + "-");
				else
					con.addRequestProperty("Range", "bytes=" + startPos + "-"
							+ (endPos - 1));
			}
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			byte[] b = new byte[1024];
			int len;
			while ((len = con.getInputStream().read(b)) > 0) {
				os.write(b, 0, len);
			}
			r.setKey(new Date(con.getLastModified()));
			r.setValue(os.toByteArray());
		} finally {
			con.disconnect();
		}
	}
}
