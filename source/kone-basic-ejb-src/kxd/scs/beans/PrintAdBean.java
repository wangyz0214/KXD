package kxd.scs.beans;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import kxd.engine.dao.BaseBean;
import kxd.remote.scs.beans.BasePrintAd;
import kxd.remote.scs.beans.adinfo.EditedPrintAd;
import kxd.remote.scs.interfaces.PrintAdBeanRemote;
import kxd.remote.scs.util.QueryResult;
import kxd.scs.dao.publish.PrintAdDao;

@Stateless(name = "kxd-ejb-printAdBean", mappedName = "kxd-ejb-printAdBean")
public class PrintAdBean extends BaseBean implements PrintAdBeanRemote {
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public QueryResult<BasePrintAd> queryPrintAd(boolean queryCount,
			long loginUserId, int orgId, Integer filter, String filterContent,
			int firstResult, int maxResults) {
		return PrintAdDao.queryPrintAd(getDao(), orgId, queryCount,
				loginUserId, filter, filterContent, firstResult, maxResults);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public BasePrintAd find(int id) {
		return PrintAdDao.find(getDao(), id);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public int add(long loginUserId, EditedPrintAd printAd) {
		return PrintAdDao.add(getDao(), loginUserId, printAd);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void delete(long loginUserId, int printAdId) {
		PrintAdDao.delete(getDao(), loginUserId, printAdId);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void edit(long loginUserId, EditedPrintAd printAd) {
		PrintAdDao.edit(getDao(), loginUserId, printAd);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<EditedPrintAd> getUsingAdList(int orgId, Integer adCategoryId) {
		return PrintAdDao.getUsingAdList(getDao(), orgId, adCategoryId);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public void onlineAudit(long loginUserId, int printAd, boolean auditOk) {
		PrintAdDao.onlineAudit(getDao(), loginUserId, printAd, auditOk);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public void offlineAudit(long loginUserId, int printAd, boolean auditOk) {
		PrintAdDao.offlineAudit(getDao(), loginUserId, printAd, auditOk);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public void onlineReq(long loginUserId, int printAdId, boolean isCancel) {
		PrintAdDao.onlineReq(getDao(), loginUserId, printAdId, isCancel);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public void offlineReq(long loginUserId, int printAdId, boolean isCancel) {
		PrintAdDao.offlineReq(getDao(), loginUserId, printAdId, isCancel);
	}

}
