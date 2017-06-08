package kxd.engine.ui.tags.website;

import java.io.IOException;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;

/**
 * 文件预览标签
 * 
 * @author zhaom
 * 
 */
public class FilePreviewTag extends DivTag {
	private static final long serialVersionUID = 1L;
	private String src;
	private int fileCategory;

	@Override
	public int startTag(JspWriter writer) throws JspTagException, IOException {
		int ret = super.startTag(writer);
		if (src != null) {
			writer.write("<iframe frameborder='0' style='width:100%;height:100%;border:1px solid gray' src='");
			writeUrl(writer, getSrc());
			writer.write("'/>");
		}
		return ret;
	}

	public String getSrc() {
		return src;
	}

	public void setSrc(String src) {
		this.src = src;
	}

	public int getFileCategory() {
		return fileCategory;
	}

	public void setFileCategory(int fileCategory) {
		this.fileCategory = fileCategory;
	}

}
