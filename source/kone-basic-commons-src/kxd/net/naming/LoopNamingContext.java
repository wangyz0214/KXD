package kxd.net.naming;

import javax.naming.NamingException;

import kxd.net.connection.LoopConnectionPoolList;
import kxd.net.connection.jndi.PooledJndiConnection;

public class LoopNamingContext extends NamingContext {
	String name;

	/**
	 * 创建一个基于Loop轮循访问的上下文
	 * 
	 * @param groupName
	 *            jndi配置组
	 * @param name
	 *            上下文名称
	 * 
	 * @throws NamingException
	 */
	public LoopNamingContext(String groupName, String name)
			throws NamingException {
		super(groupName);
		this.name = name;
	}

	/**
	 * 采用名为default的jndi配置组，创建一个基于Loop轮循访问的上下文
	 * 
	 * @param name
	 *            上下文名称
	 * 
	 * @throws NamingException
	 */
	public LoopNamingContext(String name) throws NamingException {
		this("default", name);
	}

	@Override
	protected PooledJndiConnection getConnection() throws NamingException,
			InterruptedException {
		LoopConnectionPoolList<PooledJndiConnection, NamingException> ls = poolGroup.loopMap
				.get(name);
		if (ls == null)
			throw new NamingException("上下文名称[" + name + "]未配置");
		return ls.getConnection();
	}

}
