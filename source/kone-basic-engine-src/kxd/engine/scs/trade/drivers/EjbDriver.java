package kxd.engine.scs.trade.drivers;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.naming.NamingException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import jxl.common.Logger;
import kxd.engine.cache.beans.sts.CachedTerm;
import kxd.engine.cache.beans.sts.CachedTermConfig;
import kxd.engine.helper.TermHelper;
import kxd.engine.scs.trade.ServiceBean;
import kxd.engine.scs.trade.ServiceConfig;
import kxd.net.connection.jndi.Lookuper;
import kxd.net.naming.LoopNamingContext;
import kxd.remote.scs.transaction.Request;
import kxd.remote.scs.transaction.Response;
import kxd.remote.scs.transaction.Result;
import kxd.remote.scs.transaction.TradeDriver;
import kxd.remote.scs.transaction.TradeError;
import kxd.remote.scs.transaction.TransactionBeanRemote;
import kxd.remote.scs.transaction.TransactionResponse;
import kxd.util.ArrayUtil;

import org.w3c.dom.Document;

public class EjbDriver extends TradeDriver {
	private Result result = null;
	private final static Logger logger = Logger.getLogger(EjbDriver.class);

	protected void checkBusinessOpenClose(Request req) throws NamingException {
		if (req.getTradeCode().getTradeCodeId() == null) {
			logger.warn("[" + req.getTradeCode().getService() + ","
					+ req.getTradeCode().getTradeCode()
					+ "] not configed, not check.");
			return;
		}
		CachedTermConfig term = (CachedTermConfig) getTermConfig();
		TermHelper.checkBusinessOpenClose(term, req.getTradeCode()
				.getTradeCodeId());
	}

	protected ServiceBean getBean(Request req) throws NamingException {
		ServiceConfig config = (ServiceConfig) getConfig();
		if (config.beanList == null || config.beanList.isEmpty())
			return config.defaultBean;
		CachedTerm term = (CachedTerm) req.getTerm();
		List<Integer> ls = term.getParentOrgIds();
		for (ServiceBean b : config.beanList) {
			boolean finded = true;
			if (b.getOrgIds() != null && b.getOrgIds().length > 0) {
				for (int id : ls) {
					finded = ArrayUtil.contains(b.getOrgIds(), id);
					if (finded)
						break;
				}
			}
			if (finded && b.getExcludeOrgIds() != null
					&& b.getExcludeOrgIds().length > 0) {
				for (int id : ls) {
					finded = !ArrayUtil.contains(b.getExcludeOrgIds(), id);
					if (!finded)
						break;
				}
			}
			if (finded && b.getAppIds() != null && b.getAppIds().length > 0) {
				finded = ArrayUtil.contains(b.getAppIds(), term.getAppId());
			}
			if (finded && b.getExcludeAppIds() != null
					&& b.getExcludeAppIds().length > 0) {
				finded = !ArrayUtil.contains(b.getExcludeAppIds(),
						term.getAppId());
			}
			if (finded)
				return b;
		}
		return config.defaultBean;
	}

	@Override
	public Result trade(Request req, Response resp) throws TradeError {
		ConcurrentHashMap<String, String> map = getConfigMap();
		try {
			checkBusinessOpenClose(req);
			ServiceBean sbean = getBean(req);
			req.getParams().putAll(map);
			LoopNamingContext context = new LoopNamingContext(
					sbean.getJndiName());
			try {
				TransactionBeanRemote bean = context.lookup(
						Lookuper.JNDI_TYPE_EJB, sbean.getName(),
						TransactionBeanRemote.class);
				TransactionResponse r = bean.trade(req);
				result = r.result;
				resp.setDocument(bytesToXml(r.data, false));
				return result.copy();
			} finally {
				context.close();
			}
		} catch (TradeError e) {
			result = e.getResult();
			if (e.getData() != null) {
				try {
					e.setData(bytesToXml((byte[]) e.getData(), true));
				} catch (Throwable ex) {
				}
			}
			throw e;
		} catch (Throwable e) {
			throw new TradeError(null, e);
		}
	}

	private Document bytesToXml(byte[] b, boolean ingoreException)
			throws Throwable {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			return builder.parse(new ByteArrayInputStream(b));
		} catch (Throwable e) {
			if (!ingoreException)
				throw e;
			else
				return null;
		}
	}
}
