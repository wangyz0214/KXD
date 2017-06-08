package kxd.scs.dao.invoice.converters;

import kxd.engine.dao.BaseDaoConverter;
import kxd.engine.dao.SqlConverter;
import kxd.engine.dao.SqlParams;
import kxd.remote.scs.beans.BaseOrg;
import kxd.remote.scs.beans.invoice.BaseInvoiceTemplate;
import kxd.remote.scs.beans.invoice.EditedInvoiceConfig;

public class EditedInvoiceConfigConverter extends
		BaseDaoConverter<EditedInvoiceConfig> implements
		SqlConverter<EditedInvoiceConfig> {

	private final static String tablename = "invoice_config";

	public EditedInvoiceConfig doConvert(Object result) {
		Object[] o = (Object[]) result;
		EditedInvoiceConfig r = new EditedInvoiceConfig();
		r.setId(Integer.valueOf(o[0].toString()));
		r.setConfigDesp(o[1].toString());
		r.setConfigCode(o[2].toString());
		r.setOrg(new BaseOrg(Integer.valueOf(o[3].toString()), o[4].toString()));// 机构id,
																					// 机构名称
		r.setInvoiceType(Integer.valueOf(o[5].toString()));
		r.setInvoiceTemplate(new BaseInvoiceTemplate(Integer.valueOf(o[6]
				.toString()), o[7].toString()));// 模板ID,模板描述
		r.setTaxFlag(Integer.valueOf(o[8].toString()));
		r.setAwayFlag(Integer.valueOf(o[9].toString()));
		r.setAlertCount(Integer.valueOf(o[10].toString()));
		r.setLogged(Integer.valueOf(o[11].toString()) != 0);
		r.setExtdata0(o[12] == null ? "" : o[12].toString());
		r.setExtdata1(o[13] == null ? "" : o[13].toString());
		return r;
	}

	private final static String DELETE_SQL = "delete from " + tablename
			+ " where configid=?1";

	public SqlParams getDeleteSql(EditedInvoiceConfig o) {
		return new SqlParams(DELETE_SQL, o.getId());
	}

	private final static String INSEERT_SQL = "insert into "
			+ tablename
			+ "(configid,configdesp,configcode,orgid,invoicetype,templateid,taxflag,awayflag,alertcount,logged,extdata0,extdata1) "
			+ " values(?1,?2,?3,?4,?5,?6,?7,?8,?9,?10,?11,?12)";

	public SqlParams getInsertSql(EditedInvoiceConfig o) {
		return new SqlParams(INSEERT_SQL, o.getId(), o.getConfigDesp(),
				o.getConfigCode(), o.getOrg().getOrgId().toString(),
				o.getInvoiceType(), o.getInvoiceTemplate().getId(),
				o.getTaxFlag(), o.getAwayFlag(), o.getAlertCount(),
				o.isLogged(), o.getExtdata0(), o.getExtdata1());
	}

	private final static String SEQUENCE_STRING = "seq_" + tablename;

	public String getSequenceString(EditedInvoiceConfig o) {
		return SEQUENCE_STRING;
	}

	private final static String UPDATE_SQL = "update "
			+ tablename
			+ " set configdesp=?1,configcode=?2,orgid=?3,invoicetype=?4,templateid=?5,"
			+ "taxflag=?6,awayflag=?7,alertcount=?8,logged=?9,extdata0=?10,extdata1=?11"
			+ " where configid=?12";

	public SqlParams getUpdateSql(EditedInvoiceConfig o) {
		return new SqlParams(UPDATE_SQL, o.getConfigDesp(), o.getConfigCode(),
				o.getOrg().getOrgId(), o.getInvoiceType(), o
						.getInvoiceTemplate().getId(), o.getTaxFlag(),
				o.getAwayFlag(), o.getAlertCount(), o.isLogged(),
				o.getExtdata0(), o.getExtdata1(), o.getId());
	}

}
