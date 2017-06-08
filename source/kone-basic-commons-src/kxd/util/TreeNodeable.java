package kxd.util;

import java.util.Collection;

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

public interface TreeNodeable<E> extends TreeItemable<E> {

	public Collection<?> getChildren();

	public TreeNodeable<E> getParent();

	public TreeNodeable<E> add(E id, int index);

	public TreeNodeable<E> find(E id, boolean includeChildren);

	public TreeNodeable<E> remove(E id);

	public TreeNodeable<E> getLastChild();

	public TreeNodeable<E> getFirstChild();

	public boolean isEmpty();

	public void clear();

	public boolean remove(TreeNodeable<E> o);
}
