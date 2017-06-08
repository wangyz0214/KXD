package kxd.scs.dao.device.converters;

import kxd.engine.dao.BaseDaoConverter;
import kxd.engine.dao.SqlConverter;
import kxd.engine.dao.SqlParams;
import kxd.remote.scs.beans.BaseDeviceDriver;

public class BaseDeviceDriverConverter extends
		BaseDaoConverter<BaseDeviceDriver> implements
		SqlConverter<BaseDeviceDriver> {

	public BaseDeviceDriverConverter() {
	}

	
	public BaseDeviceDriver doConvert(Object result) {
		Object[] o = (Object[]) result;
		BaseDeviceDriver r = new BaseDeviceDriver();
		r.setId(Integer.valueOf(o[0].toString()));
		r.setDeviceDriverDesp(o[1].toString());
		r.setDriverFile((String) o[2]);
		return r;
	}

	private final static String DELETE_SQL = "delete from devicedriver where devicedriverid=?1";

	
	public SqlParams getDeleteSql(BaseDeviceDriver o) {
		return new SqlParams(DELETE_SQL, o.getId());
	}

	private final static String INSEERT_SQL = "insert into devicedriver (devicedriverid,"
			+ "devicedriverdesp,driverfile) values(?1,?2,?3)";

	
	public SqlParams getInsertSql(BaseDeviceDriver o) {
		return new SqlParams(INSEERT_SQL, o.getId(), o.getDeviceDriverDesp(), o
				.getDriverFile());
	}

	private final static String SEQUENCE_STRING = "seq_devicedriver";

	
	public String getSequenceString(BaseDeviceDriver o) {
		return SEQUENCE_STRING;
	}

	private final static String UPDATE_SQL = "update devicedriver set "
			+ "devicedriverdesp=?1,driverfile=?2 where devicedriverid=?3";

	
	public SqlParams getUpdateSql(BaseDeviceDriver o) {
		return new SqlParams(UPDATE_SQL, o.getDeviceDriverDesp(), o
				.getDriverFile(), o.getId());
	}

}
