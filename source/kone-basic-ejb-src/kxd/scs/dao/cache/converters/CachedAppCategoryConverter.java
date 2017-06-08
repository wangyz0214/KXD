package kxd.scs.dao.cache.converters;

import kxd.engine.cache.CachedMap;
import kxd.engine.cache.beans.sts.CachedAppCategory;

public class CachedAppCategoryConverter extends
		CachedDaoConverter<Integer, CachedAppCategory> {

	public CachedAppCategoryConverter(CachedMap<Integer, CachedAppCategory> map) {
		super(map);
	}

	public CachedAppCategory doConvert(Object result) {
		Object[] o = (Object[]) result;
		CachedAppCategory r = new CachedAppCategory();
		r.setId(Integer.valueOf(o[0].toString()));
		r.setAppCategoryCode(o[1].toString());
		r.setAppCategoryDesp(o[2].toString());
		return r;
	}

}
