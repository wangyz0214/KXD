package kxd.engine.ui.tags.website;

import java.io.IOException;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;

public class PageSelectorTag extends DivTag {
	private static final long serialVersionUID = 1L;
	private String recordDesp;
	private int visiblePages = 5;
	private int pages, recordCount;

	@Override
	public void release() {
		recordDesp = null;
		super.release();
	}

	@Override
	protected void outputStandardAttributes(JspWriter writer)
			throws IOException {
		super.outputStandardAttributes(writer);
		writeAttribute(writer, "pages", pages);
	}

	private void encodePageElement(JspWriter writer, String formId,
			boolean disabled, String text, int page, String styleClass)
			throws IOException {
		if (disabled) {
			writeText(writer, "<span");
		} else {
			writeText(writer, "<a");
			writeAttribute(writer, "href", "#");
			String click = "var form=document.forms['" + formId
					+ "'];form['page'].value='" + (page - 1)
					+ "';form.submit();return false";
			writeAttribute(writer, "onclick", click);
		}
		writeAttribute(writer, "class", styleClass);
		writeText(writer, ">" + text);
		if (disabled) {
			writeText(writer, "</span>");
		} else
			writeText(writer, "</a>");
	}

	private void encodeSpanItem(JspWriter writer, String text, String styleClass)
			throws IOException {
		writeText(writer, "<span");
		writeAttribute(writer, "class", styleClass);
		writeText(writer, ">" + text + "</span>");
	}

	@Override
	protected void outputValue(JspWriter writer) throws JspTagException,
			IOException {
		int pages = getPages();
		int page = Integer.valueOf(getValue().toString()) + 1;
		Integer visiblePages = getVisiblePages();
		if (visiblePages == null)
			visiblePages = 3;
		else {
			visiblePages = visiblePages - 2;
			if (visiblePages < 3)
				visiblePages = 3;
		}
		String currentPageStyleClass = "current";
		if (pages > 0) {
			String ellipsePageStyleClass = "ellipse";
			String linkPageStyleClass = "link";
			String disabledPageStyleClass = "disabled";
			int start = 1;
			int end = pages;
			if (pages > visiblePages + 2) { // 需要一部分省略
				if (page > 2) {
					start = page - visiblePages / 2;
					if (start < 1)
						start = 1;
				}
				end = start + visiblePages;
				if (end > pages) {
					start = pages - visiblePages;
					end = pages;
				}
			}
			String formId = getForm().getId();
			writeText(writer, "<input type='hidden' name='page' value='"
					+ getValue() + "'/>");
			writeText(writer, "<input type='hidden' name='recordcount' value='"
					+ getRecordCount() + "'/>");
			if (page > 1)
				encodePageElement(writer, formId, false, "< Prev", page - 1,
						linkPageStyleClass);
			else
				encodePageElement(writer, formId, true, "< Prev", page - 1,
						disabledPageStyleClass);
			if (start > 1) {
				encodePageElement(writer, formId, false, "1", 1,
						linkPageStyleClass);
				encodeSpanItem(writer, " ... ", ellipsePageStyleClass);
			}
			for (int i = start; i <= end; i++) {
				String styleClass = (i == page) ? currentPageStyleClass
						: linkPageStyleClass;
				encodePageElement(writer, formId, i == page, Integer
						.toString(i), i, styleClass);
			}
			if (end < pages) {
				encodeSpanItem(writer, " ... ", ellipsePageStyleClass);
				encodePageElement(writer, formId, false, Integer
						.toString(pages), pages, linkPageStyleClass);
			}
			if (page < pages)
				encodePageElement(writer, formId, false, "Next >", page + 1,
						linkPageStyleClass);
			else
				encodePageElement(writer, formId, true, "Next >", pages,
						disabledPageStyleClass);

			String str = recordDesp;
			if (str != null) {
				encodeSpanItem(writer, "&nbsp; 共 <span id='recordcounttext'>"
						+ getRecordCount() + "</span> " + str, null);
			}
		}
	}

	public String getRecordDesp() {
		return recordDesp;
	}

	public void setRecordDesp(String recordDesp) {
		this.recordDesp = recordDesp;
	}

	public int getVisiblePages() {
		return visiblePages;
	}

	public void setVisiblePages(int visiblePages) {
		this.visiblePages = visiblePages;
	}

	public int getPages() {
		return pages;
	}

	public void setPages(int pages) {
		this.pages = pages;
	}

	public int getRecordCount() {
		return recordCount;
	}

	public void setRecordCount(int recordCount) {
		this.recordCount = recordCount;
	}
}
