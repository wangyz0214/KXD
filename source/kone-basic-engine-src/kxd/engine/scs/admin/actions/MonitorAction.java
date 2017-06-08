package kxd.engine.scs.admin.actions;

import java.util.Collection;

import javax.servlet.http.HttpServletResponse;

import kxd.engine.helper.AdminHelper;
import kxd.engine.scs.admin.AdminSessionObject;
import kxd.engine.scs.admin.util.UserRight;
import kxd.engine.ui.core.QueryAction;
import kxd.engine.ui.tags.website.OrgTreeNode;
import kxd.net.HttpRequest;
import kxd.remote.scs.beans.right.LoginUser;
import kxd.remote.scs.beans.right.QueryedOrg;

public class MonitorAction extends QueryAction {
	private String keyword;
	private OrgTreeNode treenode;

	@Override
	protected String doExecute(boolean isFormSubmit, HttpRequest request,
			HttpServletResponse response) throws Throwable {
		LoginUser user = ((AdminSessionObject) session).getLoginUser();
		Integer orgId = null;
		if (user.getOrg().getId() != null)
			orgId = user.getOrg().getId();
		if (keyword != null && keyword.trim().length() == 0)
			keyword = null;
		Collection<QueryedOrg> c = AdminHelper.getOrgTreeItems(orgId, 2, null,
				true, keyword);
		treenode = new OrgTreeNode();
		treenode.add(c);
		return null;
	}

	@Override
	protected void setParameters(boolean isFormSubmit, HttpRequest request)
			throws Throwable {
		super.setParameters(isFormSubmit, request);
		keyword = request.getParameterDef("keyword", null);
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public OrgTreeNode getTreenode() {
		return treenode;
	}

	public void setTreenode(OrgTreeNode treenode) {
		this.treenode = treenode;
	}

	@Override
	public int getEditRight() {
		return UserRight.TERMMONITOR;
	}

	@Override
	public int getQueryRight() {
		return UserRight.TERMMONITOR;
	}
}
