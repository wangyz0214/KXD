package kxd.scs.dao.maint.converters;

import java.util.Date;

import kxd.engine.dao.BaseDaoConverter;
import kxd.engine.dao.SqlConverter;
import kxd.engine.dao.SqlParams;
import kxd.remote.scs.beans.BaseOrgBusinessOpenClose;
import kxd.remote.scs.util.emun.BusinessOpenCloseMode;

public class BaseOrgBusinessOpenCloseConverter extends
		BaseDaoConverter<BaseOrgBusinessOpenClose> implements
		SqlConverter<BaseOrgBusinessOpenClose> {

	public BaseOrgBusinessOpenCloseConverter() {
	}

	public BaseOrgBusinessOpenClose doConvert(Object result) {
		Object[] o = (Object[]) result;
		BaseOrgBusinessOpenClose r = new BaseOrgBusinessOpenClose();
		r.setId(Long.valueOf(o[0].toString()));
		r.setOrgId(Integer.valueOf(o[1].toString()));
		r.setBusinessCategoryIds((String) o[2]);
		r.setBusinessIds((String) o[3]);
		r.setStartDate((Date) o[4]);
		r.setEndDate((Date) o[5]);
		r.setOpenMode(BusinessOpenCloseMode.valueOfIntString(o[6]));
		r.setOpenTimes((String) o[7]);
		r.setReason((String) o[8]);
		r.setPayItems((String) o[9]);
		r.setPayWays((String) o[10]);
		return r;
	}

	private final static String DELETE_SQL = "delete from orgbusinessclose where configid=?1";

	public SqlParams getDeleteSql(BaseOrgBusinessOpenClose o) {
		return new SqlParams(DELETE_SQL, o.getId());
	}

	private final static String INSEERT_SQL = "insert into orgbusinessclose(configid,"
			+ "orgid,startdate,enddate,opentimes,openmode,businesscategroyids,"
			+ "businessids,reason,payways,payitems) values(?1,?2,?3,?4,?5,?6,?7,?8,?9,?10,?11)";

	public SqlParams getInsertSql(BaseOrgBusinessOpenClose o) {
		return new SqlParams(INSEERT_SQL, o.getId(), o.getOrgId(),
				o.getStartDate(), o.getEndDate(), o.getOpenTimes(), o
						.getOpenMode().getValue(), o.getBusinessCategoryIds(),
				o.getBusinessIds(), o.getReason(), o.getPayWays(),
				o.getPayItems());
	}

	private final static String SEQUENCE_STRING = "seq_orgbusinessclose";

	public String getSequenceString(BaseOrgBusinessOpenClose o) {
		return SEQUENCE_STRING;
	}

	private final static String UPDATE_SQL = "update orgbusinessclose set "
			+ "orgid=?1,startdate=?2,enddate=?3,opentimes=?4,openmode=?5,"
			+ "businesscategroyids=?6,businessids=?7,reason=?8,payitems=?9,payways=?10 where configid=?11";

	public SqlParams getUpdateSql(BaseOrgBusinessOpenClose o) {
		return new SqlParams(UPDATE_SQL, o.getOrgId(), o.getStartDate(),
				o.getEndDate(), o.getOpenTimes(), o.getOpenMode().getValue(),
				o.getBusinessCategoryIds(), o.getBusinessIds(), o.getReason(),
				o.getPayItems(), o.getPayWays(), o.getId());
	}
}
