package kxd.scs.dao.device.converters;

import kxd.engine.dao.BaseDaoConverter;
import kxd.remote.scs.beans.BaseDeviceType;
import kxd.remote.scs.beans.BaseDeviceTypeDriver;

public class BaseDeviceTypeConverter extends BaseDaoConverter<BaseDeviceType>{

	public BaseDeviceTypeConverter() {
	}

	
	public BaseDeviceType doConvert(Object result) {
		Object[] o = (Object[]) result;
		BaseDeviceType r = new BaseDeviceType();
		r.setId(Integer.valueOf(o[0].toString()));
		r.setDeviceTypeCode((String) o[1]);
		r.setDeviceTypeDesp((String) o[2]);
		r.setDriver(new BaseDeviceTypeDriver(Integer.valueOf(o[3].toString()),
				(String) o[4]));
		return r;
	}
}
