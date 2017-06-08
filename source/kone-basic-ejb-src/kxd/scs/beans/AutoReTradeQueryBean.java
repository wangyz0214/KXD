package kxd.scs.beans;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import kxd.engine.dao.BaseBean;
import kxd.remote.scs.interfaces.AutoReTradeQueryBeanRemote;
import kxd.remote.scs.transaction.AutoReTradeData;
import kxd.scs.dao.trade.TradeDao;

import org.apache.log4j.Logger;

@Stateless(name = "kxd-ejb-autoReTradeQueryBean", mappedName = "kxd-ejb-autoReTradeQueryBean")
public class AutoReTradeQueryBean extends BaseBean implements
		AutoReTradeQueryBeanRemote {

	static Logger logger = Logger.getLogger(AutoReTradeQueryBean.class);

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<AutoReTradeData> getNeedAutoReTradeList() {
		return TradeDao.queryNeedAutoReTradeList(getDao());
	}
}
