package kxd.engine.cache;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.NamingException;

import kxd.engine.cache.interfaces.CacheServiceBeanRemote;
import kxd.net.connection.jndi.Lookuper;
import kxd.net.naming.LoopNamingContext;
import kxd.net.naming.NamingContext;
import kxd.remote.scs.util.AppException;
import kxd.util.memcached.Cacheable;
import kxd.util.stream.Stream;

import org.apache.log4j.Logger;

/**
 * 缓存MAP<br>
 * 本类实现Map接口，可以通过访问Map的方式访问缓存<br>
 * 本类不支持本地缓存，全部实时从缓存服务器获取数据，也不支持集合，只能简单支持get,put,delete等方法
 * 
 * @author zhaom
 * 
 * @param <K>
 * @param <V>
 */
public class CachedMap<K extends Serializable, V extends Cacheable<K>>
		implements Map<K, V> {
	private static Logger logger = Logger.getLogger(CachedMap.class);

	protected String keyPrefix;
	private CacheConfigParams params;
	protected Class<V> valueClazz;
	private Object externalParam;

	protected void setKeyPrefix(String prefix) {
		keyPrefix = prefix + ".data.";
	}

	public CachedMap(CacheConfigParams params, String prefix,
			Class<V> valueClazz) {
		super();
		this.params = params;
		this.valueClazz = valueClazz;
		setKeyPrefix(prefix);
	}

	public CacheConfigParams getParams() {
		return params;
	}

	public String getKeyPrefix() {
		return keyPrefix;
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsKey(Object key) {
		return get(key) != null;
	}

	@Override
	public boolean containsValue(Object value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		throw new UnsupportedOperationException();
	}

	@SuppressWarnings("unchecked")
	public Map<K, V> getCacheValues(List<K> keyList) throws NamingException {
		if (keyList == null || keyList.size() == 0)
			return null;
		List<K> keys = new ArrayList<K>();
		keys.addAll(keyList);
		List<String> ks = new ArrayList<String>();
		for (K k : keys) {
			ks.add(getKeyPrefix() + k);
		}
		HashMap<K, V> ret = new HashMap<K, V>();
		try {
			// logger.debug("从缓存中获取[" + this.getKeyPrefix() + "]" + keys);
			Map<String, Object> map = getParams().getMemCachedClient().get(ks);
			Iterator<String> it = map.keySet().iterator();
			int beginIndex = this.keyPrefix.length();
			while (it.hasNext()) {
				String key = it.next();
				V v = valueClazz.newInstance();
				Stream stream = new Stream(new ByteArrayInputStream(
						(byte[]) map.get(key)), null);
				v.readData(stream);
				v.setIdString(key.substring(beginIndex));
				keys.remove(v.getId());
				ret.put(v.getId(), v);
			}
		} catch (Throwable e) {
			logger.error("获取[" + keys + "]失败：", e);
		}
		if (keys.size() > 0) { // 假如缓存没有取全，则从数据库获取剩余的键
			// logger.debug("从数据中获取[" + keys + "]");
			NamingContext context = new LoopNamingContext(getParams()
					.getJndiConfigName());
			try {
				CacheServiceBeanRemote bean = context.lookup(
						Lookuper.JNDI_TYPE_EJB, getParams().getJndiBeanName(),
						CacheServiceBeanRemote.class);
				List<?> rs = bean.getValues(valueClazz.getSimpleName(), keys,
						externalParam);
				for (Object o : rs) {
					V v = (V) o;
					ret.put(v.getId(), v);
				}
			} finally {
				context.close();
			}
		}
		return ret;
	}

	/**
	 * get的具体实现，先从缓存中获取数据，如果获取不成功，则向数据库中获取
	 * 
	 * @param key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public V getCache(K key) {
		if (key == null)
			return null;
		// logger.debug("load cache object[key=" + key + "]...");
		V ret;
		try {
			ret = params.getMemCachedClient().getStreamable(
					getKeyPrefix() + key, valueClazz);
			if (ret != null && ret.isNullValue())
				return null;
		} catch (Throwable e) {
			logger.error("getCache(" + key + ") error:", e);
			ret = null;
		}
		if (ret == null) {
			try {
				// logger.debug("cache object[key=" + key
				// + "] dos not exist,loaded from the database");
				NamingContext context = new LoopNamingContext(
						params.getJndiConfigName());
				try {
					CacheServiceBeanRemote bean = context.lookup(
							Lookuper.JNDI_TYPE_EJB, params.getJndiBeanName(),
							CacheServiceBeanRemote.class);
					Object data = bean.getKeyValue(valueClazz.getSimpleName(),
							key, externalParam);
					if (data != null) {
						ret = (V) data;
					}
				} finally {
					context.close();
				}
			} catch (NamingException e) {
				logger.error("cache object[" + key + "] load failure:", e);
				throw new AppException(e);
			}
		}
		if (ret != null)
			ret.setId(key);
		return ret;
	}

	/**
	 * 从缓存中获取数据，如果缓存中没有数据，则从EJB中获取数据并保存至缓存中然后返回。
	 */
	@SuppressWarnings("unchecked")
	@Override
	public V get(Object key) {
		return getCache((K) key);
	}

	@Override
	public boolean isEmpty() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<K> keySet() {
		throw new UnsupportedOperationException();
	}

	/**
	 * 设置缓存数据<br>
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean putCache(K key, V value) {
		try {
			// logger.debug("set[key=" + this.keyPrefix + key + "]");
			params.getMemCachedClient().setStreamable(this.keyPrefix + key,
					value, null);
			return true;
		} catch (Throwable e) {
			logger.error("设置缓存值失败[" + this.keyPrefix + key + "]", e);
			return false;
		}
	}

	public boolean putCache(K key, V value, Date expiry) {
		try {
			// logger.debug("set[key=" + this.keyPrefix + key + "]");
			params.getMemCachedClient().setStreamable(this.keyPrefix + key,
					value, expiry);
			return true;
		} catch (Throwable e) {
			logger.error("设置缓存值失败[" + this.keyPrefix + key + "]", e);
			return false;
		}
	}

	/**
	 * 向缓存添加元素
	 * 
	 * @return null
	 */
	@Override
	public V put(K key, V value) {
		putCache(key, value);
		return null;
	}

	/**
	 * 向缓存中添加元素集合
	 */
	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		putCacheAll(m.values());
	}

	/**
	 * 向缓存中添加元素集合
	 */
	public void putAll(Collection<? extends V> m) {
		putCacheAll(m);
	}

	public void putCacheAll(Collection<? extends V> m) {
		String keyPrefix = this.keyPrefix.substring(0,
				this.keyPrefix.length() - 1);
		try {
			// logger.debug("设置缓存值列表[" + this.keyPrefix + "]");
			params.getMemCachedClient().setCacheableListAsync(100, 100,
					keyPrefix, m, false, null);
			// (keyPrefix, m, null);
		} catch (Throwable e) {
			logger.error("设置缓存值列表失败[" + keyPrefix + "]", e);
		}
	}

	/**
	 * 移除缓存数据
	 * 
	 * @param key
	 * @param value
	 * @return 删除是否成功
	 */
	protected boolean removeCache(K key) {
		try {
			// logger.debug("remove[key=" + this.keyPrefix + key + "]");
			return params.getMemCachedClient().delete(this.keyPrefix + key);
		} catch (Throwable e) {
			logger.error("删除缓存值失败[" + this.keyPrefix + key + "]", e);
			return false;
		}
	}

	/**
	 * 从缓存中移除元素
	 * 
	 * @return 只返回null
	 */
	@SuppressWarnings("unchecked")
	@Override
	public V remove(Object key) {
		removeCache((K) key);
		return null;
	}

	/**
	 * 移除缓存集合
	 * 
	 * @param keys
	 * @return
	 */
	public boolean removeAll(Collection<K> keys) {
		return removeCacheAll(keys);
	}

	/**
	 * 移除缓存集合
	 * 
	 * @param keys
	 * @return
	 */
	public boolean removeAll(K[] keys) {
		return removeCacheAll(keys);
	}

	/**
	 * 移除缓存集合
	 * 
	 * @param keys
	 * @return
	 */
	public boolean removeCacheAll(Collection<K> keys) {
		try {
			String ks[] = new String[keys.size()];
			// List<String> ls = new ArrayList<String>();
			int i = 0;
			for (K k : keys) {
				// ls.add(this.keyPrefix + k);
				ks[i++] = this.keyPrefix + k;
			}
			params.getMemCachedClient().delete(ks, null);
			// params.getMemCachedClient().get(ls);
			return true;
		} catch (Throwable e) {
			return false;
		}
	}

	/**
	 * 移除缓存集合
	 * 
	 * @param keys
	 * @return
	 */
	public boolean removeCacheAll(K[] keys) {
		try {
			Object ks[] = new String[keys.length];
			int i = 0;
			for (K k : keys)
				ks[i++] = this.keyPrefix + k;
			params.getMemCachedClient().delete(ks, null);
			return true;
		} catch (Throwable e) {
			return false;
		}
	}

	@Override
	public int size() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Collection<V> values() {
		throw new UnsupportedOperationException();
	}

	public Object getExternalParam() {
		return externalParam;
	}

	public void setExternalParam(Object externalParam) {
		this.externalParam = externalParam;
	}

}
