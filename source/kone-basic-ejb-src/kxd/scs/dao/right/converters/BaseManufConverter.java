package kxd.scs.dao.right.converters;

import kxd.engine.dao.BaseDaoConverter;
import kxd.remote.scs.beans.BaseManuf;

public class BaseManufConverter extends BaseDaoConverter<BaseManuf> {

	public BaseManufConverter() {
	}

	
	public BaseManuf doConvert(Object result) {
		Object[] o = (Object[]) result;
		BaseManuf r = new BaseManuf();
		r.setId(Integer.valueOf(o[0].toString()));
		r.setManufName(o[1].toString());
		return r;
	}
}
