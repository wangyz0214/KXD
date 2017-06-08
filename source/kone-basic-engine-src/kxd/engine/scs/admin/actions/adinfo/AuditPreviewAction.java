package kxd.engine.scs.admin.actions.adinfo;

import javax.servlet.http.HttpServletResponse;

import kxd.engine.scs.admin.util.UserRight;
import kxd.engine.ui.core.BaseAction;
import kxd.engine.ui.core.FacesError;
import kxd.net.HttpRequest;

public class AuditPreviewAction extends BaseAction {

	@Override
	public int getQueryRight() {
		return UserRight.AUDIT;
	}

	@Override
	public int getEditRight() {
		return 0;
	}

	@Override
	protected void processRight(boolean isFormSubmit, HttpRequest request,
			HttpServletResponse response) throws Throwable {
		if (!isQueryEnabled())
			throw new FacesError("error", "访问受限，您的权限不足!");
	}

	@Override
	protected String doExecute(boolean isFormSubmit, HttpRequest request,
			HttpServletResponse response) throws Throwable {
		return null;
	}

}
