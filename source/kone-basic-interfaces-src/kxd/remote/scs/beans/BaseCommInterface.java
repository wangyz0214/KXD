package kxd.remote.scs.beans;

import kxd.util.IdableObject;
import kxd.util.ListItem;

import org.apache.log4j.Logger;

public class BaseCommInterface extends ListItem<Short> {
	private static final long serialVersionUID = 1L;
	private String desp;
	private int type;

	@Override
	public IdableObject<Short> createObject() {
		return new BaseCommInterface();
	}

	public void copyData(Object src) {
		super.copyData(src);
		if (!(src instanceof BaseCommInterface))
			return;
		BaseCommInterface d = (BaseCommInterface) src;
		desp = d.desp;
		type = d.type;
	}

	public BaseCommInterface() {
		super();
	}

	public BaseCommInterface(Short id) {
		super(id);
	}

	public String getDesp() {
		return desp;
	}

	public void setDesp(String desp) {
		this.desp = desp;
	}

	@Override
	protected String toDisplayLabel() {
		return desp;
	}

	@Override
	public String toString() {
		return desp + "(" + getId() + ")";
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
		logger.debug(prefix + "desp: " + getDesp() + ";");
	}

	@Override
	public String getText() {
		return desp;
	}

	@Override
	public void setText(String text) {
		desp = text;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
}
