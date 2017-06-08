package kxd.engine.ui.tags.website;

import java.io.IOException;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;

public class DropdownTag extends InputTag {
	private static final long serialVersionUID = 1L;
	private String buttonWidth, popupClass, buttonClass, buttonText,
			buttonStyle;

	@Override
	public void release() {
		buttonWidth = null;
		popupClass = null;
		buttonClass = null;
		buttonText = null;
		buttonStyle = null;
		super.release();
	}

	@Override
	public String getType() {
		return "hidden";
	}

	public String getPopupClass() {
		return popupClass;
	}

	public void setPopupClass(String popupClass) {
		this.popupClass = popupClass;
	}

	public String getButtonClass() {
		return buttonClass;
	}

	public void setButtonClass(String buttonClass) {
		this.buttonClass = buttonClass;
	}

	public String getButtonText() {
		if (buttonText == null)
			buttonText = "";
		return buttonText;
	}

	public void setButtonText(String buttonText) {
		this.buttonText = buttonText;
	}

	public String getButtonStyle() {
		return buttonStyle;
	}

	public void setButtonStyle(String buttonStyle) {
		this.buttonStyle = buttonStyle;
	}

	@Override
	protected void outputStandardAttributes(JspWriter writer)
			throws IOException {
		super.outputStandardAttributes(writer);
		writeAttribute(writer, "name", getId());
	}

	protected String getDropdownClass() {
		return "DropdownButton";
	}

	@Override
	protected void outputStandardEvents(JspWriter writer) throws IOException {
	}

	@Override
	protected void outputInputContent(JspWriter writer) throws JspTagException,
			IOException {
		writer.write("<div id='" + getId() + "_button' ");
		writeAttribute(writer, "class", getButtonClass());
		writeAttribute(writer, "style", getButtonStyle());
		writeAttribute(writer, "title", getTitle());
		writer.write("></div>");
		super.outputInputContent(writer);
	}

	@Override
	public int endTag(JspWriter writer) throws JspTagException, IOException {
		int ret = super.endTag(writer);
		writeText(writer, "<script type='text/javascript'>var " + id + "=new "
				+ getDropdownClass() + "(null,{'id':'" + id
				+ "_button','inputid':'" + getId() + "'");
		writeButton(writer);
		writer.write(",'events':{");
		writeEvents(writer);
		writer.write("}");
		writeText(writer, ",'dropdown':{");
		writePopupWindow(writer);
		writeText(writer, "}});");
		writeText(writer, "</script>");
		return ret;
	}

	protected void writeEvents(JspWriter writer) throws IOException {
		if (this.getOnchange() != null)
			writer.write("'change':function(o,sel){" + getOnchange() + "}");
	}

	protected void writeButton(JspWriter writer) throws JspTagException,
			IOException {
		Object value = getValue();
		if (value != null)
			writeText(writer, ",value:'" + getValue() + "'");
		if (buttonText != null)
			writeText(writer, ",buttontext:'" + buttonText + "'");
		if (getButtonWidth() != null)
			writeText(writer, ",width:" + getButtonWidth());
	}

	protected void writePopupWindow(JspWriter writer) throws JspTagException,
			IOException {
		writeText(writer, "'contentclass':'" + getStyleClass() + "'");
		if (getTitle() != null)
			writeText(writer, ",'title':'" + getTitle() + "'");
		if (getPopupClass() != null)
			writeText(writer, ",'dropclass':'" + getPopupClass() + "'");
	}

	@Override
	public String getReadonly() {
		return "readonly";
	}

	public String getButtonWidth() {
		return buttonWidth;
	}

	public void setButtonWidth(String buttonWidth) {
		this.buttonWidth = buttonWidth;
	}

}
