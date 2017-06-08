package kxd.net.naming;

import java.util.concurrent.ConcurrentHashMap;

import javax.naming.NamingException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import kxd.net.connection.LoopConnectionPoolList;
import kxd.net.connection.jndi.JndiConnection;
import kxd.net.connection.jndi.JndiConnectionCreator;
import kxd.net.connection.jndi.JndiConnectionPool;
import kxd.net.connection.jndi.Lookuper;
import kxd.net.connection.jndi.PooledJndiConnection;
import kxd.util.ExceptionCreator;
import kxd.util.KoneUtil;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

abstract public class NamingContext {
	final static Logger logger = Logger.getLogger(NamingContext.class);
	static ConcurrentHashMap<String, JndiConnecitonPoolListGroup> jndiConnectionPoolListMap = new ConcurrentHashMap<String, JndiConnecitonPoolListGroup>();

	static ExceptionCreator<NamingException> exceptionCreator = new ExceptionCreator<NamingException>() {
		@Override
		public NamingException createException(String msg, Throwable t) {
			return new NamingException(msg);
		}
	};
	static {
		try {
			loadConfig(KoneUtil.getConfigPath() + "jndi-config.xml");
		} catch (Throwable e) {
			logger.error("Failure load jndi-config.xml:", e);
		}
	}

	/**
	 * 装入配置文件
	 * 
	 * @param file
	 */
	public static void loadConfig(String file) throws Throwable {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(file);
		NodeList groups = doc.getElementsByTagName("jndi-group");
		for (int j = 0; j < groups.getLength(); j++) {
			Element group = (Element) groups.item(j);
			String groupName = group.getAttribute("name");
			JndiConnecitonPoolListGroup poolGroup = new JndiConnecitonPoolListGroup();
			jndiConnectionPoolListMap.put(groupName, poolGroup);
			NodeList ls = group.getElementsByTagName("lookuper");
			Element lookupElement = (Element) ls.item(0);
			poolGroup.lookuper = (Lookuper) Class.forName(
					lookupElement.getAttribute("class")).newInstance();
			poolGroup.localPools = new JndiConnectionPool<PooledJndiConnection, NamingException>(
					new JndiConnectionCreator<PooledJndiConnection>(
							PooledJndiConnection.class, groupName,
							poolGroup.lookuper, null, 30000,
							poolGroup.properties, null));
			NodeList list = group.getElementsByTagName("property");
			for (int i = 0; i < list.getLength(); i++) {
				Element node = (Element) list.item(i);
				String p = node.getAttribute("name");
				String v = node.getAttribute("value");
				poolGroup.properties.put(p, v);
			}
			list = group.getElementsByTagName("loopurls");
			for (int i = 0; i < list.getLength(); i++) { // 装入命名主机列表
				Element node = (Element) list.item(i);
				String oname = node.getAttribute("name");
				String checkAliveBeanName = node
						.getAttribute("alive-test-bean-name");
				ls = node.getElementsByTagName("url");
				LoopConnectionPoolList<PooledJndiConnection, NamingException> urls = new LoopConnectionPoolList<PooledJndiConnection, NamingException>(
						exceptionCreator);
				urls.name = oname;
				for (int k = 0; k < ls.getLength(); k++) {
					node = (Element) ls.item(k);
					JndiConnectionPool<PooledJndiConnection, NamingException> pool = new JndiConnectionPool<PooledJndiConnection, NamingException>(
							new JndiConnectionCreator<PooledJndiConnection>(
									PooledJndiConnection.class, oname,
									poolGroup.lookuper, node.getTextContent(),
									30000, poolGroup.properties,
									checkAliveBeanName));
					if (!urls.contains(pool))
						urls.add(pool);
				}
				if (urls.size() > 0) {
					logger.debug("jndi group add(" + oname + "): " + urls);
					poolGroup.loopMap.put(oname, urls);
				}
			}
			// list = group.getElementsByTagName("hashedurls");
			// for (int i = 0; i < list.getLength(); i++) { // 装入命名主机列表
			// Element node = (Element) list.item(i);
			// String oname = node.getAttribute("name");
			// ls = node.getElementsByTagName("url");
			// HashConnectionPoolList<PooledJndiConnection, NamingException>
			// urls = new HashConnectionPoolList<PooledJndiConnection,
			// NamingException>(
			// exceptionCreator);
			// for (int k = 0; k < ls.getLength(); k++) {
			// node = (Element) ls.item(k);
			// urls.add(new JndiConnectionPool<PooledJndiConnection,
			// NamingException>(
			// new JndiConnectionCreator<PooledJndiConnection>(
			// PooledJndiConnection.class, oname,
			// poolGroup.lookuper, node.getTextContent(),
			// 30000, poolGroup.properties)));
			// }
			// if (urls.size() > 0) {
			// poolGroup.hashMap.put(oname, urls);
			// }
			// }
		}
	}

	JndiConnecitonPoolListGroup poolGroup;

	// JndiConnection connection;

	/**
	 * 
	 * @param jndiGroup
	 *            jndi配置组名称
	 */
	NamingContext(String jndiGroup) throws NamingException {
		poolGroup = jndiConnectionPoolListMap.get(jndiGroup);
		if (poolGroup == null) {
			throw new NamingException("jndi gorup [" + jndiGroup
					+ "] is not configured.");
		}
	}

	abstract protected JndiConnection getConnection() throws NamingException,
			InterruptedException;

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
		try {
			JndiConnection connection = getConnection();
			if (connection == null)
				throw new NamingException("lookup failed, context is null.");
			else {
				try {
					return connection.lookup(jndiType, name, clazz);
				} finally {
					connection.close();
				}
			}
		} catch (InterruptedException e) {
			throw new NamingException(e.toString());
		}
	}

	/**
	 * 关闭命名上下文
	 * 
	 * @throws NamingException
	 */
	public void close() throws NamingException {
		// if (connection != null) {
		// connection.close();
		// }
	}
}
