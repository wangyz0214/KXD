package kxd.scs.dao.appdeploy.converters;

import kxd.engine.dao.BaseDaoConverter;
import kxd.remote.scs.beans.BaseBusiness;
import kxd.remote.scs.beans.BaseTradeCode;

public class BaseTradeCodeConverter extends BaseDaoConverter<BaseTradeCode> {

	public BaseTradeCodeConverter() {
	}

	
	public BaseTradeCode doConvert(Object result) {
		Object[] o = (Object[]) result;
		BaseTradeCode r = new BaseTradeCode();
		r.setId(Integer.valueOf(o[0].toString()));
		r.setTradeCodeDesp(o[1].toString());
		r.setTradeCode(o[2].toString());
		r.setTradeService(o[3].toString());
		r.setBusiness(new BaseBusiness(Integer.valueOf(o[4].toString()),
				(String) o[5]));
		return r;
	}

}
