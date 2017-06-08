package kxd.engine.scs.web.tags;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspTag;

public class KeyCommandTag extends BaseTag {
	private static final long serialVersionUID = 1L;
	String keyCode;
	String link, action;
	String sendKey, handleKey;

	@Override
	public int doStartTag() throws JspException {
		JspTag parent = getParent();
		if (parent instanceof KeyEventTag) {
			KeyEventTag tag = (KeyEventTag) parent;
			KeyCommand cmd = new KeyCommand();
			cmd.key = keyCode;
			cmd.link = link;
			cmd.action = action;
			tag.commandList.add(cmd);
		}
		return super.doStartTag();
	}

	public String getKeyCode() {
		return keyCode;
	}

	public void setKeyCode(String keyCode) {
		this.keyCode = keyCode;
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
