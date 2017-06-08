package kxd.remote.scs.beans;

import kxd.util.IdableObject;
import kxd.util.ListItem;

import org.apache.log4j.Logger;

public class BaseBusiness extends ListItem<Integer> {
	private static final long serialVersionUID = 1L;
	private String businessDesp;
	private BaseBusinessCategory businessCategory;

	@Override
	public String getText() {
		return businessDesp;
	}

	@Override
	public void setText(String text) {
		businessDesp = text;
	}

	@Override
	protected void loggingContent(Logger logger, String prefix) {
		logger.debug(prefix + "id: " + getId() + ";");
		logger.debug(prefix + "desp: " + businessDesp + ";");
		logger.debug(prefix + "businessCategory: ");
		businessCategory.debug(logger, prefix + "  ");
	}

	public BaseBusinessCategory getBusinessCategory() {
		return businessCategory;
	}

	public void setBusinessCategory(BaseBusinessCategory businessCategory) {
		this.businessCategory = businessCategory;
	}

	@Override
	public IdableObject<Integer> createObject() {
		return new BaseBusiness();
	}

	public void copyData(Object src) {
		super.copyData(src);
		if (!(src instanceof BaseBusiness))
			return;
		BaseBusiness d = (BaseBusiness) src;
		businessDesp = d.businessDesp;
		businessCategory = d.businessCategory;
	}

	public BaseBusiness() {
		super();
	}

	public BaseBusiness(Integer id) {
		super(id);
	}

	public Integer getBusinessId() {
		return getId();
	}

	public void setBusinessId(Integer id) {
		setId(id);
	}

	public BaseBusiness(Integer id, String desp) {
		setId(id);
		this.businessDesp = desp;
	}

	public String getBusinessDesp() {
		return businessDesp;
	}

	public void setBusinessDesp(String businessDesp) {
		this.businessDesp = businessDesp;
	}

	@Override
	protected String toDisplayLabel() {
		return businessDesp;
	}

	@Override
	public String toString() {
		return businessDesp + "(" + getId() + ")";
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
