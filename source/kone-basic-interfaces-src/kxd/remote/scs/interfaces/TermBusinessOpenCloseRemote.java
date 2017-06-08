/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package kxd.remote.scs.interfaces;

import java.util.List;

import javax.ejb.Remote;

import kxd.remote.scs.beans.BaseTermBusinessOpenClose;
import kxd.remote.scs.util.QueryResult;

/**
 * 
 * @author 赵明
 */
@Remote
public interface TermBusinessOpenCloseRemote {

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
	public QueryResult<BaseTermBusinessOpenClose> query(boolean queryCount,
			long loginUserId, Integer termId, Integer filter,
			String filterContent, int startRecord, int maxResult);

	/**
	 * 添加业务停开
	 */
	public long add(long loginUserId, BaseTermBusinessOpenClose o);

	/**
	 * 编辑业务停开
	 */
	public void edit(long loginUserId, BaseTermBusinessOpenClose o);

	/**
	 * 删除业务停开
	 */
	public void delete(long loginUserId, Long[] ids);

	/**
	 * 查找业务停开
	 * 
	 * @param manufId
	 */
	public BaseTermBusinessOpenClose find(long id);

	/**
	 * 获取业务停开列表
	 * 
	 * @param loginUserId
	 * @param keyword
	 */
	public List<BaseTermBusinessOpenClose> getList(long loginUserId,
			String keyword);
}
