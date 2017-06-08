package kxd.engine.ui.tags.website;

import java.io.IOException;

import javax.servlet.jsp.JspWriter;

public class ATag extends BaseHtmlTag {
	private static final long serialVersionUID = 1L;
	private static String TAG_NAME = "A";

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

	@Override
	protected void outputStandardAttributes(JspWriter writer)
			throws IOException {
		outputAAttributes(writer);
		super.outputStandardAttributes(writer);
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

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
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

	@Override
	protected String getTagName() {
		return TAG_NAME;
	}

}
