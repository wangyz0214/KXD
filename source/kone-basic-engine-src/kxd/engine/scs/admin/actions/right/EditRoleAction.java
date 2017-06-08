package kxd.engine.scs.admin.actions.right;

import javax.servlet.http.HttpServletResponse;

import kxd.engine.scs.admin.AdminSessionObject;
import kxd.engine.scs.admin.util.UserRight;
import kxd.engine.ui.core.EditAction;
import kxd.net.HttpRequest;
import kxd.net.connection.jndi.Lookuper;
import kxd.net.naming.LoopNamingContext;
import kxd.remote.scs.beans.BaseRole;
import kxd.remote.scs.interfaces.RoleBeanRemote;

public class EditRoleAction extends EditAction {
	private BaseRole role;
	boolean added;

	@Override
	protected String doExecute(boolean isFormSubmit, HttpRequest request,
			HttpServletResponse response) throws Throwable {
		if (isFormSubmit) {
			addOrEditRole();
			return "success";
		} else
			return null;
	}

	@Override
	protected void setParameters(boolean isFormSubmit, HttpRequest request)
			throws Throwable {
		super.setParameters(isFormSubmit, request);
		if (isFormSubmit) {
			role = getRole();
			role.setId(request.getParameterIntDef("id", null));
			role.setRoleName(request.getParameter("desp"));
			added = role.getId() == null;
		} else {
			getRole(request.getParameterIntDef("id", null));
		}
	}

	private void getRole(Integer id) throws Throwable {
		if (id == null)
			return;
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			RoleBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-roleBean", RoleBeanRemote.class);
			role = bean.find(id);
		} finally {
			context.close();
		}
	}

	private void addOrEditRole() throws Throwable {
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			RoleBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-roleBean", RoleBeanRemote.class);
			if (added) {
				role.setId(bean.add(((AdminSessionObject) session)
						.getLoginUser().getUserId(), role));
			} else {
				bean.edit(((AdminSessionObject) session).getLoginUser()
						.getUserId(), role);
			}
		} finally {
			context.close();
		}
	}

	public String getSuccessScript() {
		String script = "var items={id:'" + role.getIdString() + "',desp:'"
				+ role.getRoleName() + "',columns:[";
		script += "'" + role.getIdString() + "',";
		script += "'" + role.getRoleName() + "'";
		script += "]};";
		if (added)
			script += "top.ajax_table.addsuccess(items);";
		else
			script += "top.ajax_table.editsuccess(items);";
		return script;
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

	@Override
	public int getEditRight() {
		return UserRight.ROLE_EDIT;
	}

	@Override
	public int getQueryRight() {
		return UserRight.ROLE;
	}
}
