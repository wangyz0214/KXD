/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package kxd.remote.scs.interfaces;

import java.util.List;

import javax.ejb.Remote;

import kxd.remote.scs.beans.BaseFileOwner;
import kxd.remote.scs.util.QueryResult;

/**
 * 
 * @author 赵明
 */
@Remote
public interface FileOwnerBeanRemote {

	/**
	 * 查询文件属主
	 * 
	 * @param queryCount
	 *            是否返回记录总条数
	 * @param loginUserId
	 *            登录属主
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
	public QueryResult<BaseFileOwner> queryFileOwner(boolean queryCount,
			long loginOwnerId, Integer filter, String filterContent,
			int startRecord, int maxResult);

	/**
	 * 添加文件属主
	 */
	public int add(long loginOwnerId, BaseFileOwner fileOwner);

	/**
	 * 编辑文件属主
	 */
	public void edit(long loginOwnerId, BaseFileOwner fileOwner);

	/**
	 * 删除文件属主
	 */
	public void delete(long loginOwnerId, Short[] ownerId);

	/**
	 * 查找文件属主
	 * 
	 * @param manufId
	 */
	public BaseFileOwner find(short ownerId);

	/**
	 * 获取文件属主列表
	 * 
	 * @param loginOwnerId
	 * @param keyword
	 */
	public List<BaseFileOwner> getFileOwnerList(long loginOwnerId,
			String keyword);
}
