package kxd.engine.scs.admin.drivers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import kxd.engine.cache.beans.sts.CachedTradeCode;
import kxd.engine.helper.CacheHelper;
import kxd.engine.helper.beans.IdentOrg;
import kxd.engine.scs.admin.AdminSessionObject;
import kxd.engine.scs.admin.util.UserRight;
import kxd.engine.scs.report.TermNameItem;
import kxd.engine.scs.report.beans.BusinessStatField;
import kxd.engine.scs.report.beans.BusinessStatItem;
import kxd.engine.scs.report.beans.FaultStatField;
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
import kxd.util.DataUnit;
import kxd.util.DateTime;
import kxd.util.KeyValue;
import kxd.util.KoneException;
import kxd.util.StringUnit;
import kxd.util.excel.ExcelCell;
import kxd.util.excel.ExcelSheet;

public class ExeclReportExecutor extends BaseExeclReportExecutor {

	@Override
	public void execute(HttpRequest request, WritableWorkbook book)
			throws IOException, NoSuchFieldException {
		AdminSessionObject session = new AdminSessionObject(request, null);
		if (((AdminSessionObject) session).getLoginUser() == null) {
			throw new KoneException("请先登录,再执行本操作");
		}
		String cmd = request.getParameter("cmd");
		int maxresults = request.getParameterIntDef("maxresults", 50);
		int firstresult = request.getParameterIntDef("firstresult", 0);
		if (maxresults > 2000)
			maxresults = 2000;
		if (firstresult < 0)
			firstresult = 0;
		int orgId = request.getParameterInt("orgid");
		try {
			if (cmd.equals("querytermreceivablelist")) {
				queryTermReceivalbeReport(session, request, orgId, firstresult,
						maxresults, book);
			} else if (cmd.equals("queryreceivablereport")) {
				queryReceivalbeReport(session, request, orgId, book);
			} else if (cmd.equals("querytermtransactionlist")) {
				queryTermTransactionsReport(session, orgId, firstresult,
						maxresults, request, book);
			} else if (cmd.equals("querytransactionsreport")) {
				queryTransactionsReport(session, orgId, request, book);
			} else if (cmd.equals("queryprintlist")) {
				queryPrintReportList(session, orgId, firstresult, maxresults,
						request, book);
			} else if (cmd.equals("queryprintreport")) {
				queryPrintReport(session, orgId, request, book);
			} else if (cmd.equals("queryfaultlist")) {
				queryFaultReport(session, orgId, firstresult, maxresults,
						request, book);
			} else if (cmd.equals("querytermopenratelist")) {
				queryTermOpenRateReport(session, orgId, firstresult,
						maxresults, request, book);
			} else if (cmd.equals("querybusinessvisitreport")) {
				queryBusinessVisitReport(session, orgId, firstresult,
						maxresults, request, book);
			} else if (cmd.equals("queryloginuserreport")) {
				queryLoginUserReport(session, orgId, request, book);
			} else if (cmd.equals("queryvisituserreport")) {
				queryVisitUserReport(session, orgId, request, book);
			} else if (cmd.equals("querytradedetail")) {
				queryTradeDetailReport(session, orgId, firstresult, maxresults,
						request, book);
			} else if (cmd.equals("queryrefunddetail")) {
				queryRefundDetailReport(session, orgId, firstresult,
						maxresults, request, book);
			}
			book.write();
			book.close();
		} catch (Throwable e) {
			throw new KoneException(e);
		}
	}

	protected void queryRefundDetailReport(AdminSessionObject session,
			int orgId, int firstResult, int maxResults, HttpRequest request,
			WritableWorkbook book) throws Throwable {
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
					false, firstResult, maxResults, orgId, termCode, startTime,
					endTime, userNo, termGlide, tradeglide, payType);
			ExcelSheet sh = new ExcelSheet();
			String fields[] = new String[] { "终端流水", "交易流水", "用户号码", "交易金额",
					"退款时间", "退款结果" };
			int row = createSheetHead(
					request,
					sh,
					10,
					"自助终端退款明细",
					"起始日期：" + DataUnit.formatDateTime(startTime, "yyyy-MM-dd")
							+ "  截止时间: "
							+ DataUnit.formatDateTime(endTime, "yyyy-MM-dd"),
					"单位：元");
			row = createDetailHead2(request, sh, row, null, null,
					new KeyValue<String, String[]>("明细数据", fields));
			Iterator<?> it = r.getResultList().iterator();
			while (it.hasNext()) {
				RefundDetailItem<?> o = (RefundDetailItem<?>) it.next();
				TermNameItem nameItem = (TermNameItem) o.getNameItem();
				List<ExcelCell> cells = sh.add();
				cells.add(new ExcelCell(row, 0, nameItem.getProvinceName(),
						ExcelCell.LEFT, 1));
				cells.add(new ExcelCell(row, 1, nameItem.getCityName(),
						ExcelCell.LEFT, 1));
				cells.add(new ExcelCell(row, 2, nameItem.getHallName(),
						ExcelCell.LEFT, 1));
				cells.add(new ExcelCell(row, 3, nameItem.getTermCode(),
						ExcelCell.LEFT, 1));
				int i = 4;
				cells.add(new ExcelCell(row, i++, o.getTermGlide(),
						ExcelCell.LEFT, 1));
				cells.add(new ExcelCell(row, i++, o.getTradeGlide(),
						ExcelCell.LEFT, 1));
				cells.add(new ExcelCell(row, i++, o.getUserNo(),
						ExcelCell.LEFT, 1));
				cells.add(new ExcelCell(row, i++, DataUnit.fenToYuan(o
						.getAmount()), ExcelCell.RIGHT, 1));
				cells.add(new ExcelCell(row, i++, DataUnit.formatDateTime(
						o.getRefundTime(), "HH:mm:ss"), ExcelCell.RIGHT, 1));
				cells.add(new ExcelCell(row, i++, o.isCanceled() ? "退款撤消" : o
						.getRefundResult().toString(), ExcelCell.LEFT, 1));
				row++;
			}
			WritableSheet sheet = book.createSheet("您没有查询退款明细的权限.", 0);
			sh.write(sheet, new int[] { 18, 18, 18, 15, 20, 20, 15, 15, 15, 18,
					15, 15 });
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void queryTradeDetailReport(AdminSessionObject session,
			int orgId, int firstResult, int maxResults, HttpRequest request,
			WritableWorkbook book) throws Throwable {
		if (!((AdminSessionObject) session).hasRight(UserRight.TRADEPROC))
			throw new KoneException("您没有查询交易明细的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
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
			Integer tradeStatus = request.getParameterIntDef("tradeStatus",
					null);
			Integer refundStatus = request.getParameterIntDef("refundStatus",
					null);
			String tradeglide = request.getParameterDef("tradeglide", null);
			Integer recStatus = request.getParameterIntDef("recStatus", null);
			String businessid = request.getParameter("businessid");
			List<Integer> businessIdList = StringUnit.splitToInt1(businessid,
					",");
			SettlementBeanRemote bean = context
					.lookup(Lookuper.JNDI_TYPE_EJB,
							"kxd-ejb-settlementServiceBean",
							SettlementBeanRemote.class);
			QueryResult<?> r = bean.queryTradeDetail(
					((AdminSessionObject) session).getLoginUser().getId(),
					false, firstResult, maxResults, orgId, termCode,
					DataUnit.parseDateTime(startTime, "yyyy-MM-dd HH:mm:ss"),
					DataUnit.parseDateTime(endTime, "yyyy-MM-dd HH:mm:ss"),
					userNo, termGlide, tradeglide, payStatus, refundStatus,
					tradeStatus, recStatus, businessIdList);
			ExcelSheet sh = new ExcelSheet();
			String fields[] = new String[] { "终端流水", "交易流水", "用户号码", "交易金额",
					"交易时间", "交易描述", "支付状态", "交易状态", "退款状态", "对账状态", "结果描述" };
			int row = createSheetHead(request, sh, 15, "自助终端交易明细", "起始日期："
					+ startTime + "  截止时间: " + endTime, "单位：元");
			row = createDetailHead2(request, sh, row, null, null,
					new KeyValue<String, String[]>("明细数据", fields));
			Iterator<?> it = r.getResultList().iterator();
			while (it.hasNext()) {
				TradeDetailItem<?> o = (TradeDetailItem<?>) it.next();
				TermNameItem nameItem = (TermNameItem) o.getNameItem();
				List<ExcelCell> cells = sh.add();
				CachedTradeCode tradeCode = CacheHelper.tradeCodeMap.get(o
						.getTradeCodeId());
				cells.add(new ExcelCell(row, 0, nameItem.getProvinceName(),
						ExcelCell.LEFT, 1));
				cells.add(new ExcelCell(row, 1, nameItem.getCityName(),
						ExcelCell.LEFT, 1));
				cells.add(new ExcelCell(row, 2, nameItem.getHallName(),
						ExcelCell.LEFT, 1));
				cells.add(new ExcelCell(row, 3, nameItem.getTermCode(),
						ExcelCell.LEFT, 1));
				int i = 4;
				cells.add(new ExcelCell(row, i++, o.getTermGlide(),
						ExcelCell.LEFT, 1));
				cells.add(new ExcelCell(row, i++, o.getTradeGlide(),
						ExcelCell.LEFT, 1));
				cells.add(new ExcelCell(row, i++, o.getUserNo(),
						ExcelCell.LEFT, 1));
				cells.add(new ExcelCell(row, i++, DataUnit.fenToYuan(o
						.getAmount()), ExcelCell.RIGHT, 1));
				cells.add(new ExcelCell(row, i++, DataUnit.formatDateTime(
						o.getTradeTime(), "HH:mm:ss"), ExcelCell.RIGHT, 1));
				cells.add(new ExcelCell(row, i++, tradeCode.getTradeDesp(),
						ExcelCell.LEFT, 1));
				cells.add(new ExcelCell(row, i++, o.getPayStatus().toString(),
						ExcelCell.LEFT, 1));
				cells.add(new ExcelCell(row, i++,
						o.getTradeStatus().toString(), ExcelCell.LEFT, 1));
				cells.add(new ExcelCell(row, i++, o.getRefundStatus()
						.toString(), ExcelCell.LEFT, 1));
				cells.add(new ExcelCell(row, i++, o.getRecStatus().toString(),
						ExcelCell.LEFT, 1));
				cells.add(new ExcelCell(row, i++, o.getTradeResult(),
						ExcelCell.LEFT, 1));
				row++;
			}
			WritableSheet sheet = book.createSheet("您没有查询交易明细的权限.", 0);
			sh.write(sheet, new int[] { 18, 18, 18, 15, 20, 20, 15, 15, 15, 18,
					15, 15 });
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void queryVisitUserReport(AdminSessionObject session, int orgId,
			HttpRequest request, WritableWorkbook book) throws Throwable {
		if (!((AdminSessionObject) session)
				.hasRight(UserRight.VISITUSERS_REPORT))
			throw new KoneException("您没有查询访问用户数报表的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			Date day = DateTime.parseDate(request.getParameter("startDate"),
					"yyyy-MM-dd");
			ReportBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-reportBean", ReportBeanRemote.class);
			List<?> r = bean.queryVisitUsersDayReport(orgId, day);
			ExcelSheet sh = new ExcelSheet();
			String fields[] = new String[] { "当日", "本月累计", "上有同期", "环比增长(%)" };
			int row = createSheetHead(request, sh, 9, "访问用户数日报表", "统计日期："
					+ request.getParameter("startDate"), "单位：户/%");
			row = createHead2(request, sh, row, null, null,
					new KeyValue<String, String[]>("合计", fields),
					new KeyValue<String, String[]>("自助终端", fields));
			Iterator<?> it = r.iterator();
			while (it.hasNext()) {
				UserCountStatItem o = (UserCountStatItem) it.next();
				IdentOrg nameItem = (IdentOrg) o.getNameItem();
				List<ExcelCell> cells = sh.add();
				cells.add(new ExcelCell(row, 0, nameItem.getOrgDesp(),
						ExcelCell.LEFT, 1));
				int i = 1;
				cells.add(new ExcelCell(row, i++, Long.toString(o
						.getToDayCount()), ExcelCell.RIGHT, 1));
				cells.add(new ExcelCell(row, i++, Long.toString(o
						.getCurMonthCount()), ExcelCell.RIGHT, 1));
				cells.add(new ExcelCell(row, i++, Long.toString(o
						.getPrevMonthCount()), ExcelCell.RIGHT, 1));
				cells.add(new ExcelCell(row, i++, o.getRate(), ExcelCell.RIGHT,
						1));
				cells.add(new ExcelCell(row, i++, Long.toString(o
						.getToDayCount()), ExcelCell.RIGHT, 1));
				cells.add(new ExcelCell(row, i++, Long.toString(o
						.getCurMonthCount()), ExcelCell.RIGHT, 1));
				cells.add(new ExcelCell(row, i++, Long.toString(o
						.getPrevMonthCount()), ExcelCell.RIGHT, 1));
				cells.add(new ExcelCell(row, i++, o.getRate(), ExcelCell.RIGHT,
						1));
				row++;
			}
			WritableSheet sheet = book.createSheet("自助终端访问用户数日报表", 0);
			sh.write(sheet, new int[] { 20, 15, 15, 15, 15, 15, 15, 15, 15, 18,
					15, 15 });
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void queryLoginUserReport(AdminSessionObject session, int orgId,
			HttpRequest request, WritableWorkbook book) throws Throwable {
		if (!((AdminSessionObject) session)
				.hasRight(UserRight.LOGINUSERS_REPORT))
			throw new KoneException("您没有查询登录用户数报表的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			Date day = DateTime.parseDate(request.getParameter("startDate"),
					"yyyy-MM-dd");
			ReportBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-reportBean", ReportBeanRemote.class);
			List<?> r = bean.queryLoginUsersDayReport(orgId, day);
			ExcelSheet sh = new ExcelSheet();
			String fields[] = new String[] { "当日", "本月累计", "上有同期", "环比增长(%)" };
			int row = createSheetHead(request, sh, 9, "登录用户数日报表", "统计日期："
					+ request.getParameter("startDate"), "单位：户/%");
			row = createHead2(request, sh, row, null, null,
					new KeyValue<String, String[]>("合计", fields),
					new KeyValue<String, String[]>("自助终端", fields));
			Iterator<?> it = r.iterator();
			while (it.hasNext()) {
				UserCountStatItem o = (UserCountStatItem) it.next();
				IdentOrg nameItem = (IdentOrg) o.getNameItem();
				List<ExcelCell> cells = sh.add();
				cells.add(new ExcelCell(row, 0, nameItem.getOrgDesp(),
						ExcelCell.LEFT, 1));
				int i = 1;
				cells.add(new ExcelCell(row, i++, Long.toString(o
						.getToDayCount()), ExcelCell.RIGHT, 1));
				cells.add(new ExcelCell(row, i++, Long.toString(o
						.getCurMonthCount()), ExcelCell.RIGHT, 1));
				cells.add(new ExcelCell(row, i++, Long.toString(o
						.getPrevMonthCount()), ExcelCell.RIGHT, 1));
				cells.add(new ExcelCell(row, i++, o.getRate(), ExcelCell.RIGHT,
						1));
				cells.add(new ExcelCell(row, i++, Long.toString(o
						.getToDayCount()), ExcelCell.RIGHT, 1));
				cells.add(new ExcelCell(row, i++, Long.toString(o
						.getCurMonthCount()), ExcelCell.RIGHT, 1));
				cells.add(new ExcelCell(row, i++, Long.toString(o
						.getPrevMonthCount()), ExcelCell.RIGHT, 1));
				cells.add(new ExcelCell(row, i++, o.getRate(), ExcelCell.RIGHT,
						1));
				row++;
			}
			WritableSheet sheet = book.createSheet("自助终端登录用户数日报表", 0);
			sh.write(sheet, new int[] { 20, 15, 15, 15, 15, 15, 15, 15, 15, 18,
					15, 15 });
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void queryBusinessVisitReport(AdminSessionObject session,
			int orgId, int firstResult, int maxResults, HttpRequest request,
			WritableWorkbook book) throws Throwable {
		if (!((AdminSessionObject) session)
				.hasRight(UserRight.BUSINESSVISIT_REPORT))
			throw new KoneException("您没有查询业务击率报表的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			List<Integer> ls = StringUnit.splitToInt1(
					request.getParameter("businessid"), ",");
			Date startDate = DateTime.parseDate(
					request.getParameter("startDate"), "yyyy-MM-dd");
			Date endDate = DateTime.parseDate(request.getParameter("endDate"),
					"yyyy-MM-dd");
			ReportBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-reportBean", ReportBeanRemote.class);
			List<?> r = bean.queryOrgBusinessVisitReport(orgId, ls, startDate,
					endDate);
			ExcelSheet sh = new ExcelSheet();
			int row = createSheetHead(request, sh, ls.size() + 4,
					"自助终端业务点击率日报表", "起始日期：" + request.getParameter("startDate")
							+ " 截止日期：" + request.getParameter("endDate"),
					"单位：元/%");
			String field[] = new String[] { "合计", "上月完成", "环比(%)" };
			Collection<String> keys = CacheHelper.businessMap.textValues(ls);
			row = createHead2(request, sh, row, "业务类型", keys,
					new KeyValue<String, String[]>("合计", field));
			Iterator<?> it = r.iterator();
			while (it.hasNext()) {
				PageClickStatItem<?> o = (PageClickStatItem<?>) it.next();
				IdentOrg nameItem = (IdentOrg) o.getNameItem();
				List<ExcelCell> cells = sh.add();
				cells.add(new ExcelCell(row, 0, nameItem.getOrgDesp(),
						ExcelCell.LEFT, 1));
				int i = 1;
				for (PageClickStatField f : o.getItems()) {
					cells.add(new ExcelCell(row, i++, Long.toString(f
							.getCount()), ExcelCell.RIGHT, 1));
				}
				cells.add(new ExcelCell(row, i++, Long.toString(o.getCount()),
						ExcelCell.RIGHT, 1));
				cells.add(new ExcelCell(row, i++, Long.toString(o
						.getPrevMonthCount()), ExcelCell.RIGHT, 1));
				cells.add(new ExcelCell(row, i++, o.getCountRate(),
						ExcelCell.RIGHT, 1));
				row++;
			}
			WritableSheet sheet = book.createSheet("自助终端业务点击率日报表", 0);
			sh.write(sheet, new int[] { 20, 15, 15, 15, 15, 15, 15, 15, 15, 18,
					15, 15 });
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void queryTermOpenRateReport(AdminSessionObject session,
			int orgId, int firstResult, int maxResults, HttpRequest request,
			WritableWorkbook book) throws Throwable {
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
			QueryResult<?> r = bean.queryTermUseRateReport(false, firstResult,
					maxResults, orgId,
					request.getParameterIntDef("manufid", null), startDate,
					endDate);
			ExcelSheet sh = new ExcelSheet();
			int row = createSheetHead(request, sh, 11, "自助终端开机率报表",
					"起始日期：" + request.getParameter("startDate") + " 截止日期："
							+ request.getParameter("endDate"), "单位：笔/元/%");
			row = createDetailHead1(request, sh, row, "应开机时间", "实际开机时间",
					"有人使用时间", "无人使用时间", "开机率(%)", "上月同期开机时间", "环比(%)");
			Iterator<?> it = r.getResultList().iterator();
			while (it.hasNext()) {
				OpenRateStatItem o = (OpenRateStatItem) it.next();
				TermNameItem nameItem = (TermNameItem) o.getNameItem();
				ArrayList<ExcelCell> cells = sh.add();
				cells.add(new ExcelCell(row, 0, nameItem.getProvinceName(),
						ExcelCell.LEFT, 1));
				cells.add(new ExcelCell(row, 1, nameItem.getCityName(),
						ExcelCell.LEFT, 1));
				cells.add(new ExcelCell(row, 2, nameItem.getHallName(),
						ExcelCell.LEFT, 1));
				cells.add(new ExcelCell(row, 3, nameItem.getTermCode(),
						ExcelCell.LEFT, 1));
				cells.add(new ExcelCell(row, 4, DateTime.minuteToDHMS(o
						.getNeedOpenTime()), ExcelCell.RIGHT, 1));
				cells.add(new ExcelCell(row, 5, DateTime.minuteToDHMS(o
						.getRealOpenTime()), ExcelCell.RIGHT, 1));
				cells.add(new ExcelCell(row, 6, DateTime.minuteToDHMS(o
						.getBusyTime()), ExcelCell.RIGHT, 1));
				cells.add(new ExcelCell(row, 7, DateTime.minuteToDHMS(o
						.getIdleTime()), ExcelCell.RIGHT, 1));
				cells.add(new ExcelCell(row, 8, DateTime.minuteToDHMS(o
						.getPrevMonthOpenTime()), ExcelCell.RIGHT, 1));
				cells.add(new ExcelCell(row, 9, o.getOpenRate(),
						ExcelCell.RIGHT, 1));
				cells.add(new ExcelCell(row, 10, o.getCycleRate(),
						ExcelCell.RIGHT, 1));
				row++;
			}
			WritableSheet sheet = book.createSheet("自助终端开机率报表", 0);
			sh.write(sheet, new int[] { 15, 15, 15, 15, 15, 15, 15, 15, 15, 18,
					15, 15 });
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void queryFaultReport(AdminSessionObject session, int orgId,
			int firstResult, int maxResults, HttpRequest request,
			WritableWorkbook book) throws Throwable {
		if (!((AdminSessionObject) session).hasRight(UserRight.FAULT_REPORT))
			throw new KoneException("您没有查询故障报表的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			List<Integer> ls = StringUnit.splitToInt1(
					request.getParameter("alarmcategoryid"), ",");
			Date startDate = DateTime.parseDate(
					request.getParameter("startDate"), "yyyy-MM-dd");
			Date endDate = DateTime.parseDate(request.getParameter("endDate"),
					"yyyy-MM-dd");
			ReportBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-reportBean", ReportBeanRemote.class);
			QueryResult<?> r = bean.queryTermFaultReport(false, firstResult,
					maxResults, orgId,
					request.getParameterIntDef("manufid", null), ls, startDate,
					endDate);
			ExcelSheet sh = new ExcelSheet();
			int row = createSheetHead(request, sh, ls.size() + 16, "自助终端故障日报表",
					"报表日期：" + request.getParameter("startDate"), "单位：次/分钟/%");
			String field[] = new String[] { "本月累计", "上月同期", "环比(%)" };
			Collection<String> keys = CacheHelper.alarmCategoryMap
					.textValues(ls);
			row = createDetailHead2(request, sh, row, "故障类型", keys,
					new KeyValue<String, String[]>("故障次数合计", field),
					new KeyValue<String, String[]>("故障时间合计", field),
					new KeyValue<String, String[]>("响应时间合计", field),
					new KeyValue<String, String[]>("恢复时间合计", field));
			Iterator<?> it = r.getResultList().iterator();
			while (it.hasNext()) {
				FaultStatItem o = (FaultStatItem) it.next();
				TermNameItem nameItem = (TermNameItem) o.getNameItem();
				List<ExcelCell> cells = sh.add();
				cells.add(new ExcelCell(row, 0, nameItem.getProvinceName(),
						ExcelCell.LEFT, 1));
				cells.add(new ExcelCell(row, 1, nameItem.getCityName(),
						ExcelCell.LEFT, 1));
				cells.add(new ExcelCell(row, 2, nameItem.getHallName(),
						ExcelCell.LEFT, 1));
				cells.add(new ExcelCell(row, 3, nameItem.getTermCode(),
						ExcelCell.LEFT, 1));
				int i = 4;
				for (FaultStatField ff : o.getItems()) {
					cells.add(new ExcelCell(row, i++, Long.toString(ff
							.getCount()), ExcelCell.RIGHT, 1));
				}
				cells.add(new ExcelCell(row, i++, Long.toString(o
						.getFaultCount().getCount()), ExcelCell.RIGHT, 1));
				cells.add(new ExcelCell(row, i++, Long.toString(o
						.getFaultCount().getPrevMonthCount()), ExcelCell.RIGHT,
						1));
				cells.add(new ExcelCell(row, i++, o.getFaultCount().getRate(),
						ExcelCell.RIGHT, 1));
				cells.add(new ExcelCell(row, i++, Long.toString(o
						.getFaultTime().getCount()), ExcelCell.RIGHT, 1));
				cells.add(new ExcelCell(row, i++, Long.toString(o
						.getFaultTime().getPrevMonthCount()), ExcelCell.RIGHT,
						1));
				cells.add(new ExcelCell(row, i++, o.getFaultTime().getRate(),
						ExcelCell.RIGHT, 1));
				cells.add(new ExcelCell(row, i++, Long.toString(o
						.getResponseTime().getCount()), ExcelCell.RIGHT, 1));
				cells.add(new ExcelCell(row, i++, Long.toString(o
						.getResponseTime().getPrevMonthCount()),
						ExcelCell.RIGHT, 1));
				cells.add(new ExcelCell(row, i++,
						o.getResponseTime().getRate(), ExcelCell.RIGHT, 1));
				cells.add(new ExcelCell(row, i++, Long.toString(o
						.getRestoreTime().getCount()), ExcelCell.RIGHT, 1));
				cells.add(new ExcelCell(row, i++, Long.toString(o
						.getRestoreTime().getPrevMonthCount()),
						ExcelCell.RIGHT, 1));
				cells.add(new ExcelCell(row, i++, o.getRestoreTime().getRate(),
						ExcelCell.RIGHT, 1));
				row++;
			}
			WritableSheet sheet = book.createSheet("自助终端故障日报表", 0);
			sh.write(sheet, new int[] { 15, 15, 15, 15 });
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void queryPrintReportList(AdminSessionObject session, int orgId,
			int firstResult, int maxResults, HttpRequest request,
			WritableWorkbook book) throws Throwable {
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
			QueryResult<?> r = bean.queryTermPrintReport(false, firstResult,
					maxResults, orgId,
					request.getParameterIntDef("manufid", null), ls, startDate,
					endDate);
			ExcelSheet sh = new ExcelSheet();
			int row = createSheetHead(request, sh, ls.size() * 2 + 10,
					"自助终端打印日报表", "报表日期：" + request.getParameter("startDate"),
					"单位：页/行/%");
			String field[] = new String[] { "页数", "行数" };
			Collection<KeyValue<String, Object[]>> keys = CacheHelper.printTypeMap
					.keyValues(ls, field);
			row = createDetailHead3(request, sh, row, "打印类型", keys,
					new KeyValue<String, String[]>("合计", field),
					new KeyValue<String, String[]>("上月完成", new String[] { "页数",
							"行数", "页数环比", "行数环比" }));
			Iterator<?> it = r.getResultList().iterator();
			while (it.hasNext()) {
				TermPrintStatItem<?> o = (TermPrintStatItem<?>) it.next();
				TermNameItem nameItem = (TermNameItem) o.getNameItem();
				List<ExcelCell> cells = sh.add();
				cells.add(new ExcelCell(row, 0, nameItem.getProvinceName(),
						ExcelCell.LEFT, 1));
				cells.add(new ExcelCell(row, 1, nameItem.getCityName(),
						ExcelCell.LEFT, 1));
				cells.add(new ExcelCell(row, 2, nameItem.getHallName(),
						ExcelCell.LEFT, 1));
				cells.add(new ExcelCell(row, 3, nameItem.getTermCode(),
						ExcelCell.LEFT, 1));
				int i = 4;
				for (TermPrintStatField ff : o.getItems()) {
					cells.add(new ExcelCell(row, i++, Long.toString(ff
							.getCount()), ExcelCell.RIGHT, 1));
					cells.add(new ExcelCell(row, i++, Long.toString(ff
							.getLines()), ExcelCell.RIGHT, 1));
				}
				cells.add(new ExcelCell(row, i++, Long.toString(o.getCount()),
						ExcelCell.RIGHT, 1));
				cells.add(new ExcelCell(row, i++, Long.toString(o.getLines()),
						ExcelCell.RIGHT, 1));
				cells.add(new ExcelCell(row, i++, Long.toString(o
						.getPrevMonthCount()), ExcelCell.RIGHT, 1));
				cells.add(new ExcelCell(row, i++, Long.toString(o
						.getPrevMonthLines()), ExcelCell.RIGHT, 1));
				cells.add(new ExcelCell(row, i++, o.getCountRate(),
						ExcelCell.RIGHT, 1));
				cells.add(new ExcelCell(row, i++, o.getLinesRate(),
						ExcelCell.RIGHT, 1));
				row++;
			}
			WritableSheet sheet = book.createSheet("打印明细日报表", 0);
			sh.write(sheet, new int[] { 15 });
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void queryPrintReport(AdminSessionObject session, int orgId,
			HttpRequest request, WritableWorkbook book) throws Throwable {
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
			List<?> r = bean.queryOrgPrintReport(orgId,
					request.getParameterIntDef("manufid", null), ls, startDate,
					endDate);
			ExcelSheet sh = new ExcelSheet();
			int row = createSheetHead(request, sh, ls.size() * 2 + 7,
					"自助终端打印日报表", "报表日期：" + request.getParameter("startDate")
							+ " 截止日期：" + request.getParameter("endDate"),
					"单位：页/行/%");
			String field[] = new String[] { "页数", "行数" };
			Collection<KeyValue<String, Object[]>> keys = CacheHelper.printTypeMap
					.keyValues(ls, field);
			row = createHead3(request, sh, row, "打印类型", keys,
					new KeyValue<String, String[]>("合计", field),
					new KeyValue<String, String[]>("上月完成", new String[] { "页数",
							"行数", "页数环比", "行数环比" }));
			Iterator<?> it = r.iterator();
			while (it.hasNext()) {
				TermPrintStatItem<?> o = (TermPrintStatItem<?>) it.next();
				IdentOrg nameItem = (IdentOrg) o.getNameItem();
				List<ExcelCell> cells = sh.add();
				cells.add(new ExcelCell(row, 0, nameItem.getOrgDesp(),
						ExcelCell.LEFT, 1));
				int i = 1;
				for (TermPrintStatField ff : o.getItems()) {
					cells.add(new ExcelCell(row, i++, Long.toString(ff
							.getCount()), ExcelCell.RIGHT, 1));
					cells.add(new ExcelCell(row, i++, Long.toString(ff
							.getLines()), ExcelCell.RIGHT, 1));
				}
				cells.add(new ExcelCell(row, i++, Long.toString(o.getCount()),
						ExcelCell.RIGHT, 1));
				cells.add(new ExcelCell(row, i++, Long.toString(o.getLines()),
						ExcelCell.RIGHT, 1));
				cells.add(new ExcelCell(row, i++, Long.toString(o
						.getPrevMonthCount()), ExcelCell.RIGHT, 1));
				cells.add(new ExcelCell(row, i++, Long.toString(o
						.getPrevMonthLines()), ExcelCell.RIGHT, 1));
				cells.add(new ExcelCell(row, i++, o.getCountRate(),
						ExcelCell.RIGHT, 1));
				cells.add(new ExcelCell(row, i++, o.getLinesRate(),
						ExcelCell.RIGHT, 1));
				row++;
			}
			WritableSheet sheet = book.createSheet("自助终端打印日报表", 0);
			sh.write(sheet, new int[] { 15, 15, 15, 15 });
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void queryTermTransactionsReport(AdminSessionObject session,
			int orgId, int firstResult, int maxResults, HttpRequest request,
			WritableWorkbook book) throws Throwable {
		if (!((AdminSessionObject) session)
				.hasRight(UserRight.TRANSACTION_REPORT))
			throw new KoneException("您没有查询业务量分析报表的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			List<Integer> ls = StringUnit.splitToInt1(
					request.getParameter("businessid"), ",");
			Date day = DateTime.parseDate(request.getParameter("startDate"),
					"yyyy-MM-dd");
			ReportBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-reportBean", ReportBeanRemote.class);
			QueryResult<?> r = bean.queryTermTransactionsReport(false,
					firstResult, maxResults, orgId, ls, day);
			ExcelSheet sh = new ExcelSheet();
			int row = createSheetHead(request, sh, ls.size() * 2 + 10,
					"自助终端业务量分析日报表",
					"查询日期：" + request.getParameter("startDate"), "单位：笔/元/%");
			String field[] = new String[] { "业务量", "交易额" };
			Collection<KeyValue<String, Object[]>> keys = CacheHelper.businessMap
					.keyValues(ls, field);
			row = createDetailHead3(request, sh, row, "业务类型", keys,
					new KeyValue<String, String[]>("合计", field),
					new KeyValue<String, String[]>("上月完成", new String[] {
							"业务量", "交易额", "业务量环比", "交易额环比" }));
			Iterator<?> it = r.getResultList().iterator();
			while (it.hasNext()) {
				BusinessStatItem<?> o = (BusinessStatItem<?>) it.next();
				TermNameItem nameItem = (TermNameItem) o.getNameItem();
				List<ExcelCell> cells = sh.add();
				cells.add(new ExcelCell(row, 0, nameItem.getProvinceName(),
						ExcelCell.LEFT, 1));
				cells.add(new ExcelCell(row, 1, nameItem.getCityName(),
						ExcelCell.LEFT, 1));
				cells.add(new ExcelCell(row, 2, nameItem.getHallName(),
						ExcelCell.LEFT, 1));
				cells.add(new ExcelCell(row, 3, nameItem.getTermCode(),
						ExcelCell.LEFT, 1));
				int i = 4;
				for (BusinessStatField ff : o.getItems()) {
					cells.add(new ExcelCell(row, i++, Long.toString(ff
							.getCount()), ExcelCell.RIGHT, 1));
					cells.add(new ExcelCell(row, i++, DataUnit.fenToYuan(Long
							.toString(ff.getAmount())), ExcelCell.RIGHT, 1));
				}
				cells.add(new ExcelCell(row, i++, Long.toString(o.getCount()),
						ExcelCell.RIGHT, 1));
				cells.add(new ExcelCell(row, i++, DataUnit.fenToYuan(Long
						.toString(o.getMoney())), ExcelCell.RIGHT, 1));
				cells.add(new ExcelCell(row, i++, Long.toString(o
						.getPrevMonthCount()), ExcelCell.RIGHT, 1));
				cells.add(new ExcelCell(row, i++, DataUnit.fenToYuan(Long
						.toString(o.getPrevMonthMoney())), ExcelCell.RIGHT, 1));
				cells.add(new ExcelCell(row, i++, o.getCountRate(),
						ExcelCell.RIGHT, 1));
				cells.add(new ExcelCell(row, i++, o.getMoneyRate(),
						ExcelCell.RIGHT, 1));
				row++;
			}
			WritableSheet sheet = book.createSheet("自助终端业务量分析明细日报表", 0);
			sh.write(sheet, new int[] { 15, 15, 15, 15, 15, 15, 15, 15, 15, 15,
					15, 15 });
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void queryTransactionsReport(AdminSessionObject session,
			int orgId, HttpRequest request, WritableWorkbook book)
			throws Throwable {
		if (!((AdminSessionObject) session)
				.hasRight(UserRight.TRANSACTION_REPORT))
			throw new KoneException("您没有查询业务量分析报表的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			List<Integer> ls = StringUnit.splitToInt1(
					request.getParameter("businessid"), ",");
			Date startDate = DateTime.parseDate(
					request.getParameter("startDate"), "yyyy-MM-dd");
			Date endDate = DateTime.parseDate(request.getParameter("endDate"),
					"yyyy-MM-dd");
			ReportBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-reportBean", ReportBeanRemote.class);
			List<?> r = bean.queryOrgTransactionsReport(orgId, ls, startDate,
					endDate);
			ExcelSheet sh = new ExcelSheet();
			int row = createSheetHead(request, sh, ls.size() * 2 + 7,
					"自助终端业务量分析日报表", "起始日期：" + request.getParameter("startDate")
							+ " 截止日期：" + request.getParameter("endDate"),
					"单位：笔/元/%");
			String field[] = new String[] { "业务量", "交易额" };
			Collection<KeyValue<String, Object[]>> keys = CacheHelper.businessMap
					.keyValues(ls, field);
			row = createHead3(request, sh, row, "业务类型", keys,
					new KeyValue<String, String[]>("合计", field),
					new KeyValue<String, String[]>("上月完成", new String[] {
							"业务量", "交易额", "业务量环比", "交易额环比" }));
			Iterator<?> it = r.iterator();
			while (it.hasNext()) {
				BusinessStatItem<?> o = (BusinessStatItem<?>) it.next();
				IdentOrg nameItem = (IdentOrg) o.getNameItem();
				List<ExcelCell> fields = sh.add();
				fields.add(new ExcelCell(row, 0, nameItem.getOrgDesp(),
						ExcelCell.LEFT, 1));
				int i = 1;
				for (BusinessStatField ff : o.getItems()) {
					fields.add(new ExcelCell(row, i++, Long.toString(ff
							.getCount()), ExcelCell.RIGHT, 1));
					fields.add(new ExcelCell(row, i++, DataUnit.fenToYuan(Long
							.toString(ff.getAmount())), ExcelCell.RIGHT, 1));
				}
				fields.add(new ExcelCell(row, i++, Long.toString(o.getCount()),
						ExcelCell.RIGHT, 1));
				fields.add(new ExcelCell(row, i++, DataUnit.fenToYuan(Long
						.toString(o.getMoney())), ExcelCell.RIGHT, 1));
				fields.add(new ExcelCell(row, i++, Long.toString(o
						.getPrevMonthCount()), ExcelCell.RIGHT, 1));
				fields.add(new ExcelCell(row, i++, DataUnit.fenToYuan(Long
						.toString(o.getPrevMonthMoney())), ExcelCell.RIGHT, 1));
				fields.add(new ExcelCell(row, i++, o.getCountRate(),
						ExcelCell.RIGHT, 1));
				fields.add(new ExcelCell(row, i++, o.getMoneyRate(),
						ExcelCell.RIGHT, 1));
				row++;
			}
			WritableSheet sheet = book.createSheet("自助终端业务量分析日报表", 0);
			sh.write(sheet, new int[] { 15, 15, 15, 15, 15, 15, 15, 15, 15, 15,
					15, 15 });
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void queryTermReceivalbeReport(AdminSessionObject session,
			HttpRequest request, int orgId, int firstResult, int maxResults,
			WritableWorkbook book) throws Throwable {
		if (!((AdminSessionObject) session)
				.hasRight(UserRight.RECEIVABLE_REPORT))
			throw new KoneException("您没有查询应收账报表的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			List<Integer> ls = StringUnit.splitToInt1(
					request.getParameter("paywayid"), ",");
			Date day = DateTime.parseDate(request.getParameter("startDate"),
					"yyyy-MM-dd");
			ReportBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-reportBean", ReportBeanRemote.class);
			QueryResult<?> r = bean.queryTermReceivableReport(false,
					firstResult, maxResults, orgId, ls, day);
			ExcelSheet sh = new ExcelSheet();
			int row = createSheetHead(request, sh, 15, "自助终端应收账明细日报表", "查询日期："
					+ request.getParameter("startDate"), "单位：笔/元/%");
			row = createDetailHead1(request, sh, row, "交易笔数", "交易金额", "成功笔数",
					"成功金额", "失败笔数", "失败金额", "失败率", "退款笔数", "退款金额", "取消退款笔数",
					"取消退款金额", "金额小计");
			Iterator<?> it = r.getResultList().iterator();
			while (it.hasNext()) {
				ReceivableStatItem<?> o = (ReceivableStatItem<?>) it.next();
				TermNameItem nameItem = (TermNameItem) o.getNameItem();
				ArrayList<ExcelCell> cells = sh.add();
				cells.add(new ExcelCell(row, 0, nameItem.getProvinceName(),
						ExcelCell.LEFT, 1));
				cells.add(new ExcelCell(row, 1, nameItem.getCityName(),
						ExcelCell.LEFT, 1));
				cells.add(new ExcelCell(row, 2, nameItem.getHallName(),
						ExcelCell.LEFT, 1));
				cells.add(new ExcelCell(row, 3, nameItem.getTermCode(),
						ExcelCell.LEFT, 1));
				cells.add(new ExcelCell(row, 4, Long.toString(o.getCount()),
						ExcelCell.RIGHT, 1));
				cells.add(new ExcelCell(row, 5, DataUnit.fenToYuan(Long
						.toString(o.getMoney())), ExcelCell.RIGHT, 1));
				cells.add(new ExcelCell(row, 6, Long.toString(o
						.getSuccessCount()), ExcelCell.RIGHT, 1));
				cells.add(new ExcelCell(row, 7, DataUnit.fenToYuan(Long
						.toString(o.getSuccessMoney())), ExcelCell.RIGHT, 1));
				cells.add(new ExcelCell(row, 8,
						Long.toString(o.getErrorCount()), ExcelCell.RIGHT, 1));
				cells.add(new ExcelCell(row, 9, DataUnit.fenToYuan(Long
						.toString(o.getErrorMoney())), ExcelCell.RIGHT, 1));
				cells.add(new ExcelCell(row, 10, o.getFaultRate(),
						ExcelCell.RIGHT, 1));
				cells.add(new ExcelCell(row, 11, Long.toString(o
						.getRefundCount()), ExcelCell.RIGHT, 1));
				cells.add(new ExcelCell(row, 12, DataUnit.fenToYuan(Long
						.toString(o.getRefundMoney())), ExcelCell.RIGHT, 1));
				cells.add(new ExcelCell(row, 13, Long.toString(o
						.getCancelRefundCount()), ExcelCell.RIGHT, 1));
				cells.add(new ExcelCell(row, 14, DataUnit.fenToYuan(Long
						.toString(o.getCancelRefundMoney())), ExcelCell.RIGHT,
						1));
				cells.add(new ExcelCell(row, 15, DataUnit.fenToYuan(Long
						.toString(o.getTotalMoney())), ExcelCell.RIGHT, 1));
				row++;
			}
			WritableSheet sheet = book.createSheet("应收账报表", 0);
			sh.write(sheet, new int[] { 15, 15, 15, 15, 15, 15, 15, 15, 15, 15,
					15, 15 });
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void queryReceivalbeReport(AdminSessionObject session,
			HttpRequest request, int orgId, WritableWorkbook book)
			throws Throwable {
		if (!((AdminSessionObject) session)
				.hasRight(UserRight.RECEIVABLE_REPORT))
			throw new KoneException("您没有查询应收账报表的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			List<Short> ls = StringUnit.splitToShort1(
					request.getParameter("paywayid"), ",");
			Date startDate = DateTime.parseDate(
					request.getParameter("startDate"), "yyyy-MM-dd");
			Date endDate = DateTime.parseDate(request.getParameter("endDate"),
					"yyyy-MM-dd");
			ReportBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-reportBean", ReportBeanRemote.class);
			List<?> r = bean.queryOrgReceivableReport(orgId, ls, startDate,
					endDate);
			ExcelSheet sh = new ExcelSheet();
			int row = createSheetHead(request, sh, 9, "自助终端应收账日报表",
					"起始日期：" + request.getParameter("startDate") + " 截止日期："
							+ request.getParameter("endDate"), "单位：笔/元/%");
			row = createHead1(request, sh, row, "交易笔数", "交易金额", "成功笔数", "成功金额",
					"失败笔数", "失败金额", "失败率", "退款笔数", "退款金额", "取消退款笔数", "取消退款金额",
					"金额小计");
			Iterator<?> it = r.iterator();
			while (it.hasNext()) {
				ReceivableStatItem<?> o = (ReceivableStatItem<?>) it.next();
				IdentOrg nameItem = (IdentOrg) o.getNameItem();
				ArrayList<ExcelCell> cells = sh.add();
				int i = 0;
				cells.add(new ExcelCell(row, i++, nameItem.getOrgDesp(),
						ExcelCell.LEFT, 1));
				cells.add(new ExcelCell(row, i++, Long.toString(o.getCount()),
						ExcelCell.RIGHT, 1));
				cells.add(new ExcelCell(row, i++, DataUnit.fenToYuan(Long
						.toString(o.getMoney())), ExcelCell.RIGHT, 1));
				cells.add(new ExcelCell(row, i++, Long.toString(o
						.getSuccessCount()), ExcelCell.RIGHT, 1));
				cells.add(new ExcelCell(row, i++, DataUnit.fenToYuan(Long
						.toString(o.getSuccessMoney())), ExcelCell.RIGHT, 1));
				cells.add(new ExcelCell(row, i++, Long.toString(o
						.getErrorCount()), ExcelCell.RIGHT, 1));
				cells.add(new ExcelCell(row, i++, DataUnit.fenToYuan(Long
						.toString(o.getErrorMoney())), ExcelCell.RIGHT, 1));
				cells.add(new ExcelCell(row, i++, o.getFaultRate(),
						ExcelCell.RIGHT, 1));
				cells.add(new ExcelCell(row, i++, Long.toString(o
						.getRefundCount()), ExcelCell.RIGHT, 1));
				cells.add(new ExcelCell(row, i++, DataUnit.fenToYuan(Long
						.toString(o.getRefundMoney())), ExcelCell.RIGHT, 1));
				cells.add(new ExcelCell(row, i++, Long.toString(o
						.getCancelRefundCount()), ExcelCell.RIGHT, 1));
				cells.add(new ExcelCell(row, i++, DataUnit.fenToYuan(Long
						.toString(o.getCancelRefundMoney())), ExcelCell.RIGHT,
						1));
				cells.add(new ExcelCell(row, i++, DataUnit.fenToYuan(Long
						.toString(o.getTotalMoney())), ExcelCell.RIGHT, 1));
				row++;
			}
			WritableSheet sheet = book.createSheet("应收账日报表", 0);
			sh.write(sheet, new int[] { 15, 15, 15, 15, 15, 15, 15, 15, 15, 15,
					15, 15 });
		} finally {
			if (context != null)
				context.close();
		}
	}
}
