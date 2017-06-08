/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package kxd.remote.scs.interfaces;

import java.util.List;

import javax.ejb.Remote;

import kxd.remote.scs.beans.BaseOrgBusinessOpenClose;
import kxd.remote.scs.util.QueryResult;

/**
 * 
 * @author 赵明
 */
@Remote
public interface OrgBusinessOpenCloseRemote {

	/**
	 * 查询业务停开
	 * 
	 * @param queryCount
	 *            是否返回记录总条数
	 * @param loginUserId
	 *            登录主机
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
	public QueryResult<BaseOrgBusinessOpenClose> query(boolean queryCount,
			long loginUserId, Integer orgId, Integer filter,
			String filterContent, int startRecord, int maxResult);

	/**
	 * 添加业务停开
	 */
	public long add(long loginUserId, BaseOrgBusinessOpenClose o);

	/**
	 * 编辑业务停开
	 */
	public void edit(long loginUserId, BaseOrgBusinessOpenClose o);

	/**
	 * 删除业务停开
	 */
	public void delete(long loginUserId, Long[] ids);

	/**
	 * 查找业务停开
	 * 
	 * @param manufId
	 */
	public BaseOrgBusinessOpenClose find(long id);

	/**
	 * 获取业务停开列表
	 * 
	 * @param loginUserId
	 * @param keyword
	 */
	public List<BaseOrgBusinessOpenClose> getList(long loginUserId,
			String keyword);
}
