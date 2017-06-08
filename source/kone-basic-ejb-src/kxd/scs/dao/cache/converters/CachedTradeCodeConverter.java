package kxd.scs.dao.cache.converters;

import java.util.List;

import kxd.engine.cache.CachedMap;
import kxd.engine.cache.beans.sts.CachedTradeCode;

public class CachedTradeCodeConverter extends
		CachedDaoConverter<Integer, CachedTradeCode> {

	public CachedTradeCodeConverter(CachedMap<Integer, CachedTradeCode> map) {
		super(map);
	}

	public CachedTradeCode convert(Object result) {
		CachedTradeCode o = super.convert(result);
		return o;
	}

	public CachedTradeCode doConvert(Object result) {
		Object[] o = (Object[]) result;
		CachedTradeCode r = new CachedTradeCode();
		r.setId(Integer.valueOf(o[0].toString()));
		r.setTradeCode(o[1].toString());
		r.setService((String) o[2]);
		r.setStated(Integer.valueOf(o[3].toString()) != 0);
		r.setLogged(Integer.valueOf(o[4].toString()) != 0);
		if (o[5] == null)
			r.setStrikeTradeCodeId(null);
		else
			r.setStrikeTradeCodeId(Integer.valueOf(o[5].toString()));
		r.setTradeDesp((String) o[6]);
		if (o[7] == null)
			r.setPayWayId(null);
		else
			r.setPayWayId(Short.valueOf(o[7].toString()));
		if (o[8] == null)
			r.setPayItemId(null);
		else
			r.setPayItemId(Short.valueOf(o[8].toString()));
		r.setRefundMode(Integer.valueOf(o[9].toString()));
		r.setRedoEnabled(Integer.valueOf(o[10].toString()) != 0);
		r.setCancelRefundMode(Integer.valueOf(o[11].toString()));
		r.setBusinessId(Integer.valueOf(o[12].toString()));
		return r;
	}

	public List<CachedTradeCode> convert(List<?> results) {
		List<CachedTradeCode> ls = super.convert(results);
		return ls;
	}
}
