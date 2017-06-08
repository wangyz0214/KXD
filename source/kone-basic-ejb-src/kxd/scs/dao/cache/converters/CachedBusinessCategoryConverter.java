package kxd.scs.dao.cache.converters;

import kxd.engine.cache.CachedMap;
import kxd.engine.cache.beans.sts.CachedBusinessCategory;

public class CachedBusinessCategoryConverter extends
		CachedDaoConverter<Integer, CachedBusinessCategory> {

	public CachedBusinessCategoryConverter(
			CachedMap<Integer, CachedBusinessCategory> map) {
		super(map);
	}

	public CachedBusinessCategory doConvert(Object result) {
		Object[] o = (Object[]) result;
		CachedBusinessCategory r = new CachedBusinessCategory();
		r.setId(Integer.valueOf(o[0].toString()));
		r.setBusinessCategoryDesp(o[1].toString());
		return r;
	}

}
