package kxd.scs.dao.device.converters;

import kxd.engine.dao.BaseDaoConverter;
import kxd.remote.scs.beans.BaseDevice;

public class BaseDeviceConverter extends BaseDaoConverter<BaseDevice> {

	public BaseDeviceConverter() {
	}

	
	public BaseDevice doConvert(Object result) {
		Object[] o = (Object[]) result;
		BaseDevice r = new BaseDevice();
		r.setId(Integer.valueOf(o[0].toString()));
		r.setDeviceName((String) o[1]);
		return r;
	}

}
