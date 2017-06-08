package kxd.util.stream;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;

import kxd.util.DataUnit;

public class DayLogStream extends AbstractStream {
	private String logRootPath;
	private String logModule, ext;
	private String day;
	private boolean append;
	FileLock lock;
	RandomAccessFile file;

	public DayLogStream(String logRootPath, String logModule, String ext,
			boolean append) {
		this.logModule = logModule;
		this.logRootPath = logRootPath;
		this.ext = ext;
		this.append = append;
	}

	public void checkDayChange() throws IOException {
		String toDay = DataUnit.formatCurrentDateTime("yyyyMMdd");
		if (day != null) {
			if (!day.equals(toDay)) {
				close();
				day = toDay;
			}
		} else
			day = toDay;
		if (file == null) {
			try {
				String fileName = logRootPath + "/" + logModule + day + "."
						+ ext;
				File f = new File(fileName).getParentFile();
				if (!f.exists())
					f.mkdirs();
				file = new RandomAccessFile(fileName, "rw");
				if (append)
					file.seek(file.length());
				lock = file.getChannel().tryLock();
			} catch (Throwable e) {
				throw new IOException("打开文件失败：" + e.toString());
			}
		}
	}

	public boolean isOpened() {
		return file != null;
	}

	@Override
	public void close() throws IOException {
		try {
			if (lock != null) {
				lock.release();
				lock = null;
			}
			if (file != null) {
				file.close();
				file = null;
			}
		} catch (Throwable e) {
			throw new IOException("关闭文件失败：" + e.toString());
		}
	}

	@Override
	public int getSize() throws IOException {
		try {
			if (isOpened())
				return (int) file.length();
			else
				return 0;
		} catch (Throwable e) {
			throw new IOException("获取文件长度失败：" + e.toString());
		}
	}

	public void seek(long position) throws IOException {
		if (!isOpened())
			throw new IOException("定位文件失败：文件未打开或打开失败");
		try {
			file.seek(position);
		} catch (Throwable e) {
			throw new IOException("定位文件失败：" + e.toString());
		}
	}

	@Override
	public int readOne(int offset, int maxcount, byte[] data, int timeout)
			throws IOException {
		if (!isOpened())
			throw new IOException("读文件失败：文件未打开或打开失败");
		try {
			return file.read(data, offset, maxcount);
		} catch (Throwable e) {
			throw new IOException("读文件失败：" + e.toString());
		}
	}

	@Override
	public int writeOne(int offset, int length, byte[] data, int timeout)
			throws IOException {
		if (!isOpened())
			throw new IOException("写文件失败：文件未打开或打开失败");
		try {
			file.write(data, offset, length);
			return length;
		} catch (Throwable e) {
			throw new IOException("写文件失败：" + e.toString());
		}
	}
}
