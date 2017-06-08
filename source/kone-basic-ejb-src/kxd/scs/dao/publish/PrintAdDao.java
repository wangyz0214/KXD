package kxd.scs.dao.publish;

import java.util.Iterator;
import java.util.List;

import kxd.engine.dao.Dao;
import kxd.engine.helper.AdminHelper;
import kxd.remote.scs.beans.BasePrintAd;
import kxd.remote.scs.beans.adinfo.EditedPrintAd;
import kxd.remote.scs.util.AppException;
import kxd.remote.scs.util.QueryResult;
import kxd.remote.scs.util.emun.AuditStatus;
import kxd.scs.dao.BaseDao;
import kxd.scs.dao.publish.converters.BasePrintAdConverter;
import kxd.scs.dao.publish.converters.EditedPrintAdConverter;
import kxd.scs.dao.right.OrgDao;

public class PrintAdDao extends BaseDao {
	final public static BasePrintAdConverter baseConverter = new BasePrintAdConverter();
	final public static EditedPrintAdConverter converter = new EditedPrintAdConverter();
	final private static String baseFieldDefs = "a.printadid,a.orgid,orgfullname,"
			+ "a.printadcategoryid,printadcategorydesp,a.audited";
	final private static String fieldDefs = "a.printadid,a.orgid,orgfullname,"
			+ "a.printadcategoryid,printadcategorydesp,a.content,a.audited";
	final private static String tableName = "orgprintad";

	static public EditedPrintAd find(Dao dao, int printAdId) {
		String sql = "select " + fieldDefs + " from orgprintad a,"
				+ "org b,printadcategory c "
				+ "where printadid=?1 and a.orgid=b.orgid and "
				+ "a.printadcategoryid=c.printadcategoryid";
		Iterator<EditedPrintAd> it = dao.query(converter, sql, printAdId)
				.iterator();
		if (it.hasNext())
			return it.next();
		else
			return null;
	}

	static public int add(Dao dao, long loginUserId, EditedPrintAd printAd) {
		if (printAdExists(dao, printAd.getOrg().getId(), printAd
				.getAdCategory().getId()))
			throw new AppException("机构[id=" + printAd.getOrg().getIdString()
					+ "]已经包含打印广告[分类ID=" + printAd.getAdCategory().getId() + "]");
		dao.insert(printAd, converter);
		addUserLog(dao, loginUserId, "添加机构打印广告[分类ID="
				+ printAd.getAdCategory().getIdString() + "]");
		AdminHelper.printAdChanged(printAd.getOrg().getOrgId(), printAd
				.getAdCategory().getPrintAdCategoryId());
		return printAd.getId();
	}

	static public boolean printAdExists(Dao dao, int orgId,
			int printAdCategoryId) {
		return !dao
				.queryPage(
						"select * from orgprintad where orgid=?1 and printadcategoryid=?2",
						0, 1, orgId, printAdCategoryId).isEmpty();
	}

	/**
	 * 上线申请
	 * 
	 * @param dao
	 * @param loginUserId
	 * @param printAdId
	 */
	static public void onlineReq(Dao dao, long loginUserId, int printAdId,
			boolean isCancel) {
		EditedPrintAd u = find(dao, printAdId);
		if (u == null)
			throw new AppException("申请上线的打印广告[" + printAdId + "]不存在.");
		if (isCancel) {
			switch (u.getAuditStatus()) {
			case ONLINE_REQ:
				dao.execute("update orgprintad set audited=?1"
						+ " where printadid=?2",
						AuditStatus.OFFLINE.getValue(), printAdId);
				addUserLog(dao, loginUserId, "撤消打印广告[" + u.getId() + "]上线申请");
				break;
			default:
				throw new AppException("该广告没有申请上线.");
			}
		} else {
			switch (u.getAuditStatus()) {
			case OFFLINE:
				dao.execute("update orgprintad set audited=?1"
						+ " where printadid=?2",
						AuditStatus.ONLINE_REQ.getValue(), printAdId);
				addUserLog(dao, loginUserId, "打印广告[" + u.getId() + "]上线申请");
				break;
			default:
				throw new AppException("该广告没在下线状态.");
			}
		}
		AdminHelper.printAdChanged(u.getOrg().getOrgId(), u.getAdCategory()
				.getPrintAdCategoryId());
	}

	/**
	 * 下线申请
	 * 
	 * @param dao
	 * @param loginUserId
	 * @param printAdId
	 */
	static public void offlineReq(Dao dao, long loginUserId, int printAdId,
			boolean isCancel) {
		EditedPrintAd u = find(dao, printAdId);
		if (u == null)
			throw new AppException("申请下线的打印广告[" + printAdId + "]不存在.");
		if (isCancel) {
			switch (u.getAuditStatus()) {
			case OFFLINE_REQ:
				dao.execute("update orgprintad set audited=?1"
						+ " where printadid=?2", AuditStatus.ONLINE.getValue(),
						printAdId);
				addUserLog(dao, loginUserId, "撤消打印广告[" + u.getId() + "]下线申请");
				break;
			default:
				throw new AppException("该广告没有申请下线.");
			}
		} else {
			switch (u.getAuditStatus()) {
			case ONLINE:
				dao.execute("update orgprintad set audited=?1"
						+ " where printadid=?2",
						AuditStatus.OFFLINE_REQ.getValue(), printAdId);
				addUserLog(dao, loginUserId, "打印广告[" + u.getId() + "]下线申请");
				break;
			default:
				throw new AppException("该广告没在上线状态.");
			}
		}
		AdminHelper.printAdChanged(u.getOrg().getOrgId(), u.getAdCategory()
				.getPrintAdCategoryId());
	}

	/**
	 * 上线审核
	 * 
	 * @param dao
	 * @param loginUserId
	 * @param printAdId
	 *            被审核的广告ID
	 * @param auditOk
	 *            审核是否通过
	 */
	static public void onlineAudit(Dao dao, long loginUserId, int printAdId,
			boolean auditOk) {
		EditedPrintAd u = find(dao, printAdId);
		if (u == null)
			throw new AppException("要审核的打印广告[" + printAdId + "]不存在.");
		switch (u.getAuditStatus()) {
		case ONLINE_REQ:
			dao.execute("update orgprintad set audited=?1"
					+ ",audituserid=?2,audittime=sysdate where printadid=?3",
					(auditOk ? AuditStatus.ONLINE.getValue()
							: AuditStatus.OFFLINE.getValue()), loginUserId,
					printAdId);
			break;
		default:
			throw new AppException("该打印广告没有上线请求.");
		}
		AdminHelper.printAdChanged(u.getOrg().getOrgId(), u.getAdCategory()
				.getPrintAdCategoryId());
		addUserLog(dao, loginUserId, "打印广告[" + u.getId() + "]上线审核");
	}

	/**
	 * 广告下线审核
	 * 
	 * @param dao
	 * @param loginUserId
	 * @param printAdId
	 *            被审核的广告ID
	 * @param auditOk
	 *            审核是否通过
	 */
	static public void offlineAudit(Dao dao, long loginUserId, int printAdId,
			boolean auditOk) {
		EditedPrintAd u = find(dao, printAdId);
		if (u == null)
			throw new AppException("要审核的打印广告[" + printAdId + "]不存在.");
		switch (u.getAuditStatus()) {
		case OFFLINE_REQ:
			dao.execute("update orgprintad set audited=?1"
					+ ",audituserid=?2,audittime=sysdate where printadid=?3",
					(auditOk ? AuditStatus.OFFLINE.getValue()
							: AuditStatus.ONLINE.getValue()), loginUserId,
					printAdId);
			break;
		default:
			throw new AppException("该打印广告没有提交下架审核请求.");
		}
		AdminHelper.printAdChanged(u.getOrg().getOrgId(), u.getAdCategory()
				.getPrintAdCategoryId());
		addUserLog(dao, loginUserId, "审核下架打印广告[" + u.getId() + "]");
	}

	static public void delete(Dao dao, long loginUserId, int printAdId) {
		EditedPrintAd u = find(dao, printAdId);
		if (u == null)
			throw new AppException("要删除的打印广告[" + printAdId + "]不存在.");
		switch (u.getAuditStatus()) {
		case OFFLINE:
			dao.delete(u, converter);
			AdminHelper.printAdChanged(u.getOrg().getOrgId(), u.getAdCategory()
					.getPrintAdCategoryId());
			addUserLog(dao, loginUserId, "删除打印广告[" + u.getId() + "]");
			break;
		default:
			throw new AppException("只有处于下线状态的广告，才能删除.");
		}
	}

	static public void edit(Dao dao, long loginUserId, EditedPrintAd printAd) {
		EditedPrintAd u = find(dao, printAd.getId());
		if (u == null)
			throw new AppException("要编辑的打印广告[" + printAd.getId() + "]不存在.");
		switch (u.getAuditStatus()) {
		case OFFLINE:
			dao.update(printAd, converter);
			AdminHelper.printAdChanged(u.getOrg().getOrgId(), u.getAdCategory()
					.getPrintAdCategoryId());
			addUserLog(dao, loginUserId, "编辑打印广告[" + printAd.getId() + "]");
			break;
		default:
			throw new AppException("只有处于下线状态的广告，才能编辑.");
		}
	}

	static public QueryResult<BasePrintAd> queryPrintAd(Dao dao, int orgId,
			boolean queryCount, long loginUserId, Integer filter,
			String filterContent, int firstResult, int maxResults) {
		String qlString = " from " + tableName + " a,org b,printadcategory c ";
		String whereString = "a.orgid=b.orgid and a.printadcategoryid=c.printadcategoryid and "
				+ OrgDao.getOrgFilterString(dao, orgId, "b");
		if (filterContent != null) {
			filterContent = filterContent.trim();
			if (filterContent.length() > 0) {
				whereString += " and c.printadcategorydesp like '"
						+ filterContent + "%'";
			}
		}
		if (whereString.length() > 0)
			whereString = " where " + whereString;
		return query(dao, baseConverter, queryCount, baseFieldDefs, qlString
				+ whereString + " order by a.printadid", firstResult,
				maxResults);
	}

	static public QueryResult<BasePrintAd> queryNeedAuditPrintAd(Dao dao,
			int orgId, boolean queryCount, long loginUserId, Integer filter,
			String filterContent, int firstResult, int maxResults) {
		String qlString = " from " + tableName + " a,org b,printadcategory c ";
		String whereString = "a.orgid=b.orgid and a.printadcategoryid=c.printadcategoryid and (a.audited="
				+ AuditStatus.OFFLINE_REQ.getValue()
				+ " or a.audited="
				+ AuditStatus.ONLINE_REQ.getValue()
				+ ") and "
				+ OrgDao.getOrgFilterString(dao, orgId, "b");
		if (filterContent != null) {
			filterContent = filterContent.trim();
			if (filterContent.length() > 0) {
				whereString += " and c.printadcategorydesp like '"
						+ filterContent + "%'";
			}
		}
		if (whereString.length() > 0)
			whereString = " where " + whereString;
		return query(dao, baseConverter, queryCount, baseFieldDefs, qlString
				+ whereString + " order by a.printadid", firstResult,
				maxResults);
	}

	static public List<EditedPrintAd> getUsingAdList(Dao dao, int orgId,
			Integer adCategoryId) {
		String whereString = " from " + tableName
				+ " a,org b,printadcategory c ";
		whereString += " where a.orgid=b.orgid and a.printadcategoryid=c.printadcategoryid and "
				+ "a.printadcategoryid="
				+ adCategoryId
				+ " and a.audited>=?1 and a.orgid=" + orgId;
		return dao.query(converter, "select " + fieldDefs + whereString,
				AuditStatus.ONLINE.getValue());
	}

}
