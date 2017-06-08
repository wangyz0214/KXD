package kxd.scs.dao.cache.converters;

import kxd.engine.cache.CachedMap;
import kxd.engine.cache.beans.sts.CachedFileUser;

public class CachedFileUserConverter extends
		CachedDaoConverter<String, CachedFileUser> {

	public CachedFileUserConverter(CachedMap<String, CachedFileUser> map) {
		super(map);
	}

	
	public CachedFileUser doConvert(Object result) {
		Object[] o = (Object[]) result;
		CachedFileUser r = new CachedFileUser();
		r.setId((String) o[0]);
		r.setFileOwnerId(Short.valueOf(o[1].toString()));
		r.setFileUserPwd((String) o[2]);
		return r;
	}
}
