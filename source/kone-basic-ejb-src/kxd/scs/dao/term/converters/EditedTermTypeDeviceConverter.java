package kxd.scs.dao.term.converters;

import kxd.engine.dao.BaseDaoConverter;
import kxd.engine.dao.SqlConverter;
import kxd.engine.dao.SqlParams;
import kxd.remote.scs.beans.BaseDevice;
import kxd.remote.scs.beans.BaseTermType;
import kxd.remote.scs.beans.device.EditedTermTypeDevice;

public class EditedTermTypeDeviceConverter extends
		BaseDaoConverter<EditedTermTypeDevice> implements
		SqlConverter<EditedTermTypeDevice> {

	public EditedTermTypeDeviceConverter() {
	}

	
	public EditedTermTypeDevice doConvert(Object result) {
		Object[] o = (Object[]) result;
		EditedTermTypeDevice r = new EditedTermTypeDevice();
		r.setTermType(new BaseTermType(Integer.valueOf(o[0].toString()),
				(String) o[1]));
		r.setDevice(new BaseDevice(Integer.valueOf(o[2].toString()),
				(String) o[3]));
		r.setPort(Integer.valueOf(o[4].toString()));
		r.setExtConfig((String) o[5]);
		return r;
	}

	private final static String DELETE_SQL = "delete from termtypedevice where typeid=?1 and deviceid=?2";

	
	public SqlParams getDeleteSql(EditedTermTypeDevice o) {
		return new SqlParams(DELETE_SQL, o.getTermType().getId(), o.getDevice()
				.getId());
	}

	private final static String INSEERT_SQL = "insert into termtypedevice (typeid,"
			+ "deviceid,port,extconfig) values(?1,?2,?3,?4)";

	
	public SqlParams getInsertSql(EditedTermTypeDevice o) {
		return new SqlParams(INSEERT_SQL, o.getTermType().getId(), o
				.getDevice().getId(), o.getPort(), o.getExtConfig());
	}

	private final static String SEQUENCE_STRING = "";

	
	public String getSequenceString(EditedTermTypeDevice o) {
		return SEQUENCE_STRING;
	}

	private final static String UPDATE_SQL = "update termtypedevice set "
			+ "port=?1,extconfig=?2 where typeid=?3 and deviceid=?4";

	
	public SqlParams getUpdateSql(EditedTermTypeDevice o) {
		return new SqlParams(UPDATE_SQL, o.getPort(), o.getExtConfig(), o
				.getTermType().getId(), o.getDevice().getId());
	}

}
