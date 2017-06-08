package kxd.util;

import org.apache.log4j.Logger;

public abstract class IdCheckableObject<E> extends IdableObject<E> implements
		IdCheckable<E> {
	private static final long serialVersionUID = 1L;
	private boolean checked;

	public IdCheckableObject() {
		super();
	}

	public IdCheckableObject(E id) {
		super(id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void copyData(Object src) {
		super.copyData(src);
		if (!(src instanceof IdCheckableObject<?>))
			return;
		IdCheckableObject<E> d = (IdCheckableObject<E>) src;
		checked = d.checked;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	@Override
	protected void loggingContent(Logger logger, String prefix) {
		super.loggingContent(logger, prefix);
		logger.debug( prefix + "checked=" + checked);
	}

}
