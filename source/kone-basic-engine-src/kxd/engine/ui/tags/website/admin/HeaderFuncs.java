package kxd.engine.ui.tags.website.admin;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import kxd.engine.scs.admin.AdminSessionObject;
import kxd.engine.ui.tags.website.BaseTagSupport;

public class HeaderFuncs extends BaseTagSupport {
	private static final long serialVersionUID = 1L;

	@Override
	protected int startTag(JspWriter writer) throws JspException, IOException {
		return EVAL_BODY_INCLUDE;
	}

	@Override
	protected int endTag(JspWriter writer) throws JspException, IOException {
		String text = configParams.get("header-funchtml");
		HttpServletRequest req = (HttpServletRequest) pageContext.getRequest();
		AdminSessionObject session = (AdminSessionObject) req
				.getAttribute("sessionclient");
		if (session.isLogined())
			text = text.replace("${username}", session.getLoginUser()
					.getUserName());
		text = text.replace("${contextpath}", req.getContextPath());
		writer.write(text);
		return EVAL_PAGE;

	}

}
