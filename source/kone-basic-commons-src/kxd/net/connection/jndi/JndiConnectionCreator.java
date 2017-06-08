package kxd.net.connection.jndi;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.naming.NamingException;

import kxd.net.connection.AbstractConnectionCreator;

public class JndiConnectionCreator<C extends PooledJndiConnection> extends
		AbstractConnectionCreator<C, NamingException> {
	String url;
	ConcurrentHashMap<Object, Object> properties;
	private volatile int connectionTimeout;
	private Lookuper lookuper;
	private String checkAliveBeanName;

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
	public JndiConnectionCreator(Class<C> clazz, String groupName,
			Lookuper lookuper, String url, int connectionTimeout,
			ConcurrentHashMap<Object, Object> properties,
			String checkAliveBeanName) {
		super(clazz, groupName);
		this.url = url;
		this.connectionTimeout = connectionTimeout;
		this.lookuper = lookuper;
		this.properties = properties;
		this.checkAliveBeanName = checkAliveBeanName;
	}

	@Override
	public String getConnectionUrl() {
		return url;
	}

	public Map<Object, Object> getProperties() {
		return properties;
	}

	@Override
	public int getConnectionTimeout() {
		return connectionTimeout;
	}

	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	public Lookuper getLookuper() {
		return lookuper;
	}

	@Override
	public NamingException createException(String msg, Throwable t) {
		return new NamingException(msg);
	}

	public String toString() {
		if (getConnectionUrl() == null)
			return "local[" + getGroupName() + "]";
		else
			return super.toString();
	}

	public String getCheckAliveBeanName() {
		return checkAliveBeanName;
	}

	public void setCheckAliveBeanName(String checkAliveBeanName) {
		this.checkAliveBeanName = checkAliveBeanName;
	}
}
