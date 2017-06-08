package kxd.scs.dao.right.converters;

import kxd.engine.dao.BaseDaoConverter;
import kxd.remote.scs.beans.right.EditedFunction;
import kxd.remote.scs.util.emun.UserGroup;

public class EditedFunctionConverter extends BaseDaoConverter<EditedFunction> {

	public EditedFunctionConverter() {
	}

	
	public EditedFunction doConvert(Object result) {
		Object[] o = (Object[]) result;
		EditedFunction r = new EditedFunction();
		r.setId(Integer.valueOf(o[0].toString()));
		r.setFuncDesp(o[1].toString());
		r.setFuncIcon((String) o[2]);
		r.setFuncUrl((String) o[3]);
		r.setFuncDepth(Short.valueOf(o[4].toString()));
		r.setUserGroup(UserGroup.valueOfIntString(o[5]));
		r.setCustomEnabled(Integer.valueOf(o[6].toString()) != 0);
		return r;
	}
}
