package kxd.remote.scs.beans;

import kxd.util.ListItem;
import kxd.util.IdableObject;
import org.apache.log4j.Logger;

public class BaseTermType extends ListItem<Integer> {
	private static final long serialVersionUID = 1L;
	private String typeDesp;
	private BaseManuf manuf;

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
		logger.debug(prefix + "manuf: ");
		manuf.debug(logger, prefix + "  ");
	}

	public BaseTermType() {
		super();
	}

	public BaseTermType(Integer id) {
		super(id);
	}

	public BaseTermType(Integer id, String typeDesp) {
		super(id);
		this.typeDesp = typeDesp;
	}

	public void copyData(Object src) {
		super.copyData(src);
		if (!(src instanceof BaseTermType))
			return;
		BaseTermType d = (BaseTermType) src;
		typeDesp = d.typeDesp;
		manuf = d.manuf;
	}

	@Override
	public IdableObject<Integer> createObject() {
		return new BaseTermType();
	}

	public Integer getTermType() {
		return getId();
	}

	public void setTermType(Integer id) {
		setId(id);
	}

	public String getTypeDesp() {
		return typeDesp;
	}

	public void setTypeDesp(String typeDesp) {
		this.typeDesp = typeDesp;
	}

	public BaseManuf getManuf() {
		return manuf;
	}

	public void setManuf(BaseManuf manuf) {
		this.manuf = manuf;
	}

	@Override
	protected String toDisplayLabel() {
		if (manuf != null)
			return manuf.getManufName() + "-" + typeDesp;
		else
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
			setId(Integer.parseInt(id));
	}
}
