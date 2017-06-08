/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package kxd.remote.scs.interfaces;

import java.util.List;

import javax.ejb.Remote;

import kxd.remote.scs.beans.BaseDevice;
import kxd.remote.scs.beans.device.EditedDevice;
import kxd.remote.scs.util.QueryResult;

/**
 * 
 * @author 赵明
 */
@Remote
public interface DeviceBeanRemote {

	/**
	 * 查询配件
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
	public QueryResult<EditedDevice> queryDevice(boolean queryCount,
			long loginUserId, Integer filter, String filterContent,
			int startRecord, int maxResult);
	
	/**
	 * 查询配件
	 * 
	 * @param queryCount
	 *            是否返回记录总条数
	 * @param loginUserId
	 *            登录用户
	 * @param filter
	 *            过滤操作
	 * @param filterContent
	 *            过滤内容
	 * @param typeId
	 * 			    所属模块
	 * @param startRecord
	 *            起始记录
	 * @param maxResult
	 *            最大的返回记录条数
	 * @return
	 */
	public QueryResult<EditedDevice> queryDevice(boolean queryCount,
			long loginUserId, Integer filter, String filterContent,Integer typeId,
			int startRecord, int maxResult);	

	/**
	 * 添加配件
	 */
	public int add(long loginUserId, EditedDevice device);

	/**
	 * 编辑配件
	 */
	public void edit(long loginUserId, EditedDevice device);

	/**
	 * 删除配件
	 */
	public void delete(long loginUserId, Integer[] device);

	/**
	 * 查找配件
	 * 
	 * @param manufId
	 */
	public EditedDevice find(int device);

	/**
	 * 获取配件列表
	 * 
	 * @param loginUserId
	 * @param keyword
	 */
	public List<BaseDevice> getDeviceList(long loginUserId,
			Integer exceptTermType, String keyword);
}
