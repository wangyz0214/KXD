package kxd.scs.beans;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import kxd.engine.dao.BaseBean;
import kxd.remote.scs.beans.BaseInfoFile;
import kxd.remote.scs.beans.adinfo.EditedInfo;
import kxd.remote.scs.interfaces.InfoBeanRemote;
import kxd.remote.scs.util.QueryResult;
import kxd.scs.dao.publish.InfoDao;

@Stateless(name = "kxd-ejb-infoBean", mappedName = "kxd-ejb-infoBean")
public class InfoBean extends BaseBean implements InfoBeanRemote {

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<EditedInfo> getUsingInfoList(int orgId, Integer infoCategoryId) {
		return InfoDao.getUsingInfoList(getDao(), orgId, infoCategoryId);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public EditedInfo find(long infoId) {
		EditedInfo r = InfoDao.find(getDao(), infoId);
		return r;
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void add(long loginUserId, EditedInfo info) {
		InfoDao.add(getDao(), loginUserId, info);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public String[] delete(long loginUserId, long infoId) {
		return InfoDao.delete(getDao(), loginUserId, infoId);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void edit(long loginUserId, EditedInfo info) {
		InfoDao.edit(getDao(), loginUserId, info);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public QueryResult<EditedInfo> queryInfo(boolean queryCount,
			long loginUserId, int orgId, Integer filter, String filterContent,
			int firstResult, int maxResults) {
		return InfoDao.queryInfo(getDao(), orgId, queryCount, loginUserId,
				filter, filterContent, firstResult, maxResults);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public QueryResult<EditedInfo> queryInfo(boolean queryCount,
			long loginUserId, int orgId, Integer filter, String filterContent,
			Integer infoCategoryId, int firstResult, int maxResults) {
		return InfoDao.queryInfo(getDao(), orgId, queryCount, loginUserId,
				filter, filterContent, infoCategoryId, firstResult, maxResults);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public String addFile(long loginUserId, BaseInfoFile infoFile) {
		return InfoDao.addFile(getDao(), loginUserId, infoFile);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<BaseInfoFile> queryInfoFile(long infoId, Integer fileType,
			Integer filter, String filterContent) {
		return InfoDao.queryInfoFile(getDao(), infoId, fileType);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public String deleteFile(long loginUserId, long infoFileId) {
		return InfoDao.deleteFile(getDao(), loginUserId, infoFileId);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public long getNextInfoId() {
		return InfoDao.getNextInfoId(getDao());
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public void onlineAudit(long loginUserId, int id, boolean auditOk) {
		InfoDao.onlineAudit(getDao(), loginUserId, id, auditOk);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public void onlineReq(long loginUserId, int id, boolean isCancel) {
		InfoDao.onlineReq(getDao(), loginUserId, id, isCancel);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public void offlineAudit(long loginUserId, int id, boolean auditOk) {
		InfoDao.offlineAudit(getDao(), loginUserId, id, auditOk);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public void offlineReq(long loginUserId, int id, boolean isCancel) {
		InfoDao.offlineReq(getDao(), loginUserId, id, isCancel);
	}
}
