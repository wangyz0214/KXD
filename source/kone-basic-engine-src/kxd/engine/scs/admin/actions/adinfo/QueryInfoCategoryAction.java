package kxd.engine.scs.admin.actions.adinfo;

import javax.servlet.http.HttpServletResponse;

import kxd.engine.scs.admin.actions.BaseDatatableAction;
import kxd.engine.scs.admin.util.UserRight;
import kxd.net.HttpRequest;

public class QueryInfoCategoryAction extends BaseDatatableAction {

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
		return UserRight.INFOCATEGORY_EDIT;
	}

	@Override
	public int getQueryRight() {
		return UserRight.INFOCATEGORY;
	}
}
