package kxd.remote.scs.beans.invoice;

import kxd.util.IdableObject;
import kxd.util.ListItem;

/**
 * 
 * @author jurstone
 *
 */
public class BaseInvoiceTemplate  extends ListItem<Integer> {

	private static final long serialVersionUID = -1351577495069319786L;
	private String templateDesp;
	
	public BaseInvoiceTemplate() {
	}

	public BaseInvoiceTemplate(Integer invoiceTemplateId) {
		super(invoiceTemplateId);
	}

	public BaseInvoiceTemplate(Integer invoiceTemplateId, String templateDesp) {
		super(invoiceTemplateId);
		this.templateDesp = templateDesp;
	}
	
	
	public String getTemplateDesp() {
		return templateDesp;
	}

	public void setTemplateDesp(String templateDesp) {
		this.templateDesp = templateDesp;
	}

	@Override
	public String getText() {
		return templateDesp;
	}

	@Override
	public void setText(String text) {
		templateDesp = text;
	}


	@Override
	public String getIdString() {
		if (getId() == null)
			return null;
		else
			return getId().toString();
	}

	@Override
	public void setIdString(String id) {
		if (id == null || id.trim().isEmpty())
			setId(null);
		else
			setId(Integer.parseInt(id));
	}

	@Override
	public IdableObject<Integer> createObject() {
		return new BaseInvoiceTemplate();
	}

	@Override
	protected String toDisplayLabel() {
		return templateDesp + "(" + getId() + ")";
	}


}
