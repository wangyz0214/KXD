package kxd.scs.dao.term.converters;

import kxd.engine.dao.BaseDaoConverter;
import kxd.engine.dao.SqlConverter;
import kxd.engine.dao.SqlParams;
import kxd.remote.scs.beans.BaseApp;
import kxd.remote.scs.beans.BaseManuf;
import kxd.remote.scs.beans.device.EditedTermType;
import kxd.remote.scs.util.emun.CashFlag;
import kxd.remote.scs.util.emun.FixType;

public class EditedTermTypeConverter extends BaseDaoConverter<EditedTermType>
		implements SqlConverter<EditedTermType> {

	public EditedTermTypeConverter() {
	}

	
	public EditedTermType doConvert(Object result) {
		Object[] o = (Object[]) result;
		EditedTermType r = new EditedTermType();
		r.setId(Integer.valueOf(o[0].toString()));
		r.setTypeDesp((String) o[1]);
		r.setManuf(new BaseManuf(Integer.valueOf(o[2].toString()),
				(String) o[3]));
		r.setTypeCode((String) o[4]);
		r.setFixType(FixType.valueOfIntString(o[5]));
		r.setCashFlag(CashFlag.valueOfIntString(o[6]));
		r.setApp(new BaseApp(Integer.valueOf(o[7].toString()), (String) o[8]));
		return r;
	}

	private final static String DELETE_SQL = "delete from termtype where typeid=?1";

	
	public SqlParams getDeleteSql(EditedTermType o) {
		return new SqlParams(DELETE_SQL, o.getId());
	}

	private final static String INSEERT_SQL = "insert into termtype (typeid,"
			+ "typecode,typedesp,manufid,appid,fixtype,cashflag) values(?1,?2,"
			+ "?3,?4,?5,?6,?7)";

	
	public SqlParams getInsertSql(EditedTermType o) {
		return new SqlParams(INSEERT_SQL, o.getId(), o.getTypeCode(), o
				.getTypeDesp(), o.getManuf().getId(), o.getApp().getId(), o
				.getFixType().getValue(), o.getCashFlag().getValue());
	}

	private final static String SEQUENCE_STRING = "seq_termtype";

	
	public String getSequenceString(EditedTermType o) {
		return SEQUENCE_STRING;
	}

	private final static String UPDATE_SQL = "update termtype set "
			+ "typecode=?1,typedesp=?2,appid=?3,fixtype=?4"
			+ ",cashflag=?5 where typeid=?6";

	
	public SqlParams getUpdateSql(EditedTermType o) {
		return new SqlParams(UPDATE_SQL, o.getTypeCode(), o.getTypeDesp(), o
				.getApp().getId(), o.getFixType().getValue(), o.getCashFlag()
				.getValue(), o.getId());
	}
}
