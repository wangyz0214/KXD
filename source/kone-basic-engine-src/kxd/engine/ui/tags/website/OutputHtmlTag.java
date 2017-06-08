package kxd.engine.ui.tags.website;

import java.io.IOException;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;

public class OutputHtmlTag extends BaseTagSupport {
	private static final long serialVersionUID = 1L;
	Object value;

	@Override
	public int startTag(JspWriter writer) throws JspTagException, IOException {
		writer.write(value.toString());
		return EVAL_BODY_INCLUDE;
	}

	@Override
	public int endTag(JspWriter writer) throws JspTagException, IOException {
		return EVAL_PAGE;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

}
