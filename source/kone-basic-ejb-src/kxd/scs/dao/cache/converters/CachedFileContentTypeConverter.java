package kxd.scs.dao.cache.converters;

import kxd.engine.cache.CachedMap;
import kxd.engine.cache.beans.sts.CachedFileContentType;

public class CachedFileContentTypeConverter extends
		CachedDaoConverter<String, CachedFileContentType> {

	public CachedFileContentTypeConverter(
			CachedMap<String, CachedFileContentType> map) {
		super(map);
	}

	
	public CachedFileContentType doConvert(Object result) {
		Object[] o = (Object[]) result;
		CachedFileContentType r = new CachedFileContentType();
		r.setId((String) o[0]);
		r.setContentType((String) o[1]);
		return r;
	}
}
