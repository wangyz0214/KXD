package kxd.engine.scs.admin.drivers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import kxd.engine.cache.beans.sts.CachedBusiness;
import kxd.engine.cache.beans.sts.CachedPrintType;
import kxd.engine.cache.beans.sts.CachedTradeCode;
import kxd.engine.helper.CacheHelper;
import kxd.engine.helper.beans.IdentOrg;
import kxd.engine.scs.admin.AdminSessionObject;
import kxd.engine.scs.admin.AdminTradeDriver;
import kxd.engine.scs.admin.util.UserRight;
import kxd.engine.scs.report.TermNameItem;
import kxd.engine.scs.report.beans.BusinessStatField;
import kxd.engine.scs.report.beans.BusinessStatItem;
import kxd.engine.scs.report.beans.FaultStatItem;
import kxd.engine.scs.report.beans.OpenRateStatItem;
import kxd.engine.scs.report.beans.PageClickStatField;
import kxd.engine.scs.report.beans.PageClickStatItem;
import kxd.engine.scs.report.beans.ReceivableStatItem;
import kxd.engine.scs.report.beans.RefundDetailItem;
import kxd.engine.scs.report.beans.TermPrintStatField;
import kxd.engine.scs.report.beans.TermPrintStatItem;
import kxd.engine.scs.report.beans.TradeDetailItem;
import kxd.engine.scs.report.beans.UserCountStatItem;
import kxd.net.HttpRequest;
import kxd.net.connection.jndi.Lookuper;
import kxd.net.naming.LoopNamingContext;
import kxd.remote.scs.interfaces.ReportBeanRemote;
import kxd.remote.scs.interfaces.SettlementBeanRemote;
import kxd.remote.scs.util.QueryResult;
import kxd.remote.scs.util.emun.PayWayType;
import kxd.remote.scs.util.emun.RefundStatus;
import kxd.remote.scs.util.emun.TradeStatus;
import kxd.util.DataUnit;
import kxd.util.DateTime;
import kxd.util.KoneException;
import kxd.util.StringUnit;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ReportTradeDriver implements AdminTradeDriver {

	@Override
	public void execute(AdminSessionObject session, HttpRequest request,
			HttpServletResponse response, Document xmlDoc, Element content,
			Element result) throws Throwable {
		String cmd = request.getParameter("cmd");
		boolean firstQuery = request.getParameterBooleanDef("firstquery", true);
		int maxresults = request.getParameterIntDef("maxresults", 12);
		int firstresult = request.getParameterIntDef("page", 0) * maxresults;
		int orgId = request.getParameterInt("orgid");
		if (cmd.equals("querytermtransactionlist")) {
			queryTermTransactionsReport(session, orgId, firstQuery,
					firstresult, maxresults, request, xmlDoc, content);
		} else if (cmd.equals("querytermreceivablelist")) {
			queryTermReceivalbeReport(session, orgId, firstQuery, firstresult,
					maxresults, request, xmlDoc, content);
		} else if (cmd.equals("querytermpageclicklist")) {
			queryTermPageClickReport(session, orgId, firstQuery, firstresult,
					maxresults, request, xmlDoc, content);
		} else if (cmd.equals("querytermopenratelist")) {
			queryTermOpenRateReport(session, orgId, firstQuery, firstresult,
					maxresults, request, xmlDoc, content);
		} else if (cmd.equals("queryfaultlist")) {
			queryFaultReport(session, orgId, firstQuery, firstresult,
					maxresults, request, xmlDoc, content);
		} else if (cmd.equals("queryprintlist")) {
			queryPrintReport(session, orgId, firstQuery, firstresult,
					maxresults, request, xmlDoc, content);
		} else if (cmd.equals("querytradedetaillist")) {
			queryTradeDetail(session, orgId, firstQuery, firstresult,
					maxresults, request, xmlDoc, content);
		} else if (cmd.equals("queryrefundloglist")) {
			queryRefundLog(session, orgId, firstQuery, firstresult, maxresults,
					request, xmlDoc, content);
		} else if (cmd.equals("queryloginuserslist")) {
			queryLoginUsers(session, orgId, firstQuery, firstresult,
					maxresults, request, xmlDoc, content);
		} else if (cmd.equals("queryvisituserslist")) {
			queryVisitUsers(session, orgId, firstQuery, firstresult,
					maxresults, request, xmlDoc, content);
		} else if (cmd.equals("querytransactionslist")) {
			queryTransactions(session, orgId, firstQuery, firstresult,
					maxresults, request, xmlDoc, content);
		} else if (cmd.equals("queryreceivablelist")) {
			queryReceivable(session, orgId, firstQuery, firstresult,
					maxresults, request, xmlDoc, content);
		} else if (cmd.equals("querybusinessvisitlist")) {
			queryBusinessVisit(session, orgId, firstQuery, firstresult,
					maxresults, request, xmlDoc, content);
		} else if (cmd.equals("queryprinttotallist")) {
			queryPrint(session, orgId, firstQuery, firstresult, maxresults,
					request, xmlDoc, content);
		}

		else
			throw new KoneException("无效的交易代码");
	}

	protected void queryRefundLog(AdminSessionObject session, int orgId,
			boolean firstQuery, int firstResult, int maxResults,
			HttpRequest request, Document xmlDoc, Element content)
			throws Throwable {
		if (!((AdminSessionObject) session).hasRight(UserRight.REFUND_LOG))
			throw new KoneException("您没有查询退款明细的权限.");
		String termCode = request.getParameter("termcode");
		Date startTime = new DateTime(request.getParameter("starttime"),
				"yyyy-MM-dd").getTime();
		Date endTime = new DateTime(request.getParameter("endtime")
				+ " 23:59:59", "yyyy-MM-dd HH:mm:ss").getTime();
		String userNo = request.getParameterDef("userno", null);
		String termGlide = request.getParameterDef("termglide", null);
		String tradeglide = request.getParameterDef("tradeglide", null);
		Short type = request.getParameterShortDef("payType", null);
		PayWayType payType = type == null ? null : PayWayType.valueOf(type);
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			SettlementBeanRemote bean = context
					.lookup(Lookuper.JNDI_TYPE_EJB,
							"kxd-ejb-settlementServiceBean",
							SettlementBeanRemote.class);
			QueryResult<?> r = bean.queryRefundDetail(
					((AdminSessionObject) session).getLoginUser().getId(),
					firstQuery, firstResult, maxResults, orgId, termCode,
					startTime, endTime, userNo, termGlide, tradeglide, payType);
			appendCountElement(firstQuery, content, xmlDoc, r.getCount());
			Iterator<?> it = r.getResultList().iterator();
			while (it.hasNext()) {
				Element el = xmlDoc.createElement("record");
				RefundDetailItem<?> o = (RefundDetailItem<?>) it.next();
				TermNameItem nameItem = (TermNameItem) o.getNameItem();
				DateTime time = new DateTime(o.getRefundTime());
				el.setAttribute("province", nameItem.getProvinceName());
				el.setAttribute("city", nameItem.getCityName());
				el.setAttribute("hall", nameItem.getHallName());
				el.setAttribute("termcode", nameItem.getTermCode());
				el.setAttribute("id", o.getTermGlide());
				el.setAttribute("tradeglide", o.getTradeGlide());
				el.setAttribute("userno", o.getUserNo());
				el.setAttribute("amount", DataUnit.fenToYuan(o.getAmount()));
				el.setAttribute("refundtime", time.format("yyyy-MM-dd"));
				el.setAttribute("result", o.isCanceled() ? "退款撤消" : o
						.getRefundResult().toString());
				content.appendChild(el);
			}
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void appendCountElement(boolean firstQuery, Element content,
			Document document, int count) {
		if (firstQuery) {
			Element el = document.createElement("count");
			el.setAttribute("value", Integer.toString(count));
			content.appendChild(el);
		}
	}

	/**
	 * 查询交易明细
	 * 
	 * @param session
	 * @param orgId
	 * @param firstQuery
	 * @param firstResult
	 * @param maxResults
	 * @param request
	 * @param xmlDoc
	 * @param content
	 */
	protected void queryTradeDetail(AdminSessionObject session, int orgId,
			boolean firstQuery, int firstResult, int maxResults,
			HttpRequest request, Document xmlDoc, Element content)
			throws Throwable {
		if (!((AdminSessionObject) session).hasRight(UserRight.TRADEPROC))
			throw new KoneException("您没有查询交易明细的权限.");
		int orgid = request.getParameterInt("orgid");
		String termCode = request.getParameterDef("termcode", null);
		String startTime = request.getParameter("starttime");
		String endTime = request.getParameter("endtime");
		if (startTime.endsWith("00"))
			startTime += ":00";
		else
			startTime += ":59";
		if (endTime.endsWith("00"))
			endTime += ":00";
		else
			endTime += ":59";
		String userNo = request.getParameterDef("userno", null);
		String termGlide = request.getParameterDef("termglide", null);
		Integer payStatus = request.getParameterIntDef("payStatus", null);
		Integer tradeStatus = request.getParameterIntDef("tradeStatus", null);
		Integer refundStatus = request.getParameterIntDef("refundStatus", null);
		String tradeglide = request.getParameterDef("tradeglide", null);
		Integer recStatus = request.getParameterIntDef("recStatus", null);
		String businessid = request.getParameter("businessid");
		List<Integer> businessIdList = StringUnit.splitToInt1(businessid, ",");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			SettlementBeanRemote bean = context
					.lookup(Lookuper.JNDI_TYPE_EJB,
							"kxd-ejb-settlementServiceBean",
							SettlementBeanRemote.class);
			QueryResult<?> r = bean.queryTradeDetail(
					((AdminSessionObject) session).getLoginUser().getId(),
					firstQuery, firstResult, maxResults, orgid, termCode,
					DataUnit.parseDateTime(startTime, "yyyy-MM-dd HH:mm:ss"),
					DataUnit.parseDateTime(endTime, "yyyy-MM-dd HH:mm:ss"),
					userNo, termGlide, tradeglide, payStatus, refundStatus,
					tradeStatus, recStatus, businessIdList);
			appendCountElement(firstQuery, content, xmlDoc, r.getCount());
			Iterator<?> it = r.getResultList().iterator();
			boolean myHasReturnToDay = ((AdminSessionObject) session)
					.hasRight(UserRight.RETURN_MONEY_TODAY);
			boolean myHasReturnNextDay = ((AdminSessionObject) session)
					.hasRight(UserRight.RETURN_MONEY_NEXTDAY);
			boolean myHasReturnNextMonth = ((AdminSessionObject) session)
					.hasRight(UserRight.RETURN_MONEY_NEXTMONTH);
			boolean myHasCancelReturnToDay = ((AdminSessionObject) session)
					.hasRight(UserRight.UNDO_RETURN_MONEY_TODAY);
			boolean myHasCancelReturnNextDay = ((AdminSessionObject) session)
					.hasRight(UserRight.UNDO_RETURN_MONEY_NEXTDAY);
			boolean myHasCancelReturnNextMonth = ((AdminSessionObject) session)
					.hasRight(UserRight.UNDO_RETURN_MONEY_NEXTMONTH);
			boolean hasSuccessReturn = ((AdminSessionObject) session)
					.hasRight(UserRight.SUCCESS_TRADE_RETURN);
			boolean hasUnionPayReturn = ((AdminSessionObject) session)
					.hasRight(UserRight.UNIONPAY_TRADE_RETURN);
			DateTime now = new DateTime();
			int toDay = now.getFullDay();
			int month = now.getFullMonth();
			while (it.hasNext()) {
				Element el = xmlDoc.createElement("record");
				TradeDetailItem<?> o = (TradeDetailItem<?>) it.next();
				TermNameItem nameItem = (TermNameItem) o.getNameItem();
				CachedTradeCode tradeCode = CacheHelper.tradeCodeMap.get(o
						.getTradeCodeId());

				DateTime time = new DateTime(o.getTradeTime());
				el.setAttribute("province", nameItem.getProvinceName());
				el.setAttribute("city", nameItem.getCityName());
				el.setAttribute("hall", nameItem.getHallName());
				el.setAttribute("termcode", nameItem.getTermCode());
				el.setAttribute("id", o.getTermGlide());
				el.setAttribute("tradeglide", o.getTradeGlide());
				el.setAttribute("userno", o.getUserNo());
				el.setAttribute("amount", DataUnit.fenToYuan(o.getAmount()));
				el.setAttribute("tradetime", time.format("HH:mm:ss"));
				el.setAttribute("tradedesp", tradeCode.getTradeDesp());
				el.setAttribute("paystatus", o.getPayStatus().toString());
				el.setAttribute("tradestatus", o.getTradeStatus().toString());
				el.setAttribute("refundstatus", o.getRefundStatus().toString());
				el.setAttribute("recstatus", o.getRecStatus().toString());
				el.setAttribute("traderesult", o.getTradeResult());
				String option = "";
				boolean success = (o.getTradeStatus().getValue() >= TradeStatus.TRADE_SUCCESS
						.getValue() && o.getTradeStatus().getValue() < TradeStatus.TRADE_TIMEOUT
						.getValue());

				if (o.getRefundStatus().equals(RefundStatus.NOT_REFUND)) {
					boolean nextDay = time.getFullDay() != toDay;
					boolean nextMonth = time.getFullMonth() != month;
					boolean refundEnabled = true;
					if (success && !hasSuccessReturn) // 检查成功交易退款权限
						refundEnabled = false;
					if (refundEnabled
							&& tradeCode.getPayWay().getType()
									.equals(PayWayType.UNIONPAY) // 检查银联交易退款权限
							&& !hasUnionPayReturn)
						refundEnabled = false;
					if (refundEnabled) {
						if (nextMonth) {
							refundEnabled = myHasReturnNextMonth
									&& (tradeCode.getRefundMode() & 4) == 4;
						} else if (nextDay) {
							refundEnabled = myHasReturnNextDay
									&& (tradeCode.getRefundMode() & 2) == 2;
						} else {
							refundEnabled = myHasReturnToDay
									&& (tradeCode.getRefundMode() & 1) == 1;
						}
					}
					if (refundEnabled)
						option = "<a href=\"javascript:returnMoney('"
								+ o.getTermGlide() + "'," + o.getAmount() + ","
								+ success + ",'" + o.getUserNo() + "','"
								+ time.format("yyyy-MM-dd HH:mm:ss") + "','"
								+ tradeCode.getTradeDesp() + "');\">标记退款</a>";
				} else if (!o.getRefundStatus().equals(RefundStatus.REFUNDING)
						&& o.getOptionCode() == 1
						&& (!o.getRefundStatus().equals(
								RefundStatus.REFUND_SUCCESS) || !tradeCode
								.getPayWay().getType()
								.equals(PayWayType.UNIONPAY))) {
					time = new DateTime(o.getOptionTime() == null ? 0 : o
							.getOptionTime().getTime());
					boolean nextDay = time.getFullDay() != toDay;
					boolean nextMonth = time.getFullMonth() != month;
					boolean cancelRefundEnabled = false;
					if (nextMonth) {
						cancelRefundEnabled = myHasCancelReturnNextMonth
								&& (tradeCode.getCancelRefundMode() & 4) == 4;
					} else if (nextDay) {
						cancelRefundEnabled = myHasCancelReturnNextDay
								&& (tradeCode.getCancelRefundMode() & 2) == 2;
					} else {
						cancelRefundEnabled = myHasCancelReturnToDay
								&& (tradeCode.getCancelRefundMode() & 1) == 1;
					}
					if (cancelRefundEnabled)
						option = "<a href=\"javascript:cancelReturnMoney('"
								+ o.getTermGlide() + "'," + o.getAmount() + ","
								+ success + ",'" + o.getUserNo() + "','"
								+ time.format("yyyy-MM-dd HH:mm:ss") + "','"
								+ tradeCode.getTradeDesp() + "');\">撤消退款</a>";
				}
				el.setAttribute("paystatuscode",
						Integer.toString(o.getPayStatus().getValue() / 10));
				el.setAttribute("tradestatuscode",
						Integer.toString(o.getTradeStatus().getValue() / 10));
				el.setAttribute("refundstatuscode",
						Integer.toString(o.getRefundStatus().getValue()));
				el.setAttribute("option", option);
				el.setAttribute("proclist", "<a href=\"javascript:procList('"
						+ o.getTermGlide() + "');\">处理记录</a>");
				content.appendChild(el);
			}
		} finally {
			if (context != null)
				context.close();
		}

	}

	/**
	 * 查询业务量分析报表
	 * 
	 * @param session
	 * @param orgId
	 * @param firstQuery
	 * @param firstResult
	 * @param maxResults
	 * @param request
	 * @param xmlDoc
	 * @param content
	 */
	protected void queryTermTransactionsReport(AdminSessionObject session,
			int orgId, boolean firstQuery, int firstResult, int maxResults,
			HttpRequest request, Document xmlDoc, Element content)
			throws Throwable {
		if (!((AdminSessionObject) session)
				.hasRight(UserRight.TRANSACTION_REPORT))
			throw new KoneException("您没有查询业务量分析报表的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			String businessValue = request.getParameter("businessid");
			String[] s1 = StringUnit.split(businessValue, ",");
			ArrayList<Integer> ls = new ArrayList<Integer>();
			for (int i = 0; i < s1.length; i++) {
				if (s1[i].trim().length() > 0) {
					ls.add(Integer.valueOf(s1[i]));
				}
			}
			Date day = DateTime.parseDate(request.getParameter("startDate"),
					"yyyy-MM-dd");
			ReportBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-reportBean", ReportBeanRemote.class);
			QueryResult<?> r = bean.queryTermTransactionsReport(firstQuery,
					firstResult, maxResults, orgId, ls, day);
			appendCountElement(firstQuery, content, xmlDoc, r.getCount());
			Iterator<?> it = r.getResultList().iterator();
			while (it.hasNext()) {
				Element el = xmlDoc.createElement("record");
				BusinessStatItem<?> o = (BusinessStatItem<?>) it.next();
				TermNameItem nameItem = (TermNameItem) o.getNameItem();
				el.setAttribute("province", nameItem.getProvinceName());
				el.setAttribute("city", nameItem.getCityName());
				el.setAttribute("hall", nameItem.getHallName());
				el.setAttribute("termcode", nameItem.getTermCode());
				int i = 0;
				for (BusinessStatField field : o.getItems()) {
					el.setAttribute("count" + i,
							Long.toString(field.getCount()));
					el.setAttribute("amount" + i, DataUnit.fenToYuan(Long
							.toString(field.getAmount())));
					i++;
				}
				el.setAttribute("totalcount", Long.toString(o.getCount()));
				el.setAttribute("totalamount",
						DataUnit.fenToYuan(Long.toString(o.getMoney())));
				el.setAttribute("prevmonthcount",
						Long.toString(o.getPrevMonthCount()));
				el.setAttribute("prevmonthamount", DataUnit.fenToYuan(Long
						.toString(o.getPrevMonthMoney())));
				el.setAttribute("countrate", o.getCountRate());
				el.setAttribute("amountrate", o.getMoneyRate());
				content.appendChild(el);
			}
		} finally {
			if (context != null)
				context.close();
		}

	}

	/**
	 * 查询业务量分析报表
	 * 
	 * @param session
	 * @param orgId
	 * @param firstQuery
	 * @param firstResult
	 * @param maxResults
	 * @param request
	 * @param xmlDoc
	 * @param content
	 */
	protected void queryTermReceivalbeReport(AdminSessionObject session,
			int orgId, boolean firstQuery, int firstResult, int maxResults,
			HttpRequest request, Document xmlDoc, Element content)
			throws Throwable {
		if (!((AdminSessionObject) session)
				.hasRight(UserRight.RECEIVABLE_REPORT))
			throw new KoneException("您没有查询应收账报表的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			String businessValue = request.getParameter("paywayid");
			String[] s1 = StringUnit.split(businessValue, ",");
			ArrayList<Integer> ls = new ArrayList<Integer>();
			for (int i = 0; i < s1.length; i++) {
				if (s1[i].trim().length() > 0) {
					ls.add(Integer.valueOf(s1[i]));
				}
			}
			Date day = DateTime.parseDate(request.getParameter("startDate"),
					"yyyy-MM-dd");
			ReportBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-reportBean", ReportBeanRemote.class);
			QueryResult<?> r = bean.queryTermReceivableReport(firstQuery,
					firstResult, maxResults, orgId, ls, day);
			appendCountElement(firstQuery, content, xmlDoc, r.getCount());
			Iterator<?> it = r.getResultList().iterator();
			while (it.hasNext()) {
				Element el = xmlDoc.createElement("record");
				ReceivableStatItem<?> o = (ReceivableStatItem<?>) it.next();
				TermNameItem nameItem = (TermNameItem) o.getNameItem();
				el.setAttribute("province", nameItem.getProvinceName());
				el.setAttribute("city", nameItem.getCityName());
				el.setAttribute("hall", nameItem.getHallName());
				el.setAttribute("termcode", nameItem.getTermCode());
				el.setAttribute("count", Long.toString(o.getCount()));
				el.setAttribute("amount",
						DataUnit.fenToYuan(Long.toString(o.getMoney())));
				el.setAttribute("succount", Long.toString(o.getSuccessCount()));
				el.setAttribute("sucamount",
						DataUnit.fenToYuan(Long.toString(o.getSuccessMoney())));
				el.setAttribute("errcount", Long.toString(o.getErrorCount()));
				el.setAttribute("erramount",
						DataUnit.fenToYuan(Long.toString(o.getErrorMoney())));
				el.setAttribute("faultrate", o.getFaultRate());
				el.setAttribute("refundcount",
						Long.toString(o.getRefundCount()));
				el.setAttribute("refundamount",
						DataUnit.fenToYuan(Long.toString(o.getRefundMoney())));
				el.setAttribute("cancelrefundcount",
						Long.toString(o.getCancelRefundCount()));
				el.setAttribute("cancelrefundamount", DataUnit.fenToYuan(Long
						.toString(o.getCancelRefundMoney())));
				el.setAttribute("totalamount",
						DataUnit.fenToYuan(Long.toString(o.getTotalMoney())));
				content.appendChild(el);
			}
		} finally {
			if (context != null)
				context.close();
		}

	}

	/**
	 * 查询页面点击率分析报表
	 * 
	 * @param session
	 * @param orgId
	 * @param firstQuery
	 * @param firstResult
	 * @param maxResults
	 * @param request
	 * @param xmlDoc
	 * @param content
	 */
	protected void queryTermPageClickReport(AdminSessionObject session,
			int orgId, boolean firstQuery, int firstResult, int maxResults,
			HttpRequest request, Document xmlDoc, Element content)
			throws Throwable {
		if (!((AdminSessionObject) session)
				.hasRight(UserRight.BUSINESSVISIT_REPORT))
			throw new KoneException("您没有查询点击率报表的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			String businessValue = request.getParameter("businessid");
			String[] s1 = StringUnit.split(businessValue, ",");
			ArrayList<Integer> ls = new ArrayList<Integer>();
			for (int i = 0; i < s1.length; i++) {
				if (s1[i].trim().length() > 0) {
					ls.add(Integer.valueOf(s1[i]));
				}
			}
			Date day = DateTime.parseDate(request.getParameter("startDate"),
					"yyyy-MM-dd");
			Date eday = DateTime.parseDate(request.getParameter("endDate"),
					"yyyy-MM-dd");
			ReportBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-reportBean", ReportBeanRemote.class);
			QueryResult<?> r = bean.queryTermBusinessVisitReport(firstQuery,
					firstResult, maxResults, orgId, ls, day, eday);
			appendCountElement(firstQuery, content, xmlDoc, r.getCount());
			Iterator<?> it = r.getResultList().iterator();
			while (it.hasNext()) {
				Element el = xmlDoc.createElement("record");
				PageClickStatItem<?> o = (PageClickStatItem<?>) it.next();
				TermNameItem nameItem = (TermNameItem) o.getNameItem();
				el.setAttribute("province", nameItem.getProvinceName());
				el.setAttribute("city", nameItem.getCityName());
				el.setAttribute("hall", nameItem.getHallName());
				el.setAttribute("termcode", nameItem.getTermCode());
				int i = 0;
				for (PageClickStatField f : o.getItems())
					el.setAttribute("count" + (i++),
							Long.toString(f.getCount()));
				el.setAttribute("totalcount", Long.toString(o.getCount()));
				el.setAttribute("prevmonthcount",
						Long.toString(o.getPrevMonthCount()));
				el.setAttribute("rate", o.getCountRate());
				content.appendChild(el);
			}
		} finally {
			if (context != null)
				context.close();
		}

	}

	/**
	 * 查询终端开机率分析报表
	 * 
	 * @param session
	 * @param orgId
	 * @param firstQuery
	 * @param firstResult
	 * @param maxResults
	 * @param request
	 * @param xmlDoc
	 * @param content
	 */
	protected void queryTermOpenRateReport(AdminSessionObject session,
			int orgId, boolean firstQuery, int firstResult, int maxResults,
			HttpRequest request, Document xmlDoc, Element content)
			throws Throwable {
		if (!((AdminSessionObject) session).hasRight(UserRight.USERATE_REPORT))
			throw new KoneException("您没有查询开机率报表的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			Date startDate = DateTime.parseDate(
					request.getParameter("startDate"), "yyyy-MM-dd");
			Date endDate = DateTime.parseDate(request.getParameter("endDate"),
					"yyyy-MM-dd");
			ReportBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-reportBean", ReportBeanRemote.class);
			QueryResult<?> r = bean.queryTermUseRateReport(firstQuery,
					firstResult, maxResults, orgId,
					request.getParameterIntDef("manufid", null), startDate,
					endDate);
			appendCountElement(firstQuery, content, xmlDoc, r.getCount());
			Iterator<?> it = r.getResultList().iterator();
			while (it.hasNext()) {
				Element el = xmlDoc.createElement("record");
				OpenRateStatItem o = (OpenRateStatItem) it.next();
				TermNameItem nameItem = (TermNameItem) o.getNameItem();
				el.setAttribute("province", nameItem.getProvinceName());
				el.setAttribute("city", nameItem.getCityName());
				el.setAttribute("hall", nameItem.getHallName());
				el.setAttribute("termcode", nameItem.getTermCode());
				el.setAttribute("needopentime",
						DateTime.minuteToDHMS(o.getNeedOpenTime()));
				el.setAttribute("realopentime",
						DateTime.minuteToDHMS(o.getRealOpenTime()));
				el.setAttribute("busytime",
						DateTime.minuteToDHMS(o.getBusyTime()));
				el.setAttribute("idletime",
						DateTime.minuteToDHMS(o.getIdleTime()));
				el.setAttribute("prevmonthopentime",
						DateTime.minuteToDHMS(o.getPrevMonthOpenTime()));
				el.setAttribute("openrate", o.getOpenRate());
				el.setAttribute("cyclerate", o.getCycleRate());
				content.appendChild(el);
			}
		} finally {
			if (context != null)
				context.close();
		}

	}

	/**
	 * 查询故障分析报表
	 * 
	 * @param session
	 * @param orgId
	 * @param firstQuery
	 * @param firstResult
	 * @param maxResults
	 * @param request
	 * @param xmlDoc
	 * @param content
	 */
	protected void queryFaultReport(AdminSessionObject session, int orgId,
			boolean firstQuery, int firstResult, int maxResults,
			HttpRequest request, Document xmlDoc, Element content)
			throws Throwable {
		if (!((AdminSessionObject) session).hasRight(UserRight.FAULT_REPORT))
			throw new KoneException("您没有查询开机率报表的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			String values = request.getParameter("alarmcategoryid");
			String[] s1 = StringUnit.split(values, ",");
			ArrayList<Integer> ls = new ArrayList<Integer>();
			for (int i = 0; i < s1.length; i++) {
				if (s1[i].trim().length() > 0) {
					ls.add(Integer.valueOf(s1[i]));
				}
			}
			Date startDate = DateTime.parseDate(
					request.getParameter("startDate"), "yyyy-MM-dd");
			Date endDate = DateTime.parseDate(request.getParameter("endDate"),
					"yyyy-MM-dd");
			ReportBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-reportBean", ReportBeanRemote.class);
			QueryResult<?> r = bean.queryTermFaultReport(firstQuery,
					firstResult, maxResults, orgId,
					request.getParameterIntDef("manufid", null), ls, startDate,
					endDate);
			appendCountElement(firstQuery, content, xmlDoc, r.getCount());
			Iterator<?> it = r.getResultList().iterator();
			while (it.hasNext()) {
				Element el = xmlDoc.createElement("record");
				FaultStatItem o = (FaultStatItem) it.next();
				TermNameItem nameItem = (TermNameItem) o.getNameItem();
				el.setAttribute("province", nameItem.getProvinceName());
				el.setAttribute("city", nameItem.getCityName());
				el.setAttribute("hall", nameItem.getHallName());
				el.setAttribute("termcode", nameItem.getTermCode());
				for (int i = 0; i < o.getItems().size(); i++)
					el.setAttribute("count" + i,
							Long.toString(o.getItems().get(i).getCount()));
				el.setAttribute("faultcount.cur",
						Long.toString(o.getFaultCount().getCount()));
				el.setAttribute("faultcount.prev",
						Long.toString(o.getFaultCount().getPrevMonthCount()));
				el.setAttribute("faultcount.rate", o.getFaultCount().getRate());
				el.setAttribute("faulttime.cur",
						Long.toString(o.getFaultTime().getCount()));
				el.setAttribute("faulttime.prev",
						Long.toString(o.getFaultTime().getPrevMonthCount()));
				el.setAttribute("faulttime.rate", o.getFaultTime().getRate());
				el.setAttribute("resptime.cur",
						Long.toString(o.getResponseTime().getCount()));
				el.setAttribute("resptime.prev",
						Long.toString(o.getResponseTime().getPrevMonthCount()));
				el.setAttribute("resptime.rate", o.getResponseTime().getRate());
				el.setAttribute("resttime.cur",
						Long.toString(o.getRestoreTime().getCount()));
				el.setAttribute("resttime.prev",
						Long.toString(o.getRestoreTime().getPrevMonthCount()));
				el.setAttribute("resttime.rate", o.getRestoreTime().getRate());
				content.appendChild(el);
			}
		} finally {
			if (context != null)
				context.close();
		}

	}

	/**
	 * 查询打印报表
	 * 
	 * @param session
	 * @param orgId
	 * @param firstQuery
	 * @param firstResult
	 * @param maxResults
	 * @param request
	 * @param xmlDoc
	 * @param content
	 */
	protected void queryPrintReport(AdminSessionObject session, int orgId,
			boolean firstQuery, int firstResult, int maxResults,
			HttpRequest request, Document xmlDoc, Element content)
			throws Throwable {
		if (!((AdminSessionObject) session).hasRight(UserRight.PRINT_REPORT))
			throw new KoneException("您没有查询打印报表的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			List<Short> ls = StringUnit.splitToShort1(
					request.getParameter("printtype"), ",");
			Date startDate = DateTime.parseDate(
					request.getParameter("startDate"), "yyyy-MM-dd");
			Date endDate = DateTime.parseDate(request.getParameter("endDate"),
					"yyyy-MM-dd");
			ReportBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-reportBean", ReportBeanRemote.class);
			QueryResult<?> r = bean.queryTermPrintReport(firstQuery,
					firstResult, maxResults, orgId,
					request.getParameterIntDef("manufid", null), ls, startDate,
					endDate);
			appendCountElement(firstQuery, content, xmlDoc, r.getCount());
			Iterator<?> it = r.getResultList().iterator();
			while (it.hasNext()) {
				Element el = xmlDoc.createElement("record");
				TermPrintStatItem<?> o = (TermPrintStatItem<?>) it.next();
				TermNameItem nameItem = (TermNameItem) o.getNameItem();
				el.setAttribute("province", nameItem.getProvinceName());
				el.setAttribute("city", nameItem.getCityName());
				el.setAttribute("hall", nameItem.getHallName());
				el.setAttribute("termcode", nameItem.getTermCode());
				for (int i = 0; i < o.getItems().size(); i++) {
					TermPrintStatField f = o.getItems().get(i);
					el.setAttribute("pages" + i, Long.toString(f.getCount()));
					el.setAttribute("lines" + i, Long.toString(f.getLines()));
				}
				el.setAttribute("pagestotal", Long.toString(o.getCount()));
				el.setAttribute("linestotal", Long.toString(o.getLines()));
				el.setAttribute("prevpagestotal",
						Long.toString(o.getPrevMonthCount()));
				el.setAttribute("prevlinestotal",
						Long.toString(o.getPrevMonthLines()));
				el.setAttribute("pagesrate", o.getCountRate());
				el.setAttribute("linesrate", o.getLinesRate());
				content.appendChild(el);
			}
		} finally {
			if (context != null)
				context.close();
		}

	}

	/**
	 * 登录用户数日报表
	 * 
	 * @param session
	 * @param orgId
	 * @param firstQuery
	 * @param firstResult
	 * @param maxResults
	 * @param request
	 * @param xmlDoc
	 * @param content
	 */
	protected void queryLoginUsers(AdminSessionObject session, int orgId,
			boolean firstQuery, int firstResult, int maxResults,
			HttpRequest request, Document xmlDoc, Element content)
			throws Throwable {
		if (!((AdminSessionObject) session)
				.hasRight(UserRight.LOGINUSERS_REPORT))
			throw new KoneException("您没有查询登录用户数日报表的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			Integer orgid = request.getParameterIntDef("orgid", null);
			Date startDate = DateTime.parseDate(
					request.getParameter("startDate"), "yyyy-MM-dd");
			ReportBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-reportBean", ReportBeanRemote.class);
			List<?> table = bean.queryLoginUsersDayReport(orgid, startDate);
			appendCountElement(firstQuery, content, xmlDoc, table.size());
			Iterator<?> it = table.iterator();
			while (it.hasNext()) {
				Element el = xmlDoc.createElement("record");
				UserCountStatItem o = (UserCountStatItem) it.next();
				IdentOrg org = (IdentOrg) o.getNameItem();
				el.setAttribute("orgdesp", org.getOrgDesp());

				el.setAttribute("todaycount",
						Long.toString((o.getToDayCount())));
				el.setAttribute("curmonthcount",
						Long.toString(o.getCurMonthCount()));
				el.setAttribute("prevmonthcount",
						Long.toString(o.getPrevMonthCount()));
				el.setAttribute("rate", o.getRate());

				content.appendChild(el);
			}
		} finally {
			if (context != null)
				context.close();
		}

	}

	// 访问用户报表
	protected void queryVisitUsers(AdminSessionObject session, int orgId,
			boolean firstQuery, int firstResult, int maxResults,
			HttpRequest request, Document xmlDoc, Element content)
			throws Throwable {
		if (!((AdminSessionObject) session)
				.hasRight(UserRight.VISITUSERS_REPORT))
			throw new KoneException("您没有查询访问用户报表的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			Integer orgid = request.getParameterIntDef("orgid", null);
			Date startDate = DateTime.parseDate(
					request.getParameter("startDate"), "yyyy-MM-dd");
			ReportBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-reportBean", ReportBeanRemote.class);
			List<?> table = bean.queryVisitUsersDayReport(orgid, startDate);
			appendCountElement(firstQuery, content, xmlDoc, table.size());
			Iterator<?> it = table.iterator();
			while (it.hasNext()) {
				Element el = xmlDoc.createElement("record");
				UserCountStatItem o = (UserCountStatItem) it.next();
				IdentOrg org = (IdentOrg) o.getNameItem();
				el.setAttribute("orgdesp", org.getOrgDesp());

				el.setAttribute("todaycount",
						Long.toString((o.getToDayCount())));
				el.setAttribute("curmonthcount",
						Long.toString(o.getCurMonthCount()));
				el.setAttribute("prevmonthcount",
						Long.toString(o.getPrevMonthCount()));
				el.setAttribute("rate", o.getRate());

				content.appendChild(el);
			}
		} finally {
			if (context != null)
				context.close();
		}

	}

	// 业务量分析报表
	@SuppressWarnings("unchecked")
	protected void queryTransactions(AdminSessionObject session, int orgId,
			boolean firstQuery, int firstResult, int maxResults,
			HttpRequest request, Document xmlDoc, Element content)
			throws Throwable {
		if (!((AdminSessionObject) session)
				.hasRight(UserRight.TRANSACTION_REPORT))
			throw new KoneException("您没有查询业务量分析报表的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		List<CachedBusiness> businessList;
		// TreeNode<Integer> businessTree;
		String businessid;
		List<Integer> businessIdList = null;
		try {
			Integer orgid = request.getParameterIntDef("orgid", null);
			Date startDate = DateTime.parseDate(
					request.getParameter("startDate"), "yyyy-MM-dd");
			Date endDate = DateTime.parseDate(request.getParameter("endDate"),
					"yyyy-MM-dd");

			if (orgid == null) {
				businessList = (List<CachedBusiness>) CacheHelper.businessMap
						.values();
			} else {
				businessid = request.getParameter("businessid");
				businessIdList = StringUnit.splitToInt1(businessid, ",");
				businessList = (List<CachedBusiness>) CacheHelper.businessMap
						.values(businessIdList);
			}
			ReportBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-reportBean", ReportBeanRemote.class);

			List<?> table = bean.queryOrgTransactionsReport(orgid,
					businessIdList, startDate, endDate);
			appendCountElement(firstQuery, content, xmlDoc, table.size());

			// //显示标题
			// Element title = xmlDoc.createElement("title");
			// title.setAttribute("area", "");
			// title.setAttribute("startDate", String.valueOf(startDate));
			// title.setAttribute("endDate", String.valueOf(endDate));
			// content.appendChild(title);
			// 显示表头数所
			if (!businessList.isEmpty()) {
				for (CachedBusiness bus : businessList) {
					if (bus != null) {
						Element el = xmlDoc.createElement("header");
						el.setAttribute("name", bus.getText());
						content.appendChild(el);
					}
				}
			}

			Iterator<?> it = table.iterator();
			while (it.hasNext()) {
				Element el = xmlDoc.createElement("record");
				BusinessStatItem<IdentOrg> o = (BusinessStatItem<IdentOrg>) it
						.next();
				IdentOrg org = (IdentOrg) o.getNameItem();
				el.setAttribute("orgdesp", org.getOrgDesp());
				// 明细数据
				if (!o.getItems().isEmpty()) {
					for (BusinessStatField f : o.getItems()) {
						Element subEl = xmlDoc.createElement("detail");
						subEl.setAttribute("count", Long.toString(f.getCount()));
						subEl.setAttribute("amount",
								Long.toString(f.getAmount()));
						el.appendChild(subEl);
					}
				}
				el.setAttribute("totalcount", Long.toString((o.getCount())));
				el.setAttribute("totalamount", Long.toString(o.getMoney()));
				el.setAttribute("prevmonthcount",
						Long.toString(o.getPrevMonthCount()));
				el.setAttribute("prevmonthamount", Long.toString(o.getPrevMonthMoney()));
				el.setAttribute("countrate", o.getCountRate());
				el.setAttribute("amountrate", o.getMoneyRate());
				content.appendChild(el);
			}
		} finally {
			if (context != null)
				context.close();
		}

	}

	// 应收款报表
	protected void queryReceivable(AdminSessionObject session, int orgId,
			boolean firstQuery, int firstResult, int maxResults,
			HttpRequest request, Document xmlDoc, Element content)
			throws Throwable {
		if (!((AdminSessionObject) session)
				.hasRight(UserRight.RECEIVABLE_REPORT))
			throw new KoneException("您没有查询应收款报表的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		String paywayid = request.getParameter("paywayid");
		Integer orgid = request.getParameterIntDef("orgid", null);
		try {
			List<Short> paywayIdList = StringUnit.splitToShort1(paywayid, ",");
			ReportBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-reportBean", ReportBeanRemote.class);
			DateTime startDateTime = new DateTime(
					request.getParameter("startDate"), "yyyy-MM-dd");
			DateTime endDateTime = new DateTime(
					request.getParameter("endDate"), "yyyy-MM-dd");
			List<?> table = bean.queryOrgReceivableReport(orgid, paywayIdList,
					startDateTime.getTime(), endDateTime.getTime());
			appendCountElement(firstQuery, content, xmlDoc, table.size());
			Iterator<?> it = table.iterator();
			while (it.hasNext()) {
				Element el = xmlDoc.createElement("record");
				@SuppressWarnings("rawtypes")
				ReceivableStatItem o = (ReceivableStatItem) it.next();
				IdentOrg org = (IdentOrg) o.getNameItem();
				el.setAttribute("orgdesp", org.getOrgDesp());
				el.setAttribute("count", Long.toString((o.getCount())));
				el.setAttribute("money", Long.toString(o.getMoney()));
				el.setAttribute("successCount",
						Long.toString(o.getSuccessCount()));
				el.setAttribute("successMoney",
						Long.toString(o.getSuccessMoney()));
				el.setAttribute("errorCount", Long.toString(o.getErrorCount()));
				el.setAttribute("errorMoney", Long.toString(o.getErrorMoney()));
				el.setAttribute("faultRate", o.getFaultRate());
				el.setAttribute("refundCount",
						Long.toString(o.getRefundCount()));
				el.setAttribute("refundMoney",
						Long.toString(o.getRefundMoney()));
				el.setAttribute("cancelRefundCount",
						Long.toString(o.getCancelRefundCount()));
				el.setAttribute("cancelRefundMoney",
						Long.toString(o.getCancelRefundMoney()));
				el.setAttribute("totalMoney", Long.toString(o.getTotalMoney()));

				content.appendChild(el);
			}
		} finally {
			context.close();
		}
	}

	// 业务点击率报表
	@SuppressWarnings("unchecked")
	protected void queryBusinessVisit(AdminSessionObject session, int orgId,
			boolean firstQuery, int firstResult, int maxResults,
			HttpRequest request, Document xmlDoc, Element content)
			throws Throwable {
		if (!((AdminSessionObject) session)
				.hasRight(UserRight.BUSINESSVISIT_REPORT))
			throw new KoneException("您没有查询业务点击率报表的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		String businessid = request.getParameter("businessid");
		Integer orgid = request.getParameterIntDef("orgid", null);
		String startDate = request.getParameter("startDate");
		String endDate = request.getParameterDef("endDate", startDate);
		try {
			List<Integer> businessIdList = StringUnit.splitToInt1(businessid,
					",");
			List<CachedBusiness> businessList = (List<CachedBusiness>) CacheHelper.businessMap
					.values(businessIdList);
			ReportBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-reportBean", ReportBeanRemote.class);
			DateTime startDateTime = new DateTime(startDate, "yyyy-MM-dd");
			DateTime endDateTime = new DateTime(endDate, "yyyy-MM-dd");
			List<?> table = bean.queryOrgBusinessVisitReport(orgid,
					businessIdList, startDateTime.getTime(),
					endDateTime.getTime());
			appendCountElement(firstQuery, content, xmlDoc, table.size());

			// 显示表头数所
			if (!businessList.isEmpty()) {
				for (CachedBusiness bus : businessList) {
					if (bus != null) {
						Element el = xmlDoc.createElement("header");
						el.setAttribute("name", bus.getText());
						content.appendChild(el);
					}
				}
			}

			Iterator<?> it = table.iterator();
			while (it.hasNext()) {
				Element el = xmlDoc.createElement("record");
				@SuppressWarnings("rawtypes")
				PageClickStatItem o = (PageClickStatItem) it.next();
				IdentOrg org = (IdentOrg) o.getNameItem();
				el.setAttribute("orgdesp", org.getOrgDesp());
				el.setAttribute("count", Long.toString((o.getCount())));
				// 明细数据
				if (!o.getItems().isEmpty()) {
					for (PageClickStatField f : (List<PageClickStatField>) o
							.getItems()) {
						Element subEl = xmlDoc.createElement("detail");
						subEl.setAttribute("count", Long.toString(f.getCount()));
						el.appendChild(subEl);
					}
				}
				el.setAttribute("prevMonthCount",
						Long.toString(o.getPrevMonthCount()));
				el.setAttribute("countRate", o.getCountRate());
				content.appendChild(el);
			}
		} finally {
			context.close();
		}
	}

	// 打印统计报表
	@SuppressWarnings("unchecked")
	protected void queryPrint(AdminSessionObject session, int orgId,
			boolean firstQuery, int firstResult, int maxResults,
			HttpRequest request, Document xmlDoc, Element content)
			throws Throwable {
		if (!((AdminSessionObject) session).hasRight(UserRight.PRINT_REPORT))
			throw new KoneException("您没有查询打印统计报表的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		Integer orgid = request.getParameterIntDef("orgid", null);
		Integer manufid = request.getParameterIntDef("manufid", null);
		String startDate = "", endDate = "";
		Collection<CachedPrintType> printTypeList;
		List<Short> printTypeIdList;
		String printtype;
		printtype = request.getParameter("printtype");
		printTypeIdList = StringUnit.splitToShort1(printtype, ",");
		printTypeList = CacheHelper.printTypeMap.values(printTypeIdList);
		startDate = request.getParameter("startDate");
		endDate = request.getParameterDef("endDate", startDate);
		try {
			ReportBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-reportBean", ReportBeanRemote.class);
			DateTime startDateTime = new DateTime(startDate, "yyyy-MM-dd");
			DateTime endDateTime = new DateTime(endDate, "yyyy-MM-dd");
			List<?> table = bean.queryOrgPrintReport(orgid, manufid,
					printTypeIdList, startDateTime.getTime(),
					endDateTime.getTime());
			appendCountElement(firstQuery, content, xmlDoc, table.size());

			// 显示表头数所
			if (!printTypeList.isEmpty()) {
				for (CachedPrintType bus : printTypeList) {
					if (bus != null) {
						Element el = xmlDoc.createElement("header");
						el.setAttribute("name", bus.getText());
						content.appendChild(el);
					}
				}
			}

			Iterator<?> it = table.iterator();
			while (it.hasNext()) {
				Element el = xmlDoc.createElement("record");
				@SuppressWarnings("rawtypes")
				TermPrintStatItem o = (TermPrintStatItem) it.next();
				IdentOrg org = (IdentOrg) o.getNameItem();
				el.setAttribute("orgdesp", org.getOrgDesp());
				el.setAttribute("count", Long.toString((o.getCount())));
				// 明细数据
				if (!o.getItems().isEmpty()) {
					for (TermPrintStatField f : (List<TermPrintStatField>) o
							.getItems()) {
						Element subEl = xmlDoc.createElement("detail");
						subEl.setAttribute("count", Long.toString(f.getCount()));
						subEl.setAttribute("lines", Long.toString(f.getLines()));
						el.appendChild(subEl);
					}
				}
				el.setAttribute("lines", Long.toString(o.getLines()));
				el.setAttribute("prevMonthCount",
						Long.toString(o.getPrevMonthCount()));
				el.setAttribute("prevMonthLines",
						Long.toString(o.getPrevMonthLines()));
				el.setAttribute("countRate", o.getCountRate());
				el.setAttribute("linesRate", o.getLinesRate());
				content.appendChild(el);
			}
		} finally {
			context.close();
		}
	}

}
