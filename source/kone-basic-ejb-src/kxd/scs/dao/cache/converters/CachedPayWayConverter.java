package kxd.scs.dao.cache.converters;

import kxd.engine.cache.CachedMap;
import kxd.engine.cache.beans.sts.CachedPayWay;
import kxd.remote.scs.util.emun.PayWayType;

public class CachedPayWayConverter extends
		CachedDaoConverter<Short, CachedPayWay> {

	public CachedPayWayConverter(CachedMap<Short, CachedPayWay> map) {
		super(map);
	}

	public CachedPayWay doConvert(Object result) {
		Object[] o = (Object[]) result;
		CachedPayWay r = new CachedPayWay();
		r.setId(Short.valueOf(o[0].toString()));
		r.setPayWayDesp(o[1].toString());
		r.setNeedTrade(Integer.valueOf(o[2].toString()) != 0);
		r.setType(PayWayType.valueOfIntString(o[3]));
		return r;
	}
}
