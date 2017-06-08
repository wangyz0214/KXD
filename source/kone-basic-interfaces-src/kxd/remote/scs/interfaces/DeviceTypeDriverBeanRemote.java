/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package kxd.remote.scs.interfaces;

import java.util.List;

import javax.ejb.Remote;

import kxd.remote.scs.beans.BaseDeviceTypeDriver;
import kxd.remote.scs.util.QueryResult;
import kxd.util.KeyValue;

/**
 * 
 * @author 赵明
 */
@Remote
public interface DeviceTypeDriverBeanRemote {

	/**
	 * 查询模块驱动
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
	public QueryResult<BaseDeviceTypeDriver> queryDeviceTypeDriver(
			boolean queryCount, long loginUserId, Integer filter,
			String filterContent, int startRecord, int maxResult);

	/**
	 * 添加模块驱动
	 */
	public KeyValue<Integer, String> add(long loginUserId,
			BaseDeviceTypeDriver o);

	/**
	 * 编辑模块驱动
	 */
	public String edit(long loginUserId, BaseDeviceTypeDriver o);

	/**
	 * 删除模块驱动
	 */
	public String[] delete(long loginUserId, Integer[] o);

	/**
	 * 查找模块驱动
	 * 
	 * @param id
	 */
	public BaseDeviceTypeDriver find(int id);

	/**
	 * 获取模块驱动列表
	 * 
	 * @param loginUserId
	 * @param keyword
	 */
	public List<BaseDeviceTypeDriver> getDeviceTypeDriverList(long loginUserId,
			String keyword);
}
