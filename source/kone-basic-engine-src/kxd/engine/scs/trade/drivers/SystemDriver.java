package kxd.engine.scs.trade.drivers;

import static kxd.util.StringUnit.getExceptionMessage;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.naming.NamingException;

import kxd.engine.cache.beans.sts.CachedBankTerm;
import kxd.engine.cache.beans.sts.CachedDevice;
import kxd.engine.cache.beans.sts.CachedDeviceDriver;
import kxd.engine.cache.beans.sts.CachedDeviceType;
import kxd.engine.cache.beans.sts.CachedDeviceTypeDriver;
import kxd.engine.cache.beans.sts.CachedOrg;
import kxd.engine.cache.beans.sts.CachedTerm;
import kxd.engine.cache.beans.sts.CachedTermCommand;
import kxd.engine.cache.beans.sts.CachedTermConfig;
import kxd.engine.cache.beans.sts.CachedTermType;
import kxd.engine.cache.beans.sts.CachedTermTypeDevice;
import kxd.engine.cache.beans.sts.CachedTradeCode;
import kxd.engine.helper.CacheHelper;
import kxd.engine.helper.DaoHelper;
import kxd.engine.helper.TermHelper;
import kxd.engine.scs.invoice.InvoiceConfig;
import kxd.engine.scs.invoice.InvoiceHelper;
import kxd.json.JSONObject;
import kxd.net.connection.jndi.Lookuper;
import kxd.net.naming.LoopNamingContext;
import kxd.remote.scs.beans.BaseAd;
import kxd.remote.scs.beans.BaseDeviceDriverFile;
import kxd.remote.scs.beans.BaseDeviceStatus;
import kxd.remote.scs.beans.BaseOrgAd;
import kxd.remote.scs.beans.BaseTermAd;
import kxd.remote.scs.beans.adinfo.EditedInfo;
import kxd.remote.scs.beans.adinfo.EditedPrintAd;
import kxd.remote.scs.interfaces.service.TermServiceBeanRemote;
import kxd.remote.scs.interfaces.service.TradeServiceBeanRemote;
import kxd.remote.scs.transaction.Request;
import kxd.remote.scs.transaction.Response;
import kxd.remote.scs.transaction.Result;
import kxd.remote.scs.transaction.TradeCode;
import kxd.remote.scs.transaction.TradeDriver;
import kxd.remote.scs.transaction.TradeError;
import kxd.remote.scs.util.AppException;
import kxd.remote.scs.util.emun.CounterOption;
import kxd.remote.scs.util.emun.PayStatus;
import kxd.remote.scs.util.emun.TradePhase;
import kxd.remote.scs.util.emun.TradeResult;
import kxd.remote.scs.util.emun.TradeStatus;
import kxd.util.DataSecurity;
import kxd.util.DataUnit;
import kxd.util.DateTime;
import kxd.util.StringUnit;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

public class SystemDriver extends TradeDriver {
	static Logger logger = Logger.getLogger(SystemDriver.class);
	static public String silverlightVersion = null;
	static public List<Integer> silverlightEnabledApps = new CopyOnWriteArrayList<Integer>();
	static public String fontsConfig = null;

	@Override
	public Result trade(Request req, Response resp) throws TradeError {
		String tradeCode = req.getTradeCode().getTradeCode().toLowerCase();
		try {
			if (tradeCode.equals("termactive")) {
				termActive(req, resp);
			} else {
				CachedTerm term = (CachedTerm) req.getTerm();
				if (term == null)
					throw new TradeError("ER", "非法终端");
				if (tradeCode.equals("termlogin")) {
					termLogin(term, req, resp);
				} else if (tradeCode.equals("gettradeglide")) {
					getTradeGlide(term, req, resp);
				} else if (tradeCode.equals("updatestatus")) {
					updateStatus((CachedTermConfig) getTermConfig(), req, resp);
				} else if (tradeCode.equals("updatetradestatus")) {
					updateTradeStatus(term, req, resp);
				} else if (tradeCode.equals("getuserprintstatus")) {
					getUserPrintStatus(term, req, resp);
				} else if (tradeCode.equals("updateprinttimes")) {
					updatePrintTimes(term, req, resp);
				} else if (tradeCode.equals("updatevisitusercount")) {
					updateVisitUserCount(term, req, resp);
				} else if (tradeCode.equals("updateloginusercount")) {
					updateLoginUserCount(term, req, resp);
				} else if (tradeCode.equals("updatebusinessvisitcount")) {
					updateBusinessVisitCount(term, req, resp);
				} else if (tradeCode.equals("submittradedetaillist")) {
					submitTradeDetails(term, req, resp);
				} else if (tradeCode.equals("submitbvmodify")) {
					submitBvModify(term, req, resp);
				} else if (tradeCode.equals("updatetermfilesattr")) {
					updateTermFilesAttr(term, req, resp);
				} else if (tradeCode.equals("getprintad")) {
					getPrintAd(term, req, resp);
				} else if (tradeCode.equals("getadlist")) {
					getAdList(term, req, resp);
				} else if (tradeCode.equals("getinfolist")) {
					getInfoList(term, req, resp);
				} else if (tradeCode.equals("getinvoiceconfig")) {
					InvoiceConfig config = InvoiceHelper.getInvoiceConfig(-1,
							term, 1);
					HashMap<String, String> params = new HashMap<String, String>();
					params.put("taxno", "as;dfljsakfjsalkfd");
					params.put("userno", "13509626798");
					params.put("smallmoney", "100");
					params.put("bigmoney", "壹佰元整");
					List<String[]> details = new ArrayList<String[]>();
					details.add(new String[] { "月租费", "30元" });
					details.add(new String[] { "通讯费", "30元" });
					details.add(new String[] { "短信费", "30元" });
					details.add(new String[] { "其他其他其他其他其他费", "30元" });
					details.add(new String[] { "月租费", "30元" });
					details.add(new String[] { "通讯费", "30元" });
					details.add(new String[] { "短信费", "30元" });
					details.add(new String[] { "其他其他其他其他其他费", "30元" });
					details.add(new String[] { "月租费", "30元" });
					details.add(new String[] { "通讯费", "30元" });
					details.add(new String[] { "短信费", "30元" });
					details.add(new String[] { "其他其他其他其他其他费", "30元" });
					List<String> ls = new ArrayList<String>();
					config.getTemplate().format(term, params, details, ls);
					for (int i = 0; i < ls.size(); i++)
						System.out.println(i + ". " + ls.get(i));
				} else
					throw new TradeError("NO", "无效的交易代码");
			}
		} catch (NamingException e) {
			throw new TradeError("ER", getExceptionMessage(e), e);
		} catch (NoSuchFieldException e) {
			throw new TradeError("ER:", getExceptionMessage(e), e);
		} catch (Exception e) {
			throw new TradeError("ER", getExceptionMessage(e), e);
		}
		return new Result(TradeResult.SUCCESS);
	}

	private void getPrintAd(CachedTerm term, Request req, Response resp)
			throws TradeError, NamingException, NoSuchFieldException,
			IllegalArgumentException, InterruptedException, IOException {
		EditedPrintAd ad = TermHelper.getPrintAd(term,
				req.getParameterInt("categoryid"));
		Element el = resp.createElement("printad");
		if (ad != null)
			el.setAttribute("content", ad.getContent());
		else
			el.setAttribute("content", "");
		resp.getContentElement().appendChild(el);
	}

	private void getInfoList(CachedTerm term, Request req, Response resp)
			throws TradeError, NamingException, NoSuchFieldException,
			IllegalArgumentException, InterruptedException, IOException {
		int categoryId = req.getParameterInt("categoryid");
		List<EditedInfo> ls = TermHelper.getInfoList(term, categoryId);
		Iterator<EditedInfo> it = ls.iterator();
		while (it.hasNext()) {
			EditedInfo o = it.next();
			Element el = resp.createElement("info");
			el.setAttribute("filecategory", "6");
			el.setAttribute("title", o.getTitle());
			el.setAttribute("infocategoryid", o.getInfoCategory().getIdString());
			el.setAttribute("infocategorydesp", o.getInfoCategory()
					.getInfoCategoryDesp());
			el.setAttribute("filename", o.getFileName());
			el.setAttribute("summary", o.getSummary());
			el.setAttribute("publishtime", DataUnit.formatDateTime(
					o.getPublishTime(), "yyyy-MM-dd HH:mm:ss"));
			resp.getContentElement().appendChild(el);
		}

	}

	private void getAdList(CachedTerm term, Request req, Response resp)
			throws TradeError, NamingException, NoSuchFieldException,
			IllegalArgumentException, InterruptedException, IOException {
		int categoryId[] = StringUnit.splitToInt(
				req.getParameter("categoryid"), ",");
		for (int id : categoryId) {
			List<BaseAd> ls = TermHelper.getAdList(term, id);
			Iterator<BaseAd> it = ls.iterator();
			while (it.hasNext()) {
				BaseAd o = it.next();
				Element el = resp.createElement("ad");
				el.setAttribute("adcategory", Integer.toString(id));
				if (o instanceof BaseTermAd)
					el.setAttribute("filecategory", "5");
				else
					el.setAttribute("filecategory", "4");
				el.setAttribute("desp", o.getAdDesp());
				el.setAttribute("filename", o.getFileName());
				el.setAttribute("duration", Integer.toString(o.getDuration()));
				el.setAttribute("playtimes", Integer.toString(o.getPlayTimes()));
				el.setAttribute("priority",
						Integer.toString(o.getPriority().getValue()));
				el.setAttribute("storetype",
						Integer.toString(o.getStoreType().getValue()));
				el.setAttribute("startdate",
						DataUnit.formatDateTime(o.getStartDate(), "yyyy-MM-dd"));
				el.setAttribute("enddate",
						DataUnit.formatDateTime(o.getEndDate(), "yyyy-MM-dd"));
				el.setAttribute("fileurl", o.getUrl());
				if (o instanceof BaseOrgAd) {
					el.setAttribute("orgid",
							Integer.toString(((BaseOrgAd) o).getOrg().getId()));
				}
				resp.getContentElement().appendChild(el);
			}
		}
	}

	private void updateBusinessVisitCount(CachedTerm term, Request req,
			Response resp) throws TradeError, NamingException,
			NoSuchFieldException {
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			TradeServiceBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-tradeServiceBean",
					TradeServiceBeanRemote.class);
			bean.updateBusinessVisitCount(req.getTradeCode().getTermId(), req
					.getTradeCode().getOrgId(), req
					.getParameterInt("businessid"), req.getParameter("userno"));
		} finally {
			context.close();
		}
	}

	private void updateVisitUserCount(CachedTerm term, Request req,
			Response resp) throws TradeError, NamingException,
			NoSuchFieldException {
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			TradeServiceBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-tradeServiceBean",
					TradeServiceBeanRemote.class);
			bean.updateVisitUserCount(req.getTradeCode().getTermId(), req
					.getTradeCode().getOrgId(), req.getParameter("userno"));
		} finally {
			context.close();
		}
	}

	private void updateLoginUserCount(CachedTerm term, Request req,
			Response resp) throws TradeError, NamingException,
			NoSuchFieldException {
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			TradeServiceBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-tradeServiceBean",
					TradeServiceBeanRemote.class);
			bean.updateLoginUserCount(req.getTradeCode().getTermId(), req
					.getTradeCode().getOrgId(), req.getParameter("userno"));
		} finally {
			context.close();
		}
	}

	public List<CachedTermCommand> updateTermStatus(CachedTerm term,
			boolean pause, int busyTimes, int idleTimes,
			List<BaseDeviceStatus> status) throws NamingException {
		TermHelper.updateTermStatus(term, pause, busyTimes, idleTimes, status);
		List<CachedTermCommand> ls = null;
		try {
			ls = CacheHelper.termMc.getStreamableList(
					CachedTermCommand.KEY_PREFIX + term.getId(),
					CachedTermCommand.class);
			if (ls != null && ls.size() > 0)
				CacheHelper.termMc.delete(CachedTermCommand.KEY_PREFIX
						+ term.getId());
		} catch (Throwable e) {
			logger.error("更新终端状态失败:", e);
		}
		return ls;
	}

	public void updateStatus(CachedTermConfig term, Request req, Response resp)
			throws TradeError, NoSuchFieldException, NamingException {
		String status = req.getParameterDef("status", "").trim();
		boolean pause = req.getParameterDef("pause", "").equals("true");
		int busyTimes = req.getParameterIntDef("busytimes", 0);
		int idleTimes = req.getParameterIntDef("idletimes", 0);
		ArrayList<BaseDeviceStatus> list = new ArrayList<BaseDeviceStatus>();
		if (!status.isEmpty()) {
			// logger.info("update status[status=" + status + "]");
			String str;
			try {
				str = new String(DataUnit.hexToBytes(status), "gb18030");
			} catch (UnsupportedEncodingException e) {
				try {
					str = new String(DataUnit.hexToBytes(status), "gbk");
				} catch (UnsupportedEncodingException e1) {
					try {
						str = new String(DataUnit.hexToBytes(status), "gb2312");
					} catch (UnsupportedEncodingException e2) {
						str = status;
					}
				}
			}
			logger.info("update status[termid=" + term.getIdString()
					+ ";status=" + str + "]");
			String[] ls = StringUnit.split(str, "\r\n");
			for (int i = 0; i < ls.length; i++) {
				String ds[] = StringUnit.split(ls[i], "\t");
				if (ds.length < 3)
					break;
				BaseDeviceStatus st = new BaseDeviceStatus();
				st.setId(Integer.valueOf(ds[0]));
				st.setStatus(Integer.valueOf(ds[1]));
				st.setMessage(ds[2]);
				list.add(st);
			}
		}
		List<CachedTermCommand> ls = updateTermStatus(term.getTerm(), pause,
				busyTimes, idleTimes, list);
		if (ls != null) {
			Iterator<CachedTermCommand> it = ls.iterator();
			while (it.hasNext()) {
				CachedTermCommand cm = it.next();
				Element el = resp.createElement("command");
				el.setAttribute("command", Integer.toString(cm.getCommand()));
				el.setAttribute("param1", cm.getParam());
				el.setAttribute("param2", cm.getParam1());
				resp.getContentElement().appendChild(el);
			}
		}
		term.getTerm().setLastInlineTime(System.currentTimeMillis());
		CacheHelper.termMap.put(term.getId(), term);
	}

	private void updateTradeStatus(CachedTerm term, Request req, Response resp)
			throws TradeError, NamingException, NoSuchFieldException {
		BigDecimal termGlide = new BigDecimal(
				req.getParameter("update_termglide"));
		Result result = new Result(req);
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			TradeServiceBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-tradeServiceBean",
					TradeServiceBeanRemote.class);
			bean.updateTradeDetail(termGlide, term.getOrg().getId(), result);
		} finally {
			context.close();
		}
	}

	private void updatePrintTimes(CachedTerm term, Request req, Response resp)
			throws TradeError, NamingException, NoSuchFieldException {
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			TradeServiceBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-tradeServiceBean",
					TradeServiceBeanRemote.class);
			String userno = req.getParameter("userno");
			int month = req.getParameterInt("month");
			int printType = req.getParameterInt("printtype");
			int pages = req.getParameterInt("times"), lines = req
					.getParameterInt("lines");
			bean.updateUserPrintTimes(req.getTradeCode().getTermId(), req
					.getTradeCode().getOrgId(), userno, month, printType,
					pages, lines);
		} finally {
			context.close();
		}
	}

	private void getUserPrintStatus(CachedTerm term, Request req, Response resp)
			throws TradeError, NamingException, NoSuchFieldException {
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			TradeServiceBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-tradeServiceBean",
					TradeServiceBeanRemote.class);
			String userno = req.getParameter("userno");
			int month = req.getParameterInt("month");
			int printType = req.getParameterInt("printtype");
			int maxPrintTimes = req.getParameterInt("maxprinttimes");
			boolean enabled = bean.getUserNoQueryPrintStatus(userno, month,
					printType, maxPrintTimes);
			Element el = resp.createElement("printstatus");
			el.setAttribute("enabled", enabled ? "true" : "false");
			resp.getContentElement().appendChild(el);
		} finally {
			context.close();
		}
	}

	private void getTradeGlide(CachedTerm term, Request req, Response resp)
			throws TradeError, NamingException, NoSuchFieldException {
		Element el = resp.createElement("glide");
		el.setAttribute("id", TermHelper.getTradeGlide());
		resp.getContentElement().appendChild(el);
	}

	public int termActivate(String manufNo, String guid) throws NamingException {
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			TermServiceBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-termServiceBean", TermServiceBeanRemote.class);
			return bean.termActivate(manufNo, guid);
		} finally {
			context.close();
		}
	}

	private void termActive(Request req, Response resp) throws TradeError,
			NamingException, NoSuchFieldException {
		int termId = termActivate(req.getParameter("manufno"),
				req.getParameter("guid"));
		Element el = resp.createElement("termid");
		el.setTextContent(Integer.toString(termId));
		resp.getContentElement().appendChild(el);
	}

	private void submitBvModify(CachedTerm term, Request req, Response resp)
			throws TradeError, NamingException, NoSuchFieldException {
		int termId = req.getTradeCode().getTermId();
		int day = req.getParameterInt("day");
		boolean detailModified = req.getParameterDef("detailmodified", "false")
				.equals("true");
		boolean traceModified = req.getParameterDef("tracemodified", "false")
				.equals("true");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			TermServiceBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-termServiceBean", TermServiceBeanRemote.class);
			try {
				bean.submitCashModify(termId, day, detailModified,
						traceModified);
			} catch (Throwable e) {
				throw new TradeError("ER", StringUnit.getExceptionMessage(e));
			}
		} finally {
			context.close();
		}
	}

	private void updateTermFilesAttr(CachedTerm term, Request req, Response resp)
			throws TradeError, NamingException, NoSuchFieldException,
			IOException {
		int termId = req.getTradeCode().getTermId();
		byte[] b = DataUnit.hexToBytes(req.getParameter("filesattr"));
		String filesAttr = new String(
				DataSecurity.zipDecompress(b, 0, b.length));
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			TermServiceBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-termServiceBean", TermServiceBeanRemote.class);
			try {
				byte[] bytes = filesAttr.getBytes();
				if (bytes.length > 4000)
					filesAttr = new String(bytes, 0, 4000);
				bean.submitTermFilesAttr(termId, filesAttr);
			} catch (Throwable e) {
				throw new TradeError("ER", StringUnit.getExceptionMessage(e));
			}
		} finally {
			context.close();
		}
	}

	final static int LOGINUPDATE_TIMES = 60000;

	private void termLogin(CachedTerm term, Request req, Response resp)
			throws TradeError, NamingException, NoSuchFieldException {
		int termId = req.getTradeCode().getTermId();
		String guid = req.getParameter("guid");
		if (!guid.equals(term.getGuid()))
			throw new AppException("非法终端");
		switch (term.getStatus()) {
		case DISUSE:
			throw new AppException("终端被禁止使用");
		}
		CachedOrg org = term.getOrg();
		CachedTermType termType = term.getTermType();
		Hashtable<String, Object> ret;
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			TermServiceBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-termServiceBean", TermServiceBeanRemote.class);
			ret = bean
					.termLogin(term, req.getRemoteAddr(),
							req.getParameter("${requesturi}"),
							req.getParameter("guid"));
		} finally {
			context.close();
		}

		JSONObject json = new JSONObject();
		try {
			json.put("termid", termId);
			json.put("termcode", term.getTermCode());
			JSONObject tmpjson = new JSONObject();
			tmpjson.put("id", org.getId().toString());
			tmpjson.put("code", org.getOrgCode());
			tmpjson.put("name", org.getOrgName());
			tmpjson.put("fullname", org.getOrgFullName());
			json.put("org", tmpjson);
			json.put("address",
					org.getAddress() == null ? "" : org.getAddress());
			json.put("appid", Integer.toString(term.getAppId()));
			json.put("areacode",
					term.getAreaCode() == null ? "" : term.getAreaCode());
			CachedBankTerm bankTerm = term.getBankTerm();
			if (bankTerm != null) {
				tmpjson = new JSONObject();
				tmpjson.put("id", bankTerm.getId());
				tmpjson.put("code", bankTerm.getBankTermCode());
				tmpjson.put("desp", bankTerm.getBankTermDesp());
				tmpjson.put("batch", bankTerm.getBatch() == null ? ""
						: bankTerm.getBatch());
				tmpjson.put("extfield", bankTerm.getExtField() == null ? ""
						: bankTerm.getExtField());
				tmpjson.put("mackey", bankTerm.getMacKey() == null ? ""
						: bankTerm.getMacKey());
				tmpjson.put(
						"merchant",
						bankTerm.getMerchantAccount() == null ? "" : bankTerm
								.getMerchantAccount());
				tmpjson.put("signintime", bankTerm.getSigninTime() == null ? ""
						: bankTerm.getSigninTime().toString());
				tmpjson.put("pinkey", bankTerm.getWorkKey() == null ? ""
						: bankTerm.getWorkKey());
				json.put("bankterm", tmpjson);
			}
			Element el = resp.createElement("terminfo");
			String appid = Integer.toString(term.getAppId());
			if (silverlightEnabledApps.contains(term.getAppId())
					&& silverlightVersion != null) {
				el.setAttribute("silverlight_version", silverlightVersion);
				el.setAttribute("enable_silverlight", "true");
			}
			if (fontsConfig != null && !fontsConfig.isEmpty())
				el.setAttribute("fonts", fontsConfig);
			el.setAttribute("appid", appid);
			el.setAttribute("starturl", "/" + appid + "/start.jsp");
			el.setAttribute("homeurl", "/" + appid + "/index.jsp");
			el.setAttribute("adurl", "/" + appid + "/ad.jsp");
			el.setAttribute("reconciletime", "0000");
			el.setAttribute("workkey", (String) ret.get("workkey"));
			el.setAttribute("loginsessionid",
					(String) ret.get("loginsessionid"));

			json.put("starturl", "/" + appid + "/start.jsp");
			json.put("homeurl", "/" + appid + "/index.jsp");
			json.put("adurl", "/" + appid + "/ad.jsp");
			json.put("extfield0",
					term.getExtField0() == null ? "" : term.getExtField0());
			json.put("extfield1",
					term.getExtField1() == null ? "" : term.getExtField1());
			json.put("extfield2",
					term.getExtField2() == null ? "" : term.getExtField2());
			json.put("extfield3",
					term.getExtField3() == null ? "" : term.getExtField3());
			json.put("extfield4",
					term.getExtField4() == null ? "" : term.getExtField4());
			el.setAttribute("data", json.toString());
			el.setAttribute("opentime",
					term.getOpenTime() == null ? "" : term.getOpenTime());
			el.setAttribute("closetime", term.getCloseTime() == null ? ""
					: term.getCloseTime());
			el.setAttribute("systime", new DateTime().format("yyyyMMddHHmmss"));
			resp.getContentElement().appendChild(el);
			el = resp.createElement("devices");
			resp.getContentElement().appendChild(el);
			Enumeration<CachedTermTypeDevice> en = termType.getDeviceMap()
					.elements();
			while (en.hasMoreElements()) {
				CachedTermTypeDevice d = en.nextElement();
				CachedDevice device = d.getDevice();
				CachedDeviceType deviceType = device.getDeviceType();
				CachedDeviceDriver deviceDriver = device.getDeviceDriver();
				CachedDeviceTypeDriver deviceTypeDriver = deviceType
						.getDeviceTypeDriver();
				Element e = resp.createElement("device");
				el.appendChild(e);
				e.setAttribute("deviceid", Integer.toString(d.getId()));
				e.setAttribute("devicetype", deviceType.getDeviceTypeCode());
				e.setAttribute("devicename", device.getDeviceName());
				e.setAttribute("devicedriver", deviceDriver.getDriverFile());
				e.setAttribute("typedriver", deviceTypeDriver.getDriverFile());
				e.appendChild(resp.getDocument().createCDATASection(
						d.getExtConfig() == null ? "" : d.getExtConfig()));
				e.setAttribute("port", Integer.toString(d.getPort()));
				for (BaseDeviceDriverFile f : deviceDriver.getFiles()) {
					Element ef = resp.createElement("file");
					e.appendChild(ef);
					ef.setAttribute("filename", f.getFileName());
				}
			}
		} catch (Throwable e) {
			throw new TradeError("ER", StringUnit.getExceptionMessage(e));
		}
	}

	public void submitTradeDetails(CachedTerm term, Request req, Response resp)
			throws Exception {
		String data = new String(DataSecurity.zipDecompress(
				DataUnit.hexToBytes(req.getParameter("data")), 0, 0));
		StringTokenizer token = new StringTokenizer(data, "\r\n");
		List<Object> ls = new ArrayList<Object>();
		Integer orgId = -1;
		int oldTermId = -1;
		while (token.hasMoreTokens()) {
			String[] ds = StringUnit.split(token.nextToken(), "\t");
			int termid = Integer.valueOf(ds[0]);

			if (termid != oldTermId || orgId == null) {
				orgId = CacheHelper.termMap.getTerm(termid).getOrgId();
			}
			oldTermId = termid;
			String userNo = ds[1];
			// Date tradeTime = DataUnit.parseDateTime(ds[2], "yyyyMMddhhmmss");
			// 缴费日志中交易时间以24小时制，否则12点会记录成0点
			// modified by zhangjb 2009-11-09
			Date tradeTime = DataUnit.parseDateTime(ds[2], "yyyyMMddHHmmss");
			// 缴费日志以元为单位，数据库以分为单位，将元转换为分
			// 如果上传金额为0则不记录到数据库
			// modified by zhangjb 2009-11-02
			int amount = Integer.parseInt(ds[3]) * 100;
			logger.info("termid:" + termid + "||userNo:" + userNo
					+ "||tradeTime:" + ds[2] + "||amount:" + amount
					+ "||ds[3]:" + ds[3]);
			if (amount <= 0)
				continue;
			int needAmount = Integer.parseInt(ds[4]) * 100;
			int tradeStatus = Integer.valueOf(ds[5]);
			String service = ds[6];
			String tradeCode = ds[7];
			String termGlide = ds[8];
			String tradeGlide = ds[9];
			String bankCardNo = ds[10];
			String bankGlide = ds[11];
			String[] attachData = StringUnit.split(ds[12], "&&");
			CachedTradeCode cachedTradeCode = CacheHelper.tradeCodeMap
					.getByCodeServ(tradeCode, service);
			if (cachedTradeCode != null) {
				Object[] o = new Object[15];
				o[0] = termid;
				o[1] = termGlide;
				o[2] = userNo;
				o[3] = tradeTime;
				o[4] = amount;
				o[5] = needAmount;
				o[6] = TradeResult.valueOf(tradeStatus);
				o[7] = cachedTradeCode.getId();
				o[8] = bankCardNo;
				o[9] = tradeGlide;
				o[10] = bankGlide;
				o[11] = attachData;
				o[12] = service;
				o[13] = tradeCode;
				o[14] = orgId;
				ls.add(o);
			}
		}
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			TradeServiceBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-tradeServiceBean",
					TradeServiceBeanRemote.class);
			bean.submitTradeDetails(ls, req.getRemoteAddr(),
					req.getServerAddr());
		} catch (Exception e) {
			Iterator<?> it = ls.iterator();
			TradeCode tc = new TradeCode();
			Result result = new Result(TradeResult.TIMEOUT);
			while (it.hasNext()) {
				Object[] o = (Object[]) it.next();
				tc.setTermId((Integer) o[0]);
				tc.setTermGlide(BigDecimal.valueOf(Long.valueOf((String) o[1])));
				tc.setUserno((String) o[2]);
				tc.setTradeTime((Date) o[3]);
				tc.setAmount((Integer) o[4]);
				tc.setTradeAmount((Integer) o[5]);
				result.setResult((TradeResult) o[6]);
				tc.setTradeCodeId((Integer) o[7]);
				tc.setBankCardNo((String) o[8]);
				result.setTradeGlide((String) o[9]);
				result.setBankGlide((String) o[10]);
				CachedTradeCode tradeCode = CacheHelper.tradeCodeMap.get(tc
						.getTradeCodeId());
				result.setTradeStatus(TradeStatus.NOT_TRADE);
				result.setPayStatus(PayStatus.NOT_PAY);
				if (tradeCode != null) {
					if (tradeCode.getPayWay() != null
							&& tradeCode.getPayWay().isNeedTrade()) {
						if (result.getBankGlide() != null
								&& !result.getBankGlide().trim().isEmpty()) {
							result.setPhase(TradePhase.BEFORE_TRADE);
							result.setPayStatus(PayStatus.PAY_SUCCESS);
						} else
							result.setPhase(TradePhase.BEFORE_PAY);
					} else if (tradeCode.getPayItem() != null
							&& !tradeCode.getPayItem().isNeedTrade()) {
						result.setPayStatus(PayStatus.PAY_SUCCESS);
						result.setPhase(TradePhase.TRADE);
						result.setTradeStatus(TradeStatus.TRADE_SUCCESS);
					} else {
						result.setPhase(TradePhase.BEFORE_TRADE);
						result.setPayStatus(PayStatus.PAY_SUCCESS);
					}
				} else {
					result.setPayStatus(PayStatus.PAY_SUCCESS);
					result.setPhase(TradePhase.BEFORE_TRADE);
				}
				String[] attachData = (String[]) o[11];
				if (attachData.length > 1)
					result.setExtData0(attachData[1]);
				else
					result.setExtData0("");
				if (attachData.length > 2)
					result.setExtData1(attachData[2]);
				else
					result.setExtData1("");
				if (attachData.length > 3)
					result.setExtData2(attachData[3]);
				else
					result.setExtData2("");
				if (attachData.length > 4)
					result.setExtData3(attachData[4]);
				else
					result.setExtData3("");
				if (attachData.length > 5)
					result.setExtData4(attachData[5]);
				else
					result.setExtData4("");
				result.setResultInfo("终端交易网络故障后自动提交");
				tc.setService((String) o[12]);
				tc.setTradeCode((String) o[13]);
				tc.setOrgId((Integer) o[14]);
				String redoParams = "";
				if (attachData.length > 0)
					redoParams = attachData[0];
				Integer userOrgId = null;
				String[] ps = StringUnit.split(redoParams, "&");
				for (String param : ps) {
					String ps1[] = StringUnit.split(param, "=");
					if (ps1.length > 1) {
						if (ps1[0].trim().equalsIgnoreCase("citycode")
								&& !ps1[1].trim().isEmpty()) {
							userOrgId = TermHelper
									.getUserNoOrgId(ps1[1].trim());
						}
					}
				}
				DaoHelper.getLogThread().addLog(
						DaoHelper.getInsertTradeDetailSql(true, tc, 0,
								CounterOption.NONE, result, new Date(),
								userOrgId, redoParams, req.getRemoteAddr(),
								req.getServerAddr()));
			}
			throw e;
		} finally {
			context.close();
		}
	}
}
