package kxd.engine.cache.beans;

abstract public class CachedStringObject extends CachedObject<String> {
	private static final long serialVersionUID = -1L;

	public CachedStringObject(String id, boolean isNullValue) {
		super(id, isNullValue);
	}

	public CachedStringObject(String id) {
		super(id);
	}

	public CachedStringObject() {
		super();
	}

	@Override
	public String getIdString() {
		return getId();
	}

	@Override
	public void setIdString(String id) {
		setId(id);
	}

}
