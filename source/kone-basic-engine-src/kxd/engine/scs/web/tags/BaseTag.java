package kxd.engine.scs.web.tags;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

public class BaseTag extends TagSupport {
	private static final long serialVersionUID = 1L;
	private String sitePrefix;
	static public ConcurrentHashMap<String, String> sites;

	public String getSitePrefix() {
		return sitePrefix;
	}

	public void setSitePrefix(String sitePrefix) {
		this.sitePrefix = sitePrefix;
	}

	@Override
	public void release() {
		sitePrefix = null;
		super.release();
	}

	public void writeUrl(JspWriter writer, String url) throws IOException {
		HttpServletRequest request = (HttpServletRequest) pageContext
				.getRequest();
		if (url != null) {
			url = url.replace("\\", "/");
			url = url.replace("//", "/").trim();
			if (sitePrefix != null && sites != null) {
				String site = sites.get(sitePrefix);
				if (site == null || site.trim().isEmpty()) {
					if (url.startsWith("/"))
						url = request.getContextPath() + url;
				} else {
					if (!url.startsWith("/"))
						url = "/" + url;
					url = sites.get(sitePrefix) + url;
				}
			} else if (url.startsWith("/"))
				url = request.getContextPath() + url;
			writer.write(url);
		}
	}

	public void writeUrlAttribute(JspWriter writer, String name, String url)
			throws IOException {
		HttpServletRequest request = (HttpServletRequest) pageContext
				.getRequest();
		if (url != null) {
			url = url.replace("\\", "/");
			url = url.replace("//", "/").trim();
			if (sitePrefix != null && sites != null) {
				String site = sites.get(sitePrefix);
				if (site == null || site.trim().isEmpty()) {
					if (url.startsWith("/"))
						url = request.getContextPath() + url;
				} else {
					if (!url.startsWith("/"))
						url = "/" + url;
					url = sites.get(sitePrefix) + url;
				}
			} else if (url.startsWith("/"))
				url = request.getContextPath() + url;
			writer.write(" " + name + "=\"" + url + "\"");
		}
	}

	public void writeAttribute(JspWriter writer, String name, Object value)
			throws IOException {
		if (value != null)
			writer.write(" " + name + "=\"" + value + "\"");
	}

	public void writeAttribute(JspWriter writer, String name, Object value,
			Object defaultValue) throws IOException {
		if (value != null)
			writer.write(" " + name + "=\"" + value + "\"");
		else
			writer.write(" " + name + "=\"" + defaultValue + "\"");
	}
}
