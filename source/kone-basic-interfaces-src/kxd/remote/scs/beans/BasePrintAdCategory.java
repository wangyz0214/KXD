package kxd.remote.scs.beans;

import kxd.util.IdableObject;
import kxd.util.ListItem;

import org.apache.log4j.Logger;

public class BasePrintAdCategory extends ListItem<Short> {
	private static final long serialVersionUID = 1L;
	private String printAdCategoryDesp;

	@Override
	public IdableObject<Short> createObject() {
		return new BasePrintAdCategory();
	}

	public void copyData(Object src) {
		super.copyData(src);
		if (!(src instanceof BasePrintAdCategory))
			return;
		BasePrintAdCategory d = (BasePrintAdCategory) src;
		printAdCategoryDesp = d.printAdCategoryDesp;
	}

	public BasePrintAdCategory() {
		super();
	}

	public BasePrintAdCategory(Short id) {
		super(id);
	}

	public Short getPrintAdCategoryId() {
		return getId();
	}

	public void setPrintAdCategoryId(Short id) {
		setId(id);
	}

	public String getPrintAdCategoryDesp() {
		return printAdCategoryDesp;
	}

	public void setPrintAdCategoryDesp(String printAdCategoryDesp) {
		this.printAdCategoryDesp = printAdCategoryDesp;
	}

	@Override
	protected String toDisplayLabel() {
		return printAdCategoryDesp;
	}

	@Override
	public String toString() {
		return printAdCategoryDesp + "(" + getId() + ")";
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
		logger.debug(prefix + "desp: " + getPrintAdCategoryDesp() + ";");
	}

	@Override
	public String getText() {
		return printAdCategoryDesp;
	}

	@Override
	public void setText(String text) {
		printAdCategoryDesp = text;
	}
}
