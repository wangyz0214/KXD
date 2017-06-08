package kxd.engine.scs.trade;

/**
 * 交易处理器，处理系统中所有业务相关的交易
 * ***************************************
 * 修改时间：2010-03-24
 * 修改人：lixin
 * 修改内容：增加了部分代码的注释，
 * 修改了result的返回错误码
 */
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.concurrent.ConcurrentHashMap;

import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kxd.engine.cache.beans.sts.CachedPayWay;
import kxd.engine.cache.beans.sts.CachedTerm;
import kxd.engine.cache.beans.sts.CachedTermConfig;
import kxd.engine.cache.beans.sts.CachedTradeCode;
import kxd.engine.helper.CacheHelper;
import kxd.engine.helper.DaoHelper;
import kxd.engine.helper.TermHelper;
import kxd.net.HttpRequest;
import kxd.net.XMLExecutor;
import kxd.net.connection.jndi.Lookuper;
import kxd.net.naming.LoopNamingContext;
import kxd.remote.scs.interfaces.service.TermServiceBeanRemote;
import kxd.remote.scs.transaction.Request;
import kxd.remote.scs.transaction.Response;
import kxd.remote.scs.transaction.Result;
import kxd.remote.scs.transaction.TradeDriver;
import kxd.remote.scs.transaction.TradeError;
import kxd.remote.scs.util.AppException;
import kxd.remote.scs.util.emun.PayStatus;
import kxd.remote.scs.util.emun.TradePhase;
import kxd.util.DataUnit;
import kxd.util.DateTime;
import kxd.util.KoneException;
import kxd.util.StringUnit;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class XMLTradeExecutor implements XMLExecutor {

	static Logger logger = Logger.getLogger("kxd");

	/**
	 * 保存当前交易服务配置项列表
	 */
	static public ConcurrentHashMap<String, ServiceConfig> serviceMap = new ConcurrentHashMap<String, ServiceConfig>();
	// static public ConcurrentHashMap<String, ConcurrentHashMap<Integer,
	// String>> serviceAppTradeClasses = new ConcurrentHashMap<String,
	// ConcurrentHashMap<Integer, String>>();
	static public ConcurrentHashMap<String, AutoRePayConfig> autoRePayList = new ConcurrentHashMap<String, AutoRePayConfig>();
	static public boolean clientverify = false;

	/**
	 * 保存当前的主密钥
	 */
	static public byte[] mainKey = null;

	public static synchronized byte[] getMainKey() {

		if (mainKey == null) {
			try {
				LoopNamingContext context = new LoopNamingContext("db");
				try {
					TermServiceBeanRemote bean = context.lookup(
							Lookuper.JNDI_TYPE_EJB, "kxd-ejb-termServiceBean",
							TermServiceBeanRemote.class);
					mainKey = bean.getMainKey();

				} finally {
					context.close();
				}

			} catch (Throwable e) {

			}
		}

		return mainKey;
	}

	static synchronized byte[] getWorkKey(int termId) throws NamingException {

		LoopNamingContext context = new LoopNamingContext("db");
		try {
			TermServiceBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-termServiceBean", TermServiceBeanRemote.class);
			return DataUnit.hexToBytes(bean.getWorkKey(termId));

		} finally {
			context.close();
		}
	}

	public void init(Document doc) {
		try { // 装入交易配置
			NodeList list = doc.getElementsByTagName("client");
			if (list.getLength() > 0) {
				Element node = (Element) list.item(0);
				if (node.hasAttribute("verify")) {
					boolean value = Boolean
							.valueOf(node.getAttribute("verify"));
					clientverify = value;
				}
			}

			list = doc.getElementsByTagName("service");
			for (int i = 0; i < list.getLength(); i++) {
				Element pnode = (Element) list.item(i);
				if (pnode.hasAttribute("name") && pnode.hasAttribute("class")) {
					ServiceConfig config = new ServiceConfig();
					String name = pnode.getAttribute("name");
					serviceMap.put(name, config);
					config.clazz = Class.forName(pnode.getAttribute("class"));
					if (pnode.hasAttribute("urlname")
							&& pnode.hasAttribute("beanname")) {
						config.defaultBean = new ServiceBean(
								pnode.getAttribute("beanname"),
								pnode.getAttribute("urlname"));
					}

					NodeList ls = pnode.getElementsByTagName("param");
					NodeList appls = pnode
							.getElementsByTagName("apptradeclass");
					for (int j = 0; j < ls.getLength(); j++) {
						Element node = (Element) ls.item(j);
						name = node.getAttribute("name");
						String value = node.getAttribute("value");
						if (!config.params.containsKey(name))
							config.params.put(name, value);
					}
					for (int j = 0; j < appls.getLength(); j++) {
						Element node = (Element) appls.item(j);
						Integer appId = Integer.valueOf(node
								.getAttribute("appid"));
						String value = node.getAttribute("class");
						if (value != null
								&& !config.appClasses.containsKey(appId))
							config.appClasses.put(appId,
									Class.forName(node.getAttribute("class")));
					}
					if (config.defaultBean != null) {
						NodeList beans = pnode.getElementsByTagName("bean");
						for (int j = 0; j < beans.getLength(); j++) {
							Element node = (Element) beans.item(j);
							String beanname = node.getAttribute("name");
							String jndiname = node.getAttribute("jndiname");
							if (beanname.equals(config.defaultBean.getName())) {
								logger.warn("bean name:[" + beanname
										+ "]与默认的bean名称相同，忽略");
								continue;
							}
							ServiceBean b = new ServiceBean(beanname, jndiname);
							b.orgIds = StringUnit.splitToInt(
									node.getAttribute("orgidlist"), ",");
							b.appIds = StringUnit.splitToInt(
									node.getAttribute("appidlist"), ",");
							b.excludeOrgIds = StringUnit
									.splitToInt(node
											.getAttribute("exclude_orgidlist"),
											",");
							b.excludeAppIds = StringUnit
									.splitToInt(node
											.getAttribute("exclude_appidlist"),
											",");
							config.beanList.add(b);
						}
					}
				}
			}
			list = doc.getElementsByTagName("autorepayconfig");
			AutoRePayRunnable.startTime = new DateTime("00:00:00", "HH:mm:ss");
			AutoRePayRunnable.endTime = new DateTime("23:59:59", "HH:mm:ss");
			if (list.getLength() > 0) {
				Element node = (Element) list.item(0);
				AutoRePayRunnable.interval = Integer.valueOf(node
						.getAttribute("interval")) * 60000;
				String time = node.getAttribute("effective-time");
				if (time != null && !time.trim().isEmpty()) {
					int index = time.indexOf(" - ");
					AutoRePayRunnable.startTime = new DateTime(time.substring(
							0, index).trim(), "HH:mm:ss");
					AutoRePayRunnable.endTime = new DateTime(time.substring(
							index + 3).trim(), "HH:mm:ss");
				}
			}
			AutoRePayRunnable.startTime.set(2010, 10, 01);
			AutoRePayRunnable.endTime.set(2010, 10, 01);

			list = doc.getElementsByTagName("autorepay");
			for (int i = 0; i < list.getLength(); i++) {
				Element node = (Element) list.item(i);
				AutoRePayConfig config = new AutoRePayConfig();
				config.beanName = node.getAttribute("beanname");
				config.enabled = "true".equals(node.getAttribute("enabled"));
				config.name = node.getAttribute("name");
				autoRePayList.put(node.getAttribute("name"), config);
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public void uninit() {
		serviceMap.clear();
	}

	protected void processTermOnlineStatus(Request req, CachedTermConfig term) {
		try {
			if (req.getTradeCode().getService().equals("system")
					&& req.getTradeCode().getTradeCode().equals("updatestatus"))
				return;
			if (term != null
					&& DateTime.secondsBetween(System.currentTimeMillis(), term
							.getTerm().getLastInlineTime()) > 120) { // 更新终端的在线状态
				//logger.warn("Terminal[id="
				//		+ term.getIdString()
				//		+ "] state may not be properly submitted. update online status.");
				TermHelper.updateTermStatus(term.getTerm(), false, 0, 0, null);
				term.getTerm().setLastInlineTime(System.currentTimeMillis());
				CacheHelper.termMap.put(term.getId(), term);
			}
		} catch (Throwable e) {
		}
	}

	protected void fillResultElement(Document doc, Element resultElement,
			Request req, Result result) throws Exception {
		if (resultElement == null) {
			NodeList ls = doc.getDocumentElement().getElementsByTagName(
					"result");
			for (int i = 0; i < ls.getLength(); i++) {
				Element e = (Element) ls.item(i);
				if (e.getParentNode() == doc.getDocumentElement()) {
					resultElement = e;
					break;
				}
			}
			if (resultElement == null)
				return;
		}
		Element el = doc.createElement("detail");
		resultElement.appendChild(el);
		if (req.getTradeCode().getTermGlide() != null)
			el.setTextContent(result.toJSONString(req.getTradeCode()
					.getTermGlide().toString(), req.getTradeCode()
					.getBankCardNo(), true));
		resultElement.setAttribute("tradestatus",
				Integer.toString(result.getTradeStatus().getValue()));
		resultElement.setAttribute("paystatus",
				Integer.toString(result.getPayStatus().getValue()));
		resultElement.setAttribute("tradestatus_desp", result.getTradeStatus()
				.toString());
		resultElement.setAttribute("paystatus_desp", result.getPayStatus()
				.toString());
	}

	/**
	 * 执行一个交易
	 * 
	 * @param request
	 *            交易请求
	 * @param xmlDoc
	 *            返回的XML文档对象，交易驱动可以使用该对象创建内容结点。
	 * @param contentElement
	 *            内容结点，具体执行器生成的全部结点应该是该结点的子结点
	 * @param resultElement
	 *            结果结点，默认包含success,message两个属性，由系统自动设定，具体执行器可以添加其他属性
	 */
	@Override
	public Document execute(HttpRequest request, HttpServletResponse response,
			Document xmlDoc, Element contentElement, Element resultElement)
			throws Throwable {
		TermHelper.checkSystemStarted();
		Hashtable<String, String> params = new Hashtable<String, String>();

		Enumeration<?> em = request.getParameterNames();
		while (em.hasMoreElements()) {
			String name = (String) em.nextElement();
			if (!name.equals("name") && !name.equals("class"))
				params.put(name, request.getParameter(name));
		}
		Request req = new Request(params);
		params.put("${requesturi}", request.getRequest().getRequestURL()
				.toString());
		String ip = getIpAddr(request.getRequest());
		req.setRemoteAddr(ip);
		req.setServerAddr(request.getRequest().getLocalAddr());
		String service = req.getTradeCode().getService();
		String tradecode = req.getTradeCode().getTradeCode();

		int termId = req.getTradeCode().getTermId();
		CachedTermConfig termconfig = CacheHelper.termMap.get(termId);
		if (termconfig != null)
			req.getTradeCode().setOrgId(termconfig.getTerm().getOrgId());
		else if (!("system".equals(service) && ("termactive".equals(tradecode))))
			throw new KoneException("非法终端");
		CachedTerm term = termconfig != null ? termconfig.getTerm() : null;
		String verifyCode = request.getParameterDef("verifycode", null);
		if (clientverify
				&& !("system".equals(service) && ("termactive"
						.equals(tradecode) || "termlogin".equals(tradecode)))) {
			if (verifyCode == null)
				throw new KoneException("非法连接");
		}

		if (clientverify && verifyCode != null) {
			String id = term.getLoginSessionID();
			if (!verifyCode.equals(id))
				throw new KoneException("非法连接");
		}
		CachedTradeCode cachedTradeCode = CacheHelper.tradeCodeMap
				.getByCodeServ(tradecode, service);
		Response resp = new Response(xmlDoc, contentElement, resultElement);

		if (cachedTradeCode != null) {
			req.getTradeCode().setLogged(cachedTradeCode.isLogged());
			req.getTradeCode().setStated(cachedTradeCode.isStated());
			req.getTradeCode().setTradeCodeId(cachedTradeCode.getId());
		}

		if (req.getTradeCode().isTrade() || req.getTradeCode().isLogged()
				|| req.getTradeCode().isStated()) { // 交易或需要统计或记录日志的业务
			if (req.getTradeCode().getTradeCodeId() == null)
				throw new KoneException("找不到对应的交易代码[service="
						+ req.getTradeCode().getService() + " tradeCode="
						+ req.getTradeCode().getTradeCode() + "]");
		}
		processTermOnlineStatus(req, termconfig);
		req.setTerm(term);

		Document doc = resp.getDocument();
		try {
			Result result = trade(termconfig, cachedTradeCode, req, resp);

			if (doc == resp.getDocument()) {
				resultElement.setAttribute("code", "00");
				if (result != null) {
					fillResultElement(resp.getDocument(), resultElement, req,
							result);
				}
				return null;

			} else {
				if (result != null) {
					fillResultElement(resp.getDocument(), null, req, result);
				}
				return resp.getDocument();
			}
		} catch (TradeError e) {
			resultElement.setAttribute("code", e.getErrorCode());
			Result result = e.getResult();
			if (result != null) {
				if (e.getData() != null && e.getData() instanceof Document)
					fillResultElement((Document) e.getData(), null, req, result);
				else
					fillResultElement(doc, resultElement, req, result);
			}
			throw new KoneException(e.getErrorDesp(), e, e.getData());
		} catch (Exception e) {
			resultElement.setAttribute("code", "01");
			throw e;
		} catch (Throwable e) {
			resultElement.setAttribute("code", "01");
			throw e;
		}
	}

	public String getIpAddr(HttpServletRequest request) {

		String ip = "";
		try {
			ip = request.getHeader("x-forwarded-for");
			if (ip == null || ip.length() == 0
					|| "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("Proxy-Client-IP");
			}
			if (ip == null || ip.length() == 0
					|| "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("WL-Proxy-Client-IP");
			}
			if (ip == null || ip.length() == 0
					|| "unknown".equalsIgnoreCase(ip)) {
				ip = request.getRemoteAddr();
			}
		} catch (Throwable e) {
			ip = "0.0.0.0";
		}
		return ip;
	}

	public Result trade(CachedTermConfig termConfig,
			CachedTradeCode cachedTradeCode, Request req, Response resp)
			throws TradeError, NamingException, InstantiationException,
			IllegalAccessException, ClassNotFoundException {

		TradeDriver driver = null;
		Result result = new Result(req);
		if (cachedTradeCode != null) {
			final CachedPayWay payWay = cachedTradeCode.getPayWay();
			if (payWay != null) {
				if (payWay.isNeedTrade()) {
					result.setPhase(TradePhase.BEFORE_PAY);
					result.setPayStatus(PayStatus.NOT_PAY);
				} else {
					result.setPhase(TradePhase.BEFORE_TRADE);
					result.setPayStatus(PayStatus.PAY_SUCCESS);
				}
			}
		}
		result.setBankGlide(null);
		try {
			ServiceConfig config = serviceMap.get(req.getTradeCode()
					.getService());
			if (config == null)
				throw new TradeError("01", "交易服务["
						+ req.getTradeCode().getService() + "]配置不正确 ：找不到配置项");
			Class<?> clazz = null;
			CachedTerm term = (CachedTerm) req.getTerm();
			if (config.appClasses != null && term != null) {
				clazz = config.appClasses.get(term.getAppId());
			}
			if (clazz == null)
				clazz = config.clazz;
			if (clazz == null)
				throw new TradeError("01", "交易服务["
						+ req.getTradeCode().getService() + "]配置不正确 ：未配置class");
			if (term != null && req.getTradeCode().getService().equals("pay"))
				logger.info("termid:" + req.getTradeCode().getTermId()
						+ "--service:" + req.getTradeCode().getService()
						+ "--appid:" + term.getAppId() + "--className:"
						+ clazz.getName());
			// req.getTradeCode().setTradeTime(new Date());
			driver = (TradeDriver) clazz.newInstance();
			driver.setTermConfig(termConfig);
			driver.setConfigMap(config.params);
			driver.setConfig(config);
			driver.tradeStart(req);
			result = driver.trade(req, resp);
			result.setPhase(TradePhase.TRADE);
			if (req.getTradeCode().isTrade())
				return result;
			else
				return null;
		} catch (TradeError e) {
			if (e.getResult() != null)
				result = e.getResult();
			try {
				if (!result.isTradeDetailInserted())
					DaoHelper.insertTradeDetail(req, result, true);
			} catch (Throwable ex) {
			}
			if (logger.isDebugEnabled())
				logger.error(e.getMessage(), e);
			throw e;
		} catch (AppException e) {
			try {
				if (!result.isTradeDetailInserted())
					DaoHelper.insertTradeDetail(req, result, true);
			} catch (Throwable ex) {
			}
			if (logger.isDebugEnabled())
				logger.error(e.getMessage(), e);
			throw new TradeError("YW", StringUnit.getExceptionMessage(e));
		} catch (Throwable e) {
			if (logger.isDebugEnabled())
				logger.error(e.getMessage(), e);
			try {
				if (!result.isTradeDetailInserted())
					DaoHelper.insertTradeDetail(req, result, true);
			} catch (Throwable ex) {
			}
			throw new TradeError("YW", StringUnit.getExceptionMessage(e));
		} finally {
			if (driver != null)
				driver.tradeComplete(req, resp);
		}
	}
}
