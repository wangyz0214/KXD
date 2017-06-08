package kxd.scs.dao.cache.converters;

import kxd.engine.cache.CachedMap;
import kxd.engine.cache.beans.sts.CachedBankTerm;
import kxd.util.DateTime;

public class CachedBankTermConverter extends
		CachedDaoConverter<Integer, CachedBankTerm> {

	public CachedBankTermConverter(CachedMap<Integer, CachedBankTerm> map) {
		super(map);
	}

	
	public CachedBankTerm doConvert(Object result) {
		Object[] o = (Object[]) result;
		CachedBankTerm r = new CachedBankTerm();
		r.setId(Integer.valueOf(o[0].toString()));
		r.setBankTermDesp(o[1].toString());
		r.setBankTermCode((String) o[2]);
		r.setBatch((String) o[3]);
		r.setExtField((String) o[4]);
		r.setMacKey((String) o[5]);
		r.setWorkKey((String) o[6]);
		r.setMerchantAccount((String) o[7]);
		r.setSigninTime(DateTime.parseDate((String) o[8], "yyyyMMddHHmmss"));
		return r;
	}

}
