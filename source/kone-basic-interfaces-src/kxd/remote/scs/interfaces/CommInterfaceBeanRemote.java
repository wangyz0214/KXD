/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package kxd.remote.scs.interfaces;

import java.util.List;

import javax.ejb.Remote;

import kxd.remote.scs.beans.BaseCommInterface;
import kxd.remote.scs.util.QueryResult;

/**
 * 
 * @author 赵明
 */
@Remote
public interface CommInterfaceBeanRemote {

	/**
	 * 查询接口
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
	public QueryResult<BaseCommInterface> queryCommInterface(
			boolean queryCount, long loginUserId, Integer filter,
			String filterContent, int startRecord, int maxResult);

	/**
	 * 添加接口
	 */
	public short add(long loginUserId, BaseCommInterface o);

	/**
	 * 编辑接口
	 */
	public void edit(long loginUserId, BaseCommInterface o);

	/**
	 * 删除接口
	 */
	public void delete(long loginUserId, Short[] id);

	/**
	 * 查找接口
	 * 
	 * @param manufId
	 */
	public BaseCommInterface find(short id);

	/**
	 * 获取接口列表
	 * 
	 * @param loginUserId
	 * @param keyword
	 */
	public List<BaseCommInterface> getCommInterfaceList(long loginUserId,
			String keyword);
}
