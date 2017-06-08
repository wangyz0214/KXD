package kxd.scs.dao;

import kxd.engine.dao.BaseDaoConverter;

public class LongConverter extends BaseDaoConverter<Long> {

	public LongConverter() {
	}

	
	public Long doConvert(Object result) {
		return Long.valueOf(result.toString());
	}
}
