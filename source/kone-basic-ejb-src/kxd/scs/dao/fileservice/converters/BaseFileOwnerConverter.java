package kxd.scs.dao.fileservice.converters;

import kxd.engine.dao.BaseDaoConverter;
import kxd.engine.dao.SqlConverter;
import kxd.engine.dao.SqlParams;
import kxd.remote.scs.beans.BaseFileOwner;
import kxd.remote.scs.util.emun.FileVisitRight;

public class BaseFileOwnerConverter extends BaseDaoConverter<BaseFileOwner>
		implements SqlConverter<BaseFileOwner> {

	public BaseFileOwnerConverter() {

	}

	
	public BaseFileOwner doConvert(Object result) {
		Object[] o = (Object[]) result;
		BaseFileOwner r = new BaseFileOwner();
		r.setId(Short.valueOf(o[0].toString()));
		r.setFileOwnerDesp((String) o[1]);
		r.setVisitRight(FileVisitRight.valueOfIntString(o[2]));
		return r;
	}

	private final static String DELETE_SQL = "delete from fileowner where fileownerid=?1";

	
	public SqlParams getDeleteSql(BaseFileOwner o) {
		return new SqlParams(DELETE_SQL, o.getId());
	}

	private final static String INSEERT_SQL = "insert into fileowner (fileownerid,"
			+ "fileownerdesp,visitright) values(?1,?2,?3)";

	
	public SqlParams getInsertSql(BaseFileOwner o) {
		return new SqlParams(INSEERT_SQL, o.getId(), o.getFileOwnerDesp(), o
				.getVisitRight().getValue());
	}

	private final static String SEQUENCE_STRING = "";

	
	public String getSequenceString(BaseFileOwner o) {
		return SEQUENCE_STRING;
	}

	private final static String UPDATE_SQL = "update fileowner set "
			+ "fileownerdesp=?1,visitright=?2 where fileownerid=?3";

	
	public SqlParams getUpdateSql(BaseFileOwner o) {
		return new SqlParams(UPDATE_SQL, o.getFileOwnerDesp(), o
				.getVisitRight().getValue(), o.getId());
	}

}
