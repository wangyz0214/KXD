package kxd.scs.dao.cache.converters;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import kxd.engine.cache.CachedMap;
import kxd.engine.cache.beans.sts.CachedTermType;
import kxd.engine.cache.beans.sts.CachedTermTypeDevice;
import kxd.remote.scs.util.emun.CashFlag;
import kxd.remote.scs.util.emun.FixType;

public class CachedTermTypeConverter extends
		CachedDaoConverter<Integer, CachedTermType> {

	public CachedTermTypeConverter(CachedMap<Integer, CachedTermType> map) {
		super(map);
	}

	
	public CachedTermType doConvert(Object result) {
		Object[] o = (Object[]) result;
		CachedTermType r = new CachedTermType();
		r.setId(Integer.valueOf(o[0].toString()));
		r.setTypeCode(o[1].toString());
		r.setTypeDesp(o[2].toString());
		r.setFixType(FixType.valueOfIntString(o[3]));
		r.setCashFlag(CashFlag.valueOfIntString(o[4]));
		r.setAppId(Integer.valueOf(o[5].toString()));
		r.setManufId(Integer.valueOf(o[6].toString()));
		return r;
	}

	
	public List<CachedTermType> convert(List<?> results) {
		List<CachedTermType> ls = new ArrayList<CachedTermType>();
		Iterator<?> it = results.iterator();
		CachedTermType curItem = null;
		while (it.hasNext()) {
			Object[] o = (Object[]) it.next();
			if (curItem == null) {
				curItem = doConvert(o);
			} else {
				CachedTermType term = doConvert(o);
				if (!term.getId().equals(curItem.getId())) {
					ls.add(curItem);
					curItem = term;
				}
			}
			if (o[7] != null) { // 
				CachedTermTypeDevice st = new CachedTermTypeDevice(Integer
						.valueOf(o[7].toString()));
				st.setPort(Integer.valueOf(o[8].toString()));
				st.setExtConfig((String) o[9]);
				curItem.getDeviceMap().put(st.getId(), st);
			}
		}
		if (curItem != null) {
			ls.add(curItem);
		}
		//map.putCacheAll(ls);
		return ls;
	}
}
