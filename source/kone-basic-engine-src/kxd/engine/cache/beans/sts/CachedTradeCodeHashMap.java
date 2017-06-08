package kxd.engine.cache.beans.sts;

import java.util.concurrent.ConcurrentHashMap;

import kxd.engine.cache.CacheConfigParams;
import kxd.engine.cache.CachedHashMap;
import kxd.engine.cache.CachedIdable;

public class CachedTradeCodeHashMap extends
		CachedHashMap<Integer, CachedTradeCode> {
	private static ConcurrentHashMap<String, CachedTradeCode> codeServMap = new ConcurrentHashMap<String, CachedTradeCode>();

	public CachedTradeCodeHashMap(CacheConfigParams params, String prefix,
			Class<CachedTradeCode> valueClazz,
			Class<? extends CachedIdable<Integer>> idClazz) {
		super(36000, params, prefix, valueClazz, idClazz);
	}

	@Override
	public synchronized boolean refresh(boolean force) {
		if (super.refresh(force)) {
			codeServMap.clear();
			for (CachedTradeCode o : map.values()) {
				codeServMap.put(o.getTradeCode() + "[|]" + o.getService(), o);
			}
			return true;
		} else
			return false;
	}

	public CachedTradeCode getByCodeServ(String tradeCode, String tradeService) {
		refresh(false);
		return codeServMap.get(tradeCode + "[|]" + tradeService);
	}
}
