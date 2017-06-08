package kxd.scs.beans;

import java.io.IOException;
import java.sql.Types;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import kxd.engine.dao.BaseBean;
import kxd.engine.dao.CallableOutParameter;
import kxd.engine.helper.AdminHelper;
import kxd.engine.helper.TermHelper;
import kxd.engine.scs.monitor.RefundEventData;
import kxd.engine.scs.report.TermDetailReportList;
import kxd.engine.scs.report.TermNameItem;
import kxd.engine.scs.report.beans.RefundDetailItem;
import kxd.engine.scs.report.beans.TradeDetailItem;
import kxd.remote.scs.interfaces.SettlementBeanRemote;
import kxd.remote.scs.util.AppException;
import kxd.remote.scs.util.QueryResult;
import kxd.remote.scs.util.emun.PayStatus;
import kxd.remote.scs.util.emun.PayWayType;
import kxd.remote.scs.util.emun.TradeStatus;
import kxd.util.DateTime;
import kxd.util.ObjectCreator;
import kxd.util.StringUnit;

import org.apache.log4j.Logger;

@Stateless(name = "kxd-ejb-settlementServiceBean", mappedName = "kxd-ejb-settlementServiceBean")
public class SettlementServiceBean extends BaseBean implements
		SettlementBeanRemote {
	static Logger logger = Logger.getLogger(SettlementServiceBean.class);

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Object cancelOption(long loginUserId,
			boolean hasCancelReturnMoneyToday,
			boolean hasCancelReturnMoneyNextday,
			boolean hasCancelReturnMoneyNextMonth, String termGlide,
			String reason) {
		CallableOutParameter r = new CallableOutParameter(Types.VARCHAR, null,
				null);
		getDao().execProcedure(
				"kxd_self_trade.trade_cancel_option(?1,?2,?3,?4,?5,?6,?7)",
				loginUserId, hasCancelReturnMoneyToday ? 1 : 0,
				hasCancelReturnMoneyNextday ? 1 : 0,
				hasCancelReturnMoneyNextMonth ? 1 : 0, termGlide, reason, r);
		String ret = (String) r.getValue();
		String[] rr = StringUnit.split(ret, ",");
		RefundEventData data = new RefundEventData();
		data.setAmount(Integer.valueOf(rr[4]));
		data.setOrgId(Integer.valueOf(rr[5]));
		data.setTradeCodeId(Integer.valueOf(rr[0]));
		try {
			AdminHelper.updateCancelRefundStatus(data);
		} catch (Throwable e) {
		}
		return r.getValue();
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public QueryResult<?> queryTradeDetail(long loginUserId,
			boolean firstQuery, int firstResult, int maxResults, Integer orgId,
			String termCode, Date startTime, Date endTime, String userNo,
			String termGlide, String tradeglide, Integer payStatus,
			Integer refundStatus, Integer tradeStatus, Integer recStatus,
			List<Integer> businessIdList) {
		DateTime st = new DateTime(startTime);
		DateTime et = new DateTime(endTime);
		int month = st.getFullMonth();
		if (month != et.getFullMonth())
			throw new AppException("起始时间和终止时间必须在同一个月内");
		StringBuffer where = new StringBuffer();
		where.append("a.tradetime>=?1 and a.tradetime<=?2");
		if (termGlide != null && !termGlide.isEmpty()) {
			where.append(" and a.termglide='" + termGlide + "'");
		} else if (termCode != null && !termCode.trim().isEmpty()) {
			where.append(" and c.termcode='" + termCode + "'");
		}
		if (tradeglide != null && !tradeglide.isEmpty())
			where.append(" and a.tradeglide like '" + tradeglide + "%'");
		if (userNo != null && !userNo.isEmpty())
			where.append(" and a.userno like '" + userNo + "%'");
		where.append(" and a.termid=c.termid and b.orgid=c.orgid");
		where.append(" and d.tradecodeid=a.tradecodeid and e.businessid=d.businessid ");
		if (refundStatus != null)
			where.append(" and a.refundstatus=" + refundStatus);
		if (tradeStatus != null) {
			switch (tradeStatus) {
			case 0:
				where.append(" and a.tradestatus<10 ");
				break;
			case 1:
				where.append(" and a.tradestatus>=10 and a.tradestatus<20 ");
				break;
			case 2:
				where.append(" and a.tradestatus="
						+ TradeStatus.TRADE_SUCCESS_REDO.getValue() + " ");
				break;
			case 3:
				where.append(" and a.tradestatus="
						+ TradeStatus.TRADE_SUCCESS_RECONCIL.getValue() + " ");
				break;
			case 4:
				where.append(" and a.tradestatus=20 ");
				break;
			case 5:
				where.append(" and a.tradestatus>=30 and a.tradestatus<40 ");
				break;
			}
		}
		if (payStatus != null) {
			switch (payStatus) {
			case 0:
				where.append(" and a.paystatus<10");
				break;
			case 1:
				where.append(" and a.paystatus>=10 and a.paystatus<20 ");
				break;
			case 2:
				where.append(" and a.tradestatus="
						+ PayStatus.PAY_SUCCESS_RECONCIL.getValue() + " ");
				break;
			case 3:
				where.append(" and a.paystatus=20 ");
				break;
			case 4:
				where.append(" and a.paystatus>=30 and a.paystatus<40 ");
				break;
			}
		}
		if (recStatus != null)
			where.append(" and a.rec_status =" + recStatus);

		Iterator<Integer> iter = businessIdList.iterator();
		if (iter.hasNext()) {
			where.append(" and (");
			boolean first = true;
			while (iter.hasNext()) {
				if (!first)
					where.append(" or ");
				where.append("d.businessid=" + iter.next());
				first = false;
			}
			where.append(")");
		}
		TermDetailReportList<TradeDetailItem<TermNameItem>> ls = new TermDetailReportList<TradeDetailItem<TermNameItem>>(
				getDao(), new ObjectCreator<TradeDetailItem<TermNameItem>>() {
					public TradeDetailItem<TermNameItem> newInstance() {
						return new TradeDetailItem<TermNameItem>();
					}
				}, orgId, firstQuery, firstResult, maxResults);
		return ls
				.queryReportList(
						"trade_" + month,
						"select c.termcode,b.orgid,termglide,tradeglide,userno,a.tradecodeid,"
								+ "paystatus,tradestatus,refundstatus,rec_status,tradetime,"
								+ "termmoney,resultinfo,optiontime,optioncode from trade_"
								+ month
								+ " a,org b,term c,tradecode d,business e ",
						where.toString(), "order by a.tradetime desc", st, et);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Object returnMoney(long loginUserId, boolean hasReturnMoneyToday,
			boolean hasReturnMoneyNextday, boolean hasReturnMoneyNextMonth,
			boolean hasSuccessReurn, boolean hasUnionPayReturn,
			String termGlide, int counterMoney, String reason) {
		try {
			if (TermHelper.isReTrading(termGlide))
				throw new AppException("该交易正在补交，现在不能操作!");
		} catch (InterruptedException e) {
		} catch (IOException e) {
		}
		CallableOutParameter r = new CallableOutParameter(Types.VARCHAR, null,
				null);
		getDao().execProcedure(
				"kxd_self_trade.trade_return_money(?1,?2,?3,?4,?5,?6,?7,?8,?9,?10)",
				loginUserId, hasReturnMoneyToday ? 1 : 0,
				hasReturnMoneyNextday ? 1 : 0, hasReturnMoneyNextMonth ? 1 : 0,
				hasSuccessReurn ? 1 : 0, hasUnionPayReturn ? 1 : 0, termGlide,
				counterMoney, reason, r);
		String ret = (String) r.getValue();
		String[] rr = StringUnit.split(ret, ",");
		if (Integer.valueOf(rr[0]) == 2) {
			RefundEventData data = new RefundEventData();
			data.setAmount(Integer.valueOf(rr[4]));
			data.setOrgId(Integer.valueOf(rr[5]));
			data.setTradeCodeId(Integer.valueOf(rr[1]));
			try {
				AdminHelper.updateRefundStatus(data);
			} catch (Throwable e) {
			}
		}
		return ret;
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<?> queryTradeProcList(long loginUserId, String termGlide) {
		return getDao()
				.query("select usercode,to_char(adjusttime,'yyyy-mm-dd hh24:mi:ss'),adjustmoney,adjustreason from tradeadjust_"
						+ termGlide.substring(0, 6)
						+ " a,systemuser b where termglide='"
						+ termGlide
						+ "' and a.userid=b.userid");
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public QueryResult<?> queryRefundDetail(long loginUserId,
			boolean firstQuery, int firstResult, int maxResults, Integer orgId,
			String termCode, Date startTime, Date endTime, String userNo,
			String termGlide, String tradeglide, PayWayType payType) {
		DateTime st = new DateTime(startTime);
		DateTime et = new DateTime(endTime);
		int month = st.getFullMonth();
		if (month != et.getFullMonth())
			throw new AppException("起始时间和终止时间必须在同一个月内");
		StringBuffer where = new StringBuffer();
		where.append("a.create_time>=?1 and a.create_time<=?2");
		if (termGlide != null && !termGlide.isEmpty()) {
			where.append(" and d.termglide='" + termGlide + "'");
		} else if (termCode != null && !termCode.trim().isEmpty()) {
			where.append(" and d.termcode='" + termCode + "'");
		}
		if (tradeglide != null && !tradeglide.isEmpty())
			where.append(" and d.tradeglide like '" + tradeglide + "%'");
		if (userNo != null && !userNo.isEmpty())
			where.append(" and d.userno like '" + userNo + "%'");
		where.append(" and a.termglide=d.termglide and d.termid=c.termid and b.orgid=c.orgid");
		where.append(" and d.tradecodeid=e.tradecodeid and e.payway=f.payway ");
		if (payType != null)
			where.append(" and f.type=" + payType.getValue());
		TermDetailReportList<RefundDetailItem<TermNameItem>> ls = new TermDetailReportList<RefundDetailItem<TermNameItem>>(
				getDao(), new ObjectCreator<RefundDetailItem<TermNameItem>>() {
					public RefundDetailItem<TermNameItem> newInstance() {
						return new RefundDetailItem<TermNameItem>();
					}
				}, orgId, firstQuery, firstResult, maxResults);
		return ls
				.queryReportList(
						"refund_" + month,
						"select c.termcode,b.orgid,a.termglide,tradeglide,userno,d.tradecodeid,"
								+ "a.create_time,termmoney,a.refund_result,a.iscanceled from refund_"
								+ month + " a,org b,term c,trade_" + month
								+ " d,tradecode e,payway f ", where.toString(),
						"order by a.create_time desc", st, et);
	} 

}
