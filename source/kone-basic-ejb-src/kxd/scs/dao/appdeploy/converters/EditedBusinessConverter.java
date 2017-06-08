package kxd.scs.dao.appdeploy.converters;

import kxd.engine.dao.BaseDaoConverter;
import kxd.engine.dao.SqlConverter;
import kxd.engine.dao.SqlParams;
import kxd.remote.scs.beans.BaseBusinessCategory;
import kxd.remote.scs.beans.appdeploy.EditedBusiness;

public class EditedBusinessConverter extends BaseDaoConverter<EditedBusiness>
		implements SqlConverter<EditedBusiness> {

	public EditedBusinessConverter() {
	}

	public EditedBusiness doConvert(Object result) {
		Object[] o = (Object[]) result;
		EditedBusiness r = new EditedBusiness();
		r.setId(Integer.valueOf(o[0].toString()));
		r.setBusinessDesp(o[1].toString());
		r.setBusinessCategory(new BaseBusinessCategory(Integer.valueOf(o[2]
				.toString()), (String) o[3]));
		return r;
	}

	private final static String DELETE_SQL = "delete from business where businessid=?1";

	public SqlParams getDeleteSql(EditedBusiness o) {
		return new SqlParams(DELETE_SQL, o.getId());
	}

	private final static String INSEERT_SQL = "insert into business (businessid,"
			+ "businessdesp,businesscategoryid) values(?1,?2,?3)";

	public SqlParams getInsertSql(EditedBusiness o) {
		return new SqlParams(INSEERT_SQL, o.getId(), o.getBusinessDesp(), o
				.getBusinessCategory().getId());
	}

	private final static String SEQUENCE_STRING = "seq_business";

	public String getSequenceString(EditedBusiness o) {
		return SEQUENCE_STRING;
	}

	private final static String UPDATE_SQL = "update business set "
			+ "businessdesp=?1,businesscategoryid=?2 where businessid=?3";

	public SqlParams getUpdateSql(EditedBusiness o) {
		return new SqlParams(UPDATE_SQL, o.getBusinessDesp(), o
				.getBusinessCategory().getId(), o.getId());
	}

}
