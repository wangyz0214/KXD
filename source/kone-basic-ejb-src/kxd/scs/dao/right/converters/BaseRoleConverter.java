package kxd.scs.dao.right.converters;

import kxd.engine.dao.BaseDaoConverter;
import kxd.engine.dao.SqlConverter;
import kxd.engine.dao.SqlParams;
import kxd.remote.scs.beans.BaseRole;

public class BaseRoleConverter extends BaseDaoConverter<BaseRole> implements
		SqlConverter<BaseRole> {

	public BaseRoleConverter() {
	}

	
	public BaseRole doConvert(Object result) {
		Object[] o = (Object[]) result;
		BaseRole r = new BaseRole();
		r.setId(Integer.valueOf(o[0].toString()));
		r.setRoleName(o[1].toString());
		return r;
	}

	private final static String DELETE_SQL = "delete from role where roleid=?1";

	
	public SqlParams getDeleteSql(BaseRole o) {
		return new SqlParams(DELETE_SQL, o.getId());
	}

	private final static String INSEERT_SQL = "insert into role (roleid,"
			+ "roledesp) values(?1,?2)";

	
	public SqlParams getInsertSql(BaseRole o) {
		return new SqlParams(INSEERT_SQL, o.getId(), o.getRoleName());
	}

	private final static String SEQUENCE_STRING = "seq_role";

	
	public String getSequenceString(BaseRole o) {
		return SEQUENCE_STRING;
	}

	private final static String UPDATE_SQL = "update role set "
			+ "roledesp=?1 where roleid=?2";

	
	public SqlParams getUpdateSql(BaseRole o) {
		return new SqlParams(UPDATE_SQL, o.getRoleName(), o.getId());
	}

}
