package kxd.net.connection.ftp;

import java.io.IOException;

import kxd.net.connection.AbstractConnectionCreator;

public class FtpConnectionCreator<E extends PooledFtpConnection> extends
		AbstractConnectionCreator<E, IOException> {
	String host, user, pwd;
	int port;

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
	public FtpConnectionCreator(Class<E> clazz, String groupName,
			String address, int port, String user, String password) {
		super(clazz, groupName);
		this.host = address;
		this.port = port;
		this.user = user;
		this.pwd = password;
	}

	@Override
	public String getConnectionUrl() {
		return host + ":" + port;
	}

	public int getPort() {
		return port;
	}

	@Override
	public IOException createException(String msg, Throwable t) {
		if (t == null)
			return new IOException(msg);
		else
			return new IOException(msg, t);
	}

	@Override
	public int getConnectionTimeout() {
		return 0;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public void setPort(int port) {
		this.port = port;
	}

}
