package kxd.remote.scs.interfaces.service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.ejb.Remote;

import kxd.remote.scs.transaction.Result;
import kxd.remote.scs.transaction.TradeCode;
import kxd.remote.scs.util.AppException;
import kxd.remote.scs.util.emun.CounterOption;

/**
 * 
 * @author 赵明
 */
@Remote
public interface TradeServiceBeanRemote {

	/**
	 * 插入普通交易日志
	 * 
	 * @param stated
	 *            是否更新统计
	 * @param logged
	 *            是否记录日志
	 * @param termId
	 *            终端ID
	 * @param orgId
	 *            机构ID，用于更新机构报表
	 * @param tradeCodeId
	 *            交易代码ID
	 * @param tradeTime
	 *            交易日志
	 * @param success
	 *            是否成功
	 * @param message
	 *            交易结果
	 * 
	 * @param userno
	 *            用户号码
	 * @param userip
	 *            用户ip
	 * @param result
	 *            扩展交易参数
	 */
	public void insertTradeLog(boolean stated, boolean logged, int termId,
			int orgId, int userOrgId, int tradeCodeId, Date tradeTime,
			boolean success, String message, String userno, String userip,
			String serverip, Result result);

	/**
	 * 插入终端交易明细
	 * 
	 * @param tradeCode
	 *            终端流水
	 * @param adjustAmount
	 *            调整金额
	 * @param counterOption
	 *            柜员操作 0 - 未操作；1 - 退款；2 - 补充值
	 * @param result
	 *            交易结果
	 * @param ingoreRepeat
	 *            忽略重复，如果为true，则重复不抛错误
	 */
	public void insertTradeDetail(TradeCode tradeCode, int adjustAmount,
			CounterOption counterOption, Result result, String clientip,
			String serverip, boolean ingoreRepeat);

	/**
	 * 更新交易明细
	 * 
	 * @param termGlide
	 *            交易代码ID
	 * @param orgId
	 *            机构ID，用于更新机构报表
	 * @param result
	 *            交易结果码 0 - 交易成功；1 - 交易未发起至目的系统；2 - 交易超时;
	 */
	public void updateTradeDetail(BigDecimal termGlide, int orgId, Result result);

	/**
	 * 执行一条SQL的update语句。特殊情况下使用，与Cache相关的数据表禁止使用
	 * 
	 * @param sql
	 * @param params
	 */
	public void execSql(String sql, Object[] params);

	/**
	 * 执行一条SQL的查询语句。特殊情况下使用，与Cache相关的数据表禁止使用
	 * 
	 * @param sql
	 * @param params
	 */
	public Collection<?> querySql(String sql, Object[] params);

	/**
	 * 获取当前用户号码能够查询和打印的状态
	 * 
	 * @param userno
	 *            用户号码
	 * @param month
	 *            月份
	 * @param printType
	 *            打印类型
	 * @param maxPrintTimes
	 *            最大的次数
	 * @return 用户号码能否打印
	 * @throws AppException
	 *             如果不允许查询，则抛出此异常
	 */
	public boolean getUserNoQueryPrintStatus(String userno, int month,
			int printType, int maxPrintTimes);

	/**
	 * 更新用户的打印次数
	 * 
	 * @param termid
	 *            打印终端
	 * @param orgId
	 *            机构ID，用于更新机构报表
	 * @param userno
	 *            用户号码
	 * @param printType
	 *            打印类型
	 * @param month
	 *            打印月份
	 * @param printType
	 *            打印类型
	 * @param times
	 *            打印次数
	 * @param lines
	 *            打印行数
	 */
	public void updateUserPrintTimes(int termid, int orgId, String userno,
			int month, int printType, int times, int lines);

	/**
	 * 当用户在终端成功登录后，调用本函数更新登录用户数
	 * 
	 * @param termid
	 *            终端ID
	 * @param businessId
	 *            业务ID
	 * @param orgId
	 *            机构ID，用于更新机构报表
	 * @param userno
	 *            用户号码
	 */
	public void updateLoginUserCount(int termid, int orgId, String userno);

	/**
	 * 只要有用户访问（包括不需要认证的用户），调用本函数更新使用用户数
	 * 
	 * @param termid
	 *            终端ID
	 * @param businessId
	 *            业务ID
	 * @param orgId
	 *            机构ID，用于更新机构报表
	 * @param userno
	 *            用户号码
	 */
	public void updateVisitUserCount(int termid, int orgId, String userno);

	/**
	 * 更新业务访问次数
	 * 
	 * @param termid
	 *            终端ID
	 * @param orgId
	 *            机构ID，用于更新机构报表
	 * @param pageNo
	 *            页面编码
	 */
	public void updateBusinessVisitCount(int termid, int orgId, int businessId,
			String userno);

	/**
	 * 提交交易明细
	 * 
	 * @param detailList
	 */
	public void submitTradeDetails(List<?> detailList, String clientip,
			String serverip);

}
