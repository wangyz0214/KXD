/**
 * @(#)ListItem.java	1.0 2008/05/06
 */
package kxd.util;

import org.apache.log4j.Logger;

/**
 * 列表数据元素类。本类可以存储包含ID和复选属性的数据，通常应用于选择列表或表格中。
 * 
 * @author 赵明
 * @param <E>
 *            列表元素的ID属性类型
 * @version 1.0
 */
abstract public class TreeItem<E> extends ListItem<E> implements
		TreeItemable<E> {
	private static final long serialVersionUID = -8219738537237143693L;
	private boolean expanded;

	public TreeItem() {
		super();
	}

	public TreeItem(E id) {
		super(id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void copyData(Object src) {
		super.copyData(src);
		if (!(src instanceof TreeItem<?>))
			return;
		TreeItem<E> d = (TreeItem<E>) src;
		expanded = d.expanded;
	}

	@Override
	protected void loggingContent(Logger logger, String prefix) {
		super.loggingContent(logger, prefix);
		logger.debug( prefix + "expanded=" + expanded);
	}

	public boolean isExpanded() {
		return expanded;
	}

	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}

}
