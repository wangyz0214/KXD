package kxd.engine.scs.admin;

import javax.servlet.http.HttpServletResponse;

import kxd.net.HttpRequest;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public interface AdminTradeDriver {
	public void execute(AdminSessionObject session, HttpRequest request,
			HttpServletResponse response, Document xmlDoc, Element content,
			Element result) throws Throwable;
}
