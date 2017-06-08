package kxd.engine.scs.admin.actions.term;

import javax.servlet.http.HttpServletResponse;

import kxd.engine.scs.admin.actions.BaseDatatableAction;
import kxd.engine.scs.admin.util.UserRight;
import kxd.net.HttpRequest;

public class QueryTermTypeAction extends BaseDatatableAction {

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
		return UserRight.TERMTYPE_EDIT;
	}

	@Override
	public int getQueryRight() {
		return UserRight.TERMTYPE;
	}

	@Override
	protected String doGetTableOptionHtml() {
		return super.doGetTableOptionHtml()
				+ " <span> [</span><a onclick='javascript:expandDevices(arguments[0]);return false;' href='#'>设备</a> "
				+ " <a onclick='javascript:showAddDevicesDialog(arguments[0]);return false;' href='#'>添加</a><span>]</span>";
	}
}
