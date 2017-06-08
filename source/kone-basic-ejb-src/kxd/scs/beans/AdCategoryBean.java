package kxd.scs.beans;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import kxd.engine.dao.BaseBean;
import kxd.remote.scs.beans.BaseAdCategory;
import kxd.remote.scs.interfaces.AdCategoryBeanRemote;
import kxd.remote.scs.util.QueryResult;
import kxd.scs.dao.publish.AdCategoryDao;

@Stateless(name = "kxd-ejb-adCategoryBean", mappedName = "kxd-ejb-adCategoryBean")
public class AdCategoryBean extends BaseBean implements AdCategoryBeanRemote {
	
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<BaseAdCategory> getAdCategoryList(long loginUserId,
			String keyword) {
		return AdCategoryDao.getAdCategoryList(getDao(), loginUserId, keyword);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public BaseAdCategory find(short adCategoryId) {
		return AdCategoryDao.find(getDao(), adCategoryId);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void add(long loginUserId, BaseAdCategory adCategory) {
		AdCategoryDao.add(getDao(), loginUserId, adCategory);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void delete(long loginUserId, short[] adCategoryId) {
		AdCategoryDao.delete(getDao(), loginUserId, adCategoryId);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void edit(long loginUserId, BaseAdCategory adCategory) {
		AdCategoryDao.edit(getDao(), loginUserId, adCategory);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public QueryResult<BaseAdCategory> queryAdCategory(boolean queryCount,
			long loginUserId, Integer filter, String filterContent,
			int firstResult, int maxResults) {
		return AdCategoryDao.queryAdCategory(getDao(), queryCount, loginUserId,
				filter, filterContent, firstResult, maxResults);
	}
}
