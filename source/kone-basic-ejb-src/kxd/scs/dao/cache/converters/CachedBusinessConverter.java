package kxd.scs.dao.cache.converters;

import kxd.engine.cache.CachedMap;
import kxd.engine.cache.beans.sts.CachedBusiness;

public class CachedBusinessConverter extends
		CachedDaoConverter<Integer, CachedBusiness> {

	public CachedBusinessConverter(CachedMap<Integer, CachedBusiness> map) {
		super(map);
	}

	public CachedBusiness doConvert(Object result) {
		Object[] o = (Object[]) result;
		CachedBusiness r = new CachedBusiness();
		r.setId(Integer.valueOf(o[0].toString()));
		r.setBusinessDesp(o[1].toString());
		r.setBusinessCategoryId(Integer.valueOf(o[2].toString()));
		return r;
	}

}
