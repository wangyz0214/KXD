package kxd.scs.dao.invoice.converters;

import java.sql.Clob;

import kxd.engine.dao.BaseDaoConverter;
import kxd.engine.dao.SqlConverter;
import kxd.engine.dao.SqlParams;
import kxd.remote.scs.beans.invoice.EditedInvoiceTemplate;
import kxd.remote.scs.util.AppException;

public class EditInvoiceTemplateConverter extends
		BaseDaoConverter<EditedInvoiceTemplate> implements
		SqlConverter<EditedInvoiceTemplate> {

	@Override
	protected EditedInvoiceTemplate doConvert(Object result) {
		Object[] o = (Object[]) result;
		EditedInvoiceTemplate r = new EditedInvoiceTemplate();
		r.setId(Integer.valueOf(o[0].toString()));
		r.setTemplateDesp(o[1].toString());
		r.setTemplateCode(o[2].toString());
		Clob clob = (Clob) o[3];
		try {
			int len = (int) clob.length();
			r.setTemplateContent(clob.getSubString(1, len));
		} catch (Exception e) {
			throw new AppException(e);
		}
		/*
		 * Clob clob=null; try { clob = (Clob)o[3]; } catch (Exception e) {
		 * e.printStackTrace(); } r.setTemplateContent(clob);
		 */
		return r;
	}

	private final static String DELETE_SQL = "delete from INVOICE_TEMPLATE where templateid=?1";

	public SqlParams getDeleteSql(EditedInvoiceTemplate o) {
		return new SqlParams(DELETE_SQL, o.getId());
	}

	private final static String INSEERT_SQL = "insert into INVOICE_TEMPLATE(templateid,templatedesp,templatecode,templatecontent) values(?1,?2,?3,?4)";

	public SqlParams getInsertSql(EditedInvoiceTemplate o) {
		return new SqlParams(INSEERT_SQL, o.getId(), o.getTemplateDesp(),
				o.getTemplateCode(), o.getTemplateContent());
	}

	private final static String SEQUENCE_STRING = "SEQ_INVOICE_TEMPLATE";

	public String getSequenceString(EditedInvoiceTemplate o) {
		return SEQUENCE_STRING;
	}

	private final static String UPDATE_SQL = "update invoice_template set "
			+ "templatecode=?1,templatedesp=?2,templatecontent=?3 where templateid=?4";

	public SqlParams getUpdateSql(EditedInvoiceTemplate o) {
		return new SqlParams(UPDATE_SQL, o.getTemplateCode(),
				o.getTemplateDesp(), o.getTemplateContent(), o.getId());
	}

}
