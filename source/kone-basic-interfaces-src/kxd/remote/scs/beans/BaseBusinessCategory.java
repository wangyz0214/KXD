package kxd.remote.scs.beans;

import kxd.util.IdableObject;
import kxd.util.ListItem;

import org.apache.log4j.Logger;

public class BaseBusinessCategory extends ListItem<Integer> {
	private static final long serialVersionUID = 1L;
	private String businessCategoryDesp;

	@Override
	public String getText() {
		return businessCategoryDesp;
	}

	@Override
	public void setText(String text) {
		businessCategoryDesp = text;
	}

	@Override
	protected void loggingContent(Logger logger, String prefix) {
		logger.debug(prefix + "id: " + getId() + ";");
		logger.debug(prefix + "desp: " + businessCategoryDesp + ";");
	}

	@Override
	public IdableObject<Integer> createObject() {
		return new BaseBusinessCategory();
	}

	@Override
	public void copyData(Object src) {
		super.copyData(src);
		if (!(src instanceof BaseBusinessCategory))
			return;
		BaseBusinessCategory d = (BaseBusinessCategory) src;
		businessCategoryDesp = d.businessCategoryDesp;
	}

	public BaseBusinessCategory() {
		super();
	}

	public BaseBusinessCategory(Integer id) {
		super(id);
	}

	public Integer getBusinessCategoryId() {
		return getId();
	}

	public void setBusinessCategoryId(Integer id) {
		setId(id);
	}

	public BaseBusinessCategory(Integer id, String desp) {
		setId(id);
		this.businessCategoryDesp = desp;
	}

	public String getBusinessCategoryDesp() {
		return businessCategoryDesp;
	}

	public void setBusinessCategoryDesp(String businessCategoryDesp) {
		this.businessCategoryDesp = businessCategoryDesp;
	}

	@Override
	protected String toDisplayLabel() {
		return businessCategoryDesp;
	}

	@Override
	public String toString() {
		return businessCategoryDesp + "(" + getId() + ")";
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
