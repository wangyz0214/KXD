package kxd.scs.dao.cache.converters;

import kxd.engine.cache.CachedMap;
import kxd.engine.cache.beans.sts.CachedFileHost;

public class CachedFileHostConverter extends
		CachedDaoConverter<Short, CachedFileHost> {

	public CachedFileHostConverter(CachedMap<Short, CachedFileHost> map) {
		super(map);
	}

	public CachedFileHost doConvert(Object result) {
		Object[] o = (Object[]) result;
		CachedFileHost r = new CachedFileHost();
		r.setId(Short.valueOf(o[0].toString()));
		r.setHostDesp((String) o[1]);
		r.setFileRootDir((String) o[2]);
		r.setHttpUrlPrefix((String) o[3]);
		r.setFtpHost((String) o[4]);
		r.setFtpUser((String) o[5]);
		r.setFtpPasswd((String) o[6]);
		r.setRealHttpUrlRoot((String) o[7]);
		return r;
	}
}
