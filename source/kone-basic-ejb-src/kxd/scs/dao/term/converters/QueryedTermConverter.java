package kxd.scs.dao.term.converters;

import kxd.engine.dao.BaseDaoConverter;
import kxd.remote.scs.beans.BaseApp;
import kxd.remote.scs.beans.BaseBankTerm;
import kxd.remote.scs.beans.BaseOrg;
import kxd.remote.scs.beans.BaseTermType;
import kxd.remote.scs.beans.device.QueryedTerm;
import kxd.remote.scs.util.emun.AlarmStatus;
import kxd.remote.scs.util.emun.SettlementType;
import kxd.remote.scs.util.emun.TermStatus;

public class QueryedTermConverter extends BaseDaoConverter<QueryedTerm> {

	public QueryedTermConverter() {
	}

	public QueryedTerm doConvert(Object result) {
		Object[] o = (Object[]) result;
		QueryedTerm r = new QueryedTerm();
		r.setId(Integer.valueOf(o[0].toString()));
		r.setTermCode((String) o[1]);
		r.setTermDesp((String) o[2]);
		r.setTermType(new BaseTermType(Integer.valueOf(o[3].toString()),
				(String) o[4]));
		r.setApp(new BaseApp(Integer.valueOf(o[5].toString()), (String) o[6]));
		r.setOrg(new BaseOrg(Integer.valueOf(o[7].toString()), (String) o[8]));
		r.getOrg().setOrgFullName(r.getOrg().getOrgName());
		if (o[9] == null)
			r.setBankTerm(new BaseBankTerm());
		else {
			r.setBankTerm(new BaseBankTerm(Integer.valueOf(o[9].toString())));
		}
		r.setStatus(TermStatus.valueOfIntString(o[10]));
		r.setAlarmStatus(AlarmStatus.valueOfIntString(o[11]));
		r.setSettlementType(SettlementType.valueOfIntString(o[12]));
		return r;
	}
}
