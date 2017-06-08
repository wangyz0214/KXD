package kxd.scs.dao;

import kxd.engine.dao.BaseDaoConverter;

public class IntegerConverter extends BaseDaoConverter<Integer> {

	public IntegerConverter() {
	}

	
	public Integer doConvert(Object result) {
		return Integer.valueOf(result.toString());
	}
}
