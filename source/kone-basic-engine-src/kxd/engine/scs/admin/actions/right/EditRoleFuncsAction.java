package kxd.engine.scs.admin.actions.right;

import java.util.ArrayList;
import java.util.Iterator;

import javax.servlet.http.HttpServletResponse;

import kxd.engine.scs.admin.AdminSessionObject;
import kxd.engine.scs.admin.util.UserRight;
import kxd.engine.ui.core.EditAction;
import kxd.engine.ui.tags.website.FuncTreeNode;
import kxd.net.HttpRequest;
import kxd.net.connection.jndi.Lookuper;
import kxd.net.naming.LoopNamingContext;
import kxd.remote.scs.beans.BaseRole;
import kxd.remote.scs.interfaces.RoleBeanRemote;
import kxd.util.StringUnit;
import kxd.util.TreeNode;

public class EditRoleFuncsAction extends EditAction {
	private BaseRole role;
	private String oldFuncs, funcs;
	private FuncTreeNode funcTree;

	@Override
	protected String doExecute(boolean isFormSubmit, HttpRequest request,
			HttpServletResponse response) throws Throwable {
		if (isFormSubmit) {
			editFuncs();
			return "success";
		} else
			return null;
	}

	@Override
	protected void setParameters(boolean isFormSubmit, HttpRequest request)
			throws Throwable {
		super.setParameters(isFormSubmit, request);
		if (isFormSubmit) {
			oldFuncs = request.getParameter("funcs_oldvalue");
			funcs = request.getParameter("funcs_value");
			role = getRole();
			role.setId(request.getParameterIntDef("id", null));
			role.setRoleName(request.getParameter("desp"));
		} else {
			getRoleFuncs(request.getParameterInt("id"),
					request.getParameter("desp"));
		}
	}

	private void getRoleFuncs(Integer id, String desp) throws Throwable {
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			RoleBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-roleBean", RoleBeanRemote.class);
			getRole().setId(id);
			getRole().setRoleName(desp);
			Iterator<Integer> it = bean.getFunction(getRole().getRoleId())
					.iterator();
			funcTree = ((AdminSessionObject) session).getCurCustomFuncTree();
			// StringBuffer sb = new StringBuffer();
			while (it.hasNext()) {
				TreeNode<Integer> n = funcTree.find(it.next(), true);
				if (n != null) {
					n.setChecked(true);
					// if (sb.length() > 0)
					// sb.append(",");
					// sb.append(n.getId());
				}
			}
			// oldFuncs = "[" + sb.toString() + "]";
		} finally {
			context.close();
		}
	}

	private void editFuncs() throws Throwable {
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			if (oldFuncs.endsWith(","))
				oldFuncs = oldFuncs.substring(0, oldFuncs.length() - 1);
			if (funcs.endsWith(","))
				funcs = funcs.substring(0, funcs.length() - 1);
			String[] os = StringUnit.split(oldFuncs, ",");
			String[] ns = StringUnit.split(funcs, ",");
			ArrayList<Integer> olist = new ArrayList<Integer>();
			ArrayList<Integer> nlist = new ArrayList<Integer>();
			for (int i = 0; i < os.length; i++)
				olist.add(Integer.valueOf(os[i]));
			for (int i = 0; i < ns.length; i++)
				nlist.add(Integer.valueOf(ns[i]));
			for (int i = 0; i < olist.size(); i++) {
				Integer l = olist.get(i);
				if (nlist.contains(l)) {
					olist.remove(i);
					nlist.remove(l);
					i--;
				}
			}
			if (nlist.size() > 0 || olist.size() > 0) {
				RoleBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
						"kxd-ejb-roleBean", RoleBeanRemote.class);
				bean.setFunction(((AdminSessionObject) session).getLoginUser()
						.getUserId(), role.getRoleId(), nlist, olist);
			}
		} finally {
			context.close();
		}
	}

	public BaseRole getRole() {
		if (role == null) {
			role = new BaseRole();
		}
		return role;
	}

	public void setRole(BaseRole role) {
		this.role = role;
	}

	public String getOldFuncs() {
		return oldFuncs;
	}

	public void setOldFuncs(String oldFuncs) {
		this.oldFuncs = oldFuncs;
	}

	public String getFuncs() {
		return funcs;
	}

	public void setFuncs(String funcs) {
		this.funcs = funcs;
	}

	public FuncTreeNode getFuncTree() {
		return funcTree;
	}

	public void setFuncTree(FuncTreeNode funcTree) {
		this.funcTree = funcTree;
	}

	@Override
	public int getEditRight() {
		return UserRight.ROLE_EDIT;
	}

	@Override
	public int getQueryRight() {
		return UserRight.ROLE;
	}
}
