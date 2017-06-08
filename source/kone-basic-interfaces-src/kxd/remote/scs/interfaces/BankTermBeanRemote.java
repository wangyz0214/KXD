/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package kxd.remote.scs.interfaces;

import java.util.List;

import javax.ejb.Remote;

import kxd.remote.scs.beans.BaseBankTerm;
import kxd.remote.scs.beans.device.EditedBankTerm;
import kxd.remote.scs.util.QueryResult;

/**
 * 
 * @author 赵明
 */
@Remote
public interface BankTermBeanRemote {

	/**
	 * 查询银联终端
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
	public QueryResult<EditedBankTerm> queryBankTerm(boolean queryCount,
			long loginUserId, Integer filter, String filterContent,
			int startRecord, int maxResult);

	/**
	 * 添加银联终端
	 */
	public int add(long loginUserId, EditedBankTerm term);

	/**
	 * 编辑银联终端
	 */
	public void edit(long loginUserId, EditedBankTerm term);

	/**
	 * 删除银联终端
	 */
	public void delete(long loginUserId, Integer[] term);

	/**
	 * 查找银联终端
	 * 
	 * @param manufId
	 */
	public EditedBankTerm find(int term);

	/**
	 * 获取银联终端列表
	 * 
	 * @param loginUserId
	 * @param keyword
	 */
	public List<BaseBankTerm> getBankTermList(long loginUserId, String keyword);

	/**
	 * 查找银联终端
	 * 
	 * @param unionpayTermCode
	 * @return
	 */
	public EditedBankTerm findbasebank(String unionpayTermCode,
			String commercialCode);
}
