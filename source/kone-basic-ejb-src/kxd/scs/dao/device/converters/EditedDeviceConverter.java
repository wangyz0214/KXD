package kxd.scs.dao.device.converters;

import kxd.engine.dao.BaseDaoConverter;
import kxd.engine.dao.SqlConverter;
import kxd.engine.dao.SqlParams;
import kxd.remote.scs.beans.BaseDeviceDriver;
import kxd.remote.scs.beans.BaseDeviceType;
import kxd.remote.scs.beans.device.EditedDevice;

public class EditedDeviceConverter extends BaseDaoConverter<EditedDevice>
		implements SqlConverter<EditedDevice> {

	public EditedDeviceConverter() {
	}

	
	public EditedDevice doConvert(Object result) {
		Object[] o = (Object[]) result;
		EditedDevice r = new EditedDevice();
		r.setId(Integer.valueOf(o[0].toString()));
		r.setDeviceName((String) o[1]);
		r.setManufDesp((String) o[2]);
		r.setDriver(new BaseDeviceDriver(Integer.valueOf(o[3].toString()),
				(String) o[4]));
		r.setDeviceType(new BaseDeviceType(Integer.valueOf(o[5].toString()),
				(String) o[6]));
		r.setExtConfig((String) o[7]);
		return r;
	}

	private final static String DELETE_SQL = "delete from device where deviceid=?1";

	
	public SqlParams getDeleteSql(EditedDevice o) {
		return new SqlParams(DELETE_SQL, o.getId());
	}

	private final static String INSEERT_SQL = "insert into device (deviceid,"
			+ "devicetype,devicedriverid,manufdesp,devicename,addconfig)"
			+ " values(?1,?2,?3,?4,?5,?6)";

	
	public SqlParams getInsertSql(EditedDevice o) {
		return new SqlParams(INSEERT_SQL, o.getId(), o.getDeviceType().getId(),
				o.getDriver().getId(), o.getManufDesp(), o.getDeviceName(), o
						.getExtConfig());
	}

	private final static String SEQUENCE_STRING = "seq_device";

	
	public String getSequenceString(EditedDevice o) {
		return SEQUENCE_STRING;
	}

	private final static String UPDATE_SQL = "update device set "
			+ "devicetype=?1,devicedriverid=?2,manufdesp=?3,devicename=?4,addconfig=?5"
			+ " where deviceid=?6";

	
	public SqlParams getUpdateSql(EditedDevice o) {
		return new SqlParams(UPDATE_SQL, o.getDeviceType().getId(), o
				.getDriver().getId(), o.getManufDesp(), o.getDeviceName(), o
				.getExtConfig(), o.getId());
	}

}
