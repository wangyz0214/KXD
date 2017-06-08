package kxd.engine.ui.tags.website;

import java.io.IOException;

import javax.servlet.jsp.JspWriter;

public class LabelTag extends BaseHtmlTag {
	private static final long serialVersionUID = 1L;
	private static String TAG_NAME = "LABEL";
	private String forElement;

	@Override
	protected String getTagName() {
		return TAG_NAME;
	}

	@Override
	protected void outputStandardAttributes(JspWriter writer)
			throws IOException {
		writeAttribute(writer, "for", getFor());
		super.outputStandardAttributes(writer);
	}

	public String getFor() {
		return forElement;
	}

	public void setFor(String forElement) {
		this.forElement = forElement;
	}
}
