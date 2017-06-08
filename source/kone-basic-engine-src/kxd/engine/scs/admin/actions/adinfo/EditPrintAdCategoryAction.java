package kxd.engine.scs.admin.actions.adinfo;

import javax.servlet.http.HttpServletResponse;

import kxd.engine.scs.admin.AdminSessionObject;
import kxd.engine.scs.admin.util.UserRight;
import kxd.engine.ui.core.EditAction;
import kxd.net.HttpRequest;
import kxd.net.connection.jndi.Lookuper;
import kxd.net.naming.LoopNamingContext;
import kxd.remote.scs.beans.BasePrintAdCategory;
import kxd.remote.scs.interfaces.PrintAdCategoryBeanRemote;

public class EditPrintAdCategoryAction extends EditAction {
	private BasePrintAdCategory printAdCategory;
	boolean added;

	@Override
	protected String doExecute(boolean isFormSubmit, HttpRequest request,
			HttpServletResponse response) throws Throwable {
		if (isFormSubmit) {
			addOrEditPrintAdCategory();
			return "success";
		} else
			return null;
	}

	@Override
	protected void setParameters(boolean isFormSubmit, HttpRequest request)
			throws Throwable {
		super.setParameters(isFormSubmit, request);
		if (isFormSubmit) {
			printAdCategory = getPrintAdCategory();
			added = request.getParameterBoolean("add");
			printAdCategory.setId(request.getParameterShortDef("id", null));
			printAdCategory
					.setPrintAdCategoryDesp(request.getParameter("desp"));
		} else {
			getPrintAdCategory(request.getParameterShortDef("id", null));
		}
	}

	private void getPrintAdCategory(Short id) throws Throwable {
		if (id == null)
			return;
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			PrintAdCategoryBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-printAdCategoryBean",
					PrintAdCategoryBeanRemote.class);
			printAdCategory = bean.find(id);
		} finally {
			context.close();
		}
	}

	private void addOrEditPrintAdCategory() throws Throwable {
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			PrintAdCategoryBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-printAdCategoryBean",
					PrintAdCategoryBeanRemote.class);
			if (added) {
				bean.add(((AdminSessionObject) session).getLoginUser()
						.getUserId(), printAdCategory);
			} else {
				bean.edit(((AdminSessionObject) session).getLoginUser()
						.getUserId(), printAdCategory);
			}
		} finally {
			context.close();
		}
	}

	public String getSuccessScript() {
		String script = "var items={id:'" + printAdCategory.getIdString()
				+ "',desp:'" + printAdCategory.getPrintAdCategoryDesp()
				+ "',columns:[";
		script += "'" + printAdCategory.getIdString() + "',";
		script += "'" + printAdCategory.getPrintAdCategoryDesp() + "'";
		script += "]};";
		if (added)
			script += "top.ajax_table.addsuccess(items);";
		else
			script += "top.ajax_table.editsuccess(items);";
		return script;
	}

	public BasePrintAdCategory getPrintAdCategory() {
		if (printAdCategory == null) {
			printAdCategory = new BasePrintAdCategory();
		}
		return printAdCategory;
	}

	public void setPrintAdCategory(BasePrintAdCategory printAdCategory) {
		this.printAdCategory = printAdCategory;
	}

	@Override
	public int getEditRight() {
		return UserRight.PRINTADCATEGORY_EDIT;
	}

	@Override
	public int getQueryRight() {
		return UserRight.PRINTADCATEGORY;
	}
}
