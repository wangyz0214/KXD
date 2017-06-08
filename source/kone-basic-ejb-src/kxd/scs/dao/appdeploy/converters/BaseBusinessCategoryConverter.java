package kxd.scs.dao.appdeploy.converters;

import kxd.engine.dao.BaseDaoConverter;
import kxd.engine.dao.SqlConverter;
import kxd.engine.dao.SqlParams;
import kxd.remote.scs.beans.BaseBusinessCategory;

public class BaseBusinessCategoryConverter extends
		BaseDaoConverter<BaseBusinessCategory> implements
		SqlConverter<BaseBusinessCategory> {

	public BaseBusinessCategoryConverter() {
	}

	public BaseBusinessCategory doConvert(Object result) {
		Object[] o = (Object[]) result;
		BaseBusinessCategory r = new BaseBusinessCategory();
		r.setId(Integer.valueOf(o[0].toString()));
		r.setBusinessCategoryDesp(o[1].toString());
		return r;
	}

	private final static String DELETE_SQL = "delete from businesscategory where businesscategoryid=?1";

	public SqlParams getDeleteSql(BaseBusinessCategory o) {
		return new SqlParams(DELETE_SQL, o.getId());
	}

	private final static String INSEERT_SQL = "insert into businesscategory (businesscategoryid,"
			+ "businesscategorydesp) values(?1,?2)";

	public SqlParams getInsertSql(BaseBusinessCategory o) {
		return new SqlParams(INSEERT_SQL, o.getId(),
				o.getBusinessCategoryDesp());
	}

	private final static String SEQUENCE_STRING = "seq_businesscategory";

	public String getSequenceString(BaseBusinessCategory o) {
		return SEQUENCE_STRING;
	}

	private final static String UPDATE_SQL = "update businesscategory set "
			+ "businesscategorydesp=?1 where businesscategoryid=?2";

	public SqlParams getUpdateSql(BaseBusinessCategory o) {
		return new SqlParams(UPDATE_SQL, o.getBusinessCategoryDesp(), o.getId());
	}

}
