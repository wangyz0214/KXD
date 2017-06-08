package kxd.scs.dao.cache.converters;

import kxd.engine.cache.CachedMap;
import kxd.engine.cache.beans.sts.CachedPayItem;

public class CachedPayItemConverter extends
		CachedDaoConverter<Short, CachedPayItem> {

	public CachedPayItemConverter(CachedMap<Short, CachedPayItem> map) {
		super(map);
	}

	
	public CachedPayItem doConvert(Object result) {
		Object[] o = (Object[]) result;
		CachedPayItem r = new CachedPayItem();
		r.setId(Short.valueOf(o[0].toString()));
		r.setPayItemDesp(o[1].toString());
		r.setNeedTrade(Integer.valueOf(o[2].toString()) != 0);
		r.setPrice(Long.valueOf(o[3].toString()));
		r.setMemo((String) o[4]);
		return r;
	}
}
