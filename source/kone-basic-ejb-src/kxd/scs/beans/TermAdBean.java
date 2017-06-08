package kxd.scs.beans;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import kxd.engine.dao.BaseBean;
import kxd.remote.scs.beans.BaseTermAd;
import kxd.remote.scs.interfaces.TermAdBeanRemote;
import kxd.scs.dao.publish.TermAdDao;
import kxd.util.KeyValue;

@Stateless(name = "kxd-ejb-termAdBean", mappedName = "kxd-ejb-termAdBean")
public class TermAdBean extends BaseBean implements TermAdBeanRemote {

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<BaseTermAd> getAdList(long loginUserId, int termId) {
		return TermAdDao.getTermAdList(getDao(), termId);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<BaseTermAd> getAdList(long loginUserId, int termId,
			Integer adCategoryId,String adDesp) {
		return TermAdDao.getTermAdList(getDao(), termId, adCategoryId,adDesp);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<BaseTermAd> getTermAdUsingList(int termId, Integer adCategoryId) {
		return TermAdDao.getTermAdUsingList(getDao(), termId, adCategoryId);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public BaseTermAd find(int id) {
		BaseTermAd r = TermAdDao.find(getDao(), id);
		return r;
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public BaseTermAd add(long loginUserId, BaseTermAd termAd) {
		return TermAdDao.add(getDao(), loginUserId, termAd);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public BaseTermAd delete(long loginUserId, int termAdId) {
		return TermAdDao.delete(getDao(), loginUserId, termAdId);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public KeyValue<String, BaseTermAd> edit(long loginUserId, BaseTermAd termAd) {
		return TermAdDao.edit(getDao(), loginUserId, termAd);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public void onlineAudit(long loginUserId, int id, boolean auditOk) {
		TermAdDao.onlineAudit(getDao(), loginUserId, id, auditOk);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public void onlineReq(long loginUserId, int id, boolean isCancel) {
		TermAdDao.onlineReq(getDao(), loginUserId, id, isCancel);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public void offlineAudit(long loginUserId, int id, boolean auditOk) {
		TermAdDao.offlineAudit(getDao(), loginUserId, id, auditOk);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public void offlineReq(long loginUserId, int id, boolean isCancel) {
		TermAdDao.offlineReq(getDao(), loginUserId, id, isCancel);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public BaseTermAd updateUpload(long loginUserId, BaseTermAd item) {
		return TermAdDao.updateUpload(getDao(), loginUserId, item);
	}

}
