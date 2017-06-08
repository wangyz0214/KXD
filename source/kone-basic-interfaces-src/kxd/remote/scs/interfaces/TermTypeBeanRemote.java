/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package kxd.remote.scs.interfaces;

import java.util.List;

import javax.ejb.Remote;

import kxd.remote.scs.beans.BaseTermType;
import kxd.remote.scs.beans.BaseTermTypeDevice;
import kxd.remote.scs.beans.device.EditedTermType;
import kxd.remote.scs.beans.device.EditedTermTypeDevice;
import kxd.remote.scs.util.QueryResult;

/**
 * 
 * @author 赵明
 */
@Remote
public interface TermTypeBeanRemote {

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
	public QueryResult<EditedTermType> queryTermType(boolean queryCount,
			long loginUserId, Integer filter, String filterContent,
			int startRecord, int maxResult);

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
	 * @param desp
	 *            型号描述
	 * @param appId
	 *            所属应用
	 * @param manufId
	 *            所属厂商
	 * @param cashFlag
	 *            现金标志
	 * @param fixType
	 *            安装类型
	 * @param startRecord
	 *            起始记录
	 * @param maxResult
	 *            最大的返回记录条数
	 * @return
	 */
	public QueryResult<EditedTermType> queryTermType(boolean queryCount,
			long loginUserId, Integer filter, String filterContent,
			String desp, Integer appId, Integer manufId, Integer cashFlag,
			Integer fixType, int startRecord, int maxResult);

	/**
	 * 添加配件类型
	 */
	public int add(long loginUserId, EditedTermType termType);

	/**
	 * 编辑配件类型
	 */
	public void edit(long loginUserId, EditedTermType termType);

	/**
	 * 删除配件类型
	 */
	public void delete(long loginUserId, Integer[] termType);

	/**
	 * 查找配件类型
	 * 
	 * @param manufId
	 */
	public EditedTermType find(int termType);

	/**
	 * 获取配件列表
	 * 
	 * @param loginUserId
	 * @param keyword
	 */
	public List<BaseTermType> getTermTypeList(long loginUserId, String keyword);

	/**
	 * 获得型号的模块列表
	 * 
	 * @param loginUserId
	 * @param typeId
	 * @return
	 */
	public List<BaseTermTypeDevice> getTermTypeDeviceList(long loginUserId,
			int typeId);

	/**
	 * 添加模块
	 * 
	 * @param loginUserId
	 * @param device
	 */
	public void addTermTypeDevice(long loginUserId, EditedTermTypeDevice device);

	/**
	 * 编辑模块
	 * 
	 * @param loginUserId
	 * @param device
	 */
	public void editTermTypeDevice(long loginUserId, EditedTermTypeDevice device);

	/**
	 * 删除模块
	 * 
	 * @param loginUserId
	 * @param device
	 */
	public void deleteTermTypeDevice(long loginUserId, int typeId,
			int[] deviceId);

	public EditedTermTypeDevice findDevice(long loginUserId, int typeId,
			int deviceId);
}
