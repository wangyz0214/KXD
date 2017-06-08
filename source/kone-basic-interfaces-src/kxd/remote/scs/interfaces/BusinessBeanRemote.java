/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package kxd.remote.scs.interfaces;

import java.util.List;

import javax.ejb.Remote;

import kxd.remote.scs.beans.BaseBusiness;
import kxd.remote.scs.beans.appdeploy.EditedBusiness;
import kxd.remote.scs.util.QueryResult;

/**
 * 
 * @author 赵明
 */
@Remote
public interface BusinessBeanRemote {

	/**
	 * 查询业务
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
	public QueryResult<EditedBusiness> queryBusiness(boolean queryCount,
			long loginUserId, Integer filter, String filterContent,
			int startRecord, int maxResult);

	/**
	 * 添加业务
	 */
	public int add(long loginUserId, EditedBusiness business);

	/**
	 * 编辑业务
	 */
	public void edit(long loginUserId, EditedBusiness business);

	/**
	 * 删除业务
	 */
	public void delete(long loginUserId, Integer[] business);

	/**
	 * 查找业务
	 * 
	 * @param manufId
	 */
	public EditedBusiness find(int business);

	/**
	 * 获取业务列表
	 * 
	 * @param loginUserId
	 * @param keyword
	 */
	public List<BaseBusiness> getBusinessList(long loginUserId, String keyword);
}
