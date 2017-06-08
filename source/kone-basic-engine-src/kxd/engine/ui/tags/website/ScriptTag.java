package kxd.engine.ui.tags.website;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;

public class ScriptTag extends TaggedTagSupport {
	private static final long serialVersionUID = 1L;
	private static String TAG_NAME = "SCRIPT";
	private boolean isOptionFooter;

	@Override
	protected String getTagName() {
		return TAG_NAME;
	}

	@Override
	protected void outputStandardAttributes(JspWriter writer)
			throws IOException {
		writeAttribute(writer, "type", getType());
		writeAttribute(writer, "charset", getCharset());
		writeAttribute(writer, "defer", getDefer());
		writeAttribute(writer, "language", getLanguage());
		writeUrlAttribute(writer, "src", getSrc());
		writeAttribute(writer, "xml:space", getXmlspace());
	}

	@Override
	public int startTag(JspWriter writer) throws JspTagException, IOException {
		if (pageContext.getAttribute("CONTEXTSCRIPTRENDERED") == null) {
			pageContext.setAttribute("CONTEXTSCRIPTRENDERED", true);
			HttpServletRequest request = (HttpServletRequest) pageContext
					.getRequest();
			String path = request.getContextPath();
			if (path == null)
				path = "";
			writeText(writer,
					"<script type='text/javascript'>var contextPath='" + path
							+ "';</script>");
			writeText(writer, "<script type='text/javascript' src='" + path
					+ "/scripts/core/mootools-core.js'></script>");
			writeText(writer, "<script type='text/javascript' src='" + path
					+ "/scripts/core/kone-core.js'></script>");
		}
		return super.startTag(writer);
	}

	@Override
	protected void outputValue(JspWriter writer) throws JspTagException,
			IOException {
		writeText(writer, getScriptText());
	}

	String type;
	String charset;
	String defer;
	String language;
	String src;
	String xmlspace;
	String scriptText;

	@Override
	public void uninit() {
		scriptText = null;
		super.uninit();
	}

	@Override
	public void release() {
		charset = null;
		defer = null;
		src = null;
		language = null;
		xmlspace = null;
		type = null;
		super.release();
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public String getDefer() {
		return defer;
	}

	public void setDefer(String defer) {
		this.defer = defer;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getSrc() {
		return src;
	}

	public void setSrc(String src) {
		this.src = src;
	}

	public String getXmlspace() {
		return xmlspace;
	}

	public void setXmlspace(String xmlspace) {
		this.xmlspace = xmlspace;
	}

	public String getScriptText() {
		return scriptText;
	}

	public void setScriptText(String scriptText) {
		this.scriptText = scriptText;
	}

	public boolean isOptionFooter() {
		return isOptionFooter;
	}

	public void setIsOptionFooter(boolean isOptionFooter) {
		this.isOptionFooter = isOptionFooter;
	}

}
