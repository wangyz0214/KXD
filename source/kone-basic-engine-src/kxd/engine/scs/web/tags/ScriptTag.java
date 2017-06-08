package kxd.engine.scs.web.tags;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

public class ScriptTag extends BaseTag {
	private static final long serialVersionUID = 1L;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSrc() {
		return src;
	}

	public void setSrc(String src) {
		this.src = src;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	private String type; // script的type属性

	private String src;// script的src属性

	private String language;// script的language属性
	private String scriptText;

	public String getScriptText() {
		return scriptText;
	}

	public void setScriptText(String scriptText) {
		this.scriptText = scriptText;
	}

	public ScriptTag() {

	}

	@Override
	public int doEndTag() throws JspException {
		JspWriter out = pageContext.getOut();
		try {
			out.write("<script");
			writeAttribute(out, "type", type, "text/javascript");
			writeUrlAttribute(out, "src", src);
			writeAttribute(out, "language", language, "javascript");
			out.write(">");
			if (scriptText != null)
				out.write(scriptText);
			out.write("</script>");
		} catch (IOException e) {
			try {
				out.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
		return EVAL_PAGE;
	}

	@Override
	public int doStartTag() throws JspException {
		if (pageContext.getAttribute("CONTEXTSCRIPTRENDERED") == null) {
			pageContext.setAttribute("CONTEXTSCRIPTRENDERED", true);
			HttpServletRequest request = (HttpServletRequest) pageContext
					.getRequest();
			String path = request.getContextPath();
			if (path == null)
				path = "";
			try {
				pageContext.getOut().write(
						"<script type='text/javascript'>var contextPath='"
								+ path + "';</script>");
				pageContext.getOut().write(
						"<script type='text/javascript' src='" + path
								+ "/scripts/core/mootools-core.js'></script>");
			} catch (IOException e) {
				throw new JspException(e);
			}
		}
		return super.doStartTag();
	}

}
