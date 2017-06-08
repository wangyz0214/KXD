package kxd.scs.dao.publish;

import java.util.Iterator;
import java.util.List;

import kxd.engine.dao.Dao;
import kxd.engine.helper.AdminHelper;
import kxd.remote.scs.beans.BaseTermAd;
import kxd.remote.scs.util.AppException;
import kxd.remote.scs.util.QueryResult;
import kxd.remote.scs.util.emun.AuditStatus;
import kxd.scs.dao.BaseDao;
import kxd.scs.dao.publish.converters.BaseTermAdConverter;
import kxd.scs.dao.right.OrgDao;
import kxd.util.KeyValue;

public class TermAdDao extends BaseDao {
	final public static BaseTermAdConverter baseConverter = new BaseTermAdConverter();
	final public static BaseTermAdConverter converter = new BaseTermAdConverter();
	final private static String fieldDefs = "a.termadid,a.termid,termdesp,"
			+ "a.adcategoryid,adcategorydesp,a.addesp,to_char(a.startdate,'yyyy-mm-dd hh24:mi:ss')"
			+ ",to_char(a.enddate,'yyyy-mm-dd hh24:mi:ss'),a.filename,a.playtimes,a.duration,a.audited,a.url,"
			+ "a.uploadcomplete,a.storetype,a.priority";
	final private static String tableName = "termad";

	static private String jionWhereSql(String where, int termId,
			Integer adCategoryId, String keyword) {
		String qlString = " from " + tableName + " a,term b,adcategory c ";
		String whereString = "a.termid=" + termId
				+ " and a.termid=b.termid and a.adcategoryid=c.adcategoryid";
		if (where != null)
			whereString += " and " + where;
		if (adCategoryId != null) {
			whereString += " and a.adcategoryid=" + adCategoryId;
		}
		if (keyword!=null&&!keyword.trim().equals(""))
			whereString += " and a.addesp like '" + keyword + "%'";

		if (whereString.length() > 0)
			whereString = " where " + whereString;
		return qlString + whereString + " order by a.termadid";
	}

	static public BaseTermAd find(Dao dao, int termAdId) {
		String sql = "select " + fieldDefs + " from termad a,"
				+ "term b,adcategory c "
				+ "where termadid=?1 and a.termid=b.termid and "
				+ "a.adcategoryid=c.adcategoryid";
		Iterator<BaseTermAd> it = dao.query(converter, sql, termAdId)
				.iterator();
		if (it.hasNext())
			return it.next();
		else
			return null;
	}

	static public BaseTermAd add(Dao dao, long loginUserId, BaseTermAd termAd) {
		if (termAdFileExists(dao, termAd.getTerm().getId(), termAd
				.getAdCategory().getId(), termAd.getFileName()))
			throw new AppException("同一终端[id=" + termAd.getTerm().getId()
					+ "]和广告分类[id=" + termAd.getAdCategory().getId()
					+ "]下的广告文件名不能重复");
		dao.insert(termAd, converter);
		addUserLog(dao, loginUserId, "添加终端终端广告[" + termAd.getAdDesp() + "]");
		AdminHelper.termAdChanged(termAd.getTerm().getTermId(), termAd
				.getAdCategory().getAdCategoryId());
		return find(dao, termAd.getId());
	}

	static public boolean termAdFileExists(Dao dao, int orgId,
			int adCategoryId, String fileName) {
		return !dao.queryPage(
				"select * from termad where termid=?1 and adcategoryid=?2"
						+ " and filename=?3", 0, 1, orgId, adCategoryId,
				fileName).isEmpty();
	}

	static public BaseTermAd delete(Dao dao, long loginUserId, int termAdId) {
		BaseTermAd u = find(dao, termAdId);
		if (u == null)
			throw new AppException("要删除的终端广告[" + termAdId + "]不存在.");
		switch (u.getAuditStatus()) {
		case OFFLINE:
			dao.delete(u, converter);
			AdminHelper.termAdChanged(u.getTerm().getTermId(), u
					.getAdCategory().getAdCategoryId());
			addUserLog(dao, loginUserId, "删除终端广告[" + u.getId() + "]");
			return u;
		default:
			throw new AppException("只有下线的广告才能删除");
		}
	}

	static public KeyValue<String, BaseTermAd> edit(Dao dao, long loginUserId,
			BaseTermAd termAd) {
		BaseTermAd u = find(dao, termAd.getId());
		if (u == null)
			throw new AppException("要编辑的终端广告[" + termAd.getId() + "]不存在.");
		switch (u.getAuditStatus()) {
		case OFFLINE:
			if (termAd.getFileName() == null
					|| termAd.getFileName().trim().isEmpty()) {
				termAd.setFileName(u.getFileName());
				termAd.setUploadComplete(u.isUploadComplete());
				termAd.setUrl(u.getOriginalUrl());
			} else {
				if (u.getTerm().equals(termAd.getTerm())
						&& u.getAdCategory().equals(termAd.getAdCategory())) {
					if (!u.getFileName().equals(termAd.getFileName())) {
						if (termAdFileExists(dao, termAd.getTerm().getId(),
								termAd.getAdCategory().getId(),
								termAd.getFileName()))
							throw new AppException("同一终端[id="
									+ termAd.getTerm().getId() + "]和广告分类[id="
									+ termAd.getAdCategory().getId()
									+ "]下的广告文件名不能重复");
					}
				}
			}
			dao.update(termAd, converter);
			AdminHelper.termAdChanged(u.getTerm().getTermId(), u
					.getAdCategory().getAdCategoryId());
			addUserLog(dao, loginUserId, "编辑终端广告[" + termAd.getId() + "]");
			if (termAd.getAdCategory().getId()
					.equals(u.getAdCategory().getId()))
				termAd.setAdCategory(AdCategoryDao.find(dao, termAd
						.getAdCategory().getId()));
			else {
				termAd.setAdCategory(u.getAdCategory());
			}
			return new KeyValue<String, BaseTermAd>(u.getFileName(), termAd);
		default:
			throw new AppException("只有下线的广告才能编辑");
		}
	}

	static public BaseTermAd updateUpload(Dao dao, long loginUserId,
			BaseTermAd termAd) {
		BaseTermAd u = find(dao, termAd.getId());
		if (u == null)
			throw new AppException("要上传的终端广告[" + termAd.getId() + "]不存在.");
		dao.execute("update termad set uploadcomplete=?1 where termadid=?2",
				termAd.isUploadComplete(), termAd.getId());
		AdminHelper.termAdChanged(u.getTerm().getTermId(), u.getAdCategory()
				.getAdCategoryId());
		return u;
	}

	static public List<BaseTermAd> getTermAdList(Dao dao, int termId) {
		List<BaseTermAd> ls = dao.query(converter, "select " + fieldDefs
				+ jionWhereSql(null, termId, null, ""));
		return ls;
	}

	static public List<BaseTermAd> getTermAdList(Dao dao, int termId,
			Integer adCategoryId, String adDesp) {
		List<BaseTermAd> ls = dao.query(converter, "select " + fieldDefs
				+ jionWhereSql(null, termId, adCategoryId, adDesp));
		return ls;
	}

	static public List<BaseTermAd> getTermAdUsingList(Dao dao, int termId,
			Integer adCategoryId) {
		List<BaseTermAd> ls = dao
				.query(converter,
						"select "
								+ fieldDefs
								+ jionWhereSql(
										"a.audited>1 and a.startdate<=sysdate and a.enddate>=sysdate",
										termId, adCategoryId, ""));
		return ls;
	}

	static public QueryResult<BaseTermAd> queryNeedAuditAd(Dao dao, int orgId,
			boolean queryCount, long loginUserId, Integer filter,
			String filterContent, int firstResult, int maxResults) {
		String qlString = " from " + tableName
				+ " a,org b,term c,adcategory d ";
		String whereString = " where (a.audited=1 or a.audited=3) and a.termid=c.termid and a.adcategoryid=d.adcategoryid and "
				+ "b.orgid=c.orgid and "
				+ OrgDao.getOrgFilterString(dao, orgId, "b");
		QueryResult<BaseTermAd> r = query(dao, converter, queryCount,
				fieldDefs, qlString + whereString + " order by a.termadid ",
				firstResult, maxResults);
		return r;
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
		BaseTermAd u = find(dao, id);
		if (u == null)
			throw new AppException("申请上线的机构广告[" + id + "]不存在.");
		if (isCancel) {
			switch (u.getAuditStatus()) {
			case ONLINE_REQ:
				dao.execute("update termad set audited=?1"
						+ " where termadid=?2", AuditStatus.OFFLINE.getValue(),
						id);
				addUserLog(dao, loginUserId, "撤消机构广告[" + u.getId() + "]上线申请");
				break;
			default:
				throw new AppException("该广告没有申请上线.");
			}
		} else {
			switch (u.getAuditStatus()) {
			case OFFLINE:
				dao.execute("update termad set audited=?1"
						+ " where termadid=?2",
						AuditStatus.ONLINE_REQ.getValue(), id);
				addUserLog(dao, loginUserId, "机构广告[" + u.getId() + "]上线申请");
				break;
			default:
				throw new AppException("该广告没在下线状态.");
			}
		}
		AdminHelper.termAdChanged(u.getTerm().getTermId(), u.getAdCategory()
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
		BaseTermAd u = find(dao, id);
		if (u == null)
			throw new AppException("申请下线的机构广告[" + id + "]不存在.");
		if (isCancel) {
			switch (u.getAuditStatus()) {
			case OFFLINE_REQ:
				dao.execute("update termad set audited=?1"
						+ " where termadid=?2", AuditStatus.ONLINE.getValue(),
						id);
				addUserLog(dao, loginUserId, "撤消机构广告[" + u.getId() + "]下线申请");
				break;
			default:
				throw new AppException("该广告没有申请下线.");
			}
		} else {
			switch (u.getAuditStatus()) {
			case ONLINE:
				dao.execute("update termad set audited=?1"
						+ " where termadid=?2",
						AuditStatus.OFFLINE_REQ.getValue(), id);
				addUserLog(dao, loginUserId, "机构广告[" + u.getId() + "]下线申请");
				break;
			default:
				throw new AppException("该广告没在上线状态.");
			}
		}
		AdminHelper.termAdChanged(u.getTerm().getTermId(), u.getAdCategory()
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
		BaseTermAd u = find(dao, id);
		if (u == null)
			throw new AppException("要审核的机构广告[" + id + "]不存在.");
		switch (u.getAuditStatus()) {
		case ONLINE_REQ:
			dao.execute("update termad set audited=?1"
					+ ",audituserid=?2,audittime=sysdate where termadid=?3",
					(auditOk ? AuditStatus.ONLINE.getValue()
							: AuditStatus.OFFLINE.getValue()), loginUserId, id);
			break;
		default:
			throw new AppException("该机构广告没有上线请求.");
		}
		AdminHelper.termAdChanged(u.getTerm().getTermId(), u.getAdCategory()
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
		BaseTermAd u = find(dao, id);
		if (u == null)
			throw new AppException("要审核的机构广告[" + id + "]不存在.");
		switch (u.getAuditStatus()) {
		case OFFLINE_REQ:
			dao.execute("update termad set audited=?1"
					+ ",audituserid=?2,audittime=sysdate where termadid=?3",
					(auditOk ? AuditStatus.OFFLINE.getValue()
							: AuditStatus.ONLINE.getValue()), loginUserId, id);
			break;
		default:
			throw new AppException("该机构广告没有提交下架审核请求.");
		}
		AdminHelper.termAdChanged(u.getTerm().getTermId(), u.getAdCategory()
				.getAdCategoryId());
		addUserLog(dao, loginUserId, "审核下架机构广告[" + u.getId() + "]");
	}

}
