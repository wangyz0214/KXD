package kxd.scs.dao.right.converters;

import kxd.engine.dao.BaseDaoConverter;
import kxd.remote.scs.beans.BaseOrg;
import kxd.remote.scs.beans.right.QueryedOrg;

public class QueryedOrgConverter extends BaseDaoConverter<QueryedOrg> {

	public QueryedOrgConverter() {
	}

	
	public QueryedOrg doConvert(Object result) {
		Object[] o = (Object[]) result;
		QueryedOrg r = new QueryedOrg();
		r.setId(Integer.valueOf(o[0].toString()));
		r.setOrgName(o[1].toString());
		r.setOrgFullName((String) o[2]);
		r.setDepth(Integer.valueOf(o[3].toString()));
		r.setIdent(Integer.valueOf(o[4].toString()));
		r.setParentOrg(new BaseOrg(Integer.valueOf(o[5].toString())));
		r.setHasChildren(o[6] != null);
		return r;
	}
}
