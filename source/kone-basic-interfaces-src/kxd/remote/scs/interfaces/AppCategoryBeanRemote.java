/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package kxd.remote.scs.interfaces;

import java.util.List;

import javax.ejb.Remote;

import kxd.remote.scs.beans.BaseAppCategory;
import kxd.remote.scs.beans.appdeploy.EditedAppCategory;
import kxd.remote.scs.util.QueryResult;

/**
 * 
 * @author 赵明
 */
@Remote
public interface AppCategoryBeanRemote {

	/**
	 * 查询应用分类
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
	public QueryResult<EditedAppCategory> queryAppCategory(boolean queryCount,
			long loginUserId, Integer filter, String filterContent,
			int startRecord, int maxResult);

	/**
	 * 添加应用分类
	 */
	public int add(long loginUserId, EditedAppCategory appCategory);

	/**
	 * 编辑应用分类
	 */
	public void edit(long loginUserId, EditedAppCategory appCategory);

	/**
	 * 删除应用分类
	 */
	public void delete(long loginUserId, Integer[] appCategory);

	/**
	 * 查找应用分类
	 * 
	 * @param manufId
	 */
	public EditedAppCategory find(int appCategory);

	/**
	 * 获取应用分类列表
	 * 
	 * @param loginUserId
	 * @param keyword
	 */
	public List<BaseAppCategory> getAppCategoryList(long loginUserId,
			String keyword);
}
