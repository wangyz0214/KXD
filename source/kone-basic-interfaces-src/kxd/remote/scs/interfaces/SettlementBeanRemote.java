package kxd.remote.scs.interfaces;

import java.util.Date;
import java.util.List;

import javax.ejb.Remote;

import kxd.remote.scs.util.QueryResult;
import kxd.remote.scs.util.emun.PayWayType;

/**
 * 
 * @author 赵明
 */
@Remote
public interface SettlementBeanRemote {
	public QueryResult<?> queryRefundDetail(long loginUserId,
			boolean firstQuery, int firstResult, int maxResults, Integer orgId,
			String termCode, Date startTime, Date endTime, String userNo,
			String termGlide, String tradeglide, PayWayType payType);

	/**
	 * 查询应用
	 * 
	 * @param queryCount
	 *            是否返回记录总条数
	 * @param loginUserId
	 *            登录用户
	 * @param orgId
	 *            机构ID
	 * @param termCode
	 *            终端编码，为NULL，则查全部
	 * @param startTime
	 *            起始时间
	 * @param endTime
	 *            终止时间
	 * @param userNo
	 *            用户号码，为NULL，则查全部
	 * @param termGlide
	 *            终端流水号，为NULL，则查全部
	 * @param tradeglide
	 * @param result
	 *            0 - 全部交易；1 - 失败交易
	 * @param firstResult
	 *            起始记录
	 * @param maxResults
	 *            最大返回记录
	 * @param optionStatus
	 *            是否已退款
	 * @param payType
	 * @param channel
	 * @param tradephase交易状态
	 * @param fisrt
	 * @return
	 */
	public QueryResult<?> queryTradeDetail(long loginUserId,
			boolean firstQuery, int firstResult, int maxResults, Integer orgId,
			String termCode, Date startTime, Date endTime, String userNo,
			String termGlide, String tradeglide, Integer payStatus,
			Integer refundStatus, Integer tradeStatus, Integer recStatus,
			List<Integer> businessIdList);

	/**
	 * 查询某条交易的柜员处理记录
	 * 
	 * @param loginUserId
	 * @param termGlide
	 * @return
	 */
	public List<?> queryTradeProcList(long loginUserId, String termGlide);

	/**
	 * 退款
	 * 
	 * @param loginUserId
	 *            登录用户
	 * @param hasReturnMoneyToday
	 *            是否有当日退款的权限
	 * @param hasReturnMoneyNextday
	 *            是否有隔日退款的权限
	 * @param termGlide
	 *            终端流水
	 * @param counterMoney
	 *            退款金额
	 */
	public Object returnMoney(long loginUserId, boolean hasReturnMoneyToday,
			boolean hasReturnMoneyNextday, boolean hasReturnMoneyNextMonth,
			boolean hasSuccessReurn, boolean hasUnionPayReturn,
			String termGlide, int counterMoney, String reason);

	/**
	 * 取消上次的操作
	 * 
	 * @param loginUserId
	 *            登录用户
	 * @param hasCancelReturnMoneyToday
	 *            是否有当日撤消退款的权限
	 * @param hasCancelReturnMoneyNextday
	 *            是否有隔日撤消退款的权限
	 * @param termGlide
	 *            终端流水
	 */
	public Object cancelOption(long loginUserId,
			boolean hasCancelReturnMoneyToday,
			boolean hasCancelReturnMoneyNextday,
			boolean hasCancelReturnMoneyNextMonth, String termGlide,
			String reason);

}
