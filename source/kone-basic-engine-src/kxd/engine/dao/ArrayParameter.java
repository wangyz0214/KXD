package kxd.engine.dao;

import java.io.Serializable;

public class ArrayParameter implements Serializable {
	private static final long serialVersionUID = 1L;
	String itemTypeName;
	String typeName;
	Object[] values;

	public ArrayParameter(String itemTypeName, String typeName, Object[] values) {
		super();
		this.itemTypeName = itemTypeName;
		this.values = values;
		this.typeName = typeName;
	}

	public String getItemTypeName() {
		return itemTypeName;
	}

	public void setItemTypeName(String itemTypeName) {
		this.itemTypeName = itemTypeName;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public Object[] getValues() {
		return values;
	}

	public void setValues(Object[] values) {
		this.values = values;
	}

}
