package kxd.engine.scs.admin.actions.invoice;

import javax.servlet.http.HttpServletResponse;

import kxd.engine.scs.admin.actions.BaseDatatableAction;
import kxd.engine.scs.admin.util.UserRight;
import kxd.net.HttpRequest;

public class QueryInvoiceTemplateAction extends BaseDatatableAction {
	
	@Override
	protected String doExecute(boolean isFormSubmit, HttpRequest request,
			HttpServletResponse response) throws Throwable {
		return null;
	}

	@Override
	protected void setParameters(boolean isFormSubmit, HttpRequest request)
			throws Throwable {
		super.setParameters(isFormSubmit, request);
	}
	
	@Override
	public int getEditRight() {
		return UserRight.INVOICE_TEMPLATE_EDIT;
	}

	@Override
	public int getQueryRight() {
		return UserRight.INVOICE_TEMPLATE;
	}
	
	@Override
	protected String doGetTableOptionHtml() {
		return "<a href='#' onclick='ajax_table.showEditDialog(arguments[0],750,700);return false;'>编辑</a> "
			+  "<a href='#' onclick='ajax_table.deleteSelected(arguments[0]);return false;'>删除</a>"
			+ " <a href='#' onclick='showTemplatePreviewDialog(arguments[0]);return false;'>[模板预览]</a>";
	}

}
