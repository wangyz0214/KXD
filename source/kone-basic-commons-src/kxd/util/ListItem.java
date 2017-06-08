package kxd.util;

import org.apache.log4j.Logger;

public abstract class ListItem<E> extends IdCheckableObject<E> implements
		ListItemable<E> {
	private static final long serialVersionUID = 1L;
	private boolean disabled;
	private boolean selected;

	public ListItem() {
		super();
	}

	public ListItem(E id) {
		super(id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void copyData(Object src) {
		super.copyData(src);
		if (!(src instanceof ListItem<?>))
			return;
		ListItem<E> d = (ListItem<E>) src;
		disabled = d.disabled;
		selected = d.selected;
	}

	@Override
	protected void loggingContent(Logger logger, String prefix) {
		super.loggingContent(logger, prefix);
		logger.debug(prefix + "disabled=" + disabled);
		logger.debug(prefix + "selected=" + selected);
		logger.debug(prefix + "text=" + getText());
	}

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

}
