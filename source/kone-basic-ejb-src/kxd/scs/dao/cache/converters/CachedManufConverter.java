package kxd.scs.dao.cache.converters;

import kxd.engine.cache.CachedMap;
import kxd.engine.cache.beans.sts.CachedManuf;

public class CachedManufConverter extends
		CachedDaoConverter<Integer, CachedManuf> {

	public CachedManufConverter(CachedMap<Integer, CachedManuf> map) {
		super(map);
	}

	public CachedManuf doConvert(Object result) {
		Object[] o = (Object[]) result;
		CachedManuf r = new CachedManuf();
		r.setId(Integer.valueOf(o[0].toString()));
		r.setManufCode((String) o[1]);
		r.setManufName((String) o[2]);
		if (o[3] == null)
			r.setSerialNumber(0);
		else
			r.setSerialNumber(Integer.valueOf(o[3].toString()));
		r.setManufType(Short.valueOf(o[4].toString()));
		return r;
	}

}
