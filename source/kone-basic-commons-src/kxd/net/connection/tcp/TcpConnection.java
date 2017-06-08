package kxd.net.connection.tcp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import kxd.net.connection.Connection;

/**
 * Tcp连接
 * 
 * @author zhaom
 * 
 */
public class TcpConnection implements Connection<IOException> {
	InetSocketAddress address;
	int connectTimeout;
	protected SocketStream stream = null;

	protected TcpConnection() {
		super();
	}

	/**
	 * Tcp连接构建器
	 * 
	 * @param address
	 *            套接字地址和端口
	 * @param connectTimeout
	 *            连接超时
	 */
	public TcpConnection(InetSocketAddress address, int connectTimeout) {
		this.address = address;
		this.connectTimeout = connectTimeout;
	}

	@Override
	public void open() throws IOException {
		Socket socket = new Socket();
		socket.connect(address, connectTimeout);
		stream = new SocketStream(socket);
	}

	@Override
	public void close() throws IOException {
		closeSocket();
	}

	@Override
	public boolean isConnected() {
		try {
			return stream != null && stream.isConnected();
		} catch (IOException e) {
			return false;
		}
	}

	/***
	 * 关闭套接字连接
	 */
	public void closeSocket() {
		if (stream != null) {
			try {
				stream.close();
			} catch (Throwable e) {
			}
			stream = null;
		}
	}

	public InetSocketAddress getAddress() {
		return address;
	}

	public int getConnectTimeout() {
		return connectTimeout;
	}

	public SocketStream getStream() {
		return stream;
	}
}
