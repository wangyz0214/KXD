package kxd.engine.scs.admin.actions.device;

import javax.servlet.http.HttpServletResponse;

import kxd.engine.scs.admin.actions.BaseDatatableAction;
import kxd.engine.scs.admin.util.UserRight;
import kxd.net.HttpRequest;

public class QueryDeviceTypeAction extends BaseDatatableAction {

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
		return UserRight.DEVICETYPE_EDIT;
	}

	@Override
	public int getQueryRight() {
		return UserRight.DEVICETYPE;
	}

	@Override
	protected String doGetTableOptionHtml() {
		return super.doGetTableOptionHtml()
				+ " <span> [</span><a onclick='javascript:expandAlarms(arguments[0]);return false;' href='#'>告警代码</a> "
				+ " <a onclick='javascript:showAddAlarmDialog(arguments[0]);return false;' href='#'>添加</a><span>]</span>";
	}
}
