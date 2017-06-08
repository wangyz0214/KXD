/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package kxd.remote.scs.interfaces;

import java.util.List;

import javax.ejb.Remote;

import kxd.remote.scs.beans.BaseAdCategory;
import kxd.remote.scs.util.QueryResult;

/**
 * 
 * @author 赵明
 */
@Remote
public interface AdCategoryBeanRemote {

	/**
	 * 查询广告分类
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
	public QueryResult<BaseAdCategory> queryAdCategory(boolean queryCount,
			long loginUserId, Integer filter, String filterContent,
			int startRecord, int maxResult);

	/**
	 * 添加广告分类
	 */
	public void add(long loginUserId, BaseAdCategory AdCategory);

	/**
	 * 编辑广告分类
	 */
	public void edit(long loginUserId, BaseAdCategory AdCategory);

	/**
	 * 删除广告分类
	 */
	public void delete(long loginUserId, short[] AdCategory);

	/**
	 * 查找广告分类
	 * 
	 * @param manufId
	 */
	public BaseAdCategory find(short AdCategory);

	/**
	 * 获取广告分类列表
	 * 
	 * @param loginUserId
	 * @param keyword
	 */
	public List<BaseAdCategory> getAdCategoryList(long loginUserId,
			String keyword);
}
