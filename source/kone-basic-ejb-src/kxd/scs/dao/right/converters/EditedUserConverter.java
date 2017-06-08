package kxd.scs.dao.right.converters;

import kxd.engine.dao.BaseDaoConverter;
import kxd.engine.dao.SqlConverter;
import kxd.engine.dao.SqlParams;
import kxd.remote.scs.beans.BaseManuf;
import kxd.remote.scs.beans.BaseOrg;
import kxd.remote.scs.beans.BaseRole;
import kxd.remote.scs.beans.right.EditedUser;
import kxd.remote.scs.util.AppException;
import kxd.remote.scs.util.emun.ManageScope;
import kxd.remote.scs.util.emun.UserGroup;

public class EditedUserConverter extends BaseDaoConverter<EditedUser> implements
		SqlConverter<EditedUser> {

	public EditedUserConverter() {
	}

	
	public EditedUser doConvert(Object result) {
		Object[] o = (Object[]) result;
		EditedUser r = new EditedUser();
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
		else {
			r.setOrg(new BaseOrg(Integer.valueOf(o[5].toString()),
					(String) o[6]));
			r.getOrg().setOrgFullName((String) o[6]);
		}
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
		return r;
	}

	private final static String DELETE_SQL = "delete from systemuser where userid=?1";

	
	public SqlParams getDeleteSql(EditedUser o) {
		return new SqlParams(DELETE_SQL, o.getId());
	}

	private final static String INSEERT_SQL = "insert into systemuser (userid,"
			+ "usercode,username,userpwd,usergroup,telphone,mobile,email"
			+ ",rightscope,orgid,manufid,roleid) values(?1,?2,"
			+ "?3,?4,?5,?6,?7,?8,?9,?10,?11,?12)";

	private void check(EditedUser o) {
		if (o.getUserGroup().isManager()) {
			if (o.getManuf().getId() != null)
				throw new AppException("厂商用户的权限必须是操作员或角色用户");
		}
		if (o.getUserGroup().isOperator()) {
			if (o.getRole().getId() != null)
				throw new AppException("只有角色用户才能指定角色");
		}
		if (o.getRole().getId() == null) {
			if (o.getUserGroup().isCustomer())
				throw new AppException("角色用户必须指定角色");
		}
	}

	
	public SqlParams getInsertSql(EditedUser o) {
		check(o);
		return new SqlParams(INSEERT_SQL, o.getId(), o.getUserCode(), o
				.getUserName(), o.getUserPwd(), o.getUserGroup().getValue(), o
				.getTelphone(), o.getMobile(), o.getEmail(), o.getManageScope()
				.getValue(), o.getOrg().getId(), o.getManuf().getId(), o
				.getRole().getId());
	}

	private final static String SEQUENCE_STRING = "seq_user";

	
	public String getSequenceString(EditedUser o) {
		return SEQUENCE_STRING;
	}

	private final static String UPDATE_SQL = "update systemuser set "
			+ "usercode=?1,username=?2,userpwd=?3,usergroup=?4,telphone=?5,mobile=?6,email=?7"
			+ ",manufid=?8,roleid=?9 where userid=?10";
	private final static String UPDATE_SQL1 = "update systemuser set "
			+ "usercode=?1,username=?2,usergroup=?3,telphone=?4,mobile=?5,email=?6"
			+ ",manufid=?7,roleid=?8 where userid=?9";

	
	public SqlParams getUpdateSql(EditedUser o) {
		check(o);
		if (o.getUserPwd() != null && o.getUserPwd().length() > 0)
			return new SqlParams(UPDATE_SQL, o.getUserCode(), o.getUserName(),
					o.getUserPwd(), o.getUserGroup().getValue(), o
							.getTelphone(), o.getMobile(), o.getEmail(), o
							.getManuf().getId(), o.getRole().getId(), o.getId());
		else
			return new SqlParams(UPDATE_SQL1, o.getUserCode(), o.getUserName(),
					o.getUserGroup().getValue(), o.getTelphone(),
					o.getMobile(), o.getEmail(), o.getManuf().getId(), o
							.getRole().getId(), o.getId());
	}

}
