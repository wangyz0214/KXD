package kxd.engine.ui.tags.website;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;

import kxd.util.ListItem;

public class DropmenuTag extends DivTag {
	private static final long serialVersionUID = 1L;
	private Object items;
	private String buttonText;
	private String nullItemText;
	private String onValueChanged;
	private int dropdownHeight;

	@Override
	public void release() {
		onValueChanged = null;
		items = null;
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
		String style = getStyleClass();
		setStyleClass("menubutton");
		super.outputStandardAttributes(writer);
		setStyleClass(style);
		writeAttribute(writer, "dropdownheight", getDropdownHeight());
	}

	@Override
	protected void outputValue(JspWriter writer) throws JspTagException,
			IOException {
		String value = null;
		if (getValue() != null)
			value = getValue().toString();
		String valueStr = "";
		if (value != null)
			valueStr = " value='" + value + "'";
		writeText(writer, "<input type='hidden' id='" + getId()
				+ "_value' name='" + getId() + "_value'" + valueStr);
		if (getOnValueChanged() != null) {
			writeAttribute(writer, "onchange", getOnValueChanged());
		}
		writeText(writer, "/>");
	}

	private void outputMenuItem(JspWriter writer, String id, String text,
			boolean checked, boolean selected, boolean disabled)
			throws JspTagException, IOException {
		writeText(writer, "<div class='menuitem'><div id='" + getId()
				+ "_item'  name='" + getId() + "_item' itemid='" + id
				+ "' itemtext='" + text + "'");
		writeAttribute(writer, "unselectable", "on");
		writeAttribute(writer, "style", "-moz-user-select:none");
		if (disabled) {
			writeAttribute(writer, "class", "menudisabled");
		} else {
			if (checked) {
				writeAttribute(writer, "class", "menuchecked");
				writeAttribute(writer, "checked", "true");
			} else if (selected) {
				writeAttribute(writer, "class", "menuselected");
				writeAttribute(writer, "selected", "true");
			} else
				writeAttribute(writer, "class", "menunormal");
		}
		writeText(writer, ">" + text + "</div></div>");
	}

	@Override
	public int startTag(JspWriter writer) throws JspTagException, IOException {
		writeText(writer, "<div class='" + getStyleClass() + "'>");
		return super.startTag(writer);
	}

	@Override
	public int endTag(JspWriter writer) throws JspTagException, IOException {
		writeText(
				writer,
				"<div class='buttonnormal'><div class='topline'></div>"
						+ "<div class='content' unselectable='on' style='-moz-user-select:none'>"
						+ "<div class='buttontext' id='" + getId() + "_text'>"
						+ (getButtonText() == null ? "" : getButtonText())
						+ "</div></div><div class='bottomline'></div></div>");
		writeText(writer, "</div><div id='" + getId()
				+ "_panel' class='menupanel' "
				+ "style='position:absolute;display:none'>"
				+ "<div class='buttonshadow'>"
				+ "</div><div class='menucontainer'><div id='" + getId()
				+ "_menu' class='menu'>");
		String selectText = getNullItemText();
		if (getItems() != null) {
			Object[] items = null;
			if (getItems() instanceof Object[]) {
				items = (Object[]) getItems();
			} else if (getItems() instanceof Collection<?>) {
				items = ((Collection<?>) getItems()).toArray();
			}
			if (getNullItemText() != null)
				outputMenuItem(writer, "", getNullItemText(), false, false,
						false);
			if (items != null) {
				for (int i = 0; i < items.length; i++) {
					Object o = items[i];
					if (!(o instanceof ListItem<?>))
						throw new IOException(
								"Elements in the list must be inherited from the ListItem object.");
					ListItem<?> li = (ListItem<?>) o;
					boolean selected = li.isSelected()
							|| li.getIdString().equals(value);
					if (selected)
						selectText = li.getText();
					outputMenuItem(writer, li.getIdString(), li.getText(), li
							.isChecked(), li.isSelected()
							|| li.getIdString().equals(value), li.isDisabled());
				}
			}
		}
		writeText(writer, "</div></div><div class='maskline'></div></div>");
		if (selectText != null)
			buttonText = selectText;
		int ret = super.endTag(writer);
		writeText(writer,
				"<script type='text/javascript'>new DropdownButton().initialize('"
						+ getId() + "');</script>");
		return ret;
	}

	public Object getItems() {
		return items;
	}

	public void setItems(Object items) {
		this.items = items;
	}

	public String getButtonText() {
		return buttonText;
	}

	public void setButtonText(String buttonText) {
		this.buttonText = buttonText;
	}

	public String getNullItemText() {
		return nullItemText;
	}

	public void setNullItemText(String nullItemText) {
		this.nullItemText = nullItemText;
	}

	public int getDropdownHeight() {
		return dropdownHeight;
	}

	public void setDropdownHeight(int dropdownHeight) {
		this.dropdownHeight = dropdownHeight;
	}

}
