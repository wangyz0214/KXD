package kxd.scs.dao.system.converters;

import kxd.engine.dao.BaseDaoConverter;
import kxd.engine.dao.SqlConverter;
import kxd.engine.dao.SqlParams;
import kxd.remote.scs.beans.BaseDisabledPrintUser;

public class BaseDisabledPrintUserConverter extends
		BaseDaoConverter<BaseDisabledPrintUser> implements
		SqlConverter<BaseDisabledPrintUser> {

	public BaseDisabledPrintUserConverter() {

	}

	
	public BaseDisabledPrintUser doConvert(Object result) {
		Object[] o = (Object[]) result;
		BaseDisabledPrintUser r = new BaseDisabledPrintUser();
		r.setId(Long.valueOf(o[0].toString()));
		r.setUserno((String) o[1]);
		return r;
	}

	private final static String DELETE_SQL = "delete from disableprintuser where userid=?1";

	
	public SqlParams getDeleteSql(BaseDisabledPrintUser o) {
		return new SqlParams(DELETE_SQL, o.getId());
	}

	private final static String INSEERT_SQL = "insert into disableprintuser(userid,"
			+ "userno) values(?1,?2)";

	
	public SqlParams getInsertSql(BaseDisabledPrintUser o) {
		return new SqlParams(INSEERT_SQL, o.getId(), o.getUserno());
	}

	private final static String SEQUENCE_STRING = "seq_disableprintuser";

	
	public String getSequenceString(BaseDisabledPrintUser o) {
		return SEQUENCE_STRING;
	}

	private final static String UPDATE_SQL = "update disableprintuser set "
			+ "userno=?1 where userid=?2";

	
	public SqlParams getUpdateSql(BaseDisabledPrintUser o) {
		return new SqlParams(UPDATE_SQL, o.getUserno(), o.getId());
	}

}
