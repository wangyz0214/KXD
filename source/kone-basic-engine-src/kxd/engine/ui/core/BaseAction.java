package kxd.engine.ui.core;

import javax.servlet.http.HttpServletResponse;

import kxd.net.HttpRequest;

abstract public class BaseAction implements FacesAction {
	protected SessionObject session;
	String command;
	private boolean queryEnabled;
	private boolean editEnabled;

	abstract public int getQueryRight();

	abstract public int getEditRight();

	protected boolean needLogin() {
		return true;
	}

	abstract protected void processRight(boolean isFormSubmit,
			HttpRequest request, HttpServletResponse response) throws Throwable;

	protected void setParameters(boolean isFormSubmit, HttpRequest request)
			throws Throwable {
		if (isFormSubmit)
			command = request.getParameterDef("command", null);
	}

	abstract protected String doExecute(boolean isFormSubmit,
			HttpRequest request, HttpServletResponse response) throws Throwable;

	@Override
	public String execute(boolean isFormSubmit, HttpRequest request,
			HttpServletResponse response) throws Throwable {
		session = (SessionObject) request.getAttribute("sessionclient");
		if (needLogin()) {
			if (!session.isLogined())
				return "login";
			queryEnabled = session.hasRight(getQueryRight());
			editEnabled = session.hasRight(getEditRight());
		} else {
			queryEnabled = true;
			editEnabled = true;
		}
		processRight(isFormSubmit, request, response);
		setParameters(isFormSubmit, request);
		return doExecute(isFormSubmit, request, response);
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public SessionObject getSession() {
		return session;
	}

	public boolean isQueryEnabled() {
		return queryEnabled;
	}

	public void setQueryEnabled(boolean queryEnabled) {
		this.queryEnabled = queryEnabled;
	}

	public boolean isEditEnabled() {
		return editEnabled;
	}

	public void setEditEnabled(boolean editEnabled) {
		this.editEnabled = editEnabled;
	}
}
