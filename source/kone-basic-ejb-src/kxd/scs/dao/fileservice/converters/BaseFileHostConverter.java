package kxd.scs.dao.fileservice.converters;

import kxd.engine.dao.BaseDaoConverter;
import kxd.engine.dao.SqlConverter;
import kxd.engine.dao.SqlParams;
import kxd.remote.scs.beans.BaseFileHost;

public class BaseFileHostConverter extends BaseDaoConverter<BaseFileHost>
		implements SqlConverter<BaseFileHost> {

	public BaseFileHostConverter() {

	}

	public BaseFileHost doConvert(Object result) {
		Object[] o = (Object[]) result;
		BaseFileHost r = new BaseFileHost();
		r.setId(Short.valueOf(o[0].toString()));
		r.setHostDesp((String) o[1]);
		r.setFileRootDir((String) o[2]);
		r.setHttpUrlPrefix((String) o[3]);
		r.setFtpHost((String) o[4]);
		r.setFtpUser((String) o[5]);
		r.setFtpPasswd((String) o[6]);
		r.setRealHttpUrlRoot((String) o[7]);
		return r;
	}

	private final static String DELETE_SQL = "delete from filehost where hostid=?1";

	public SqlParams getDeleteSql(BaseFileHost o) {
		return new SqlParams(DELETE_SQL, o.getId());
	}

	private final static String INSEERT_SQL = "insert into filehost (hostid,"
			+ "hostdesp,filerootdir,httpurlprefix,ftphost,ftpuser,ftppasswd,realhttpurlroot) "
			+ "values(?1,?2,?3,?4,?5,?6,?7,?8)";

	public SqlParams getInsertSql(BaseFileHost o) {
		return new SqlParams(INSEERT_SQL, o.getId(), o.getHostDesp(),
				o.getFileRootDir(), o.getHttpUrlPrefix(), o.getFtpHost(),
				o.getFtpUser(), o.getFtpPasswd(), o.getRealHttpUrlRoot());
	}

	private final static String SEQUENCE_STRING = "";

	public String getSequenceString(BaseFileHost o) {
		return SEQUENCE_STRING;
	}

	private final static String UPDATE_SQL = "update filehost set "
			+ "hostdesp=?1,filerootdir=?2,httpurlprefix=?3,ftphost=?4,"
			+ "ftpuser=?5,ftppasswd=?6,realhttpurlroot=?7 where hostid=?8";

	public SqlParams getUpdateSql(BaseFileHost o) {
		return new SqlParams(UPDATE_SQL, o.getHostDesp(), o.getFileRootDir(),
				o.getHttpUrlPrefix(), o.getFtpHost(), o.getFtpUser(),
				o.getFtpPasswd(), o.getRealHttpUrlRoot(), o.getId());
	}

}
