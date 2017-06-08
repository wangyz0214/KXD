package kxd.engine.ui.tags.website.admin;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import kxd.engine.ui.tags.website.BaseTagSupport;

public class Fotter extends BaseTagSupport {
	private static final long serialVersionUID = 1L;

	@Override
	protected int startTag(JspWriter writer) throws JspException, IOException {
		return EVAL_BODY_INCLUDE;
	}

	@Override
	protected int endTag(JspWriter writer) throws JspException, IOException {
		String text = configParams.get("footer-html");
		writer.write(text);
		return EVAL_PAGE;

	}

}
