package kxd.util.memcached;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import kxd.util.IdStreamable;
import kxd.util.KeyValue;
import kxd.util.ObjectCreator;
import kxd.util.Streamable;
import kxd.util.stream.Stream;

/**
 * <p>
 * MemCached客户端类，本类完成对MemCached执行器的封装
 * 
 * @author 赵明
 * @version 1.0
 */
public class MemCachedClient {
	public static MemCachedClient getInstance(String name)
			throws MemCachedException {
		return MemcachedConnectionFactory.getClientInstance(name);
	}

	/**
	 * 执行器
	 */
	MemCachedExecutor executor;

	/**
	 * 创建一个MemCached客户端
	 * 
	 * @param executor
	 *            MemCached执行器
	 */
	public MemCachedClient(MemCachedExecutor executor) {
		this.executor = executor;
	}

	/**
	 * 删除全部缓存内容
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void flushAll() throws InterruptedException, IOException {
		executor.flushAll();
	}

	/**
	 * 删除一个值
	 * 
	 * @param key
	 *            键名
	 * @param expiry
	 *            过期时间，为null则不过期
	 * @return 删除成功返回true，键不存在返回false
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public boolean delete(String key, Date expiry) throws InterruptedException,
			IOException {
		return executor.delete(key, expiry);
	}

	public boolean delete(Object[] keys, Date expiry)
			throws InterruptedException, IOException {
		return executor.delete(keys, expiry);
	}

	/**
	 * 删除一个值
	 * 
	 * @param key
	 *            键名
	 * @return 删除成功返回true，键不存在返回false
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public boolean delete(String key) throws InterruptedException, IOException {
		return delete(key, null);
	}

	/**
	 * 保存一个值，如果键已经存在，则复写
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @return 保存成功返回true，否则返回false
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void set(String key, Object value) throws IOException,
			InterruptedException {
		set("set", key, value, false, null);
	}

	/**
	 * 保存一个值，如果键已经存在，则复写
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @param compress
	 *            是否压缩
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void set(String key, Object value, boolean compress)
			throws IOException, InterruptedException {
		set("set", key, value, compress, null);
	}

	/**
	 * 保存一个值，如果键已经存在，则复写
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @param expiry
	 *            过期时间，为null则不过期
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void set(String key, Object value, Date expiry) throws IOException,
			InterruptedException {
		set("set", key, value, false, expiry);
	}

	/**
	 * 保存一个值，如果键已经存在，则复写
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @param compress
	 *            是否压缩
	 * @param expiry
	 *            过期时间，为null则不过期
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void set(String key, Object value, boolean compress, Date expiry)
			throws IOException, InterruptedException {
		set("set", key, value, compress, expiry);
	}

	/**
	 * 添加一个值
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void add(String key, Object value) throws IOException,
			InterruptedException {
		set("add", key, value, false, null);
	}

	/**
	 * 添加一个值
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @param compress
	 *            是否压缩
	 * @throws IOException
	 * @throws InterruptedException
	 */

	public void add(String key, Object value, boolean compress)
			throws IOException, InterruptedException {
		set("add", key, value, compress, null);
	}

	/**
	 * 添加一个值
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @param expiry
	 *            过期时间，为null则不过期
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void add(String key, Object value, Date expiry) throws IOException,
			InterruptedException {
		set("add", key, value, false, expiry);
	}

	/**
	 * 添加一个值
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @param compress
	 *            是否压缩
	 * @param expiry
	 *            过期时间，为null则不过期
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void add(String key, Object value, boolean compress, Date expiry)
			throws IOException, InterruptedException {
		set("add", key, value, compress, expiry);
	}

	/**
	 * 将一个值同时设置到多个键中， 不支持自动分布式定位服务器
	 * 
	 * @param keys
	 * @param value
	 * @param compress
	 * @param expiry
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public void set(String keys[], Object value, boolean compress, Date expiry)
			throws IOException, InterruptedException {
		executor.set("set", keys, value, compress, expiry);
	}

	/**
	 * 一次性设置多个键值对
	 * 
	 * @param keyValues
	 * @param compress
	 * @param expiry
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void set(Map<String, Object> keyValues, boolean compress, Date expiry)
			throws IOException, InterruptedException {
		executor.set("set", keyValues, compress, expiry);
	}

	public <E extends Cacheable<?>> void setCacheableListAsync(int maxThreads,
			int maxWaitSeconds, String keyPrefix, Collection<E> values,
			boolean compress, Date expiry) throws IOException,
			InterruptedException {
		executor.setCacheableListAsync(maxThreads, maxWaitSeconds, keyPrefix,
				values, compress, expiry);
	}

	public void setAsync(int maxThreads, int maxWaitSeconds,
			Collection<KeyValue<String, Object>> values, boolean compress,
			Date expiry) throws IOException, InterruptedException {
		executor.setAsync(maxThreads, maxWaitSeconds, values, compress, expiry);
	}

	/**
	 * 复写一个值
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void replace(String key, Object value) throws IOException,
			InterruptedException {
		set("replace", key, value, false, null);
	}

	/**
	 * 复写一个值
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @param compress
	 *            是否压缩
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void replace(String key, Object value, boolean compress)
			throws IOException, InterruptedException {
		set("replace", key, value, compress, null);
	}

	/**
	 * 复写一个值
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @param expiry
	 *            过期时间，为null则不过期
	 * @return 保存成功返回true，否则返回false
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void replace(String key, Object value, Date expiry)
			throws IOException, InterruptedException {
		set("replace", key, value, false, expiry);
	}

	/**
	 * 复写一个值
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @param compress
	 *            是否压缩
	 * @param expiry
	 *            过期时间，为null则不过期
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void replace(String key, Object value, boolean compress, Date expiry)
			throws IOException, InterruptedException {
		set("replace", key, value, compress, expiry);
	}

	/**
	 * 存储一个值
	 * 
	 * @param cmd
	 *            存储命令
	 * @param key
	 *            键名
	 * @param value
	 *            键值
	 * @param hashCode
	 *            指定哈希值，用于查找合适的服务器。如果为0，则默认为键名的哈希值
	 * @param compress
	 *            是否压缩
	 * @param expiry
	 *            过期时间，为null则不过期
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws NoSuchAlgorithmException
	 */
	protected void set(String cmd, String key, Object value, boolean compress,
			Date expiry) throws IOException, InterruptedException {
		executor.set(cmd, key, value, compress, expiry);
	}

	/**
	 * 获取一个值
	 * 
	 * @param key
	 *            要获取的键名
	 * @throws InterruptedException
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 */
	public Object get(String key) throws InterruptedException, IOException {
		return executor.get(key);
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
	public long incr(String key, long initValue, long stepValue)
			throws IOException, InterruptedException {
		return executor.incrdecr("incr", key, initValue, stepValue);
	}

	/**
	 * 增加计数器，初始值为0，步长为1
	 * 
	 * @param key
	 *            键
	 * @return -1，设置失败; >=0,操作成功，返回操作后的值
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public long incr(String key) throws IOException, InterruptedException {
		return incr(key, 0, 1);
	}

	/**
	 * 减少计数器，步长为1
	 * 
	 * @param key
	 *            键
	 * @param initValue
	 *            初始值
	 * @return -1，设置失败; >=0,操作成功，返回操作后的值
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public long decr(String key, long initValue) throws IOException,
			InterruptedException {
		return decr(key, initValue, 1);
	}

	/**
	 * 增加计数器，步长为1
	 * 
	 * @param key
	 *            键
	 * @param initValue
	 *            初始值
	 * @return -1，设置失败; >=0,操作成功，返回操作后的值
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public long decr(String key, int initValue) throws IOException,
			InterruptedException {
		return decr(key, initValue, 1);
	}

	/**
	 * 减少计数器
	 * 
	 * @param key
	 *            键
	 * @param initValue
	 *            初始值
	 * @param stepValue
	 *            减量步长
	 * @return -1，设置失败; >=0,操作成功，返回操作后的值
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public long decr(String key, long initValue, long stepValue)
			throws IOException, InterruptedException {
		return executor.incrdecr("decr", key, initValue, stepValue);
	}

	/**
	 * 获取一个或多个值，键值可能分布在不同的服务器上
	 * 
	 * @param keys
	 *            要获取的键名数组
	 * @param map
	 *            返回的键值map
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public void get(List<String> keys, Map<String, Object> map)
			throws InterruptedException, IOException {
		executor.get(keys, map);
	}

	/**
	 * 获取一个或多个值，键值可能分布在不同的服务器上
	 * 
	 * @param keys
	 *            要获取的键名数组
	 * @return 返回的键值map
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public Map<String, Object> get(List<String> keys)
			throws InterruptedException, IOException {
		Map<String, Object> map = new HashMap<String, Object>();
		get(keys, map);
		return map;
	}

	public List<Integer> getIntList(String key) throws InterruptedException,
			IOException {
		byte[] b = (byte[]) get(key);
		if (b == null)
			return null;
		Stream stream = new Stream(new ByteArrayInputStream(b), null);
		int c = stream.readInt(false, 3000);
		ArrayList<Integer> it = new ArrayList<Integer>();
		for (int i = 0; i < c; i++)
			it.add(stream.readInt(false, 3000));
		return it;
	}

	public void setIntList(String key, List<Integer> list, Date expiry)
			throws InterruptedException, IOException {
		Stream stream = new Stream(null, new ByteArrayOutputStream());
		int c = list.size();
		stream.writeInt(c, false, 3000);
		Iterator<Integer> it = list.iterator();
		while (it.hasNext())
			stream.writeInt(it.next(), false, 3000);
		set(key,
				((ByteArrayOutputStream) stream.getOutputStream())
						.toByteArray(), expiry);
	}

	public void setIntList(String key, List<Integer> list, int startIndex,
			int endIndex, Date expiry) throws InterruptedException, IOException {
		Stream stream = new Stream(null, new ByteArrayOutputStream());
		stream.writeInt(endIndex - startIndex, false, 3000);
		for (int i = startIndex; i < endIndex; i++)
			stream.writeInt(list.get(i), false, 3000);
		set(key,
				((ByteArrayOutputStream) stream.getOutputStream())
						.toByteArray(), expiry);
	}

	public List<Long> getLongList(String key) throws InterruptedException,
			IOException {
		byte[] b = (byte[]) get(key);
		if (b == null)
			return null;
		Stream stream = new Stream(new ByteArrayInputStream(b), null);
		int c = stream.readInt(false, 3000);
		ArrayList<Long> it = new ArrayList<Long>();
		for (int i = 0; i < c; i++)
			it.add(stream.readLong(3000));
		return it;
	}

	public void setLongList(String key, List<Long> list, Date expiry)
			throws InterruptedException, IOException {
		Stream stream = new Stream(null, new ByteArrayOutputStream());
		int c = list.size();
		stream.writeInt(c, false, 3000);
		Iterator<Long> it = list.iterator();
		while (it.hasNext())
			stream.writeLong(it.next(), 3000);
		set(key,
				((ByteArrayOutputStream) stream.getOutputStream())
						.toByteArray(), expiry);
	}

	public List<Short> getShortList(String key) throws InterruptedException,
			IOException {
		byte[] b = (byte[]) get(key);
		if (b == null)
			return null;
		Stream stream = new Stream(new ByteArrayInputStream(b), null);
		int c = stream.readInt(false, 3000);
		ArrayList<Short> it = new ArrayList<Short>();
		for (int i = 0; i < c; i++)
			it.add(stream.readShort(false, 3000));
		return it;
	}

	public void setShortList(String key, List<Short> list, Date expiry)
			throws InterruptedException, IOException {
		Stream stream = new Stream(null, new ByteArrayOutputStream());
		int c = list.size();
		stream.writeInt(c, false, 3000);
		Iterator<Short> it = list.iterator();
		while (it.hasNext())
			stream.writeShort(it.next(), false, 3000);
		set(key,
				((ByteArrayOutputStream) stream.getOutputStream())
						.toByteArray(), expiry);
	}

	public List<Byte> getByteList(String key) throws InterruptedException,
			IOException {
		byte[] b = (byte[]) get(key);
		if (b == null)
			return null;
		Stream stream = new Stream(new ByteArrayInputStream(b), null);
		int c = stream.readInt(false, 3000);
		ArrayList<Byte> it = new ArrayList<Byte>();
		for (int i = 0; i < c; i++)
			it.add(stream.readByte(3000));
		return it;
	}

	public void setByteList(String key, List<Byte> list, Date expiry)
			throws InterruptedException, IOException {
		Stream stream = new Stream(null, new ByteArrayOutputStream());
		int c = list.size();
		stream.writeInt(c, false, 3000);
		Iterator<Byte> it = list.iterator();
		while (it.hasNext())
			stream.writeByte(it.next(), 3000);
		set(key,
				((ByteArrayOutputStream) stream.getOutputStream())
						.toByteArray(), expiry);
	}

	public <K, E extends IdStreamable<K>> Map<K, E> getIdStreamableMap(
			String key, Map<K, E> map, Class<E> clazz)
			throws InterruptedException, IOException, InstantiationException,
			IllegalAccessException {
		byte[] b = (byte[]) get(key);
		if (b == null)
			return null;
		Stream stream = new Stream(new ByteArrayInputStream(b), null);
		int c = stream.readInt(false, 3000);
		for (int i = 0; i < c; i++) {
			E cl = clazz.newInstance();
			cl.readData(stream);
			map.put(cl.getId(), cl);
		}
		return map;
	}

	public <K, E extends IdStreamable<K>> void setIdStreamableMap(String key,
			Map<K, E> map, Date expiry) throws InterruptedException,
			IOException {
		Stream stream = new Stream(null, new ByteArrayOutputStream());
		int c = map.size();
		stream.writeInt(c, false, 3000);
		Iterator<E> it = map.values().iterator();
		while (it.hasNext())
			it.next().writeData(stream);
		set(key,
				((ByteArrayOutputStream) stream.getOutputStream())
						.toByteArray(), expiry);
	}

	@Deprecated
	public <E extends Streamable> List<E> getStreamableList(String key,
			Integer hashCode, Class<E> clazz) throws InterruptedException,
			IOException, InstantiationException, IllegalAccessException {
		return getStreamableList(key, clazz);
	}

	public <E extends Streamable> List<E> getStreamableList(String key,
			Class<E> clazz) throws InterruptedException, IOException,
			InstantiationException, IllegalAccessException {
		byte[] b = (byte[]) get(key);
		if (b == null)
			return null;
		Stream stream = new Stream(new ByteArrayInputStream(b), null);
		int c = stream.readInt(false, 3000);
		ArrayList<E> ls = new ArrayList<E>();
		for (int i = 0; i < c; i++) {
			E cl = clazz.newInstance();
			cl.readData(stream);
			ls.add(cl);
		}
		return ls;
	}

	public <E extends Streamable> List<E> getStreamableList(String key,
			ObjectCreator<E> creator) throws InterruptedException, IOException,
			InstantiationException, IllegalAccessException {
		byte[] b = (byte[]) get(key);
		if (b == null)
			return null;
		Stream stream = new Stream(new ByteArrayInputStream(b), null);
		int c = stream.readInt(false, 3000);
		ArrayList<E> ls = new ArrayList<E>();
		for (int i = 0; i < c; i++) {
			E cl = creator.newInstance();
			cl.readData(stream);
			ls.add(cl);
		}
		return ls;
	}

	public <E extends Cacheable<?>> void setCacheableList(String keyPrefix,
			Collection<E> list, Date expiry) throws InterruptedException,
			IOException {
		if (list == null || list.size() == 0)
			return;
		executor.setCacheableList("set", keyPrefix, list, false, expiry);
	}

	public <E extends Cacheable<?>> void setCacheable(String keyPrefix,
			E value, Date expiry) throws InterruptedException, IOException {
		executor.setCacheable("set", keyPrefix, value, false, expiry);
	}

	public <E extends Streamable> void setStreamableList(String key,
			List<E> list, Date expiry) throws InterruptedException, IOException {
		if (list == null || list.size() == 0)
			return;
		Stream stream = new Stream(null, new ByteArrayOutputStream());
		int c = list.size();
		stream.writeInt(c, false, 3000);
		Iterator<E> it = list.iterator();
		while (it.hasNext())
			it.next().writeData(stream);
		set(key,
				((ByteArrayOutputStream) stream.getOutputStream())
						.toByteArray(), expiry);
	}

	public <E extends Streamable> void setStreamableList(String key,
			List<E> list, int startIndex, int endIndex, Date expiry)
			throws InterruptedException, IOException {
		if (list == null || endIndex < startIndex)
			return;
		Stream stream = new Stream(null, new ByteArrayOutputStream());
		int c = endIndex - startIndex;
		stream.writeInt(c, false, 3000);
		for (int i = startIndex; i < endIndex; i++)
			list.get(i).writeData(stream);
		set(key,
				((ByteArrayOutputStream) stream.getOutputStream())
						.toByteArray(), expiry);
	}

	/**
	 * 读取流对象，如果对象数据格式出错，则删除键相关的对象
	 * 
	 * @param <E>
	 * @param key
	 * @param hashCode
	 * @param valueClazz
	 * @return
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public <E extends Streamable> E getStreamable(String key,
			Class<E> valueClazz) throws InterruptedException, IOException {
		byte[] b = (byte[]) get(key);
		if (b == null)
			return null;
		E cl;
		try {
			cl = valueClazz.newInstance();
			cl.readData(new Stream(new ByteArrayInputStream(b), null));
		} catch (IOException e) {
			delete(key, null);
			throw e;
		} catch (Throwable e) {
			delete(key, null);
			throw new IOException(e);
		}
		return cl;
	}

	/**
	 * 读取流数据并放到data中，如果对象数据格式出错，则删除键相关的对象
	 * 
	 * @param <E>
	 * @param key
	 * @param hashCode
	 * @param data
	 * @return
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public <E extends Streamable> boolean loadStreamable(String key, E data)
			throws InterruptedException, IOException {
		byte[] b = (byte[]) get(key);
		if (b == null)
			return false;
		try {
			data.readData(new Stream(new ByteArrayInputStream(b), null));
			return true;
		} catch (IOException e) {
			delete(key, null);
			throw e;
		} catch (Throwable e) {
			delete(key, null);
			throw new IOException(e);
		}
	}

	public <E extends Streamable> void setStreamable(String key, E data,
			Date expiry) throws InterruptedException, IOException {
		Stream stream = new Stream(null, new ByteArrayOutputStream());
		data.writeData(stream);
		set(key,
				((ByteArrayOutputStream) stream.getOutputStream())
						.toByteArray(), expiry);
	}

}
