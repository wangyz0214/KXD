/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kxd.scs.beans;

import java.util.Collection;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import kxd.engine.dao.BaseBean;
import kxd.remote.scs.beans.BaseRole;
import kxd.remote.scs.beans.right.EditedFunction;
import kxd.remote.scs.beans.right.EditedUser;
import kxd.remote.scs.beans.right.LoginUser;
import kxd.remote.scs.beans.right.QueryedUser;
import kxd.remote.scs.interfaces.UserBeanRemote;
import kxd.remote.scs.util.QueryResult;
import kxd.scs.dao.right.UserDao;

/**
 * 
 * @author Administrator
 */
@Stateless(name = "kxd-ejb-userBean", mappedName = "kxd-ejb-userBean")
public class UserBean extends BaseBean implements UserBeanRemote {

	/**
	 * 登录
	 * 
	 * @param userCode
	 *            用户编码
	 * @param userPwd
	 *            用户密码
	 */
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public LoginUser login(String userCode, String userPwd) {
		return UserDao.login(getDao(), userCode, userPwd);
	} 

	/**
	 * 外联登录
	 * 
	 * @param userCode
	 *            用户编码
	 * @return LoginUser 登陆用户信息
	 */

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public LoginUser login(String userCode) {
		return UserDao.login(getDao(), userCode, null);
	}

	/**
	 * 修改密码
	 */

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void modifyPwd(long userId, String oldPwd, String newPwd) {
		UserDao.modifyPwd(getDao(), userId, oldPwd, newPwd);
	}

	/**
	 * 登录用户修改个人信息
	 */

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void editInfo(QueryedUser userInfo) {
		UserDao.editInfo(getDao(), userInfo);
	}

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
	 * @param firstResult
	 *            起始记录
	 * @param maxResults
	 *            最大的返回记录条数
	 * @return
	 */

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public QueryResult<EditedUser> queryUser(boolean queryCount,
			long loginUserId, Integer orgId, Integer manufId, Integer filter,
			String filterContent, int firstResult, int maxResults) {
		return UserDao.queryUser(getDao(), queryCount, loginUserId, orgId,
				manufId, filter, filterContent, firstResult, maxResults);
	}

	/**
	 * 添加用户
	 */

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public long add(long loginUserId, EditedUser user, String userPwd) {
		return UserDao.add(getDao(), loginUserId, user, userPwd);
	}

	/**
	 * 编辑用户
	 */

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void edit(long loginUserId, EditedUser user, String userPwd) {
		UserDao.edit(getDao(), loginUserId, user, userPwd);
	}

	/**
	 * 删除用户
	 */

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void delete(long loginUserId, long[] userId) {
		UserDao.delete(getDao(), loginUserId, userId);
	}

	/**
	 * 获得系统所有功能项
	 */

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Collection<EditedFunction> getAllFunction() {
		return UserDao.getAllFunction(getDao());
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public QueryedUser find(long userId) {
		return UserDao.findQueryedUser(getDao(), userId);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void setUserManagedRoles(long loginUserId, long userId,
			Collection<Integer> addList, Collection<Integer> removeList) {
		UserDao.setUserManagedRoles(getDao(), loginUserId, userId, addList,
				removeList);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Collection<BaseRole> getUserManagedRoles(long loginUserId,
			Long userId, String keyword) {
		return UserDao.getUserManagedRoles(getDao(), loginUserId, userId,
				keyword);
	}

	/**
	 * 检查用户
	 * 
	 * @param userCode
	 *            用户代码
	 * @return EditedUser 用户信息
	 */

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public EditedUser find(String userCode) {
		return UserDao.find(getDao(), userCode);
	}
}
