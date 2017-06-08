package kxd.util.stream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Stream extends AbstractStream {
	private InputStream inputStream = null;
	private OutputStream outputStream = null;

	public Stream(InputStream in, OutputStream out) {
		this.inputStream = in;
		this.outputStream = out;
	}

	public int getSize() throws IOException {
		try {
			if (inputStream != null)
				return inputStream.available();
			else
				return 0;
		} catch (Throwable e) {
			throw new IOException("错误的数据格式: " + e.getMessage());
		}
	}

	public void close() {
		if (this.inputStream != null) {
			try {
				inputStream.close();
			} catch (Throwable e) {
			}
			inputStream = null;
		}
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
		if (inputStream == null)
			throw new IOException("尚未指定具体的输入流对象，不允许读数据!");
		int ret = inputStream.read(data, offset, maxcount);
		if (ret <= 0){
			throw new IOException("流通道已经关闭或断开");
		}
		return ret;
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
	public int readOneManuf(int offset, int maxcount, byte[] data, int timeout)
			throws IOException {
		if (inputStream == null)
			throw new IOException("尚未指定具体的输入流对象，不允许读数据!");
		System.out.println();
		int ret = inputStream.read(data, offset, maxcount);
		if (ret <= 0){
			ret = 16;
			//throw new IOException("流通道已经关闭或断开");
		}
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
		if (outputStream == null)
			throw new IOException("尚未指定具体的输出流对象，不允许写数据!");
		outputStream.write(data, offset, length);
		outputStream.flush();
		return length;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public OutputStream getOutputStream() {
		return outputStream;
	}

	public void setOutputStream(OutputStream outputStream) {
		this.outputStream = outputStream;
	}
}
