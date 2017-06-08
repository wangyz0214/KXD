package kxd.remote.scs.transaction;

import javax.ejb.Remote;

/**
 * 交易接口
 * 
 * @author 赵明
 */
@Remote
public interface TransactionBeanRemote {
	TransactionResponse trade(Request req) throws TradeError;
}
