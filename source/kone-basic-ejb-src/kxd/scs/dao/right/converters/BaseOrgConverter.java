package kxd.scs.dao.right.converters;

import kxd.engine.dao.BaseDaoConverter;
import kxd.remote.scs.beans.BaseOrg;

public class BaseOrgConverter extends BaseDaoConverter<BaseOrg> {

	public BaseOrgConverter() {
	}

	
	public BaseOrg doConvert(Object result) {
		Object[] o = (Object[]) result;
		BaseOrg r = new BaseOrg();
		r.setId(Integer.valueOf(o[0].toString()));
		r.setOrgName(o[1].toString());
		r.setOrgFullName((String) o[2]);
		return r;
	}
}
