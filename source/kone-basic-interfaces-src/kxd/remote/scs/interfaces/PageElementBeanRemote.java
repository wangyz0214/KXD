/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package kxd.remote.scs.interfaces;

import java.util.List;

import javax.ejb.Remote;

import kxd.remote.scs.beans.BasePageElement;
import kxd.remote.scs.beans.appdeploy.EditedPageElement;
import kxd.remote.scs.util.QueryResult;

/**
 * 
 * @author 赵明
 */
@Remote
public interface PageElementBeanRemote {

	/**
	 * 查询页面元素
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
	public QueryResult<EditedPageElement> queryPageElement(boolean queryCount,
			long loginUserId, Integer filter, String filterContent,
			int startRecord, int maxResult);

	/**
	 * 添加页面元素
	 */
	public int add(long loginUserId, EditedPageElement pageElement);

	/**
	 * 编辑页面元素
	 */
	public void edit(long loginUserId, EditedPageElement pageElement);

	/**
	 * 删除页面元素
	 */
	public void delete(long loginUserId, int[] pageElement);

	/**
	 * 查找页面元素
	 * 
	 */
	public EditedPageElement find(int pageElement);

	/**
	 * 获取页面元素列表
	 * 
	 * @param loginUserId
	 * @param keyword
	 */
	public List<BasePageElement> getPageElementList(long loginUserId,
			String keyword);
}
