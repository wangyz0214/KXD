package kxd.engine.ui.tags.website;

import java.io.IOException;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;

public class LinkTag extends TaggedTagSupport {
	private static final long serialVersionUID = 1L;
	private static String TAG_NAME = "LINK";

	@Override
	protected String getTagName() {
		return TAG_NAME;
	}

	@Override
	protected void outputStandardAttributes(JspWriter writer)
			throws IOException {
		writeAttribute(writer, "type", getType());
		writeAttribute(writer, "charset", getCharset());
		writeUrlAttribute(writer, "href", getHref());
		writeAttribute(writer, "hreflang", getHreflang());
		writeAttribute(writer, "media", getMedia());
		writeAttribute(writer, "rel", getRel());
		writeAttribute(writer, "rev", getRev());
		writeAttribute(writer, "target", getTarget());
		writeAttribute(writer, "title", getTitle());
	}

	@Override
	protected void outputValue(JspWriter writer) throws JspTagException,
			IOException {
	}

	String charset;
	String href;
	String hreflang;
	String media;
	String rel;
	String rev;
	String target;
	String title;
	String type;

	@Override
	public void release() {
		charset = null;
		href = null;
		hreflang = null;
		media = null;
		rel = null;
		rev = null;
		target = null;
		title = null;
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

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public String getHreflang() {
		return hreflang;
	}

	public void setHreflang(String hreflang) {
		this.hreflang = hreflang;
	}

	public String getMedia() {
		return media;
	}

	public void setMedia(String media) {
		this.media = media;
	}

	public String getRel() {
		return rel;
	}

	public void setRel(String rel) {
		this.rel = rel;
	}

	public String getRev() {
		return rev;
	}

	public void setRev(String rev) {
		this.rev = rev;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
