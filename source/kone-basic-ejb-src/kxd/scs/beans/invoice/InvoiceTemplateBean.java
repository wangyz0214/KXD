package kxd.scs.beans.invoice;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import kxd.engine.dao.BaseBean;
import kxd.remote.scs.beans.invoice.BaseInvoiceTemplate;
import kxd.remote.scs.beans.invoice.EditedInvoiceTemplate;
import kxd.remote.scs.interfaces.invoice.InvoiceTemplateBeanRemote;
import kxd.remote.scs.util.QueryResult;
import kxd.scs.dao.invoice.InvoiceTemplateDao;

@Stateless(name = "kxd-ejb-invoiceTemplateBean", mappedName = "kxd-ejb-invoiceTemplateBean")
public class InvoiceTemplateBean extends BaseBean implements
		InvoiceTemplateBeanRemote {

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public QueryResult<EditedInvoiceTemplate> queryInvoiceTemplate(
			boolean queryCount, long loginUserId, String filterContent,
			int startRecord, int maxResult) {
		QueryResult<EditedInvoiceTemplate> qr = InvoiceTemplateDao
				.queryInvoiceTemplate(getDao(), queryCount, loginUserId,
						filterContent, startRecord, maxResult);
		return qr;
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public int add(long loginUserId, EditedInvoiceTemplate invoiceTemplate) {
		return InvoiceTemplateDao.add(getDao(), loginUserId, invoiceTemplate);

	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void edit(long loginUserId, EditedInvoiceTemplate invoiceTemplate) {
		InvoiceTemplateDao.edit(getDao(), loginUserId, invoiceTemplate);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void delete(long loginUserId, Integer[] invoiceTemplateIds) {
		InvoiceTemplateDao.delete(getDao(), loginUserId, invoiceTemplateIds);

	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public EditedInvoiceTemplate find(int invoiceTemplateId) {
		return InvoiceTemplateDao.find(getDao(), invoiceTemplateId);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<BaseInvoiceTemplate> getInvoiceTemplateList(long loginUserId,
			String keyword) {
		return InvoiceTemplateDao.getInvoiceTemplateList(getDao(), loginUserId,
				keyword);
	}
}
