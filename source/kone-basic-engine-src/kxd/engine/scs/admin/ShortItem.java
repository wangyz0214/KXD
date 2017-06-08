package kxd.engine.scs.admin;

import kxd.util.IdableObject;
import kxd.util.ListItem;

public class ShortItem extends ListItem<Short> {
	private static final long serialVersionUID = -5731476243475092620L;
	private String text;

	public ShortItem() {
		super();
	}

	public ShortItem(Short id) {
		super(id);
	}

	public ShortItem(Short id, String text) {
		super(id);
		this.text = text;
	}

	@Override
	public IdableObject<Short> createObject() {
		return new ShortItem();
	}

	@Override
	public String getIdString() {
		return getId().toString();
	}

	@Override
	public void setIdString(String id) {
		setId(Short.valueOf(id));
	}

	@Override
	protected String toDisplayLabel() {
		return text;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}
