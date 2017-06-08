package kxd.util.stream;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class MemoryStream extends AbstractStream {
	private byte[] buffer = null;
	/**
	 * 空间不够时，重新分配内存的增量
	 */
	int incBytes = 512;
	private int pos = 0;
	private int size = 0;

	public MemoryStream() {
	}

	public MemoryStream(int incBytes) {
		this.incBytes = incBytes;
	}

	public MemoryStream(byte[] buffer, int incBytes) {
		this.buffer = buffer;
		this.incBytes = incBytes;
		if (buffer != null)
			size = buffer.length;
	}

	public MemoryStream(byte[] buffer) {
		this.buffer = buffer;
		if (buffer != null)
			size = buffer.length;
	}

	public void close() {
		this.buffer = null;
		pos = 0;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		realloc(size - getSize());
		this.size = size;
	}

	private void realloc(int bytes) {
		byte[] b = new byte[getSize() + bytes];
		System.arraycopy(buffer, 0, b, 0, getSize());
		buffer = null;
		buffer = b;
	}

	public void load(String fileName) throws IOException {
		close();
		Stream stream = new Stream(new FileInputStream(fileName), null);
		try {
			buffer = stream.read(stream.getSize(), 100);
			size = buffer.length;
		} catch (IOException e) {
			close();
			throw e;
		} finally {
			if (stream != null)
				stream.close();
			stream = null;
		}
	}

	public void save(String fileName) throws IOException {
		Stream stream = new Stream(null, new FileOutputStream(fileName));
		try {
			stream.write(buffer, 100);
		} finally {
			if (stream != null)
				stream.close();
			stream = null;
		}
	}

	@Override
	public int readOne(int offset, int maxcount, byte[] data, int timeout)
			throws IOException {
		int r = maxcount;
		if ((pos + r) > getSize())
			r = getSize() - pos;
		if (r > 0) {
			System.arraycopy(buffer, pos, data, offset, maxcount);
			pos += r;
		}
		return r;
	}

	@Override
	public int writeOne(int offset, int length, byte[] data, int timeout)
			throws IOException {
		if ((length + pos) > getSize()) {
			int bytes = length + pos - getSize();
			if ((bytes % incBytes) != 0)
				bytes = ((bytes / incBytes) + 1) * incBytes;
			realloc(bytes);
			size = length + pos;
		}
		System.arraycopy(data, offset, buffer, pos, length);
		pos += length;
		return length;
	}

	public int getPostion() {
		return pos;
	}

	public byte[] getBuffer() {
		return buffer;
	}

	public void setPostion(int pos) {
		if (pos > getSize())
			this.pos = getSize();
		else
			this.pos = pos;
	}

	public ByteArrayInputStream getInputStream(int offset, int length)
			throws IOException {
		if (buffer != null) {
			if ((offset + length) > getSize())
				return null;
			return new ByteArrayInputStream(buffer, offset, length);
		} else
			return null;
	}

	public ByteArrayInputStream readStream(int length) throws IOException {
		if (buffer != null) {
			if ((pos + length) > getSize())
				return null;
			int offset = pos;
			pos += length;
			return new ByteArrayInputStream(buffer, offset, length);
		} else
			return null;
	}
}
