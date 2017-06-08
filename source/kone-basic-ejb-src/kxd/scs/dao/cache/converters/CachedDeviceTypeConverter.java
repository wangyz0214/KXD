package kxd.scs.dao.cache.converters;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import kxd.engine.cache.CachedMap;
import kxd.engine.cache.beans.sts.CachedAlarmCode;
import kxd.engine.cache.beans.sts.CachedDeviceType;
import kxd.remote.scs.util.emun.FaultPromptOption;

public class CachedDeviceTypeConverter extends
		CachedDaoConverter<Integer, CachedDeviceType> {

	public CachedDeviceTypeConverter(CachedMap<Integer, CachedDeviceType> map) {
		super(map);
	}

	
	public CachedDeviceType doConvert(Object result) {
		Object[] o = (Object[]) result;
		CachedDeviceType r = new CachedDeviceType();
		r.setId(Integer.valueOf(o[0].toString()));
		r.setAlarmNotifyOption(Short.valueOf(o[1].toString()));
		r.setAlarmSendForm(Integer.valueOf(o[2].toString()) != 0);
		r.setDeviceTypeCode((String) o[3]);
		r.setDeviceTypeDesp((String) o[4]);
		r.setDeviceTypeDriverId(Integer.valueOf(o[5].toString()));
		r.setFaultNotifyOption(Short.valueOf(o[6].toString()));
		r.setFaultPromptOption(FaultPromptOption.valueOfIntString(o[7]));
		r.setFaultSendForm(Integer.valueOf(o[8].toString()) != 0);
		return r;
	}

	
	public List<CachedDeviceType> convert(List<?> results) {
		List<CachedDeviceType> ls = new ArrayList<CachedDeviceType>();
		Iterator<?> it = results.iterator();
		CachedDeviceType curDeviceType = null;
		while (it.hasNext()) {
			Object[] o = (Object[]) it.next();
			if (curDeviceType == null) {
				curDeviceType = doConvert(o);
			} else {
				CachedDeviceType term = doConvert(o);
				if (!term.getId().equals(curDeviceType.getId())) {
					ls.add(curDeviceType);
					curDeviceType = term;
				}
			}
			if (o[9] != null) { // 告警代码信息
				CachedAlarmCode st = new CachedAlarmCode(Integer.valueOf(o[9]
						.toString()));
				st.setAlarmCategoryId(Integer.valueOf(o[10].toString()));
				st.setAlarmDesp((String) o[11]);
				curDeviceType.getAlarmCodes().put(st.getId(), st);
			}
		}
		if (curDeviceType != null) {
			ls.add(curDeviceType);
		}
		//map.putCacheAll(ls);
		return ls;
	}
}
