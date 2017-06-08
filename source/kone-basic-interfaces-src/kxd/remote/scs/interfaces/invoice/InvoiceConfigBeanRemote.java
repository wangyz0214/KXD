/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package kxd.remote.scs.interfaces.invoice;

import java.util.List;

import javax.ejb.Remote;

import kxd.remote.scs.beans.invoice.BaseInvoiceConfig;
import kxd.remote.scs.beans.invoice.EditedInvoiceConfig;
import kxd.remote.scs.util.QueryResult;

/**
 * 
 * @author jurstone
 */
@Remote
public interface InvoiceConfigBeanRemote {

	/**
	 * 查询厂商
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
	public QueryResult<EditedInvoiceConfig> queryInvoiceConfig(
			boolean queryCount, long loginUserId, int orgId, Integer filter,
			String filterContent, int startRecord, int maxResult);

	/**
	 * 添加机构
	 */
	public int add(long loginUserId, EditedInvoiceConfig invoiceConfig);

	/**
	 * 编辑机构
	 */
	public void edit(long loginUserId, EditedInvoiceConfig invoiceConfig);

	/**
	 * 删除机构
	 */
	public void delete(long loginUserId, Integer[] invoiceConfigIds);

	/**
	 * 查找发票配置
	 * 
	 * @param invoiceConfigId
	 */
	public EditedInvoiceConfig find(int invoiceConfigId);

	/**
	 * 查找发票配置
	 * 
	 * @param invoiceConfigId
	 */
	public EditedInvoiceConfig find(int orgId, int type);

	/**
	 * 获取应用列表
	 * 
	 * @param loginUserId
	 * @param keyword
	 */
	public List<BaseInvoiceConfig> getInvoiceConfigList(long loginUserId,
			String keyword);
}
