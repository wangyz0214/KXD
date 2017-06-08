package kxd.scs.dao.publish;

import java.util.Iterator;
import java.util.List;

import kxd.engine.dao.Dao;
import kxd.engine.helper.AdminHelper;
import kxd.remote.scs.beans.BaseOrgAd;
import kxd.remote.scs.util.AppException;
import kxd.remote.scs.util.QueryResult;
import kxd.remote.scs.util.emun.AuditStatus;
import kxd.scs.dao.BaseDao;
import kxd.scs.dao.publish.converters.BaseOrgAdConverter;
import kxd.scs.dao.right.OrgDao;
import kxd.util.KeyValue;

public class OrgAdDao extends BaseDao {
	final public static BaseOrgAdConverter baseConverter = new BaseOrgAdConverter();
	final public static BaseOrgAdConverter converter = new BaseOrgAdConverter();
	final private static String fieldDefs = "a.orgadid,a.orgid,orgfullname,"
			+ "a.adcategoryid,adcategorydesp,a.addesp,to_char(a.startdate,'yyyy-mm-dd hh24:mi:ss')"
			+ ",to_char(a.enddate,'yyyy-mm-dd hh24:mi:ss'),a.filename,a.playtimes,a.duration,a.audited,a.url,"
			+ "a.uploadcomplete,a.storetype,a.priority";
	final private static String tableName = "orgad";

	static private String jionWhereSql(String where, Dao dao, int orgId,
			Integer categoryId, String keyword) {
		String qlString = " from " + tableName + " a,org b,adcategory c";
		String whereString = "a.orgid=b.orgid and a.adcategoryid=c.adcategoryid and "
				+ OrgDao.getOrgFilterString(dao, orgId, "b");
		if (where != null)
			whereString += " and " + where;
		if (keyword != null) {
			keyword = keyword.trim();
			if (keyword.length() > 0) {
				whereString += " and a.addesp like '" + keyword + "%'";
			}
		}
		if (categoryId != null)
			whereString += " and a.adcategoryid=" + categoryId;
		if (whereString.length() > 0)
			whereString = " where " + whereString;
		return qlString + " " + whereString + " order by a.orgadid";
	}

	static public BaseOrgAd find(Dao dao, int orgAdId) {
		String sql = "select " + fieldDefs + " from orgad a,"
				+ "org b,adcategory c "
				+ "where orgadid=?1 and a.orgid=b.orgid and "
				+ "a.adcategoryid=c.adcategoryid";
		Iterator<BaseOrgAd> it = dao.query(converter, sql, orgAdId).iterator();
		if (it.hasNext())
			return it.next();
		else
			return null;
	}

	static public BaseOrgAd add(Dao dao, long loginUserId, BaseOrgAd orgAd) {
		if (orgAdFileExists(dao, orgAd.getOrg().getId(), orgAd.getAdCategory()
				.getId(), orgAd.getFileName()))
			throw new AppException("同一机构[id=" + orgAd.getOrg().getId()
					+ "]和广告分类[id=" + orgAd.getAdCategory().getId()
					+ "]下的广告文件名不能重复");
		dao.insert(orgAd, converter);
		addUserLog(dao, loginUserId, "添加机构机构广告[" + orgAd.getAdDesp() + "]");
		AdminHelper.orgAdChanged(orgAd.getOrg().getOrgId(), orgAd
				.getAdCategory().getAdCategoryId());
		return find(dao, orgAd.getId());
	}

	static public boolean orgAdFileExists(Dao dao, int orgId, int adCategoryId,
			String fileName) {
		return !dao.queryPage(
				"select * from orgad where orgid=?1 and adcategoryid=?2"
						+ " and filename=?3", 0, 1, orgId, adCategoryId,
				fileName).isEmpty();
	}

	static public BaseOrgAd delete(Dao dao, long loginUserId, int orgAdId) {
		BaseOrgAd u = find(dao, orgAdId);
		if (u == null)
			throw new AppException("要删除的机构广告[" + orgAdId + "]不存在.");
		switch (u.getAuditStatus()) {
		case OFFLINE:
			dao.delete(u, converter);
			AdminHelper.orgAdChanged(u.getOrg().getOrgId(), u.getAdCategory()
					.getAdCategoryId());
			addUserLog(dao, loginUserId, "删除机构广告[" + u.getId() + "]");
			return u;
		default:
			throw new AppException("只有下线的广告才能删除");
		}
	}

	static public BaseOrgAd updateUpload(Dao dao, long loginUserId,
			BaseOrgAd orgAd) {
		BaseOrgAd u = find(dao, orgAd.getId());
		if (u == null)
			throw new AppException("要上传的机构广告[" + orgAd.getId() + "]不存在.");
		dao.execute("update orgad set uploadcomplete=?1 where orgadid=?2",
				orgAd.isUploadComplete(), orgAd.getId());
		AdminHelper.orgAdChanged(u.getOrg().getOrgId(), u.getAdCategory()
				.getAdCategoryId());
		return u;
	}

	static public KeyValue<String, BaseOrgAd> edit(Dao dao, long loginUserId,
			BaseOrgAd orgAd) {
		BaseOrgAd u = find(dao, orgAd.getId());
		if (u == null)
			throw new AppException("要编辑的机构广告[" + orgAd.getId() + "]不存在.");
		switch (u.getAuditStatus()) {
		case OFFLINE:
			if (orgAd.getFileName() == null
					|| orgAd.getFileName().trim().isEmpty()) {
				orgAd.setUploadComplete(u.isUploadComplete());
				orgAd.setFileName(u.getFileName());
				orgAd.setUrl(u.getOriginalUrl());
			} else {
				if (u.getOrg().equals(orgAd.getOrg())
						&& u.getAdCategory().equals(orgAd.getAdCategory())) {
					if (!u.getFileName().equals(orgAd.getFileName())) {
						if (orgAdFileExists(dao, orgAd.getOrg().getId(), orgAd
								.getAdCategory().getId(), orgAd.getFileName()))
							throw new AppException("同一机构[id="
									+ orgAd.getOrg().getId() + "]和广告分类[id="
									+ orgAd.getAdCategory().getId()
									+ "]下的广告文件名不能重复");
					}
				}
			}
			dao.update(orgAd, converter);
			AdminHelper.orgAdChanged(u.getOrg().getOrgId(), u.getAdCategory()
					.getAdCategoryId());
			addUserLog(dao, loginUserId, "编辑机构广告[" + orgAd.getId() + "]");
			if (orgAd.getAdCategory().getId().equals(u.getAdCategory().getId()))
				orgAd.setAdCategory(AdCategoryDao.find(dao, orgAd
						.getAdCategory().getId()));
			else {
				orgAd.setAdCategory(u.getAdCategory());
			}
			orgAd.setOrg(u.getOrg());
			return new KeyValue<String, BaseOrgAd>(u.getFileName(), orgAd);
		default:
			throw new AppException("只有下线的广告才能删除");
		}
	}

	static public QueryResult<BaseOrgAd> queryOrgAd(Dao dao, int orgId,
			boolean queryCount, long loginUserId, Integer filter,
			String filterContent, int firstResult, int maxResults) {
		QueryResult<BaseOrgAd> r = query(dao, converter, queryCount, fieldDefs,
				jionWhereSql(null, dao, orgId, null, filterContent),
				firstResult, maxResults);
		return r;
	}

	static public QueryResult<BaseOrgAd> queryOrgAd(Dao dao, int orgId,
			boolean queryCount, long loginUserId, Integer filter,
			String filterContent, Integer adCategoryId, int firstResult,
			int maxResults) {
		QueryResult<BaseOrgAd> r = query(dao, converter, queryCount, fieldDefs,
				jionWhereSql(null, dao, orgId, adCategoryId, filterContent),
				firstResult, maxResults);
		return r;
	}

	static public List<BaseOrgAd> getUsingAdList(Dao dao, int orgId,
			Integer adCategoryId) {
		String qlString = " from " + tableName + " a,org b,adcategory c";
		String whereString = "where a.orgid="
				+ orgId
				+ " and a.startdate<=sysdate and a.enddate>=sysdate and a.audited>1 and a.adcategoryid=c.adcategoryid and a.orgid=b.orgid";
		if (adCategoryId != null)
			whereString += " and a.adcategoryid=" + adCategoryId;
		qlString = qlString + " " + whereString + " order by a.orgadid";
		List<BaseOrgAd> ls = dao.query(converter, "select " + fieldDefs
				+ qlString);
		return ls;
	}

	/**
	 * 上线申请
	 * 
	 * @param dao
	 * @param loginUserId
	 * @param id
	 */
	static public void onlineReq(Dao dao, long loginUserId, int id,
			boolean isCancel) {
		BaseOrgAd u = find(dao, id);
		if (u == null)
			throw new AppException("申请上线的机构广告[" + id + "]不存在.");
		if (isCancel) {
			switch (u.getAuditStatus()) {
			case ONLINE_REQ:
				dao.execute(
						"update orgad set audited=?1" + " where orgadid=?2",
						AuditStatus.OFFLINE.getValue(), id);
				addUserLog(dao, loginUserId, "撤消机构广告[" + u.getId() + "]上线申请");
				break;
			default:
				throw new AppException("该广告没有申请上线.");
			}
		} else {
			switch (u.getAuditStatus()) {
			case OFFLINE:
				dao.execute(
						"update orgad set audited=?1" + " where orgadid=?2",
						AuditStatus.ONLINE_REQ.getValue(), id);
				addUserLog(dao, loginUserId, "机构广告[" + u.getId() + "]上线申请");
				break;
			default:
				throw new AppException("该广告没在下线状态.");
			}
		}
		AdminHelper.orgAdChanged(u.getOrg().getOrgId(), u.getAdCategory()
				.getAdCategoryId());
	}

	/**
	 * 下线申请
	 * 
	 * @param dao
	 * @param loginUserId
	 * @param id
	 */
	static public void offlineReq(Dao dao, long loginUserId, int id,
			boolean isCancel) {
		BaseOrgAd u = find(dao, id);
		if (u == null)
			throw new AppException("申请下线的机构广告[" + id + "]不存在.");
		if (isCancel) {
			switch (u.getAuditStatus()) {
			case OFFLINE_REQ:
				dao.execute(
						"update orgad set audited=?1" + " where orgadid=?2",
						AuditStatus.ONLINE.getValue(), id);
				addUserLog(dao, loginUserId, "撤消机构广告[" + u.getId() + "]下线申请");
				break;
			default:
				throw new AppException("该广告没有申请下线.");
			}
		} else {
			switch (u.getAuditStatus()) {
			case ONLINE:
				dao.execute(
						"update orgad set audited=?1" + " where orgadid=?2",
						AuditStatus.OFFLINE_REQ.getValue(), id);
				addUserLog(dao, loginUserId, "机构广告[" + u.getId() + "]下线申请");
				break;
			default:
				throw new AppException("该广告没在上线状态.");
			}
		}
		AdminHelper.orgAdChanged(u.getOrg().getOrgId(), u.getAdCategory()
				.getAdCategoryId());
	}

	/**
	 * 上线审核
	 * 
	 * @param dao
	 * @param loginUserId
	 * @param id
	 *            被审核的机构广告ID
	 * @param auditOk
	 *            审核是否通过
	 */
	static public void onlineAudit(Dao dao, long loginUserId, int id,
			boolean auditOk) {
		BaseOrgAd u = find(dao, id);
		if (u == null)
			throw new AppException("要审核的机构广告[" + id + "]不存在.");
		switch (u.getAuditStatus()) {
		case ONLINE_REQ:
			dao.execute("update orgad set audited=?1"
					+ ",audituserid=?2,audittime=sysdate where orgadid=?3",
					(auditOk ? AuditStatus.ONLINE.getValue()
							: AuditStatus.OFFLINE.getValue()), loginUserId, id);
			break;
		default:
			throw new AppException("该机构广告没有上线请求.");
		}
		AdminHelper.orgAdChanged(u.getOrg().getOrgId(), u.getAdCategory()
				.getAdCategoryId());
		addUserLog(dao, loginUserId, "机构广告[" + u.getId() + "]上线审核");
	}

	/**
	 * 广告下线审核
	 * 
	 * @param dao
	 * @param loginUserId
	 * @param id
	 *            被审核的机构广告ID
	 * @param auditOk
	 *            审核是否通过
	 */
	static public void offlineAudit(Dao dao, long loginUserId, int id,
			boolean auditOk) {
		BaseOrgAd u = find(dao, id);
		if (u == null)
			throw new AppException("要审核的机构广告[" + id + "]不存在.");
		switch (u.getAuditStatus()) {
		case OFFLINE_REQ:
			dao.execute("update orgad set audited=?1"
					+ ",audituserid=?2,audittime=sysdate where orgadid=?3",
					(auditOk ? AuditStatus.OFFLINE.getValue()
							: AuditStatus.ONLINE.getValue()), loginUserId, id);
			break;
		default:
			throw new AppException("该机构广告没有提交下架审核请求.");
		}
		AdminHelper.orgAdChanged(u.getOrg().getOrgId(), u.getAdCategory()
				.getAdCategoryId());
		addUserLog(dao, loginUserId, "审核下架机构广告[" + u.getId() + "]");
	}

	static public QueryResult<BaseOrgAd> queryNeedAuditAd(Dao dao, int orgId,
			boolean queryCount, long loginUserId, Integer filter,
			String filterContent, int firstResult, int maxResults) {
		QueryResult<BaseOrgAd> r = query(
				dao,
				converter,
				queryCount,
				fieldDefs,
				jionWhereSql("(a.audited=1 or a.audited=3)", dao, orgId, null,
						filterContent), firstResult, maxResults);
		return r;
	}

}
