package kxd.engine.ui.tags.website;

import java.io.IOException;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;

import kxd.util.DataUnit;

public class FormTag extends BaseHtmlTag {
	private static final long serialVersionUID = 1L;
	private static String TAG_NAME = "FORM";

	@Override
	protected String getTagName() {
		return TAG_NAME;
	}

	@Override
	public String getId() {
		String id = super.getId();
		if (id == null) {
			id = "form" + new Random(1000000).nextLong();
			setId(id);
		}
		return id;
	}

	private String method = "POST";
	private String acceptCharset;
	private String enctype = "application/x-www-form-urlencoded";
	private String target;
	private String onsubmit;
	private String onreset;
	private String name;
	private String action;
	private boolean ajaxform;

	@Override
	public void release() {
		method = null;
		acceptCharset = null;
		enctype = null;
		target = null;
		onsubmit = null;
		onreset = null;
		name = null;
		action = null;
		super.release();
	}

	@Override
	protected void outputStandardAttributes(JspWriter writer)
			throws IOException {
		super.outputStandardAttributes(writer);
		if (!isAjaxform()) {
			String url = ((HttpServletRequest) pageContext.getRequest())
					.getRequestURI();
			int index = url.indexOf(".jsp");
			if (index > -1)
				url = url.substring(0, index) + ".go";
			if (action == null || action.trim().isEmpty())
				writeAttribute(writer, "action", url);
			else
				writeUrlAttribute(writer, "action", action);
		}
		writeAttribute(writer, "method", getMethod());
		writeAttribute(writer, "accept-charset", getAcceptCharset());
		writeAttribute(writer, "target", getTarget());
		writeAttribute(writer, "enctype", getEnctype());
		if (getOnsubmit() != null) {
			String s = "_submit" + Integer.toString(new Random().nextInt(1000));
			writeAttribute(writer, "onsubmit", "var " + s + "=function(){"
					+ getOnsubmit() + "};if(" + s + "()){return facessubmit('"
					+ getId() + "');}else{return false;}");
		} else
			writeAttribute(writer, "onsubmit", "return facessubmit('" + getId()
					+ "');");
		writeAttribute(writer, "onreset", getOnreset());
	}

	@Override
	protected void outputValue(JspWriter writer) throws JspTagException,
			IOException {
		if (!isAjaxform()) {
			String url = ((HttpServletRequest) pageContext.getRequest())
					.getRequestURI();
			writeText(writer,
					"<input type='hidden' name='konefacesfromid' value='"
							+ DataUnit.bytesToHex(url.getBytes()) + "'/>");
		}
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getAcceptCharset() {
		return acceptCharset;
	}

	public void setAcceptCharset(String acceptCharset) {
		this.acceptCharset = acceptCharset;
	}

	public String getEnctype() {
		return enctype;
	}

	public void setEnctype(String enctype) {
		this.enctype = enctype;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getOnsubmit() {
		return onsubmit;
	}

	public void setOnsubmit(String onsubmit) {
		this.onsubmit = onsubmit;
	}

	public String getOnreset() {
		return onreset;
	}

	public void setOnreset(String onreset) {
		this.onreset = onreset;
	}

	public boolean isAjaxform() {
		return ajaxform;
	}

	public void setAjaxform(boolean ajaxForm) {
		this.ajaxform = ajaxForm;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}
}
