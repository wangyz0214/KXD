package kxd.scs.dao.publish;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import kxd.engine.dao.Dao;
import kxd.engine.helper.AdminHelper;
import kxd.remote.scs.beans.BaseInfoFile;
import kxd.remote.scs.beans.adinfo.EditedInfo;
import kxd.remote.scs.util.AppException;
import kxd.remote.scs.util.QueryResult;
import kxd.remote.scs.util.emun.AuditStatus;
import kxd.scs.dao.BaseDao;
import kxd.scs.dao.publish.converters.EditedInfoConverter;
import kxd.scs.dao.right.OrgDao;

public class InfoDao extends BaseDao {
	final public static EditedInfoConverter converter = new EditedInfoConverter();
	final private static String fieldDefs = "a.infoid,a.orgid,orgfullname,"
			+ "a.infocategoryid,infocategorydesp,a.title,a.publishdate"
			+ ",a.summary,a.filename,a.starttime,a.endtime,a.audited,a.url";
	final private static String tableName = "info";

	static private String jionWhereSql(String where, Dao dao, int orgId,
			String keyword, Integer infoCategoryId) {
		String qlString = " from " + tableName + " a,org b,infocategory c ";
		String whereString = "a.orgid=b.orgid and a.infocategoryid=c.infocategoryid and "
				+ OrgDao.getOrgFilterString(dao, orgId, "b");
		if (where != null)
			whereString += " and " + where;
		if (keyword != null) {
			keyword = keyword.trim();
			if (keyword.length() > 0) {
				whereString += " and a.title like '" + keyword + "%'";
			}
		}
		if (infoCategoryId != null)
			whereString += " and a.infocategoryid=" + infoCategoryId;

		if (whereString.length() > 0)
			whereString = " where " + whereString;
		return qlString + whereString + " order by a.publishdate desc";
	}

	static public List<EditedInfo> getUsingInfoList(Dao dao, int orgId,
			Integer infoCategoryId) {
		String qlString = " from " + tableName + " a,org b,infocategory c ";
		String whereString = " where a.orgid=b.orgid and a.audited>1 and a.infocategoryid=c.infocategoryid and orgid="
				+ orgId;
		if (infoCategoryId != null) {
			whereString += " and a.infocategoryid=" + infoCategoryId;
		}
		return dao.query(converter, "select " + fieldDefs + qlString
				+ whereString + " order by publishdate desc");
	}

	static public EditedInfo find(Dao dao, long infoId) {
		String sql = "select " + fieldDefs + " from info a,"
				+ "org b,infocategory c "
				+ "where infoid=?1 and a.orgid=b.orgid and "
				+ "a.infocategoryid=c.infocategoryid";
		Iterator<EditedInfo> it = dao.query(converter, sql, infoId).iterator();
		if (it.hasNext())
			return it.next();
		else
			return null;
	}

	static public long add(Dao dao, long loginUserId, EditedInfo info) {
		dao.insert(info, converter);
		addUserLog(dao, loginUserId, "添加信息[" + info.getTitle() + "]");
		AdminHelper.infoChanged(info.getOrg().getOrgId(), info
				.getInfoCategory().getInfoCategoryId());
		return info.getId();
	}

	static public String[] delete(Dao dao, long loginUserId, long infoId) {
		EditedInfo u = find(dao, infoId);
		if (u == null)
			throw new AppException("要删除的信息[" + infoId + "]不存在.");
		switch (u.getAuditStatus()) {
		case OFFLINE:
			dao.delete(u, converter);
			List<?> ls = dao.query("select infoid,filetype,filename "
					+ "from infofiles where infoid=?1", infoId);
			String[] ret = new String[ls.size() + 1];
			int i = 0;
			ret[i++] = u.getOrg().getOrgId() + "/" + u.getFileName();
			for (Object o : ls) {
				Object[] a = (Object[]) o;
				ret[i++] = "files/" + a[0] + "/" + a[1] + "/" + a[2];
			}
			AdminHelper.infoChanged(u.getOrg().getOrgId(), u.getInfoCategory()
					.getInfoCategoryId());
			addUserLog(dao, loginUserId, "删除信息[" + u.getId() + "]");
			return ret;
		default:
			throw new AppException("只有处于下线状态的信息，才能删除.");
		}
	}

	static public void edit(Dao dao, long loginUserId, EditedInfo info) {
		EditedInfo u = find(dao, info.getId());
		if (u == null)
			throw new AppException("要编辑的信息[" + info.getId() + "]不存在.");
		switch (u.getAuditStatus()) {
		case OFFLINE:
			dao.update(info, converter);
			AdminHelper.infoChanged(u.getOrg().getOrgId(), u.getInfoCategory()
					.getInfoCategoryId());
			addUserLog(dao, loginUserId, "编辑信息[" + info.getId() + "]");
			break;
		default:
			throw new AppException("只有处于下线状态的信息，才能编辑.");
		}
	}

	static public QueryResult<EditedInfo> queryInfo(Dao dao, int orgId,
			boolean queryCount, long loginUserId, Integer filter,
			String filterContent, int firstResult, int maxResults) {
		QueryResult<EditedInfo> r = query(dao, converter, queryCount,
				fieldDefs, jionWhereSql(null, dao, orgId, filterContent, null),
				firstResult, maxResults);
		return r;
	}

	static public QueryResult<EditedInfo> queryInfo(Dao dao, int orgId,
			boolean queryCount, long loginUserId, Integer filter,
			String filterContent, Integer infoCategoryId, int firstResult,
			int maxResults) {
		QueryResult<EditedInfo> r = query(dao, converter, queryCount,
				fieldDefs,
				jionWhereSql(null, dao, orgId, filterContent, infoCategoryId),
				firstResult, maxResults);
		return r;
	}

	static public BaseInfoFile findFile(Dao dao, long infoFileId) {
		Iterator<?> it = dao.query(
				"select fileid,infoid,filetype,filename,url "
						+ "from infofiles where fileid=?1", infoFileId)
				.iterator();
		if (it.hasNext()) {
			Object[] o = (Object[]) it.next();
			BaseInfoFile f = new BaseInfoFile();
			f.setId(Long.valueOf(o[0].toString()));
			f.setInfoId(Long.valueOf(o[1].toString()));
			f.setFileType(Integer.valueOf(o[2].toString()));
			f.setFileName((String) o[3]);
			f.setUrl((String) o[4]);
			return f;
		} else
			return null;
	}

	static public String addFile(Dao dao, long loginUserId,
			BaseInfoFile infoFile) {
		long id = Long.valueOf(dao
				.query("select seq_infofile.nextval from dual").get(0)
				.toString());
		String fileName = infoFile.getFileName();
		int index = fileName.lastIndexOf(".");
		if (index >= 0)
			fileName = id + fileName.substring(index);
		else
			fileName = Long.toString(id);
		dao.execute(
				"insert into infofiles(fileid,infoid,filetype,filename,url)"
						+ " values(?1,?2,?3,?4,?5)", id, infoFile.getInfoId(),
				infoFile.getFileType(), fileName, infoFile.getUrl());
		return fileName;
	}

	static public List<BaseInfoFile> queryInfoFile(Dao dao, long infoId,
			Integer fileType) {
		String sql = "select fileid,infoid,filetype,filename,url from infofiles where "
				+ "infoid=" + infoId;
		if (fileType != null)
			sql += " and fileType=" + fileType;
		Iterator<?> it = dao.query(sql).iterator();
		List<BaseInfoFile> ls = new ArrayList<BaseInfoFile>();
		while (it.hasNext()) {
			Object[] o = (Object[]) it.next();
			BaseInfoFile f = new BaseInfoFile();
			f.setId(Long.valueOf(o[0].toString()));
			f.setInfoId(Long.valueOf(o[1].toString()));
			f.setFileType(Integer.valueOf(o[2].toString()));
			f.setFileName((String) o[3]);
			f.setUrl((String) o[4]);
			ls.add(f);
		}
		return ls;
	}

	static public String deleteFile(Dao dao, long loginUserId, long infoFileId) {
		BaseInfoFile f = findFile(dao, infoFileId);
		if (f != null) {
			dao.execute("delete from infofiles fileid=?1", infoFileId);
			return "files/" + f.getInfoId() + "/" + f.getFileType() + "/"
					+ f.getFileName();
		}
		return null;
	}

	static public long getNextInfoId(Dao dao) {
		return Long.valueOf(dao.query("select seq_info.nextval from dual")
				.get(0).toString());
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
		EditedInfo u = find(dao, id);
		if (u == null)
			throw new AppException("申请上线的信息[" + id + "]不存在.");
		if (isCancel) {
			switch (u.getAuditStatus()) {
			case ONLINE_REQ:
				dao.execute("update info set audited=?1" + " where infoid=?2",
						AuditStatus.OFFLINE.getValue(), id);
				addUserLog(dao, loginUserId, "撤消信息[" + u.getId() + "]上线申请");
				break;
			default:
				throw new AppException("该广告没有申请上线.");
			}
		} else {
			switch (u.getAuditStatus()) {
			case OFFLINE:
				dao.execute("update info set audited=?1" + " where infoid=?2",
						AuditStatus.ONLINE_REQ.getValue(), id);
				addUserLog(dao, loginUserId, "信息[" + u.getId() + "]上线申请");
				break;
			default:
				throw new AppException("该广告没在下线状态.");
			}
		}
		AdminHelper.infoChanged(u.getOrg().getOrgId(), u.getInfoCategory()
				.getInfoCategoryId());
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
		EditedInfo u = find(dao, id);
		if (u == null)
			throw new AppException("申请下线的信息[" + id + "]不存在.");
		if (isCancel) {
			switch (u.getAuditStatus()) {
			case OFFLINE_REQ:
				dao.execute("update info set audited=?1" + " where infoid=?2",
						AuditStatus.ONLINE.getValue(), id);
				addUserLog(dao, loginUserId, "撤消信息[" + u.getId() + "]下线申请");
				break;
			default:
				throw new AppException("该广告没有申请下线.");
			}
		} else {
			switch (u.getAuditStatus()) {
			case ONLINE:
				dao.execute("update info set audited=?1" + " where infoid=?2",
						AuditStatus.OFFLINE_REQ.getValue(), id);
				addUserLog(dao, loginUserId, "信息[" + u.getId() + "]下线申请");
				break;
			default:
				throw new AppException("该广告没在上线状态.");
			}
		}
		AdminHelper.infoChanged(u.getOrg().getOrgId(), u.getInfoCategory()
				.getInfoCategoryId());
	}

	/**
	 * 上线审核
	 * 
	 * @param dao
	 * @param loginUserId
	 * @param id
	 *            被审核的信息ID
	 * @param auditOk
	 *            审核是否通过
	 */
	static public void onlineAudit(Dao dao, long loginUserId, int id,
			boolean auditOk) {
		EditedInfo u = find(dao, id);
		if (u == null)
			throw new AppException("要审核的信息[" + id + "]不存在.");
		switch (u.getAuditStatus()) {
		case ONLINE_REQ:
			dao.execute("update info set audited=?1"
					+ ",audituserid=?2,audittime=sysdate where infoid=?3",
					(auditOk ? AuditStatus.ONLINE.getValue()
							: AuditStatus.OFFLINE.getValue()), loginUserId, id);
			break;
		default:
			throw new AppException("该信息没有上线请求.");
		}
		AdminHelper.infoChanged(u.getOrg().getOrgId(), u.getInfoCategory()
				.getInfoCategoryId());
		addUserLog(dao, loginUserId, "信息[" + u.getId() + "]上线审核");
	}

	/**
	 * 广告下线审核
	 * 
	 * @param dao
	 * @param loginUserId
	 * @param id
	 *            被审核的信息ID
	 * @param auditOk
	 *            审核是否通过
	 */
	static public void offlineAudit(Dao dao, long loginUserId, int id,
			boolean auditOk) {
		EditedInfo u = find(dao, id);
		if (u == null)
			throw new AppException("要审核的信息[" + id + "]不存在.");
		switch (u.getAuditStatus()) {
		case OFFLINE_REQ:
			dao.execute("update info set audited=?1"
					+ ",audituserid=?2,audittime=sysdate where infoid=?3",
					(auditOk ? AuditStatus.OFFLINE.getValue()
							: AuditStatus.ONLINE.getValue()), loginUserId, id);
			break;
		default:
			throw new AppException("该信息没有提交下架审核请求.");
		}
		AdminHelper.infoChanged(u.getOrg().getOrgId(), u.getInfoCategory()
				.getInfoCategoryId());
		addUserLog(dao, loginUserId, "审核下架信息[" + u.getId() + "]");
	}

	static public QueryResult<EditedInfo> queryNeedAuditInfo(Dao dao,
			int orgId, boolean queryCount, long loginUserId, Integer filter,
			String filterContent, int firstResult, int maxResults) {
		QueryResult<EditedInfo> r = query(
				dao,
				converter,
				queryCount,
				fieldDefs,
				jionWhereSql("(a.audited=1 or a.audited=3)", dao, orgId,
						filterContent, null), firstResult, maxResults);
		return r;
	}

}
