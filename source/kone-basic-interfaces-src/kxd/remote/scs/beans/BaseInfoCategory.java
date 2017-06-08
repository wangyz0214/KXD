package kxd.remote.scs.beans;

import kxd.util.IdableObject;
import kxd.util.ListItem;

import org.apache.log4j.Logger;

public class BaseInfoCategory extends ListItem<Short> {
	private static final long serialVersionUID = 1L;
	private String infoCategoryDesp;

	@Override
	public IdableObject<Short> createObject() {
		return new BaseInfoCategory();
	}

	public void copyData(Object src) {
		super.copyData(src);
		if (!(src instanceof BaseInfoCategory))
			return;
		BaseInfoCategory d = (BaseInfoCategory) src;
		infoCategoryDesp = d.infoCategoryDesp;
	}

	public BaseInfoCategory() {
		super();
	}

	public BaseInfoCategory(Short id) {
		super(id);
	}

	public Short getInfoCategoryId() {
		return getId();
	}

	public void setInfoCategoryId(Short id) {
		setId(id);
	}

	public String getInfoCategoryDesp() {
		return infoCategoryDesp;
	}

	public void setInfoCategoryDesp(String infoCategoryDesp) {
		this.infoCategoryDesp = infoCategoryDesp;
	}

	@Override
	protected String toDisplayLabel() {
		return infoCategoryDesp;
	}

	@Override
	public String toString() {
		return infoCategoryDesp + "(" + getId() + ")";
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
		logger.debug(prefix + "desp: " + getInfoCategoryDesp() + ";");
	}

	@Override
	public String getText() {
		return infoCategoryDesp;
	}

	@Override
	public void setText(String text) {
		infoCategoryDesp = text;
	}
}
