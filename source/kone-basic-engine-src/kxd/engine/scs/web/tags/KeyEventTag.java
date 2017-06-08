package kxd.engine.scs.web.tags;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;

class KeyCommand {
	String key;
	String link, action;
}

public class KeyEventTag extends BaseTag {
	private static final long serialVersionUID = 1L;

	/**
	 * 按钮事件
	 */
	private String onKeyPress;
	ArrayList<KeyCommand> commandList = new ArrayList<KeyCommand>();

	public int doStartTag() throws JspTagException {
		commandList.clear();
		return EVAL_BODY_INCLUDE;
	}

	public int doEndTag() throws JspTagException {
		JspWriter writer = pageContext.getOut();
		try {
			Iterator<KeyCommand> it = commandList.iterator();
			if (it.hasNext() || getOnKeyPress() != null) {
				writer.write("<script type='text/javascript'>");
				if (getOnKeyPress() != null)
					writer.write("var keyeventtag_keypress=function(keyCode){"
							+ getOnKeyPress() + "};");
				writer.write("ExtInf.keyReceiveFuncList[ExtInf.keyReceiveFuncList.length]=function(k){");
				if (getOnKeyPress() != null)
					writer.write("keyeventtag_keypress(k);");
				boolean start = true;
				while (it.hasNext()) {
					KeyCommand cmd = it.next();
					if (!start)
						writer.write("else ");
					else
						start = false;
					writer.write("if(k==" + cmd.key + "){");
					if (cmd.link != null) {
						writer.write("document.location='");
						writeUrl(writer, cmd.link);
						writer.write("';");
					}
					if (cmd.action != null) {
						writer.write(cmd.action);
					}
					writer.write("}");
				}
				writer.write("};");
				writer.write("</script>");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return EVAL_PAGE;
	}

	public String getOnKeyPress() {
		return onKeyPress;
	}

	public void setOnKeyPress(String onKeyPress) {
		this.onKeyPress = onKeyPress;
	}

}
