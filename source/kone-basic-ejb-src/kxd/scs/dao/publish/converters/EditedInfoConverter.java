package kxd.scs.dao.publish.converters;

import java.util.Date;

import kxd.engine.dao.BaseDaoConverter;
import kxd.engine.dao.SqlConverter;
import kxd.engine.dao.SqlParams;
import kxd.remote.scs.beans.BaseInfoCategory;
import kxd.remote.scs.beans.BaseOrg;
import kxd.remote.scs.beans.adinfo.EditedInfo;
import kxd.remote.scs.util.emun.AuditStatus;

public class EditedInfoConverter extends BaseDaoConverter<EditedInfo> implements
		SqlConverter<EditedInfo> {

	public EditedInfoConverter() {

	}

	public EditedInfo doConvert(Object result) {
		Object[] o = (Object[]) result;
		EditedInfo r = new EditedInfo();
		r.setId(Long.valueOf(o[0].toString()));
		r.setOrg(new BaseOrg(Integer.valueOf(o[1].toString()), (String) o[2]));
		BaseInfoCategory a = new BaseInfoCategory(
				Short.valueOf(o[3].toString()));
		a.setInfoCategoryDesp((String) o[4]);
		r.setInfoCategory(a);
		r.setTitle((String) o[5]);
		r.setPublishTime((Date) o[6]);
		r.setSummary((String) o[7]);
		r.setFileName((String) o[8]);
		r.setStartDate((Date) o[9]);
		r.setEndDate((Date) o[10]);
		r.setAuditStatus(AuditStatus.valueOfIntString(o[11]));
		r.setUrl((String) o[12]);
		if (!r.getFileName().startsWith("/"))
			r.setFileName(r.getOrg().getIdString() + "/" + a.getIdString()
					+ "/" + r.getFileName());
		return r;
	}

	private final static String DELETE_SQL = "delete from info where infoid=?1";

	public SqlParams getDeleteSql(EditedInfo o) {
		return new SqlParams(DELETE_SQL, o.getId());
	}

	private final static String INSEERT_SQL = "insert into info(infoid,"
			+ "orgid,infocategoryid,publishdate,title,summary,filename,url)"
			+ " values(?1,?2,?3,?4,?5,?6,?7,?8)";

	public SqlParams getInsertSql(EditedInfo o) {
		return new SqlParams(INSEERT_SQL, o.getId(), o.getOrg().getId(), o
				.getInfoCategory().getId(), o.getPublishTime(), o.getTitle(),
				o.getSummary(), o.getFileName(), o.getUrl());
	}

	private final static String SEQUENCE_STRING = "seq_info";

	public String getSequenceString(EditedInfo o) {
		return SEQUENCE_STRING;
	}

	private final static String UPDATE_SQL = "update info set "
			+ "orgid=?1,infocategoryid=?2,publishdate=?3,"
			+ "title=?4,summary=?5,filename=?6,audited=0,url=?7 where infoid=?8";

	public SqlParams getUpdateSql(EditedInfo o) {
		return new SqlParams(UPDATE_SQL, o.getOrg().getId(), o
				.getInfoCategory().getId(), o.getPublishTime(), o.getTitle(),
				o.getSummary(), o.getFileName(), o.getUrl(), o.getId());
	}

}
