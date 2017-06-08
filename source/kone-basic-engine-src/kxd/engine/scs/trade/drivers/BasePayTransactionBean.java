package kxd.engine.scs.trade.drivers;

import kxd.remote.scs.transaction.Request;
import kxd.remote.scs.transaction.Response;
import kxd.remote.scs.transaction.Result;
import kxd.remote.scs.transaction.TradeError;
import kxd.remote.scs.transaction.TradeTimeoutError;
import kxd.remote.scs.util.emun.PayStatus;
import kxd.remote.scs.util.emun.TradePhase;
import kxd.remote.scs.util.emun.TradeResult;
import kxd.remote.scs.util.emun.TradeStatus;

import org.apache.log4j.Logger;

/**
 * 最基本的交易会话Bean的抽象实现，完成远程数据包的转换处理
 * 
 * @author zhaom
 * 
 */
abstract public class BasePayTransactionBean extends BaseTransactionBean {
	abstract public Logger getLogger();

	/**
	 * 付费处理之前的预处理动作
	 * 
	 * @param req
	 *            交易请求
	 * @param result
	 *            传入此参数，可按需要修改参数中的某些成员值
	 * @throws TradeError
	 */
	abstract public void payBefore(Request req, Result result) throws Throwable;

	/**
	 * 付费，将结果填充至result中，如果付费失败，则需要抛出TradeError
	 * 
	 * @param req
	 *            交易请求，如果需要的话，可以回传付费交易中得到的参数，可用于接下来的业务处理交易
	 * @param result
	 *            交易结果，传出付费成功后的信息，可用于接下来的业务处理交易
	 * @throws TradeError
	 *             付费失败时抛出
	 * @throws TradeTimeoutError
	 *             付费超时时抛出
	 */
	abstract public void doPay(Request req, Result result, Response resp) throws Throwable;

	/**
	 * 是否需要支付冲正，本方法中默认为支付超时即需要支付冲正
	 * 
	 * @param req
	 * @param result
	 * @param e
	 *            异常信息，可以用于判断
	 * @return true - 需要支付冲正<br>
	 *         false - 不需要支付冲正，此时不会调支付冲正的接口
	 */
	public boolean isNeedPayCancel(Request req, Result result, Throwable e) {
		return result.getPhase().compareTo(TradePhase.BEFORE_TRADE) > 0
				&& e instanceof TradeTimeoutError;
	}

	/**
	 * 付费冲销
	 * 
	 * @param req
	 *            交易请求
	 * @param result
	 *            传入此参数，冲销后可按需要修改参数中的某些成员值
	 * @throws TradeError
	 */
	abstract public void payCancel(Request req, Result result) throws Throwable;

	/**
	 * 业务办理之前的预处理动作
	 * 
	 * @param req
	 *            交易请求
	 * @param result
	 *            传入此参数，可按需要修改参数中的某些成员值
	 * @throws TradeError
	 */
	abstract public void tradeBefore(Request req, Result result)
			throws Throwable;

	/**
	 * 业务办理
	 * 
	 * @param req
	 *            交易请求
	 * @param result
	 *            传入此参数，业务办理成功后可按需要修改参数中的某些成员值
	 * @param resp
	 *            返回至用户端的数据
	 * @throws TradeError
	 *             交易失败时抛出
	 * @throws TradeTimeoutError
	 *             交易超时时抛出
	 */
	abstract public void doTrade(Request req, Result result, Response resp)
			throws Throwable;

	/**
	 * 是否需要业务冲正，本方法中默认为不冲正
	 * 
	 * @param req
	 * @param result
	 * @param e
	 *            异常信息，可以用于判断
	 * @return true - 需要业务冲正<br>
	 *         false - 不需要业务冲正，此时不会调业务冲正的接口
	 */
	public boolean isNeedTradeCancel(Request req, Result result, Throwable e) {
		return false;
	}

	/**
	 * 撤销业务办理
	 * 
	 * @param req
	 *            交易请求
	 * @param result
	 *            传入此参数，业务返销成功后可按需要修改参数中的某些成员值
	 * @param resp
	 * @throws TradeError
	 *             交易失败时抛出
	 * @throws TradeTimeoutError
	 *             交易超时时抛出
	 */
	abstract public void tradeCancel(Request req, Result result)
			throws Throwable;

	/**
	 * 做一笔业务
	 * 
	 * @param req
	 *            终端请求包
	 * @param Result
	 *            交易返回结果
	 * @param resp
	 *            终端响应包
	 */
	final protected void trade(Request req, Result result, Response resp)
			throws Throwable {
		payBefore(req, result);
		try {
			result.setPayStatus(PayStatus.PAY_TIMEOUT);
			result.setPhase(TradePhase.PAY);
			result.setResult(TradeResult.TIMEOUT);
			doPay(req, result, resp);
			result.setPayStatus(PayStatus.PAY_SUCCESS);
			result.setPhase(TradePhase.BEFORE_TRADE);
			result.setResult(TradeResult.FAILURE);
			tradeBefore(req, result);
			result.setTradeStatus(TradeStatus.TRADE_TIMEOUT);
			result.setPhase(TradePhase.TRADE);
			result.setResult(TradeResult.TIMEOUT);
			doTrade(req, result, resp);
			if (req.isRedo())
				result.setTradeStatus(TradeStatus.TRADE_SUCCESS_REDO);
			else
				result.setTradeStatus(TradeStatus.TRADE_SUCCESS);
			result.setResult(TradeResult.SUCCESS);
		} catch (Throwable e) {
			if (e instanceof TradeTimeoutError) {
				result.setResult(TradeResult.TIMEOUT);
				if (result.getPhase().compareTo(TradePhase.BEFORE_TRADE) < 0) {
					result.setPayStatus(PayStatus.PAY_TIMEOUT);
				} else {
					result.setTradeStatus(TradeStatus.TRADE_TIMEOUT);
				}
			} else {
				result.setResult(TradeResult.FAILURE);
				if (result.getPhase().compareTo(TradePhase.BEFORE_TRADE) < 0) {
					result.setPayStatus(PayStatus.PAY_FAILURE);
				} else {
					if (req.isRedo())
						result.setTradeStatus(TradeStatus.TRADE_FAILURE_REDO);
					else
						result.setTradeStatus(TradeStatus.TRADE_FAILURE);
				}
			}
			if (isNeedTradeCancel(req, result, e)) {
				getLogger().debug("Transaction is canceling...");
				try {
					tradeCancel(req, result);
					result.setResult(TradeResult.FAILURE);
					result.setTradeStatus(TradeStatus.TRADE_FAILURE_RESOLD);
					getLogger().debug("Transaction is canceled.");
				} catch (Throwable ex) {
					getLogger().error("Transaction cancel failure:", ex);
				}
			}
			if (isNeedPayCancel(req, result, e)) {
				try {
					getLogger().debug("Payment is withdrawning...");
					payCancel(req, result);
					if (result.getPhase().compareTo(TradePhase.BEFORE_TRADE) < 0) {
						result.setResult(TradeResult.FAILURE);
					}
					result.setPhase(TradePhase.PAY);
					result.setPayStatus(PayStatus.PAY_FAILURE_RESOLD);
					getLogger().debug("Payment has been withdrawn.");
				} catch (Throwable ex) {
					getLogger().error("Pay withdrawn failure:", ex);
				}
			}
			throw e;
		}
	}
}
