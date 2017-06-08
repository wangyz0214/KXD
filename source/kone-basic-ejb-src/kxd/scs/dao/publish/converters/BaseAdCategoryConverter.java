package kxd.scs.dao.publish.converters;

import kxd.engine.dao.BaseDaoConverter;
import kxd.engine.dao.SqlConverter;
import kxd.engine.dao.SqlParams;
import kxd.remote.scs.beans.BaseAdCategory;

public class BaseAdCategoryConverter extends BaseDaoConverter<BaseAdCategory>
		implements SqlConverter<BaseAdCategory> {

	public BaseAdCategoryConverter() {

	}

	
	public BaseAdCategory doConvert(Object result) {
		Object[] o = (Object[]) result;
		BaseAdCategory r = new BaseAdCategory();
		r.setId(Short.valueOf(o[0].toString()));
		r.setAdCategoryDesp((String) o[1]);
		return r;
	}

	private final static String DELETE_SQL = "delete from adcategory where adcategoryid=?1";

	
	public SqlParams getDeleteSql(BaseAdCategory o) {
		return new SqlParams(DELETE_SQL, o.getId());
	}

	private final static String INSEERT_SQL = "insert into adcategory(adcategoryid,"
			+ "adcategorydesp) values(?1,?2)";

	
	public SqlParams getInsertSql(BaseAdCategory o) {
		return new SqlParams(INSEERT_SQL, o.getId(), o.getAdCategoryDesp());
	}

	private final static String SEQUENCE_STRING = "";

	
	public String getSequenceString(BaseAdCategory o) {
		return SEQUENCE_STRING;
	}

	private final static String UPDATE_SQL = "update adcategory set "
			+ "adcategorydesp=?1 where adcategoryid=?2";

	
	public SqlParams getUpdateSql(BaseAdCategory o) {
		return new SqlParams(UPDATE_SQL, o.getAdCategoryDesp(), o.getId());
	}

}
