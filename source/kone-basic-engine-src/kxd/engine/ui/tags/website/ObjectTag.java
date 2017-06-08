package kxd.engine.ui.tags.website;

import java.io.IOException;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;

public class ObjectTag extends TaggedTagSupport {
	private static final long serialVersionUID = 1L;
	private static String TAG_NAME = "object";

	@Override
	protected String getTagName() {
		return TAG_NAME;
	}

	@Override
	protected void outputStandardAttributes(JspWriter writer)
			throws IOException {
		writeAttribute(writer, "classid", getClassid());
		writeUrlAttribute(writer, "codebase", getCodebase());
	}

	@Override
	protected void outputValue(JspWriter writer) throws JspTagException,
			IOException {

	}

	String classid;
	String codebase;

	@Override
	public void uninit() {
		super.uninit();
	}

	@Override
	public void release() {
		classid = null;
		codebase = null;
		super.release();
	}

	public String getClassid() {
		return classid;
	}

	public void setClassid(String classid) {
		this.classid = classid;
	}

	public String getCodebase() {
		return codebase;
	}

	public void setCodebase(String codebase) {
		this.codebase = codebase;
	}

}
