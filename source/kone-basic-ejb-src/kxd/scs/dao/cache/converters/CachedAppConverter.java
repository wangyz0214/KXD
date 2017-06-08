package kxd.scs.dao.cache.converters;

import kxd.engine.cache.CachedMap;
import kxd.engine.cache.beans.sts.CachedApp;

public class CachedAppConverter extends CachedDaoConverter<Integer, CachedApp> {

	public CachedAppConverter(CachedMap<Integer, CachedApp> map) {
		super(map);
	}

	public CachedApp doConvert(Object result) {
		Object[] o = (Object[]) result;
		CachedApp r = new CachedApp();
		r.setId(Integer.valueOf(o[0].toString()));
		r.setAppDesp(o[1].toString());
		r.setAppCode((String) o[2]);
		r.setAppCategoryId(Integer.valueOf(o[3].toString()));
		return r;
	}

}
