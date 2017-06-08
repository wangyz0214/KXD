package kxd.engine.scs.admin.actions.term;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import kxd.engine.cache.beans.sts.CachedTerm;
import kxd.engine.helper.CacheHelper;
import kxd.engine.helper.AdminHelper;
import kxd.engine.scs.admin.AdminSessionObject;
import kxd.engine.scs.admin.util.UserRight;
import kxd.engine.ui.core.EditAction;
import kxd.engine.ui.tags.website.OrgTreeNode;
import kxd.net.HttpRequest;
import kxd.net.connection.jndi.Lookuper;
import kxd.net.naming.LoopNamingContext;
import kxd.remote.scs.beans.BaseTermMoveItem;
import kxd.remote.scs.beans.right.QueryedOrg;
import kxd.remote.scs.interfaces.TermBeanRemote;
import kxd.util.DateTime;

public class EditTermParentAction extends EditAction {
	private OrgTreeNode treenode;
	CachedTerm term;
	List<String> moveList;

	@Override
	protected String doExecute(boolean isFormSubmit, HttpRequest request,
			HttpServletResponse response) throws Throwable {
		if (isFormSubmit) {
			editParent(request);
			return "success";
		} else
			return null;
	}

	@Override
	protected void setParameters(boolean isFormSubmit, HttpRequest request)
			throws Throwable {
		super.setParameters(isFormSubmit, request);
		term = CacheHelper.termMap.getTerm(request.getParameterInt("id"));
		if (!isFormSubmit) {
			Collection<QueryedOrg> c = AdminHelper.getOrgTreeItems(null, 2,
					term.getOrgId(), false, null);
			treenode = new OrgTreeNode();
			treenode.add(c);
			LoopNamingContext context = new LoopNamingContext("db");
			try {
				TermBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
						"kxd-ejb-termBean", TermBeanRemote.class);
				List<BaseTermMoveItem> list = bean.getTermMoveList(
						((AdminSessionObject) session).getLoginUser()
								.getUserId(), term.getId());
				moveList = new ArrayList<String>();
				if (list.size() > 0) {
					BaseTermMoveItem item = list.get(0);
					String orgdesp = item.getOrg().getOrgFullName();
					for (int i = 1; i < list.size(); i++) {
						BaseTermMoveItem item1 = list.get(i);
						String s = new DateTime(item.getMoveTime())
								.format("yyyy-MM-dd: ");
						s += orgdesp + "  ->  ";
						orgdesp = item1.getOrg().getOrgFullName();
						s += orgdesp;
						item = item1;
						moveList.add(s);
					}
					String s = new DateTime(item.getMoveTime())
							.format("yyyy-MM-dd: ");
					s += orgdesp + "  ->  ";
					s += term.getOrg().getOrgFullName();
					moveList.add(s);
				} else
					moveList.add("该终端没有迁移记录");

			} finally {
				if (context != null)
					context.close();
			}
		}
	}

	private void editParent(HttpRequest request) throws Throwable {
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			Integer orgId = request.getParameterInt("orgid_value");
			TermBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-termBean", TermBeanRemote.class);
			bean.move(
					((AdminSessionObject) session).getLoginUser().getUserId(),
					term.getId(), orgId);
		} finally {
			context.close();
		}
	}

	@Override
	public int getEditRight() {
		return UserRight.TERM_EDIT;
	}

	@Override
	public int getQueryRight() {
		return UserRight.TERM;
	}

	public OrgTreeNode getTreenode() {
		return treenode;
	}

	public CachedTerm getTerm() {
		return term;
	}

	public List<String> getMoveList() {
		return moveList;
	}
}
