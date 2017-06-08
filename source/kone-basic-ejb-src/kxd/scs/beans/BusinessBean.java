package kxd.scs.beans;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import kxd.engine.dao.BaseBean;
import kxd.remote.scs.beans.BaseBusiness;
import kxd.remote.scs.beans.appdeploy.EditedBusiness;
import kxd.remote.scs.interfaces.BusinessBeanRemote;
import kxd.remote.scs.util.QueryResult;
import kxd.scs.dao.appdeploy.BusinessDao;

@Stateless(name = "kxd-ejb-businessBean", mappedName = "kxd-ejb-businessBean")
public class BusinessBean extends BaseBean implements BusinessBeanRemote {

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<BaseBusiness> getBusinessList(long loginUserId, String keyword) {
		return BusinessDao.getBusinessList(getDao(), loginUserId, keyword);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public EditedBusiness find(int businessId) {
		return BusinessDao.find(getDao(), businessId);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public int add(long loginUserId, EditedBusiness business) {
		return BusinessDao.add(getDao(), loginUserId, business);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void delete(long loginUserId, Integer[] businessId) {
		BusinessDao.delete(getDao(), loginUserId, businessId);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void edit(long loginUserId, EditedBusiness business) {
		BusinessDao.edit(getDao(), loginUserId, business);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public QueryResult<EditedBusiness> queryBusiness(boolean queryCount,
			long loginUserId, Integer filter, String filterContent,
			int firstResult, int maxResults) {
		return BusinessDao.queryBusiness(getDao(), queryCount, loginUserId,
				filter, filterContent, firstResult, maxResults);
	}
}
