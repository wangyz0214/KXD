package kxd.util.stream;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.io.IOException;

public class RandomFileStream extends AbstractStream {
	File fileName;
	boolean append;
	FileLock lock;
	RandomAccessFile file;

	public RandomFileStream(String fileName, boolean append) {
		this.fileName = new File(fileName);
		this.append = append;
	}

	public boolean isOpened() {
		return file != null;
	}

	public boolean exists() {
		return fileName.exists();
	}

	public void open() throws IOException {
		close();
		File f = fileName.getParentFile();
		if (!f.exists())
			f.mkdirs();
		file = new RandomAccessFile(fileName, "rw");
		if (append)
			file.seek(file.length());
		lock = file.getChannel().tryLock();
	}

	@Override
	public void close() throws IOException {
		if (file != null) {
			file.close();
			file = null;
		}
		if (lock != null) {
			lock.release();
			lock = null;
		}
	}

	@Override
	public int getSize() throws IOException {
		if (isOpened())
			return (int) file.length();
		else
			return 0;
	}

	public void seek(long position) throws IOException {
		if (isOpened())
			file.seek(position);
	}

	@Override
	public int readOne(int offset, int maxcount, byte[] data, int timeout)
			throws IOException {
		if (!isOpened())
			open();
		return file.read(data, offset, maxcount);
	}

	@Override
	public int writeOne(int offset, int length, byte[] data, int timeout)
			throws IOException {
		if (!isOpened())
			open();
		file.write(data, offset, length);
		return length;
	}
}
