package kxd.engine.cache.beans;

import java.io.IOException;
import java.io.Serializable;

import kxd.engine.cache.CachedIdable;
import kxd.util.stream.Stream;

abstract public class CachedIdObject<E extends Serializable> implements
		CachedIdable<E> {
	private static final long serialVersionUID = -5029178863626289657L;
	E id;

	public CachedIdObject(E id) {
		this.id = id;
	}

	public CachedIdObject() {
		super();
	}

	public E getId() {
		return id;
	}

	public void setId(E id) {
		this.id = id;
	}

	@Override
	public void readData(Stream stream) throws IOException {
	}

	@Override
	public void writeData(Stream stream) throws IOException {
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CachedIdObject<E> other = (CachedIdObject<E>) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "" + id;
	}

}
