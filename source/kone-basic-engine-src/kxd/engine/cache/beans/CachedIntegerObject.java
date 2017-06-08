package kxd.engine.cache.beans;

abstract public class CachedIntegerObject extends CachedObject<Integer> {
	private static final long serialVersionUID = -1L;

	public CachedIntegerObject(Integer id) {
		super(id);
	}

	public CachedIntegerObject(Integer id, boolean isNullValue) {
		super(id, isNullValue);
	}

	public CachedIntegerObject() {
		super();
	}

	@Override
	public String getIdString() {
		if (getId() == null)
			return null;
		else
			return Integer.toString(getId());
	}

	@Override
	public void setIdString(String id) {
		if (id == null)
			setId(null);
		else
			setId(Integer.valueOf(id));
	}

}
