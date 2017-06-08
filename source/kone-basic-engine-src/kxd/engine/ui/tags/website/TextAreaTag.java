package kxd.engine.ui.tags.website;

import java.io.IOException;

import javax.servlet.jsp.JspWriter;

public class TextAreaTag extends BaseHtmlTag {
	private static final long serialVersionUID = 1L;
	private static String TAG_NAME = "textarea";
	private String rows, cols;
	private Boolean disabled;
	private Boolean readonly;

	@Override
	public void release() {
		rows = null;
		cols = null;
		super.release();
	}

	@Override
	protected String getTagName() {
		return TAG_NAME;
	}

	@Override
	protected void outputStandardAttributes(JspWriter writer)
			throws IOException {
		writeAttribute(writer, "rows", rows);
		writeAttribute(writer, "cols", cols);
		writeAttribute(writer, "disabled",
				Boolean.TRUE.equals(disabled) ? "disabled" : null);
		writeAttribute(writer, "readonly",
				Boolean.TRUE.equals(readonly) ? "readonly" : null);
		writeAttribute(writer, "name", getId());
		super.outputStandardAttributes(writer);
	}

	public String getRows() {
		return rows;
	}

	public void setRows(String rows) {
		this.rows = rows;
	}

	public String getCols() {
		return cols;
	}

	public void setCols(String cols) {
		this.cols = cols;
	}

	public Boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(Boolean disabled) {
		this.disabled = disabled;
	}

	public Boolean isReadonly() {
		return readonly;
	}

	public void setReadonly(Boolean readonly) {
		this.readonly = readonly;
	}

}
