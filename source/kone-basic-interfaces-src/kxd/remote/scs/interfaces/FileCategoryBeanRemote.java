/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package kxd.remote.scs.interfaces;

import java.util.List;

import javax.ejb.Remote;

import kxd.remote.scs.beans.BaseFileCategory;
import kxd.remote.scs.util.QueryResult;

/**
 * 
 * @author 赵明
 */
@Remote
public interface FileCategoryBeanRemote {

	/**
	 * 查询文件分类
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
	public QueryResult<BaseFileCategory> queryFileCategory(boolean queryCount,
			long loginUserId, Integer filter, String filterContent,
			int startRecord, int maxResult);

	/**
	 * 添加文件分类
	 */
	public int add(long loginUserId, BaseFileCategory fileCategory);

	/**
	 * 编辑文件分类
	 */
	public void edit(long loginUserId, BaseFileCategory fileCategory);

	/**
	 * 删除文件分类
	 */
	public void delete(long loginUserId, Short[] fileCategory);

	/**
	 * 查找文件分类
	 * 
	 * @param manufId
	 */
	public BaseFileCategory find(short fileCategory);

	/**
	 * 获取文件分类列表
	 * 
	 * @param loginUserId
	 * @param keyword
	 */
	public List<BaseFileCategory> getFileCategoryList(long loginUserId,
			String keyword);
}
