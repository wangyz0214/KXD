package kxd.util;

/**
 * 实现树元素的能力
 * 
 * @author 赵明
 * @param <E>
 *            树ID
 * @param <V>
 *            树元素
 * @version 1.0
 */

public interface ListItemable<E> extends Checkable, Disable, Copyable,
		Selectable, Idable<E> {
	public String getText();

	public void setText(String text);

}
