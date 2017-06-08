package kxd.engine.cache.beans;

import java.io.IOException;
import java.io.Serializable;

import kxd.engine.cache.CacheConfigParams;
import kxd.util.DateTime;
import kxd.util.ListItem;
import kxd.util.memcached.Cacheable;
import kxd.util.stream.Stream;

abstract public class CachedObject<E extends Serializable> extends ListItem<E>
		implements Cacheable<E> {
	private static final long serialVersionUID = -5029178863626289657L;
	static private CacheConfigParams params = new CacheConfigParams("db",
			"kxd-ejb-termCacheBean", memCachedClientMap.get("terminal"));
	final static private int RELATED_OBJECTS_UPDATE_TIME = 30;
	private boolean nullValue = false;

	protected boolean isRelatedObjectNeedUpdate(long lastUpdateTime) {
		return DateTime.secondsBetween(lastUpdateTime,
				System.currentTimeMillis()) > RELATED_OBJECTS_UPDATE_TIME;
	}

	public CachedObject(E id) {
		super(id);
	}

	public CachedObject(E id, boolean isNullValue) {
		super(id);
		this.nullValue = isNullValue;
	}

	public CachedObject() {
		super();
	}

	static public CacheConfigParams getParams() {
		return params;
	}

	@Override
	protected String toDisplayLabel() {
		return getText();
	}

	abstract protected void doReadData(Stream stream) throws IOException;

	abstract protected void doWriteData(Stream stream) throws IOException;

	public boolean isNullValue() {
		return nullValue;
	}

	public void setNullValue(boolean nullValue) {
		this.nullValue = nullValue;
	}

	@Override
	public void readData(Stream stream) throws IOException {
		this.nullValue = stream.readBoolean(3000);
		if (!nullValue)
			doReadData(stream);
	}

	@Override
	public void writeData(Stream stream) throws IOException {
		stream.writeBoolean(nullValue, 3000);
		if (!nullValue)
			doWriteData(stream);
	}
}
