package kxd.scs.dao.appdeploy.converters;

import kxd.engine.dao.BaseDaoConverter;
import kxd.engine.dao.SqlConverter;
import kxd.engine.dao.SqlParams;
import kxd.remote.scs.beans.BaseAppCategory;
import kxd.remote.scs.beans.appdeploy.EditedApp;

public class EditedAppConverter extends BaseDaoConverter<EditedApp> implements
		SqlConverter<EditedApp> {

	public EditedAppConverter() {
	}

	public EditedApp doConvert(Object result) {
		Object[] o = (Object[]) result;
		EditedApp r = new EditedApp();
		r.setId(Integer.valueOf(o[0].toString()));
		r.setAppCode(o[1].toString());
		r.setAppDesp(o[2].toString());
		r.setAppCategory(new BaseAppCategory(Integer.valueOf(o[3].toString()),
				o[4].toString()));
		return r;
	}

	private final static String DELETE_SQL = "delete from app where appid=?1";

	public SqlParams getDeleteSql(EditedApp o) {
		return new SqlParams(DELETE_SQL, o.getId());
	}

	private final static String INSEERT_SQL = "insert into app(appid,"
			+ "appcode,appdesp,appcategoryid) values(?1,?2,?3,?4)";

	public SqlParams getInsertSql(EditedApp o) {
		return new SqlParams(INSEERT_SQL, o.getId(), o.getAppCode(),
				o.getAppDesp(), o.getAppCategory().getId());
	}

	private final static String SEQUENCE_STRING = "seq_app";

	public String getSequenceString(EditedApp o) {
		return SEQUENCE_STRING;
	}

	private final static String UPDATE_SQL = "update app set "
			+ "appcode=?1,appdesp=?2,appcategoryid=?3 where appid=?4";

	public SqlParams getUpdateSql(EditedApp o) {
		return new SqlParams(UPDATE_SQL, o.getAppCode(), o.getAppDesp(), o
				.getAppCategory().getId(), o.getId());
	}

}
