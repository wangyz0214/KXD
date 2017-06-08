package kxd.engine.scs.admin;

import kxd.util.IdableObject;
import kxd.util.ListItem;

public class BooleanItem extends ListItem<Boolean> {
	private static final long serialVersionUID = 1L;

	public BooleanItem() {
		super();
	}

	public BooleanItem(Boolean id) {
		super(id);
	}

	@Override
	public IdableObject<Boolean> createObject() {
		return new BooleanItem();
	}

	@Override
	public String getIdString() {
		return getId().toString();
	}

	@Override
	public void setIdString(String id) {
		setId(Boolean.valueOf(id));
	}

	@Override
	protected String toDisplayLabel() {
		return Boolean.TRUE.equals(getId()) ? "是" : "否";
	}

	@Override
	public String getText() {
		return Boolean.TRUE.equals(getId()) ? "是" : "否";
	}

	@Override
	public void setText(String text) {
		if (text.equals("是"))
			setId(true);
		else
			setId(false);
	}

}
