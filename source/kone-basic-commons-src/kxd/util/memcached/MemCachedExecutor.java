package kxd.util.memcached;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import kxd.net.connection.CommonConnectionPool;
import kxd.net.connection.ConnectionPool;
import kxd.util.DataSecurity;
import kxd.util.DataUnit;
import kxd.util.DateTime;
import kxd.util.KeyValue;
import kxd.util.Value;
import kxd.util.stream.Stream;

import org.apache.log4j.Logger;

/**
 * 缓存执行器
 * 
 * @author 赵明
 * 
 */
public class MemCachedExecutor implements MemCachedValueConverter {
	public static final int MARKER_BYTE = 1;
	public static final int MARKER_BOOLEAN = 8192;
	public static final int MARKER_INTEGER = 4;
	public static final int MARKER_LONG = 16384;
	public static final int MARKER_CHARACTER = 16;
	public static final int MARKER_STRING = 32;
	public static final int MARKER_STRINGBUFFER = 64;
	public static final int MARKER_FLOAT = 128;
	public static final int MARKER_SHORT = 256;
	public static final int MARKER_DOUBLE = 512;
	public static final int MARKER_DATE = 1024;
	public static final int MARKER_STRINGBUILDER = 2048;
	public static final int MARKER_BYTEARR = 4096;
	public static final int MARKER_SERIALIZED = 8;
	public static final int MARKER_COMPRESSED = 2;
	MemcachedConnectionPoolList poolGroup;
	private static final Logger logger = Logger
			.getLogger(MemCachedExecutor.class);

	public MemCachedExecutor() {
	}

	public void initialize(MemcachedConnectionPoolList poolGroup) {
		this.poolGroup = poolGroup;
	}

	public MemCachedValue objectToBytes(Object value, boolean compress)
			throws IOException {
		int flags = 0;
		byte[] buf;
		if (value instanceof Byte) {
			flags |= MARKER_BYTE;
			buf = new byte[] { ((Byte) value).byteValue() };
		} else if (value instanceof Boolean) {
			flags |= MARKER_BOOLEAN;
			buf = new byte[] { (byte) (((Boolean) value).booleanValue() ? 1 : 0) };
		} else if (value instanceof Integer) {
			flags |= MARKER_INTEGER;
			buf = DataUnit.intToBytes(((Integer) value).intValue());
		} else if (value instanceof Long) {
			flags |= MARKER_LONG;
			buf = DataUnit.longToBytes(((Long) value).longValue());
		} else if (value instanceof Character) {
			flags |= MARKER_CHARACTER;
			buf = DataUnit
					.shortToBytes((short) ((Character) value).charValue());
		} else if (value instanceof String) {
			flags |= MARKER_STRING;
			buf = ((String) value).getBytes("UTF-8");
		} else if (value instanceof StringBuffer) {
			flags |= MARKER_STRINGBUFFER;
			buf = ((StringBuffer) value).toString().getBytes("UTF-8");
		} else if (value instanceof Float) {
			flags |= MARKER_FLOAT;
			buf = DataUnit.floatToBytes(((Float) value).floatValue());
		} else if (value instanceof Short) {
			flags |= MARKER_SHORT;
			buf = DataUnit.shortToBytes(((Short) value).shortValue());
		} else if (value instanceof Double) {
			flags |= MARKER_DOUBLE;
			buf = DataUnit.doubleToBytes(((Double) value).doubleValue());
		} else if (value instanceof Date) {
			flags |= MARKER_DATE;
			buf = DataUnit.longToBytes(((Date) value).getTime());
		} else if (value instanceof StringBuilder) {
			flags |= MARKER_STRINGBUILDER;
			buf = ((StringBuilder) value).toString().getBytes("UTF-8");
		} else if (value instanceof byte[]) {
			flags |= MARKER_BYTEARR;
			buf = (byte[]) value;
		} else if (value instanceof Serializable) {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			(new ObjectOutputStream(bos)).writeObject(value);
			flags |= MARKER_SERIALIZED;
			buf = bos.toByteArray();
		} else
			throw new MemCachedException("必须是序列化对象");
		MemCachedValue mv = new MemCachedValue(buf, flags);
		if (compress)
			compress(mv);
		return mv;
	}

	/**
	 * 压缩数据
	 * 
	 * @param buf
	 *            要压缩数据
	 * @return null 数据不需要压缩；非null-压缩后的数据
	 * @throws IOException
	 */
	public void compress(MemCachedValue value) throws IOException {
		if (value.getValue().length > 512) { // 如果大于512字节，则压缩
			value.setValue(DataSecurity.zipCompressIncludeUncompressLen(
					value.getValue(), 0, value.getValue().length));
			value.setFlags(value.getFlags() | MARKER_COMPRESSED);
		}
	}

	/**
	 * 压缩数据
	 * 
	 * @param buf
	 *            要压缩数据
	 * @return null 数据不需要压缩；非null-压缩后的数据
	 * @throws IOException
	 */
	public void decompress(MemCachedValue value) throws IOException {
		if ((value.getFlags() & MARKER_COMPRESSED) == MARKER_COMPRESSED) {
			value.setValue(DataSecurity.zipDecompressIncludeUncompressLen(
					value.getValue(), 0, value.getValue().length));
		}
	}

	public Object bytesToObject(MemCachedValue value) throws IOException {
		if (value.getValue().length < 1)
			return null;
		else {
			int flags = value.getFlags();
			decompress(value);
			byte[] b = value.getValue();
			if ((flags & MARKER_BYTE) == MARKER_BYTE)
				return new Byte(b[0]);
			else if ((flags & MARKER_BOOLEAN) == MARKER_BOOLEAN)
				return b[0] == 1 ? Boolean.TRUE : Boolean.FALSE;
			else if ((flags & MARKER_INTEGER) == MARKER_INTEGER)
				return DataUnit.bytesToInt(b, 0);
			else if ((flags & MARKER_LONG) == MARKER_LONG)
				return DataUnit.bytesToLong(b, 0);
			else if ((flags & MARKER_CHARACTER) == MARKER_CHARACTER)
				return new Character((char) DataUnit.bytesToShort(b, 0));
			else if ((flags & MARKER_STRING) == MARKER_STRING)
				return new String(b, "UTF-8");
			else if ((flags & MARKER_STRINGBUFFER) == MARKER_STRINGBUFFER)
				return new StringBuffer(new String(b, "UTF-8"));
			else if ((flags & MARKER_FLOAT) == MARKER_FLOAT)
				return DataUnit.bytesToFloat(b, 0);
			else if ((flags & MARKER_SHORT) == MARKER_SHORT)
				return DataUnit.bytesToShort(b, 0);
			else if ((flags & MARKER_DOUBLE) == MARKER_DOUBLE)
				return DataUnit.bytesToDouble(b, 0);
			else if ((flags & MARKER_DATE) == MARKER_DATE)
				return new Date(DataUnit.bytesToLong(b, 0));
			else if ((flags & MARKER_STRINGBUILDER) == MARKER_STRINGBUILDER)
				return new StringBuilder(new String(b, "UTF-8"));
			else if ((flags & MARKER_BYTEARR) == MARKER_BYTEARR)
				return b;
			else {
				ObjectInputStream ois = new ObjectInputStream(
						new ByteArrayInputStream(b));
				try {
					return ois.readObject();
				} catch (ClassNotFoundException e) {
					throw new MemCachedException(e);
				}
			}
		}
	}

	/**
	 * 转换Key
	 * 
	 * @param key
	 * @return
	 */
	protected String convertKey(String key) {
		return key;
	}

	/**
	 * 设置键值对
	 * 
	 * @param cmdname
	 *            命令名(add,replace,set)
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @param hashCode
	 *            指定哈希值，完成分布式的服务器访问。如果为0，则按默认规则选择服务器
	 * @param expiry
	 *            服务器的过期时间点
	 * @throws IOException
	 * @throws InterruptedException
	 */
	protected void set(String cmdname, String key, Object value,
			boolean compress, Date expiry) throws IOException,
			InterruptedException {
		key = convertKey(key);
		MemcachedConnection con = poolGroup.getConnection(key, null);
		try {
			con.set(this, compress, cmdname, key, value, expiry);
		} finally {
			con.close();
		}
	}

	/**
	 * 设置多个值
	 * 
	 * @param cmdname
	 *            命令名(add,replace,set)
	 * @param keyValues
	 *            要设置键和值Map
	 * @param hashCode
	 *            指定哈希值，如果为0，则按默认规则选择服务器
	 * @param expiry
	 *            服务器的过期时间点
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void set(String cmdname, Map<String, Object> keyValues,
			boolean compress, Date expiry) throws IOException,
			InterruptedException {
		if (keyValues.size() == 0)
			return;
		HashMap<String, Object> map = new HashMap<String, Object>();
		Iterator<String> it = keyValues.keySet().iterator();
		List<String> keys = new ArrayList<String>();
		while (it.hasNext()) {
			String key = it.next();
			Object value = keyValues.get(key);
			String ckey = convertKey(key);
			keys.add(ckey);
			map.put(ckey, value);
		}
		Map<ConnectionPool<MemcachedConnection, IOException>, Collection<String>> cmap = poolGroup
				.getConnectionPoolsMapKey(keys, null);
		Iterator<ConnectionPool<MemcachedConnection, IOException>> cit = cmap
				.keySet().iterator();
		while (cit.hasNext()) {
			ConnectionPool<MemcachedConnection, IOException> pool = cit.next();
			MemcachedConnection con = pool.getConnection();
			try {
				Collection<String> c = cmap.get(pool);
				if (c != null) {
					con.set(this, compress, cmdname, map, expiry);
				} else {
					it = map.keySet().iterator();
					while (it.hasNext()) {
						String key = it.next();
						Object value = map.get(key);
						con.set(this, compress, cmdname, key, value, expiry);
					}
				}
			} finally {
				con.close();
			}
		}
	}

	public void set(String cmdname, List<KeyValue<String, Object>> keyValues,
			boolean compress, Date expiry) throws IOException,
			InterruptedException {
		if (keyValues.size() == 0)
			return;
		List<String> keys = new ArrayList<String>();
		for (KeyValue<String, Object> o : keyValues) {
			keys.add(convertKey(o.getKey()));
		}

		Map<ConnectionPool<MemcachedConnection, IOException>, Collection<Integer>> map = poolGroup
				.getConnectionPools(keys, null);
		Iterator<ConnectionPool<MemcachedConnection, IOException>> it = map
				.keySet().iterator();
		while (it.hasNext()) {
			ConnectionPool<MemcachedConnection, IOException> pool = it.next();
			MemcachedConnection con = pool.getConnection();
			try {
				Collection<Integer> c = map.get(pool);
				if (c == null) {
					for (int i = 0; i < keys.size(); i++) {
						con.set(this, compress, cmdname, keys.get(i), keyValues
								.get(i).getValue(), expiry);
					}
				} else {
					for (Integer i : c) {
						con.set(this, compress, cmdname, keys.get(i), keyValues
								.get(i).getValue(), expiry);
					}
				}
			} finally {
				con.close();
			}
		}
	}

	/**
	 * 将一个值设置到多个键中
	 * 
	 * @param cmdname
	 * @param keys
	 * @param value
	 * @param compress
	 * @param expiry
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void set(String cmdname, String[] keys, Object value,
			boolean compress, Date expiry) throws IOException,
			InterruptedException {
		if (keys.length == 0)
			return;
		if (expiry == null)
			expiry = new Date(0);
		MemCachedValue dv = this.objectToBytes(value, compress);
		for (int i = 0; i < keys.length; i++)
			keys[i] = convertKey(keys[i].toString());
		Map<ConnectionPool<MemcachedConnection, IOException>, Collection<Integer>> map = poolGroup
				.getConnectionPools(keys, null);
		Iterator<ConnectionPool<MemcachedConnection, IOException>> it = map
				.keySet().iterator();
		while (it.hasNext()) {
			ConnectionPool<MemcachedConnection, IOException> pool = it.next();
			MemcachedConnection con = pool.getConnection();
			try {
				Collection<Integer> c = map.get(pool);
				if (c == null) {
					for (String key : keys)
						con.set(this, compress, cmdname, key, dv, expiry);
				} else {
					for (Integer i : c)
						con.set(this, compress, cmdname, keys[i], dv, expiry);
				}
			} finally {
				con.close();
			}
		}
	}

	/**
	 * 设置多个值<br>
	 * 
	 * @param cmdname
	 *            命令名(add,replace,set)
	 * @param keyValues
	 *            要设置键和值Map
	 * @param expiry
	 *            服务器的过期时间点
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public <E extends Cacheable<?>> void setCacheableList(String cmdname,
			String keyPrefix, Collection<E> values, boolean compress,
			Date expiry) throws IOException, InterruptedException {
		if (values.size() == 0)
			return;
		List<String> keys = new ArrayList<String>();
		for (E o : values) {
			keys.add(convertKey(keyPrefix + "." + o.getIdString()));
		}
		List<E> ls;
		if (values instanceof List<?>)
			ls = (List<E>) values;
		else {
			ls = new ArrayList<E>();
			ls.addAll(values);
		}
		Map<ConnectionPool<MemcachedConnection, IOException>, Collection<Integer>> map = poolGroup
				.getConnectionPools(keys, null);
		Iterator<ConnectionPool<MemcachedConnection, IOException>> it = map
				.keySet().iterator();
		while (it.hasNext()) {
			ConnectionPool<MemcachedConnection, IOException> pool = it.next();
			MemcachedConnection con = pool.getConnection();
			try {
				Collection<Integer> c = map.get(pool);
				if (c == null) {
					for (int i = 0; i < ls.size(); i++) {
						Stream stream = new Stream(null,
								new ByteArrayOutputStream());
						ls.get(i).writeData(stream);
						con.set(this, compress, cmdname, keys.get(i),
								((ByteArrayOutputStream) stream
										.getOutputStream()).toByteArray(),
								expiry);
					}
				} else {
					for (Integer i : c) {
						Stream stream = new Stream(null,
								new ByteArrayOutputStream());
						ls.get(i).writeData(stream);
						con.set(this, compress, cmdname, keys.get(i),
								((ByteArrayOutputStream) stream
										.getOutputStream()).toByteArray(),
								expiry);
					}
				}
			} finally {
				con.close();
			}
		}
	}

	protected boolean setCacheableListAsync(
			final ConnectionPool<MemcachedConnection, IOException> pool,
			final int maxThreads, final int maxWaitSeconds,
			final String cmdname,
			final ArrayBlockingQueue<KeyValue<String, Object>> valueQueue,
			final boolean compress, final Date expiry) throws IOException,
			InterruptedException {
		final int maxCons = ((CommonConnectionPool<MemcachedConnection, IOException>) pool)
				.getMaxConnectionSize();
		int threads = valueQueue.size() / 50;
		if (threads > maxThreads)
			threads = maxThreads;
		if (threads > maxCons)
			threads = maxCons;
		final AtomicBoolean noError = new AtomicBoolean(true);
		Thread[] threadArray = new Thread[threads];
		// ReentrantLock lock = new ReentrantLock();
		// final Condition condition = lock.newCondition();
		final AtomicInteger runingThreads = new AtomicInteger(threads);
		final MemCachedValueConverter converter = this;
		for (int i = 0; i < threads; i++) {
			threadArray[i] = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						MemcachedConnection con = pool.getConnection();
						try {
							while (noError.get()
									&& !Thread.currentThread().isInterrupted()) {
								KeyValue<String, Object> value = valueQueue
										.poll();
								if (value == null) {
									runingThreads.decrementAndGet();
									// if (runingThreads.decrementAndGet() <= 0)
									// condition.signal();
									break;
								} else
									con.set(converter, compress, cmdname,
											value.getKey(), value.getValue(),
											expiry);
							}
						} finally {
							con.close();
						}
						runingThreads.decrementAndGet();
						// if (runingThreads.decrementAndGet() <= 0)
						// condition.signal();
					} catch (InterruptedException e1) {
						runingThreads.decrementAndGet();
						// if (runingThreads.decrementAndGet() <= 0)
						// condition.signal();
						logger.error("设置缓存失败，线程已经终止: ", e1);
						noError.set(false);
					} catch (IOException e1) {
						runingThreads.decrementAndGet();
						// if (runingThreads.decrementAndGet() <= 0)
						// condition.signal();
						logger.error("设置缓存失败: ", e1);
						noError.set(false);
						// condition.signal();
					}
				}
			});
			threadArray[i].start();
		}
		long t = System.currentTimeMillis();
		while (runingThreads.get() > 0) {
			if (DateTime.secondsBetween(t, System.currentTimeMillis()) >= maxWaitSeconds)
				return false;
			Thread.sleep(10);
		}
		// if (!condition.await(maxWaitSeconds, TimeUnit.SECONDS))
		// return false;
		return noError.get();
	}

	/**
	 * 异步设置缓存集，允许以多线程的方式设置缓存集，缩短设置时间
	 * 
	 * @param maxThreads
	 *            最大的线程数
	 * @param maxWaitSeconds
	 *            最大的等待时间
	 * @param cmdname
	 *            命令名[add,set,replace]
	 * @param keyPrefix
	 *            缓存键前缀
	 * @param values
	 *            值集合
	 * @param compress
	 *            是否对数据进行压缩
	 * @param expiry
	 *            过期时间，为null则永不超时
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public <E extends Cacheable<?>> void setCacheableListAsync(int maxThreads,
			int maxWaitSeconds, String keyPrefix, Collection<E> values,
			boolean compress, Date expiry) throws IOException,
			InterruptedException {
		if (values.size() < 100) {
			if (values.size() > 0)
				setCacheableList("set", keyPrefix, values, compress, expiry);
			return;
		}
		List<String> keys = new ArrayList<String>();
		for (E o : values) {
			keys.add(convertKey(keyPrefix + "." + o.getIdString()));
		}
		List<E> ls;
		if (values instanceof List<?>)
			ls = (List<E>) values;
		else {
			ls = new ArrayList<E>();
			ls.addAll(values);
		}
		Map<ConnectionPool<MemcachedConnection, IOException>, Collection<Integer>> map = poolGroup
				.getConnectionPools(keys, null);
		Iterator<ConnectionPool<MemcachedConnection, IOException>> it = map
				.keySet().iterator();
		while (it.hasNext()) {
			ConnectionPool<MemcachedConnection, IOException> pool = it.next();
			try {
				Collection<Integer> c = map.get(pool);
				ArrayBlockingQueue<KeyValue<String, Object>> list;
				if (c == null) {
					list = new ArrayBlockingQueue<KeyValue<String, Object>>(
							ls.size());
					for (int i = 0; i < ls.size(); i++) {
						Stream stream = new Stream(null,
								new ByteArrayOutputStream());
						ls.get(i).writeData(stream);
						list.add(new KeyValue<String, Object>(keys.get(i),
								((ByteArrayOutputStream) stream
										.getOutputStream()).toByteArray()));
					}
				} else {
					list = new ArrayBlockingQueue<KeyValue<String, Object>>(
							c.size());
					for (Integer i : c) {
						Stream stream = new Stream(null,
								new ByteArrayOutputStream());
						ls.get(i).writeData(stream);
						list.add(new KeyValue<String, Object>(keys.get(i),
								((ByteArrayOutputStream) stream
										.getOutputStream()).toByteArray()));
					}
				}
				setCacheableListAsync(pool, maxThreads, maxWaitSeconds, "set",
						list, compress, expiry);
			} finally {
			}
		}
	}

	/**
	 * 异步设置缓存集，允许以多线程的方式设置缓存集，缩短设置时间
	 * 
	 * @param maxThreads
	 *            最大的线程数
	 * @param maxWaitSeconds
	 *            最大的等待时间
	 * @param cmdname
	 *            命令名[add,set,replace]
	 * @param keyPrefix
	 *            缓存键前缀
	 * @param values
	 *            值集合
	 * @param compress
	 *            是否对数据进行压缩
	 * @param expiry
	 *            过期时间，为null则永不超时
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void setAsync(int maxThreads, int maxWaitSeconds,
			Collection<KeyValue<String, Object>> values, boolean compress,
			Date expiry) throws IOException, InterruptedException {
		if (values.size() == 0)
			return;
		List<String> keys = new ArrayList<String>();
		for (KeyValue<String, Object> o : values) {
			o.setKey(convertKey(o.getKey()));
			keys.add(o.getKey());
		}
		List<KeyValue<String, Object>> ls;
		if (values instanceof List<?>)
			ls = (List<KeyValue<String, Object>>) values;
		else {
			ls = new ArrayList<KeyValue<String, Object>>();
			ls.addAll(values);
		}
		if (values.size() < 100) {
			set("set", ls, compress, expiry);
			return;
		}
		Map<ConnectionPool<MemcachedConnection, IOException>, Collection<Integer>> map = poolGroup
				.getConnectionPools(keys, null);
		Iterator<ConnectionPool<MemcachedConnection, IOException>> it = map
				.keySet().iterator();
		while (it.hasNext()) {
			ConnectionPool<MemcachedConnection, IOException> pool = it.next();
			MemcachedConnection con = pool.getConnection();
			try {
				Collection<Integer> c = map.get(pool);
				ArrayBlockingQueue<KeyValue<String, Object>> list;
				if (c == null) {
					list = new ArrayBlockingQueue<KeyValue<String, Object>>(
							ls.size());
					list.addAll(ls);
				} else {
					list = new ArrayBlockingQueue<KeyValue<String, Object>>(
							c.size());
					for (Integer i : c) {
						list.add(ls.get(i));
					}
				}
				setCacheableListAsync(pool, maxThreads, maxWaitSeconds, "set",
						list, compress, expiry);
			} finally {
				con.close();
			}
		}
	}

	public <E extends Cacheable<?>> void setCacheable(String cmdname,
			String keyPrefix, E value, boolean compress, Date expiry)
			throws IOException, InterruptedException {
		String key = convertKey(keyPrefix + "." + value.getIdString());
		MemcachedConnection con = poolGroup.getConnection(key, null);
		try {
			Stream stream = new Stream(null, new ByteArrayOutputStream());
			value.writeData(stream);
			con.set(this, compress, cmdname, key,
					((ByteArrayOutputStream) stream.getOutputStream())
							.toByteArray(), expiry);
		} finally {
			con.close();
		}
	}

	/**
	 * 获取一个键值
	 * 
	 * @param key
	 *            键值数组
	 * @param hashCode
	 *            指定哈希值，完成分布式的服务器访问。如果为0，则按默认规则选择服务器
	 * @param classLoader
	 *            反序列化时的类装载器，通常为null
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public Object get(String key) throws IOException, InterruptedException {
		key = convertKey(key);
		MemcachedConnection con = poolGroup.getConnection(key, null);
		try {
			return con.get(this, key);
		} finally {
			con.close();
		}
	}

	/**
	 * 获取一个或多个值，键值可能分布在不同的服务器上
	 * 
	 * @param keys
	 *            要获取的键名数组
	 * @param hashCodes
	 *            指定哈希值列表，用于查找合适的服务器。如果为null，则取键值列表中的各键的哈希值，如果不为null，
	 *            则哈希列表与键列表必须具备一一对应的关系
	 * @param map
	 *            返回的键值map
	 * @param classLoader
	 *            类装载器，通常为null
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public void get(List<String> keys, Map<String, Object> map)
			throws InterruptedException, IOException {
		if (keys == null || keys.size() == 0)
			return;
		for (int i = 0; i < keys.size(); i++)
			keys.set(i, convertKey(keys.get(i)));
		Map<ConnectionPool<MemcachedConnection, IOException>, Collection<Integer>> poolMap = poolGroup
				.getConnectionPools(keys, null);
		Iterator<ConnectionPool<MemcachedConnection, IOException>> it = poolMap
				.keySet().iterator();
		while (it.hasNext()) {
			ConnectionPool<MemcachedConnection, IOException> pool = it.next();
			Collection<Integer> c = poolMap.get(pool);
			List<String> ks;
			if (c == null) {
				ks = keys;
			} else {
				ks = new ArrayList<String>();
				Iterator<Integer> iit = c.iterator();
				while (iit.hasNext())
					ks.add(keys.get(iit.next()));
			}
			MemcachedConnection con = pool.getConnection();
			try {
				con.get(this, keys, map);
			} finally {
				con.close();
			}
		}
	}

	/**
	 * 增加计数器
	 * 
	 * @param key
	 *            键
	 * @param initValue
	 *            初始值
	 * @param stepValue
	 *            增量步长
	 * @param hashCode
	 *            指定哈希值，用于查找合适的服务器。如果为0，则默认为键名的哈希值
	 * @return -1，设置失败; >=0,操作成功，返回操作后的值
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public long incrdecr(String cmd, String key, long initValue, long stepValue)
			throws IOException, InterruptedException {
		key = convertKey(key);
		MemcachedConnection con = poolGroup.getConnection(key, null);
		try {
			Value<Long> value = new Value<Long>();
			if (con.incrdecr(cmd, key, stepValue, value)) {
				return value.getValue();
			} else {
				con.set(this, false, "add", key, initValue, null);
				return initValue;
			}
		} finally {
			con.close();
		}
	}

	/**
	 * 删除全部缓存内容
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void flushAll() throws InterruptedException, IOException {
		poolGroup.flushAll();
	}

	/**
	 * 删除
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public boolean delete(String key, Date expiry) throws IOException,
			InterruptedException {
		key = convertKey(key);
		MemcachedConnection con = poolGroup.getConnection(key, null);
		try {
			return con.delete(key, expiry == null ? 0 : expiry.getTime());
		} finally {
			con.close();
		}
	}

	public boolean delete(Object keys[], Date expiry) throws IOException,
			InterruptedException {
		for (int i = 0; i < keys.length; i++)
			keys[i] = convertKey(keys[i].toString());
		Map<ConnectionPool<MemcachedConnection, IOException>, Collection<Integer>> map = poolGroup
				.getConnectionPools(keys, null);
		Iterator<ConnectionPool<MemcachedConnection, IOException>> it = map
				.keySet().iterator();
		while (it.hasNext()) {
			ConnectionPool<MemcachedConnection, IOException> pool = it.next();
			MemcachedConnection con = pool.getConnection();
			try {
				Collection<Integer> c = map.get(pool);
				if (c == null) {
					if (!con.delete(keys, expiry))
						return false;
				} else {
					for (Integer i : c)
						if (!con.delete(keys[i].toString(), expiry == null ? 0
								: expiry.getTime()))
							return false;
				}
			} finally {
				con.close();
			}
		}
		return true;
	}
}
