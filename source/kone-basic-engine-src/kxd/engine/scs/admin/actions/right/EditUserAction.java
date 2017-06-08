package kxd.engine.scs.admin.actions.right;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.naming.NamingException;
import javax.servlet.http.HttpServletResponse;

import kxd.engine.cache.beans.sts.CachedManuf;
import kxd.engine.helper.CacheHelper;
import kxd.engine.scs.admin.AdminSessionObject;
import kxd.engine.scs.admin.util.UserRight;
import kxd.engine.ui.core.EditAction;
import kxd.net.HttpRequest;
import kxd.net.connection.jndi.Lookuper;
import kxd.net.naming.LoopNamingContext;
import kxd.remote.scs.beans.BaseManuf;
import kxd.remote.scs.beans.BaseOrg;
import kxd.remote.scs.beans.BaseRole;
import kxd.remote.scs.beans.right.EditedUser;
import kxd.remote.scs.beans.right.LoginUser;
import kxd.remote.scs.interfaces.UserBeanRemote;
import kxd.remote.scs.util.emun.ManageScope;
import kxd.remote.scs.util.emun.UserGroup;
import kxd.util.DataSecurity;

public class EditUserAction extends EditAction {
	private EditedUser user;
	boolean added;
	String userPwd;

	@Override
	protected String doExecute(boolean isFormSubmit, HttpRequest request,
			HttpServletResponse response) throws Throwable {
		if (isFormSubmit) {
			addOrEditUser(request);
			return "success";
		} else
			return null;
	}

	@Override
	protected void setParameters(boolean isFormSubmit, HttpRequest request)
			throws Throwable {
		super.setParameters(isFormSubmit, request);
		if (isFormSubmit) {
			user = getUser();
			user.setId(request.getParameterLongDef("id", null));
			user.setUserCode(request.getParameter("code"));
			user.setUserName(request.getParameter("desp"));
			user.setEmail(request.getParameterDef("email", null));
			user.setManageScope(ManageScope.AREA);
			Integer tid = request.getParameterIntDef("manufid", null);
			if (tid != null)
				user.setManuf(new BaseManuf(tid, request
						.getParameter("manufid_desp")));
			else
				user.setManuf(new BaseManuf());
			user.setMobile(request.getParameterDef("mobile", null));
			user.setTelphone(request.getParameterDef("telphone", null));
			user.setUserGroup(UserGroup.valueOf(request
					.getParameterInt("usergroup")));
			tid = request.getParameterIntDef("roleid", null);
			if (tid != null)
				user.setRole(new BaseRole(tid, request
						.getParameter("roleid_desp")));
			else
				user.setRole(new BaseRole());
			userPwd = request.getParameterDef("userpwd", null);
			if (userPwd != null && userPwd.trim().length() > 0)
				userPwd = DataSecurity.md5(userPwd.getBytes());
			else
				userPwd = null;
			if (user.getUserId() == null) {
				Integer orgid = request.getParameterInt("orgid");
				user.setOrg(new BaseOrg(orgid, request
						.getParameter("orgid_desp")));
				// getUser().getOrg().setOrgFullName(
				// request.getParameter("fullpath"));
			}
		} else {
			getUser(request.getParameterIntDef("id", null));
			if (user == null) {
				getUser().setOrg(
						new BaseOrg(request.getParameterInt("orgid"), request
								.getParameter("orgid_desp")));
				// getUser().getOrg().setOrgFullName(
				// request.getParameter("fullpath"));
			}
		}
	}

	private void getUser(Integer id) throws Throwable {
		if (id == null)
			return;
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			UserBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-userBean", UserBeanRemote.class);
			user = bean.find(id);
		} finally {
			context.close();
		}
	}

	private void addOrEditUser(HttpRequest request) throws Throwable {
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			UserBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-userBean", UserBeanRemote.class);
			if (getUser().getId() == null) {
				user.setId(bean.add(((AdminSessionObject) session)
						.getLoginUser().getUserId(), user, userPwd));
				added = true;
			} else {
				bean.edit(((AdminSessionObject) session).getLoginUser()
						.getUserId(), user, userPwd);
				added = false;
			}
		} finally {
			context.close();
		}
	}

	public String getSuccessScript() {
		String script = "var items={id:'" + user.getIdString() + "',desp:'"
				+ user.getUserName() + "',extflag:"
				+ Integer.toString(user.getUserGroup().getValue())
				+ ",columns:[";
		script += "'" + user.getUserName() + "',";
		if (user.getUserGroup().isCustomer()) {
			if (user.getRole().getId() != null)
				script += "'" + user.getRole().getRoleName() + "',";
			else
				script += "'" + user.getUserGroup().getString() + "',";
		} else
			script += "'" + user.getUserGroup() + "',";
		if (user.getOrg().getId() != null) {
			if (user.getOrg().getOrgFullName() == null
					|| user.getOrg().getOrgFullName().trim().length() == 0)
				script += "'" + user.getOrg().getOrgName() + "',";
			else
				script += "'" + user.getOrg().getOrgFullName() + "',";
		} else
			script += "null,";
		if (user.getManuf().getId() != null)
			script += "'" + user.getManuf().getManufName() + "'";
		else
			script += "''";
		script += "]};";
		if (added)
			script += "top.ajax_table.addsuccess(items);";
		else
			script += "top.ajax_table.editsuccess(items);";
		return script;
	}

	public EditedUser getUser() {
		if (user == null) {
			user = new EditedUser();
			user.setOrg(new BaseOrg());
			user.setUserGroup(UserGroup.OPERATOR);
		}
		return user;
	}

	public void setUser(EditedUser user) {
		this.user = user;
	}

	@Override
	public int getEditRight() {
		return UserRight.USER_EDIT;
	}

	@Override
	public int getQueryRight() {
		return UserRight.USER;
	}

	public String getUserPwd() {
		return userPwd;
	}

	public void setUserPwd(String userPwd) {
		this.userPwd = userPwd;
	}

	private Collection<CachedManuf> manufList;

	public Collection<CachedManuf> getManufList() throws NamingException {
		if (manufList == null) {
			LoginUser user = ((AdminSessionObject) session).getLoginUser();
			if (user.getUserGroup().isSystemManager()) {
				manufList = CacheHelper.manufMap.values();
				CachedManuf nm = new CachedManuf();
				nm.setManufName("");
				((List<CachedManuf>) manufList).add(0, nm);
			} else if (user.getManuf().getId() != null) {
				manufList = new ArrayList<CachedManuf>();
				CachedManuf manuf = new CachedManuf();
				manuf.setId(user.getManuf().getId());
				manuf.setManufName(user.getManuf().getManufName());
				manufList.add(manuf);
				CachedManuf nm = new CachedManuf();
				nm.setManufName("");
				((List<CachedManuf>) manufList).add(0, nm);
			} else
				manufList = new ArrayList<CachedManuf>();
		}
		return manufList;
	}

	private Collection<BaseRole> roleList;

	public Collection<BaseRole> getRoleList() throws NamingException {
		if (roleList == null) {
			LoginUser loginUser = ((AdminSessionObject) session).getLoginUser();
			LoopNamingContext context = new LoopNamingContext("db");
			try {
				UserBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
						"kxd-ejb-userBean", UserBeanRemote.class);
				roleList = bean.getUserManagedRoles(loginUser.getUserId(),
						null, null);
				((List<BaseRole>) roleList).add(0, new BaseRole());
			} finally {
				if (context != null)
					context.close();
			}
		}
		return roleList;
	}
}
