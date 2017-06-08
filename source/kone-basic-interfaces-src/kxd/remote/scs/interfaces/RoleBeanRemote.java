/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package kxd.remote.scs.interfaces;

import java.util.Collection;

import javax.ejb.Remote;

import kxd.remote.scs.beans.BaseRole;
import kxd.remote.scs.util.QueryResult;

/**
 * 
 * @author 赵明
 */
@Remote
public interface RoleBeanRemote {

	/**
	 * 查询角色
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
	public QueryResult<BaseRole> queryRole(boolean queryCount,
			long loginUserId, Integer filter, String filterContent,
			int startRecord, int maxResult);

	/**
	 * 添加用户
	 */
	public int add(long loginUserId, BaseRole role);

	/**
	 * 编辑用户
	 */
	public void edit(long loginUserId, BaseRole role);

	/**
	 * 删除用户
	 * 
	 * @param userId
	 */
	public void delete(long loginUserId, int[] roleId);

	/**
	 * 获得角色的功能项
	 * 
	 * @return
	 */
	public Collection<Integer> getFunction(int roleId);

	/**
	 * 查找角色
	 * 
	 * @param roleId
	 */
	public BaseRole find(int roleId);

	/**
	 * 设置角色的权限
	 * 
	 * @param roleId
	 * @param addList
	 * @param removeList
	 */
	public void setFunction(long loginUserId, int roleId,
			Collection<Integer> addList, Collection<Integer> removeList);
}
