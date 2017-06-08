package kxd.scs.dao.term.converters;

import kxd.engine.dao.BaseDaoConverter;
import kxd.engine.dao.SqlConverter;
import kxd.engine.dao.SqlParams;
import kxd.remote.scs.beans.device.EditedBankTerm;

public class EditedBankTermConverter extends BaseDaoConverter<EditedBankTerm>
		implements SqlConverter<EditedBankTerm> {

	public EditedBankTermConverter() {
	}

	
	public EditedBankTerm doConvert(Object result) {
		Object[] o = (Object[]) result;
		EditedBankTerm r = new EditedBankTerm();
		r.setId(Integer.valueOf(o[0].toString()));
		r.setBankTermCode((String) o[1]);
		r.setBankTermDesp((String) o[2]);
		r.setMerchantAccount((String) o[3]);
		r.setExtField((String) o[4]);
		return r;
	}

	private final static String DELETE_SQL = "delete from bankterm where banktermid=?1";

	
	public SqlParams getDeleteSql(EditedBankTerm o) {
		return new SqlParams(DELETE_SQL, o.getId());
	}

	private final static String INSEERT_SQL = "insert into bankterm (banktermid,"
			+ "banktermcode,banktermdesp,merchantaccount,extfield) values(?1,?2,"
			+ "?3,?4,?5)";

	
	public SqlParams getInsertSql(EditedBankTerm o) {
		return new SqlParams(INSEERT_SQL, o.getId(), o.getBankTermCode(), o
				.getBankTermDesp(), o.getMerchantAccount(), o.getExtField());
	}

	private final static String SEQUENCE_STRING = "seq_bankterm";

	
	public String getSequenceString(EditedBankTerm o) {
		return SEQUENCE_STRING;
	}

	private final static String UPDATE_SQL = "update bankterm set "
			+ "banktermcode=?1,banktermdesp=?2,merchantaccount=?3,extfield=?4"
			+ " where banktermid=?5";

	
	public SqlParams getUpdateSql(EditedBankTerm o) {
		return new SqlParams(UPDATE_SQL, o.getBankTermCode(), o
				.getBankTermDesp(), o.getMerchantAccount(), o.getExtField(), o
				.getId());
	}
}
