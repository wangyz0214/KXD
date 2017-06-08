/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package kxd.remote.scs.interfaces;

import java.util.List;

import javax.ejb.Remote;

import kxd.remote.scs.beans.BaseBusinessCategory;
import kxd.remote.scs.util.QueryResult;

/**
 * 
 * @author 赵明
 */
@Remote
public interface BusinessCategoryBeanRemote {

	/**
	 * 查询业务分类
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
	public QueryResult<BaseBusinessCategory> queryBusinessCategory(
			boolean queryCount, long loginUserId, Integer filter,
			String filterContent, int startRecord, int maxResult);

	/**
	 * 添加业务分类
	 */
	public int add(long loginUserId, BaseBusinessCategory businessCategory);

	/**
	 * 编辑业务分类
	 */
	public void edit(long loginUserId, BaseBusinessCategory businessCategory);

	/**
	 * 删除业务分类
	 */
	public void delete(long loginUserId, Integer[] businessCategory);

	/**
	 * 查找业务分类
	 * 
	 * @param manufId
	 */
	public BaseBusinessCategory find(int businessCategory);

	/**
	 * 获取业务分类列表
	 * 
	 * @param loginUserId
	 * @param keyword
	 */
	public List<BaseBusinessCategory> getBusinessCategoryList(long loginUserId,
			String keyword);
}
