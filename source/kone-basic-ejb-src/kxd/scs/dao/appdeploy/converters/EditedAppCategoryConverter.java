package kxd.scs.dao.appdeploy.converters;

import kxd.engine.dao.BaseDaoConverter;
import kxd.engine.dao.SqlConverter;
import kxd.engine.dao.SqlParams;
import kxd.remote.scs.beans.appdeploy.EditedAppCategory;

public class EditedAppCategoryConverter extends
		BaseDaoConverter<EditedAppCategory> implements
		SqlConverter<EditedAppCategory> {

	public EditedAppCategoryConverter() {
	}

	public EditedAppCategory doConvert(Object result) {
		Object[] o = (Object[]) result;
		EditedAppCategory r = new EditedAppCategory();
		r.setId(Integer.valueOf(o[0].toString()));
		r.setAppCategoryCode(o[1].toString());
		r.setAppCategoryDesp(o[2].toString());
		return r;
	}

	private final static String DELETE_SQL = "delete from appcategory where appcategoryid=?1";

	public SqlParams getDeleteSql(EditedAppCategory o) {
		return new SqlParams(DELETE_SQL, o.getId());
	}

	private final static String INSEERT_SQL = "insert into appcategory(appcategoryid,"
			+ "appcategorycode,appcategorydesp) values(?1,?2,?3)";

	public SqlParams getInsertSql(EditedAppCategory o) {
		return new SqlParams(INSEERT_SQL, o.getId(), o.getAppCategoryCode(),
				o.getAppCategoryDesp());
	}

	private final static String SEQUENCE_STRING = "seq_appcategory";

	public String getSequenceString(EditedAppCategory o) {
		return SEQUENCE_STRING;
	}

	private final static String UPDATE_SQL = "update appcategory set "
			+ "appcategorycode=?1,appcategorydesp=?2 where appcategoryid=?3";

	public SqlParams getUpdateSql(EditedAppCategory o) {
		return new SqlParams(UPDATE_SQL, o.getAppCategoryCode(),
				o.getAppCategoryDesp(), o.getId());
	}

}
