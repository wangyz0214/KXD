package kxd.scs.dao.cache.converters;

import kxd.engine.cache.CachedMap;
import kxd.engine.cache.beans.sts.CachedFileCategory;
import kxd.remote.scs.util.emun.FileCachedType;

public class CachedFileCategoryConverter extends
		CachedDaoConverter<Short, CachedFileCategory> {

	public CachedFileCategoryConverter(CachedMap<Short, CachedFileCategory> map) {
		super(map);
	}

	public CachedFileCategory doConvert(Object result) {
		Object[] o = (Object[]) result;
		CachedFileCategory r = new CachedFileCategory();
		r.setId(Short.valueOf(o[0].toString()));
		r.setFileCategoryDesp(o[1].toString());
		r.setCachedType(FileCachedType.valueOfIntString(o[2]));
		r.setFileHost(Short.valueOf(o[3].toString()));
		return r;
	}
}
