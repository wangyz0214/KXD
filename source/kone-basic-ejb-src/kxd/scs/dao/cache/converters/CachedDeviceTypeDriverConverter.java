package kxd.scs.dao.cache.converters;

import kxd.engine.cache.CachedMap;
import kxd.engine.cache.beans.sts.CachedDeviceTypeDriver;

public class CachedDeviceTypeDriverConverter extends
		CachedDaoConverter<Integer, CachedDeviceTypeDriver> {

	public CachedDeviceTypeDriverConverter(
			CachedMap<Integer, CachedDeviceTypeDriver> map) {
		super(map);
	}

	
	public CachedDeviceTypeDriver doConvert(Object result) {
		Object[] o = (Object[]) result;
		CachedDeviceTypeDriver r = new CachedDeviceTypeDriver();
		r.setId(Integer.valueOf(o[0].toString()));
		r.setDeviceTypeDriverDesp(o[1].toString());
		r.setDriverFile((String) o[2]);
		return r;
	}
}
