package kxd.scs.beans;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import kxd.engine.dao.BaseBean;
import kxd.remote.scs.beans.BasePageElement;
import kxd.remote.scs.beans.appdeploy.EditedPageElement;
import kxd.remote.scs.interfaces.PageElementBeanRemote;
import kxd.remote.scs.util.QueryResult;
import kxd.scs.dao.appdeploy.PageElementDao;

@Stateless(name = "kxd-ejb-pageElementBean", mappedName = "kxd-ejb-pageElementBean")
public class PageElementBean extends BaseBean implements PageElementBeanRemote {

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<BasePageElement> getPageElementList(long loginUserId,
			String keyword) {
		return PageElementDao
				.getPageElementList(getDao(), loginUserId, keyword);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public EditedPageElement find(int pageElementId) {
		return PageElementDao.find(getDao(), pageElementId);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public int add(long loginUserId, EditedPageElement pageElement) {
		return PageElementDao.add(getDao(), loginUserId, pageElement);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void delete(long loginUserId, int[] pageElementId) {
		PageElementDao.delete(getDao(), loginUserId, pageElementId);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void edit(long loginUserId, EditedPageElement pageElement) {
		PageElementDao.edit(getDao(), loginUserId, pageElement);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public QueryResult<EditedPageElement> queryPageElement(boolean queryCount,
			long loginUserId, Integer filter, String filterContent,
			int firstResult, int maxResults) {
		return PageElementDao.queryPageElement(getDao(), queryCount,
				loginUserId, filter, filterContent, firstResult, maxResults);
	}
}
