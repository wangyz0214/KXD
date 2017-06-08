package kxd.remote.scs.beans;

import kxd.util.IdableObject;
import kxd.util.ListItem;

import org.apache.log4j.Logger;

public class BaseFileUser extends ListItem<String> {
	private static final long serialVersionUID = 1L;
	private String fileUserPwd;
	private BaseFileOwner fileOwner;

	@Override
	public IdableObject<String> createObject() {
		return new BaseFileUser();
	}

	public void copyData(Object src) {
		super.copyData(src);
		if (!(src instanceof BaseFileUser))
			return;
		BaseFileUser d = (BaseFileUser) src;
		fileUserPwd = d.fileUserPwd;
		fileOwner = d.fileOwner;
	}

	public BaseFileUser() {
		super();
	}

	public BaseFileUser(String id) {
		super(id);
	}

	public String getFileUserId() {
		return getId();
	}

	public void setFileUserId(String id) {
		setId(id);
	}

	@Override
	protected String toDisplayLabel() {
		return getId();
	}

	@Override
	public String toString() {
		return getId();
	}

	public String getFileUserPwd() {
		return fileUserPwd;
	}

	public void setFileUserPwd(String fileUserPwd) {
		this.fileUserPwd = fileUserPwd;
	}

	public BaseFileOwner getFileOwner() {
		return fileOwner;
	}

	public void setFileOwner(BaseFileOwner fileOwner) {
		this.fileOwner = fileOwner;
	}

	@Override
	public String getIdString() {
		if (getId() == null)
			return null;
		else
			return getId().toString();
	}

	@Override
	public void setIdString(String id) {
		setId(id);
	}

	@Override
	protected void loggingContent(Logger logger, String prefix) {
		logger.debug(prefix + "id: " + getId() + ";");
	}

	@Override
	public String getText() {
		return getId();
	}

	@Override
	public void setText(String text) {
		setId(text);
	}
}
