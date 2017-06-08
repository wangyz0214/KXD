package kxd.engine.cache;

import java.io.Serializable;

import kxd.util.memcached.Cacheable;

public interface NulledCacheable<E extends Serializable> extends Cacheable<E> {
	public boolean isNullValue();
}
