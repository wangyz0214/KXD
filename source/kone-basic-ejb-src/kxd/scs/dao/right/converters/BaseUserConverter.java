package kxd.scs.dao.right.converters;

import kxd.engine.dao.BaseDaoConverter;
import kxd.remote.scs.beans.BaseUser;
import kxd.remote.scs.util.emun.UserGroup;

public class BaseUserConverter extends BaseDaoConverter<BaseUser> {

	public BaseUserConverter() {
	}

	
	public BaseUser doConvert(Object result) {
		Object[] o = (Object[]) result;
		BaseUser r = new BaseUser();
		r.setId(Long.valueOf(o[0].toString()));
		r.setUserName(o[1].toString());
		r.setUserGroup(UserGroup.valueOfIntString(o[2]));
		return r;
	}
}
