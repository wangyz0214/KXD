package kxd.engine.ui.tags.website;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;

public class SilverlightTag extends TaggedTagSupport {
	private static final long serialVersionUID = 1L;
	private String configFile;
	private String name;
	private String styleClass;
	private String style;
	private String height;
	private String width;
	private String src;

	@Override
	public void release() {
		configFile = null;
		name = null;
		styleClass = null;
		style = null;
		height = null;
		width = null;
		styleClass = null;
		src = null;
		super.release();
	}

	@Override
	public void uninit() {
		src = null;
		super.uninit();
	}

	@Override
	public int startTag(JspWriter writer) throws JspTagException, IOException {
		if (pageContext.getAttribute("CONTEXTSILVERLIGHTENDERED") == null) {
			pageContext.setAttribute("CONTEXTSILVERLIGHTENDERED", true);
			HttpServletRequest request = (HttpServletRequest) pageContext
					.getRequest();
			writer.write("<script type='text/javascript' src='"
					+ request.getContextPath()
					+ "/scripts/core/silverlight.js'></script><iframe id='_sl_historyFrame' style='visibility:hidden;height:0px;width:0px;border:0px'></iframe>");
		}
		return super.startTag(writer);
	}

	@Override
	protected void outputValue(JspWriter writer) throws JspTagException,
			IOException {
		writer.write("<param name='source' value='");
		writeUrl(writer, getSrc());
		writer.write("'/>");
		writer.write("<param name='onError' value='onSilverlightError'/>");
		writer.write("<param name='background' value='Transparent'/>");
		writer.write("<param name='windowless' value='true'/>");
		writer.write("<param name='minRuntimeVersion' value='5.0.61118.0'/>");
		writer.write("<param name='autoUpgrade' value='true'/>");
		writer.write("<param name='initParams' value='contextpath=");
		HttpServletRequest request = (HttpServletRequest) pageContext
				.getRequest();
		writer.write(request.getContextPath());
		if (getConfigFile() != null)
			writer.write(",configfile=" + getConfigFile());
		writer.write("'/>");
		writer.write("<a href='" + request.getContextPath()
				+ "/silverlight/silverlight.exe' style='text-decoration:none'>");
		writer.write("<img src='"
				+ request.getContextPath()
				+ "/silverlight/silverlight.png' alt='获取 Microsoft Silverlight' style='border-style:none'/></a>");
	}
 
	public String getConfigFile() {
		return configFile;
	}

	public void setConfigFile(String configFile) {
		this.configFile = configFile;
	}

	@Override
	protected void outputStandardAttributes(JspWriter writer)
			throws IOException {
		writeAttribute(writer, "id", getId());
		writeAttribute(writer, "type", "application/x-silverlight-2");
		writeAttribute(writer, "data", "data:application/x-silverlight-2,");
		writeAttribute(writer, "name", getName());
		writeAttribute(writer, "styleClass", getStyleClass());
		writeAttribute(writer, "width", getWidth());
		writeAttribute(writer, "height", getHeight());
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStyleClass() {
		return styleClass;
	}

	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public String getSrc() {
		return src;
	}

	public void setSrc(String src) {
		this.src = src;
	}

	private static String TAG_NAME = "object";

	@Override
	protected String getTagName() {
		return TAG_NAME;
	}
}
