package kxd.scs.dao.cache.converters;

import kxd.engine.cache.CachedMap;
import kxd.engine.cache.beans.sts.CachedFileOwner;
import kxd.remote.scs.util.emun.FileVisitRight;

public class CachedFileOwnerConverter extends
		CachedDaoConverter<Short, CachedFileOwner> {

	public CachedFileOwnerConverter(CachedMap<Short, CachedFileOwner> map) {
		super(map);
	}

	
	public CachedFileOwner doConvert(Object result) {
		Object[] o = (Object[]) result;
		CachedFileOwner r = new CachedFileOwner();
		r.setId(Short.valueOf(o[0].toString()));
		r.setFileOwnerDesp((String) o[1]);
		r.setVisitRight(FileVisitRight.valueOfIntString(o[2]));
		return r;
	}
}
