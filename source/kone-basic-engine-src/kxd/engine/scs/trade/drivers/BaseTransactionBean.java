package kxd.engine.scs.trade.drivers;

import java.io.StringWriter;
import java.util.Date;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.naming.NamingException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import kxd.engine.cache.beans.sts.CachedPayWay;
import kxd.engine.cache.beans.sts.CachedTradeCode;
import kxd.engine.helper.CacheHelper;
import kxd.engine.helper.DaoHelper;
import kxd.engine.helper.MonitorHelper;
import kxd.remote.scs.transaction.Request;
import kxd.remote.scs.transaction.Response;
import kxd.remote.scs.transaction.Result;
import kxd.remote.scs.transaction.TradeError;
import kxd.remote.scs.transaction.TransactionResponse;
import kxd.remote.scs.util.AppException;
import kxd.remote.scs.util.emun.PayStatus;
import kxd.remote.scs.util.emun.TradePhase;
import kxd.remote.scs.util.emun.TradeResult;
import kxd.remote.scs.util.emun.TradeStatus;
import kxd.util.StringUnit;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * 最基本的交易会话Bean的抽象实现，完成远程数据包的转换处理
 * 
 * @author zhaom
 * 
 */
abstract public class BaseTransactionBean {
	// private static final Logger logger = Logger
	// .getLogger(BaseTransactionBean.class);

	// static {
	// try {
	// loadConfig(KoneUtil.getConfigPath() + "monitor-service-config.xml");
	// } catch (Throwable e) {
	// logger.error("load [monitor-service-config.xml] failed: ", e);
	// }
	// }
	//
	// static void loadConfig(String file) {
	// try {
	// DocumentBuilderFactory factory = DocumentBuilderFactory
	// .newInstance();
	// DocumentBuilder builder = factory.newDocumentBuilder();
	// Document doc = builder.parse(file);
	// NodeList list = doc.getElementsByTagName("tradeeventlisteners");
	// for (int i = 0; i < list.getLength(); i++) {
	// Element node = (Element) list.item(i);
	// NodeList list1 = node.getElementsByTagName("listener");
	// for (int j = 0; j < list1.getLength(); j++) {
	// node = (Element) list1.item(j);
	// if (node.hasAttribute("class")
	// && !node.getAttribute("class").trim().isEmpty()) {
	// String cn = node.getAttribute("class").trim();
	// try {
	// MonitorHelper.tradeEventListeners
	// .add((TradeEventListener) Class.forName(cn)
	// .newInstance());
	// } catch (Throwable e) {
	// logger.error("tradeeventlistener[class=" + cn
	// + "] init error:", e);
	// }
	// }
	// }
	// }
	// } catch (Throwable e) {
	// e.printStackTrace();
	// }
	// }

	/**
	 * 获取日志记录器
	 * 
	 */
	abstract public Logger getLogger();

	/**
	 * 交易初始化
	 * 
	 * @param req
	 * @param result
	 */
	abstract protected void tradeInit(Request req, Result result);

	/**
	 * 交易完成后的清理动作
	 * 
	 * @param req
	 * @param result
	 */
	abstract protected void tradeClean(Request req, Result result);

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
	abstract protected void trade(Request req, Result result, Response resp)
			throws Throwable;

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public TransactionResponse trade(Request req) throws TradeError {
		TransactionResponse r = new TransactionResponse();
		r.result = new Result(req);
		r.result.setPhase(TradePhase.BEFORE_PAY);
		r.result.setResult(TradeResult.FAILURE);
		r.result.setPayStatus(PayStatus.NOT_PAY);
		r.result.setTradeStatus(TradeStatus.NOT_TRADE);
		r.result.setTradeStartTime(new Date());
		CachedTradeCode cachedTradeCode = null;
		if (req.getTradeCode().getTradeCodeId() != null)
			cachedTradeCode = CacheHelper.tradeCodeMap.get(req.getTradeCode()
					.getTradeCodeId());
		if (cachedTradeCode != null) {
			final CachedPayWay payWay = cachedTradeCode.getPayWay();
			if (payWay != null) {
				if (payWay.isNeedTrade()) {
					r.result.setPhase(TradePhase.BEFORE_PAY);
					r.result.setPayStatus(PayStatus.NOT_PAY);
				} else {
					r.result.setPhase(TradePhase.BEFORE_TRADE);
					r.result.setPayStatus(PayStatus.PAY_SUCCESS);
				}
			}
		}
		// r.result.setBankGlide(null);
		try {
			try {
				if (!req.isRedo()) // 补发交易不插入数据库
					DaoHelper.insertTradeDetail(req, r.result, false);
			} catch (NamingException e1) {
				throw new TradeError("DB", "插入数据库失败：", e1);
			}
			r.result.setTradeDetailInserted(true);
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder;
			Element root, result, content;
			Document doc;
			try {
				builder = factory.newDocumentBuilder();

				doc = builder.newDocument();
				root = doc.createElement("kone");
				doc.appendChild(root);

				result = doc.createElement("result");
				root.appendChild(result);

				content = doc.createElement("content");
				root.appendChild(content);
			} catch (Throwable e) {
				DaoHelper.updateTradeDetail(req, r.result, false,
						StringUnit.getExceptionMessage(e));
				// getLogger().error("trade error:", e);
				throw new TradeError(r.result, e);
			}
			try {

				Response resp = new Response(doc, content, result);

				result.setAttribute("success", "true");
				result.setAttribute("message", "操作成功");
				tradeInit(req, r.result);
				try {
					trade(req, r.result, resp);
				} finally {
					try {
						tradeClean(req, r.result);
					} catch (Throwable e) {
					}
				}
				if (req.isRedo())
					r.result.setResultInfo("补发成功");
				else
					r.result.setResultInfo("交易成功");
				DaoHelper.updateTradeDetail(req, r.result, true,
						req.isRedo() ? "补发成功" : "交易成功");
				r.data = xmlToBytes(doc);
				return r;
			} catch (AppException e) {
				throw e;
			} catch (TradeError e) {
				DaoHelper.updateTradeDetail(req, r.result, false,
						StringUnit.getExceptionMessage(e));
				// getLogger().error("trade error:", e);
				e.setResult(r.result);
				try {
					e.setData(xmlToBytes(doc));
				} catch (Throwable ex) {
					e.setData(null);
				}
				throw e;
			} catch (Throwable e) {
				DaoHelper.updateTradeDetail(req, r.result, false,
						StringUnit.getExceptionMessage(e));
				// getLogger().error("trade error:", e);
				TradeError er = new TradeError(r.result, e);
				try {
					er.setData(xmlToBytes(doc));
				} catch (Throwable ex) {
					er.setData(null);
				}
				throw er;
			}
		} finally {
			r.result.setTradeEndTime(new Date());
			MonitorHelper.onTradeEvent(req.getTradeCode(), r.result);
		}
	}

	private byte[] xmlToBytes(Document doc) {
		try {
			TransformerFactory tfactory = TransformerFactory.newInstance();
			Transformer transformer = tfactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StringWriter wr = new StringWriter();
			StreamResult sr = new StreamResult(wr);
			transformer.setOutputProperty("encoding", "utf-8");
			transformer.transform(source, sr);
			String str = wr.getBuffer().toString();
			return str.getBytes("utf-8");
		} catch (Throwable e) {
			throw new AppException(e);
		}
	}
}
