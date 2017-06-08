package kxd.engine.scs.admin;

import kxd.util.IdableObject;
import kxd.util.ListItem;

public class IntegerItem extends ListItem<Integer> {
	private static final long serialVersionUID = -5731476243475092620L;
	private String text;

	public IntegerItem() {
		super();
	}

	public IntegerItem(Integer id) {
		super(id);
	}

	public IntegerItem(Integer id, String text) {
		super(id);
		this.text = text;
	}

	@Override
	public IdableObject<Integer> createObject() {
		return new IntegerItem();
	}

	@Override
	public String getIdString() {
		return getId().toString();
	}

	@Override
	public void setIdString(String id) {
		setId(Integer.valueOf(id));
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
