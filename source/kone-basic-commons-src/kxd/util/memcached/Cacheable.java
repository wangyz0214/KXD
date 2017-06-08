package kxd.util.memcached;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

import kxd.util.ListItemable;
import kxd.util.Streamable;

public interface Cacheable<E extends Serializable> extends Streamable,
		ListItemable<E> {
	/**
	 * MemCachedClient实例MAP，包含全部配置的实例
	 */
	public static final ConcurrentHashMap<String, MemCachedClient> memCachedClientMap = MemcachedConnectionFactory
			.getClientInstanceMap();

	/**
	 * 指定当前缓存值是否是null
	 * 
	 */
	public boolean isNullValue();
}
