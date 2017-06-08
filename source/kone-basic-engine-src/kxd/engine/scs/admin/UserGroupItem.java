package kxd.engine.scs.admin;

import kxd.util.IdableObject;
import kxd.util.ListItem;

public class UserGroupItem extends ListItem<Integer> {
	private static final long serialVersionUID = 2577976427791977585L;
	String text;

	public UserGroupItem() {

	}

	public UserGroupItem(Integer id, String text) {
		setId(id);
		setText(text);
	}

	@Override
	public IdableObject<Integer> createObject() {
		return new UserGroupItem();
	}

	@Override
	public String getIdString() {
		return Integer.toString(getId());
	}

	@Override
	public void setIdString(String id) {
		setId(Integer.valueOf(id));
	}

	@Override
	protected String toDisplayLabel() {
		return text;
	}

	@Override
	public String getText() {
		return text;
	}

	@Override
	public void setText(String text) {
		this.text = text;
	}

}