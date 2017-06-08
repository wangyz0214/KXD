package kxd.remote.scs.beans;

import kxd.remote.scs.util.emun.FileVisitRight;
import kxd.util.IdableObject;
import kxd.util.ListItem;

import org.apache.log4j.Logger;

public class BaseFileOwner extends ListItem<Short> {
	private static final long serialVersionUID = 1L;
	private String fileOwnerDesp;
	private FileVisitRight visitRight;

	@Override
	public IdableObject<Short> createObject() {
		return new BaseFileOwner();
	}

	public void copyData(Object src) {
		super.copyData(src);
		if (!(src instanceof BaseFileOwner))
			return;
		BaseFileOwner d = (BaseFileOwner) src;
		fileOwnerDesp = d.fileOwnerDesp;
		visitRight = d.visitRight;
	}

	public BaseFileOwner() {
		super();
	}

	public BaseFileOwner(Short id) {
		super(id);
	}

	public Short getFileOwnerId() {
		return getId();
	}

	public void setFileOwnerId(Short id) {
		setId(id);
	}

	@Override
	protected String toDisplayLabel() {
		return fileOwnerDesp;
	}

	@Override
	public String toString() {
		return fileOwnerDesp + "(" + getId() + ")";
	}

	public String getFileOwnerDesp() {
		return fileOwnerDesp;
	}

	public void setFileOwnerDesp(String fileOwnerDesp) {
		this.fileOwnerDesp = fileOwnerDesp;
	}

	public FileVisitRight getVisitRight() {
		return visitRight;
	}

	public void setVisitRight(FileVisitRight visitRight) {
		this.visitRight = visitRight;
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
		if (id == null || id.trim().isEmpty())
			setId(null);
		else
			setId(Short.parseShort(id));
	}

	@Override
	protected void loggingContent(Logger logger, String prefix) {
		logger.debug(prefix + "id: " + getId() + ";");
		logger.debug(prefix + "desp: " + fileOwnerDesp + ";");
	}

	@Override
	public String getText() {
		return fileOwnerDesp;
	}

	@Override
	public void setText(String text) {
		fileOwnerDesp = text;
	}
}
