package kxd.scs.dao.appdeploy.converters;

import kxd.engine.dao.BaseDaoConverter;
import kxd.remote.scs.beans.BasePageElement;

public class BasePageElementConverter extends BaseDaoConverter<BasePageElement> {

	public BasePageElementConverter() {
	}

	
	public BasePageElement doConvert(Object result) {
		Object[] o = (Object[]) result;
		BasePageElement r = new BasePageElement();
		r.setId(Integer.valueOf(o[0].toString()));
		r.setPageDesp(o[1].toString());
		return r;
	}

}
