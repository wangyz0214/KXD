package kxd.engine.cache;

import kxd.engine.cache.beans.sts.CachedTerm;
import kxd.engine.cache.beans.sts.CachedTermConfig;

public class CachedTermMap extends CachedMap<Integer, CachedTermConfig> {

	public CachedTermMap(CacheConfigParams params, String prefix,
			Class<CachedTermConfig> valueClazz) {
		super(params, prefix, valueClazz);
	}

	public CachedTerm getTerm(Integer key) {
		CachedTermConfig c = get(key);
		if (c != null)
			return c.getTerm();
		else
			return null;
	}
}
