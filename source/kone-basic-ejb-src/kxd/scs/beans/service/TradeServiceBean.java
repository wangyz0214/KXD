package kxd.scs.beans.service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import kxd.engine.cache.beans.sts.CachedTradeCode;
import kxd.engine.dao.BaseBean;
import kxd.engine.helper.CacheHelper;
import kxd.engine.helper.DaoHelper;
import kxd.engine.helper.TermHelper;
import kxd.remote.scs.interfaces.service.TradeServiceBeanRemote;
import kxd.remote.scs.transaction.Result;
import kxd.remote.scs.transaction.TradeCode;
import kxd.remote.scs.util.AppException;
import kxd.remote.scs.util.emun.CounterOption;
import kxd.remote.scs.util.emun.PayStatus;
import kxd.remote.scs.util.emun.TradePhase;
import kxd.remote.scs.util.emun.TradeResult;
import kxd.remote.scs.util.emun.TradeStatus;
import kxd.util.DataUnit;
import kxd.util.DateTime;
import kxd.util.StringUnit;

import org.apache.log4j.Logger;

@Stateless(name = "kxd-ejb-tradeServiceBean", mappedName = "kxd-ejb-tradeServiceBean")
public class TradeServiceBean extends BaseBean implements
		TradeServiceBeanRemote {

	static Logger logger = Logger.getLogger(TradeServiceBean.class);

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	void inInsertTradeDetail(boolean ingoreRepeatError, TradeCode tradeCode,
			int adjustAmount, CounterOption counterOption, Result result,
			Date uploadTime, Integer userOrgId, String redoParams,
			String clientip, String serverip) {
		getDao().execProcedure(
				DaoHelper.getInsertTradeDetailSql(ingoreRepeatError, tradeCode,
						adjustAmount, counterOption, result, uploadTime,
						userOrgId, redoParams, clientip, serverip));
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void insertTradeDetail(TradeCode tradeCode, int adjustAmount,
			CounterOption counterOption, Result result, String clientip,
			String serverip, boolean ingoreRepeat) {
		Integer userOrgId = null;
		if (tradeCode.getUserCityCode() != null
				&& !tradeCode.getUserCityCode().trim().isEmpty()) {
			userOrgId = TermHelper.getUserNoOrgId(tradeCode.getUserCityCode());
		}
		inInsertTradeDetail(ingoreRepeat, tradeCode, adjustAmount,
				counterOption, result, tradeCode.getTradeTime(), userOrgId,
				tradeCode.getRedoParams(), clientip, serverip);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void insertTradeLog(boolean stated, boolean logged, int termId,
			int orgId,int userOrgId, int tradeCodeId, Date tradeTime, boolean success,
			String message, String userno, String userip, String serverip,
			Result result) {
		if (!stated && !logged)
			return;
		getDao().execProcedure(
				"kxd_self_trade.insert_trade_log("
						+ "?1,?2,?3,?4,?5,?6,?7,?8,?9,"
						+ "?10,?11,?12,?13,?14,?15,?16,?17)",
				new Object[] {
						stated ? 1 : 0,
						termId,
						orgId,
						userOrgId,
						tradeCodeId,
						tradeTime,
						success ? 1 : 0,
						DataUnit.checkBytesLength(message, 255),
						userno,
						userip,
						serverip,
						DateTime.milliSecondsBetween(
								result.getTradeStartTime(),
								result.getTradeEndTime()),
						result.getExtData0(), result.getExtData1(),
						result.getExtData2(), result.getExtData3(),
						result.getExtData4() });
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void updateTradeDetail(BigDecimal termGlide, int orgId, Result result) {
		getDao().execProcedure(
				DaoHelper.getUpdateTradeDetailSql(termGlide, orgId, result));
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void execSql(String sql, Object[] params) {
		getDao().execute(sql, params);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Collection<?> querySql(String sql, Object[] params) {
		return getDao().query(sql, params);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public boolean getUserNoQueryPrintStatus(String userno, int month,
			int printType, int maxPrintTimes) {

		if (!getDao().query(
				"select userno from disableprintuser where userno='" + userno
						+ "'").isEmpty())
			throw new AppException("该号码被禁止查询和打印");

		String tableName = "printlog_" + month;
		Iterator<?> it = getDao().query(
				"select times from " + tableName + " where userno='" + userno
						+ "' and printtype=" + printType).iterator();
		if (it.hasNext()) {
			int times = Integer.valueOf(it.next().toString());
			return times < maxPrintTimes;
		}
		return true;
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void updateUserPrintTimes(int termid, int orgId, String userno,
			int month, int printType, int times, int lines) {
		getDao().execProcedure(
				"kxd_self_trade.update_printtimes_report(?1,?2,?3,?4,?5,?6,?7)",
				new Object[] { termid, orgId, userno, month, printType, times,
						lines });
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void updateLoginUserCount(int termId, int orgId, String userno) {
		getDao().execProcedure(
				"kxd_self_trade.update_usercount_report(?1,?2,?3)",
				new Object[] { 1, userno, orgId });
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void updateVisitUserCount(int termId, int orgId, String userno) {
		getDao().execProcedure(
				"kxd_self_trade.update_usercount_report(?1,?2,?3)",
				new Object[] { 0, userno, orgId });
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void updateBusinessVisitCount(int termId, int orgId, int businessId,
			String userno) {
		getDao().execProcedure(
				"kxd_self_trade.update_businessvisit_report(?1,?2,?3,?4)",
				new Object[] { termId, orgId, userno, businessId });
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void submitTradeDetails(List<?> detailList, String clientip,
			String serverip) {

		Iterator<?> it = detailList.iterator();
		TradeCode tc = new TradeCode();
		Result result = new Result(TradeResult.TIMEOUT);
		logger.info("submit trade detail list begin...");
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
						userOrgId = TermHelper.getUserNoOrgId(ps1[1].trim());
					}
				}
			}
			inInsertTradeDetail(true, tc, 0, CounterOption.NONE, result,
					new Date(), userOrgId, redoParams, clientip, serverip);
			logger.info("submit trade item[termid=" + tc.getTermId()
					+ ";tradecodeid=" + tc.getTradeCodeId() + ";termglide="
					+ tc.getTermGlide() + "] success!");
		}
		logger.info("submit trade detail list end.");
	}
}
