package kxd.scs.dao.cache.converters;

import kxd.engine.cache.CachedMap;
import kxd.engine.cache.beans.sts.CachedDevice;

public class CachedDeviceConverter extends
		CachedDaoConverter<Integer, CachedDevice> {

	public CachedDeviceConverter(CachedMap<Integer, CachedDevice> map) {
		super(map);
	}

	
	public CachedDevice doConvert(Object result) {
		Object[] o = (Object[]) result;
		CachedDevice r = new CachedDevice();
		r.setId(Integer.valueOf(o[0].toString()));
		r.setDeviceDriverId(Integer.valueOf(o[1].toString()));
		r.setDeviceName((String) o[2]);
		r.setDeviceTypeId(Integer.valueOf(o[3].toString()));
		r.setExtConfig((String) o[4]);
		r.setManufDesp((String) o[5]);
		return r;
	}

}
