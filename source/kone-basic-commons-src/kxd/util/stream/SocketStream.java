package kxd.util.stream;

import java.net.Socket;
import java.net.SocketTimeoutException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SocketStream extends AbstractStream {

	private InputStream inputStream = null;

	private OutputStream outputStream = null;

	private Socket socket;

	public SocketStream() {

	}

	public SocketStream(Socket socket) throws IOException {

		inputStream = socket.getInputStream();
		outputStream = socket.getOutputStream();
		this.socket = socket;
	}

	public SocketStream(String host, int ip) throws IOException {

		this(new Socket(host, ip));
	}

	synchronized public void connect(String host, int ip) throws IOException {

		setSocket(new Socket(host, ip));
	}

	public int getSize() throws IOException {

		throw new IOException("不支持的接口gegSize()");
	}

	synchronized public void close() {

		try {
			if (socket != null)
				socket.close();
		} catch (Throwable e) {
		}
		inputStream = null;
		outputStream = null;
		socket = null;
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
	synchronized public int readOne(int offset, int maxcount, byte[] data,
			int timeout) throws IOException {

		if (inputStream == null)
			throw new IOException("尚未指定具体的输入流对象，不允许读数据!");
		try {
			if (socket.getSoTimeout() != timeout)
				socket.setSoTimeout(timeout);
			int ret = inputStream.read(data, offset, maxcount);
			if (ret <= 0)
				throw new IOException("连接已经断开");
			return ret;
		} catch (SocketTimeoutException e) {
			return 0;
		}
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
	synchronized public int writeOne(int offset, int length, byte[] data,
			int timeout) throws IOException {

		if (outputStream == null)
			throw new IOException("尚未指定具体的输出流对象，不允许写数据!");
		outputStream.write(data, offset, length);
		outputStream.flush();
		return length;
	}

	synchronized public Socket getSocket() {

		return socket;
	}

	public synchronized void setSocket(Socket socket) throws IOException {

		if (socket != null) {
			inputStream = socket.getInputStream();
			outputStream = socket.getOutputStream();

		} else {
			inputStream = null;
			outputStream = null;
		}

		this.socket = socket;
	}

	synchronized public boolean isConnected() {

		return socket != null && socket.isConnected();
	}
}
