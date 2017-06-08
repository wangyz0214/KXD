package kxd.scs.beans;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import kxd.engine.dao.BaseBean;
import kxd.remote.scs.beans.BaseBusinessCategory;
import kxd.remote.scs.interfaces.BusinessCategoryBeanRemote;
import kxd.remote.scs.util.QueryResult;
import kxd.scs.dao.appdeploy.BusinessCategoryDao;

@Stateless(name = "kxd-ejb-businessCategoryBean", mappedName = "kxd-ejb-businessCategoryBean")
public class BusinessCategoryBean extends BaseBean implements
		BusinessCategoryBeanRemote {
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<BaseBusinessCategory> getBusinessCategoryList(long loginUserId,
			String keyword) {
		return BusinessCategoryDao.getBusinessCategoryList(getDao(),
				loginUserId, keyword);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public BaseBusinessCategory find(int businessCategoryId) {
		return BusinessCategoryDao.find(getDao(), businessCategoryId);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public int add(long loginUserId, BaseBusinessCategory businessCategory) {
		return BusinessCategoryDao.add(getDao(), loginUserId, businessCategory);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void delete(long loginUserId, Integer[] businessCategoryId) {
		BusinessCategoryDao.delete(getDao(), loginUserId, businessCategoryId);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void edit(long loginUserId, BaseBusinessCategory businessCategory) {
		BusinessCategoryDao.edit(getDao(), loginUserId, businessCategory);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public QueryResult<BaseBusinessCategory> queryBusinessCategory(
			boolean queryCount, long loginUserId, Integer filter,
			String filterContent, int firstResult, int maxResults) {
		return BusinessCategoryDao.queryBusinessCategory(getDao(), queryCount,
				loginUserId, filter, filterContent, firstResult, maxResults);
	}
}
