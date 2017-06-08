package kxd.scs.dao.device.converters;

import kxd.engine.dao.BaseDaoConverter;
import kxd.engine.dao.SqlConverter;
import kxd.engine.dao.SqlParams;
import kxd.remote.scs.beans.BaseDeviceTypeDriver;
import kxd.remote.scs.beans.device.EditedDeviceType;
import kxd.remote.scs.util.emun.FaultPromptOption;

public class EditedDeviceTypeConverter extends
		BaseDaoConverter<EditedDeviceType> implements
		SqlConverter<EditedDeviceType> {

	public EditedDeviceTypeConverter() {
	}

	
	public EditedDeviceType doConvert(Object result) {
		Object[] o = (Object[]) result;
		EditedDeviceType r = new EditedDeviceType();
		r.setId(Integer.valueOf(o[0].toString()));
		r.setDeviceTypeCode((String) o[1]);
		r.setDeviceTypeDesp((String) o[2]);
		r.setDriver(new BaseDeviceTypeDriver(Integer.valueOf(o[3].toString()),
				(String) o[4]));
		r.setFaultPromptOption(FaultPromptOption.valueOfIntString(o[5]));
		r.setAlarmNotifyOption(Short.valueOf(o[6].toString()));
		r.setFaultNotifyOption(Short.valueOf(o[7].toString()));
		r.setAlarmSendForm(Integer.valueOf(o[8].toString()) != 0);
		r.setFaultSendForm(Integer.valueOf(o[9].toString()) != 0);
		return r;
	}

	private final static String DELETE_SQL = "delete from devicetype where devicetype=?1";

	
	public SqlParams getDeleteSql(EditedDeviceType o) {
		return new SqlParams(DELETE_SQL, o.getId());
	}

	private final static String INSEERT_SQL = "insert into devicetype (devicetype,"
			+ "devicetypedriverid,devicetypecode,devicetypedesp,faultpromptoption,"
			+ "alarmnotifyoption,faultnotifyoption,alarmsendform,falutsendform)"
			+ " values(?1,?2,?3,?4,?5,?6,?7,?8,?9)";

	
	public SqlParams getInsertSql(EditedDeviceType o) {
		return new SqlParams(INSEERT_SQL, o.getId(), o.getDriver().getId(), o
				.getDeviceTypeCode(), o.getDeviceTypeDesp(), o
				.getFaultPromptOption().getValue(), o.getAlarmNotifyOption(), o
				.getFaultNotifyOption(), o.isAlarmSendForm() ? 1 : 0, o
				.isFaultSendForm() ? 1 : 0);
	}

	private final static String SEQUENCE_STRING = "seq_devicetype";

	
	public String getSequenceString(EditedDeviceType o) {
		return SEQUENCE_STRING;
	}

	private final static String UPDATE_SQL = "update devicetype set "
			+ "devicetypedriverid=?1,devicetypecode=?2,devicetypedesp=?3,faultpromptoption=?4,"
			+ "alarmnotifyoption=?5,faultnotifyoption=?6,alarmsendform=?7,falutsendform=?8 where devicetype=?9";

	
	public SqlParams getUpdateSql(EditedDeviceType o) {
		return new SqlParams(UPDATE_SQL, o.getDriver().getId(), o
				.getDeviceTypeCode(), o.getDeviceTypeDesp(), o
				.getFaultPromptOption().getValue(), o.getAlarmNotifyOption(), o
				.getFaultNotifyOption(), o.isAlarmSendForm() ? 1 : 0, o
				.isFaultSendForm() ? 1 : 0, o.getId());
	}

}
