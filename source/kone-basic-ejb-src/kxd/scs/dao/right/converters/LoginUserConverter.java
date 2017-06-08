package kxd.scs.dao.right.converters;

import kxd.engine.dao.BaseDaoConverter;
import kxd.remote.scs.beans.BaseManuf;
import kxd.remote.scs.beans.BaseOrg;
import kxd.remote.scs.beans.BaseRole;
import kxd.remote.scs.beans.right.LoginUser;
import kxd.remote.scs.util.emun.ManageScope;
import kxd.remote.scs.util.emun.UserGroup;

public class LoginUserConverter extends BaseDaoConverter<LoginUser> {

	public LoginUserConverter() {
	}

	
	public LoginUser doConvert(Object result) {
		Object[] o = (Object[]) result;
		LoginUser r = new LoginUser();
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
		r.setUserPwd((String) o[9]);
		if (o[10] == null)
			r.setRole(new BaseRole());
		else
			r.setRole(new BaseRole(Integer.valueOf(o[10].toString())));
		return r;
	}
}
