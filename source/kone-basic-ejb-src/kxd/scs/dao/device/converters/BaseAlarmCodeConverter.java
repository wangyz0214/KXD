package kxd.scs.dao.device.converters;

import kxd.engine.dao.BaseDaoConverter;
import kxd.engine.dao.SqlConverter;
import kxd.engine.dao.SqlParams;
import kxd.remote.scs.beans.BaseAlarmCategory;
import kxd.remote.scs.beans.BaseAlarmCode;

public class BaseAlarmCodeConverter extends BaseDaoConverter<BaseAlarmCode>
		implements SqlConverter<BaseAlarmCode> {

	public BaseAlarmCodeConverter() {
	}

	
	public BaseAlarmCode doConvert(Object result) {
		Object[] o = (Object[]) result;
		BaseAlarmCode r = new BaseAlarmCode();
		r.setDeviceType(Integer.valueOf(o[0].toString()));
		r.setAlarmCode(Integer.valueOf(o[1].toString()));
		r.setAlarmCategory(new BaseAlarmCategory(Integer.valueOf(o[2]
				.toString())));
		r.getAlarmCategory().setAlarmCategoryDesp((String) o[3]);
		r.setAlarmDesp((String) o[4]);
		return r;
	}

	private final static String DELETE_SQL = "delete from alarmcode where devicetype=?1 and alarmcode=?2";

	
	public SqlParams getDeleteSql(BaseAlarmCode o) {
		return new SqlParams(DELETE_SQL, o.getDeviceType(), o.getAlarmCode());
	}

	private final static String INSEERT_SQL = "insert into alarmcode (devicetype,"
			+ "alarmcode,alarmclassid,alarmcodedesp) values(?1,?2,?3,?4)";

	
	public SqlParams getInsertSql(BaseAlarmCode o) {
		return new SqlParams(INSEERT_SQL, o.getDeviceType(), o.getAlarmCode(),
				o.getAlarmCategory().getId(), o.getAlarmDesp());
	}

	private final static String SEQUENCE_STRING = "";

	
	public String getSequenceString(BaseAlarmCode o) {
		return SEQUENCE_STRING;
	}

	private final static String UPDATE_SQL = "update alarmcode set "
			+ "alarmclassid=?1,alarmcodedesp=?2 where devicetype=?3 and alarmcode=?4";

	
	public SqlParams getUpdateSql(BaseAlarmCode o) {
		return new SqlParams(UPDATE_SQL, o.getAlarmCategory().getId(), o
				.getAlarmDesp(), o.getDeviceType(), o.getAlarmCode());
	}

}
