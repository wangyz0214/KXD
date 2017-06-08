package kxd.engine.ui.core;

import javax.servlet.http.HttpServletResponse;

import kxd.net.HttpRequest;

public interface FacesAction {
	String execute(boolean isFormSubmit, HttpRequest request,
			HttpServletResponse response) throws Throwable;
}
