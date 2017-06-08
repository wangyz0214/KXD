package kxd.engine.ui.tags.website;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;

class AjaxTableColumnData {
	String header;
	String headerClass;
	String rowClass;
	String html;
	String attrname;
}

public class AjaxTableTag extends DivTag {
	private static final long serialVersionUID = 1L;
	private String onValueChanged;
	private boolean headerRendered = true;
	private String emptyText;
	private boolean editEnabled;
	private String loadurl;
	private String onloadbefore;
	private String onloadend;
	private String funcdesp;
	private String funcname;
	private String extflag; // 为每行数据添加一个附加检查标识
	private String onrowadded; // 当增加一行数据时触发
	private String onrowedited; // 当修改一行数据时触发
	private String onrowdeleted; // 当删除一行数据时触发
	private String dialogHeight, dialogWidth;
	private String service;
	private String pageControlId;
	List<AjaxTableColumnData> columns = new CopyOnWriteArrayList<AjaxTableColumnData>();

	public boolean isEditEnabled() {
		return editEnabled;
	}

	public void setEditEnabled(boolean editEnabled) {
		this.editEnabled = editEnabled;
	}

	public String getService() {
		if (service == null)
			service = "default";
		return service;
	}

	public void setService(String executor) {
		this.service = executor;
	}

	public String getPageControlId() {
		return pageControlId;
	}

	public void setPageControlId(String pageControlId) {
		this.pageControlId = pageControlId;
	}

	@Override
	public void init() {
		columns.clear();
		super.init();
	}

	@Override
	public void uninit() {
		headerRendered = true;
		pageControlId = null;
		columns.clear();
		super.uninit();
	}

	@Override
	public void release() {
		onrowadded = null;
		emptyText = null;
		onValueChanged = null;
		onloadbefore = null;
		onloadend = null;
		funcdesp = null;
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

	@Override
	public int startTag(JspWriter writer) throws JspTagException, IOException {
		int r = super.startTag(writer);
		if (getPageControlId() == null) {
			pageControlId = getId() + "_page";
			writeText(writer, "<div id='" + pageControlId + "' class='pageselector'></div>");
		}
		writeText(writer, "<div id='" + getId() + "_c" + "'></div>");
		return r;
	}

	@Override
	public int endTag(JspWriter writer) throws JspTagException, IOException {
		int ret = super.endTag(writer);
		FormTag form = getForm();
		if (form == null)
			return ret;
		String table = getId() + "_table";
		writeText(writer, "<script type='text/javascript'>");
		writeText(writer, "var ps={formid:'" + form.getId() + "',");

		writeText(writer, "pagecontrolid:'" + getPageControlId() + "',");
		if (getOnloadbefore() != null)
			writeText(writer, "onloadbefore:function(){" + getOnloadbefore()
					+ "},");
		if (getOnloadend() != null)
			writeText(writer, "onloadend:function(){" + getOnloadend() + "},");
		if (loadurl != null)
			writeText(writer, "loadurl:'" + loadurl + "',");
		if (extflag != null)
			writeText(writer, "extflag:'" + extflag + "',");
		if (onrowadded != null)
			writeText(writer, "onrowadded:function(r){" + onrowadded + "},");
		if (onrowedited != null)
			writeText(writer, "onrowedited:function(r){" + onrowedited + "},");
		if (onrowdeleted != null)
			writeText(writer, "onrowdeleted:function(r){" + onrowdeleted + "},");
		writeText(writer, "id:'" + getId() + "',");
		if (getDialogWidth() != null)
			writeText(writer, "dialogWidth:" + getDialogWidth() + ",");
		if (getDialogHeight() != null)
			writeText(writer, "dialogHeight:" + getDialogHeight() + ",");
		writeText(writer, "funcdesp:'" + getFuncdesp() + "',");
		writeText(writer, "funcname:'" + getFuncname() + "',");
		writeText(writer, "service:'" + getService() + "',");
		writeText(writer, "styleClass:'" + getStyleClass() + "',");
		writeText(writer, "emptyText:'" + getEmptyText() + "',");
		writeText(writer, "headRendered:"
				+ (isHeaderRendered() ? "true" : "false") + ",");
		writeText(writer, "columns:[");
		Iterator<AjaxTableColumnData> it = this.columns.iterator();
		while (it.hasNext()) {
			AjaxTableColumnData data = it.next();
			writeText(writer, "{header:\"" + data.header + "\",headerClass:\""
					+ data.headerClass + "\",rowClass:\"" + data.rowClass
					+ "\",");
			if (data.html != null)
				writeText(writer, "html:\"" + data.html + "\"");
			else
				writeText(writer, "attrname:\"" + data.attrname + "\"");
			if (it.hasNext())
				writeText(writer, "},");
			else
				writeText(writer, "}");
		}
		writeText(writer, "]};");
		writeText(writer, "var " + table + "= new AjaxDataTable(ps);");
		writeText(writer, "document.forms['" + form.getId()
				+ "'].onsubmit=function(){" + table
				+ ".load(true);return false;};");
		writeText(writer, getId() + "_table.load(true);</script>");
		return ret;
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

	public String getLoadurl() {
		return loadurl;
	}

	public void setLoadurl(String loadurl) {
		this.loadurl = loadurl;
	}

	public String getOnloadbefore() {
		return onloadbefore;
	}

	public void setOnloadbefore(String onloadbefore) {
		this.onloadbefore = onloadbefore;
	}

	public String getOnloadend() {
		return onloadend;
	}

	public void setOnloadend(String onloadend) {
		this.onloadend = onloadend;
	}

	public String getFuncdesp() {
		return funcdesp;
	}

	public void setFuncdesp(String funcdesp) {
		this.funcdesp = funcdesp;
	}

	public String getFuncname() {
		return funcname;
	}

	public void setFuncname(String funcname) {
		this.funcname = funcname;
	}

	public String getExtflag() {
		return extflag;
	}

	public void setExtflag(String extflag) {
		this.extflag = extflag;
	}

	public String getOnrowadded() {
		return onrowadded;
	}

	public void setOnrowadded(String onrowadded) {
		this.onrowadded = onrowadded;
	}

	public String getOnrowedited() {
		return onrowedited;
	}

	public void setOnrowedited(String onrowedited) {
		this.onrowedited = onrowedited;
	}

	public String getOnrowdeleted() {
		return onrowdeleted;
	}

	public void setOnrowdeleted(String onrowdeleted) {
		this.onrowdeleted = onrowdeleted;
	}

	public String getDialogHeight() {
		return dialogHeight;
	}

	public void setDialogHeight(String dialogHeight) {
		this.dialogHeight = dialogHeight;
	}

	public String getDialogWidth() {
		return dialogWidth;
	}

	public void setDialogWidth(String dialogWidth) {
		this.dialogWidth = dialogWidth;
	}
}
