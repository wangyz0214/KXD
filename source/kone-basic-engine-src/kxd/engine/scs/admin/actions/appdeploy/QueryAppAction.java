package kxd.engine.scs.admin.actions.appdeploy;

import javax.servlet.http.HttpServletResponse;

import kxd.engine.scs.admin.actions.BaseDatatableAction;
import kxd.engine.scs.admin.util.UserRight;
import kxd.net.HttpRequest;

public class QueryAppAction extends BaseDatatableAction {

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
		return UserRight.APP_EDIT;
	}

	@Override
	public int getQueryRight() {
		return UserRight.APP;
	}

	@Override
	protected String doGetTableOptionHtml() {
		return super.doGetTableOptionHtml()
				+ " <span> [</span><a onclick='javascript:expandFiles(arguments[0]);return false;' href='#'>文件</a> "
				+ " <a onclick='javascript:showAddFileDialog(arguments[0]);return false;' href='#'>添加</a><span>]</span>";
	}

}
