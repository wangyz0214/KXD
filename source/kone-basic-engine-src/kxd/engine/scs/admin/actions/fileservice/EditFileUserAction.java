package kxd.engine.scs.admin.actions.fileservice;

import javax.servlet.http.HttpServletResponse;

import kxd.engine.scs.admin.AdminSessionObject;
import kxd.engine.scs.admin.util.UserRight;
import kxd.engine.ui.core.EditAction;
import kxd.net.HttpRequest;
import kxd.net.connection.jndi.Lookuper;
import kxd.net.naming.LoopNamingContext;
import kxd.remote.scs.beans.BaseFileOwner;
import kxd.remote.scs.beans.BaseFileUser;
import kxd.remote.scs.interfaces.FileUserBeanRemote;
import kxd.util.DataSecurity;

public class EditFileUserAction extends EditAction {
	private BaseFileUser fileUser;
	boolean added;

	@Override
	protected String doExecute(boolean isFormSubmit, HttpRequest request,
			HttpServletResponse response) throws Throwable {
		if (isFormSubmit) {
			addOrEditFileUser();
			return "success";
		} else
			return null;
	}

	@Override
	protected void setParameters(boolean isFormSubmit, HttpRequest request)
			throws Throwable {
		super.setParameters(isFormSubmit, request);
		if (isFormSubmit) {
			fileUser = getFileUser();
			added = request.getParameterBoolean("add");
			fileUser.setId(request.getParameter("id"));
			String userpwd = request.getParameterDef("userpwd", null);
			if (userpwd != null && !userpwd.isEmpty())
				fileUser.setFileUserPwd(DataSecurity.md5(userpwd));
			fileUser.setFileOwner(new BaseFileOwner(request
					.getParameterShort("fileowner")));
			fileUser.getFileOwner().setFileOwnerDesp(
					request.getParameter("fileowner_desp"));
		} else {
			getFileUser(request.getParameterDef("id", null));
		}
	}

	private void getFileUser(String id) throws Throwable {
		if (id == null)
			return;
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			FileUserBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-fileUserBean", FileUserBeanRemote.class);
			fileUser = bean.find(id);
		} finally {
			context.close();
		}
	}

	private void addOrEditFileUser() throws Throwable {
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			FileUserBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-fileUserBean", FileUserBeanRemote.class);
			if (added) {
				bean.add(((AdminSessionObject) session).getLoginUser()
						.getUserId(), fileUser);
			} else {
				bean.edit(((AdminSessionObject) session).getLoginUser()
						.getUserId(), fileUser);
			}
		} finally {
			context.close();
		}
	}

	public String getSuccessScript() {
		String script = "var items={id:'" + fileUser.getIdString() + "',desp:'"
				+ fileUser.getId() + "',columns:[";
		script += "'" + fileUser.getIdString() + "',";
		script += "'" + fileUser.getFileOwner().getFileOwnerDesp() + "',";
		script += "]};";
		if (added)
			script += "top.ajax_table.addsuccess(items);";
		else
			script += "top.ajax_table.editsuccess(items);";
		return script;
	}

	public BaseFileUser getFileUser() {
		if (fileUser == null) {
			fileUser = new BaseFileUser();
		}
		return fileUser;
	}

	public void setFileUser(BaseFileUser fileUser) {
		this.fileUser = fileUser;
	}

	@Override
	public int getEditRight() {
		return UserRight.FILEUSER_EDIT;
	}

	@Override
	public int getQueryRight() {
		return UserRight.FILEUSER;
	}
}
