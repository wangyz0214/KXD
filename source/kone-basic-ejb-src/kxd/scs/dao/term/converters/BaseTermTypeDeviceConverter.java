package kxd.scs.dao.term.converters;

import kxd.engine.dao.BaseDaoConverter;
import kxd.remote.scs.beans.BaseDevice;
import kxd.remote.scs.beans.BaseTermType;
import kxd.remote.scs.beans.BaseTermTypeDevice;

public class BaseTermTypeDeviceConverter extends
		BaseDaoConverter<BaseTermTypeDevice> {

	public BaseTermTypeDeviceConverter() {
	}

	
	public BaseTermTypeDevice doConvert(Object result) {
		Object[] o = (Object[]) result;
		BaseTermTypeDevice r = new BaseTermTypeDevice();
		r.setTermType(new BaseTermType(Integer.valueOf(o[0].toString())));
		int port = Integer.valueOf(o[4].toString());
		String name;
		if (port < 100)
			name = "COM" + port;
		else
			name = "LPT" + port;
		r.setDevice(new BaseDevice(Integer.valueOf(o[1].toString()),
				((String) o[2]) + " - " + ((String) o[3]) + " - " + name));
		return r;
	}

}
