package kxd.engine.cache;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.naming.NamingException;

import kxd.engine.cache.interfaces.CacheServiceBeanRemote;
import kxd.net.connection.jndi.Lookuper;
import kxd.net.naming.LoopNamingContext;
import kxd.net.naming.NamingContext;
import kxd.remote.scs.util.AppException;
import kxd.util.DataUnit;
import kxd.util.DateTime;
import kxd.util.KeyValue;
import kxd.util.memcached.Cacheable;
import kxd.util.stream.Stream;

import org.apache.log4j.Logger;

public class CachedHashMap<K extends Serializable, V extends Cacheable<K>>
		extends CachedMap<K, V> {

	private static Logger logger = Logger.getLogger(CachedHashMap.class);
	protected ConcurrentHashMap<K, V> map = new ConcurrentHashMap<K, V>();
	private String keysKey;
	private String checkSumKey;
	private byte[] checksum = null;
	private Class<? extends CachedIdable<K>> idClazz;
	private volatile int refreshInterval = 60;

	/**
	 * 构造器
	 * 
	 * @param refresInterval
	 *            定时刷新的时间间隔，以秒为单位
	 * @param params
	 *            缓存配置参数
	 * @param prefix
	 *            缓存前缀
	 * @param valueClazz
	 *            生成缓存的类
	 * @param idClazz
	 *            生成缓存ID的类
	 */
	public CachedHashMap(int refresInterval, CacheConfigParams params,
			String prefix, Class<V> valueClazz,
			Class<? extends CachedIdable<K>> idClazz) {
		super(params, prefix, valueClazz);
		this.idClazz = idClazz;
		this.refreshInterval = refresInterval;
	}

	public boolean keysLoading() {
		try {
			return getParams().getMemCachedClient().get(
					getKeyPrefix() + "loading") != null;
		} catch (Throwable e) {
			return false;
		}
	}

	public void setKeysLoading(boolean loading) {
		try {
			if (loading)
				getParams().getMemCachedClient().set(
						getKeyPrefix() + "loading", true,
						new DateTime().addSeconds(30).getTime());
			else
				getParams().getMemCachedClient().delete(
						getKeyPrefix() + "loading");
		} catch (Throwable e) {
		}
	}

	@Override
	protected void setKeyPrefix(String prefix) {
		super.setKeyPrefix(prefix);
		keysKey = prefix + ".keys";
		checkSumKey = prefix + ".checksum";
	}

	public void deleteCheckSum() {
		try {
			// logger.debug("delete checksum[" + getKeyPrefix() + "]");
			getParams().getMemCachedClient().delete(
					new Object[] { checkSumKey, keysKey }, null);
		} catch (Throwable e) {
		}
	}

	public byte[] setKeys(List<? extends CachedIdable<K>> ls) {
		try {
			getParams().getMemCachedClient().setStreamableList(keysKey, ls,
					null);
		} catch (Throwable e) {
		}
		return setChecksum(ls);
	}

	private byte[] setChecksum(List<? extends CachedIdable<K>> ls) {
		try {
			checksum = UUID.randomUUID().toString().getBytes();
			// logger.debug("set checksum[" + checkSumKey + "]:"
			// + new String(checksum));
			getParams().getMemCachedClient().set(checkSumKey, checksum);
		} catch (Throwable e) {
		}
		return checksum;
	}

	/**
	 * 获取键值列表摘要
	 * 
	 * @return
	 * @throws NamingException
	 */
	private byte[] getCheckSum() throws NamingException {
		byte[] ret = null;
		try {
			ret = (byte[]) getParams().getMemCachedClient().get(checkSumKey);
			// if (ret == null)
			// logger.debug("get checksum[" + getKeyPrefix() + "]: null");
			// else
			// logger.debug("get checksum[" + getKeyPrefix() + "]: "
			// + new String(ret));
		} catch (Throwable e) {
			logger.error("获取[" + this.keyPrefix + "]摘要失败：", e);
			ret = null;
		}
		return ret;
	}

	/**
	 * 获取键值列表
	 * 
	 * @return
	 * @throws NamingException
	 */
	private List<?> getKeys() throws NamingException {
		List<?> ret = null;
		try {
			ret = getParams().getMemCachedClient().getStreamableList(
					this.keysKey, idClazz);
		} catch (Throwable e) {
			logger.error("获取[" + this.keyPrefix + "]摘要失败：", e);
			ret = null;
		}
		if (ret == null) { // 假如缓存不存在，则从数据库中获取
			// logger.debug("从数据中获取[" + this.keyPrefix + "]摘要");
			NamingContext context = new LoopNamingContext(getParams()
					.getJndiConfigName());
			try {
				CacheServiceBeanRemote bean = context.lookup(
						Lookuper.JNDI_TYPE_EJB, getParams().getJndiBeanName(),
						CacheServiceBeanRemote.class);
				KeyValue<byte[], List<?>> r = bean.getKeys(
						valueClazz.getSimpleName(), getExternalParam());
				checksum = r.getKey();
				ret = r.getValue();
			} finally {
				context.close();
			}
		}
		if (ret == null)
			throw new NamingException("获取[" + this.keyPrefix + "]键列表失败");
		return ret;
	}

	/**
	 * 获取键列表指定的数据
	 * 
	 * @param keys
	 * @return
	 * @throws NamingException
	 */
	@SuppressWarnings("unchecked")
	private void getValues(List<? extends CachedIdable<K>> keys)
			throws NamingException {
		if (keys == null || keys.size() == 0)
			return;
		List<String> ks = new ArrayList<String>();
		for (CachedIdable<K> k : keys) {
			ks.add(getKeyPrefix() + k.getId());
		}
		try {
			// logger.debug("从缓存中获取[" + this.getKeyPrefix() + "]" + keys);
			Map<String, Object> map = getParams().getMemCachedClient().get(ks);
			Iterator<String> it = map.keySet().iterator();
			int beginIndex = this.keyPrefix.length();
			CachedIdable<K> id = idClazz.newInstance();
			while (it.hasNext()) {
				String key = it.next();
				V v = valueClazz.newInstance();
				Stream stream = new Stream(new ByteArrayInputStream(
						(byte[]) map.get(key)), null);
				v.readData(stream);
				v.setIdString(key.substring(beginIndex));
				id.setId(v.getId());
				keys.remove(id);
				this.map.put(v.getId(), v);
			}
		} catch (Throwable e) {
			logger.error("获取[" + keys + "]失败：", e);
		}
		if (keys.size() > 0) { // 假如缓存没有取全，则从数据库获取剩余的键
			// logger.debug("从数据中获取[" + keys + "]");
			List<K> list = new ArrayList<K>();
			for (CachedIdable<K> o : keys) {
				list.add(o.getId());
			}
			NamingContext context = new LoopNamingContext(getParams()
					.getJndiConfigName());
			try {
				CacheServiceBeanRemote bean = context.lookup(
						Lookuper.JNDI_TYPE_EJB, getParams().getJndiBeanName(),
						CacheServiceBeanRemote.class);
				List<?> rs = bean.getValues(valueClazz.getSimpleName(), list,
						getExternalParam());
				for (Object o : rs) {
					V v = (V) o;
					this.map.put(v.getId(), v);
				}
			} finally {
				context.close();
			}
		}
	}

	private long lastCheckTime = 0;
	private long lastRefreshTime = 0;

	public long getLastRefreshTime() {
		return lastRefreshTime;
	}

	@SuppressWarnings("unchecked")
	synchronized public boolean refresh(boolean force) {
		try {
			if (!force
					&& DateTime.secondsBetween(System.currentTimeMillis(),
							lastCheckTime) < refreshInterval)
				return false;
			long t = System.currentTimeMillis();
			while (keysLoading()
					&& DateTime.secondsBetween(t, System.currentTimeMillis()) < 30) {
				Thread.sleep(1000);
			}
			lastCheckTime = System.currentTimeMillis();
			byte[] s = getCheckSum();
			if (!force && checksum != null) {
				if (s != null && s.length == checksum.length
						&& DataUnit.memcmp(checksum, 0, s, 0, s.length) == 0) { // 摘要未改变，表示数据未改变，不需要刷新
					// logger.debug("checksum[" + checkSumKey + "]:"
					// + new String(checksum) + " not change.");
					return false;
				}
			}
			logger.debug("refresh cache [" + getKeyPrefix() + "] ...");
			lastRefreshTime = System.currentTimeMillis();
			checksum = s;
			List<CachedIdable<K>> ls = (List<CachedIdable<K>>) getKeys();
			if (checksum == null)
				setChecksum(ls);
			map.clear();
			getValues(ls);
			return true;
		} catch (Throwable e) {
			logger.error("refresh failure[keyprefix:" + getKeyPrefix()
					+ " jndi{" + getParams().getJndiConfigName() + "-"
					+ getParams().getJndiBeanName() + "}]:", e);
			throw new AppException(e);
		}
	}

	@Override
	public void clear() {
		map.clear();
	}

	@Override
	public boolean containsKey(Object key) {
		refresh(false);
		return map.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		refresh(false);
		return map.containsValue(value);
	}

	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		refresh(false);
		return map.entrySet();
	}

	@Override
	public V get(Object key) {
		if (key == null)
			throw new NullPointerException("Key can not be null.");
		refresh(false);
		return map.get(key);
	}

	public Enumeration<K> keys() {
		refresh(false);
		return map.keys();
	}

	@SuppressWarnings("unchecked")
	public V getLatest(Object key) {
		return super.getCache((K) key);
	}

	@Override
	public boolean isEmpty() {
		refresh(false);
		return map.isEmpty();
	}

	@Override
	public Set<K> keySet() {
		refresh(false);
		return map.keySet();
	}

	@Override
	public V put(K key, V value) {
		deleteCheckSum();
		remove(key);
		return null;
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		putAll(m.values());
	}

	@Override
	public V remove(Object key) {
		deleteCheckSum();
		super.remove(key);
		return null;
	}

	@Override
	public void putAll(Collection<? extends V> m) {
		deleteCheckSum();
		List<K> keys = new ArrayList<K>();
		for (V v : m)
			keys.add(v.getId());
		super.removeAll(keys);
	}

	@Override
	public boolean removeAll(Collection<K> keys) {
		deleteCheckSum();
		super.removeAll(keys);
		return true;
	}

	@Override
	public boolean removeAll(K[] keys) {
		deleteCheckSum();
		super.removeAll(keys);
		return true;
	}

	@Override
	public int size() {
		refresh(false);
		return map.size();
	}

	@Override
	public Collection<V> values() {
		refresh(false);
		Collection<V> v = new ArrayList<V>();
		v.addAll(map.values());
		return v;
	}

	public Collection<V> values(List<K> keys) {
		refresh(false);
		Collection<V> v = new ArrayList<V>();
		for (K k : keys) {
			v.add(get(k));
		}
		return v;
	}

	public Collection<String> textValues(List<K> keys) {
		refresh(false);
		Collection<String> v = new ArrayList<String>();
		for (K k : keys) {
			v.add(get(k).toString());
		}
		return v;
	}

	public Collection<KeyValue<String, Object[]>> keyValues(List<K> keys,
			Object[] value) {
		refresh(false);
		Collection<KeyValue<String, Object[]>> v = new ArrayList<KeyValue<String, Object[]>>();
		for (K k : keys) {
			v.add(new KeyValue<String, Object[]>(get(k).toString(), value));
		}
		return v;
	}
}
