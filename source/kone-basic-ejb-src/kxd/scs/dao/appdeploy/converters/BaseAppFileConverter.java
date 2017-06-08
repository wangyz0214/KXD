package kxd.scs.dao.appdeploy.converters;

import kxd.engine.dao.BaseDaoConverter;
import kxd.engine.dao.SqlConverter;
import kxd.engine.dao.SqlParams;
import kxd.remote.scs.beans.BaseApp;
import kxd.remote.scs.beans.BaseAppFile;

public class BaseAppFileConverter extends BaseDaoConverter<BaseAppFile>
		implements SqlConverter<BaseAppFile> {

	public BaseAppFileConverter() {
	}

	public BaseAppFile doConvert(Object result) {
		Object[] o = (Object[]) result;
		BaseAppFile r = new BaseAppFile();
		r.setId(Integer.valueOf(o[0].toString()));
		r.setAppFilename(o[1].toString());
		r.setApp(new BaseApp(Integer.valueOf(o[2].toString())));
		r.getApp().setAppDesp(o[3].toString());
		return r;
	}

	private final static String DELETE_SQL = "delete from appfiles where appfileid=?1";

	public SqlParams getDeleteSql(BaseAppFile o) {
		return new SqlParams(DELETE_SQL, o.getId());
	}

	private final static String INSEERT_SQL = "insert into appfiles(appfileid,"
			+ "appfilename,appid) values(?1,?2,?3)";

	public SqlParams getInsertSql(BaseAppFile o) {
		return new SqlParams(INSEERT_SQL, o.getId(), o.getAppFilename(), o
				.getApp().getId());
	}

	private final static String SEQUENCE_STRING = "seq_appfile";

	public String getSequenceString(BaseAppFile o) {
		return SEQUENCE_STRING;
	}

	private final static String UPDATE_SQL = "update appfiles set "
			+ "appfilename=?1 where appfileid=?2";

	public SqlParams getUpdateSql(BaseAppFile o) {
		return new SqlParams(UPDATE_SQL, o.getAppFilename(), o.getId());
	}

}
