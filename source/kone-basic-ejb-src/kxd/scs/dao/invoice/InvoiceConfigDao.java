package kxd.scs.dao.invoice;

import java.util.Iterator;
import java.util.List;

import kxd.engine.dao.Dao;
import kxd.engine.scs.invoice.InvoiceHelper;
import kxd.remote.scs.beans.invoice.BaseInvoiceConfig;
import kxd.remote.scs.beans.invoice.EditedInvoiceConfig;
import kxd.remote.scs.util.AppException;
import kxd.remote.scs.util.QueryResult;
import kxd.scs.dao.BaseDao;
import kxd.scs.dao.invoice.converters.BaseInvoiceConfigConverter;
import kxd.scs.dao.invoice.converters.EditedInvoiceConfigConverter;
import kxd.scs.dao.right.OrgDao;

public class InvoiceConfigDao extends BaseDao {
	// final public static String SQL_QUERY_ALL_INVOICE_ID =
	// "select a.configid from invoice_config a order by a.configid";
	final public static String SQL_QUERY_CACHED_INVOICECONFIG_BASIC = "select a.configid,a.configdesp,a.configcode from invoice_config a";
	final public static String SQL_QUERY_ALL_CACHED_INVOICECONFIG = "select a.configid,a.configdesp,a.configcode,a.orgid,a.invoicetype,a.templateid,a.taxflag,a.awayflag,a.alertcount,a.logged,a.extdata0,a.extdata1 "
			+ "from invoice_config a order by a.configid";
	final public static String SQL_QUERY_CACHED_INVOICECONFIG_BYID = SQL_QUERY_ALL_CACHED_INVOICECONFIG
			+ " where a.orgid=?1 and a.invoicetype=?2";

	final public static BaseInvoiceConfigConverter baseConverter = new BaseInvoiceConfigConverter();
	final public static EditedInvoiceConfigConverter converter = new EditedInvoiceConfigConverter();

	final private static String fieldDefs = "a.configid,a.configdesp,a.configcode,a.orgid,orgname,a.invoicetype,a.templateid,templatedesp,a.taxflag,a.awayflag,a.alertcount,a.logged,a.extdata0,a.extdata1 ";
	final private static String tableName = "invoice_config";

	static private String jionWhereSql(boolean isDetail, String where, Dao dao,
			int orgId, String keyword) {
		String qlString = " from " + tableName + " a ";
		String whereString = "";
		if (isDetail) {
			qlString += ",org b,invoice_template c";
			whereString = "a.orgid=b.orgid and a.templateid=c.templateid and "
					+ OrgDao.getOrgFilterString(dao, orgId, "b");
		}
		if (keyword != null) {
			keyword = keyword.trim();
			if (keyword.length() > 0) {
				if (whereString.length() > 0)
					whereString += " and ";
				whereString += "(a.configdesp like '%" + keyword
						+ "%' or a.configcode like '%" + keyword + "%')";
			}
		}

		if (whereString.length() > 0)
			whereString = " where " + whereString;
		return qlString + whereString + " order by a.configid";
	}

	static public List<BaseInvoiceConfig> getInvoiceConfigList(Dao dao,
			long loginUserId, String keyword) {
		return dao.query(baseConverter, "select configid,configdesp"
				+ jionWhereSql(false, null, null, 0, keyword));
	}

	static public boolean invoiceConfigCodeExists(Dao dao, String configCode) {
		return !dao.query(
				"select * from " + tableName + " a where a.configcode=?1",
				configCode).isEmpty();
	}

	static public boolean invoiceConfigExists(Dao dao, Integer orgid,
			Integer invoiceType) {
		return !dao.query(
				"select * from " + tableName
						+ " a where a.orgid=?1 and a.invoicetype=?2", orgid,
				invoiceType).isEmpty();
	}

	static public EditedInvoiceConfig find(Dao dao, int configId) {
		String sql = "select "
				+ fieldDefs
				+ " from "
				+ tableName
				+ " a,org b,invoice_template c "
				+ "where configid=?1 and a.orgid=b.orgid and a.templateid=c.templateid";
		Iterator<EditedInvoiceConfig> it = dao.query(converter, sql, configId)
				.iterator();
		if (it.hasNext())
			return it.next();
		else
			return null;
	}

	static public EditedInvoiceConfig find(Dao dao, int orgId, int type) {
		String sql = "select "
				+ fieldDefs
				+ " from "
				+ tableName
				+ " a,org b,invoice_template c "
				+ "where a.orgid=?1 and a.invoicetype=?2 and a.orgid=b.orgid and a.templateid=c.templateid";
		Iterator<EditedInvoiceConfig> it = dao.query(converter, sql, orgId,
				type).iterator();
		if (it.hasNext())
			return it.next();
		else
			return null;
	}

	static public int add(Dao dao, long loginUserId,
			EditedInvoiceConfig invoiceConfig) {
		if (invoiceConfigCodeExists(dao, invoiceConfig.getConfigCode()))
			throw new AppException("本机构下该类型的发票已经配置.");
		if (invoiceConfigExists(dao, invoiceConfig.getOrg().getOrgId(),
				invoiceConfig.getInvoiceType()))
			throw new AppException("本机构下该类型的发票已经配置.");

		dao.insert(invoiceConfig, converter);
		// 确保从数据库装入最新的数据
		try {
			InvoiceHelper.configAdded(invoiceConfig.getOrg().getId(),
					invoiceConfig.getInvoiceType());
		} catch (Exception e) {
			throw new AppException(e);
		}
		addUserLog(dao, loginUserId, "添加发票配置[" + invoiceConfig.getConfigCode()
				+ "]");
		return invoiceConfig.getConfigId();
	}

	static public void delete(Dao dao, long loginUserId,
			Integer[] invoiceConfigId) {
		for (int i = 0; i < invoiceConfigId.length; i++) {
			EditedInvoiceConfig u = find(dao, invoiceConfigId[i]);
			if (u == null)
				throw new AppException("要删除的发票配置[" + invoiceConfigId + "]不存在.");

			dao.delete(u, converter);
			// 确保从数据库装入最新的数据
			try {
				InvoiceHelper.configRemoved(u.getOrg().getId(),
						u.getInvoiceType());
			} catch (Exception e) {
				throw new AppException(e);
			}
			addUserLog(dao, loginUserId, "删除发票配置[" + u.getConfigId() + "]");
		}
	}

	static public void edit(Dao dao, long loginUserId,
			EditedInvoiceConfig invoiceConfig) {
		EditedInvoiceConfig u = find(dao, invoiceConfig.getConfigId());
		if (u == null)
			throw new AppException("要编辑的发票配置[" + invoiceConfig.getConfigId()
					+ "]不存在.");

		if (!invoiceConfig.getConfigCode().equals(u.getConfigCode())
				&& invoiceConfigCodeExists(dao, invoiceConfig.getConfigCode()))
			throw new AppException("发票配置编码[" + invoiceConfig.getConfigCode()
					+ "]已被占用.");

		if (invoiceConfig.getInvoiceType() != u.getInvoiceType()
				&& invoiceConfigExists(dao, invoiceConfig.getOrg().getOrgId(),
						invoiceConfig.getInvoiceType()))
			throw new AppException("本机构下该类型的发票已经配置.");

		dao.update(invoiceConfig, converter);
		// 确保从数据库装入最新的数据 key(机构ID+发票类型)
		try {
			InvoiceHelper.configEdited(invoiceConfig.getOrg().getId(),
					invoiceConfig.getInvoiceType());
		} catch (Exception e) {
			throw new AppException(e);
		}
		addUserLog(dao, loginUserId, "编辑发票配置[" + invoiceConfig.getConfigId()
				+ "]");
	}

	static public QueryResult<EditedInvoiceConfig> queryInvoiceConfig(Dao dao,
			int orgId, boolean queryCount, long loginUserId, Integer filter,
			String filterContent, int firstResult, int maxResults) {
		return query(dao, converter, queryCount, fieldDefs,
				jionWhereSql(true, null, dao, orgId, filterContent),
				firstResult, maxResults);
	}
}
