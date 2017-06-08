package kxd.scs.dao.invoice;

import java.util.Iterator;
import java.util.List;

import kxd.engine.dao.Dao;
import kxd.engine.scs.invoice.InvoiceHelper;
import kxd.remote.scs.beans.invoice.BaseInvoiceTemplate;
import kxd.remote.scs.beans.invoice.EditedInvoiceTemplate;
import kxd.remote.scs.util.AppException;
import kxd.remote.scs.util.QueryResult;
import kxd.scs.dao.BaseDao;
import kxd.scs.dao.invoice.converters.BaseInvoiceTemplateConverter;
import kxd.scs.dao.invoice.converters.EditInvoiceTemplateConverter;

public class InvoiceTemplateDao extends BaseDao {
	private final static String tableName = "INVOICE_TEMPLATE";
	private final static String fieldDefs = "a.TEMPLATEID,a.TEMPLATEDESP,a.TEMPLATECODE,a.TEMPLATECONTENT";

	final public static String SQL_QUERY_CACHED_INVOICETEMPLATE_BASIC = "select a.templateid,a.templatedesp,a.templatecode from "
			+ tableName + " a";
	final public static String SQL_QUERY_ALL_CACHED_INVOICETEMPLATE = "select a.templateid,a.templatedesp,a.templatecode,a.templatecontent,a.extdata0,a.extdata1 "
			+ "  from " + tableName + " a order by a.templateid";
	final public static String SQL_QUERY_ALL_INVOICETEMPLATE_ID = "select a.templateid from "
			+ tableName + " a order by a.templateid";
	final public static String SQL_QUERY_CACHED_INVOICETEMPLATE_BYID = "select  a.templateid,a.templatedesp,a.templatecode,a.templatecontent,a.extdata0,a.extdata1 "
			+ "  from " + tableName + " a where a.templateid=?1";

	final public static BaseInvoiceTemplateConverter baseconverter = new BaseInvoiceTemplateConverter();
	final public static EditInvoiceTemplateConverter converter = new EditInvoiceTemplateConverter();

	static private String jionWhereSql(String keyword) {
		String qlString = " from " + tableName + " a ";
		String whereString = "";
		if (keyword != null) {
			keyword = keyword.trim();
			if (keyword.length() > 0) {
				whereString += "(a.templatedesp like '" + keyword
						+ "%' or a.templatecode like '" + keyword + "%')";
			}
		}
		if (whereString.length() > 0)
			whereString = " where " + whereString;
		return qlString + whereString + " order by templateid";
	}

	static public EditedInvoiceTemplate find(Dao dao, int invoiceTemplateId) {
		String sql = "select " + fieldDefs + " from " + tableName + " a "
				+ "where templateid=?1";
		Iterator<EditedInvoiceTemplate> it = dao.query(converter, sql,
				invoiceTemplateId).iterator();
		if (it.hasNext())
			return it.next();
		else
			return null;
	}

	static public boolean invoiceTemplateCodeExists(Dao dao, String templateCode) {
		return !dao.query(
				"select * from " + tableName + " a where a.templatecode=?1",
				templateCode).isEmpty();
	}

	static public boolean hasInvoiceTemplate(Dao dao, int templateId) {
		return !dao.queryPage(
				"select * from invoice_config where templateid=?1", 0, 1,
				templateId).isEmpty();
	}

	static public int add(Dao dao, long loginUserId,
			EditedInvoiceTemplate invoiceTemplate) {
		if (invoiceTemplateCodeExists(dao, invoiceTemplate.getTemplateCode()))
			throw new AppException("发票模板编码["
					+ invoiceTemplate.getTemplateCode() + "]已被占用.");

		dao.insert(invoiceTemplate, converter);
		try {
			InvoiceHelper.templateRemoved(invoiceTemplate.getId());
		} catch (Exception e) {
			throw new AppException(e);
		}
		addUserLog(dao, loginUserId,
				"添加发票模板[" + invoiceTemplate.getTemplateDesp() + "]");
		return invoiceTemplate.getId();
	}

	static public void delete(Dao dao, long loginUserId,
			Integer[] invoiceTemplateIds) {
		for (int i = 0; i < invoiceTemplateIds.length; i++) {
			EditedInvoiceTemplate u = find(dao, invoiceTemplateIds[i]);
			if (u == null)
				throw new AppException("要删除的发票模板[" + invoiceTemplateIds[i]
						+ "]不存在.");

			if (hasInvoiceTemplate(dao, invoiceTemplateIds[i])) {
				throw new AppException("要删除的发票模板[" + invoiceTemplateIds[i]
						+ "]已有机构使用.");
			}
			dao.delete(u, converter);
			try {
				InvoiceHelper.templateRemoved(u.getId());
			} catch (Exception e) {
				throw new AppException(e);
			}
			addUserLog(dao, loginUserId, "删除发票模板[" + u.getIdString() + "]");
		}
	}

	static public void edit(Dao dao, long loginUserId,
			EditedInvoiceTemplate invoiceTemplate) {
		EditedInvoiceTemplate u = find(dao, invoiceTemplate.getId());
		if (u == null)
			throw new AppException("要编辑的发票模板[" + invoiceTemplate.getId()
					+ "]不存在.");

		if (!invoiceTemplate.getTemplateCode().equals(u.getTemplateCode())
				&& invoiceTemplateCodeExists(dao, u.getTemplateCode()))
			throw new AppException("发票模板编码["
					+ invoiceTemplate.getTemplateCode() + "]已被占用.");

		dao.update(invoiceTemplate, converter);
		try {
			InvoiceHelper.templateEdited(invoiceTemplate.getId());
		} catch (Exception e) {
			throw new AppException(e);
		}
		addUserLog(dao, loginUserId, "编辑发票模板[" + invoiceTemplate.getId() + "]");
	}

	static public QueryResult<EditedInvoiceTemplate> queryInvoiceTemplate(
			Dao dao, boolean queryCount, long loginUserId,
			String filterContent, int firstResult, int maxResults) {
		return query(dao, converter, queryCount, fieldDefs,
				jionWhereSql(filterContent), firstResult, maxResults);
	}

	static public List<BaseInvoiceTemplate> getInvoiceTemplateList(Dao dao,
			long loginUserId, String keyword) {
		return dao.query(baseconverter, "select templateid,templatedesp"
				+ jionWhereSql(keyword));
	}

}
