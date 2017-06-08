package kxd.scs.dao.publish.converters;

import kxd.engine.dao.BaseDaoConverter;
import kxd.engine.dao.SqlConverter;
import kxd.engine.dao.SqlParams;
import kxd.remote.scs.beans.BaseOrg;
import kxd.remote.scs.beans.BasePrintAdCategory;
import kxd.remote.scs.beans.adinfo.EditedPrintAd;
import kxd.remote.scs.util.emun.AuditStatus;

public class EditedPrintAdConverter extends BaseDaoConverter<EditedPrintAd>
		implements SqlConverter<EditedPrintAd> {

	public EditedPrintAdConverter() {

	}

	public EditedPrintAd doConvert(Object result) {
		Object[] o = (Object[]) result;
		EditedPrintAd r = new EditedPrintAd();
		r.setId(Integer.valueOf(o[0].toString()));
		r.setOrg(new BaseOrg(Integer.valueOf(o[1].toString()), (String) o[2]));
		BasePrintAdCategory a = new BasePrintAdCategory(Short.valueOf(o[3]
				.toString()));
		a.setPrintAdCategoryDesp((String) o[4]);
		r.setAdCategory(a);
		r.setContent((String) o[5]);
		r.setAuditStatus(AuditStatus.valueOfIntString(o[6]));
		return r;
	}

	private final static String DELETE_SQL = "delete from orgprintad where printadid=?1";

	public SqlParams getDeleteSql(EditedPrintAd o) {
		return new SqlParams(DELETE_SQL, o.getId());
	}

	private final static String INSEERT_SQL = "insert into orgprintad(printadid,"
			+ "orgid,printadcategoryid,content) values(?1,?2,?3,?4)";

	public SqlParams getInsertSql(EditedPrintAd o) {
		return new SqlParams(INSEERT_SQL, o.getId(), o.getOrg().getId(), o
				.getAdCategory().getId(), o.getContent());
	}

	private final static String SEQUENCE_STRING = "seq_orgprintad";

	public String getSequenceString(EditedPrintAd o) {
		return SEQUENCE_STRING;
	}

	private final static String UPDATE_SQL = "update orgprintad set "
			+ "orgid=?1,printadcategoryid=?2,content=?3,audited=0 where printadid=?4";

	public SqlParams getUpdateSql(EditedPrintAd o) {
		return new SqlParams(UPDATE_SQL, o.getOrg().getId(), o.getAdCategory()
				.getId(), o.getContent(), o.getId());
	}

}
