package kxd.util;

public class KeyValue<K, V> extends Value<V> {
	private static final long serialVersionUID = 4888626895022498453L;
	private K key;

	public KeyValue() {
		super();
	}

	public KeyValue(K key, V value) {
		super(value);
		this.key = key;
	}

	public K getKey() {
		return key;
	}

	public void setKey(K key) {
		this.key = key;
	}
}
