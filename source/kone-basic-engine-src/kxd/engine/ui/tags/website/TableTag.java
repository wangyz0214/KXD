package kxd.engine.ui.tags.website;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;

import kxd.util.ListItem;

public class TableTag extends DivTag {
	private static final long serialVersionUID = 1L;
	private Collection<?> items;
	private String onValueChanged;
	private boolean headerRendered;
	private String emptyText;
	private String var;
	private boolean editEnabled;
	String checkvalues;

	public boolean isEditEnabled() {
		return editEnabled;
	}

	public void setEditEnabled(boolean editEnabled) {
		this.editEnabled = editEnabled;
	}

	@Override
	public void uninit() {
		checkvalues = null;
	}

	@Override
	public void init() {
		checkvalues = "";
		super.init();
	}

	@Override
	public void release() {
		emptyText = null;
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
		super.outputStandardAttributes(writer);
		writeAttribute(writer, "editenabled", editEnabled ? "true" : "false");
	}

	@Override
	protected void outputValue(JspWriter writer) throws JspTagException,
			IOException {
	}

	int currentIndex;
	Iterator<?> iterator;
	private String varindex;
	Object current;
	boolean headerRendering;
	String currentItemId;

	@Override
	public int startTag(JspWriter writer) throws JspTagException, IOException {
		int ret = super.startTag(writer);
		currentIndex = 0;
		varindex = var + "_index";
		if (getItems() == null || getItems().isEmpty()) {
			writeText(writer, "<div class='emptytable'>" + emptyText + "</div>");
			return SKIP_BODY;
		}
		if (this.headerRendered) { // 渲染表头
			headerRendering = true;
			writeText(writer, "<div id='" + getId()
					+ "_header' class='headerrow'>");
		} else {
			iterator = getItems().iterator();
			current = iterator.next();
			pageContext.setAttribute(var, current);
			pageContext.setAttribute(varindex, currentIndex);
			currentItemId = getId() + "_"
					+ ((ListItem<?>) current).getIdString();
			writeText(writer, "<div id='" + currentItemId
					+ "' class='row' onmouseout=\"this.className='row';\" "
					+ "onmouseover=\"this.className='row rowover';\">");
		}
		return ret;
	}

	@Override
	public int doAfterBody() throws JspException {
		try {
			JspWriter writer = pageContext.getOut();
			writeText(writer, "<div class='clear'></div></div>");
			if (this.headerRendering) {
				this.headerRendering = false;
				iterator = getItems().iterator();
				current = iterator.next();
				pageContext.setAttribute(var, current);
				pageContext.setAttribute(varindex, currentIndex);
				currentItemId = getId() + "_"
						+ ((ListItem<?>) current).getIdString();
				writeText(
						writer,
						"<div id='"
								+ currentItemId
								+ "' class='row' onmouseout=\"this.className = 'row';\" "
								+ "onmouseover=\"this.className='row rowover';\">");
				return EVAL_BODY_AGAIN;
			} else {
				pageContext.removeAttribute(var);
				pageContext.removeAttribute(varindex);
				if (iterator.hasNext()) {
					current = iterator.next();
					pageContext.setAttribute(var, current);
					pageContext.setAttribute(varindex, currentIndex);
					currentItemId = getId() + "_"
							+ ((ListItem<?>) current).getIdString();
					writeText(
							writer,
							"<div id='"
									+ currentItemId
									+ "' class='row' onmouseout=\"this.className = 'row';\" "
									+ "onmouseover=\"this.className='row rowover';\">");
					return EVAL_BODY_AGAIN;
				} else
					return SKIP_BODY;
			}
		} catch (IOException e) {
			throw new JspException(e);
		}
	}

	@Override
	public int endTag(JspWriter writer) throws JspTagException, IOException {
		int ret = super.endTag(writer);
		String valueStr = "";
		if (checkvalues != null)
			valueStr = " value='" + checkvalues + "'";
		writeText(writer, "<input type='hidden' id='" + getId()
				+ "_oldvalue' name='" + getId() + "_oldvalue'" + valueStr
				+ "/>");
		writeText(writer, "<input type='hidden' id='" + getId()
				+ "_value' name='" + getId() + "_value'" + valueStr);
		if (getOnValueChanged() != null) {
			writeAttribute(writer, "onchange", getOnValueChanged());
		}
		writeText(writer, "/>");
		return ret;
	}

	public Collection<?> getItems() {
		return items;
	}

	public void setItems(Collection<?> items) {
		this.items = items;
	}

	public boolean isHeaderRendered() {
		return headerRendered;
	}

	public void setHeaderRendered(boolean headerRendered) {
		this.headerRendered = headerRendered;
	}

	public String getEmptyText() {
		return emptyText;
	}

	public void setEmptyText(String emptyText) {
		this.emptyText = emptyText;
	}

	public String getVar() {
		return var;
	}

	public void setVar(String var) {
		this.var = var;
	}

}
