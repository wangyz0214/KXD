package kxd.scs.beans;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import kxd.engine.dao.BaseBean;
import kxd.remote.scs.beans.BaseInfoCategory;
import kxd.remote.scs.interfaces.InfoCategoryBeanRemote;
import kxd.remote.scs.util.QueryResult;
import kxd.scs.dao.publish.InfoCategoryDao;

@Stateless(name = "kxd-ejb-infoCategoryBean", mappedName = "kxd-ejb-infoCategoryBean")
public class InfoCategoryBean extends BaseBean implements
		InfoCategoryBeanRemote {

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<BaseInfoCategory> getInfoCategoryList(long loginUserId,
			String keyword) {
		return InfoCategoryDao.getInfoCategoryList(getDao(), loginUserId,
				keyword);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public BaseInfoCategory find(short infoCategoryId) {
		return InfoCategoryDao.find(getDao(), infoCategoryId);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void add(long loginUserId, BaseInfoCategory infoCategory) {
		InfoCategoryDao.add(getDao(), loginUserId, infoCategory);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void delete(long loginUserId, short[] infoCategoryId) {
		InfoCategoryDao.delete(getDao(), loginUserId, infoCategoryId);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void edit(long loginUserId, BaseInfoCategory infoCategory) {
		InfoCategoryDao.edit(getDao(), loginUserId, infoCategory);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public QueryResult<BaseInfoCategory> queryInfoCategory(boolean queryCount,
			long loginUserId, Integer filter, String filterContent,
			int firstResult, int maxResults) {
		return InfoCategoryDao.queryInfoCategory(getDao(), queryCount,
				loginUserId, filter, filterContent, firstResult, maxResults);
	}
}
