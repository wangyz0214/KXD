package kxd.engine.scs.admin;

import kxd.util.IdableObject;
import kxd.util.ListItem;

public class StringItem extends ListItem<String> {
	private static final long serialVersionUID = -5731476243475092620L;
	private String text;

	public StringItem() {
		super();
	}

	public StringItem(String id) {
		super(id);
	}

	public StringItem(String id, String text) {
		super(id);
		this.text = text;
	}

	@Override
	public IdableObject<String> createObject() {
		return new StringItem();
	}

	@Override
	public String getIdString() {
		return getId().toString();
	}

	@Override
	public void setIdString(String id) {
		setId(id);
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
