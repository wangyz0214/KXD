package kxd.scs.beans;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import kxd.engine.dao.BaseBean;
import kxd.remote.scs.beans.BaseFileCategory;
import kxd.remote.scs.interfaces.FileCategoryBeanRemote;
import kxd.remote.scs.util.QueryResult;
import kxd.scs.dao.fileservice.FileCategoryDao;

@Stateless(name = "kxd-ejb-fileCategoryBean", mappedName = "kxd-ejb-fileCategoryBean")
public class FileCategoryBean extends BaseBean implements
		FileCategoryBeanRemote {

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<BaseFileCategory> getFileCategoryList(long loginUserId,
			String keyword) {
		return FileCategoryDao.getFileCategoryList(getDao(), loginUserId,
				keyword);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public BaseFileCategory find(short fileCategoryId) {
		return FileCategoryDao.find(getDao(), fileCategoryId);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public int add(long loginUserId, BaseFileCategory fileCategory) {
		return FileCategoryDao.add(getDao(), loginUserId, fileCategory);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void delete(long loginUserId, Short[] fileCategoryId) {
		FileCategoryDao.delete(getDao(), loginUserId, fileCategoryId);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void edit(long loginUserId, BaseFileCategory fileCategory) {
		FileCategoryDao.edit(getDao(), loginUserId, fileCategory);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public QueryResult<BaseFileCategory> queryFileCategory(boolean queryCount,
			long loginUserId, Integer filter, String filterContent,
			int firstResult, int maxResults) {
		return FileCategoryDao.queryFileCategory(getDao(), queryCount,
				loginUserId, filter, filterContent, firstResult, maxResults);
	}
}
