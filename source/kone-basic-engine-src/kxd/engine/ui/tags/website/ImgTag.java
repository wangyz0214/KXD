package kxd.engine.ui.tags.website;

import java.io.IOException;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;

public class ImgTag extends BaseHtmlTag {
	private static final long serialVersionUID = 1L;
	private static String TAG_NAME = "IMG";

	@Override
	protected String getTagName() {
		return TAG_NAME;
	}

	@Override
	public int startTag(JspWriter writer) throws JspTagException, IOException {
		if (getHref() != null) {
			writeText(writer, "<A ");
			super.outputStandardAttributes(writer);
			outputAAttributes(writer);
			writeText(writer, ">");
		}
		return super.startTag(writer);
	}

	@Override
	public int endTag(JspWriter writer) throws JspTagException, IOException {
		int ret = super.endTag(writer);
		if (getHref() != null) {
			if (getValue() != null)
				writeText(writer, getValue());
			writeText(writer, "</A>");
		}
		return ret;
	}

	@Override
	protected void outputValue(JspWriter writer) throws JspTagException,
			IOException {
	}

	@Override
	protected void outputStandardAttributes(JspWriter writer)
			throws IOException {
		String c = getOnclick();
		if (getHref() != null) {
			setOnclick(null);
		}
		outputImgAttributes(writer);
		super.outputStandardAttributes(writer);
		if (getHref() != null)
			setOnclick(c);
	}

	protected void outputAAttributes(JspWriter writer) throws IOException {
		writeAttribute(writer, "charset", getCharset());
		writeAttribute(writer, "coords", getCoords());
		writeUrlAttribute(writer, "href", getHref());
		writeAttribute(writer, "hreflang", getHreflang());
		writeAttribute(writer, "name", getName());
		writeAttribute(writer, "rel", getRel());
		writeAttribute(writer, "rev", getRev());
		writeAttribute(writer, "shape", getShape());
		writeAttribute(writer, "target", getTarget());
		writeAttribute(writer, "type", getType());
	}

	protected void outputImgAttributes(JspWriter writer) throws IOException {
		writeUrlAttribute(writer, "src", getSrc());
		writeAttribute(writer, "border", getBorder());
		writeAttribute(writer, "height", getHeight());
		writeAttribute(writer, "align", getAlign());
		writeAttribute(writer, "alt", getAlt());
		writeAttribute(writer, "hspace", getHspace());
		writeAttribute(writer, "ismap", getIsmap());
		writeAttribute(writer, "longdesc", getLongdesc());
		writeAttribute(writer, "usemap", getUsemap());
		writeAttribute(writer, "vspace", getVspace());
		writeUrlAttribute(writer, "width", getWidth());
	}

	String alt;
	String src;
	String border;
	String height;
	String align;
	String hspace;
	String ismap;
	String longdesc;
	String usemap;
	String vspace;
	String width;

	@Override
	public void release() {
		alt = null;
		src = null;
		border = null;
		height = null;
		align = null;
		hspace = null;
		ismap = null;
		longdesc = null;
		usemap = null;
		vspace = null;
		width = null;
		vspace = null;
		charset = null;
		coords = null;
		href = null;
		hreflang = null;
		name = null;
		rel = null;
		rev = null;
		shape = null;
		target = null;
		type = null;
		super.release();
	}

	String charset;
	String coords;
	String href;
	String hreflang;
	String name;
	String rel;
	String rev;
	String shape;
	String target;
	String type;

	public String getAlt() {
		return alt;
	}

	public void setAlt(String alt) {
		this.alt = alt;
	}

	public String getSrc() {
		return src;
	}

	public void setSrc(String src) {
		this.src = src;
	}

	public String getBorder() {
		return border;
	}

	public void setBorder(String border) {
		this.border = border;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public String getAlign() {
		return align;
	}

	public void setAlign(String align) {
		this.align = align;
	}

	public String getHspace() {
		return hspace;
	}

	public void setHspace(String hspace) {
		this.hspace = hspace;
	}

	public String getIsmap() {
		return ismap;
	}

	public void setIsmap(String ismap) {
		this.ismap = ismap;
	}

	public String getLongdesc() {
		return longdesc;
	}

	public void setLongdesc(String longdesc) {
		this.longdesc = longdesc;
	}

	public String getUsemap() {
		return usemap;
	}

	public void setUsemap(String usemap) {
		this.usemap = usemap;
	}

	public String getVspace() {
		return vspace;
	}

	public void setVspace(String vspace) {
		this.vspace = vspace;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public String getCoords() {
		return coords;
	}

	public void setCoords(String coords) {
		this.coords = coords;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public String getShape() {
		return shape;
	}

	public void setShape(String shape) {
		this.shape = shape;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
