package kxd.engine.ui.tags.website;

import java.io.IOException;

import javax.servlet.jsp.JspWriter;

public class IframeTag extends BaseHtmlTag {
	private static final long serialVersionUID = 1L;
	private static String TAG_NAME = "iframe";
	private String src;
	private String frameBorder;
	private String height;
	private String marginHeight;
	private String marginWidth;
	private String scrolling;
	private String width;

	@Override
	protected String getTagName() {
		return TAG_NAME;
	}

	@Override
	protected void outputStandardAttributes(JspWriter writer)
			throws IOException {
		super.outputStandardAttributes(writer);
		writeUrlAttribute(writer, "src", getSrc());
		writeAttribute(writer, "frameBorder", getFrameBorder());
		writeAttribute(writer, "height", getHeight());
		writeAttribute(writer, "marginHeight", getMarginHeight());
		writeAttribute(writer, "marginWidth", marginWidth);
		writeAttribute(writer, "scrolling", scrolling);
		writeAttribute(writer, "width", width);
	}

	public String getSrc() {
		return src;
	}

	public void setSrc(String src) {
		this.src = src;
	}

	public String getFrameBorder() {
		return frameBorder;
	}

	public void setFrameBorder(String frameBorder) {
		this.frameBorder = frameBorder;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public String getMarginHeight() {
		return marginHeight;
	}

	public void setMarginHeight(String marginHeight) {
		this.marginHeight = marginHeight;
	}

	public String getMarginWidth() {
		return marginWidth;
	}

	public void setMarginWidth(String marginWidth) {
		this.marginWidth = marginWidth;
	}

	public String getScrolling() {
		return scrolling;
	}

	public void setScrolling(String scrolling) {
		this.scrolling = scrolling;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}
}
