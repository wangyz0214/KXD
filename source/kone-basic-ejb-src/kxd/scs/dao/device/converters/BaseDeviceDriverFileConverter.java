package kxd.scs.dao.device.converters;

import kxd.engine.dao.BaseDaoConverter;
import kxd.engine.dao.SqlConverter;
import kxd.engine.dao.SqlParams;
import kxd.remote.scs.beans.BaseDeviceDriverFile;

public class BaseDeviceDriverFileConverter extends
		BaseDaoConverter<BaseDeviceDriverFile> implements
		SqlConverter<BaseDeviceDriverFile> {

	public BaseDeviceDriverFileConverter() {
	}

	
	public BaseDeviceDriverFile doConvert(Object result) {
		Object[] o = (Object[]) result;
		BaseDeviceDriverFile r = new BaseDeviceDriverFile();
		r.setId(Integer.valueOf(o[0].toString()));
		r.setFileName((String) o[1]);
		r.setDeviceDriverId(Integer.valueOf(o[2].toString()));
		return r;
	}

	private final static String DELETE_SQL = "delete from devicedriverfiles where devicedriverfileid=?1";

	
	public SqlParams getDeleteSql(BaseDeviceDriverFile o) {
		return new SqlParams(DELETE_SQL, o.getId());
	}

	private final static String INSEERT_SQL = "insert into devicedriverfiles (devicedriverfileid,"
			+ "filename,devicedriverid) values(?1,?2,?3)";

	
	public SqlParams getInsertSql(BaseDeviceDriverFile o) {
		return new SqlParams(INSEERT_SQL, o.getId(), o.getFileName(), o
				.getDeviceDriverId());
	}

	private final static String SEQUENCE_STRING = "seq_devicedriverfile";

	
	public String getSequenceString(BaseDeviceDriverFile o) {
		return SEQUENCE_STRING;
	}

	private final static String UPDATE_SQL = "update devicedriverfiles set "
			+ "filename=?1,devicedriverid=?2 where devicedriverfileid=?3";

	
	public SqlParams getUpdateSql(BaseDeviceDriverFile o) {
		return new SqlParams(UPDATE_SQL, o.getFileName(),
				o.getDeviceDriverId(), o.getId());
	}

}
