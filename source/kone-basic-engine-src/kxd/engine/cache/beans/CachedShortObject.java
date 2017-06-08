package kxd.engine.cache.beans;

abstract public class CachedShortObject extends CachedObject<Short> {
	private static final long serialVersionUID = -1L;

	public CachedShortObject(Short id, boolean isNullValue) {
		super(id, isNullValue);
	}

	public CachedShortObject(Short id) {
		super(id);
	}

	public CachedShortObject() {
		super();
	}

	@Override
	public String getIdString() {
		if (getId() == null)
			return null;
		else
			return Short.toString(getId());
	}

	@Override
	public void setIdString(String id) {
		if (id == null)
			setId(null);
		else
			setId(Short.valueOf(id));
	}

}
