package kxd.scs.dao.cache.converters;

import kxd.engine.cache.CachedMap;
import kxd.engine.cache.beans.sts.CachedFileItem;

public class CachedFileItemConverter extends
		CachedDaoConverter<String, CachedFileItem> {

	public CachedFileItemConverter(CachedMap<String, CachedFileItem> map) {
		super(map);
	}

	public CachedFileItem doConvert(Object result) {
		Object[] o = (Object[]) result;
		CachedFileItem r = new CachedFileItem();
		r.setId((String) o[0]);
		r.setFileCategoryId(Short.valueOf(o[1].toString()));
		r.setFileHostId(Short.valueOf(o[2].toString()));
		r.setFileOwnerId(Short.valueOf(o[3].toString()));
		r.setSavePath((String) o[4]);
		r.setOriginalFileName((String) o[5]);
		r.setMd5((String) o[6]);
		return r;
	}
}
