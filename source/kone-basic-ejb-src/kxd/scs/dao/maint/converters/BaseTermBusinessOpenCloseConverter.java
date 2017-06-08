package kxd.scs.dao.maint.converters;

import java.util.Date;

import kxd.engine.dao.BaseDaoConverter;
import kxd.engine.dao.SqlConverter;
import kxd.engine.dao.SqlParams;
import kxd.remote.scs.beans.BaseTermBusinessOpenClose;
import kxd.remote.scs.util.emun.BusinessOpenCloseMode;

public class BaseTermBusinessOpenCloseConverter extends
		BaseDaoConverter<BaseTermBusinessOpenClose> implements
		SqlConverter<BaseTermBusinessOpenClose> {

	public BaseTermBusinessOpenCloseConverter() {
	}

	public BaseTermBusinessOpenClose doConvert(Object result) {
		Object[] o = (Object[]) result;
		BaseTermBusinessOpenClose r = new BaseTermBusinessOpenClose();
		r.setId(Long.valueOf(o[0].toString()));
		r.setTermId(Integer.valueOf(o[1].toString()));
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

	private final static String DELETE_SQL = "delete from termbusinessclose where configid=?1";

	public SqlParams getDeleteSql(BaseTermBusinessOpenClose o) {
		return new SqlParams(DELETE_SQL, o.getId());
	}

	private final static String INSEERT_SQL = "insert into termbusinessclose(configid,"
			+ "termid,startdate,enddate,opentimes,openmode,businesscategroyids,"
			+ "businessids,reason,payways,payitems) values(?1,?2,?3,?4,?5,?6,?7,?8,?9,?10,?11)";

	public SqlParams getInsertSql(BaseTermBusinessOpenClose o) {
		return new SqlParams(INSEERT_SQL, o.getId(), o.getTermId(),
				o.getStartDate(), o.getEndDate(), o.getOpenTimes(), o
						.getOpenMode().getValue(), o.getBusinessCategoryIds(),
				o.getBusinessIds(), o.getReason(), o.getPayWays(),
				o.getPayItems());
	}

	private final static String SEQUENCE_STRING = "seq_termbusinessclose";

	public String getSequenceString(BaseTermBusinessOpenClose o) {
		return SEQUENCE_STRING;
	}

	private final static String UPDATE_SQL = "update termbusinessclose set "
			+ "termid=?1,startdate=?2,enddate=?3,opentimes=?4,openmode=?5,"
			+ "businesscategroyids=?6,businessids=?7,reason=?8,payitems=?9,payways=?10 where configid=?11";

	public SqlParams getUpdateSql(BaseTermBusinessOpenClose o) {
		return new SqlParams(UPDATE_SQL, o.getTermId(), o.getStartDate(),
				o.getEndDate(), o.getOpenTimes(), o.getOpenMode().getValue(),
				o.getBusinessCategoryIds(), o.getBusinessIds(), o.getReason(),
				o.getPayItems(), o.getPayWays(), o.getId());
	}
}
