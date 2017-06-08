package kxd.util.stream;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.IOException;

public class FileOutStream extends AbstractStream {
	private OutputStream outputStream = null;

	public FileOutStream(File file) throws FileNotFoundException {
		this.outputStream = new FileOutputStream(file);
	}

	public FileOutStream(String file) throws FileNotFoundException {
		this.outputStream = new FileOutputStream(file);
	}

	@Override
	public int getSize() throws IOException {
		throw new IOException("不支持这个接口");
	}

	public void close() {
		if (this.outputStream != null) {
			try {
				outputStream.close();
			} catch (Throwable e) {

			}
			outputStream = null;
		}
	}

	/**
	 * 读一次数据
	 * 
	 * @param offset
	 *            偏移量
	 * @param maxcount
	 *            读数据的长度
	 * @param data
	 *            数据缓冲区
	 * @param timeout
	 *            超时时间。以秒为单位
	 * @return int 真正读出的数据长度
	 * @throws SerialError
	 */
	public int readOne(int offset, int maxcount, byte[] data, int timeout)
			throws IOException {
		throw new IOException("不支持读接口");
	}

	/**
	 * 写一次数据
	 * 
	 * @param offset
	 *            偏移量
	 * @param length
	 *            写数据的长度
	 * @param data
	 *            数据缓冲区
	 * @param timeout
	 *            超时时间。以秒为单位
	 * @return int 本次真正写入的数据长度
	 * @throws SerialError
	 */
	public int writeOne(int offset, int length, byte[] data, int timeout)
			throws IOException {
		if (outputStream == null)
			throw new IOException("尚未指定具体的输出流对象，不允许写数据!");
		outputStream.write(data, offset, length);
		outputStream.flush();
		return length;
	}
}
