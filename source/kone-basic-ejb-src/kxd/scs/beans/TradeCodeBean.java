package kxd.scs.beans;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import kxd.engine.dao.BaseBean;
import kxd.remote.scs.beans.BaseTradeCode;
import kxd.remote.scs.beans.appdeploy.EditedTradeCode;
import kxd.remote.scs.interfaces.TradeCodeBeanRemote;
import kxd.remote.scs.util.QueryResult;
import kxd.scs.dao.appdeploy.TradeCodeDao;

@Stateless(name = "kxd-ejb-tradeCodeBean", mappedName = "kxd-ejb-tradeCodeBean")
public class TradeCodeBean extends BaseBean implements TradeCodeBeanRemote {
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<BaseTradeCode> getTradeCodeList(long loginUserId, String keyword) {
		return TradeCodeDao.getTradeCodeList(getDao(), loginUserId, keyword);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public EditedTradeCode find(int tradeCodeId) {
		return TradeCodeDao.find(getDao(), tradeCodeId);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public int add(long loginUserId, EditedTradeCode tradeCode) {
		return TradeCodeDao.add(getDao(), loginUserId, tradeCode);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void delete(long loginUserId, Integer[] tradeCodeId) {
		TradeCodeDao.delete(getDao(), loginUserId, tradeCodeId);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void edit(long loginUserId, EditedTradeCode tradeCode) {
		TradeCodeDao.edit(getDao(), loginUserId, tradeCode);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public QueryResult<EditedTradeCode> queryTradeCode(boolean queryCount,
			long loginUserId, Integer filter, String filterContent,
			int firstResult, int maxResults) {
		return TradeCodeDao.queryTradeCode(getDao(), queryCount, loginUserId,
				filter, filterContent, firstResult, maxResults);
	}
}
