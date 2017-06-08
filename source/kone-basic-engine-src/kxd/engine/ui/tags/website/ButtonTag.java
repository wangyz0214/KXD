package kxd.engine.ui.tags.website;

import java.io.IOException;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;

public class ButtonTag extends DivTag {
	private static final long serialVersionUID = 1L;
	private String overclass;
	private String downclass;
	private String type = "button";
	private Integer group;
	private String onexecute;
	private boolean checked;

	@Override
	public void release() {
		overclass = null;
		downclass = null;
		type = null;
		super.release();
	}

	@Override
	protected void outputStandardAttributes(JspWriter writer)
			throws IOException {
		super.outputStandardAttributes(writer);
	}

	@Override
	public int endTag(JspWriter writer) throws JspTagException, IOException {
		int ret = super.endTag(writer);
		writeText(writer, "<script type='text/javascript'>new Button({id:'"
				+ getId() + "',type:'" + getType() + "',group:" + getGroup()
				+ ",normalclass:'" + getStyleClass() + "',overclass:'"
				+ getOverclass() + "',downclass:'" + getDownclass()
				+ "',checked:" + checked + ",'onexecute':function(button){"
				+ getOnexecute() + "}});</script>");
		return ret;
	}

	public String getStyleClass() {
		if (styleClass == null)
			return "";
		return styleClass;
	}

	public String getOverclass() {
		if (overclass == null)
			return "";
		return overclass;
	}

	public void setOverclass(String overclass) {
		this.overclass = overclass;
	}

	public String getDownclass() {
		if (downclass == null)
			return "";
		return downclass;
	}

	public void setDownclass(String downclass) {
		this.downclass = downclass;
	}

	public String getType() {
		if (type == null)
			return "button";
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getGroup() {
		if (group == null)
			return -1;
		return group;
	}

	public void setGroup(Integer group) {
		this.group = group;
	}

	public String getOnexecute() {
		if (onexecute == null)
			return "";
		return onexecute;
	}

	public void setOnexecute(String onexecute) {
		this.onexecute = onexecute;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}
}
