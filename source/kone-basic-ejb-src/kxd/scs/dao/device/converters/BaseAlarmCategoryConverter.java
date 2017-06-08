package kxd.scs.dao.device.converters;

import kxd.engine.dao.BaseDaoConverter;
import kxd.engine.dao.SqlConverter;
import kxd.engine.dao.SqlParams;
import kxd.remote.scs.beans.BaseAlarmCategory;
import kxd.remote.scs.util.emun.AlarmLevel;

public class BaseAlarmCategoryConverter extends
		BaseDaoConverter<BaseAlarmCategory> implements
		SqlConverter<BaseAlarmCategory> {

	public BaseAlarmCategoryConverter() {
	}

	
	public BaseAlarmCategory doConvert(Object result) {
		Object[] o = (Object[]) result;
		BaseAlarmCategory r = new BaseAlarmCategory();
		r.setId(Integer.valueOf(o[0].toString()));
		r.setAlarmCategoryDesp(o[1].toString());
		r.setAlarmLevel(AlarmLevel.valueOfIntString(o[2]));
		return r;
	}

	private final static String DELETE_SQL = "delete from alarmcategory where alarmclassid=?1";

	
	public SqlParams getDeleteSql(BaseAlarmCategory o) {
		return new SqlParams(DELETE_SQL, o.getId());
	}

	private final static String INSEERT_SQL = "insert into alarmcategory (alarmclassid,"
			+ "alarmclassdesp,alarmlevel) values(?1,?2,?3)";

	
	public SqlParams getInsertSql(BaseAlarmCategory o) {
		return new SqlParams(INSEERT_SQL, o.getId(), o.getAlarmCategoryDesp(),
				o.getAlarmLevel().getValue());
	}

	private final static String SEQUENCE_STRING = "";

	
	public String getSequenceString(BaseAlarmCategory o) {
		return SEQUENCE_STRING;
	}

	private final static String UPDATE_SQL = "update alarmcategory set "
			+ "alarmclassdesp=?1,alarmlevel=?2 where alarmclassid=?3";

	
	public SqlParams getUpdateSql(BaseAlarmCategory o) {
		return new SqlParams(UPDATE_SQL, o.getAlarmCategoryDesp(), o
				.getAlarmLevel().getValue(), o.getId());
	}

}
