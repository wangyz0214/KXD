package kxd.scs.dao.cache.converters;

import kxd.engine.cache.CachedMap;
import kxd.engine.cache.beans.sts.CachedPrintType;

public class CachedPrintTypeConverter extends
		CachedDaoConverter<Short, CachedPrintType> {

	public CachedPrintTypeConverter(CachedMap<Short, CachedPrintType> map) {
		super(map);
	}

	
	public CachedPrintType doConvert(Object result) {
		Object[] o = (Object[]) result;
		CachedPrintType r = new CachedPrintType();
		r.setId(Short.valueOf(o[0].toString()));
		r.setPrintTypeDesp(o[1].toString());
		return r;
	}
}
