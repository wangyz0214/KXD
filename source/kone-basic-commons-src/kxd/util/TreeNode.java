/**
 * @(#)ListItem.java	1.0 2008/05/06
 */
package kxd.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import kxd.json.JSONException;
import kxd.json.JSONObject;

import org.apache.log4j.Logger;

/**
 * 列表数据元素类。本类可以存储包含ID和复选属性的数据，通常应用于选择列表或表格中。
 * 
 * @author 赵明
 * @param <E>
 *            列表元素的ID属性类型
 * @version 1.0
 */
abstract public class TreeNode<E> extends TreeItem<E> implements
		TreeNodeable<E> {
	private static final long serialVersionUID = -8219738537237143693L;
	private TreeNode<E> parent;
	private CopyOnWriteArrayList<TreeNode<E>> children = new CopyOnWriteArrayList<TreeNode<E>>();
	private int depth;
	private boolean childrenDisabled;

	public TreeNode() {
		super();
		depth = 0;
		childrenDisabled = true;
	}

	public TreeNode(E id) {
		super(id);
		depth = 0;
		childrenDisabled = true;
	}

	public <C extends TreeNodeData<E>> void addAll(List<C> ls) {
		Iterator<C> it = ls.iterator();
		if (it.hasNext()) {
			C e = it.next();
			int depth = e.getDepth();
			TreeNode<E> curNode = this;
			TreeNode<E> node = add(e.getId());
			e.copyDataToTreeNode(node);
			while (it.hasNext()) {
				e = it.next();
				if (e.getDepth() > depth) {
					if (curNode.getLastChild() == null) {
						throw new NullPointerException("org[" + e.getIdString()
								+ "] has not children");
					}
					curNode = curNode.getLastChild();
					depth = e.getDepth();
				} else if (depth > e.getDepth()) {
					for (int i = 0; i < depth - e.getDepth(); i++) {
						if (curNode.getParent() == null) {
							throw new NullPointerException("org["
									+ e.getIdString() + "] has not parent");
						}
						curNode = curNode.getParent();
					}
					depth = e.getDepth();
				}
				if (e.getId() == null)
					throw new NullPointerException();
				else if (curNode == null)
					throw new NullPointerException();
				node = curNode.add(e.getId());
				e.copyDataToTreeNode(node);
			}
		}
	}

	public int compare(TreeNode<E> o2) {
		throw new UnsupportedOperationException(
				"在TreeNode类中不支持该操作，请继承compre方法，实现该操作。");
	}

	public void sort(boolean includeChildren) {
		ArrayList<TreeNode<E>> ls = new ArrayList<TreeNode<E>>();
		ls.addAll(children);
		Collections.sort(ls, new Comparator<TreeNode<E>>() {
			@Override
			public int compare(TreeNode<E> o1, TreeNode<E> o2) {
				return o1.compare(o2);
			}
		});
		children.clear();
		children.addAll(ls);
		if (includeChildren) {
			for (TreeNode<E> o : children)
				o.sort(includeChildren);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void copyData(Object src) {
		super.copyData(src);
		if (!(src instanceof TreeNode<?>))
			return;
		TreeNode<E> d = (TreeNode<E>) src;
		childrenDisabled = d.childrenDisabled;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void copy(Object src) {
		super.copy(src);
		if (!(src instanceof TreeNode<?>))
			return;
		TreeNode<E> d = (TreeNode<E>) src;
		children.clear();
		Iterator<TreeNode<E>> it = d.children.iterator();
		while (it.hasNext()) {
			TreeNode<E> o = it.next();
			TreeNode<E> node = (TreeNode<E>) add(o.getId());
			node.copy(o);
		}
	}

	@Override
	protected void loggingContent(Logger logger, String prefix) {
		super.loggingContent(logger, prefix);
	}

	public TreeNode<E> add(E id) {
		if (id == null)
			throw new NullPointerException();
		TreeNode<E> o = (TreeNode<E>) createObject();
		children.add(o);
		o.parent = this;
		o.depth = depth + 1;
		o.setId(id);
		return o;
	}

	public void add(TreeNode<E> o) {
		children.add(o);
		o.parent = this;
		o.depth = depth + 1;
	}

	@Override
	public TreeNode<E> add(E id, int index) {
		if (id == null)
			throw new NullPointerException();
		TreeNode<E> o = (TreeNode<E>) createObject();
		children.add(index, o);
		o.parent = this;
		o.depth = depth + 1;
		o.setId(id);
		return o;
	}

	/**
	 * 判断当前结点是否是最后一个子结点
	 */
	public boolean isLastChild() {
		if (parent != null)
			return parent.getLastChild() == this;
		else
			return true;
	}

	/**
	 * 判断当前结点是否是第一个子结点
	 */
	public boolean isFirstChild() {
		if (parent != null)
			return parent.getFirstChild() == this;
		else
			return true;
	}

	public TreeNode<E> find(String keyword) {
		if (getText() != null && getText().contains(keyword))
			return this;
		return infind(keyword);
	}

	@SuppressWarnings("unchecked")
	private TreeNode<E> infind(String keyword) {
		Iterator<?> it = (Iterator<?>) getChildren().iterator();
		while (it.hasNext()) {
			TreeNode<E> node = (TreeNode<E>) it.next();
			if (node.getText().contains(keyword))
				return node;
			node = node.infind(keyword);
			if (node != null)
				return node;
		}
		return null;
	}

	@Override
	public TreeNode<E> find(E id, boolean includeChildren) {
		if (id.equals(getId()))
			return this;
		return infind(id, includeChildren);
	}

	@SuppressWarnings("unchecked")
	private TreeNode<E> infind(E id, boolean includeChildren) {
		Iterator<?> it = (Iterator<?>) getChildren().iterator();
		while (it.hasNext()) {
			TreeNode<E> node = (TreeNode<E>) it.next();
			if (id.equals(node.getId()))
				return node;
			if (includeChildren) {
				node = node.infind(id, includeChildren);
				if (node != null)
					return node;
			}
		}
		return null;
	}

	@Override
	public TreeNode<E> remove(E id) {
		TreeNode<E> o = find(id, false);
		if (o != null)
			remove(o);
		return o;
	}

	@Override
	public boolean remove(TreeNodeable<E> o) {
		return children.remove(o);
	}

	@Override
	public Collection<TreeNode<E>> getChildren() {
		return children;
	}

	@Override
	public TreeNode<E> getParent() {
		return parent;
	}

	@SuppressWarnings("unchecked")
	public void clear() {
		Iterator<?> it = children.iterator();
		while (it.hasNext()) {
			TreeNode<E> child = (TreeNode<E>) it.next();
			child.clear();
		}
		children.clear();
	}

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
	}

	@Override
	public void setDisabled(boolean disabled) {
		super.setDisabled(disabled);
		if (disabled == false) {
			childrenDisabled = false;
			TreeNode<E> p = getParent();
			while (p != null && p.childrenDisabled) {
				p.childrenDisabled = false;
				p = p.getParent();
			}
		} else
			childrenDisabled = true;
	}

	@SuppressWarnings("unchecked")
	public void cleanChildrenDisabledNodes() {
		Iterator<?> it = getChildren().iterator();
		while (it.hasNext()) {
			TreeNode<E> e = (TreeNode<E>) it.next();
			if (e.childrenDisabled)
				remove(e);
			else {
				e.cleanChildrenDisabledNodes();
			}
		}
	}

	@Override
	public boolean isEmpty() {
		return getChildren().isEmpty();
	}

	public boolean isChildrenDisabled() {
		return childrenDisabled;
	}

	public TreeNode<E> getLastChild() {
		if (isEmpty())
			return null;
		else
			return children.get(children.size() - 1);
	}

	public TreeNode<E> getFirstChild() {
		if (isEmpty())
			return null;
		else
			return children.get(0);
	}

	public boolean isParent(TreeNode<E> child) {
		TreeNode<E> parent = child;
		while (parent != null) {
			if (parent == this)
				return true;
			parent = parent.getParent();
		}
		return false;
	}

	private TreeNode<E> inGetFirstEnabledChild() {
		Iterator<TreeNode<E>> it = children.iterator();
		while (it.hasNext()) {
			TreeNode<E> o = it.next();
			if (!o.isDisabled())
				return o;
			o = o.inGetFirstEnabledChild();
			if (o != null)
				return o;
		}
		return null;
	}

	public TreeNode<E> getFirstEnabledChild() {
		if (!this.isDisabled() && getId() != null)
			return this;
		return inGetFirstEnabledChild();
	}

	public String getPath(String splitter, int toDepth) {
		TreeNode<E> p = this;
		StringBuffer sb = new StringBuffer(getText() == null ? "" : getText());
		while ((p = p.getParent()) != null && p.getDepth() >= toDepth)
			sb.insert(0, p.getText() + splitter);
		return sb.toString();
	}

	@Override
	public String toString() {
		return getText() + "[id=" + getId() + "]";
	}

	protected void convertToJson(JSONObject o, int mode) throws JSONException {
		o.put("nodeid", getId());
		o.put("disabled", isDisabled());
		o.put("text", getText());
		o.put("expanded", isExpanded());
		o.put("checked", isChecked());
	}

	public void toJson(JSONObject o, int mode, int depth) throws JSONException {
		convertToJson(o, mode);
		if (depth <= 0)
			return;
		Iterator<TreeNode<E>> it = getChildren().iterator();
		if (it.hasNext()) {
			ArrayList<JSONObject> ls = new ArrayList<JSONObject>();
			while (it.hasNext()) {
				JSONObject ob = new JSONObject();
				ls.add(ob);
				it.next().toJson(ob, mode, depth - 1);
			}
			o.put("children", ls);
		} else
			o.put("children", new ArrayList<JSONObject>());
	}

	public void getAll(List<Object> list) {
		list.add(this);
		for (TreeNode<?> o : children) {
			o.getAll(list);
		}
	}
}
