package kxd.engine.ui.tags.website;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.log4j.Logger;

abstract public class BaseTagSupport extends TagSupport {
	static Logger logger = Logger.getLogger(BaseTagSupport.class);
	private static final long serialVersionUID = 1L;
	private Object rendered;
	static public ConcurrentHashMap<String, String> configParams;

	public BaseTagSupport() {
		super();
	}

	public FormTag getForm() {
		Tag p = this;
		while (p != null) {
			if (p instanceof FormTag)
				return (FormTag) p;
			p = p.getParent();
		}
		return null;
	}

	public void init() {

	}

	public void uninit() {
		rendered = null;
	}

	@Override
	public void release() {
		uninit();
		super.release();
	}

	public void writeUrl(JspWriter writer, String url) throws IOException {
		HttpServletRequest request = (HttpServletRequest) pageContext
				.getRequest();
		if (url != null) {
			if (url.startsWith("/"))
				writer.write(request.getContextPath() + url);
			else
				writer.write(url);
		}
	}

	public void writeUrlAttribute(JspWriter writer, String name, String value)
			throws IOException {
		if (value != null) {
			HttpServletRequest request = (HttpServletRequest) pageContext
					.getRequest();
			if (value.startsWith("/"))
				writer.write(" " + name + "=\"" + request.getContextPath()
						+ value + "\"");
			else
				writer.write(" " + name + "=\"" + value + "\"");
		}
	}

	public void writeAttribute(JspWriter writer, String name, Object value)
			throws IOException {
		if (value != null)
			writer.write(" " + name + "=\"" + value + "\"");
	}

	public void writeText(JspWriter writer, Object value) throws IOException {
		if (value != null)
			writer.write(value.toString());
	}

	abstract protected int startTag(JspWriter writer) throws JspException,
			IOException;

	abstract protected int endTag(JspWriter writer) throws JspException,
			IOException;

	@Override
	final public int doStartTag() throws JspException {
		if (Boolean.FALSE.equals(getRendered()))
			return SKIP_BODY;
		init();
		Object p = pageContext.getRequest().getAttribute("fromkonefaces");
		if (p == null || !Boolean.TRUE.equals(p))
			throw new JspException("请不要直接调用.jsp文件");
		try {
			return startTag(pageContext.getOut());
		} catch (IOException e) {
			if (logger.isDebugEnabled())
				e.printStackTrace();
			throw new JspException(e.getCause());
		}
	}

	@Override
	final public int doEndTag() throws JspException {
		if (Boolean.FALSE.equals(getRendered())) {
			uninit();
			return EVAL_PAGE;
		}
		try {
			int ret = endTag(pageContext.getOut());
			uninit();
			return ret;
		} catch (IOException e) {
			throw new JspException(e.getCause());
		}
	}

	public Object getRendered() {
		return rendered;
	}

	public void setRendered(Object rendered) throws JspException {
		if (rendered == null)
			this.rendered = true;
		else if (rendered instanceof Boolean)
			this.rendered = rendered;
		else
			this.rendered = "true".equalsIgnoreCase(rendered.toString());
	}

}
