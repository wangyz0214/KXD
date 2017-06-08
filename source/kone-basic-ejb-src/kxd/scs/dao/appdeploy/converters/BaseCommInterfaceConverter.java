package kxd.scs.dao.appdeploy.converters;

import kxd.engine.dao.BaseDaoConverter;
import kxd.engine.dao.SqlConverter;
import kxd.engine.dao.SqlParams;
import kxd.remote.scs.beans.BaseCommInterface;

public class BaseCommInterfaceConverter extends
		BaseDaoConverter<BaseCommInterface> implements
		SqlConverter<BaseCommInterface> {

	public BaseCommInterfaceConverter() {
	}

	public BaseCommInterface doConvert(Object result) {
		Object[] o = (Object[]) result;
		BaseCommInterface r = new BaseCommInterface();
		r.setId(Short.valueOf(o[0].toString()));
		r.setDesp(o[1].toString());
		r.setType(Integer.valueOf(o[2].toString()));
		return r;
	}

	private final static String DELETE_SQL = "delete from comm_interface where interfaceid=?1";

	public SqlParams getDeleteSql(BaseCommInterface o) {
		return new SqlParams(DELETE_SQL, o.getId());
	}

	private final static String INSEERT_SQL = "insert into comm_interface(interfaceid,"
			+ "interfacedesp,interfacetype) values(?1,?2,?3)";

	public SqlParams getInsertSql(BaseCommInterface o) {
		return new SqlParams(INSEERT_SQL, o.getId(), o.getDesp(), o.getType());
	}

	private final static String SEQUENCE_STRING = "seq_comminterface";

	public String getSequenceString(BaseCommInterface o) {
		return SEQUENCE_STRING;
	}

	private final static String UPDATE_SQL = "update comm_interface set "
			+ "interfacedesp=?1,interfacetype=?2 where interfaceid=?3";

	public SqlParams getUpdateSql(BaseCommInterface o) {
		return new SqlParams(UPDATE_SQL, o.getDesp(), o.getType(), o.getId());
	}
}
