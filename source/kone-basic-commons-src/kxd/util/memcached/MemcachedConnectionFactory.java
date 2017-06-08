package kxd.util.memcached;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import kxd.net.connection.CommonConnectionPool;
import kxd.net.connection.ConnectionPool;
import kxd.net.connection.ConnectionPoolListMap;
import kxd.net.connection.tcp.TcpConnectionCreator;
import kxd.util.ExceptionCreator;
import kxd.util.KoneUtil;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * MemCached连接工厂
 * 
 * @author 赵明
 * @version 1.0
 */

public class MemcachedConnectionFactory {
	static ExceptionCreator<IOException> exceptionCreator = new ExceptionCreator<IOException>() {
		@Override
		public IOException createException(String msg, Throwable t) {
			if (t == null)
				return new IOException(msg);
			else
				return new IOException(msg, t);
		}
	};
	static ConnectionPoolListMap<MemcachedConnectionPoolList> poolsMap = new ConnectionPoolListMap<MemcachedConnectionPoolList>();
	static {
		try {
			loadConfig(KoneUtil.getConfigPath() + "memcached-config.xml");
		} catch (Throwable e) {
		}
	}

	public static void loadConfig(String configFile) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(configFile);
			NodeList list = doc.getElementsByTagName("servergroup");
			for (int i = 0; i < list.getLength(); i++) {
				Element node = (Element) list.item(i);
				String name = node.getAttribute("name");
				NodeList ls = node.getElementsByTagName("server");
				NodeList ls1 = node.getElementsByTagName("param");
				if (name != null && !name.trim().isEmpty()
						&& ls.getLength() > 0) {
					MemcachedConnectionPoolList cps = new MemcachedConnectionPoolList(
							exceptionCreator);
					poolsMap.put(name, cps);
					int dataTimeout = 0;
					// int waitIdleTimeout = 10 * 1000;
					int soTimeout = 10 * 1000;
					int connectTimeout = 10 * 1000;
					int maxConnections = 30;
					boolean useConsistentHash = false;
					cps.executorClass = node.getAttribute("executorclass");
					String localCacheClass = node
							.getAttribute("localcacheclass");
					if (localCacheClass != null
							&& localCacheClass.trim().length() > 0) {
						cps.localCache = Class.forName(localCacheClass)
								.newInstance();
					}
					if (node.hasAttribute("useconsistenthash"))
						useConsistentHash = Boolean.valueOf(node
								.getAttribute("useconsistenthash"));
					cps.setUseConsistentHash(useConsistentHash);
					if (node.hasAttribute("timeout")) {
						dataTimeout = Integer.valueOf(node
								.getAttribute("timeout")) * 1000;
					}
					if (node.hasAttribute("sotimeout"))
						soTimeout = Integer.valueOf(node
								.getAttribute("sotimeout")) * 1000;
					if (node.hasAttribute("connecttimeout"))
						connectTimeout = Integer.valueOf(node
								.getAttribute("connecttimeout")) * 1000;
					// if (node.hasAttribute("waitidletimeout"))
					// waitIdleTimeout = Integer.valueOf(node
					// .getAttribute("waitidletimeout")) * 1000;
					if (node.hasAttribute("maxconnections"))
						maxConnections = Integer.valueOf(node
								.getAttribute("maxconnections"));
					for (int j = 0; j < ls1.getLength(); j++) {
						node = (Element) ls1.item(j);
						cps.configMap.put(node.getAttribute("name"),
								node.getAttribute("value"));
					}
					ArrayList<ConnectionPool<MemcachedConnection, IOException>> pools = new ArrayList<ConnectionPool<MemcachedConnection, IOException>>();
					for (int j = 0; j < ls.getLength(); j++) {
						Element el = (Element) ls.item(j);
						String[] h = el.getTextContent().split(":");
						String host = h[0];
						int weights = 1;
						if (el.hasAttribute("weights"))
							weights = Integer.valueOf(el
									.getAttribute("weights"));
						int port = Integer.valueOf(h[1]);
						TcpConnectionCreator<MemcachedConnection> creator = new TcpConnectionCreator<MemcachedConnection>(
								MemcachedConnection.class, name, host, port,
								connectTimeout);
						creator.setSoTimeout(soTimeout);
						creator.setNoDelay(true);
						creator.setDataTimeout(dataTimeout);
						ConnectionPool<MemcachedConnection, IOException> pool = new CommonConnectionPool<MemcachedConnection, IOException>(
								creator, maxConnections);
						pool.setWeights(weights);
						pools.add(pool);
					}
					cps.addAll(pools);
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获得连接
	 * 
	 * @param name
	 *            连接配置名称
	 * @param hashCode
	 *            hashCode，根据此值决定访问哪台服务器，实现分布式访问
	 * @return 找到的主机连接
	 * @throws InterruptedException
	 * @throws IOException
	 */
	static public MemcachedConnection getConnection(String name, int hashCode)
			throws InterruptedException, IOException {
		MemcachedConnectionPoolList pools = poolsMap.get(name);
		if (pools == null)
			throw new MemCachedException("找不到MemCached的主机配置[" + name + "]");
		else
			return pools.getConnection(hashCode);
	}

	/**
	 * 根据指定的哈希列表，获取连接。
	 * <p>
	 * 以哈希列表中第1个哈希值获取连接，同时将列表中所有与第1个哈希值指向的服务器相同的哈希值索引过滤出来，加入至sameIndex集合中返回
	 * </p>
	 * 
	 * @param name
	 *            连接配置名称
	 * @param hashCodes
	 *            哈希列表
	 * @param sameIndex
	 *            哈希值中相同的服务器索引列表
	 * @return 找到的连接
	 * @throws IOException
	 * @throws InterruptedException
	 */

	static public Map<ConnectionPool<MemcachedConnection, IOException>, Collection<Integer>> getConnectionPools(
			String name, List<String> keys, int[] hashCodes) throws IOException {
		MemcachedConnectionPoolList pools = poolsMap.get(name);
		if (pools == null)
			throw new MemCachedException("找不到MemCached的主机配置[" + name + "]");
		else
			return pools.getConnectionPools(keys, hashCodes);
	}

	/**
	 * 删除指定配置的服务器上所有缓存数据
	 * 
	 * @param name
	 *            配置名称
	 * @throws InterruptedException
	 * @throws IOException
	 */
	static public void flushAll(String name) throws InterruptedException,
			IOException {
		MemcachedConnectionPoolList pools = poolsMap.get(name);
		if (pools != null)
			pools.flushAll();
	}

	/**
	 * 删除所有的服务器上全部缓存数据
	 * 
	 * @throws InterruptedException
	 * @throws IOException
	 */
	static public void flushAll() throws InterruptedException {
		Enumeration<MemcachedConnectionPoolList> en = poolsMap.elements();
		while (en.hasMoreElements()) {
			try {
				en.nextElement().flushAll();
			} catch (IOException e) {
			}
		}
	}

	static public MemCachedClient getClientInstance(String name)
			throws MemCachedException {
		MemcachedConnectionPoolList pools = poolsMap.get(name);
		if (pools == null)
			return null;
		try {
			MemCachedExecutor executor = (MemCachedExecutor) Class.forName(
					pools.executorClass).newInstance();
			executor.initialize(pools);
			return new MemCachedClient(executor);
		} catch (Exception e) {
			throw new MemCachedException(e);
		}
	}

	/**
	 * 获取配置的全部实例
	 * 
	 * @return
	 * @throws MemCachedException
	 */
	static public ConcurrentHashMap<String, MemCachedClient> getClientInstanceMap() {
		ConcurrentHashMap<String, MemCachedClient> map = new ConcurrentHashMap<String, MemCachedClient>();
		Enumeration<String> keys = poolsMap.keys();
		while (keys.hasMoreElements()) {
			String name = keys.nextElement();
			MemcachedConnectionPoolList pools = poolsMap.get(name);
			if (pools != null) {
				try {
					MemCachedExecutor executor = (MemCachedExecutor) Class
							.forName(pools.executorClass).newInstance();
					executor.initialize(pools);
					map.put(name, new MemCachedClient(executor));
				} catch (Throwable e) {
				}
			}
		}
		return map;
	}

}
