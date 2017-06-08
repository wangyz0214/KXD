package kxd.engine.ui.tags.website;

import java.io.IOException;

import javax.servlet.jsp.JspWriter;

public class DivTag extends BaseHtmlTag {
	private static final long serialVersionUID = 1L;
	private static String TAG_NAME = "DIV";
	private String align;

	@Override
	public void release() {
		align = null;
		super.release();
	}

	@Override
	protected String getTagName() {
		return TAG_NAME;
	}

	@Override
	protected void outputStandardAttributes(JspWriter writer)
			throws IOException {
		writeAttribute(writer, "align", align);
		super.outputStandardAttributes(writer);
	}

	public String getAlign() {
		return align;
	}

	public void setAlign(String align) {
		this.align = align;
	}

}
