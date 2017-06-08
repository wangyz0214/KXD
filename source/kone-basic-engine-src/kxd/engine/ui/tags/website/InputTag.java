package kxd.engine.ui.tags.website;

import java.io.IOException;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;

public class InputTag extends BaseHtmlTag {
	private static final long serialVersionUID = 1L;
	private static String TAG_NAME = "INPUT";

	@Override
	protected String getTagName() {
		return TAG_NAME;
	}

	protected void outputLabel(JspWriter writer) throws JspTagException,
			IOException {
		if (getLabel() != null) {
			writeText(writer, "<div ");
			writeAttribute(writer, "for", getName());
			writeAttribute(writer, "id", getName() + "_label");
			writeAttribute(writer, "class", getLabelClass());
			int minlen = 0;
			if (getMinlength() != null)
				minlen = Integer.valueOf(getMinlength());
			if (minlen <= 0)
				writeText(writer, ">" + getLabel() + ": </div>");
			else
				writeText(writer, ">" + getLabel() + "(*): </div>");
		}
	}

	@Override
	public int startTag(JspWriter writer) throws JspTagException, IOException {
		outputLabel(writer);
		return super.startTag(writer);
	}

	protected void outputStandardEvents(JspWriter writer) throws IOException {
		writeAttribute(writer, "onchange", getOnchange());
	}

	@Override
	protected void outputStandardAttributes(JspWriter writer)
			throws IOException {
		writeAttribute(writer, "regexp", getRegexp());
		writeAttribute(writer, "minlength", getMinlength());
		if (getLabel() != null) {
			writeAttribute(writer, "label", getLabel());
			writeAttribute(writer, "errorprompt", getErrorPrompt());
			writeAttribute(writer, "errorpromptelement",
					getErrorPromptElement());
			writeAttribute(writer, "prompttext", getPromptText());
			writeAttribute(writer, "promptelement", getPromptElement());
			writeAttribute(writer, "promptclass", getPromptClass());
			writeAttribute(writer, "successpromptclass",
					getSuccessPromptClass());
			writeAttribute(writer, "errorpromptclass", getErrorPromptClass());
		}
		if (getName() != null)
			writeAttribute(writer, "name", getName());
		else
			writeAttribute(writer, "name", getId());
		writeAttribute(writer, "type", getType());
		writeAttribute(writer, "accept", getAccept());
		writeAttribute(writer, "align", getAlign());
		writeAttribute(writer, "alt", getAlt());
		writeAttribute(writer, "checked", getChecked());
		writeAttribute(writer, "disabled", getDisabled());
		writeAttribute(writer, "maxlength", getMaxlength());
		writeAttribute(writer, "readonly", getReadonly());
		writeAttribute(writer, "size", getSize());
		writeAttribute(writer, "usemap", getUsemap());
		writeUrlAttribute(writer, "url", getUrl());
		writeAttribute(writer, "tabindex", getTabindex());
		writeAttribute(writer, "accesskey", getAccesskey());
		writeAttribute(writer, "value", getValue());
		outputStandardEvents(writer);
		super.outputStandardAttributes(writer);
	}

	@Override
	protected void outputValue(JspWriter writer) throws JspTagException,
			IOException {// 不做任何事
	}

	static final private String FOCUSVAR = "focusinputrendered";

	protected void outputInputContent(JspWriter writer) throws JspTagException,
			IOException {
	}

	@Override
	public int endTag(JspWriter writer) throws JspTagException, IOException {
		int ret = super.endTag(writer);
		outputInputContent(writer);
		FormTag form = getForm();
		if (form != null && getName() != null) {
			if (getErrorPrompt() != null || getPromptText() != null) {
				if (getErrorPromptElement() == null
						|| getPromptElement() == null) {
					writeText(writer, "<div id='" + getName() + "_prompt'");
					writeAttribute(writer, "class", getPromptClass());
					writeText(writer, ">");
					writeText(writer, getPromptText());
					writeText(writer, "</div>");
				}
			}
			writeText(writer,
					"<script type='text/javascript'>input=$('" + form.getId()
							+ "')['" + getName() + "'];");
			if (getOnsubmit() != null)
				writeText(writer, "input.onsubmit=function(){" + getOnsubmit()
						+ "}");
			if (!"hidden".equalsIgnoreCase(getType())
					&& !"disabled".equalsIgnoreCase(getDisabled())
					&& pageContext.getRequest().getAttribute(FOCUSVAR) == null) {
				writeText(writer, "try{input.focus();}catch(e){}");
				pageContext.getRequest().setAttribute(FOCUSVAR, "true");
			}
			writeText(writer, "</script>");
		} else if (getId() != null && !"hidden".equalsIgnoreCase(getType())
				&& !"disabled".equalsIgnoreCase(getDisabled())) {
			if (pageContext.getRequest().getAttribute(FOCUSVAR) == null) {
				writeText(writer, "<script type='text/javascript'>try{$('"
						+ getId() + "').focus();}catch(e){}</script>");
				pageContext.getRequest().setAttribute(FOCUSVAR, "true");
			}
		}
		return ret;
	}

	String errorPromptElement;
	String errorPrompt;
	String errorPromptClass;
	String successPromptClass;
	String promptElement;
	String promptText;
	String promptClass;
	String regexp;
	String onsubmit;
	String label;
	String labelClass;
	String type;
	String name;
	String accept;
	String align;
	String alt;
	String checked;
	String disabled;
	String minlength;
	String maxlength;
	String readonly;
	String size;
	String usemap;
	String url;
	String tabindex;
	String accesskey;
	String onchange;

	@Override
	public void init() {
		if (errorPromptClass == null)
			errorPromptClass = "error";
		if (successPromptClass == null)
			successPromptClass = "success";
		if (promptClass == null)
			promptClass = "prompt";
		if (labelClass == null)
			labelClass = "label";
		super.init();
	}

	@Override
	public void release() {
		onsubmit = null;
		errorPromptElement = null;
		errorPrompt = null;
		errorPromptClass = null;
		successPromptClass = null;
		promptElement = null;
		promptText = null;
		regexp = null;
		label = null;
		labelClass = null;
		promptClass = null;
		type = null;
		name = null;
		accept = null;
		align = null;
		alt = null;
		onchange = null;
		checked = null;
		disabled = null;
		minlength = null;
		maxlength = null;
		readonly = null;
		size = null;
		usemap = null;
		url = null;
		tabindex = null;
		accesskey = null;
		super.release();
	}

	public String getOnchange() {
		return onchange;
	}

	public void setOnchange(String onchange) {
		this.onchange = onchange;
	}

	public String getSuccessPromptClass() {
		return successPromptClass;
	}

	public void setSuccessPromptClass(String successPromptClass) {
		this.successPromptClass = successPromptClass;
	}

	public String getOnsubmit() {
		return onsubmit;
	}

	public void setOnsubmit(String onsubmit) {
		this.onsubmit = onsubmit;
	}

	public String getErrorPromptElement() {
		return errorPromptElement;
	}

	public void setErrorPromptElement(String errorPromptElement) {
		this.errorPromptElement = errorPromptElement;
	}

	public String getErrorPrompt() {
		return errorPrompt;
	}

	public void setErrorPrompt(String errorPrompt) {
		this.errorPrompt = errorPrompt;
	}

	public String getRegexp() {
		return regexp;
	}

	public void setRegexp(String regexp) {
		this.regexp = regexp;
	}

	public String getMinlength() {
		return minlength;
	}

	public void setMinlength(String minlength) {
		this.minlength = minlength;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getLabelClass() {
		return labelClass;
	}

	public void setLabelClass(String labelClass) {
		this.labelClass = labelClass;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAccept() {
		return accept;
	}

	public void setAccept(String accept) {
		this.accept = accept;
	}

	public String getAlign() {
		return align;
	}

	public void setAlign(String align) {
		this.align = align;
	}

	public String getAlt() {
		return alt;
	}

	public void setAlt(String alt) {
		this.alt = alt;
	}

	public String getChecked() {
		return checked;
	}

	public void setChecked(String checked) {
		this.checked = checked;
	}

	public String getDisabled() {
		return disabled;
	}

	public void setDisabled(String disabled) {
		this.disabled = disabled;
	}

	public String getMaxlength() {
		return maxlength;
	}

	public void setMaxlength(String maxlength) {
		this.maxlength = maxlength;
	}

	public String getReadonly() {
		return readonly;
	}

	public void setReadonly(String readonly) {
		this.readonly = readonly;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getUsemap() {
		return usemap;
	}

	public void setUsemap(String usemap) {
		this.usemap = usemap;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTabindex() {
		return tabindex;
	}

	public void setTabindex(String tabindex) {
		this.tabindex = tabindex;
	}

	public String getAccesskey() {
		return accesskey;
	}

	public void setAccesskey(String accesskey) {
		this.accesskey = accesskey;
	}

	public String getErrorPromptClass() {
		return errorPromptClass;
	}

	public void setErrorPromptClass(String errorPromptClass) {
		this.errorPromptClass = errorPromptClass;
	}

	public String getPromptElement() {
		return promptElement;
	}

	public void setPromptElement(String promptElement) {
		this.promptElement = promptElement;
	}

	public String getPromptText() {
		return promptText;
	}

	public void setPromptText(String promptText) {
		this.promptText = promptText;
	}

	public String getPromptClass() {
		return promptClass;
	}

	public void setPromptClass(String promptClass) {
		this.promptClass = promptClass;
	}

}
