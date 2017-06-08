package kxd.engine.ui.tags.website;

import java.util.Collection;
import java.util.Iterator;

import kxd.json.JSONException;
import kxd.json.JSONObject;
import kxd.remote.scs.beans.right.EditedFunction;
import kxd.util.TreeNode;

public class FuncTreeNode extends TreeNode<Integer> {
	private static final long serialVersionUID = -8079123921440052565L;
	EditedFunction function;

	public FuncTreeNode() {
		super();
	}

	@Override
	public void copyData(Object src) {
		super.copyData(src);
		if (src instanceof FuncTreeNode) {
			FuncTreeNode node = (FuncTreeNode) src;
			function = node.function;
		}
	}

	@Override
	public FuncTreeNode createObject() {
		return new FuncTreeNode();
	}

	public EditedFunction getFunction() {
		return function;
	}

	public void setFunction(EditedFunction f) {
		function = f;
	}

	@Override
	public String getIdString() {
		return getId().toString();
	}

	@Override
	public void setIdString(String id) {
		setId(Integer.valueOf(id));
	}

	@Override
	protected String toDisplayLabel() {
		return getFunction().getDisplayLabel();
	}

	@Override
	public String getText() {
		if (getFunction() == null)
			return "";
		return getFunction().getDisplayLabel();
	}

	public void disableAll() {
		for (TreeNode<Integer> o : getChildren()) {
			o.setDisabled(true);
			((FuncTreeNode) o).disableAll();
		}
	}

	/**
	 * 添加一个功能集合到功能树中
	 * 
	 * @param orgCollection
	 */
	public String add(Collection<EditedFunction> collection, boolean expanded) {
		Iterator<EditedFunction> it = collection.iterator();
		StringBuffer checked = new StringBuffer();
		if (it.hasNext()) {
			EditedFunction e = it.next();
			int depth = e.getFuncDepth();
			TreeNode<Integer> curNode = this;
			TreeNode<Integer> node = (TreeNode<Integer>) curNode.add(e.getId());
			node.setExpanded(true);
			node.setDisabled(false);
			node.setSelected(e.isSelected());
			((FuncTreeNode) node).function = e;
			node.setChecked(e.isChecked());
			if (e.isChecked())
				checked.append(e.getId() + ",");
			while (it.hasNext()) {
				e = it.next();
				if (e.getFuncDepth() > depth) {
					curNode = curNode.getLastChild();
					depth = e.getFuncDepth();
				} else if (depth > e.getFuncDepth()) {
					for (int i = 0; i < depth - e.getFuncDepth(); i++)
						curNode = curNode.getParent();
					depth = e.getFuncDepth();
				}
				node = curNode.add(e.getId());
				node.setExpanded(expanded);
				node.setSelected(e.isSelected());
				((FuncTreeNode) node).function = e;
				node.setChecked(e.isChecked());
				if (e.isChecked())
					checked.append(e.getId() + ",");
			}
		}
		return checked.toString();
	}

	/**
	 * 添加一个功能集合到功能树中，通用功能除外
	 * 
	 * @param orgCollection
	 */
	public void addRighted(Collection<EditedFunction> collection) {
		Iterator<EditedFunction> it = collection.iterator();
		if (it.hasNext()) {
			EditedFunction e = it.next();
			int depth = e.getFuncDepth();
			TreeNode<Integer> curNode = this;
			TreeNode<Integer> node;
			if (!e.getUserGroup().isCustomer() && e.getFuncDepth() < 2) {
				node = (TreeNode<Integer>) curNode.add(e.getId());
				node.setExpanded(true);
				node.setSelected(e.isSelected());
				((FuncTreeNode) node).function = e;
				node.setChecked(e.isChecked());
			}
			while (it.hasNext()) {
				e = it.next();
				if (!e.getUserGroup().isCustomer() && e.getFuncDepth() < 2) {
					if (e.getFuncDepth() > depth) {
						curNode = curNode.getLastChild();
						depth = e.getFuncDepth();
					} else if (depth > e.getFuncDepth()) {
						for (int i = 0; i < depth - e.getFuncDepth(); i++)
							curNode = curNode.getParent();
						depth = e.getFuncDepth();
					}
					node = curNode.add(e.getId());
					node.setExpanded(true);
					node.setSelected(e.isSelected());
					node.setDisabled(false);
					((FuncTreeNode) node).function = e;
					node.setChecked(e.isChecked());
				}
			}
		}
	}

	public void addEnabledChildrenDataTo(Collection<EditedFunction> list) {
		Iterator<TreeNode<Integer>> it = getChildren().iterator();
		while (it.hasNext()) {
			FuncTreeNode node = (FuncTreeNode) it.next();
			if (!node.isChildrenDisabled()) {
				list.add(node.function);
				node.addEnabledChildrenDataTo(list);
			}
		}
	}

	@Override
	protected void convertToJson(JSONObject o, int mode) throws JSONException {
		super.convertToJson(o, mode);
		String p;
		if (function == null || function.getFuncIcon() == null) {
			p = "common.gif";
		} else {
			p = function.getFuncIcon();
		}
		if (function != null && function.getFuncUrl() != null)
			o.put("href", function.getFuncUrl());
		o.put("icon", p);
	}

	public void addDataTo(Collection<EditedFunction> list) {
		Iterator<TreeNode<Integer>> it = getChildren().iterator();
		while (it.hasNext()) {
			FuncTreeNode node = (FuncTreeNode) it.next();
			list.add(node.function);
			node.addDataTo(list);
		}
	}

	@Override
	public void setText(String text) {
		if (getFunction() == null)
			return;
		getFunction().setFuncDesp(text);
	}

	public boolean isCustomEnabled() {
		return getFunction().isCustomEnabled();
	}

	static String imagePath = "/images/funcicons/";

	public String getIcon() {
		if (function == null || function.getFuncIcon() == null)
			return imagePath + "common.gif";
		else
			return imagePath + function.getFuncIcon();
	}
}
