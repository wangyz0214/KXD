/**
 * @(#)ListItem.java	1.0 2008/05/06
 */
package kxd.util;

/**
 * 列表数据元素类。本类可以存储包含ID和复选属性的数据，通常应用于选择列表或表格中。
 * 
 * @author 赵明
 * @param <E>
 *            列表元素的ID属性类型
 * @version 1.0
 */
abstract public class ListElement<E> implements IdCheckable<E> {
	private E id;
	private boolean checked;
	private boolean disabled = true;
	String text;

	public void assignDataTo(ListElement<E> other) {
		other.id = id;
		other.checked = checked;
		other.disabled = disabled;
		other.text = text;
	}

	/**
	 * 返回元素的ID
	 */
	public E getId() {
		return id;
	}

	/**
	 * 设置元素的ID
	 */
	public void setId(E id) {
		this.id = id;
	}

	/**
	 * 返回元素当前是否处于选中状态
	 */
	public boolean isChecked() {
		return checked;
	}

	/**
	 * 设置元素当前是否处于选中状态
	 */
	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	/**
	 * 比较两个元素是否相等。先比较实列化类是否相同，再比较ID是否相同
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof ListElement<?>))
			return false;
		final ListElement<?> other = (ListElement<?>) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public String toString() {
		return text;
	}

	public String getDisplayLabel() {
		return text;
	}

	public void setDisplayLabel(String newValue) {
		this.text = newValue;
	}
}
