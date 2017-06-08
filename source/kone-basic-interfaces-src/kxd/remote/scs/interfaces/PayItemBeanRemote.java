/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package kxd.remote.scs.interfaces;

import java.util.List;

import javax.ejb.Remote;

import kxd.remote.scs.beans.BasePayItem;
import kxd.remote.scs.beans.appdeploy.EditedPayItem;
import kxd.remote.scs.util.QueryResult;

/**
 * 
 * @author 赵明
 */
@Remote
public interface PayItemBeanRemote {

	/**
	 * 查询收费项目
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
	public QueryResult<EditedPayItem> queryPayItem(boolean queryCount,
			long loginUserId, Integer filter, String filterContent,
			int startRecord, int maxResult);

	/**
	 * 添加收费项目
	 */
	public short add(long loginUserId, EditedPayItem payItem);

	/**
	 * 编辑收费项目
	 */
	public void edit(long loginUserId, EditedPayItem payItem);

	/**
	 * 删除收费项目
	 */
	public void delete(long loginUserId, Short[] payItem);

	/**
	 * 查找收费项目
	 * 
	 */
	public EditedPayItem find(int payItem);

	/**
	 * 获取收费项目列表
	 * 
	 * @param loginUserId
	 * @param keyword
	 */
	public List<BasePayItem> getPayItemList(long loginUserId, String keyword);
}
