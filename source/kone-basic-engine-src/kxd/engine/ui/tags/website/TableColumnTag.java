package kxd.engine.ui.tags.website;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import kxd.util.ListItem;

public class TableColumnTag extends BaseTagSupport {
	private static final long serialVersionUID = 1L;
	String header;
	String headerClass;
	String rowClass;
	String value;
	private boolean checkboxes;

	@Override
	public void release() {
		header = null;
		headerClass = null;
		rowClass = null;
		value = null;
		super.release();
	}

	@Override
	protected int endTag(JspWriter writer) throws JspException, IOException {
		writeText(writer, "</div>");
		return EVAL_PAGE;
	}

	@Override
	protected int startTag(JspWriter writer) throws JspException, IOException {
		TableTag parent = (TableTag) findAncestorWithClass(this, TableTag.class);
		if (parent != null) {
			writeText(writer, "<div");
			if (parent.headerRendering) {
				writeAttribute(writer, "class", headerClass);
				writeText(writer, ">");
				if (checkboxes) {
					writeText(writer, "<input type='checkbox' onclick=\"$('" 
							+ parent.getId()
							+ "').checkAll(this.checked);\"/>&nbsp;");
				}
				writeText(writer, header);
				return SKIP_BODY;
			} else {
				writeAttribute(writer, "id", parent.currentItemId + "_cell");
				writeAttribute(writer, "name", parent.currentItemId + "_cell");
				writeAttribute(writer, "class", rowClass);
				writeText(writer, ">");
				if (checkboxes) {
					ListItem<?> item = (ListItem<?>) parent.current;
					String checked = "";
					if (item.isChecked()) {
						checked = " checked='checked'";
						parent.checkvalues += item.getIdString() + ",";
					}
					writeText(writer,
							"<input id='" + item.getIdString()
									+ "' type='checkbox'" + checked
									+ " onclick=\"$('" + parent.getId()
									+ "_value').value=$('" + parent.getId()
									+ "').getCheckboxesValue();\"/>&nbsp;");
				}
				if (value != null)
					writeText(writer, value);
			}
		}
		return EVAL_BODY_INCLUDE;
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public String getHeaderClass() {
		return headerClass;
	}

	public void setHeaderClass(String headerClass) {
		this.headerClass = headerClass;
	}

	public String getRowClass() {
		return rowClass;
	}

	public void setRowClass(String rowClass) {
		this.rowClass = rowClass;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public boolean isCheckboxes() {
		return checkboxes;
	}

	public void setCheckboxes(boolean checkboxes) {
		this.checkboxes = checkboxes;
	}
}
