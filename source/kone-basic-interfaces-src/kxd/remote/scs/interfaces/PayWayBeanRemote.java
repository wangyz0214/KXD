/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package kxd.remote.scs.interfaces;

import java.util.List;

import javax.ejb.Remote;

import kxd.remote.scs.beans.BasePayWay;
import kxd.remote.scs.util.QueryResult;

/**
 * 
 * @author 赵明
 */
@Remote
public interface PayWayBeanRemote {

	/**
	 * 查询收费渠道
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
	public QueryResult<BasePayWay> queryPayWay(boolean queryCount,
			long loginUserId, Integer filter, String filterContent,
			int startRecord, int maxResult);

	/**
	 * 添加收费渠道
	 */
	public short add(long loginUserId, BasePayWay payWay);

	/**
	 * 编辑收费渠道
	 */
	public void edit(long loginUserId, BasePayWay payWay);

	/**
	 * 删除收费渠道
	 */
	public void delete(long loginUserId, Short[] payWay);

	/**
	 * 查找收费渠道
	 * 
	 * @param manufId
	 */
	public BasePayWay find(int payWay);

	/**
	 * 获取收费渠道列表
	 * 
	 * @param loginUserId
	 * @param keyword
	 */
	public List<BasePayWay> getPayWayList(long loginUserId, String keyword);
}
