package kxd.engine.ui.tags.website;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import kxd.util.DataUnit;

public class FormatMoney extends BaseTagSupport {
	private static final long serialVersionUID = 6515054077632949892L;
	Object value;
	private int srcDots, destDots;

	@Override
	protected int endTag(JspWriter writer) throws JspException, IOException {
		return EVAL_PAGE;
	}

	@Override
	protected int startTag(JspWriter writer) throws JspException, IOException {
		if (value != null)
			writer.write(DataUnit.formatNumber(value.toString(), srcDots,
					destDots));
		else
			writer.write(DataUnit.formatNumber("0", srcDots, destDots));
		return EVAL_BODY_INCLUDE;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public int getSrcDots() {
		return srcDots;
	}

	public void setSrcDots(int srcDots) {
		this.srcDots = srcDots;
	}

	public int getDestDots() {
		return destDots;
	}

	public void setDestDots(int destDots) {
		this.destDots = destDots;
	}

}
