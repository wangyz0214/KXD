package kxd.scs.dao.publish.converters;

import kxd.engine.dao.BaseDaoConverter;
import kxd.engine.dao.SqlConverter;
import kxd.engine.dao.SqlParams;
import kxd.remote.scs.beans.BasePrintAdCategory;

public class BasePrintAdCategoryConverter extends
		BaseDaoConverter<BasePrintAdCategory> implements
		SqlConverter<BasePrintAdCategory> {

	public BasePrintAdCategoryConverter() {

	}

	
	public BasePrintAdCategory doConvert(Object result) {
		Object[] o = (Object[]) result;
		BasePrintAdCategory r = new BasePrintAdCategory();
		r.setId(Short.valueOf(o[0].toString()));
		r.setPrintAdCategoryDesp((String) o[1]);
		return r;
	}

	private final static String DELETE_SQL = "delete from printadcategory where printadcategoryid=?1";

	
	public SqlParams getDeleteSql(BasePrintAdCategory o) {
		return new SqlParams(DELETE_SQL, o.getId());
	}

	private final static String INSEERT_SQL = "insert into printadcategory(printadcategoryid,"
			+ "printadcategorydesp) values(?1,?2)";

	
	public SqlParams getInsertSql(BasePrintAdCategory o) {
		return new SqlParams(INSEERT_SQL, o.getId(), o.getPrintAdCategoryDesp());
	}

	private final static String SEQUENCE_STRING = "";

	
	public String getSequenceString(BasePrintAdCategory o) {
		return SEQUENCE_STRING;
	}

	private final static String UPDATE_SQL = "update printadcategory set "
			+ "printadcategorydesp=?1 where printadcategoryid=?2";

	
	public SqlParams getUpdateSql(BasePrintAdCategory o) {
		return new SqlParams(UPDATE_SQL, o.getPrintAdCategoryDesp(), o.getId());
	}

}
