package kxd.net.connection.tcp;

import java.io.IOException;

import kxd.net.connection.AbstractConnectionCreator;

public class TcpConnectionCreator<E extends PooledTcpConnection> extends
		AbstractConnectionCreator<E, IOException> {
	private volatile String address;
	private volatile int port;
	private volatile int connectionTimeout;
	private volatile int soTimeout;
	private volatile boolean noDelay;
	private volatile int dataTimeout;

	/**
	 * 
	 * @param clazz
	 *            要生成的类
	 * 
	 * @param groupName
	 *            连接分组名称
	 * @param address
	 *            主机地址
	 * @param port
	 *            主机端口
	 * @param connectionTimeout
	 *            连接超时
	 */
	public TcpConnectionCreator(Class<E> clazz, String groupName,
			String address, int port, int connectionTimeout) {
		super(clazz, groupName);
		this.address = address;
		this.port = port;
		this.connectionTimeout = connectionTimeout;
	}

	@Override
	public String getConnectionUrl() {
		return address + ":" + port;
	}

	public String getAddress() {
		return address;
	}

	public int getPort() {
		return port;
	}

	public int getConnectionTimeout() {
		return connectionTimeout;
	}

	public int getSoTimeout() {
		return soTimeout;
	}

	public void setSoTimeout(int soTimeout) {
		this.soTimeout = soTimeout;
	}

	public boolean isNoDelay() {
		return noDelay;
	}

	public void setNoDelay(boolean noDelay) {
		this.noDelay = noDelay;
	}

	public int getDataTimeout() {
		return dataTimeout;
	}

	public void setDataTimeout(int dataTimeout) {
		this.dataTimeout = dataTimeout;
	}

	@Override
	public IOException createException(String msg, Throwable t) {
		if (t == null)
			return new IOException(msg);
		else
			return new IOException(msg, t);
	}

}
