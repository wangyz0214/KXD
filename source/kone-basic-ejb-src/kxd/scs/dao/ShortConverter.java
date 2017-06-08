package kxd.scs.dao;

import kxd.engine.dao.BaseDaoConverter;

public class ShortConverter extends BaseDaoConverter<Short> {

	public ShortConverter() {
	}

	
	public Short doConvert(Object result) {
		return Short.valueOf(result.toString());
	}
}
