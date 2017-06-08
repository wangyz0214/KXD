/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package kxd.remote.scs.interfaces;

import java.util.List;

import javax.ejb.Remote;

import kxd.remote.scs.beans.BaseTradeCode;
import kxd.remote.scs.beans.appdeploy.EditedTradeCode;
import kxd.remote.scs.util.QueryResult;

/**
 * 
 * @author 赵明
 */
@Remote
public interface TradeCodeBeanRemote {

	/**
	 * 查询交易代码
	 * 
	 * @param queryCount
	 *            是否返回记录总条数
	 * @param loginUserId
	 *            登录用户
	 * @param filter
	 *            过滤操作
	 * @param filterContent
	 *            过滤内容
	 * @param startRecord
	 *            起始记录
	 * @param maxResult
	 *            最大的返回记录条数
	 * @return
	 */
	public QueryResult<EditedTradeCode> queryTradeCode(boolean queryCount,
			long loginUserId, Integer filter, String filterContent,
			int startRecord, int maxResult);

	/**
	 * 添加交易代码
	 */
	public int add(long loginUserId, EditedTradeCode tradeCode);

	/**
	 * 编辑交易代码
	 */
	public void edit(long loginUserId, EditedTradeCode tradeCode);

	/**
	 * 删除交易代码
	 */
	public void delete(long loginUserId, Integer[] tradeCode);

	/**
	 * 查找交易代码
	 * 
	 */
	public EditedTradeCode find(int tradeCode);

	/**
	 * 获取交易代码列表
	 * 
	 * @param loginUserId
	 * @param keyword
	 */
	public List<BaseTradeCode> getTradeCodeList(long loginUserId, String keyword);
}
