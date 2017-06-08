package kxd.scs.dao.publish.converters;

import kxd.engine.dao.BaseDaoConverter;
import kxd.engine.dao.SqlConverter;
import kxd.engine.dao.SqlParams;
import kxd.remote.scs.beans.BaseInfoCategory;

public class BaseInfoCategoryConverter extends
		BaseDaoConverter<BaseInfoCategory> implements
		SqlConverter<BaseInfoCategory> {

	public BaseInfoCategoryConverter() {

	}

	
	public BaseInfoCategory doConvert(Object result) {
		Object[] o = (Object[]) result;
		BaseInfoCategory r = new BaseInfoCategory();
		r.setId(Short.valueOf(o[0].toString()));
		r.setInfoCategoryDesp((String) o[1]);
		return r;
	}

	private final static String DELETE_SQL = "delete from infocategory where infocategoryid=?1";

	
	public SqlParams getDeleteSql(BaseInfoCategory o) {
		return new SqlParams(DELETE_SQL, o.getId());
	}

	private final static String INSEERT_SQL = "insert into infocategory(infocategoryid,"
			+ "infocategorydesp) values(?1,?2)";

	
	public SqlParams getInsertSql(BaseInfoCategory o) {
		return new SqlParams(INSEERT_SQL, o.getId(), o.getInfoCategoryDesp());
	}

	private final static String SEQUENCE_STRING = "";

	
	public String getSequenceString(BaseInfoCategory o) {
		return SEQUENCE_STRING;
	}

	private final static String UPDATE_SQL = "update infocategory set "
			+ "infocategorydesp=?1 where infocategoryid=?2";

	
	public SqlParams getUpdateSql(BaseInfoCategory o) {
		return new SqlParams(UPDATE_SQL, o.getInfoCategoryDesp(), o.getId());
	}

}
