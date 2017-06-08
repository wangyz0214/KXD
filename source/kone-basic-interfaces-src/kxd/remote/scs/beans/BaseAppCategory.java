package kxd.remote.scs.beans;

import kxd.util.IdableObject;
import kxd.util.ListItem;

import org.apache.log4j.Logger;

public class BaseAppCategory extends ListItem<Integer> {
	private static final long serialVersionUID = 1L;
	private String appCategoryDesp;

	@Override
	public IdableObject<Integer> createObject() {
		return new BaseAppCategory();
	}

	public void copyData(Object src) {
		super.copyData(src);
		if (!(src instanceof BaseAppCategory))
			return;
		BaseAppCategory d = (BaseAppCategory) src;
		appCategoryDesp = d.appCategoryDesp;
	}

	public BaseAppCategory() {
		super();
	}

	public BaseAppCategory(Integer id) {
		super(id);
	}

	public BaseAppCategory(Integer id, String desp) {
		super(id);
		appCategoryDesp = desp;
	}

	public Integer getAppCategoryId() {
		return getId();
	}

	public void setAppCategoryId(Integer id) {
		setId(id);
	}

	public String getAppCategoryDesp() {
		return appCategoryDesp;
	}

	public void setAppCategoryDesp(String appCategoryDesp) {
		this.appCategoryDesp = appCategoryDesp;
	}

	@Override
	protected String toDisplayLabel() {
		return appCategoryDesp;
	}

	@Override
	public String toString() {
		return appCategoryDesp + "(" + getId() + ")";
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

	@Override
	protected void loggingContent(Logger logger, String prefix) {
		logger.debug(prefix + "id: " + getId() + ";");
		logger.debug(prefix + "desp: " + getAppCategoryDesp() + ";");
	}

	@Override
	public String getText() {
		return appCategoryDesp;
	}

	@Override
	public void setText(String text) {
		appCategoryDesp = text;
	}

}
