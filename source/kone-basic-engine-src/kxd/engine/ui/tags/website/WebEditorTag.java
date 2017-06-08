package kxd.engine.ui.tags.website;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;

public class WebEditorTag extends BaseHtmlTag {
	private static final long serialVersionUID = 1L;
	private static String TAG_NAME = "input";
	private String align;
	private String cssFile = "/css/webeditor.css",
			menuCssFile = "/css/webeditor_menu.css";
	private String iconRootPath = "/images/webeditor/";
	private String uploadImageUrl;
	private String uploadFlashUrl;
	private String queryImageListUrl;
	private String queryFlashListUrl;
	private String imageRootPath;
	private String flashRootPath, onFullScreen;

	@Override
	public void release() {
		align = null;
		onFullScreen = null;
		super.release();
	}

	@Override
	protected String getTagName() {
		return TAG_NAME;
	}

	@Override
	protected void outputStandardAttributes(JspWriter writer)
			throws IOException {
		writeAttribute(writer, "type", "hidden");
		writeAttribute(writer, "name", getId());
		super.outputStandardAttributes(writer);
	}

	public String getAlign() {
		return align;
	}

	public void setAlign(String align) {
		this.align = align;
	}

	@Override
	protected void outputValue(JspWriter writer) throws JspTagException,
			IOException {

	}

	@Override
	public int endTag(JspWriter writer) throws JspTagException, IOException {
		int ret = super.endTag(writer);
		if (pageContext.getAttribute("CONTEXTWEBEDITORRENDERED") == null) {
			pageContext.setAttribute("CONTEXTWEBEDITORRENDERED", true);
			HttpServletRequest request = (HttpServletRequest) pageContext
					.getRequest();
			String path = request.getContextPath();
			if (path == null)
				path = "";
			writeText(writer, "<script type='text/javascript' src='" + path
					+ "/scripts/core/kone-webeditor.js'></script>");
		}
		writer.write("<input id='" + getId() + "_summary' name='" + getId()
				+ "_summary' type='hidden'>");
		writer.write("<textarea id='" + getId()
				+ "_area' style='display:none'>");
		writeText(writer, getValue());
		writer.write("</textarea>");
		writer.write("<div id='" + getId() + "_div'");
		writeAttribute(writer, "class", getStyleClass());
		writeAttribute(writer, "style", getStyle());
		writer.write("></div>");
		writer.write("<script type='text/javascript'>var " + getId()
				+ "=new WebEditor(null,{id:'" + getId() + "_div','inputid':'"
				+ getId() + "','events':{");
		if (getOnFullScreen() != null)
			writer.write("'fullscreen':function(o,s){" + getOnFullScreen()
					+ "}");
		writer.write("},'editor':{");
		writer.write("cssfile :'");
		writeUrl(writer, getCssFile());
		writer.write("',menucssfile :'");
		writeUrl(writer, getMenuCssFile());
		writer.write("',imgpath :'");
		writeUrl(writer, getIconRootPath());
		writer.write("',insertimg : {url :'");
		writeUrl(writer, getUploadImageUrl());
		writer.write("',rootpath :'");
		writeUrl(writer, getImageRootPath());
		writer.write("',queryurl :'" + getQueryImageListUrl());
		writer.write("'},insertflash : {url :'");
		writeUrl(writer, getUploadFlashUrl());
		writer.write("',rootpath :'");
		writeUrl(writer, getFlashRootPath());
		writer.write("',queryurl :'" + getQueryFlashListUrl());
		writer.write("'},html:$('" + getId() + "_area').get('value')");
		writer.write("}});$('" + getId() + "_area').dispose();");
		writer.write("</script>");
		return ret;
	}

	public String getCssFile() {
		return cssFile;
	}

	public void setCssFile(String cssFile) {
		this.cssFile = cssFile;
	}

	public String getMenuCssFile() {
		return menuCssFile;
	}

	public void setMenuCssFile(String menuCssFile) {
		this.menuCssFile = menuCssFile;
	}

	public String getIconRootPath() {
		return iconRootPath;
	}

	public void setIconRootPath(String iconRootPath) {
		this.iconRootPath = iconRootPath;
	}

	public String getUploadImageUrl() {
		return uploadImageUrl;
	}

	public void setUploadImageUrl(String uploadImageUrl) {
		this.uploadImageUrl = uploadImageUrl;
	}

	public String getUploadFlashUrl() {
		return uploadFlashUrl;
	}

	public void setUploadFlashUrl(String uploadFlashUrl) {
		this.uploadFlashUrl = uploadFlashUrl;
	}

	public String getQueryImageListUrl() {
		return queryImageListUrl;
	}

	public void setQueryImageListUrl(String queryImageListUrl) {
		this.queryImageListUrl = queryImageListUrl;
	}

	public String getQueryFlashListUrl() {
		return queryFlashListUrl;
	}

	public void setQueryFlashListUrl(String queryFlashListUrl) {
		this.queryFlashListUrl = queryFlashListUrl;
	}

	public String getImageRootPath() {
		return imageRootPath;
	}

	public void setImageRootPath(String imageRootPath) {
		this.imageRootPath = imageRootPath;
	}

	public String getFlashRootPath() {
		return flashRootPath;
	}

	public void setFlashRootPath(String flashRootPath) {
		this.flashRootPath = flashRootPath;
	}

	public String getOnFullScreen() {
		if (onFullScreen == null)
			return "";
		return onFullScreen;
	}

	public void setOnFullScreen(String onFullScreen) {
		this.onFullScreen = onFullScreen;
	}

}
