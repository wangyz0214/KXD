package kxd.engine.dao;

import java.io.Serializable;

public class CallableOutParameter implements Serializable {
	private static final long serialVersionUID = 1L;
	int type;
	Integer scale;
	String typeName;
	Object value;

	public CallableOutParameter(int type, Integer scale, String typeName) {
		super();
		this.type = type;
		this.scale = scale;
		this.typeName = typeName;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Integer getScale() {
		return scale;
	}

	public void setScale(Integer scale) {
		this.scale = scale;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

}
