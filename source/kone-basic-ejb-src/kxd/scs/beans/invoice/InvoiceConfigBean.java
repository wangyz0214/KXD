package kxd.scs.beans.invoice;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import kxd.engine.dao.BaseBean;
import kxd.remote.scs.beans.invoice.BaseInvoiceConfig;
import kxd.remote.scs.beans.invoice.EditedInvoiceConfig;
import kxd.remote.scs.interfaces.invoice.InvoiceConfigBeanRemote;
import kxd.remote.scs.util.QueryResult;
import kxd.scs.dao.invoice.InvoiceConfigDao;

@Stateless(name = "kxd-ejb-invoiceConfigBean", mappedName = "kxd-ejb-invoiceConfigBean")
public class InvoiceConfigBean extends BaseBean implements
		InvoiceConfigBeanRemote {

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public EditedInvoiceConfig find(int invoiceConfigId) {
		return InvoiceConfigDao.find(getDao(), invoiceConfigId);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public int add(long loginUserId, EditedInvoiceConfig invoiceConfig) {
		return InvoiceConfigDao.add(getDao(), loginUserId, invoiceConfig);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void delete(long loginUserId, Integer[] invoiceConfigIds) {
		InvoiceConfigDao.delete(getDao(), loginUserId, invoiceConfigIds);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void edit(long loginUserId, EditedInvoiceConfig invoiceConfig) {
		InvoiceConfigDao.edit(getDao(), loginUserId, invoiceConfig);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public QueryResult<EditedInvoiceConfig> queryInvoiceConfig(
			boolean queryCount, long loginUserId, int orgId, Integer filter,
			String filterContent, int firstResult, int maxResults) {
		return InvoiceConfigDao.queryInvoiceConfig(getDao(), orgId, queryCount,
				loginUserId, filter, filterContent, firstResult, maxResults);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<BaseInvoiceConfig> getInvoiceConfigList(long loginUserId,
			String keyword) {
		return InvoiceConfigDao.getInvoiceConfigList(getDao(), loginUserId,
				keyword);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public EditedInvoiceConfig find(int orgId, int type) {
		return InvoiceConfigDao.find(getDao(), orgId, type);
	}
}
