package kxd.scs.beans.transaction;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import kxd.engine.dao.Dao;
import kxd.engine.helper.MonitorHelper;
import kxd.engine.scs.trade.drivers.BasePayTransactionBean;
import kxd.engine.scs.trade.drivers.InterfaceEventData;
import kxd.net.connection.jndi.Lookuper;
import kxd.net.naming.LoopNamingContext;
import kxd.remote.scs.interfaces.service.SerialNoServiceBeanRemote;
import kxd.remote.scs.transaction.Request;
import kxd.remote.scs.transaction.Response;
import kxd.remote.scs.transaction.Result;
import kxd.remote.scs.transaction.TradeError;
import kxd.remote.scs.transaction.TransactionBeanRemote;
import kxd.remote.scs.transaction.TransactionResponse;
import kxd.remote.scs.util.emun.TradeResult;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

@Stateless(name = "kxd-ejb-testTransactionBean", mappedName = "kxd-ejb-testTransactionBean")
public class TestTransactionBean extends BasePayTransactionBean implements
		TransactionBeanRemote {

	Logger logger = Logger.getLogger(TestTransactionBean.class);

	@Override
	public void doPay(Request req, Result result, Response resp)
			throws Throwable {
		result.debug(logger, "trade");
	}

	void payFee(Request req, Result result, Response resp) throws Throwable {
		int sleep = req.getParameterIntDef("sleep", 1000);
		Thread.sleep(sleep);
		String bankGlide = req.getParameterDef("bankglide", "aaa");
		String termGlide = "";
		if (req.getTradeCode().getTermGlide() != null) {
			termGlide = req.getTradeCode().getTermGlide().toString();
		}
		String userno = req.getTradeCode().getUserno();
		if (!userno.startsWith("1")) {
			Element el = resp.createElement("tradeResult");
			el.setAttribute("userno", userno);
			resp.getContentElement().appendChild(el);
			throw new TradeError("NP", "错误的手机号码");
		}

		LoopNamingContext context = new LoopNamingContext("cache");
		Dao dao = null;
		try {
			SerialNoServiceBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-serialNoServiceBean",
					SerialNoServiceBeanRemote.class);
			String serial = Long.toString(bean.getNextDaySerial("testtrade", 1,
					99999999));
			while (serial.length() < 8) {
				serial = "0" + serial;
			}
			dao = new Dao("kss");
			dao.execute(
					"insert into testtrade(tradeglide,"
							+ "tradecode,tradeamount,"
							+ "traderesult,termglide,bankglide,userno) values(?1,?2,?3,?4,?5,?6,?7)",
					serial, "pay", req.getTradeCode().getAmount(), 0,
					termGlide, bankGlide, userno);
			Element el = resp.createElement("tradeResult");
			el.setAttribute("tradeGlide", serial);
			resp.getContentElement().appendChild(el);
			result.setTradeGlide(serial);
			MonitorHelper.onInterfaceEvent(new InterfaceEventData(0, (short) 1,
					TradeResult.SUCCESS, 100));
		} finally {
			if (dao != null)
				dao.close();
			context.close();
		}
	}

	@Override
	public void doTrade(Request req, Result result, Response resp)
			throws Throwable {
		String tradeCode = req.getTradeCode().getTradeCode();
		if (tradeCode.equals("pay")) {
			payFee(req, result, resp);
		} else if (tradeCode.equals("query")) {
			Element el = resp.getDocument().createElement("detail");
			resp.getContentElement().appendChild(el);
			el.setAttribute("通讯费", "100.00");
		} else {
			throw new TradeError("NC", "错误的交易代码");
		}
		result.debug(logger, "trade");
	}

	@Override
	public void payBefore(Request req, Result result) throws Throwable {
		result.debug(logger, "pay before");
	}

	@Override
	public void payCancel(Request req, Result result) throws Throwable {
		result.debug(logger, "trade cancel");
	}

	@Override
	public void tradeBefore(Request req, Result result) throws Throwable {
		result.debug(logger, "trade before");
	}

	@Override
	public void tradeCancel(Request req, Result result) throws Throwable {
		result.debug(logger, "trade cancel");
	}

	@Override
	public Logger getLogger() {
		return logger;
	}

	@Override
	protected void tradeClean(Request req, Result result) {
		result.debug(logger, "trade clean");
		// result.setPhase(TradePhase.BEFORE_TRADE);
		// result.setResult(TradeResult.FAILURE);
		// result.setTradeStatus(TradeStatus.NOT_TRADE);
	}

	@Override
	protected void tradeInit(Request req, Result result) {
		result.debug(logger, "trade init");
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public TransactionResponse trade(Request req) throws TradeError {
		return super.trade(req);
	}
}
