/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package kxd.remote.scs.interfaces;

import java.util.List;

import javax.ejb.Remote;

import kxd.remote.scs.beans.BaseDeviceDriver;
import kxd.remote.scs.beans.BaseDeviceDriverFile;
import kxd.remote.scs.util.QueryResult;
import kxd.util.KeyValue;

/**
 * 
 * @author 赵明
 */
@Remote
public interface DeviceDriverBeanRemote {
	public List<BaseDeviceDriverFile> getDeviceDriverFiles(int driverId);

	public int addDeviceDriverFile(long loginUserId, BaseDeviceDriverFile file);

	public String deleteDeviceDriverFile(long loginUserId, int fileId);

	/**
	 * 查询硬件驱动
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
	public QueryResult<BaseDeviceDriver> queryDeviceDriver(boolean queryCount,
			long loginUserId, Integer filter, String filterContent,
			int startRecord, int maxResult);

	/**
	 * 添加硬件驱动
	 */
	public KeyValue<Integer, String> add(long loginUserId, BaseDeviceDriver o);

	/**
	 * 编辑硬件驱动
	 */
	public String edit(long loginUserId, BaseDeviceDriver o);

	/**
	 * 删除硬件驱动
	 */
	public String[] delete(long loginUserId, Integer[] o);

	/**
	 * 查找硬件驱动
	 * 
	 * @param id
	 */
	public BaseDeviceDriver find(int id);

	/**
	 * 获取硬件驱动列表
	 * 
	 * @param loginUserId
	 * @param keyword
	 */
	public List<BaseDeviceDriver> getDeviceDriverList(long loginUserId,
			String keyword);
	
	/**
	 * 获取驱动文件
	 * @param fileId
	 * @return
	 */
	public BaseDeviceDriverFile findDriverFile(long fileId);
}
