package kxd.scs.beans;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import kxd.engine.dao.BaseBean;
import kxd.remote.scs.beans.BaseAppCategory;
import kxd.remote.scs.beans.appdeploy.EditedAppCategory;
import kxd.remote.scs.interfaces.AppCategoryBeanRemote;
import kxd.remote.scs.util.QueryResult;
import kxd.scs.dao.appdeploy.AppCategoryDao;

@Stateless(name = "kxd-ejb-appCategoryBean", mappedName = "kxd-ejb-appCategoryBean")
public class AppCategoryBean extends BaseBean implements AppCategoryBeanRemote {
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<BaseAppCategory> getAppCategoryList(long loginUserId,
			String keyword) {
		return AppCategoryDao
				.getAppCategoryList(getDao(), loginUserId, keyword);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public EditedAppCategory find(int appCategoryId) {
		return AppCategoryDao.find(getDao(), appCategoryId);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public int add(long loginUserId, EditedAppCategory appCategory) {
		return AppCategoryDao.add(getDao(), loginUserId, appCategory);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void delete(long loginUserId, Integer[] appCategoryId) {
		AppCategoryDao.delete(getDao(), loginUserId, appCategoryId);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void edit(long loginUserId, EditedAppCategory appCategory) {
		AppCategoryDao.edit(getDao(), loginUserId, appCategory);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public QueryResult<EditedAppCategory> queryAppCategory(boolean queryCount,
			long loginUserId, Integer filter, String filterContent,
			int firstResult, int maxResults) {
		return AppCategoryDao.queryAppCategory(getDao(), queryCount,
				loginUserId, filter, filterContent, firstResult, maxResults);
	}
}
