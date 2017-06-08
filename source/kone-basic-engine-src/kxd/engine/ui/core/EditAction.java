package kxd.engine.ui.core;

import javax.servlet.http.HttpServletResponse;

import kxd.net.HttpRequest;

abstract public class EditAction extends BaseAction {
	protected void processRight(boolean isFormSubmit, HttpRequest request,
			HttpServletResponse response) throws Throwable {
		if (!isEditEnabled())
			throw new FacesError("error", "访问受限，您的权限不足!");
	}
}
