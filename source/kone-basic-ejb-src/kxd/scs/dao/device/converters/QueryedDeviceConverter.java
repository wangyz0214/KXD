package kxd.scs.dao.device.converters;

import kxd.engine.dao.BaseDaoConverter;
import kxd.remote.scs.beans.BaseDeviceDriver;
import kxd.remote.scs.beans.BaseDeviceType;
import kxd.remote.scs.beans.device.QueryedDevice;

public class QueryedDeviceConverter extends BaseDaoConverter<QueryedDevice> {

	public QueryedDeviceConverter() {
	}

	
	public QueryedDevice doConvert(Object result) {
		Object[] o = (Object[]) result;
		QueryedDevice r = new QueryedDevice();
		r.setId(Integer.valueOf(o[0].toString()));
		r.setDeviceName((String) o[1]);
		r.setManufDesp((String) o[2]);
		r.setDriver(new BaseDeviceDriver(Integer.valueOf(o[3].toString()),
				(String) o[4]));
		r.setDeviceType(new BaseDeviceType(Integer.valueOf(o[5].toString()),
				(String) o[6]));
		return r;
	}

}
