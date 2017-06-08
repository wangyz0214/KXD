package kxd.engine.scs.trade;

import java.net.InetAddress;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import kxd.engine.cache.beans.sts.CachedBankTerm;
import kxd.engine.cache.beans.sts.CachedPayWay;
import kxd.engine.cache.beans.sts.CachedTerm;
import kxd.engine.cache.beans.sts.CachedTermConfig;
import kxd.engine.cache.beans.sts.CachedTradeCode;
import kxd.engine.helper.CacheHelper;
import kxd.engine.helper.TermHelper;
import kxd.net.connection.jndi.Lookuper;
import kxd.net.naming.LoopNamingContext;
import kxd.net.naming.NamingContext;
import kxd.remote.scs.interfaces.AutoReTradeQueryBeanRemote;
import kxd.remote.scs.transaction.AutoReTradeData;
import kxd.remote.scs.transaction.Request;
import kxd.remote.scs.transaction.Response;
import kxd.remote.scs.transaction.Result;
import kxd.remote.scs.transaction.TradeDriver;
import kxd.remote.scs.transaction.TradeError;
import kxd.remote.scs.util.AppException;
import kxd.remote.scs.util.emun.PayStatus;
import kxd.remote.scs.util.emun.TradePhase;
import kxd.util.DateTime;
import kxd.util.StringUnit;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * 自动补交线程
 * 
 * @author zhaom
 * 
 */
public class AutoRePayRunnable implements Runnable {
	private static Logger logger = Logger.getLogger(AutoRePayRunnable.class);
	public static int interval = 600000;
	public static DateTime startTime, endTime;

	@Override
	public void run() {
		logger.info("auto repay thread started");
		while (!Thread.interrupted()) {
			Enumeration<AutoRePayConfig> en = XMLTradeExecutor.autoRePayList
					.elements();
			while (en.hasMoreElements())
				try {
					rePay(en.nextElement());
				} catch (Throwable e) {

				}
			try {
				Thread.sleep(interval);
			} catch (InterruptedException e) {
			}
		}
		logger.info("auto repay thread exited");
	}

	protected List<AutoReTradeData> getAutoReTradeList(
			AutoRePayConfig rePayConfig) throws Throwable {
		NamingContext context = new LoopNamingContext("db");
		try {
			AutoReTradeQueryBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, rePayConfig.beanName,
					AutoReTradeQueryBeanRemote.class);
			return bean.getNeedAutoReTradeList();
		} finally {
			context.close();
		}
	}

	final static String CONFIGPAYPREFIX = "$cache.repayconfig.";

	protected void rePay(AutoRePayConfig rePayConfig) throws Throwable {
		if (!rePayConfig.enabled)
			return;
		if (CacheHelper.termMc.get(CONFIGPAYPREFIX + rePayConfig.name) != null) // 如果有主机正在交易，则不做
			return;
		DateTime time = new DateTime();
		time.set(startTime.getYear(), startTime.getMonth(), startTime.getDay());
		if (!(time.after(startTime) && time.before(endTime)))
			return; // 判断是否在对账期内。
		List<AutoReTradeData> ls = getAutoReTradeList(rePayConfig);
		CacheHelper.termMc.set(CONFIGPAYPREFIX + rePayConfig.name, true,
				new DateTime().addHours(1).getTime());
		try {
			for (AutoReTradeData d : ls) {
				try {
					TermHelper.setReTrading(d.getTermGlide().toString(), true,
							new DateTime().addMinutes(5).getTime());
				} catch (Throwable e) {
				}
			}
			Thread.sleep(60000);
			for (AutoReTradeData d : ls) {
				try {
					doPay(d);
				} catch (Throwable e) {
					logger.error("retrade failure:", e);
				}
			}
		} finally {
			for (AutoReTradeData d : ls) {
				try {
					TermHelper.setReTrading(d.getTermGlide().toString(), false,
							null);
				} catch (Throwable e) {
				}
			}
			CacheHelper.termMc.delete(CONFIGPAYPREFIX + rePayConfig.name);
		}
	}

	protected void doPay(AutoReTradeData o) throws Throwable {
		CachedTradeCode cachedTradeCode = CacheHelper.tradeCodeMap
				.getByCodeServ(o.getTradeCode(), o.getTradeService());
		if (cachedTradeCode == null)
			return;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.newDocument();

		Element root = doc.createElement("kone");
		doc.appendChild(root);

		Element resultElement = doc.createElement("result");
		root.appendChild(resultElement);

		Element contentElement = doc.createElement("content");
		root.appendChild(contentElement);
		Response resp = new Response(doc, contentElement, resultElement);
		Hashtable<String, String> params = new Hashtable<String, String>();
		params.put("termid", Integer.toString(o.getTermid()));
		params.put("tradecode", o.getTradeCode());
		params.put("service", o.getTradeService());
		params.put("termglide", o.getTermGlide().toString());
		params.put("tradetime", o.getTradeTime());
		params.put("bankcardno",
				o.getBankCardNo() == null ? "" : o.getBankCardNo());
		params.put("userno", o.getUserNo());
		params.put("amount", o.getAmount());
		params.put("tradeamount", o.getTradeAmount());

		String lochost = "";
		try {
			InetAddress inet = InetAddress.getLocalHost();
			lochost = inet.getHostAddress();
		} catch (Throwable e) {
			lochost = "0.0.0.0";
		}

		String[] ps = o.getRedoParams().split("&");
		for (int i = 0; i < ps.length; i++) {
			String[] tmp = ps[i].split("=");
			if (tmp[0].trim().length() > 0) {
				if (tmp.length > 1)
					params.put(tmp[0], tmp[1]);
				else
					params.put(tmp[0], "");
			}
		}
		Request req = new Request(params);
		req.setRedo(true);
		req.setRemoteAddr(lochost);
		TradeDriver driver = null;
		Result result = new Result(req);
		int termId = req.getTradeCode().getTermId();
		CachedTermConfig termConfig = CacheHelper.termMap.get(termId);
		CachedTerm term = termConfig.getTerm();
		if (cachedTradeCode != null) {
			req.getTradeCode().setLogged(cachedTradeCode.isLogged());
			req.getTradeCode().setStated(cachedTradeCode.isStated());
			req.getTradeCode().setTradeCodeId(cachedTradeCode.getId());
			final CachedPayWay payWay = cachedTradeCode.getPayWay();
			if (payWay != null) {
				if (payWay.isNeedTrade()) {
					result.setPhase(TradePhase.BEFORE_PAY);
					result.setPayStatus(PayStatus.NOT_PAY);
					CachedBankTerm bt = term.getBankTerm();
					if (bt != null) { // 自动加入银联商户号和银联终端号
						req.getParams().put("$bank.merchant",
								bt.getMerchantAccount());
						req.getParams().put("$bank.termcode",
								bt.getBankTermCode());
					}
				} else {
					result.setPhase(TradePhase.BEFORE_TRADE);
					result.setPayStatus(PayStatus.PAY_SUCCESS);
				}
			}
		}
		result.setBankGlide(null);
		try {
			ServiceConfig config = XMLTradeExecutor.serviceMap.get(o
					.getTradeService());
			if (config == null)
				throw new TradeError("01", "交易服务[" + o.getTradeService()
						+ "]配置不正确 ：找不到配置项");
			Class<?> clazz = null;
			if (term != null) {
				clazz = config.appClasses.get(term.getAppId());
			}
			if (clazz == null)
				clazz = config.clazz;
			if (clazz == null)
				throw new TradeError("01", "交易服务[" + o.getTradeService()
						+ "]配置不正确 ：未配置class");
			driver = (TradeDriver) clazz.newInstance();
			driver.setConfigMap(config.params);
			driver.setTermConfig(termConfig);
			driver.setConfig(config);
			driver.tradeStart(req);
			result = driver.trade(req, resp);
		} catch (TradeError e) {
			if (e.getResult() != null)
				result = e.getResult();
			if (logger.isDebugEnabled())
				logger.error(e.getMessage(), e);
			throw e;
		} catch (AppException e) {
			if (logger.isDebugEnabled())
				logger.error(e.getMessage(), e);
			throw new TradeError("YW", StringUnit.getExceptionMessage(e));
		} catch (Throwable e) {
			if (logger.isDebugEnabled())
				logger.error(e.getMessage(), e);
			throw new TradeError("YW", StringUnit.getExceptionMessage(e));
		} finally {
			if (driver != null)
				driver.tradeComplete(req, resp);
		}
	}
}
