package kxd.scs.dao.device.converters;

import kxd.engine.dao.BaseDaoConverter;
import kxd.engine.dao.SqlConverter;
import kxd.engine.dao.SqlParams;
import kxd.remote.scs.beans.BaseDeviceTypeDriver;

public class BaseDeviceTypeDriverConverter extends
		BaseDaoConverter<BaseDeviceTypeDriver> implements
		SqlConverter<BaseDeviceTypeDriver> {

	public BaseDeviceTypeDriverConverter() {
	}

	
	public BaseDeviceTypeDriver doConvert(Object result) {
		Object[] o = (Object[]) result;
		BaseDeviceTypeDriver r = new BaseDeviceTypeDriver();
		r.setId(Integer.valueOf(o[0].toString()));
		r.setDeviceTypeDriverDesp(o[1].toString());
		r.setDriverFile((String) o[2]);
		return r;
	}

	private final static String DELETE_SQL = "delete from devicetypedriver where devicetypedriverid=?1";

	
	public SqlParams getDeleteSql(BaseDeviceTypeDriver o) {
		return new SqlParams(DELETE_SQL, o.getId());
	}

	private final static String INSEERT_SQL = "insert into devicetypedriver (devicetypedriverid,"
			+ "devicetypedriverdesp,driverfile) values(?1,?2,?3)";

	
	public SqlParams getInsertSql(BaseDeviceTypeDriver o) {
		return new SqlParams(INSEERT_SQL, o.getId(), o
				.getDeviceTypeDriverDesp(), o.getDriverFile());
	}

	private final static String SEQUENCE_STRING = "seq_devicetypedriver";

	
	public String getSequenceString(BaseDeviceTypeDriver o) {
		return SEQUENCE_STRING;
	}

	private final static String UPDATE_SQL = "update devicetypedriver set "
			+ "devicetypedriverdesp=?1,driverfile=?2 where devicetypedriverid=?3";

	
	public SqlParams getUpdateSql(BaseDeviceTypeDriver o) {
		return new SqlParams(UPDATE_SQL, o.getDeviceTypeDriverDesp(), o
				.getDriverFile(), o.getId());
	}

}
