package kxd.net.connection.jndi;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

import kxd.net.connection.Connection;

public class JndiConnection implements Connection<NamingException> {
	String url;
	Map<Object, Object> properties;
	Lookuper lookuper;
	InitialContext context;
	final static Logger logger = Logger.getLogger(JndiConnection.class);
	/**
	 * 缓存查找到的bean
	 */
	ConcurrentHashMap<String, Object> lookupedBeanMap = new ConcurrentHashMap<String, Object>();

	JndiConnection() {
		super();
	}

	public JndiConnection(String url, Map<Object, Object> properties,
			Lookuper lookuper) {
		super();
		this.url = url;
		this.properties = properties;
		this.lookuper = lookuper;
	}

	/**
	 * 查找一个jndi对象
	 * 
	 * @param jndiType
	 *            jndi资源类型
	 * @param name
	 *            会话Bean的Jndi名称
	 * @return 对象
	 * @throws NamingException
	 */
	public <E> E lookup(int jndiType, String name, Class<E> clazz)
			throws NamingException {
		String key = name + "_" + jndiType;
		@SuppressWarnings("unchecked")
		E r = (E) lookupedBeanMap.get(key);
		if (r != null) {
			logger.info("lookup(" + key + "): return from cache ==> " + r);
			return r;
		}
		if (!isConnected())
			open();
		try {
			r = lookuper.lookup(context, jndiType, name, clazz);
			if (r != null) {
				lookupedBeanMap.put(name + "_" + jndiType, r);
			}
			logger.info("lookup(" + key + "): return from lookuper.lookup()==>"
					+ r);
			return r;
		} catch (NamingException e) {
			closeContext();
			throw e;
		}
	}

	@Override
	public void open() throws NamingException {
		if (url == null) { // 创建本地Jndi调用
			context = new InitialContext();
		} else {
			Properties props = new Properties();
			props.putAll(properties);
			props.setProperty("java.naming.provider.url", url);
			context = new InitialContext(props);
		}
	}

	@Override
	public void close() throws NamingException {
		closeContext();
	}

	@Override
	public boolean isConnected() {
		return context != null;
	}

	/***
	 * 关闭Jndi上下文
	 */
	public void closeContext() {
		lookupedBeanMap.clear();
		if (context != null) {
			try {
				context.close();
			} catch (Throwable e) {
			}
			context = null;
		}
	}

}
