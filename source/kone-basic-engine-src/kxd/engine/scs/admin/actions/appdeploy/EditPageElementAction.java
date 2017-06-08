package kxd.engine.scs.admin.actions.appdeploy;

import javax.servlet.http.HttpServletResponse;

import kxd.engine.scs.admin.AdminSessionObject;
import kxd.engine.scs.admin.util.UserRight;
import kxd.engine.ui.core.EditAction;
import kxd.net.HttpRequest;
import kxd.net.connection.jndi.Lookuper;
import kxd.net.naming.LoopNamingContext;
import kxd.remote.scs.beans.BaseBusiness;
import kxd.remote.scs.beans.appdeploy.EditedPageElement;
import kxd.remote.scs.interfaces.PageElementBeanRemote;

public class EditPageElementAction extends EditAction {
	private EditedPageElement pageElement;
	boolean added;

	@Override
	protected String doExecute(boolean isFormSubmit, HttpRequest request,
			HttpServletResponse response) throws Throwable {
		if (isFormSubmit) {
			addOrEditPageElement();
			return "success";
		} else
			return null;
	}

	@Override
	protected void setParameters(boolean isFormSubmit, HttpRequest request)
			throws Throwable {
		super.setParameters(isFormSubmit, request);
		if (isFormSubmit) {
			pageElement = getPageElement();
			pageElement.setId(request.getParameterIntDef("id", null));
			BaseBusiness b = new BaseBusiness(request
					.getParameterInt("businessid"));
			b.setBusinessDesp(request.getParameter("businessid_desp"));
			pageElement.setBusiness(b);
			pageElement.setPageCode(request.getParameter("code"));
			pageElement.setPageDesp(request.getParameter("desp"));
		} else {
			getPageElement(request.getParameterIntDef("id", null));
		}
	}

	private void getPageElement(Integer id) throws Throwable {
		if (id == null)
			return;
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			PageElementBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-pageElementBean", PageElementBeanRemote.class);
			pageElement = bean.find(id);
		} finally {
			context.close();
		}
	}

	private void addOrEditPageElement() throws Throwable {
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			PageElementBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-pageElementBean", PageElementBeanRemote.class);
			if (getPageElement().getId() == null) {
				pageElement.setId(bean.add(((AdminSessionObject) session)
						.getLoginUser().getUserId(), pageElement));
				added = true;
			} else {
				bean.edit(((AdminSessionObject) session).getLoginUser()
						.getUserId(), pageElement);
				added = false;
			}
		} finally {
			context.close();
		}
	}

	public String getSuccessScript() {
		String script = "var items={id:'" + pageElement.getIdString()
				+ "',desp:'" + pageElement.getPageDesp() + "',columns:[";
		script += "'" + pageElement.getIdString() + "',";
		script += "'" + pageElement.getPageCode() + "',";
		script += "'" + pageElement.getPageDesp() + "',";
		script += "'" + pageElement.getBusiness().getBusinessDesp() + "'";
		script += "]};";
		if (added)
			script += "top.ajax_table.addsuccess(items);";
		else
			script += "top.ajax_table.editsuccess(items);";
		return script;
	}

	public EditedPageElement getPageElement() {
		if (pageElement == null) {
			pageElement = new EditedPageElement();
		}
		return pageElement;
	}

	public void setPageElement(EditedPageElement pageElement) {
		this.pageElement = pageElement;
	}

	@Override
	public int getEditRight() {
		return UserRight.PAGEELEMENT_EDIT;
	}

	@Override
	public int getQueryRight() {
		return UserRight.PAGEELEMENT;
	}
}
