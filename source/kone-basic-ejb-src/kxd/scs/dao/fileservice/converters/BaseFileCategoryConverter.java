package kxd.scs.dao.fileservice.converters;

import kxd.engine.dao.BaseDaoConverter;
import kxd.engine.dao.SqlConverter;
import kxd.engine.dao.SqlParams;
import kxd.remote.scs.beans.BaseFileCategory;
import kxd.remote.scs.util.emun.FileCachedType;

public class BaseFileCategoryConverter extends
		BaseDaoConverter<BaseFileCategory> implements
		SqlConverter<BaseFileCategory> {

	public BaseFileCategoryConverter() {

	}

	public BaseFileCategory doConvert(Object result) {
		Object[] o = (Object[]) result;
		BaseFileCategory r = new BaseFileCategory();
		r.setId(Short.valueOf(o[0].toString()));
		r.setFileCategoryDesp((String) o[1]);
		r.setCachedType(FileCachedType.valueOfIntString(o[2]));
		r.setFileHost(Short.valueOf(o[3].toString()));
		return r;
	}

	private final static String DELETE_SQL = "delete from filecategory where categoryid=?1";

	public SqlParams getDeleteSql(BaseFileCategory o) {
		return new SqlParams(DELETE_SQL, o.getId());
	}

	private final static String INSEERT_SQL = "insert into filecategory (categoryid,"
			+ "categorydesp,cachedtype,filehost) values(?1,?2,?3,?4)";

	public SqlParams getInsertSql(BaseFileCategory o) {
		return new SqlParams(INSEERT_SQL, o.getId(), o.getFileCategoryDesp(), o
				.getCachedType().getValue(), o.getFileHost());
	}

	private final static String SEQUENCE_STRING = "";

	public String getSequenceString(BaseFileCategory o) {
		return SEQUENCE_STRING;
	}

	private final static String UPDATE_SQL = "update filecategory set "
			+ "categorydesp=?1,cachedtype=?2,filehost=?3 where categoryid=?4";

	public SqlParams getUpdateSql(BaseFileCategory o) {
		return new SqlParams(UPDATE_SQL, o.getFileCategoryDesp(), o
				.getCachedType().getValue(), o.getFileHost(), o.getId());
	}

}
