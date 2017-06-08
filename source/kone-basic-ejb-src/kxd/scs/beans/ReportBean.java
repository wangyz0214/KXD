package kxd.scs.beans;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import kxd.engine.dao.BaseBean;
import kxd.engine.helper.beans.IdentOrg;
import kxd.engine.scs.report.BaseReportItem;
import kxd.engine.scs.report.OrgStatReportList;
import kxd.engine.scs.report.TermNameItem;
import kxd.engine.scs.report.TermOpenRateStatReportList;
import kxd.engine.scs.report.TermStatReportList;
import kxd.engine.scs.report.beans.BusinessStatItem;
import kxd.engine.scs.report.beans.FaultStatItem;
import kxd.engine.scs.report.beans.OpenRateStatItem;
import kxd.engine.scs.report.beans.PageClickStatItem;
import kxd.engine.scs.report.beans.ReceivableStatItem;
import kxd.engine.scs.report.beans.TermPrintStatItem;
import kxd.engine.scs.report.beans.UserCountStatItem;
import kxd.remote.scs.interfaces.ReportBeanRemote;
import kxd.remote.scs.util.AppException;
import kxd.remote.scs.util.QueryResult;
import kxd.util.DateTime;
import kxd.util.ObjectCreator;

import org.apache.log4j.Logger;

@Stateless(name = "kxd-ejb-reportBean", mappedName = "kxd-ejb-reportBean")
public class ReportBean extends BaseBean implements ReportBeanRemote {
	static Logger logger = Logger.getLogger(ReportBean.class);

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<?> queryLoginUsersDayReport(int orgId, Date date) {
		DateTime day = new DateTime(date);
		int month = day.getFullMonth();
		int prevMonth = day.addMonths(-1).getFullMonth();
		int d = day.getFullDay() % 100;
		StringBuffer sb = new StringBuffer("select b.ident,a.month");
		for (int i = 1; i <= d; i++) {
			sb.append(",sum(count_" + i + ")");
		}
		sb.append(" from report_loginuser_org_${year} a,org b");

		String where = "a.month=${month} and b.orgid=a.orgid";
		OrgStatReportList<BaseReportItem<IdentOrg>> ls = new OrgStatReportList<BaseReportItem<IdentOrg>>(
				getDao(), new DateTime().addMinutes(1).getTime(),
				new ObjectCreator<BaseReportItem<IdentOrg>>() {
					public UserCountStatItem newInstance() {
						return new UserCountStatItem();
					}
				}, orgId, month, d);	
		return ls.queryMultiMonthReportList("report_loginuser_org_${year}",
				new int[] { month, prevMonth }, sb.toString(),
				where.toString(), "group by b.ident,a.month").getResultList();
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<?> queryVisitUsersDayReport(int orgId, Date date) {
		DateTime day = new DateTime(date);
		int month = day.getFullMonth();
		int prevMonth = day.addMonths(-1).getFullMonth();
		int d = day.getFullDay() % 100;
		StringBuffer sb = new StringBuffer("select b.ident,a.month");
		for (int i = 1; i <= d; i++) {
			sb.append(",sum(count_" + i + ")");
		}
		sb.append(" from report_visituser_org_${year} a,org b");

		String where = "a.month=${month} and b.orgid=a.orgid";
		OrgStatReportList<BaseReportItem<IdentOrg>> ls = new OrgStatReportList<BaseReportItem<IdentOrg>>(
				getDao(), new DateTime().addMinutes(1).getTime(),
				new ObjectCreator<BaseReportItem<IdentOrg>>() {
					public UserCountStatItem newInstance() {
						return new UserCountStatItem();
					}
				}, orgId, month, d);
		return ls.queryMultiMonthReportList("report_visituser_org_${year}",
				new int[] { month, prevMonth }, sb.toString(),
				where.toString(), "group by b.ident,a.month").getResultList();
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<?> queryOrgTransactionsReport(int orgId,
			Collection<Integer> businessList, Date startDate, Date endDate) {
		final Collection<Integer> busList = businessList;
		if (startDate.after(endDate))
			throw new AppException("输入参数错误：终止日期在起始日期之后");
		DateTime startDay = new DateTime(startDate);
		DateTime endDay = new DateTime(endDate);
		if (startDay.getFullMonth() != endDay.getFullMonth())
			throw new AppException("输入参数错误：起始日期和终止日期必须在同一个月之内");
		int month = startDay.getFullMonth();
		int prevMonth = startDay.addMonths(-1).getFullMonth();
		int startday = startDay.getFullDay() % 100;
		int endday = endDay.getFullDay() % 100;
		StringBuffer sb = new StringBuffer(
				"select b.ident,b.orgid,${month},c.businessid,(");
		for (int i = startday; i <= endday; i++) {
			if (i > startday)
				sb.append("+");
			sb.append("suc_count_" + i + "+err_count_" + i);
		}
		sb.append("),(");
		for (int i = startday; i <= endday; i++) {
			if (i > startday)
				sb.append("+");
			sb.append("suc_amount_" + i + "+err_amount_" + i);
		}
		sb.append(") from report_trade_org_${month} a,org b,tradecode c ");
		StringBuffer where = new StringBuffer("a.tradecodeid=c.tradecodeid");
		Iterator<Integer> it = businessList.iterator();

		if (it.hasNext()) {
			where.append(" and (");
			boolean first = true;
			while (it.hasNext()) {
				if (!first)
					where.append(" or ");
				where.append("c.businessid=" + it.next());
				first = false;
			}
			where.append(")");
		}
		where.append(" and b.orgid=a.orgid");
		OrgStatReportList<BusinessStatItem<IdentOrg>> ls = new OrgStatReportList<BusinessStatItem<IdentOrg>>(
				getDao(), new DateTime().addMinutes(1).getTime(),
				new ObjectCreator<BusinessStatItem<IdentOrg>>() {
					public BusinessStatItem<IdentOrg> newInstance() {
						return new BusinessStatItem<IdentOrg>(busList);
					}
				}, orgId, month, startday);
		return ls.queryMultiMonthReportList("report_trade_org_${month}",
				new int[] { month, prevMonth }, sb.toString(),
				where.toString(), null).getResultList();
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public QueryResult<?> queryTermTransactionsReport(boolean queryCount,
			int firstResult, int maxResults, int orgId,
			Collection<Integer> businessList, Date day) {
		final Collection<Integer> busList = businessList;
		DateTime date = new DateTime(day);
		int month = date.getFullMonth();
		int prevMonth = date.addMonths(-1).getFullMonth();
		int startday = date.getFullDay() % 100;
		StringBuffer sb = new StringBuffer(
				"select a.termid,0,${month},b.businessid");
		sb.append(",(suc_count_" + startday + "+err_count_" + startday
				+ "),(suc_amount_" + startday + "+err_amount_" + startday + ")");
		sb.append(" from report_trade_term_${month} a,tradecode b ");
		StringBuffer where = new StringBuffer("a.tradecodeid=b.tradecodeid");
		Iterator<Integer> it = businessList.iterator();
		if (it.hasNext()) {
			where.append(" and (");
			boolean first = true;
			while (it.hasNext()) {
				if (!first)
					where.append(" or ");
				where.append("b.businessid=" + it.next());
				first = false;
			}
			where.append(")");
		}
		TermStatReportList<BusinessStatItem<TermNameItem>> ls = new TermStatReportList<BusinessStatItem<TermNameItem>>(
				getDao(), new DateTime().addMinutes(1).getTime(),
				new ObjectCreator<BusinessStatItem<TermNameItem>>() {
					public BusinessStatItem<TermNameItem> newInstance() {
						return new BusinessStatItem<TermNameItem>(busList);
					}
				}, orgId, month, startday, queryCount, firstResult, maxResults);
		return ls.queryMultiMonthReportList("report_trade_term_${month}",
				new int[] { month, prevMonth }, sb.toString(),
				where.toString(), null);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<?> queryOrgReceivableReport(int orgId,
			Collection<Short> payWayList, Date startDate, Date endDate) {
		if (startDate.after(endDate))
			throw new AppException("输入参数错误：终止日期在起始日期之后");
		DateTime startDay = new DateTime(startDate);
		DateTime endDay = new DateTime(endDate);
		if (startDay.getFullMonth() != endDay.getFullMonth())
			throw new AppException("输入参数错误：起始日期和终止日期必须在同一个月之内");
		int month = startDay.getFullMonth();
		int startday = startDay.getFullDay() % 100;
		int endday = endDay.getFullDay() % 100;
		StringBuffer sb = new StringBuffer("select b.ident,");
		sb.append("sum(");
		for (int i = startday; i <= endday; i++) {
			if (i > startday)
				sb.append("+");
			sb.append("suc_count_" + i);
		}
		sb.append("),sum(");
		for (int i = startday; i <= endday; i++) {
			if (i > startday)
				sb.append("+");
			sb.append("suc_amount_" + i);
		}
		sb.append("),sum(");
		for (int i = startday; i <= endday; i++) {
			if (i > startday)
				sb.append("+");
			sb.append("err_count_" + i);
		}
		sb.append("),sum(");
		for (int i = startday; i <= endday; i++) {
			if (i > startday)
				sb.append("+");
			sb.append("err_amount_" + i);
		}
		sb.append("),sum(");
		for (int i = startday; i <= endday; i++) {
			if (i > startday)
				sb.append("+");
			sb.append("refund_count_" + i);
		}
		sb.append("),sum(");
		for (int i = startday; i <= endday; i++) {
			if (i > startday)
				sb.append("+");
			sb.append("refund_amount_" + i);
		}
		sb.append("),sum(");
		for (int i = startday; i <= endday; i++) {
			if (i > startday)
				sb.append("+");
			sb.append("cancel_refund_count_" + i);
		}
		sb.append("),sum(");
		for (int i = startday; i <= endday; i++) {
			if (i > startday)
				sb.append("+");
			sb.append("cancel_refund_amount_" + i);
		}
		sb.append(")");

		sb.append(" from report_trade_org_" + month + " a,org b");
		sb.append(",tradecode c ");
		StringBuffer where = new StringBuffer("a.tradecodeid=c.tradecodeid");
		if (payWayList != null && payWayList.size() > 0) {
			Iterator<Short> it = payWayList.iterator();
			if (it.hasNext()) {
				where.append(" and (");
				boolean first = true;
				while (it.hasNext()) {
					if (!first)
						where.append(" or ");
					where.append("c.payway=" + it.next());
					first = false;
				}
				where.append(")");
			}
		} else
			where.append(" and (not c.payway is null)");
		where.append(" and a.orgid=b.orgid");
		OrgStatReportList<ReceivableStatItem<IdentOrg>> ls = new OrgStatReportList<ReceivableStatItem<IdentOrg>>(
				getDao(), new DateTime().addMinutes(1).getTime(),
				new ObjectCreator<ReceivableStatItem<IdentOrg>>() {
					public ReceivableStatItem<IdentOrg> newInstance() {
						return new ReceivableStatItem<IdentOrg>();
					}
				}, orgId, month, startday);
		return ls.queryReportList("report_trade_org_" + month, sb.toString(),
				where.toString(), "group by b.ident").getResultList();
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public QueryResult<?> queryTermReceivableReport(boolean queryCount,
			int firstResult, int maxResults, int orgId,
			Collection<Integer> payWayList, Date date) {
		DateTime dateTime = new DateTime(date);
		int month = dateTime.getFullMonth();
		int day = dateTime.getFullDay() % 100;
		StringBuffer sb = new StringBuffer("select a.termid");
		sb.append(",sum(suc_count_" + day + "),sum(suc_amount_" + day
				+ "),sum(err_count_" + day + "),sum(err_amount_" + day
				+ "),sum(refund_count_" + day + "),sum(refund_amount_" + day
				+ "),sum(cancel_refund_count_" + day
				+ "),sum(cancel_refund_amount_" + day + ")");
		sb.append(" from report_trade_term_" + month + " a,org b,term d");
		sb.append(",tradecode c ");
		StringBuffer where = new StringBuffer("d.termid=a.termid");
		where.append(" and a.tradecodeid=c.tradecodeid");
		if (payWayList != null && payWayList.size() > 0) {
			where.append(" and a.tradecodeid=c.tradecodeid");
			Iterator<Integer> it = payWayList.iterator();
			if (it.hasNext()) {
				where.append(" and (");
				boolean first = true;
				while (it.hasNext()) {
					if (!first)
						where.append(" or ");
					where.append("c.payway=" + it.next());
					first = false;
				}
				where.append(")");
			}
		} else
			where.append(" and (not c.payway is null)");
		where.append(" and b.orgid=d.orgid");
		TermStatReportList<ReceivableStatItem<TermNameItem>> ls = new TermStatReportList<ReceivableStatItem<TermNameItem>>(
				getDao(), new DateTime().addMinutes(1).getTime(),
				new ObjectCreator<ReceivableStatItem<TermNameItem>>() {
					public ReceivableStatItem<TermNameItem> newInstance() {
						return new ReceivableStatItem<TermNameItem>();
					}
				}, orgId, month, day, queryCount, firstResult, maxResults);
		return ls.queryReportList("report_trade_term_" + month, sb.toString(),
				where.toString(), "group by a.termid");
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<?> queryOrgFaultReport(int orgId, Integer manufId,
			Collection<Integer> alarmCategoryList, Date startDate, Date endDate) {
		return null;
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<?> queryOrgBusinessVisitReport(int orgId,
			Collection<Integer> businessList, Date startDate, Date endDate) {
		final Collection<Integer> busList = businessList;
		if (startDate.after(endDate))
			throw new AppException("输入参数错误：终止日期在起始日期之后");
		DateTime startDay = new DateTime(startDate);
		DateTime endDay = new DateTime(endDate);
		if (startDay.getFullMonth() != endDay.getFullMonth())
			throw new AppException("输入参数错误：起始日期和终止日期必须在同一个月之内");
		int month = startDay.getFullMonth();
		int prevMonth = startDay.addMonths(-1).getFullMonth();
		int startday = startDay.getFullDay() % 100;
		int endday = endDay.getFullDay() % 100;
		StringBuffer sb = new StringBuffer(
				"select b.ident,${month} month,a.businessid");
		for (int i = startday; i <= endday; i++) {
			sb.append(",count_" + i);
		}
		sb.append(" from report_busvisit_org_${month} a,org b");

		StringBuffer where = new StringBuffer("b.orgid=a.orgid");
		Iterator<Integer> it = businessList.iterator();
		if (it.hasNext()) {
			where.append(" and (");
			boolean first = true;
			while (it.hasNext()) {
				if (!first)
					where.append(" or ");
				where.append("a.businessid=" + it.next());
				first = false;
			}
			where.append(")");
		}

		OrgStatReportList<PageClickStatItem<IdentOrg>> ls = new OrgStatReportList<PageClickStatItem<IdentOrg>>(
				getDao(), new DateTime().addMinutes(1).getTime(),
				new ObjectCreator<PageClickStatItem<IdentOrg>>() {
					public PageClickStatItem<IdentOrg> newInstance() {
						return new PageClickStatItem<IdentOrg>(busList);
					}
				}, orgId, month, startday);
		return ls.queryMultiMonthReportList("report_busvisit_org_${month}",
				new int[] { month, prevMonth }, sb.toString(),
				where.toString(), null).getResultList();
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public QueryResult<?> queryTermBusinessVisitReport(boolean firstQuery,
			int firstResult, int maxResults, int orgId,
			Collection<Integer> businessList, Date startDate, Date endDate) {
		final Collection<Integer> busList = businessList;
		if (startDate.after(endDate))
			throw new AppException("输入参数错误：终止日期在起始日期之后");
		DateTime startDay = new DateTime(startDate);
		DateTime endDay = new DateTime(endDate);
		if (startDay.getFullMonth() != endDay.getFullMonth())
			throw new AppException("输入参数错误：起始日期和终止日期必须在同一个月之内");
		int month = startDay.getFullMonth();
		int prevMonth = startDay.addMonths(-1).getFullMonth();
		int startday = startDay.getFullDay() % 100;
		int endday = endDay.getFullDay() % 100;
		StringBuffer sb = new StringBuffer(
				"select a.termid,${month} month,c.businessid");
		for (int i = startday; i <= endday; i++) {
			sb.append(",click_count_" + i);
		}
		sb.append(" from report_pageclick_org_${month} "
				+ "a,org b,pageelement c,term d");
		StringBuffer where = new StringBuffer("a.pageno=c.pageid");
		where.append(" and d.termid=a.termid");
		Iterator<Integer> it = businessList.iterator();
		if (it.hasNext()) {
			where.append(" and (");
			boolean first = true;
			while (it.hasNext()) {
				if (!first)
					where.append(" or ");
				where.append("c.businessid=" + it.next());
				first = false;
			}
			where.append(")");
		}
		where.append(" and b.orgid=d.orgid");
		TermStatReportList<PageClickStatItem<TermNameItem>> ls = new TermStatReportList<PageClickStatItem<TermNameItem>>(
				getDao(), new DateTime().addMinutes(1).getTime(),
				new ObjectCreator<PageClickStatItem<TermNameItem>>() {
					public PageClickStatItem<TermNameItem> newInstance() {
						return new PageClickStatItem<TermNameItem>(busList);
					}
				}, orgId, month, startday, firstQuery, firstResult, maxResults);
		return ls.queryMultiMonthReportList("report_pageclick_org_${month}",
				new int[] { month, prevMonth }, sb.toString(),
				where.toString(), null);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<?> queryOrgPrintReport(int orgId, Integer manufId,
			Collection<Short> printTypeList, Date startDate, Date endDate) {
		final Collection<Short> printList = printTypeList;
		if (startDate.after(endDate))
			throw new AppException("输入参数错误：终止日期在起始日期之后");
		DateTime startDay = new DateTime(startDate);
		DateTime endDay = new DateTime(endDate);
		if (startDay.getFullMonth() != endDay.getFullMonth())
			throw new AppException("输入参数错误：起始日期和终止日期必须在同一个月之内");
		int month = startDay.getFullMonth();
		int prevMonth = startDay.addMonths(-1).getFullMonth();
		int startday = startDay.getFullDay() % 100;
		int endday = endDay.getFullDay() % 100;
		StringBuffer sb = new StringBuffer(
				"select b.ident,${month} month,a.printtype");
		for (int i = startday; i <= endday; i++) {
			sb.append(",print_count_" + i + ",line_count_" + i);
		}
		sb.append(" from report_print_org_${month} a,org b");
		StringBuffer where = new StringBuffer("b.orgid=a.orgid");
		Iterator<Short> it = printTypeList.iterator();
		if (it.hasNext()) {
			where.append(" and (");
			boolean first = true;
			while (it.hasNext()) {
				if (!first)
					where.append(" or ");
				where.append("a.printtype=" + it.next());
				first = false;
			}
			where.append(")");
		}
		OrgStatReportList<TermPrintStatItem<IdentOrg>> ls = new OrgStatReportList<TermPrintStatItem<IdentOrg>>(
				getDao(), new DateTime().addMinutes(1).getTime(),
				new ObjectCreator<TermPrintStatItem<IdentOrg>>() {
					public TermPrintStatItem<IdentOrg> newInstance() {
						return new TermPrintStatItem<IdentOrg>(printList);
					}
				}, orgId, month, startday);
		return ls.queryMultiMonthReportList("report_print_org_${month}",
				new int[] { month, prevMonth }, sb.toString(),
				where.toString(), null).getResultList();
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public QueryResult<?> queryTermFaultReport(boolean firstQuery,
			int firstResult, int maxResults, int orgId, Integer manufId,
			Collection<Integer> alarmCategoryList, Date startDate, Date endDate) {
		final Collection<Integer> alarmList = alarmCategoryList;
		if (startDate.after(endDate))
			throw new AppException("输入参数错误：终止日期在起始日期之后");
		DateTime startDay = new DateTime(startDate);
		DateTime endDay = new DateTime(endDate);
		if (startDay.getFullMonth() != endDay.getFullMonth())
			throw new AppException("输入参数错误：起始日期和终止日期必须在同一个月之内");
		int month = startDay.getFullMonth();
		int prevMonth = startDay.addMonths(-1).getFullMonth();
		int startday = startDay.getFullDay() % 100;
		int endday = endDay.getFullDay() % 100;
		StringBuffer sb = new StringBuffer(
				"select a.termid,${month} month,a.alarmcategoryid");
		for (int i = startday; i <= endday; i++) {
			sb.append(",fault_count_" + i + ",fault_time_" + i
					+ ",response_time_" + i + ",restore_time_" + i);
		}
		sb.append(" from report_fault_term_${month} a,org b,term d,termtype e");

		StringBuffer where = new StringBuffer("d.termid=a.termid");
		where.append(" and e.typeid=d.typeid");
		if (manufId != null)
			where.append(" and e.manufid=" + manufId);
		if (alarmCategoryList != null && alarmCategoryList.size() > 0) {
			Iterator<Integer> it = alarmCategoryList.iterator();
			if (it.hasNext()) {
				where.append(" and (");
				boolean first = true;
				while (it.hasNext()) {
					if (!first)
						where.append(" or ");
					where.append("a.alarmcategoryid=" + it.next());
					first = false;
				}
				where.append(")");
			}
		}
		where.append(" and b.orgid=d.orgid");
		TermStatReportList<FaultStatItem> ls = new TermStatReportList<FaultStatItem>(
				getDao(), new DateTime().addMinutes(1).getTime(),
				new ObjectCreator<FaultStatItem>() {
					public FaultStatItem newInstance() {
						return new FaultStatItem(alarmList);
					}
				}, orgId, month, startday, firstQuery, firstResult, maxResults,
				manufId);
		return ls.queryMultiMonthReportList("report_fault_term_${month}",
				new int[] { month, prevMonth }, sb.toString(),
				where.toString(), null);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public QueryResult<?> queryTermPrintReport(boolean firstQuery,
			int firstResult, int maxResults, int orgId, Integer manufId,
			Collection<Short> printTypeList, Date startDate, Date endDate) {
		final Collection<Short> printList = printTypeList;
		if (startDate.after(endDate))
			throw new AppException("输入参数错误：终止日期在起始日期之后");
		DateTime startDay = new DateTime(startDate);
		DateTime endDay = new DateTime(endDate);
		if (startDay.getFullMonth() != endDay.getFullMonth())
			throw new AppException("输入参数错误：起始日期和终止日期必须在同一个月之内");
		int month = startDay.getFullMonth();
		int prevMonth = startDay.addMonths(-1).getFullMonth();
		int startday = startDay.getFullDay() % 100;
		int endday = endDay.getFullDay() % 100;
		StringBuffer sb = new StringBuffer(
				"select a.termid,${month} month,a.printtype");
		for (int i = startday; i <= endday; i++) {
			sb.append(",print_count_" + i + ",line_count_" + i);
		}
		sb.append(" from report_print_term_${month} a,org b,term d,termtype e");
		StringBuffer where = new StringBuffer("d.termid=a.termid");
		Iterator<Short> it = printTypeList.iterator();
		if (it.hasNext()) {
			where.append(" and (");
			boolean first = true;
			while (it.hasNext()) {
				if (!first)
					where.append(" or ");
				where.append("a.printtype=" + it.next());
				first = false;
			}
			where.append(")");
		}
		if (manufId != null) {
			where.append(" and e.manufid=" + manufId);
		}
		where.append(" and d.termid=a.termid");
		where.append(" and e.typeid=d.typeid");
		where.append(" and b.orgid=d.orgid");
		TermStatReportList<TermPrintStatItem<TermNameItem>> ls = new TermStatReportList<TermPrintStatItem<TermNameItem>>(
				getDao(), new DateTime().addMinutes(1).getTime(),
				new ObjectCreator<TermPrintStatItem<TermNameItem>>() {
					public TermPrintStatItem<TermNameItem> newInstance() {
						return new TermPrintStatItem<TermNameItem>(printList);
					}
				}, orgId, month, startday, firstQuery, firstResult, maxResults,
				manufId);
		return ls.queryMultiMonthReportList("report_print_term_${month}",
				new int[] { month, prevMonth }, sb.toString(),
				where.toString(), null);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public QueryResult<?> queryTermUseRateReport(boolean firstQuery,
			int firstResult, int maxResults, int orgId, Integer manufId,
			Date startDate, Date endDate) {
		if (startDate.after(endDate))
			throw new AppException("输入参数错误：终止日期在起始日期之后");
		DateTime startDay = new DateTime(startDate);
		DateTime endDay = new DateTime(endDate);
		if (startDay.getFullMonth() != endDay.getFullMonth())
			throw new AppException("输入参数错误：起始日期和终止日期必须在同一个月之内");
		int month = startDay.getFullMonth();
		int prevMonth = startDay.addMonths(-1).getFullMonth();
		int startday = startDay.getFullDay() % 100;
		int endday = endDay.getFullDay() % 100;
		StringBuffer sb = new StringBuffer();
		sb.append("select a.termid,${month} month");
		for (int i = startday; i <= endday; i++) {
			sb.append(",idle_time_" + i + ",busy_time_" + i);
		}

		sb.append(" from report_opened_term_${month} a,"
				+ "org b,term d,termtype e");

		StringBuffer where = new StringBuffer("d.termid=a.termid");
		where.append(" and e.typeid=d.typeid");
		if (manufId != null)
			where.append(" and e.manufid=" + manufId);
		where.append(" and b.orgid=d.orgid");
		TermOpenRateStatReportList ls = new TermOpenRateStatReportList(
				getDao(), new DateTime().addMinutes(1).getTime(),
				new ObjectCreator<OpenRateStatItem>() {
					public OpenRateStatItem newInstance() {
						return new OpenRateStatItem();
					}
				}, orgId, month, startday, firstQuery, firstResult, maxResults,
				startDay, endDay, manufId);
		return ls.queryMultiMonthReportList("report_opened_term_${month}",
				new int[] { month, prevMonth }, sb.toString(),
				where.toString(), null);
	}
}
