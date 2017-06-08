package kxd.scs.dao.cache.converters;

import java.io.Serializable;

import kxd.engine.cache.CachedMap;
import kxd.engine.dao.BaseDaoConverter;
import kxd.util.memcached.Cacheable;

abstract public class CachedDaoConverter<K extends Serializable, V extends Cacheable<K>>
		extends BaseDaoConverter<V> {
	abstract protected V doConvert(Object result);

	CachedMap<K, V> map;

	public CachedDaoConverter(CachedMap<K, V> map) {
		super();
		this.map = map;
	}

}
