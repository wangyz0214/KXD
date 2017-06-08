/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package kxd.remote.scs.interfaces;

import java.util.List;

import javax.ejb.Remote;

import kxd.remote.scs.beans.BaseAlarmCode;
import kxd.remote.scs.beans.BaseDeviceType;
import kxd.remote.scs.beans.device.EditedDeviceType;
import kxd.remote.scs.util.QueryResult;

/**
 * 
 * @author 赵明
 */
@Remote
public interface DeviceTypeBeanRemote {

	/**
	 * 查询配件类型
	 * 
	 * @param queryCount
	 *            是否返回记录总条数
	 * @param loginUserId
	 *            登录用户
	 * @param filter
	 *            过滤操作
	 * @param filterContent
	 *            过滤内容
	 * @param Code
	 *            模块编码
	 * @param DeviceTypeDriverId
	 *            模块驱动           
	 * @param startRecord
	 *            起始记录
	 * @param maxResult
	 *            最大的返回记录条数
	 * @return
	 */
	public QueryResult<EditedDeviceType> queryDeviceType(boolean queryCount,
			long loginUserId, Integer filter, String filterContent,
			String code, Integer DeviceTypeDriverId, int startRecord,
			int maxResult);

	/**
	 * 查询配件类型
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
	public QueryResult<EditedDeviceType> queryDeviceType(boolean queryCount,
			long loginUserId, Integer filter, String filterContent,
			int startRecord, int maxResult);

	/**
	 * 添加配件类型
	 */
	public int add(long loginUserId, EditedDeviceType deviceType);

	/**
	 * 编辑配件类型
	 */
	public void edit(long loginUserId, EditedDeviceType deviceType);

	/**
	 * 删除配件类型
	 */
	public void delete(long loginUserId, Integer[] deviceType);

	/**
	 * 查找配件类型
	 * 
	 * @param manufId
	 */
	public EditedDeviceType find(int deviceType);

	/**
	 * 获取配件列表
	 * 
	 * @param loginUserId
	 * @param keyword
	 */
	public List<BaseDeviceType> getDeviceTypeList(long loginUserId,
			String keyword);

	/**
	 * 获取模块的告警代码列表
	 * 
	 * @param loginUserId
	 * @param deviceType
	 */
	public List<BaseAlarmCode> getAlarmCodeList(long loginUserId, int deviceType);

	public void addAlarmCode(long loginUserId, BaseAlarmCode o);

	public void editAlarmCode(long loginUserId, BaseAlarmCode o);

	public void deleteAlarmCode(long loginUserId, int deviceType, int[] id);

	public BaseAlarmCode findAlarmCode(int deviceType, int alarmCode);
}
