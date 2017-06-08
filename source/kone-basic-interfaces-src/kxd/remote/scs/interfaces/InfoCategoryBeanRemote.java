/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package kxd.remote.scs.interfaces;

import java.util.List;

import javax.ejb.Remote;

import kxd.remote.scs.beans.BaseInfoCategory;
import kxd.remote.scs.util.QueryResult;

/**
 * 
 * @author 赵明
 */
@Remote
public interface InfoCategoryBeanRemote {

	/**
	 * 查询信息分类
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
	public QueryResult<BaseInfoCategory> queryInfoCategory(boolean queryCount,
			long loginUserId, Integer filter, String filterContent,
			int startRecord, int maxResult);

	/**
	 * 添加信息分类
	 */
	public void add(long loginUserId, BaseInfoCategory infoCategory);

	/**
	 * 编辑信息分类
	 */
	public void edit(long loginUserId, BaseInfoCategory infoCategory);

	/**
	 * 删除信息分类
	 */
	public void delete(long loginUserId, short[] infoCategory);

	/**
	 * 查找信息分类
	 * 
	 * @param manufId
	 */
	public BaseInfoCategory find(short infoCategory);

	/**
	 * 获取信息分类列表
	 * 
	 * @param loginUserId
	 * @param keyword
	 */
	public List<BaseInfoCategory> getInfoCategoryList(long loginUserId,
			String keyword);
}
