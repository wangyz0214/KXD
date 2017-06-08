package kxd.engine.ui.tags.website;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

public class AjaxTableColumnTag extends BaseTagSupport {
	private static final long serialVersionUID = 1L;
	String header;
	String headerClass;
	String rowClass;
	String html;
	String attrname;

	@Override
	public void release() {
		super.release();
	}

	@Override
	protected int endTag(JspWriter writer) throws JspException, IOException {
		return EVAL_PAGE;
	}

	@Override
	protected int startTag(JspWriter writer) throws JspException, IOException {
		AjaxTableTag parent = (AjaxTableTag) findAncestorWithClass(this,
				AjaxTableTag.class);
		if (parent != null) {
			AjaxTableColumnData data = new AjaxTableColumnData();
			data.attrname = attrname;
			data.header = header;
			data.headerClass = headerClass;
			data.html = html;
			data.rowClass = rowClass;
			parent.columns.add(data);
		}
		return SKIP_BODY;
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

	public String getHtml() {
		return html;
	}

	public void setHtml(String html) {
		this.html = html;
	}

	public String getAttrname() {
		return attrname;
	}

	public void setAttrname(String attrname) {
		this.attrname = attrname;
	}
}
