package kxd.scs.dao.appdeploy.converters;

import kxd.engine.dao.BaseDaoConverter;
import kxd.remote.scs.beans.BaseBusiness;
import kxd.remote.scs.beans.BaseBusinessCategory;

public class BaseBusinessConverter extends BaseDaoConverter<BaseBusiness> {

	public BaseBusinessConverter() {
	}

	
	public BaseBusiness doConvert(Object result) {
		Object[] o = (Object[]) result;
		BaseBusiness r = new BaseBusiness();
		r.setId(Integer.valueOf(o[0].toString()));
		r.setBusinessDesp(o[1].toString());
		r.setBusinessCategory(new BaseBusinessCategory(Integer.valueOf(o[2]
				.toString()), (String) o[3]));
		return r;
	}

}
