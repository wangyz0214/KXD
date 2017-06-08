package kxd.engine.scs.web.tags;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

public class CssLinkTag extends BaseTag {
	private static final long serialVersionUID = 1L;

	public String getRel() {
		if (rel == null)
			return "stylesheet";
		return rel;
	}

	public void setRel(String rel) {
		this.rel = rel;
	}

	public String getType() {
		if (type == null)
			return "text/css";
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	private String rel;

	private String type;

	private String href;

	@Override
	public int doEndTag() throws JspException {
		JspWriter out = pageContext.getOut();
		try {
			out.write("<link rel='" + getRel() + "' type='" + getType()
					+ "' href='");
			writeUrl(out, href);
			out.write("'/>");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return EVAL_PAGE;
	}

	@Override
	public int doStartTag() throws JspException {
		return super.doStartTag();
	}

}
