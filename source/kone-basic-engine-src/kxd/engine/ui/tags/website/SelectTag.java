package kxd.engine.ui.tags.website;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;

import kxd.engine.scs.admin.AdminSessionObject;
import kxd.util.ListItem;

public class SelectTag extends BaseHtmlTag {
	private static final long serialVersionUID = 1L;
	private static String TAG_NAME = "select";
	private Object items;
	private String onchange;
	private String selectedDesp;
	private String allDesp;
	/**
	 * 参数分类ID
	 */
	private Integer paramCategoryId;

	public String getOnchange() {
		return onchange;
	}

	public void setOnchange(String onchange) {
		this.onchange = onchange;
	}

	@Override
	protected String getTagName() {
		return TAG_NAME;
	}

	@Override
	public int startTag(JspWriter writer) throws JspTagException, IOException {
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
		return super.startTag(writer);
	}

	@Override
	protected void outputValue(JspWriter writer) throws JspTagException,
			IOException {
		Object[] array = null;
		Object items = getItems();
		if (items != null) {
			if (items instanceof Object[])
				array = (Object[]) items;
			else if (items instanceof Collection<?>)
				array = ((Collection<?>) items).toArray();
		}
		if (getAllDesp() != null) {
			writeText(writer, "<option value=''>" + getAllDesp() + "</option>");
		}
		if (array != null) {
			for (int i = 0; i < array.length; i++) {
				Object o = array[i];
				if (o instanceof ListItem<?>) {
					ListItem<?> e = (ListItem<?>) o;
					Object id = e.getId();
					if (id == null)
						id = "";
					Object value = e.getText();
					if (value == null)
						value = "";
					if (i == 0)
						selectedDesp = value.toString();
					writeText(writer, "<option value='" + id + "' ");
					if (e.isChecked()
							|| (e.getId() != null && e.getId().equals(
									getValue()))
							|| (e.getId() == null && getValue() == null)) {
						writeAttribute(writer, "selected", "selected");
						selectedDesp = value.toString();
					}
					if (e.isDisabled())
						writeAttribute(writer, "disabled", "disabled");
					writeText(writer, ">" + value + "</option>");
				} else
					throw new IOException(o.getClass().getSimpleName()
							+ ":参数必须是ListItem<E>");
			}
		} else if (paramCategoryId != null) {
			Map<String, String> map = AdminSessionObject
					.getParamConfig(paramCategoryId);
			if (map != null) {
				List<String> keys = new ArrayList<String>();
				Iterator<String> it = map.keySet().iterator();
				while (it.hasNext())
					keys.add(it.next());
				Collections.sort(keys);
				String value = "";
				if (getValue() != null)
					value = getValue().toString();
				for (int i = 0; i < keys.size(); i++) {
					String k = keys.get(i);
					String v = map.get(k);
					if (i == 0)
						selectedDesp = v;
					writeText(writer, "<option value='" + k + "' ");
					if (k.equals(value)) {
						writeAttribute(writer, "selected", "selected");
						selectedDesp = v;
					}
					writeText(writer, ">" + v + "</option>");
				}
			}
		}
	}

	static final private String FOCUSVAR = "focusinputrendered";

	@Override
	public int endTag(JspWriter writer) throws JspTagException, IOException {
		int ret = super.endTag(writer);
		FormTag form = getForm();
		if (form != null && getName() != null) {
			writeText(writer, "<input type='hidden' name='" + getName()
					+ "_desp' id='" + getName() + "_desp' value='"
					+ selectedDesp + "'/>");
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
					"<script type='text/javascript'>input=document.getElementById('"
							+ form.getId() + "')['" + getName() + "'];");
			if (getOnsubmit() != null)
				writeText(writer, "input.onsubmit=function(){" + getOnsubmit()
						+ "}");
			if (!"disabled".equalsIgnoreCase(getDisabled())
					&& pageContext.getRequest().getAttribute(FOCUSVAR) == null) {
				writeText(writer, "input.focus();</script>");
				pageContext.getRequest().setAttribute(FOCUSVAR, "true");
			}
			writeText(writer, "</script>");
		} else if (getId() != null
				&& !"disabled".equalsIgnoreCase(getDisabled())) {
			if (pageContext.getRequest().getAttribute(FOCUSVAR) == null) {
				writeText(writer,
						"<script type='text/javascript'>document.getElementById('"
								+ getId() + "').focus();</script>");
				pageContext.getRequest().setAttribute(FOCUSVAR, "true");
			}
		}
		return ret;
	}

	@Override
	protected void outputStandardAttributes(JspWriter writer)
			throws IOException {
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
		writeAttribute(writer, "onchange", getOnchange());
		writeAttribute(writer, "multiple", getMultiple());
		writeAttribute(writer, "name", getName());
		writeAttribute(writer, "size", getSize());
		writeAttribute(writer, "tabindex", getTabindex());
		writeAttribute(writer, "accesskey", getAccesskey());
		writeAttribute(writer, "regexp", getRegexp());
		writeAttribute(writer, "minlength", getMinlength());
		writeAttribute(writer, "disabled", getDisabled());
		writeAttribute(writer, "maxlength", getMaxlength());
		writeAttribute(writer, "value", getValue());
		super.outputStandardAttributes(writer);
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

	String multiple;
	String name;
	String disabled;
	String size;
	String tabindex;
	String accesskey;
	String minlength;
	String maxlength;

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
		minlength = null;
		maxlength = null;
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
		multiple = null;
		name = null;
		disabled = null;
		size = null;
		tabindex = null;
		accesskey = null;
		items = null;
		paramCategoryId = null;
		super.release();
	}

	public Object getItems() {
		return items;
	}

	public void setItems(Object items) {
		this.items = items;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMultiple() {
		return multiple;
	}

	public void setMultiple(String multiple) {
		this.multiple = multiple;
	}

	public String getDisabled() {
		return disabled;
	}

	public void setDisabled(String disabled) {
		this.disabled = disabled;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
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

	public String getErrorPromptClass() {
		return errorPromptClass;
	}

	public void setErrorPromptClass(String errorPromptClass) {
		this.errorPromptClass = errorPromptClass;
	}

	public String getSuccessPromptClass() {
		return successPromptClass;
	}

	public void setSuccessPromptClass(String successPromptClass) {
		this.successPromptClass = successPromptClass;
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

	public String getRegexp() {
		return regexp;
	}

	public void setRegexp(String regexp) {
		this.regexp = regexp;
	}

	public String getOnsubmit() {
		return onsubmit;
	}

	public void setOnsubmit(String onsubmit) {
		this.onsubmit = onsubmit;
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

	public String getMinlength() {
		return minlength;
	}

	public void setMinlength(String minlength) {
		this.minlength = minlength;
	}

	public String getMaxlength() {
		return maxlength;
	}

	public void setMaxlength(String maxlength) {
		this.maxlength = maxlength;
	}

	public Integer getParamCategoryId() {
		return paramCategoryId;
	}

	public void setParamCategoryId(Integer paramCategoryId) {
		this.paramCategoryId = paramCategoryId;
	}

	public String getAllDesp() {
		return allDesp;
	}

	public void setAllDesp(String allDesp) {
		this.allDesp = allDesp;
	}

}
