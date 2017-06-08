package kxd.scs.dao;

import kxd.engine.dao.BaseDaoConverter;

public class StringConverter extends BaseDaoConverter<String> {

	public StringConverter() {
	}

	
	public String doConvert(Object result) {
		return (String) result;
	}
}
