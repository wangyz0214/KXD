package kxd.scs.dao.appdeploy.converters;

import kxd.engine.dao.BaseDaoConverter;
import kxd.engine.dao.SqlConverter;
import kxd.engine.dao.SqlParams;
import kxd.remote.scs.beans.appdeploy.EditedPayItem;

public class EditedPayItemConverter extends BaseDaoConverter<EditedPayItem>
		implements SqlConverter<EditedPayItem> {

	public EditedPayItemConverter() {
	}

	public EditedPayItem doConvert(Object result) {
		Object[] o = (Object[]) result;
		EditedPayItem r = new EditedPayItem();
		r.setId(Short.valueOf(o[0].toString()));
		r.setPayItemDesp(o[1].toString());
		r.setNeedTrade(Integer.valueOf(o[2].toString()) != 0);
		r.setPrice(Long.valueOf(o[3].toString()));
		r.setMemo((String) o[4]);
		return r;
	}

	private final static String DELETE_SQL = "delete from payitem where payitem=?1";

	public SqlParams getDeleteSql(EditedPayItem o) {
		return new SqlParams(DELETE_SQL, o.getId());
	}

	private final static String INSEERT_SQL = "insert into payitem(payitem,"
			+ "payitemdesp,needtrade,price,memo) values(?1,?2,?3,?4,?5)";

	public SqlParams getInsertSql(EditedPayItem o) {
		return new SqlParams(INSEERT_SQL, o.getId(), o.getPayItemDesp(),
				o.isNeedTrade() ? 1 : 0, o.getPrice(), o.getMemo());
	}

	private final static String SEQUENCE_STRING = null;

	public String getSequenceString(EditedPayItem o) {
		return SEQUENCE_STRING;
	}

	private final static String UPDATE_SQL = "update payitem set "
			+ "payitemdesp=?1,needtrade=?2,price=?3,memo=?4 where payitem=?5";

	public SqlParams getUpdateSql(EditedPayItem o) {
		return new SqlParams(UPDATE_SQL, o.getPayItemDesp(),
				o.isNeedTrade() ? 1 : 0, o.getPrice(), o.getMemo(), o.getId());
	}

}
