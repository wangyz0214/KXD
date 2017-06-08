/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package kxd.remote.scs.interfaces;

import java.util.List;

import javax.ejb.Remote;

import kxd.remote.scs.beans.BasePrintAdCategory;
import kxd.remote.scs.util.QueryResult;

/**
 * 
 * @author 赵明
 */
@Remote
public interface PrintAdCategoryBeanRemote {

	/**
	 * 查询打印广告分类
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
	public QueryResult<BasePrintAdCategory> queryPrintAdCategory(
			boolean queryCount, long loginUserId, Integer filter,
			String filterContent, int startRecord, int maxResult);

	/**
	 * 添加打印广告分类
	 */
	public void add(long loginUserId, BasePrintAdCategory printAdCategory);

	/**
	 * 编辑打印广告分类
	 */
	public void edit(long loginUserId, BasePrintAdCategory printAdCategory);

	/**
	 * 删除打印广告分类
	 */
	public void delete(long loginUserId, short[] printAdCategory);

	/**
	 * 查找打印广告分类
	 * 
	 * @param manufId
	 */
	public BasePrintAdCategory find(short printAdCategory);

	/**
	 * 获取打印广告分类列表
	 * 
	 * @param loginUserId
	 * @param keyword
	 */
	public List<BasePrintAdCategory> getPrintAdCategoryList(long loginUserId,
			String keyword);
}
