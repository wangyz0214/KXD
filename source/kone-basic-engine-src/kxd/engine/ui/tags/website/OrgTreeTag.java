package kxd.engine.ui.tags.website;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import kxd.remote.scs.beans.right.QueryedOrg;
import kxd.util.TreeNode;

public class OrgTreeTag extends TreeTagSupport {
	private static final long serialVersionUID = -5249048637544628447L;

	@Override
	public void release() {
		super.release();
	}

	@Override
	protected String getLoadUrl(TreeNode<?> node) {
		OrgTreeNode to = (OrgTreeNode) node;
		if (to.isNeedLoadChildren() && to.isEmpty())
			return contextPath
					+ "/xmlTrade?executor=admintrade&cmd=getorgchildren";
		else
			return null;
	}

	@Override
	protected void outputTreeNodeTextAttributes(JspWriter writer,
			TreeNode<?> node) throws JspException, IOException {
		super.outputTreeNodeTextAttributes(writer, node);
		writeAttribute(writer, "fullpath",
				((OrgTreeNode) node).getOrgFullPath());
	}

	@Override
	protected void outputTreeNodeText(JspWriter writer, TreeNode<?> node)
			throws JspException, IOException {
		super.outputTreeNodeText(writer, node);
		if (isEditEnabled()) {
			OrgTreeNode tnode = (OrgTreeNode) node;
			if (!node.isDisabled()) {
				writeText(writer,
						" &nbsp; <a class='gray' href='#' onclick=\"showAddDialog(this,'"
								+ getId() + "','" + node.getIdString() + "','"
								+ node.getText() + "');return false;\">添加</a>");
				writeText(writer,
						" <a class='gray' href='#' onclick=\"showEditDialog(this,'"
								+ getId() + "','" + node.getIdString() + "','"
								+ node.getText() + "');return false;\">编辑</a>");
				if (!tnode.isNeedLoadChildren()) {
					writeText(writer,
							" <a class='gray' href='#' onclick=\"deleteSelected(this,'"
									+ getId() + "','" + node.getIdString()
									+ "','" + node.getText()
									+ "');return false;\">删除</a>");
					writeText(writer,
							" <a class='gray' href='#' onclick=\"showMoveDialog(this,'"
									+ getId() + "','" + node.getIdString()
									+ "','" + node.getText()
									+ "');return false;\">迁移</a>");
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected TreeNode<?> getTree(Object treelist) {
		OrgTreeNode node = null;
		if (treelist instanceof OrgTreeNode)
			node = (OrgTreeNode) treelist;
		else if (treelist instanceof Collection<?>) {
			node = new OrgTreeNode();
			node.add((Collection<QueryedOrg>) treelist);
		}
		node.setExpanded(true);
		return node;
	}

	@Override
	protected String getNodeIcon(TreeNode<?> node) {
		if (node.isDisabled())
			return "/images/funcicons/orgdisabled.gif";
		else
			return "/images/funcicons/org.gif";
	}

	@Override
	protected boolean shouldOutputChildren(JspWriter writer, TreeNode<?> node)
			throws JspException, IOException {
		return true;
	}

}
