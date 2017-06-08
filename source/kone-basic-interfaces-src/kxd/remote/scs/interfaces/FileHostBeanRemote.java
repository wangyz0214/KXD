/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package kxd.remote.scs.interfaces;

import java.util.List;

import javax.ejb.Remote;

import kxd.remote.scs.beans.BaseFileHost;
import kxd.remote.scs.util.QueryResult;

/**
 * 
 * @author 赵明
 */
@Remote
public interface FileHostBeanRemote {

	/**
	 * 查询文件主机
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
	public QueryResult<BaseFileHost> queryFileHost(boolean queryCount,
			long loginHostId, Integer filter, String filterContent,
			int startRecord, int maxResult);

	/**
	 * 添加文件主机
	 */
	public int add(long loginHostId, BaseFileHost fileHost);

	/**
	 * 编辑文件主机
	 */
	public void edit(long loginHostId, BaseFileHost fileHost);

	/**
	 * 删除文件主机
	 */
	public void delete(long loginHostId, Short[] hostId);

	/**
	 * 查找文件主机
	 * 
	 * @param manufId
	 */
	public BaseFileHost find(short hostId);

	/**
	 * 获取文件主机列表
	 * 
	 * @param loginHostId
	 * @param keyword
	 */
	public List<BaseFileHost> getFileHostList(long loginHostId, String keyword);
}
