package kxd.scs.dao.appdeploy.converters;

import kxd.engine.dao.BaseDaoConverter;
import kxd.engine.dao.SqlConverter;
import kxd.engine.dao.SqlParams;
import kxd.remote.scs.beans.BaseBusiness;
import kxd.remote.scs.beans.BasePayItem;
import kxd.remote.scs.beans.BasePayWay;
import kxd.remote.scs.beans.BaseTradeCode;
import kxd.remote.scs.beans.appdeploy.EditedTradeCode;

public class EditedTradeCodeConverter extends BaseDaoConverter<EditedTradeCode>
		implements SqlConverter<EditedTradeCode> {

	public EditedTradeCodeConverter() {
	}

	public EditedTradeCode doConvert(Object result) {
		Object[] o = (Object[]) result;
		EditedTradeCode r = new EditedTradeCode();
		r.setId(Integer.valueOf(o[0].toString()));
		r.setTradeCodeDesp(o[1].toString());
		r.setTradeCode(o[2].toString());
		r.setTradeService(o[3].toString());
		r.setBusiness(new BaseBusiness(Integer.valueOf(o[4].toString()),
				(String) o[5]));
		if (o[6] != null)
			r.setPayWay(new BasePayWay(Short.valueOf(o[6].toString()),
					(String) o[7]));
		else
			r.setPayWay(new BasePayWay(null));
		if (o[8] != null)
			r.setPayItem(new BasePayItem(Short.valueOf(o[8].toString()),
					(String) o[9]));
		else
			r.setPayItem(new BasePayItem(null));
		r.setStated(Integer.valueOf(o[10].toString()) != 0);
		r.setLogged(Integer.valueOf(o[11].toString()) != 0);
		if (o[12] != null) {
			r.setStrikeTadeCode(new BaseTradeCode(Integer.valueOf(o[12]
					.toString())));
		} else
			r.setStrikeTadeCode(new BaseTradeCode(null));
		r.setRefundMode(Integer.valueOf(o[13].toString()));
		r.setRedoEnabled(Integer.valueOf(o[14].toString()) != 0);
		r.setCancelRefundMode(Integer.valueOf(o[15].toString()));
		return r;
	}

	private final static String DELETE_SQL = "delete from tradecode where tradecodeid=?1";

	public SqlParams getDeleteSql(EditedTradeCode o) {
		return new SqlParams(DELETE_SQL, o.getId());
	}

	private final static String INSEERT_SQL = "insert into tradecode (tradecodeid,"
			+ "tradecodedesp,tradecode,tradeservice,stated,logged"
			+ ",striketadecodeid,businessid,payitem,payway,refund_mode,redo_mode,cancel_refund_mode) "
			+ "values(?1,?2,?3,?4,?5,?6,?7,?8,?9,?10,?11,?12,?13)";

	public SqlParams getInsertSql(EditedTradeCode o) {
		return new SqlParams(INSEERT_SQL, o.getId(), o.getTradeCodeDesp(),
				o.getTradeCode(), o.getTradeService(), o.isStated() ? 1 : 0,
				o.isLogged() ? 1 : 0, o.getStrikeTadeCode().getId(), o
						.getBusiness().getId(), o.getPayItem().getId(), o
						.getPayWay().getId(), o.getRefundMode(),
				o.isRedoEnabled() ? 1 : 0, o.getCancelRefundMode());
	}

	private final static String SEQUENCE_STRING = "seq_tradecode";

	public String getSequenceString(EditedTradeCode o) {
		return SEQUENCE_STRING;
	}

	private final static String UPDATE_SQL = "update tradecode set "
			+ "tradecodedesp=?1,tradecode=?2,tradeservice=?3,stated=?4,logged=?5"
			+ ",striketadecodeid=?6,businessid=?7,payitem=?8,payway=?9,"
			+ "refund_mode=?10,redo_mode=?11,cancel_refund_mode=?12 where tradecodeid=?13";

	public SqlParams getUpdateSql(EditedTradeCode o) {
		return new SqlParams(UPDATE_SQL, o.getTradeCodeDesp(),
				o.getTradeCode(), o.getTradeService(), o.isStated() ? 1 : 0,
				o.isLogged() ? 1 : 0, o.getStrikeTadeCode().getId(), o
						.getBusiness().getId(), o.getPayItem().getId(), o
						.getPayWay().getId(), o.getRefundMode(),
				o.isRedoEnabled() ? 1 : 0, o.getCancelRefundMode(), o.getId());
	}

}
