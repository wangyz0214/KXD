package kxd.scs.beans;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import kxd.engine.dao.BaseBean;
import kxd.remote.scs.beans.BasePayItem;
import kxd.remote.scs.beans.appdeploy.EditedPayItem;
import kxd.remote.scs.interfaces.PayItemBeanRemote;
import kxd.remote.scs.util.QueryResult;
import kxd.scs.dao.appdeploy.PayItemDao;

@Stateless(name = "kxd-ejb-payItemBean", mappedName = "kxd-ejb-payItemBean")
public class PayItemBean extends BaseBean implements PayItemBeanRemote {

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<BasePayItem> getPayItemList(long loginUserId, String keyword) {
		return PayItemDao.getPayItemList(getDao(), loginUserId, keyword);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public EditedPayItem find(int payItemId) {
		return PayItemDao.find(getDao(), payItemId);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public short add(long loginUserId, EditedPayItem payItem) {
		return PayItemDao.add(getDao(), loginUserId, payItem);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void delete(long loginUserId, Short[] payItemId) {
		PayItemDao.delete(getDao(), loginUserId, payItemId);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void edit(long loginUserId, EditedPayItem payItem) {
		PayItemDao.edit(getDao(), loginUserId, payItem);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public QueryResult<EditedPayItem> queryPayItem(boolean queryCount,
			long loginUserId, Integer filter, String filterContent,
			int firstResult, int maxResults) {
		return PayItemDao.queryPayItem(getDao(), queryCount, loginUserId,
				filter, filterContent, firstResult, maxResults);
	}
}
