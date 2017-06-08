package kxd.scs.beans;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import kxd.engine.dao.BaseBean;
import kxd.remote.scs.beans.BaseOrgAd;
import kxd.remote.scs.interfaces.OrgAdBeanRemote;
import kxd.remote.scs.util.QueryResult;
import kxd.scs.dao.publish.OrgAdDao;
import kxd.util.KeyValue;

@Stateless(name = "kxd-ejb-orgAdBean", mappedName = "kxd-ejb-orgAdBean")
public class OrgAdBean extends BaseBean implements OrgAdBeanRemote {
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public QueryResult<BaseOrgAd> queryOrgAd(boolean queryCount,
			long loginUserId, int orgId, Integer filter, String filterContent,
			int firstResult, int maxResults) {
		return OrgAdDao.queryOrgAd(getDao(), orgId, queryCount, loginUserId,
				filter, filterContent, firstResult, maxResults);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public QueryResult<BaseOrgAd> queryOrgAd(boolean queryCount,
			long loginUserId, int orgId, Integer filter, String filterContent,
			Integer adCategoryId, int firstResult, int maxResults) {
		return OrgAdDao.queryOrgAd(getDao(), orgId, queryCount, loginUserId,
				filter, filterContent, adCategoryId, firstResult, maxResults);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public BaseOrgAd find(int id) {
		BaseOrgAd o = OrgAdDao.find(getDao(), id);
		return o;
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public BaseOrgAd add(long loginUserId, BaseOrgAd orgAd) {
		return OrgAdDao.add(getDao(), loginUserId, orgAd);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public BaseOrgAd delete(long loginUserId, int orgAdId) {
		return OrgAdDao.delete(getDao(), loginUserId, orgAdId);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public KeyValue<String, BaseOrgAd> edit(long loginUserId, BaseOrgAd orgAd) {
		return OrgAdDao.edit(getDao(), loginUserId, orgAd);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<BaseOrgAd> getUsingAdList(int orgId, Integer adCategoryId) {
		return OrgAdDao.getUsingAdList(getDao(), orgId, adCategoryId);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public void onlineAudit(long loginUserId, int id, boolean auditOk) {
		OrgAdDao.onlineAudit(getDao(), loginUserId, id, auditOk);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public void onlineReq(long loginUserId, int id, boolean isCancel) {
		OrgAdDao.onlineReq(getDao(), loginUserId, id, isCancel);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public void offlineAudit(long loginUserId, int id, boolean auditOk) {
		OrgAdDao.offlineAudit(getDao(), loginUserId, id, auditOk);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public void offlineReq(long loginUserId, int id, boolean isCancel) {
		OrgAdDao.offlineReq(getDao(), loginUserId, id, isCancel);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public BaseOrgAd updateUpload(long loginUserId, BaseOrgAd item) {
		return OrgAdDao.updateUpload(getDao(), loginUserId, item);
	}

}
