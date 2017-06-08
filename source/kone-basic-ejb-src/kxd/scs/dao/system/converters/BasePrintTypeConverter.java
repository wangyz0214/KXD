package kxd.scs.dao.system.converters;

import kxd.engine.dao.BaseDaoConverter;
import kxd.engine.dao.SqlConverter;
import kxd.engine.dao.SqlParams;
import kxd.remote.scs.beans.BasePrintType;

public class BasePrintTypeConverter extends BaseDaoConverter<BasePrintType>
		implements SqlConverter<BasePrintType> {

	public BasePrintTypeConverter() {

	}

	
	public BasePrintType doConvert(Object result) {
		Object[] o = (Object[]) result;
		BasePrintType r = new BasePrintType();
		r.setId(Short.valueOf(o[0].toString()));
		r.setPrintTypeDesp((String) o[1]);
		return r;
	}

	private final static String DELETE_SQL = "delete from printtype where printtype=?1";

	
	public SqlParams getDeleteSql(BasePrintType o) {
		return new SqlParams(DELETE_SQL, o.getId());
	}

	private final static String INSEERT_SQL = "insert into printtype(printtype,"
			+ "printtypedesp) values(?1,?2)";

	
	public SqlParams getInsertSql(BasePrintType o) {
		return new SqlParams(INSEERT_SQL, o.getId(), o.getPrintTypeDesp());
	}

	private final static String SEQUENCE_STRING = "";

	
	public String getSequenceString(BasePrintType o) {
		return SEQUENCE_STRING;
	}

	private final static String UPDATE_SQL = "update printtype set "
			+ "printtypedesp=?1 where printtype=?2";

	
	public SqlParams getUpdateSql(BasePrintType o) {
		return new SqlParams(UPDATE_SQL, o.getPrintTypeDesp(), o.getId());
	}

}
