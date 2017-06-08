package kxd.engine.scs.web.tags;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

public class ImageTag extends BaseTag {
	private static final long serialVersionUID = 1L;

	public String getSrc() {
		return src;
	}

	public void setSrc(String src) {
		this.src = src;
	}

	public String getAlt() {
		return alt;
	}

	public void setAlt(String alt) {
		this.alt = alt;
	}

	public String getStyleclass() {
		return styleclass;
	}

	public void setStyleclass(String styleclass) {
		this.styleclass = styleclass;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public String getAlign() {
		return align;
	}

	public void setAlign(String align) {
		this.align = align;
	}

	public String getIsmap() {
		return ismap;
	}

	public void setIsmap(String ismap) {
		this.ismap = ismap;
	}

	public String getUsemap() {
		return usemap;
	}

	public void setUsemap(String usemap) {
		this.usemap = usemap;
	}

	private String src;

	private String alt;

	private String styleclass;

	private String style;

	private String height;

	private String width;

	private String align;

	private String ismap;

	private String usemap;

	@Override
	public int doStartTag() throws JspException {
		return super.doStartTag();
	}

	@Override
	public int doEndTag() throws JspException {
		try {
			JspWriter writer = pageContext.getOut();
			writer.write("<img");
			writeUrlAttribute(writer, "src", src);
			writeAttribute(writer, "alt", alt);
			writeAttribute(writer, "class", styleclass);
			writeAttribute(writer, "style", style);
			writeAttribute(writer, "height", height);
			writeAttribute(writer, "width", width);
			writeAttribute(writer, "align", align);
			writeAttribute(writer, "ismap", ismap);
			writeAttribute(writer, "usemap", usemap);
			writer.write("/>");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return EVAL_PAGE;
	}
}
