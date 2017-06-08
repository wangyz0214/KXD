package kxd.scs.dao.appdeploy.converters;

import kxd.engine.dao.BaseDaoConverter;
import kxd.remote.scs.beans.BaseApp;

public class BaseAppConverter extends BaseDaoConverter<BaseApp> {

	public BaseAppConverter() {
	}

	
	public BaseApp doConvert(Object result) {
		Object[] o = (Object[]) result;
		BaseApp r = new BaseApp();
		r.setId(Integer.valueOf(o[0].toString()));
		r.setAppDesp(o[1].toString());
		return r;
	}

}
