/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package kxd.remote.scs.interfaces;

import java.util.List;

import javax.ejb.Remote;

import kxd.remote.scs.beans.BaseAlarmCategory;
import kxd.remote.scs.util.QueryResult;

/**
 * 
 * @author 赵明
 */
@Remote
public interface AlarmCategoryBeanRemote {

	/**
	 * 查询告警代码分类
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
	public QueryResult<BaseAlarmCategory> queryAlarmCategory(
			boolean queryCount, long loginUserId, Integer filter,
			String filterContent, int startRecord, int maxResult);

	/**
	 * 添加告警代码分类
	 */
	public void add(long loginUserId, BaseAlarmCategory alarmCategory);

	/**
	 * 编辑告警代码分类
	 */
	public void edit(long loginUserId, BaseAlarmCategory alarmCategory);

	/**
	 * 删除告警代码分类
	 */
	public void delete(long loginUserId, Integer[] alarmCategory);

	/**
	 * 查找告警代码分类
	 * 
	 * @param manufId
	 */
	public BaseAlarmCategory find(int alarmCategory);

	/**
	 * 获取告警代码分类列表
	 * 
	 * @param loginUserId
	 * @param keyword
	 */
	public List<BaseAlarmCategory> getAlarmCategoryList(long loginUserId,
			String keyword);
}
