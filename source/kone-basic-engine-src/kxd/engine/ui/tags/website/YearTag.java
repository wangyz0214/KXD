package kxd.engine.ui.tags.website;

import java.io.IOException;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;

import kxd.util.DateTime;

public class YearTag extends DivTag {
	private static final long serialVersionUID = 1L;
	private String onValueChanged;

	@Override
	public void release() {
		onValueChanged = null;
		super.release();
	}

	public String getOnValueChanged() {
		return onValueChanged;
	}

	public void setOnValueChanged(String onValueChanged) {
		this.onValueChanged = onValueChanged;
	}

	@Override
	protected void outputStandardAttributes(JspWriter writer)
			throws IOException {
		super.outputStandardAttributes(writer);
	}

	@Override
	protected void outputValue(JspWriter writer) throws JspTagException,
			IOException {
		String value;
		int year = new DateTime().getYear();
		if (getValue() != null)
			value = getValue().toString();
		else
			value = Integer.toString(year);
		String valueStr = "";
		if (value != null)
			valueStr = " value='" + value + "'";
		writeText(writer, "<input type='hidden' id='" + getId()
				+ "_value' name='" + getId() + "_value'" + valueStr);
		if (getOnValueChanged() != null) {
			writeAttribute(writer, "onchange", getOnValueChanged());
		}
		writeText(writer, "/><div id='" + getId()
				+ "_panel' class='menupanel' "
				+ "style='position:absolute;display:none'>"
				+ "<div class='buttonshadow'>"
				+ "</div><div class='menucontainer'><div id='" + getId()
				+ "_menu' class='menu'>");
		year -= 12;
		for (int j = 0; j < 5; j++) {
			writeText(writer, "<div class='row'>");
			for (int i = 0; i < 5; i++) {
				String c = i == 0 ? "yearcell1" : "yearcell";
				writeText(writer, "<div class='" + c + "' id='" + getId()
						+ "_cell" + j + "_" + i + "'>" + year + "</div>");
				year++;
			}
			writeText(writer, "<div class='clear'></div></div>");
		}
	}

	@Override
	public int endTag(JspWriter writer) throws JspTagException, IOException {
		String value;
		if (getValue() != null)
			value = getValue().toString();
		else
			value = new DateTime().format("yyyy-MM");
		writeText(
				writer,
				"</div></div><div class='maskline'></div></div><div class='buttonnormal'><div class='topline'></div>"
						+ "<div class='content' unselectable='on' style='-moz-user-select:none'>"
						+ "<div class='buttontext' id='"
						+ getId()
						+ "_text'>"
						+ value
						+ "</div></div><div class='bottomline'></div></div>");
		int ret = super.endTag(writer);
		writeText(writer,
				"<script type='text/javascript'>new YearDropdownButton().initialize('"
						+ getId() + "');</script>");
		return ret;
	}
}
