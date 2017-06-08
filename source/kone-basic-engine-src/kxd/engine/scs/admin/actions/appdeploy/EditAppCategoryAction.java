package kxd.engine.scs.admin.actions.appdeploy;

import javax.servlet.http.HttpServletResponse;

import kxd.engine.scs.admin.AdminSessionObject;
import kxd.engine.scs.admin.util.UserRight;
import kxd.engine.ui.core.EditAction;
import kxd.net.HttpRequest;
import kxd.net.connection.jndi.Lookuper;
import kxd.net.naming.LoopNamingContext;
import kxd.remote.scs.beans.appdeploy.EditedAppCategory;
import kxd.remote.scs.interfaces.AppCategoryBeanRemote;

public class EditAppCategoryAction extends EditAction {
	private EditedAppCategory appCategory;
	boolean added;

	@Override
	protected String doExecute(boolean isFormSubmit, HttpRequest request,
			HttpServletResponse response) throws Throwable {
		if (isFormSubmit) {
			addOrEditAppCategory();
			return "success";
		} else
			return null;
	}

	@Override
	protected void setParameters(boolean isFormSubmit, HttpRequest request)
			throws Throwable {
		super.setParameters(isFormSubmit, request);
		if (isFormSubmit) {
			appCategory = getAppCategory();
			appCategory.setId(request.getParameterIntDef("id", null));
			appCategory.setAppCategoryCode(request.getParameter("code"));
			appCategory.setAppCategoryDesp(request.getParameter("desp"));
		} else {
			getAppCategory(request.getParameterIntDef("id", null));
		}
	}

	private void getAppCategory(Integer id) throws Throwable {
		if (id == null)
			return;
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			AppCategoryBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-appCategoryBean", AppCategoryBeanRemote.class);
			appCategory = bean.find(id);
		} finally {
			context.close();
		}
	}

	private void addOrEditAppCategory() throws Throwable {
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			AppCategoryBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-appCategoryBean", AppCategoryBeanRemote.class);
			if (getAppCategory().getId() == null) {
				appCategory.setId(bean.add(((AdminSessionObject) session)
						.getLoginUser().getUserId(), appCategory));
				added = true;
			} else {
				bean.edit(((AdminSessionObject) session).getLoginUser()
						.getUserId(), appCategory);
				added = false;
			}
		} finally {
			context.close();
		}
	}

	public String getSuccessScript() {
		String script = "var items={id:'" + appCategory.getIdString()
				+ "',desp:'" + appCategory.getAppCategoryCode() + "',columns:[";
		script += "'" + appCategory.getIdString() + "',";
		script += "'" + appCategory.getAppCategoryCode() + "',";
		script += "'" + appCategory.getAppCategoryDesp() + "'";
		script += "]};";
		if (added)
			script += "top.ajax_table.addsuccess(items);";
		else
			script += "top.ajax_table.editsuccess(items);";
		return script;
	}

	public EditedAppCategory getAppCategory() {
		if (appCategory == null) {
			appCategory = new EditedAppCategory();
		}
		return appCategory;
	}

	public void setAppCategory(EditedAppCategory appCategory) {
		this.appCategory = appCategory;
	}

	@Override
	public int getEditRight() {
		return UserRight.APPCATEGORY_EDIT;
	}

	@Override
	public int getQueryRight() {
		return UserRight.APPCATEGORY;
	}
}
