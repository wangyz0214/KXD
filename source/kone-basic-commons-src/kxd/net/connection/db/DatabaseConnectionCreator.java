package kxd.net.connection.db;

import java.sql.SQLException;

import kxd.net.connection.AbstractConnectionCreator;

public class DatabaseConnectionCreator<E extends PooledDatabaseConnection>
		extends AbstractConnectionCreator<E, SQLException> {
	private volatile String className;
	private volatile String url;
	private volatile String user;
	private volatile String passwd;

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
	public DatabaseConnectionCreator(Class<E> clazz, String groupName,
			String className, String url, String user, String passwd) {
		super(clazz, groupName);
		this.className = className;
		this.user = user;
		this.url = url;
		this.passwd = passwd;
	}

	@Override
	public String getConnectionUrl() {
		return url;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPasswd() {
		return passwd;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}

	@Override
	public SQLException createException(String msg, Throwable t) {
		if (t == null)
			return new SQLException(msg);
		else
			return new SQLException(msg, t);
	}

	@Override
	public int getConnectionTimeout() {
		return 0xffffffff;
	}

}
