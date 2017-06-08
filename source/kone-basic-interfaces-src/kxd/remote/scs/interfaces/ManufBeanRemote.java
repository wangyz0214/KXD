/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package kxd.remote.scs.interfaces;

import javax.ejb.Remote;

import kxd.remote.scs.beans.right.EditedManuf;
import kxd.remote.scs.util.QueryResult;

/**
 * 
 * @author 赵明
 */
@Remote
public interface ManufBeanRemote {

	/**
	 * 查询厂商
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
	public QueryResult<EditedManuf> queryManuf(boolean queryCount,
			long loginUserId, Integer filter, String filterContent,
			int startRecord, int maxResult);

	/**
	 * 添加机构
	 */
	public int add(long loginUserId, EditedManuf manuf);

	/**
	 * 编辑机构
	 */
	public void edit(long loginUserId, EditedManuf manuf);
	/**
	 * 删除机构
	 */
	public void delete(long loginUserId, Integer[] manufId);

	/**
	 * 查找厂商
	 * 
	 * @param manufId
	 */
	public EditedManuf find(int manufId);
}
