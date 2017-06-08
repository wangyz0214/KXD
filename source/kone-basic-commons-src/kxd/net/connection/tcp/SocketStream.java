package kxd.net.connection.tcp;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;

import kxd.net.adapters.NetAdapterException;
import kxd.net.adapters.NetAdapterResult;
import kxd.util.stream.AbstractStream;

public class SocketStream extends AbstractStream {
	protected Socket socket;

	public SocketStream(Socket socket) {
		this.socket = socket;
	}

	public int getSize() throws IOException {
		throw new IOException("不支持的接口gegSize()");
	}

	public boolean isConnected() throws IOException {
		return (socket != null && socket.isConnected());
	}

	public void close() {
		try {
			if (socket != null)
				socket.close();
		} catch (Throwable e) {
		}
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
	 * @throws IOException
	 */
	public int readOne(int offset, int maxcount, byte[] data, int timeout)
			throws IOException {
		if (!isConnected())
			throw new NetAdapterException(NetAdapterResult.NOTARRIVALED,
					"未连接至主机");
		try {
			if (socket.getSoTimeout() != timeout)
				socket.setSoTimeout(timeout);
			int ret = socket.getInputStream().read(data, offset, maxcount);
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
	 * @throws IOException
	 */
	public int writeOne(int offset, int length, byte[] data, int timeout)
			throws IOException {
		if (!isConnected())
			throw new NetAdapterException(NetAdapterResult.NOTARRIVALED,
					"未连接至主机");
		socket.getOutputStream().write(data, offset, length);
		socket.getOutputStream().flush();
		return length;
	}

	public Socket getSocket() {
		return socket;
	}
}
