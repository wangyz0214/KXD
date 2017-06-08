package kxd.remote.scs.beans;

import kxd.util.IdableObject;
import kxd.util.ListItem;

import org.apache.log4j.Logger;

public class BasePageElement extends ListItem<Integer> {
	private static final long serialVersionUID = 1L;
	private String pageDesp;

	@Override
	public String getText() {
		return pageDesp;
	}

	@Override
	public void setText(String text) {
		pageDesp = text;
	}

	@Override
	protected void loggingContent(Logger logger, String prefix) {
		logger.debug( prefix + "id: " + getId() + ";");
		logger.debug( prefix + "desp: " + pageDesp + ";");
	}

	@Override
	public IdableObject<Integer> createObject() {
		return new BasePageElement();
	}

	public BasePageElement(Integer id, String pageDesp) {
		super(id);
		this.pageDesp = pageDesp;
	}

	public void copyData(Object src) {
		super.copyData(src);
		if (!(src instanceof BasePageElement))
			return;
		BasePageElement d = (BasePageElement) src;
		pageDesp = d.pageDesp;
	}

	public BasePageElement() {
		super();
	}

	public BasePageElement(Integer id) {
		super(id);
	}

	public Integer getPageId() {
		return getId();
	}

	public void setPageId(Integer id) {
		setId(id);
	}

	public String getPageDesp() {
		return pageDesp;
	}

	public void setPageDesp(String pageDesp) {
		this.pageDesp = pageDesp;
	}

	@Override
	protected String toDisplayLabel() {
		return pageDesp;
	}

	@Override
	public String toString() {
		return pageDesp + "(" + getId() + ")";
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
