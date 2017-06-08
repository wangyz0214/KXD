package kxd.engine.scs.admin.actions.adinfo;

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

public class QueryNeedAuditAdInfoAction extends QueryAction {
	private OrgTreeNode treenode;
	private String org;

	@Override
	protected String doExecute(boolean isFormSubmit, HttpRequest request,
			HttpServletResponse response) throws Throwable {
		LoginUser user = ((AdminSessionObject) session).getLoginUser();
		if (user.isLogined()) {
			Integer porgId = null;
			if (user.getOrg().getId() != null)
				porgId = user.getOrg().getId();
			Collection<QueryedOrg> c = AdminHelper.getOrgTreeItems(porgId, 2,
					null, true, org);
			treenode = new OrgTreeNode();
			treenode.add(c);
		}
		return null;
	}

	public String getOrg() {
		return org;
	}

	public void setOrg(String org) {
		this.org = org;
	}

	@Override
	protected void setParameters(boolean isFormSubmit, HttpRequest request)
			throws Throwable {
		super.setParameters(isFormSubmit, request);
		org = request.getParameterDef("org", null);
	}

	@Override
	public int getEditRight() {
		return -1;
	}

	@Override
	public int getQueryRight() {
		return UserRight.AUDIT;
	}

	public OrgTreeNode getTreenode() {
		return treenode;
	}

	public void setTreenode(OrgTreeNode treenode) {
		this.treenode = treenode;
	}

	@Override
	protected String doGetTableOptionHtml() {
		return "<a href='#' onclick='audit_ok(arguments[0]);return false;'>生效</a> "
				+ "<a href='#' onclick='audit_false(arguments[0]);return false;'>不生效</a>";
	}

}
