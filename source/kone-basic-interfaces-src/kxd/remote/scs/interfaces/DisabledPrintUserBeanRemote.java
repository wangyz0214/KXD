/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package kxd.remote.scs.interfaces;

import javax.ejb.Remote;

import kxd.remote.scs.beans.BaseDisabledPrintUser;
import kxd.remote.scs.util.QueryResult;

/**
 * 
 * @author 赵明
 */
@Remote
public interface DisabledPrintUserBeanRemote {

	/**
	 * 查询禁止查询打印用户
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
	public QueryResult<BaseDisabledPrintUser> queryDisabledPrintUser(
			boolean queryCount, long loginUserId, Integer filter,
			String filterContent, int startRecord, int maxResult);

	/**
	 * 添加禁止查询打印用户
	 */
	public long add(long loginUserId, BaseDisabledPrintUser o);

	/**
	 * 编辑禁止查询打印用户
	 */
	public void edit(long loginUserId, BaseDisabledPrintUser o);

	/**
	 * 删除禁止查询打印用户
	 */
	public void delete(long loginUserId, long[] o);

	/**
	 * 查找禁止查询打印用户
	 * 
	 * @param manufId
	 */
	public BaseDisabledPrintUser find(long o);
}
