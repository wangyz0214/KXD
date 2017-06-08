package kxd.engine.scs.admin.actions.invoice;

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

public class QueryInvoiceConfigAction extends QueryAction {

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

	@Override
	protected void setParameters(boolean isFormSubmit, HttpRequest request)
			throws Throwable {
		super.setParameters(isFormSubmit, request);
		org = request.getParameterDef("org", null);
	}

	@Override
	public int getEditRight() {
		return UserRight.INVOICE_CONFIG_EDIT;
	}

	@Override
	public int getQueryRight() {
		return UserRight.INVOICE_CONFIG;
	}

	public OrgTreeNode getTreenode() {
		return treenode;
	}

	public void setTreenode(OrgTreeNode treenode) {
		this.treenode = treenode;
	}

	public String getOrg() {
		return org;
	}

	public void setOrg(String org) {
		this.org = org;
	}

	@Override
	protected String doGetTableOptionHtml() {
		return super.doGetTableOptionHtml();
	}
}
