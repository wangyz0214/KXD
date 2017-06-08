package kxd.scs.dao.right.converters;

import kxd.engine.dao.BaseDaoConverter;
import kxd.remote.scs.beans.BaseManuf;
import kxd.remote.scs.beans.BaseOrg;
import kxd.remote.scs.beans.BaseRole;
import kxd.remote.scs.beans.right.QueryedUser;
import kxd.remote.scs.util.emun.ManageScope;
import kxd.remote.scs.util.emun.UserGroup;
import kxd.util.DateTime;

public class QueryedUserConverter extends BaseDaoConverter<QueryedUser> {

	public QueryedUserConverter() {
	}

	
	public QueryedUser doConvert(Object result) {
		Object[] o = (Object[]) result;
		QueryedUser r = new QueryedUser();
		r.setId(Long.valueOf(o[0].toString()));
		r.setUserName(o[1].toString());
		r.setUserGroup(UserGroup.valueOfIntString(o[2]));
		r.setUserCode((String) o[3]);
		if (o[4] != null)
			r.setManageScope(ManageScope.valueOfIntString(o[4]));
		else
			r.setManageScope(ManageScope.AREA);
		if (o[5] == null)
			r.setOrg(new BaseOrg());
		else
			r.setOrg(new BaseOrg(Integer.valueOf(o[5].toString()),
					(String) o[6]));
		if (o[7] == null)
			r.setManuf(new BaseManuf());
		else
			r.setManuf(new BaseManuf(Integer.valueOf(o[7].toString()),
					(String) o[8]));
		if (o[9] == null)
			r.setRole(new BaseRole());
		else
			r.setRole(new BaseRole(Integer.valueOf(o[9].toString()),
					(String) o[10]));
		r.setTelphone((String) o[11]);
		r.setEmail((String) o[12]);
		r.setMobile((String) o[13]);
		try {
			r.setRegTime(new DateTime((String) o[14], "yyyy-MM-dd HH:mm:ss")
					.getTime());
			r.setLastInlineTime(new DateTime((String) o[15],
					"yyyy-MM-dd HH:mm:ss").getTime());
		} catch (Throwable e) {
		}
		r.setLoginCount(Long.valueOf(o[16].toString()));
		r.setHeadImage((String) o[17]);
		return r;
	}

}
