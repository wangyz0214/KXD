package kxd.scs.dao.fileservice.converters;

import kxd.engine.dao.BaseDaoConverter;
import kxd.engine.dao.SqlConverter;
import kxd.engine.dao.SqlParams;
import kxd.remote.scs.beans.BaseFileOwner;
import kxd.remote.scs.beans.BaseFileUser;

public class BaseFileUserConverter extends BaseDaoConverter<BaseFileUser>
		implements SqlConverter<BaseFileUser> {

	public BaseFileUserConverter() {

	}

	
	public BaseFileUser doConvert(Object result) {
		Object[] o = (Object[]) result;
		BaseFileUser r = new BaseFileUser();
		r.setId((String) o[0]);
		r.setFileOwner(new BaseFileOwner(Short.valueOf(o[1].toString())));
		r.getFileOwner().setFileOwnerDesp((String) o[2]);
		r.setFileUserPwd((String) o[3]);
		return r;
	}

	private final static String DELETE_SQL = "delete from fileuser where fileusercode=?1";

	
	public SqlParams getDeleteSql(BaseFileUser o) {
		return new SqlParams(DELETE_SQL, o.getId());
	}

	private final static String INSEERT_SQL = "insert into fileuser (fileusercode,"
			+ "fileuserpwd,fileownerid) values(?1,?2,?3)";

	
	public SqlParams getInsertSql(BaseFileUser o) {
		return new SqlParams(INSEERT_SQL, o.getId(), o.getFileUserPwd(), o
				.getFileOwner().getId());
	}

	private final static String SEQUENCE_STRING = "";

	
	public String getSequenceString(BaseFileUser o) {
		return SEQUENCE_STRING;
	}

	private final static String UPDATE_SQL = "update fileuser set "
			+ "fileuserpwd=?1,fileownerid=?2 where fileusercode=?3";

	
	public SqlParams getUpdateSql(BaseFileUser o) {
		return new SqlParams(UPDATE_SQL, o.getFileUserPwd(), o.getFileOwner()
				.getId(), o.getId());
	}

}
