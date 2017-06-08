package kxd.scs.dao.appdeploy.converters;

import kxd.engine.dao.BaseDaoConverter;
import kxd.engine.dao.SqlConverter;
import kxd.engine.dao.SqlParams;
import kxd.remote.scs.beans.BasePayWay;
import kxd.remote.scs.util.emun.PayWayType;

public class BasePayWayConverter extends BaseDaoConverter<BasePayWay> implements
		SqlConverter<BasePayWay> {

	public BasePayWayConverter() {
	}

	public BasePayWay doConvert(Object result) {
		Object[] o = (Object[]) result;
		BasePayWay r = new BasePayWay();
		r.setId(Short.valueOf(o[0].toString()));
		r.setPayWayDesp(o[1].toString());
		r.setNeedTrade(Integer.valueOf(o[2].toString()) != 0);
		r.setType(PayWayType.valueOfIntString(o[3]));
		return r;
	}

	private final static String DELETE_SQL = "delete from payway where payway=?1";

	public SqlParams getDeleteSql(BasePayWay o) {
		return new SqlParams(DELETE_SQL, o.getId());
	}

	private final static String INSEERT_SQL = "insert into payway(payway,"
			+ "paywaydesp,needtrade,type) values(?1,?2,?3,?4)";

	public SqlParams getInsertSql(BasePayWay o) {
		return new SqlParams(INSEERT_SQL, o.getId(), o.getPayWayDesp(),
				o.isNeedTrade() ? 1 : 0, o.getType().getValue());
	}

	private final static String SEQUENCE_STRING = null;

	public String getSequenceString(BasePayWay o) {
		return SEQUENCE_STRING;
	}

	private final static String UPDATE_SQL = "update payway set "
			+ "paywaydesp=?1,needtrade=?2,type=?3 where payway=?4";

	public SqlParams getUpdateSql(BasePayWay o) {
		return new SqlParams(UPDATE_SQL, o.getPayWayDesp(), o.isNeedTrade() ? 1
				: 0, o.getType().getValue(), o.getId());
	}

}
