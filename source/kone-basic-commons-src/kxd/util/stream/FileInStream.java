package kxd.util.stream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class FileInStream extends AbstractStream {
	private InputStream inputStream = null;

	public FileInStream(File file) throws FileNotFoundException {
		this.inputStream = new FileInputStream(file);
	}

	public FileInStream(String file) throws FileNotFoundException {
		this.inputStream = new FileInputStream(file);
	}

	public int getSize() throws IOException {
		if (inputStream != null)
			return inputStream.available();
		else
			return 0;
	}

	public void close() {
		if (this.inputStream != null) {
			try {
				inputStream.close();
			} catch (Throwable e) {

			}
			inputStream = null;
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
		if (inputStream == null)
			throw new IOException("尚未指定具体的输入流对象，不允许读数据!");
		int ret = inputStream.read(data, offset, maxcount);
		if (ret <= 0)
			throw new IOException("流通道已经关闭或断开");
		return ret;
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
		throw new IOException("不支持读接口");
	}
}
