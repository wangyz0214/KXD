package kxd.engine.scs.admin;

import javax.servlet.http.HttpServletResponse;

import kxd.engine.ui.core.SessionObject;
import kxd.engine.ui.core.SessionObjectFactory;
import kxd.net.HttpRequest;

public class AdminSessionObjectFactory implements SessionObjectFactory {

	@Override
	public SessionObject newInstance(HttpRequest request,
			HttpServletResponse response) {
		return new AdminSessionObject(request, response);
	}

}
