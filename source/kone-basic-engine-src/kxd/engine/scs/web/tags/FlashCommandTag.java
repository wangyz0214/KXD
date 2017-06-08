package kxd.engine.scs.web.tags;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspTag;

public class FlashCommandTag extends BaseTag {
	private static final long serialVersionUID = 1L;
	String command, params;
	String link, action;
	String sendKey, handleKey;

	@Override
	public int doStartTag() throws JspException {
		JspTag parent = getParent();
		if (parent instanceof FlashTag) {
			FlashTag tag = (FlashTag) parent;
			FlashCommand cmd = new FlashCommand();
			cmd.command = command;
			cmd.params = params;
			cmd.link = link;
			cmd.action = action;
			cmd.sendKey = sendKey;
			cmd.handleKey = handleKey;
			tag.commandList.add(cmd);
		}
		return super.doStartTag();
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getParams() {
		return params;
	}

	public void setParams(String params) {
		this.params = params;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getSendKey() {
		return sendKey;
	}

	public void setSendKey(String sendKey) {
		this.sendKey = sendKey;
	}

	public String getHandleKey() {
		return handleKey;
	}

	public void setHandleKey(String handleKey) {
		this.handleKey = handleKey;
	}

}
