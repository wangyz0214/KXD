package kxd.engine.fileservice;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import kxd.util.DateTime;

import org.apache.log4j.Logger;

public class CachedFile {
	long lastModified;
	long expireTime;
	byte[] data;
	File file, backupFile, curFile;
	final static int EXPIRE_TIMES = 10 * 60 * 1000;
	final static int KEEP_FREEMEMORY = 256 * 1024 * 1024;
	long lastLoadTime;
	static Logger logger = Logger.getLogger(CachedFile.class);

	public CachedFile(File file, File backupFile) {
		this.file = file;
		this.backupFile = backupFile;
		this.curFile = file;
	}

	public synchronized long getLastModified() {
		try {
			return curFile.lastModified();
		} catch (Throwable e) {
			if (curFile == file)
				return backupFile.lastModified();
			else
				return file.lastModified();
		}
	}

	public synchronized byte[] getData(File file) throws IOException {
		logger.debug("download starting...");
		if (DateTime.secondsBetween(lastLoadTime, System.currentTimeMillis()) > 60
				&& lastModified != file.lastModified()
				|| data == null
				|| data.length != file.length()) {
			logger.debug("download from hard disk[free memory="
					+ Runtime.getRuntime().freeMemory() + "].");
			if (!file.exists())
				throw new IOException("装入文件失败：文件不存在[" + file.getAbsolutePath()
						+ "]");
			int size = (int) file.length();
			if (size <= 0)
				return null;
			if (size > FileProcessor.MAX_FILESIZE)
				throw new FileException("装入文件失败：文件[" + file.getAbsolutePath()
						+ "]太大");
			FileInputStream is = new FileInputStream(file);
			try {
				byte[] data = new byte[size];
				int count = 0;
				while (count < data.length) {
					count += is.read(data, count, data.length - count);
				}
				lastLoadTime = System.currentTimeMillis();
				if (Runtime.getRuntime().freeMemory() >= KEEP_FREEMEMORY) { // 有空闲内存
					this.data = data;
					lastModified = file.lastModified();
				} else {
					logger.debug("剩余内存[" + Runtime.getRuntime().freeMemory()
							+ "]太小，不保存至缓存中");
					this.data = null;
					if (expireTime != 0) {
						expireTime = System.currentTimeMillis() + EXPIRE_TIMES;
					}
					return data;
				}
			} finally {
				is.close();
			}
		}
		if (expireTime != 0) {
			expireTime = System.currentTimeMillis() + EXPIRE_TIMES;
		}
		return data;
	}

	public synchronized byte[] getData() throws IOException {
		try {
			return getData(curFile);
		} catch (FileException e) {
			throw e;
		} catch (Throwable e) {
			if (curFile == file)
				curFile = backupFile;
			else
				curFile = file;
			return getData(curFile);
		}
	}

	public synchronized long getExpireTime() {
		return expireTime;
	}

	public synchronized void setExpireTime(long expireTime) {
		this.expireTime = expireTime;
	}

}
