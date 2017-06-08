package kxd.engine.cache;

import java.io.Serializable;

import kxd.util.Streamable;

public interface CachedIdable<K extends Serializable> extends Serializable,
		Streamable {
	/**
	 * 获取ID
	 * 
	 */
	public K getId();

	public void setId(K id);
}
