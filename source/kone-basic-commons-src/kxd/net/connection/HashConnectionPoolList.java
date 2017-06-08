package kxd.net.connection;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CopyOnWriteArrayList;

import kxd.util.ExceptionCreator;

/**
 * 连接池列表，用于分布式连接池调用，具备取余和统一哈希定位服务器
 * 
 * @param <C>
 */
public class HashConnectionPoolList<C extends PooledConnection<E>, E extends Throwable>
		extends ConnectionPoolList<C, E> {
	private static final long serialVersionUID = 1L;
	CopyOnWriteArrayList<Integer> noConsistentList;
	TreeMap<Long, Integer> consistentMap;
	/**
	 * 是否采用统一哈希算法实现负载均衡
	 */
	volatile boolean useConsistentHash;
	/**
	 * 当出现坏连接时，是否自动调用定位规则，如果是，则当出现坏连接时，下次将不再使用，直到连接可用时才重新使用，提高连接的成功率，但会更改主机的访问规则
	 * ，如果不同主机的访问不会改变业务处理，可以设为true
	 */
	volatile boolean adjustForBadConnection;

	public HashConnectionPoolList(ExceptionCreator<E> exceptionCreator) {
		super(exceptionCreator);
	}

	public HashConnectionPoolList(ExceptionCreator<E> exceptionCreator,
			Collection<? extends ConnectionPool<C, E>> c) {
		super(exceptionCreator, c);
	}

	public HashConnectionPoolList(ExceptionCreator<E> exceptionCreator,
			ConnectionPool<C, E>[] toCopyIn) {
		super(exceptionCreator, toCopyIn);
	}

	@Override
	public <Cx extends PooledConnection<Ex>, Ex extends Throwable> void connectFailure(
			Cx connection, Ex throwable) {
		if (adjustForBadConnection) // 如果设定为不处理，则不处理连接失败的情况
			super.connectFailure(connection, throwable);
	}

	@Override
	protected void checkBadConnectionPools() {
		if (adjustForBadConnection)// 如果设定为不处理，则不检查失败的连接
			super.checkBadConnectionPools();
	}

	private static ThreadLocal<MessageDigest> MD5 = new ThreadLocal<MessageDigest>() {
		@Override
		protected MessageDigest initialValue() {
			try {
				return MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException e) {
				throw new IllegalStateException(e);
			}
		}
	};

	/**
	 * 当连接池发生变化（添加，删除）时，调用此函数，重新组织分布式访问
	 */
	protected void changed() {
		if (noConsistentList != null) {
			noConsistentList.clear();
			noConsistentList = null;
		}
		if (consistentMap != null) {
			consistentMap.clear();
			consistentMap = null;
		}
		if (useConsistentHash) { // 使用一致性哈希
			consistentMap = new TreeMap<Long, Integer>();
			int totalWeight = 0;
			for (ConnectionPool<C, E> p : this) {
				totalWeight += p.getWeights();
			}
			int ps = size();
			MessageDigest md5 = MD5.get();
			for (int i = 0; i < ps; i++) {
				ConnectionPool<C, E> p = get(i);
				int thisWeight = p.getWeights();

				double factor = Math.floor(((double) (40 * ps * thisWeight))
						/ (double) totalWeight);

				for (long j = 0; j < factor; j++) {
					String host = p.getConnectionCreator().getConnectionUrl()
							+ "-" + j;
					byte[] d = md5.digest(host.getBytes());
					for (int h = 0; h < 4; h++) {
						Long k = ((long) (d[3 + h * 4] & 0xFF) << 24)
								| ((long) (d[2 + h * 4] & 0xFF) << 16)
								| ((long) (d[1 + h * 4] & 0xFF) << 8)
								| ((long) (d[0 + h * 4] & 0xFF));
						consistentMap.put(k, i);
					}
				}
			}
		} else {
			noConsistentList = new CopyOnWriteArrayList<Integer>();
			for (int i = 0; i < size(); i++) {
				ConnectionPool<C, E> p = get(i);
				for (int j = 0; j < p.getWeights(); j++)
					noConsistentList.add(i);
			}
		}

	}

	private static long md5HashingAlg(String key) {
		MessageDigest md5 = MD5.get();
		md5.reset();
		md5.update(key.getBytes());
		byte[] bKey = md5.digest();
		long res = ((long) (bKey[3] & 0xFF) << 24)
				| ((long) (bKey[2] & 0xFF) << 16)
				| ((long) (bKey[1] & 0xFF) << 8) | (long) (bKey[0] & 0xFF);
		return res;
	}

	private long getHash(String key, Integer hashCode) {
		if (hashCode != null) {
			if (useConsistentHash)
				return hashCode.longValue() & 0xffffffffL;
			else
				return hashCode.longValue();
		} else {
			if (useConsistentHash) {
				return md5HashingAlg(key);
			} else
				return key.hashCode();
		}
	}

	/**
	 * 根据哈希值，获取一个连接
	 * 
	 * @param hashCode
	 *            哈希值，不能为null
	 * @return 获取的连接
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public C getConnection(Integer hashCode) throws E, InterruptedException {
		long hc;
		if (hashCode != null) {
			if (useConsistentHash)
				hc = hashCode.longValue() & 0xffffffffL;
			else
				hc = hashCode.longValue();
		} else {
			throw new NullPointerException("hashCode is null");
		}
		if (useConsistentHash) {
			Long k = consistentMap.ceilingKey(hc);
			if (k == null)
				k = consistentMap.firstKey();
			return get(consistentMap.get(k)).getConnection();
		} else {
			int index = (int) hc % noConsistentList.size();
			if (index < 0)
				index *= -1;
			return get(noConsistentList.get(index)).getConnection();
		}
	}

	private int getConnectionPoolIndex(String key, Integer hashCode) throws E {
		long hc = getHash(key, hashCode);
		if (useConsistentHash) {
			Long k = consistentMap.ceilingKey(hc);
			if (k == null)
				k = consistentMap.firstKey();

			return consistentMap.get(k);
		} else {
			return (int) hc % noConsistentList.size();
		}
	}

	private ConnectionPool<C, E> getConnectionPool(String key, Integer hashCode)
			throws E {
		int index = getConnectionPoolIndex(key, hashCode);
		if (index < 0)
			index *= -1;
		return get(index);
	}

	/**
	 * 根据指定的key和hashCode，获得连接，如果hashCode=null，则根据key计算一个哈希值代替
	 * 
	 * @param key
	 *            关键字，如果hashCode=null，则根据关键字按内部哈希算法，算出哈希值，决定访问哪个连接池，实现分布式访问
	 * @param hashCode
	 *            哈希值，如果此值不是null,则根据此值决定访问哪个连接池，实现分布式访问
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public C getConnection(String key, Integer hashCode) throws E,
			InterruptedException {
		int size = size();
		if (size == 0)
			throw exceptionCreator.createException("没有可用的连接", null);
		else if (size == 1) {
			return get(0).getConnection();
		} else {
			return getConnectionPool(key, hashCode).getConnection();
		}
	}

	/**
	 * 根据指定的关键字列表和哈希列表，获取连接池Map。
	 * 
	 * @param keys
	 *            关键字列表
	 * @param hashCodes
	 *            哈希值列表
	 * @return 连接池MAP
	 * @throws IOException
	 */
	public Map<ConnectionPool<C, E>, Collection<Integer>> getConnectionPools(
			List<String> keys, int[] hashCodes) throws E {
		int size = size();
		if (size == 0)
			throw exceptionCreator.createException("没有可用的连接池", null);
		else {
			HashMap<ConnectionPool<C, E>, Collection<Integer>> map = new HashMap<ConnectionPool<C, E>, Collection<Integer>>();
			if (size == 1) {
				map.put(get(0), null);
			} else {
				if (hashCodes == null)
					hashCodes = new int[0];
				if (keys.size() == 1) {
					Integer hashCode = null;
					if (hashCodes.length > 0)
						hashCode = hashCodes[0];
					map.put(getConnectionPool(keys.get(0), hashCode), null);
				} else {
					for (int i = 0; i < keys.size(); i++) {
						Integer hashCode = null;
						if (hashCodes.length > i)
							hashCode = hashCodes[i];
						ConnectionPool<C, E> pool = getConnectionPool(
								keys.get(i), hashCode);
						Collection<Integer> c = map.get(pool);
						if (c != null)
							c.add(i);
						else {
							c = new ArrayList<Integer>();
							c.add(i);
							map.put(pool, c);
						}
					}
				}
			}
			return map;
		}
	}

	public Map<ConnectionPool<C, E>, Collection<Integer>> getConnectionPools(
			Object[] keys, int[] hashCodes) throws E {
		int size = size();
		if (size == 0)
			throw exceptionCreator.createException("没有可用的连接池", null);
		else {
			HashMap<ConnectionPool<C, E>, Collection<Integer>> map = new HashMap<ConnectionPool<C, E>, Collection<Integer>>();
			if (size == 1) {
				map.put(get(0), null);
			} else {
				if (hashCodes == null)
					hashCodes = new int[0];
				if (keys.length == 1) {
					Integer hashCode = null;
					if (hashCodes.length > 0)
						hashCode = hashCodes[0];
					map.put(getConnectionPool(keys[0].toString(), hashCode),
							null);
				} else {
					for (int i = 0; i < keys.length; i++) {
						Integer hashCode = null;
						if (hashCodes.length > i)
							hashCode = hashCodes[i];
						ConnectionPool<C, E> pool = getConnectionPool(
								keys[i].toString(), hashCode);
						Collection<Integer> c = map.get(pool);
						if (c != null)
							c.add(i);
						else {
							c = new ArrayList<Integer>();
							c.add(i);
							map.put(pool, c);
						}
					}
				}
			}
			return map;
		}
	}

	public Map<ConnectionPool<C, E>, Collection<String>> getConnectionPoolsMapKey(
			List<String> keys, int[] hashCodes) throws E {
		int size = size();
		if (size == 0)
			throw exceptionCreator.createException("没有可用的连接池", null);
		else {
			HashMap<ConnectionPool<C, E>, Collection<String>> map = new HashMap<ConnectionPool<C, E>, Collection<String>>();
			if (size == 1) {
				map.put(get(0), null);
			} else {
				if (hashCodes == null)
					hashCodes = new int[0];
				if (keys.size() == 1) {
					Integer hashCode = null;
					if (hashCodes.length > 0)
						hashCode = hashCodes[0];
					map.put(getConnectionPool(keys.get(0).toString(), hashCode),
							null);
				} else {
					for (int i = 0; i < keys.size(); i++) {
						Integer hashCode = null;
						if (hashCodes.length > i)
							hashCode = hashCodes[i];
						ConnectionPool<C, E> pool = getConnectionPool(
								keys.get(i), hashCode);
						Collection<String> c = map.get(pool);
						if (c != null)
							c.add(keys.get(i));
						else {
							c = new ArrayList<String>();
							c.add(keys.get(i));
							map.put(pool, c);
						}
					}
				}
			}
			return map;
		}
	}

	/**
	 * 当出现坏连接时，是否自动调用定位规则，如果是，则当出现坏连接时，下次将不再使用，直到连接可用时才重新使用，提高连接的成功率，但会更改主机的访问规则
	 * ，如果不同主机的访问不会改变业务处理，可以设为true
	 */
	public boolean isAdjustForBadConnection() {
		return adjustForBadConnection;
	}

	/**
	 * 当出现坏连接时，是否自动调用定位规则，如果是，则当出现坏连接时，下次将不再使用，直到连接可用时才重新使用，提高连接的成功率，但会更改主机的访问规则
	 * ，如果不同主机的访问不会改变业务处理，可以设为true
	 * 
	 * @param adjustForBadConnection
	 *            是否自动处理坏连接
	 */
	public void setAdjustForBadConnection(boolean adjustForBadConnection) {
		this.adjustForBadConnection = adjustForBadConnection;
	}

	/**
	 * 是否采用统一哈希算法，自动定位服务器
	 * 
	 */
	public boolean isUseConsistentHash() {
		return useConsistentHash;
	}

	/**
	 * 设置是否采用统一哈希算法，自动定位服务器
	 * 
	 * @param useConsistentHash
	 */
	public void setUseConsistentHash(boolean useConsistentHash) {
		if (useConsistentHash == this.useConsistentHash)
			return;
		this.useConsistentHash = useConsistentHash;
		changed();
	}
}
