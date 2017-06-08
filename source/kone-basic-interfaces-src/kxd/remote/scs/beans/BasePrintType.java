package kxd.remote.scs.beans;

import kxd.util.ListItem;
import kxd.util.IdableObject;
import org.apache.log4j.Logger;

public class BasePrintType extends ListItem<Short> {
	private static final long serialVersionUID = 1L;
	private String typeDesp;

	@Override
	public String getText() {
		return typeDesp;
	}

	@Override
	public void setText(String text) {
		typeDesp = text;
	}

	@Override
	protected void loggingContent(Logger logger, String prefix) {
		logger.debug(prefix + "id: " + getId() + ";");
		logger.debug(prefix + "desp: " + typeDesp + ";");
	}

	public BasePrintType() {
		super();
	}

	public BasePrintType(Short id) {
		super(id);
	}

	public void copyData(Object src) {
		super.copyData(src);
		if (!(src instanceof BasePrintType))
			return;
		BasePrintType d = (BasePrintType) src;
		typeDesp = d.typeDesp;
	}

	@Override
	public IdableObject<Short> createObject() {
		return new BasePrintType();
	}

	public Short getPrintType() {
		return getId();
	}

	public void setPrintType(Short id) {
		setId(id);
	}

	public String getPrintTypeDesp() {
		return typeDesp;
	}

	public void setPrintTypeDesp(String typeDesp) {
		this.typeDesp = typeDesp;
	}

	@Override
	protected String toDisplayLabel() {
		return typeDesp;
	}

	@Override
	public String toString() {
		return typeDesp + "(" + getId() + ")";
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
}
