package kxd.engine.scs.admin.actions.appdeploy;

import javax.servlet.http.HttpServletResponse;

import kxd.engine.scs.admin.AdminSessionObject;
import kxd.engine.scs.admin.util.UserRight;
import kxd.engine.ui.core.EditAction;
import kxd.net.HttpRequest;
import kxd.net.connection.jndi.Lookuper;
import kxd.net.naming.LoopNamingContext;
import kxd.remote.scs.beans.BaseAppCategory;
import kxd.remote.scs.beans.appdeploy.EditedApp;
import kxd.remote.scs.interfaces.AppBeanRemote;

public class EditAppAction extends EditAction {
	private EditedApp app;
	boolean added;

	@Override
	protected String doExecute(boolean isFormSubmit, HttpRequest request,
			HttpServletResponse response) throws Throwable {
		if (isFormSubmit) {
			addOrEditApp();
			return "success";
		} else
			return null;
	}

	@Override
	protected void setParameters(boolean isFormSubmit, HttpRequest request)
			throws Throwable {
		super.setParameters(isFormSubmit, request);
		if (isFormSubmit) {
			app = getApp();
			app.setId(request.getParameterIntDef("id", null));
			BaseAppCategory b = new BaseAppCategory(request
					.getParameterInt("appcategoryid"));
			b.setAppCategoryDesp(request.getParameter("appcategoryid_desp"));
			app.setAppCategory(b);
			app.setAppCode(request.getParameter("code"));
			app.setAppDesp(request.getParameter("desp"));
		} else {
			getApp(request.getParameterIntDef("id", null));
		}
	}

	private void getApp(Integer id) throws Throwable {
		if (id == null)
			return;
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			AppBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-appBean", AppBeanRemote.class);
			app = bean.find(id);
		} finally {
			context.close();
		}
	}

	private void addOrEditApp() throws Throwable {
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			AppBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-appBean", AppBeanRemote.class);
			if (getApp().getId() == null) {
				app.setId(bean.add(((AdminSessionObject) session)
						.getLoginUser().getUserId(), app));
				added = true;
			} else {
				bean.edit(((AdminSessionObject) session).getLoginUser()
						.getUserId(), app);
				added = false;
			}
		} finally {
			context.close();
		}
	}

	public String getSuccessScript() {
		String script = "var items={id:'" + app.getIdString() + "',desp:'"
				+ app.getAppCode() + "',columns:[";
		script += "'" + app.getIdString() + "',";
		script += "'" + app.getAppCode() + "',";
		script += "'" + app.getAppDesp() + "',";
		script += "'" + app.getAppCategory().getAppCategoryDesp() + "'";
		script += "]};";
		if (added)
			script += "top.ajax_table.addsuccess(items);";
		else
			script += "top.ajax_table.editsuccess(items);";
		return script;
	}

	public EditedApp getApp() {
		if (app == null) {
			app = new EditedApp();
		}
		return app;
	}

	public void setApp(EditedApp app) {
		this.app = app;
	}

	@Override
	public int getEditRight() {
		return UserRight.APP_EDIT;
	}

	@Override
	public int getQueryRight() {
		return UserRight.APP;
	}
}
