package kxd.engine.scs.admin.actions.right;

import javax.servlet.http.HttpServletResponse;

import kxd.engine.scs.admin.actions.BaseDatatableAction;
import kxd.engine.scs.admin.util.UserRight;
import kxd.net.HttpRequest;

public class QueryRoleAction extends BaseDatatableAction {

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
		return UserRight.ROLE_EDIT;
	}

	@Override
	public int getQueryRight() {
		return UserRight.ROLE;
	}

	@Override
	protected String doGetTableOptionHtml() {
		return super.doGetTableOptionHtml()
				+ " <a id='editfuncs' href='#' onclick='showEditFuncsDialog(arguments[0]);return false;'>权限</a>";
	}
}
