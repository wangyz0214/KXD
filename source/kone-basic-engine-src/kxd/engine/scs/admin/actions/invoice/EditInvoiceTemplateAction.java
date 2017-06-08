package kxd.engine.scs.admin.actions.invoice;

import javax.servlet.http.HttpServletResponse;

import kxd.engine.scs.admin.AdminSessionObject;
import kxd.engine.scs.admin.util.UserRight;
import kxd.engine.ui.core.EditAction;
import kxd.net.HttpRequest;
import kxd.net.connection.jndi.Lookuper;
import kxd.net.naming.LoopNamingContext;
import kxd.remote.scs.beans.invoice.EditedInvoiceTemplate;
import kxd.remote.scs.interfaces.invoice.InvoiceTemplateBeanRemote;

public class EditInvoiceTemplateAction extends EditAction {
	private EditedInvoiceTemplate invoiceTemplate;
	boolean added;

	@Override
	protected String doExecute(boolean isFormSubmit, HttpRequest request,
			HttpServletResponse response) throws Throwable {
		if (isFormSubmit) {
			addOrEditInvoiceTemplate();
			return "success";
		} else
			return null;
	}

	@Override
	protected void setParameters(boolean isFormSubmit, HttpRequest request)
			throws Throwable {
		super.setParameters(isFormSubmit, request);
		if (isFormSubmit) {
			invoiceTemplate = getInvoiceTemplate();
			invoiceTemplate.setId(request.getParameterIntDef("id", null));
			invoiceTemplate.setTemplateDesp(request
					.getParameter("templatedesp"));
			invoiceTemplate.setTemplateCode(request
					.getParameter("templatecode"));
			invoiceTemplate.setTemplateContent(request
					.getParameter("templatecontent"));
		} else {
			getInvoiceTemplate(request.getParameterIntDef("id", null));
		}
	}

	private void getInvoiceTemplate(Integer id) throws Throwable {
		if (id == null)
			return;
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			InvoiceTemplateBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-invoiceTemplateBean",
					InvoiceTemplateBeanRemote.class);
			invoiceTemplate = bean.find(id);
		} finally {
			context.close();
		}
	}

	private void addOrEditInvoiceTemplate() throws Throwable {
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			InvoiceTemplateBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-invoiceTemplateBean",
					InvoiceTemplateBeanRemote.class);
			if (getInvoiceTemplate().getId() == null) {
				invoiceTemplate.setId(bean.add(((AdminSessionObject) session)
						.getLoginUser().getUserId(), invoiceTemplate));
				added = true;
			} else {
				bean.edit(((AdminSessionObject) session).getLoginUser()
						.getUserId(), invoiceTemplate);
				added = false;
			}
		} finally {
			context.close();
		}
	}

	public String getSuccessScript() {
		String script = "var items={id:'" + invoiceTemplate.getIdString()
				+ "',desp:'" + invoiceTemplate.getTemplateDesp()
				+ "',columns:[";
		/*
		 * script +=
		 * "'<a href=\"#\" onclick=\"ajax_table.showEditDialog(arguments[0],750,700);return false;\">"
		 * +
		 * "�༭</a> <a href=\"#\" onclick=\"ajax_table.deleteSelected(arguments[0]);return false;\">"
		 * + "ɾ��</a> <a id='editfuncs' href='#' onclick=''>[ ]</a>',";
		 */
		script += "'" + invoiceTemplate.getIdString() + "',";
		script += "'" + invoiceTemplate.getTemplateDesp() + "',";
		script += "'" + invoiceTemplate.getTemplateCode() + "',";
		// script += "'" + invoiceTemplate.getTemplateContent() + "'";
		script += "]};";
		if (added)
			script += "top.ajax_table.addsuccess(items);";
		else
			script += "top.ajax_table.editsuccess(items);";
		return script;
	}

	public EditedInvoiceTemplate getInvoiceTemplate() {
		if (invoiceTemplate == null)
			invoiceTemplate = new EditedInvoiceTemplate();
		return invoiceTemplate;
	}

	public void setInvoiceTemplate(EditedInvoiceTemplate invoiceTemplate) {
		this.invoiceTemplate = invoiceTemplate;
	}

	@Override
	public int getEditRight() {
		return UserRight.INVOICE_TEMPLATE_EDIT;
	}

	@Override
	public int getQueryRight() {
		return UserRight.INVOICE_TEMPLATE;
	}

}
