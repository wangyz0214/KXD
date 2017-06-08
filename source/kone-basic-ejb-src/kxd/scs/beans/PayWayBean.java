package kxd.scs.beans;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import kxd.engine.dao.BaseBean;
import kxd.remote.scs.beans.BasePayWay;
import kxd.remote.scs.interfaces.PayWayBeanRemote;
import kxd.remote.scs.util.QueryResult;
import kxd.scs.dao.appdeploy.PayWayDao;

@Stateless(name = "kxd-ejb-payWayBean", mappedName = "kxd-ejb-payWayBean")
public class PayWayBean extends BaseBean implements PayWayBeanRemote {
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<BasePayWay> getPayWayList(long loginUserId, String keyword) {
		return PayWayDao.getPayWayList(getDao(), loginUserId, keyword);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public BasePayWay find(int payWayId) {
		return PayWayDao.find(getDao(), payWayId);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public short add(long loginUserId, BasePayWay payWay) {
		return PayWayDao.add(getDao(), loginUserId, payWay);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void delete(long loginUserId, Short[] payWayId) {
		PayWayDao.delete(getDao(), loginUserId, payWayId);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void edit(long loginUserId, BasePayWay payWay) {
		PayWayDao.edit(getDao(), loginUserId, payWay);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public QueryResult<BasePayWay> queryPayWay(boolean queryCount,
			long loginUserId, Integer filter, String filterContent,
			int firstResult, int maxResults) {
		return PayWayDao.queryPayWay(getDao(), queryCount, loginUserId, filter,
				filterContent, firstResult, maxResults);
	}
}
