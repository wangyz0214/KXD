/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package kxd.remote.scs.interfaces;

import java.util.Collection;

import javax.ejb.Remote;

import kxd.remote.scs.beans.BaseRole;
import kxd.remote.scs.beans.right.EditedFunction;
import kxd.remote.scs.beans.right.EditedUser;
import kxd.remote.scs.beans.right.LoginUser;
import kxd.remote.scs.beans.right.QueryedUser;
import kxd.remote.scs.util.QueryResult;

/**
 * 
 * @author 赵明
 */
@Remote
public interface UserBeanRemote {
	/**
	 * 登录
	 * 
	 * @param userCode
	 *            用户编码
	 * @param userPwd
	 *            用户密码
	 */
	public LoginUser login(String userCode, String userPwd);

	/**
	 * 外联登录
	 * 
	 * @param userCode
	 *            用户编码
	 * @return LoginUser 登陆用户信息
	 */
	public LoginUser login(String userCode);

	/**
	 * 修改密码
	 */
	public void modifyPwd(long userId, String oldPwd, String newPwd);

	/**
	 * 登录用户修改个人信息
	 */
	public void editInfo(QueryedUser userInfo);

	/**
	 * 查询用户
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
	public QueryResult<EditedUser> queryUser(boolean queryCount,
			long loginUserId, Integer orgId, Integer manufId, Integer filter,
			String filterContent, int startRecord, int maxResult);

	/**
	 * 添加用户
	 */
	public long add(long loginUserId, EditedUser user, String userPwd);

	/**
	 * 编辑用户
	 */
	public void edit(long loginUserId, EditedUser user, String userPwd);

	/**
	 * 删除用户
	 * 
	 * @param userId
	 */
	public void delete(long loginUserId, long[] userId);

	/**
	 * 获得系统所有功能项
	 * 
	 * @return
	 */
	public Collection<EditedFunction> getAllFunction();

	/**
	 * 查找用户
	 * 
	 * @param userId
	 */
	public QueryedUser find(long userId);


	/**
	 * 获取用户的管理角色列表,并选中属于用户userId的角色
	 * 
	 * @param loginUserId
	 *            登录用户ID
	 * @param userId
	 *            用户ID
	 * @param keyword
	 *            过滤关键字
	 */
	public Collection<BaseRole> getUserManagedRoles(long loginUserId,
			Long userId, String keyword);

	/**
	 * 设置用户能管理的角色
	 * 
	 * @param loginUserId
	 * @param userId
	 * @param addList
	 * @param removeList
	 */
	public void setUserManagedRoles(long loginUserId, long userId,
			Collection<Integer> addList, Collection<Integer> removeList);


	/**
	 * 检查用户
	 * 
	 * @param userCode
	 *            用户代码
	 * @return EditedUser 用户信息
	 */
	public EditedUser find(String userCode);

}
