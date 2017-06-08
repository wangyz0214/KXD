package kxd.engine.ui.tags.website;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import kxd.remote.scs.beans.right.EditedFunction;
import kxd.util.TreeNode;

public class FuncTreeTag extends TreeTagSupport {
	private static final long serialVersionUID = -5249048637544628447L;
	private boolean displayLink;
	private boolean displayAll;
	private boolean displayImg = true;

	@Override
	public void release() {
		super.release();
	}

	public boolean isDisplayLink() {
		return displayLink;
	}

	public void setDisplayLink(boolean displayLink) {
		this.displayLink = displayLink;
	}

	@Override
	protected String getLoadUrl(TreeNode<?> node) {
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected TreeNode<?> getTree(Object treelist) {
		if (treelist instanceof FuncTreeNode)
			return (FuncTreeNode) treelist;
		else if (treelist instanceof Collection<?>) {
			FuncTreeNode root = new FuncTreeNode();
			root.add((Collection<EditedFunction>) treelist, displayAll);
			root.setExpanded(true);
			if (!displayAll) {
				Integer v = (Integer) getValue();
				if (v != null) {
					FuncTreeNode node = (FuncTreeNode) root.find(
							(Integer) getValue(), true);
					if (node != null) {
						node.setExpanded(true);
						while (node.getParent() != null) {
							node = (FuncTreeNode) node.getParent();
							node.setExpanded(true);
						}
					}
				}
			}
			return root;
		} else
			return null;
	}

	@Override
	protected String getNodeIcon(TreeNode<?> node) {
		if (displayImg) {
			FuncTreeNode ft = (FuncTreeNode) node;
			return ft.getIcon();
		} else
			return null;
	}

	@Override
	protected String getNodeUrl(TreeNode<?> node) {
		FuncTreeNode ft = (FuncTreeNode) node;
		String url = ft.getFunction().getFuncUrl();
		if (displayLink) {
			if (url == null)
				url = "javascript:treeec('" + getId() + "','" + getId() + "_ni"
						+ node.getId() + "','" + getChildDivId(node) + "');";
			else
				url = contextPath + url;
		} else {
			if (!node.isEmpty())
				// url = "javascript:treeec('" + getId() + "','timg"
				// + node.getId() + "','tc" + node.getId() + "');";
				url = "javascript:treeec('" + getId() + "','" + getId() + "_ni"
						+ node.getId() + "','" + getChildDivId(node) + "');";
			else
				url = "javascript:;";
		}
		return url;
	}

	@Override
	protected boolean shouldOutputChildren(JspWriter writer, TreeNode<?> node)
			throws JspException, IOException {
		return displayAll || !node.isEmpty();
	}

	public boolean isDisplayAll() {
		return displayAll;
	}

	public void setDisplayAll(boolean displayAll) {
		this.displayAll = displayAll;
	}

	public boolean isDisplayImg() {
		return displayImg;
	}

	public void setDisplayImg(boolean displayImg) {
		this.displayImg = displayImg;
	}

}
