/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package kxd.remote.scs.interfaces;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.ejb.Remote;

import kxd.remote.scs.util.QueryResult;

/**
 * 
 * @author 赵明
 */
@Remote
public interface ReportBeanRemote {

	/**
	 * 查询登录用户日报表
	 * 
	 * @param orgId
	 *            机构ID
	 * @param day
	 *            报表日期
	 * @return 报表数据
	 */
	public List<?> queryLoginUsersDayReport(int orgId, Date day);

	/**
	 * 查询访问用户日报表
	 * 
	 * @param orgId
	 *            机构ID
	 * @param day
	 *            报表日期
	 * @return 报表数据
	 */
	public List<?> queryVisitUsersDayReport(int orgId, Date day);

	/**
	 * 查询机构业务量分析报表
	 * 
	 * @param orgId
	 * @param businessList
	 * @param day
	 * @return
	 */
	public List<?> queryOrgTransactionsReport(int orgId,
			Collection<Integer> businessList, Date startDate, Date endDate);

	/**
	 * 查询终端业务量分析报表
	 * 
	 * @param orgId
	 *            机构ID
	 * @param businessList
	 *            包含的业务列表
	 * @param queryCount
	 *            第一次查询
	 * @param maxresults
	 *            返回记录数
	 * @param firstResult
	 *            起始记录
	 * @param day
	 *            查询日期
	 * @return
	 */
	public QueryResult<?> queryTermTransactionsReport(boolean queryCount,
			int firstResult, int maxResults, int orgId,
			Collection<Integer> businessList, Date day);

	/**
	 * 查询机构应收账分析报表
	 * 
	 * @param orgId
	 * @param payWayList
	 * @param day
	 * @return
	 */
	public List<?> queryOrgReceivableReport(int orgId,
			Collection<Short> payWayList, Date startDate, Date endDate);

	/**
	 * 查询终端业务量分析报表
	 * 
	 * @param orgId
	 * @param payWayList
	 * @param day
	 * @return
	 */
	public QueryResult<?> queryTermReceivableReport(boolean queryCount,
			int firstResult, int maxResults, int orgId,
			Collection<Integer> payWayList, Date day);

	/**
	 * 查询机构业务点击率报表
	 * 
	 * @param orgId
	 * @param businessList
	 * @param day
	 * @return
	 */
	public List<?> queryOrgBusinessVisitReport(int orgId,
			Collection<Integer> businessList, Date startDate, Date endDate);

	/**
	 * 查询终端业务点击率报表
	 * 
	 * @param orgId
	 * @param businessList
	 * @param day
	 * @return
	 */
	public QueryResult<?> queryTermBusinessVisitReport(boolean firstQuery,
			int firstResult, int maxResults, int orgId,
			Collection<Integer> businessList, Date startDate, Date endDate);

	/**
	 * 查询终端开机率报表
	 */
	public QueryResult<?> queryTermUseRateReport(boolean firstQuery,
			int firstResult, int maxResults, int orgId, Integer manufId,
			Date startDate, Date endDate);

	/**
	 * 查询机构故障报表
	 * 
	 * @param orgId
	 * @param businessList
	 * @param day
	 * @return
	 */
	public List<?> queryOrgFaultReport(int orgId, Integer manufId,
			Collection<Integer> alarmCategoryList, Date startDate, Date endDate);

	/**
	 * 查询终端故障报表
	 * 
	 * @param orgId
	 * @param businessList
	 * @param day
	 * @return
	 */
	public QueryResult<?> queryTermFaultReport(boolean firstQuery,
			int firstResult, int maxResults, int orgId, Integer manufId,
			Collection<Integer> alarmCategoryList, Date startDate, Date endDate);

	/**
	 * 查询机构打印报表
	 * 
	 * @param orgId
	 * @param businessList
	 * @param day
	 * @return
	 */
	public List<?> queryOrgPrintReport(int orgId, Integer manufId,
			Collection<Short> printTypeList, Date startDate, Date endDate);

	/**
	 * 查询终端打印明细报表
	 * 
	 * @param orgId
	 * @param businessList
	 * @param day
	 * @return
	 */
	public QueryResult<?> queryTermPrintReport(boolean firstQuery,
			int firstResult, int maxResults, int orgId, Integer manufId,
			Collection<Short> printTypeList, Date startDate, Date endDate);

}
