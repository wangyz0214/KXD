package kxd.util;

public class Value<V> implements java.io.Serializable {
	private static final long serialVersionUID = 4888626895022498453L;
	private V value;

	public Value() {
		super();
	}

	public Value(V value) {
		super();
		this.value = value;
	}

	public V getValue() {
		return value;
	}

	public void setValue(V value) {
		this.value = value;
	}

}
