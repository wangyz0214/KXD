package kxd.remote.scs.beans;

import kxd.util.IdableObject;
import kxd.util.ListItem;

import org.apache.log4j.Logger;

public class BaseFileContentType extends ListItem<String> {
	private static final long serialVersionUID = 1L;
	private String contentType;

	@Override
	public IdableObject<String> createObject() {
		return new BaseFileContentType();
	}

	public void copyData(Object src) {
		super.copyData(src);
		if (!(src instanceof BaseFileContentType))
			return;
		BaseFileContentType d = (BaseFileContentType) src;
		contentType = d.contentType;
	}

	public BaseFileContentType() {
		super();
	}

	public BaseFileContentType(String id) {
		super(id);
	}

	public String getExtName() {
		return getId();
	}

	public void setExtName(String id) {
		setId(id);
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	@Override
	protected String toDisplayLabel() {
		return contentType;
	}

	@Override
	public String toString() {
		return contentType + "(" + getId() + ")";
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
		logger.debug(prefix + "desp: " + getContentType() + ";");
	}

	@Override
	public String getText() {
		return contentType;
	}

	@Override
	public void setText(String text) {
		contentType = text;
	}
}
