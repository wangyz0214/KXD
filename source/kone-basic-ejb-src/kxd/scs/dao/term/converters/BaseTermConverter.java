package kxd.scs.dao.term.converters;

import kxd.engine.dao.BaseDaoConverter;
import kxd.remote.scs.beans.BaseTerm;

public class BaseTermConverter extends BaseDaoConverter<BaseTerm> {

	public BaseTermConverter() {
	}

	
	public BaseTerm doConvert(Object result) {
		Object[] o = (Object[]) result;
		BaseTerm r = new BaseTerm();
		r.setId(Integer.valueOf(o[0].toString()));
		r.setTermCode((String) o[1]);
		r.setTermDesp((String) o[2]);
		return r;
	}
}
