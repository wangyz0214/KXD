package kxd.scs.dao.appdeploy.converters;

import kxd.engine.dao.BaseDaoConverter;
import kxd.remote.scs.beans.BasePayItem;

public class BasePayItemConverter extends BaseDaoConverter<BasePayItem> {

	public BasePayItemConverter() {
	}

	
	public BasePayItem doConvert(Object result) {
		Object[] o = (Object[]) result;
		BasePayItem r = new BasePayItem();
		r.setId(Short.valueOf(o[0].toString()));
		r.setPayItemDesp(o[1].toString());
		r.setNeedTrade(Integer.valueOf(o[2].toString()) != 0);
		return r;
	}

}
