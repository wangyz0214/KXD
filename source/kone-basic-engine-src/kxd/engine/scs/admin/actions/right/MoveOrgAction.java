package kxd.engine.scs.admin.actions.right;

import java.util.Collection;

import javax.servlet.http.HttpServletResponse;

import kxd.engine.helper.AdminHelper;
import kxd.engine.scs.admin.AdminSessionObject;
import kxd.engine.scs.admin.util.UserRight;
import kxd.engine.ui.core.EditAction;
import kxd.engine.ui.tags.website.OrgTreeNode;
import kxd.net.HttpRequest;
import kxd.net.connection.jndi.Lookuper;
import kxd.net.naming.LoopNamingContext;
import kxd.remote.scs.beans.right.QueryedOrg;
import kxd.remote.scs.interfaces.OrgBeanRemote;

public class MoveOrgAction extends EditAction {
	private OrgTreeNode treenode;
	private Integer orgId, parentOrgId;

	@Override
	protected String doExecute(boolean isFormSubmit, HttpRequest request,
			HttpServletResponse response) throws Throwable {
		if (isFormSubmit) {
			move(request);
			return "success";
		} else
			return null;
	}

	private void move(HttpRequest request) throws Throwable {
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			orgId = request.getParameterInt("id");
			parentOrgId = request.getParameterInt("orgid_value");
			OrgBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-orgBean", OrgBeanRemote.class);
			bean.move(
					((AdminSessionObject) session).getLoginUser().getUserId(),
					orgId, parentOrgId);
		} catch (Throwable e) {
			orgId = request.getParameterInt("id");
			parentOrgId = request.getParameterInt("parentid");
			Collection<QueryedOrg> c = AdminHelper.getOrgTreeItems(null, 2,
					parentOrgId, false, null);
			treenode = new OrgTreeNode();
			treenode.add(c);
			throw e;
		} finally {
			context.close();
		}
	}

	@Override
	protected void setParameters(boolean isFormSubmit, HttpRequest request)
			throws Throwable {
		super.setParameters(isFormSubmit, request);
		if (!isFormSubmit) {
			orgId = request.getParameterInt("id");
			parentOrgId = request.getParameterInt("parentid");
			Collection<QueryedOrg> c = AdminHelper.getOrgTreeItems(null, 2,
					parentOrgId, false, null);
			treenode = new OrgTreeNode();
			treenode.add(c);
		}
	}

	@Override
	public int getEditRight() {
		return UserRight.ORG_EDIT;
	}

	@Override
	public int getQueryRight() {
		return UserRight.ORG;
	}

	public OrgTreeNode getTreenode() {
		return treenode;
	}

	public Integer getOrgId() {
		return orgId;
	}

	public Integer getParentOrgId() {
		return parentOrgId;
	}
}
