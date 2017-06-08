package kxd.scs.dao.right.converters;

import kxd.engine.dao.BaseDaoConverter;
import kxd.engine.dao.SqlConverter;
import kxd.engine.dao.SqlParams;
import kxd.remote.scs.beans.right.EditedManuf;

public class EditedManufConverter extends BaseDaoConverter<EditedManuf>
		implements SqlConverter<EditedManuf> {

	public EditedManufConverter() {
	}

	public EditedManuf doConvert(Object result) {
		Object[] o = (Object[]) result;
		EditedManuf r = new EditedManuf();
		r.setId(Integer.valueOf(o[0].toString()));
		r.setManufName((String) o[1]);
		r.setManufCode((String) o[2]);
		r.setSerialNumber(Integer.valueOf(o[3].toString()));
		r.setManufType(Short.valueOf(o[4].toString()));
		return r;
	}

	private final static String DELETE_SQL = "delete from manuf where manufid=?1";

	public SqlParams getDeleteSql(EditedManuf o) {
		return new SqlParams(DELETE_SQL, o.getId());
	}

	private final static String INSEERT_SQL = "insert into manuf (manufid,"
			+ "manufcode,manufname,serialnumber,manuftype) values(?1,?2,?3,?4,?5)";

	public SqlParams getInsertSql(EditedManuf o) {
		return new SqlParams(INSEERT_SQL, o.getId(), o.getManufCode(),
				o.getManufName(), o.getSerialNumber(), o.getManufType());
	}

	private final static String SEQUENCE_STRING = "seq_manuf";

	public String getSequenceString(EditedManuf o) {
		return SEQUENCE_STRING;
	}

	private final static String UPDATE_SQL = "update manuf set "
			+ "manufcode=?1,manufname=?2,serialnumber=?3 where manufid=?4";

	public SqlParams getUpdateSql(EditedManuf o) {
		return new SqlParams(UPDATE_SQL, o.getManufCode(), o.getManufName(),
				o.getSerialNumber(), o.getId());
	}

}
