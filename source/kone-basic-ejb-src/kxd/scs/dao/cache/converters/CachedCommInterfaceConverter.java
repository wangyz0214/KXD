package kxd.scs.dao.cache.converters;

import kxd.engine.cache.CachedMap;
import kxd.engine.cache.beans.sts.CachedCommInterface;

public class CachedCommInterfaceConverter extends
		CachedDaoConverter<Short, CachedCommInterface> {

	public CachedCommInterfaceConverter(
			CachedMap<Short, CachedCommInterface> map) {
		super(map);
	}

	public CachedCommInterface doConvert(Object result) {
		Object[] o = (Object[]) result;
		CachedCommInterface r = new CachedCommInterface();
		r.setId(Short.valueOf(o[0].toString()));
		r.setDesp(o[1].toString());
		r.setType(Integer.valueOf(o[2].toString()));
		return r;
	}

}
