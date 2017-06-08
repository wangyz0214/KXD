package kxd.scs.dao.appdeploy.converters;

import kxd.engine.dao.BaseDaoConverter;
import kxd.engine.dao.SqlConverter;
import kxd.engine.dao.SqlParams;
import kxd.remote.scs.beans.BaseBusiness;
import kxd.remote.scs.beans.appdeploy.EditedPageElement;

public class EditedPageElementConverter extends
		BaseDaoConverter<EditedPageElement> implements
		SqlConverter<EditedPageElement> {

	public EditedPageElementConverter() {
	}

	public EditedPageElement doConvert(Object result) {
		Object[] o = (Object[]) result;
		EditedPageElement r = new EditedPageElement();
		r.setId(Integer.valueOf(o[0].toString()));
		r.setPageDesp(o[2].toString());
		r.setPageCode((String) o[1]);
		r.setBusiness(new BaseBusiness(Integer.valueOf(o[3].toString()),
				(String) o[4]));
		return r;
	}

	private final static String DELETE_SQL = "delete from pageelement where pageid=?1";

	public SqlParams getDeleteSql(EditedPageElement o) {
		return new SqlParams(DELETE_SQL, o.getId());
	}

	private final static String INSEERT_SQL = "insert into pageelement (pageid,"
			+ "pagecode,pagedesp,businessid) values(?1,?2,?3,?4)";

	public SqlParams getInsertSql(EditedPageElement o) {
		return new SqlParams(INSEERT_SQL, o.getId(), o.getPageCode(),
				o.getPageDesp(), o.getBusiness().getId());
	}

	private final static String SEQUENCE_STRING = "seq_pageelement";

	public String getSequenceString(EditedPageElement o) {
		return SEQUENCE_STRING;
	}

	private final static String UPDATE_SQL = "update pageelement set "
			+ "pagecode=?1,pagedesp=?2,businessid=?3 where pageid=?4";

	public SqlParams getUpdateSql(EditedPageElement o) {
		return new SqlParams(UPDATE_SQL, o.getPageCode(), o.getPageDesp(), o
				.getBusiness().getId(), o.getId());
	}

}
