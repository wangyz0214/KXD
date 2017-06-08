package kxd.scs.dao.term.converters;

import kxd.engine.dao.BaseDaoConverter;
import kxd.remote.scs.beans.BaseManuf;
import kxd.remote.scs.beans.BaseTermType;

public class BaseTermTypeConverter extends BaseDaoConverter<BaseTermType> {

	public BaseTermTypeConverter() {
	}

	
	public BaseTermType doConvert(Object result) {
		Object[] o = (Object[]) result;
		BaseTermType r = new BaseTermType();
		r.setId(Integer.valueOf(o[0].toString()));
		r.setTypeDesp((String) o[1]);
		r.setManuf(new BaseManuf(Integer.valueOf(o[2].toString()),
				(String) o[3]));
		return r;
	}
}
