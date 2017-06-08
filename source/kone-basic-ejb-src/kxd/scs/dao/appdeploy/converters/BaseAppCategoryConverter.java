package kxd.scs.dao.appdeploy.converters;

import kxd.engine.dao.BaseDaoConverter;
import kxd.remote.scs.beans.BaseAppCategory;

public class BaseAppCategoryConverter extends BaseDaoConverter<BaseAppCategory> {

	public BaseAppCategoryConverter() {
	}

	
	public BaseAppCategory doConvert(Object result) {
		Object[] o = (Object[]) result;
		BaseAppCategory r = new BaseAppCategory();
		r.setId(Integer.valueOf(o[0].toString()));
		r.setAppCategoryDesp(o[1].toString());
		return r;
	}

}
