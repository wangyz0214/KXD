package kxd.engine.scs.admin.actions.right;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import kxd.engine.scs.admin.AdminSessionObject;
import kxd.engine.scs.admin.util.UserRight;
import kxd.engine.ui.core.EditAction;
import kxd.net.HttpRequest;
import kxd.net.connection.jndi.Lookuper;
import kxd.net.naming.LoopNamingContext;
import kxd.remote.scs.beans.BaseRole;
import kxd.remote.scs.beans.BaseUser;
import kxd.remote.scs.interfaces.UserBeanRemote;
import kxd.util.StringUnit;

public class EditUserRolesAction extends EditAction {
	private BaseUser user;
	private String oldRoles, roles;
	private List<BaseRole> roleList;

	@Override
	protected String doExecute(boolean isFormSubmit, HttpRequest request,
			HttpServletResponse response) throws Throwable {
		if (isFormSubmit) {
			editRoles();
			return "success";
		} else
			return null;
	}

	@Override
	protected void setParameters(boolean isFormSubmit, HttpRequest request)
			throws Throwable {
		super.setParameters(isFormSubmit, request);
		if (isFormSubmit) {
			oldRoles = request.getParameter("roles_oldvalue");
			roles = request.getParameter("roles_value");
			getUser().setId(request.getParameterLong("id"));
			// getUser().setUserName(request.getParameter("desp"));
		} else {
			getRoleRoles(request);
		}
	}

	private void getRoleRoles(HttpRequest request) throws Throwable {
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			UserBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-userBean", UserBeanRemote.class);
			Long userid = request.getParameterLong("id");
			getUser().setUserId(userid);
			// getUser().setUserName(request.getParameter("desp"));
			roleList = (List<BaseRole>) bean.getUserManagedRoles(
					((AdminSessionObject) session).getLoginUser().getUserId(),
					userid, "");
		} finally {
			if (context != null)
				context.close();
		}
	}

	private void editRoles() throws Throwable {
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			if (oldRoles.endsWith(","))
				oldRoles = oldRoles.substring(0, oldRoles.length() - 1);
			if (roles.endsWith(","))
				roles = roles.substring(0, roles.length() - 1);
			String[] os = StringUnit.split(oldRoles, ",");
			String[] ns = StringUnit.split(roles, ",");
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
				UserBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
						"kxd-ejb-userBean", UserBeanRemote.class);
				bean.setUserManagedRoles(((AdminSessionObject) session)
						.getLoginUser().getUserId(), user.getUserId(), nlist,
						olist);
			}
		} finally {
			context.close();
		}
	}

	public String getOldRoles() {
		return oldRoles;
	}

	public void setOldRoles(String oldRoles) {
		this.oldRoles = oldRoles;
	}

	public String getRoles() {
		return roles;
	}

	public void setRoles(String roles) {
		this.roles = roles;
	}

	@Override
	public int getEditRight() {
		return UserRight.USER_EDIT;
	}

	@Override
	public int getQueryRight() {
		return UserRight.USER;
	}

	public BaseUser getUser() {
		if (user == null) {
			user = new BaseUser();
		}
		return user;
	}

	public void setUser(BaseUser user) {
		this.user = user;
	}

	public List<BaseRole> getRoleList() {
		return roleList;
	}
}
