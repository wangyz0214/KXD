package kxd.scs.dao.cache.converters;

import kxd.engine.cache.CachedMap;
import kxd.engine.cache.beans.sts.CachedAlarmCategory;
import kxd.remote.scs.util.emun.AlarmLevel;

public class CachedAlarmCategoryConverter extends
		CachedDaoConverter<Integer, CachedAlarmCategory> {

	public CachedAlarmCategoryConverter(
			CachedMap<Integer, CachedAlarmCategory> map) {
		super(map);
	}

	
	public CachedAlarmCategory doConvert(Object result) {
		Object[] o = (Object[]) result;
		CachedAlarmCategory r = new CachedAlarmCategory();
		r.setId(Integer.valueOf(o[0].toString()));
		r.setAlarmCategoryDesp(o[1].toString());
		r.setAlarmLevel(AlarmLevel.valueOfIntString(o[2]));
		return r;
	}
}
