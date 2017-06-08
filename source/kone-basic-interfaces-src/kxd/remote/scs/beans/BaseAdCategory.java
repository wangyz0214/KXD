package kxd.remote.scs.beans;

import kxd.util.IdableObject;
import kxd.util.ListItem;

import org.apache.log4j.Logger;

public class BaseAdCategory extends ListItem<Short> {
	private static final long serialVersionUID = 1L;
	private String adCategoryDesp;

	@Override
	public IdableObject<Short> createObject() {
		return new BaseAdCategory();
	}

	public void copyData(Object src) {
		super.copyData(src);
		if (!(src instanceof BaseAdCategory))
			return;
		BaseAdCategory d = (BaseAdCategory) src;
		adCategoryDesp = d.adCategoryDesp;
	}

	public BaseAdCategory() {
		super();
	}

	public BaseAdCategory(Short id) {
		super(id);
	}

	public Short getAdCategoryId() {
		return getId();
	}

	public void setAdCategoryId(Short id) {
		setId(id);
	}

	public String getAdCategoryDesp() {
		return adCategoryDesp;
	}

	public void setAdCategoryDesp(String adCategoryDesp) {
		this.adCategoryDesp = adCategoryDesp;
	}

	@Override
	protected String toDisplayLabel() {
		return adCategoryDesp;
	}

	@Override
	public String toString() {
		return adCategoryDesp + "(" + getId() + ")";
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
		logger.debug(prefix + "desp: " + getAdCategoryDesp() + ";");
	}

	@Override
	public String getText() {
		return adCategoryDesp;
	}

	@Override
	public void setText(String text) {
		adCategoryDesp = text;
	}
}
