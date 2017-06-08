package kxd.engine.ui.tags.website;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;

abstract public class BaseHtmlTag extends TaggedTagSupport {
	private static final long serialVersionUID = 1L;
	String title;
	String lang;
	String xmllang;
	String dir;
	String style;
	String styleClass;
	String onclick;
	String ondblclick;
	String onkeydown;
	String onkeyup;
	String onmousedown;
	String onkeypress;
	String onmousemove;
	String onmouseout;
	String onmouseup;
	String onmouseover;
	String customAttributes;
	Object value;
	boolean unselectable;

	@Override
	public void release() {
		customAttributes = null;
		title = null;
		lang = null;
		xmllang = null;
		dir = null;
		style = null;
		styleClass = null;
		onclick = null;
		ondblclick = null;
		onkeydown = null;
		onkeyup = null;
		onmousedown = null;
		onkeypress = null;
		onmousemove = null;
		onmouseout = null;
		onmouseup = null;
		onmouseover = null;
		super.release();
	}

	@Override
	public void uninit() {
		value = null;
		super.uninit();
	}

	@Override
	protected void outputValue(JspWriter writer) throws JspTagException,
			IOException {
		if (value != null)
			writeText(writer, value.toString());
	}

	@Override
	protected void outputStandardAttributes(JspWriter writer)
			throws IOException {
		writeAttribute(writer, "id", getId());
		writeAttribute(writer, "title", getTitle());
		writeAttribute(writer, "lang", getLang());
		writeAttribute(writer, "xml:lang", getXmllang());
		writeAttribute(writer, "dir", getDir());
		writeAttribute(writer, "class", getStyleClass());
		writeAttribute(writer, "onclick", getOnclick());
		writeAttribute(writer, "ondblclick", getOndblclick());
		writeAttribute(writer, "onkeydown", getOnkeydown());
		writeAttribute(writer, "onkeyup", getOnkeyup());
		writeAttribute(writer, "onmousedown", getOnmousedown());
		writeAttribute(writer, "onkeypress", getOnkeypress());
		writeAttribute(writer, "onmousemove", getOnmousemove());
		writeAttribute(writer, "onmouseout", getOnmouseout());
		writeAttribute(writer, "onmouseup", getOnmouseup());
		writeAttribute(writer, "onmouseover", getOnmouseover());
		if (unselectable) {
			writeAttribute(writer, "unselectable", "on");
			String style = "-moz-user-select:none;";
			if (getStyle() != null)
				style += getStyle();
			writeAttribute(writer, "style", style);
		} else
			writeAttribute(writer, "style", getStyle());
		if (customAttributes != null && customAttributes.trim().length() > 0)
			writer.write(" " + customAttributes);
	}

	public String getTitle() {
		if (title == null) {
		}
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public String getXmllang() {
		return xmllang;
	}

	public void setXmllang(String xmllang) {
		this.xmllang = xmllang;
	}

	public String getDir() {
		return dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public String getStyleClass() {
		return styleClass;
	}

	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}

	public String getOnclick() {
		return onclick;
	}

	public void setOnclick(String onclick) {
		this.onclick = onclick;
	}

	public String getOndblclick() {
		return ondblclick;
	}

	public void setOndblclick(String ondblclick) {
		this.ondblclick = ondblclick;
	}

	public String getOnkeydown() {
		return onkeydown;
	}

	public void setOnkeydown(String onkeydown) {
		this.onkeydown = onkeydown;
	}

	public String getOnkeyup() {
		return onkeyup;
	}

	public void setOnkeyup(String onkeyup) {
		this.onkeyup = onkeyup;
	}

	public String getOnmousedown() {
		return onmousedown;
	}

	public void setOnmousedown(String onmousedown) {
		this.onmousedown = onmousedown;
	}

	public String getOnkeypress() {
		return onkeypress;
	}

	public void setOnkeypress(String onkeypress) {
		this.onkeypress = onkeypress;
	}

	public String getOnmousemove() {
		return onmousemove;
	}

	public void setOnmousemove(String onmousemove) {
		this.onmousemove = onmousemove;
	}

	public String getOnmouseout() {
		return onmouseout;
	}

	public void setOnmouseout(String onmouseout) {
		this.onmouseout = onmouseout;
	}

	public String getOnmouseup() {
		return onmouseup;
	}

	public void setOnmouseup(String onmouseup) {
		this.onmouseup = onmouseup;
	}

	public String getOnmouseover() {
		return onmouseover;
	}

	public void setOnmouseover(String onmouseover) {
		this.onmouseover = onmouseover;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) throws JspException {
		this.value = value;
	}

	public boolean isUnselectable() {
		return unselectable;
	}

	public void setUnselectable(boolean unselectable) {
		this.unselectable = unselectable;
	}

	public String getCustomAttributes() {
		return customAttributes;
	}

	public void setCustomAttributes(String customAttributes) {
		this.customAttributes = customAttributes;
	}
}
