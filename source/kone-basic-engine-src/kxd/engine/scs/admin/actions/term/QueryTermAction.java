package kxd.engine.scs.admin.actions.term;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import kxd.engine.cache.beans.sts.CachedManuf;
import kxd.engine.helper.CacheHelper;
import kxd.engine.helper.AdminHelper;
import kxd.engine.scs.admin.AdminSessionObject;
import kxd.engine.scs.admin.util.UserRight;
import kxd.engine.ui.core.QueryAction;
import kxd.engine.ui.tags.website.OrgTreeNode;
import kxd.net.HttpRequest;
import kxd.remote.scs.beans.right.LoginUser;
import kxd.remote.scs.beans.right.QueryedOrg;

public class QueryTermAction extends QueryAction {
	Collection<CachedManuf> manufList;
	private OrgTreeNode treenode;
	private String org;

	@Override
	protected String doExecute(boolean isFormSubmit, HttpRequest request,
			HttpServletResponse response) throws Throwable {
		LoginUser user = ((AdminSessionObject) session).getLoginUser();
		if (user.isLogined()) {
			if (user.getUserGroup().isSystemManager()) {
				manufList = CacheHelper.manufMap.values();
				CachedManuf nm = new CachedManuf();
				nm.setManufName("全部厂商");
				((List<CachedManuf>) manufList).add(0, nm);
			} else if (user.getManuf().getId() != null) {
				manufList = new ArrayList<CachedManuf>();
				CachedManuf manuf = new CachedManuf();
				manuf.setId(user.getManuf().getId());
				manuf.setManufName(user.getManuf().getManufName());
				manufList.add(manuf);
			}

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
		return UserRight.TERM_EDIT;
	}

	@Override
	public int getQueryRight() {
		return UserRight.TERM;
	}

	public Collection<CachedManuf> getManufList() {
		return manufList;
	}

	public void setManufList(Collection<CachedManuf> manufList) {
		this.manufList = manufList;
	}

	public OrgTreeNode getTreenode() {
		return treenode;
	}

	public void setTreenode(OrgTreeNode treenode) {
		this.treenode = treenode;
	}

	@Override
	protected String doGetTableOptionHtml() {
		return super.doGetTableOptionHtml()
				+ " <a href='#' onclick='showEditParentOrgDialog(arguments[0]);return false;'>迁移</a> <a href='#' onclick='showEditExtDialog(arguments[0]);return false;'>扩展信息</a>";
	}

}
