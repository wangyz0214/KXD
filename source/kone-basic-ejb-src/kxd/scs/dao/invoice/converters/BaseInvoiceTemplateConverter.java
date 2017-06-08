package kxd.scs.dao.invoice.converters;

import kxd.engine.dao.BaseDaoConverter;
import kxd.remote.scs.beans.invoice.BaseInvoiceTemplate;

public class BaseInvoiceTemplateConverter  extends BaseDaoConverter<BaseInvoiceTemplate>{
	public BaseInvoiceTemplateConverter() {
	}

	@Override
	public BaseInvoiceTemplate doConvert(Object result) {
		Object[] o = (Object[]) result;
		BaseInvoiceTemplate r = new BaseInvoiceTemplate();
		r.setId(Integer.valueOf(o[0].toString()));
		r.setTemplateDesp(o[1].toString());
		return r;
	}
}
