package kxd.remote.scs.interfaces.invoice;

import java.util.List;

import javax.ejb.Remote;

import kxd.remote.scs.beans.invoice.BaseInvoiceTemplate;
import kxd.remote.scs.beans.invoice.EditedInvoiceTemplate;
import kxd.remote.scs.util.QueryResult;

/**
 * 
 * @author jurstone
 * 
 */
@Remote
public interface InvoiceTemplateBeanRemote {
	/**
	 * 查询发票样式信息
	 * 
	 * @param queryCount
	 *            是否返回记录总条数
	 * @param loginUserId
	 *            登录用户
	 * @param filterContent
	 *            过滤内容
	 * @param startRecord
	 *            起始记录
	 * @param maxResult
	 *            最大的返回记录条数
	 * @return
	 */
	public QueryResult<EditedInvoiceTemplate> queryInvoiceTemplate(
			boolean queryCount, long loginUserId, String filterContent,
			int startRecord, int maxResult);

	/**
	 * 添加发票样式信息
	 */
	public int add(long loginUserId, EditedInvoiceTemplate invoiceStyle);

	/**
	 * 编辑发票样式信息
	 */
	public void edit(long loginUserId, EditedInvoiceTemplate invoiceStyle);

	/**
	 * 删除发票样式信息
	 */
	public void delete(long loginUserId, Integer[] invoiceStyleIds);

	/**
	 * 查找发票样式信息
	 * 
	 * @param invoiceStyleId
	 */
	public EditedInvoiceTemplate find(int invoiceStyleId);

	/**
	 * 获取发票模板基础信息
	 * 
	 * @param loginUserId
	 * @param keyword
	 * @return
	 */
	public List<BaseInvoiceTemplate> getInvoiceTemplateList(long loginUserId,
			String keyword);
}
