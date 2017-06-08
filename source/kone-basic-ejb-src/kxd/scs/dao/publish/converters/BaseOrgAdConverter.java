package kxd.scs.dao.publish.converters;

import kxd.engine.dao.BaseDaoConverter;
import kxd.engine.dao.SqlConverter;
import kxd.engine.dao.SqlParams;
import kxd.remote.scs.beans.BaseAdCategory;
import kxd.remote.scs.beans.BaseOrg;
import kxd.remote.scs.beans.BaseOrgAd;
import kxd.remote.scs.util.emun.AdPriority;
import kxd.remote.scs.util.emun.AdStoreType;
import kxd.remote.scs.util.emun.AuditStatus;
import kxd.util.DataUnit;

public class BaseOrgAdConverter extends BaseDaoConverter<BaseOrgAd> implements
		SqlConverter<BaseOrgAd> {

	public BaseOrgAdConverter() {

	}

	public BaseOrgAd doConvert(Object result) {
		Object[] o = (Object[]) result;
		BaseOrgAd r = new BaseOrgAd();
		r.setId(Integer.valueOf(o[0].toString()));
		r.setOrg(new BaseOrg(Integer.valueOf(o[1].toString()), (String) o[2]));
		BaseAdCategory a = new BaseAdCategory(Short.valueOf(o[3].toString()));
		a.setAdCategoryDesp((String) o[4]);
		r.setAdCategory(a);
		r.setAdDesp((String) o[5]);
		r.setStartDate(DataUnit.parseDateTimeDef((String) o[6],
				"yyyy-MM-dd HH:mm:ss", null));
		r.setEndDate(DataUnit.parseDateTimeDef((String) o[7],
				"yyyy-MM-dd HH:mm:ss", null));
		r.setFileName((String) o[8]);
		r.setPlayTimes(Integer.valueOf(o[9].toString()));
		r.setDuration(Integer.valueOf(o[10].toString()));
		r.setAuditStatus(AuditStatus.valueOfIntString(o[11]));
		r.setUrl((String) o[12]);
		r.setUploadComplete(Integer.valueOf(o[13].toString()) != 0);
		r.setStoreType(AdStoreType.valueOfIntString(o[14]));
		r.setPriority(AdPriority.valueOfIntString(o[15]));
		if (r.getStoreType().equals(AdStoreType.SERVER)) {
			if (!r.getFileName().startsWith("/"))
				r.setFileName(r.getOrg().getIdString() + "/" + a.getIdString()
						+ "/" + r.getFileName());
		} else
			r.setUploadComplete(true);
		return r;
	}

	private final static String DELETE_SQL = "delete from orgad where orgadid=?1";

	public SqlParams getDeleteSql(BaseOrgAd o) {
		return new SqlParams(DELETE_SQL, o.getId());
	}

	private final static String INSEERT_SQL = "insert into orgad(orgadid,"
			+ "orgid,adcategoryid,addesp,startdate,enddate,"
			+ "filename,playtimes,duration,url,uploadcomplete,storetype,priority)"
			+ " values(?1,?2,?3,?4,?5,?6,?7,?8,?9,?10,?11,?12,?13)";

	public SqlParams getInsertSql(BaseOrgAd o) {
		return new SqlParams(INSEERT_SQL, o.getId(), o.getOrg().getId(), o
				.getAdCategory().getId(), o.getAdDesp(), o.getStartDate(),
				o.getEndDate(), o.getFileName(), o.getPlayTimes(),
				o.getDuration(), o.getUrl(), o.isUploadComplete(), o
						.getStoreType().getValue(), o.getPriority().getValue());
	}

	private final static String SEQUENCE_STRING = "seq_orgad";

	public String getSequenceString(BaseOrgAd o) {
		return SEQUENCE_STRING;
	}

	private final static String UPDATE_SQL = "update orgad set "
			+ "orgid=?1,adcategoryid=?2,addesp=?3,startdate=?4,enddate=?5,"
			+ "filename=?6,playtimes=?7,duration=?8,audited=0,url=?9,uploadcomplete=?10,"
			+ "storetype=?11,priority=?12 where orgadid=?13";

	public SqlParams getUpdateSql(BaseOrgAd o) {
		return new SqlParams(UPDATE_SQL, o.getOrg().getId(), o.getAdCategory()
				.getId(), o.getAdDesp(), o.getStartDate(), o.getEndDate(),
				o.getFileName(), o.getPlayTimes(), o.getDuration(), o.getUrl(),
				o.isUploadComplete(), o.getStoreType().getValue(), o
						.getPriority().getValue(), o.getId());
	}

}
