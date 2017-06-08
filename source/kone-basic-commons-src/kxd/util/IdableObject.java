package kxd.util;

import java.io.Serializable;

import org.apache.log4j.Logger;

public abstract class IdableObject<E> implements Serializable, Copyable,
		Debugable, Idable<E> {
	private static final long serialVersionUID = 1L;
	private E id;
	private String displayLabel;

	public IdableObject() {
		super();
	}

	public IdableObject(E id) {
		super();
		this.id = id;
	}

	public void copy(Object src) {
		copyData(src);
	}

	@SuppressWarnings("unchecked")
	public void copyData(Object src) {
		if (!(src instanceof IdableObject<?>))
			return;
		IdableObject<E> d = (IdableObject<E>) src;
		id = d.id;
		displayLabel = d.displayLabel;
	}

	public abstract IdableObject<E> createObject();

	public IdableObject<E> copy() {
		IdableObject<E> obj = createObject();
		obj.copy(this);
		return obj;
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
		final IdableObject<E> other = (IdableObject<E>) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public E getId() {
		return id;
	}

	public void setId(E id) {
		this.id = id;
	}

	abstract protected String toDisplayLabel();

	public String getDisplayLabel() {
		if (displayLabel == null)
			displayLabel = toDisplayLabel();
		return displayLabel;
	}

	public void setDisplayLabel(String displayLabel) {
		this.displayLabel = displayLabel;
	}

	protected void loggingStart(Logger logger, String prefix) {
		logger.debug(prefix + this.getClass().getSimpleName() + "{");
	}

	protected void loggingContent(Logger logger, String prefix) {
		logger.debug(prefix + "id=" + getIdString());
	}

	protected void loggingEnd(Logger logger, String prefix) {
		logger.debug(prefix + "}");
	}

	public void debug(Logger logger, String prefix) {
		if (!logger.isDebugEnabled())
			return;
		loggingStart(logger, prefix);
		loggingContent(logger, prefix + "  ");
		loggingEnd(logger, prefix);
	}

	@Override
	public String toString() {
		return getDisplayLabel();
	}
}
