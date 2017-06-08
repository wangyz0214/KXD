package kxd.net.naming;

import javax.naming.NamingException;

import kxd.net.connection.jndi.JndiConnection;

public class LocalNamingContext extends NamingContext {

	/**
	 * 创建一个本地上下文
	 * 
	 * @throws NamingException
	 */
	public LocalNamingContext(String jndiGroup) throws NamingException {
		super(jndiGroup);
	}

	/**
	 * 采用名为default的jndi配置组，创建一个基于范围分布式访问的上下文
	 * 
	 * @param name
	 *            上下文名称
	 * @param key
	 *            关键字
	 * @throws NamingException
	 */

	public LocalNamingContext() throws NamingException {
		this("default");
	}

	@Override
	protected JndiConnection getConnection() throws NamingException,
			InterruptedException {
		return poolGroup.localPools.getConnection();
	}
}
