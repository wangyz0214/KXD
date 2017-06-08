/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package kxd.remote.scs.interfaces;

import java.util.List;

import javax.ejb.Remote;

import kxd.remote.scs.beans.BaseFileUser;
import kxd.remote.scs.util.QueryResult;

/**
 * 
 * @author 赵明
 */
@Remote
public interface FileUserBeanRemote {

	/**
	 * 查询文件用户
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
	public QueryResult<BaseFileUser> queryFileUser(boolean queryCount,
			long loginUserId, Integer filter, String filterContent,
			int startRecord, int maxResult);

	/**
	 * 添加文件用户
	 */
	public void add(long loginUserId, BaseFileUser fileUser);

	/**
	 * 编辑文件用户
	 */
	public void edit(long loginUserId, BaseFileUser fileUser);

	/**
	 * 删除文件用户
	 */
	public void delete(long loginUserId, String[] userId);

	/**
	 * 查找文件用户
	 * 
	 * @param manufId
	 */
	public BaseFileUser find(String userId);

	/**
	 * 获取文件用户列表
	 * 
	 * @param loginUserId
	 * @param keyword
	 */
	public List<BaseFileUser> getFileUserList(long loginUserId, String keyword);
}
