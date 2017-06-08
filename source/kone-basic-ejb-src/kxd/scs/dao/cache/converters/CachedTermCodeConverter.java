package kxd.scs.dao.cache.converters;

import kxd.engine.cache.CachedMap;
import kxd.engine.cache.beans.sts.CachedTermByCode;

public class CachedTermCodeConverter extends
		CachedDaoConverter<String, CachedTermByCode> {

	public CachedTermCodeConverter(CachedMap<String, CachedTermByCode> map) {
		super(map);
	}

	protected CachedTermByCode doConvert(Object result) {
		Object[] o = (Object[]) result;
		CachedTermByCode c = new CachedTermByCode();
		c.setId(o[0].toString());
		c.setTermId(Integer.valueOf(o[1].toString()));
		return c;
	}

}
