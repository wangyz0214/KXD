package kxd.engine.ui.tags.website;

import java.io.IOException;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;

abstract public class TaggedTagSupport extends BaseTagSupport {
	private static final long serialVersionUID = 1L;

	@Override
	public int startTag(JspWriter writer) throws JspTagException, IOException {
		writeText(writer, "<" + getTagName());
		outputAttributes(writer);
		writeText(writer, ">");
		outputValue(writer);
		return EVAL_BODY_INCLUDE;
	}

	@Override
	public int endTag(JspWriter writer) throws JspTagException, IOException {
		writeText(writer, "</" + getTagName() + ">");
		return EVAL_PAGE;
	}

	protected void outputAttributes(JspWriter writer) throws IOException {
		outputStandardAttributes(writer);
	}

	abstract protected void outputValue(JspWriter writer)
			throws JspTagException, IOException;

	abstract protected String getTagName();

	abstract protected void outputStandardAttributes(JspWriter writer)
			throws IOException;
}
