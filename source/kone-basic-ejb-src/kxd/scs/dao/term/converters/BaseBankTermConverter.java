package kxd.scs.dao.term.converters;

import kxd.engine.dao.BaseDaoConverter;
import kxd.remote.scs.beans.BaseBankTerm;

public class BaseBankTermConverter extends BaseDaoConverter<BaseBankTerm> {

	public BaseBankTermConverter() {
	}

	
	public BaseBankTerm doConvert(Object result) {
		Object[] o = (Object[]) result;
		BaseBankTerm r = new BaseBankTerm();
		r.setId(Integer.valueOf(o[0].toString()));
		r.setBankTermCode((String) o[1]);
		r.setBankTermDesp((String) o[2]);
		r.setMerchantAccount((String) o[3]);
		return r;
	}
}
