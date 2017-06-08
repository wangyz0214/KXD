/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package kxd.remote.scs.interfaces;

import java.util.List;

import javax.ejb.Remote;

import kxd.remote.scs.beans.BasePrintType;
import kxd.remote.scs.util.QueryResult;

/**
 * 
 * @author 赵明
 */
@Remote
public interface PrintTypeBeanRemote {

	/**
	 * 查询打印类型
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
	public QueryResult<BasePrintType> queryPrintType(boolean queryCount,
			long loginUserId, Integer filter, String filterContent,
			int startRecord, int maxResult);

	/**
	 * 添加打印类型
	 */
	public void add(long loginUserId, BasePrintType printType);

	/**
	 * 编辑打印类型
	 */
	public void edit(long loginUserId, BasePrintType printType);

	/**
	 * 删除打印类型
	 */
	public void delete(long loginUserId, Short[] printType);

	/**
	 * 查找打印类型
	 * 
	 * @param manufId
	 */
	public BasePrintType find(int printType);

	/**
	 * 获取打印类型列表
	 * 
	 * @param loginUserId
	 * @param keyword
	 */
	public List<BasePrintType> getPrintTypeList(long loginUserId, String keyword);

	public void addUserPrintTimes(String userno, int printType, int month,
			int times);
}
