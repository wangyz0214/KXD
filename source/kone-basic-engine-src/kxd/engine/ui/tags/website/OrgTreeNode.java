package kxd.engine.ui.tags.website;

import java.util.Collection;
import java.util.Iterator;

import kxd.json.JSONException;
import kxd.json.JSONObject;
import kxd.remote.scs.beans.right.QueryedOrg;
import kxd.util.TreeNode;

public class OrgTreeNode extends TreeNode<Integer> {
	private static final long serialVersionUID = -8441719401422342700L;
	private String text;
	private boolean needLoadChildren;
	private String orgFullPath;

	public OrgTreeNode() {
		super();
	}

	@Override
	public void copyData(Object src) {
		super.copyData(src);
		if (src instanceof OrgTreeNode) {
			OrgTreeNode node = (OrgTreeNode) src;
			text = node.text;
		}
	}

	@Override
	public OrgTreeNode createObject() {
		return new OrgTreeNode();
	}

	@Override
	public String getIdString() {
		return getId().toString();
	}

	@Override
	public void setIdString(String id) {
		setId(Integer.valueOf(id));
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	protected String toDisplayLabel() {
		return text;
	}

	@Override
	public String getText() {
		return text;
	}

	public boolean isNeedLoadChildren() {
		return needLoadChildren;
	}

	public void setNeedLoadChildren(boolean needLoadChildren) {
		this.needLoadChildren = needLoadChildren;
	}

	/**
	 * 添加一个功能集合到功能树中
	 * 
	 * @param orgCollection
	 */
	public String add(Collection<QueryedOrg> collection) {
		Iterator<QueryedOrg> it = collection.iterator();
		StringBuffer checked = new StringBuffer();
		if (it.hasNext()) {
			QueryedOrg e = it.next();
			int depth = e.getDepth();
			TreeNode<Integer> curNode = this;
			OrgTreeNode node = (OrgTreeNode) curNode.add(e.getId());
			node.setText(e.getOrgName());
			node.setChecked(e.isChecked());
			node.setSelected(e.isSelected());
			node.setDisabled(e.isDisabled());
			node.setOrgFullPath(e.getOrgFullPath());
			node.setExpanded(true);
			node.setNeedLoadChildren(e.isHasChildren());
			if (e.isChecked())
				checked.append(e.getId() + ",");
			while (it.hasNext()) {
				e = it.next();
				if (e.getDepth() > depth) {
					curNode = curNode.getLastChild();
					depth = e.getDepth();
				} else if (depth > e.getDepth()) {
					for (int i = 0; i < depth - e.getDepth(); i++)
						curNode = curNode.getParent();
					depth = e.getDepth();
				}
				node = (OrgTreeNode) curNode.add(e.getId());
				node.setText(e.getOrgName());
				node.setChecked(e.isChecked());
				node.setDisabled(e.isDisabled());
				node.setSelected(e.isSelected());
				node.setOrgFullPath(e.getOrgFullPath());
				node.setExpanded(true);
				((OrgTreeNode) node).setNeedLoadChildren(e.isHasChildren());

				if (e.isChecked())
					checked.append(e.getId() + ",");
			}
		}
		return checked.toString();
	}

	public String getOrgFullPath() {
		return orgFullPath;
	}

	public void setOrgFullPath(String orgFullPath) {
		this.orgFullPath = orgFullPath;
	}

	@Override
	protected void convertToJson(JSONObject o, int mode) throws JSONException {
		super.convertToJson(o, mode);
		o.put("icon", "org.gif");
		if (isNeedLoadChildren() && getChildren().size() == 0)
			o.put("loadurl", "id=" + getId());
	}
}
