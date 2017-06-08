package kxd.scs.dao.invoice.converters;

import kxd.engine.dao.BaseDaoConverter;
import kxd.remote.scs.beans.invoice.BaseInvoiceConfig;

public class BaseInvoiceConfigConverter extends
		BaseDaoConverter<BaseInvoiceConfig> {

	public BaseInvoiceConfigConverter() {
	}

	public BaseInvoiceConfig doConvert(Object result) {
		Object[] o = (Object[]) result;
		BaseInvoiceConfig r = new BaseInvoiceConfig();
		r.setId(Integer.valueOf(o[0].toString()));
		r.setConfigDesp(o[1].toString());
		return r;
	}
}
