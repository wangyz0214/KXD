package kxd.engine.ui.tags.website;

import java.io.IOException;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;

public class StyleTag extends TaggedTagSupport {
	private static final long serialVersionUID = 1L;
	private static String TAG_NAME = "STYLE";

	@Override
	protected String getTagName() {
		return TAG_NAME;
	}

	@Override
	protected void outputStandardAttributes(JspWriter writer)
			throws IOException {
		writeAttribute(writer, "type", getType());
		writeAttribute(writer, "media", getMedia());
		writeAttribute(writer, "title", getTitle());
		writeAttribute(writer, "dir", getDir());
		writeAttribute(writer, "lang", getLang());
		writeAttribute(writer, "xml:space", getXmlspace());
	}

	@Override
	protected void outputValue(JspWriter writer) throws JspTagException,
			IOException {
		writeText(writer, getStyleText());
	}

	String type;
	String media;
	String title;
	String dir;
	String lang;
	String xmlspace;
	String styleText;

	@Override
	public void uninit() {
		styleText = null;
		super.uninit();
	}

	@Override
	public void release() {
		type = null;
		media = null;
		title = null;
		dir = null;
		lang = null;
		xmlspace = null;
		super.release();
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMedia() {
		return media;
	}

	public void setMedia(String media) {
		this.media = media;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDir() {
		return dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public String getXmlspace() {
		return xmlspace;
	}

	public void setXmlspace(String xmlspace) {
		this.xmlspace = xmlspace;
	}

	public String getStyleText() {
		return styleText;
	}

	public void setStyleText(String styleText) {
		this.styleText = styleText;
	}
}
