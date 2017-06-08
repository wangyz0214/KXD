package kxd.scs.dao.cache.converters;

import kxd.engine.cache.CachedMap;
import kxd.engine.cache.beans.sts.CachedOrgCode;

public class CachedOrgCodeConverter extends
		CachedDaoConverter<String, CachedOrgCode> {

	public CachedOrgCodeConverter(CachedMap<String, CachedOrgCode> map) {
		super(map);
	}

	
	protected CachedOrgCode doConvert(Object result) {
		Object[] o = (Object[]) result;
		CachedOrgCode c = new CachedOrgCode();
		c.setId(o[0].toString());
		c.setOrgId(Integer.valueOf(o[1].toString()));
		return c;
	}

}
