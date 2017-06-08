package kxd.engine.scs.admin.actions.fileservice;

import javax.servlet.http.HttpServletResponse;

import kxd.engine.scs.admin.AdminSessionObject;
import kxd.engine.scs.admin.util.UserRight;
import kxd.engine.ui.core.EditAction;
import kxd.net.HttpRequest;
import kxd.net.connection.jndi.Lookuper;
import kxd.net.naming.LoopNamingContext;
import kxd.remote.scs.beans.BaseFileHost;
import kxd.remote.scs.interfaces.FileHostBeanRemote;

public class EditFileHostAction extends EditAction {
	private BaseFileHost fileHost;
	boolean added;

	@Override
	protected String doExecute(boolean isFormSubmit, HttpRequest request,
			HttpServletResponse response) throws Throwable {
		if (isFormSubmit) {
			addOrEditFileHost();
			return "success";
		} else
			return null;
	}

	@Override
	protected void setParameters(boolean isFormSubmit, HttpRequest request)
			throws Throwable {
		super.setParameters(isFormSubmit, request);
		if (isFormSubmit) {
			fileHost = getFileHost();
			added = request.getParameterBoolean("add");
			fileHost.setId(request.getParameterShort("id"));
			fileHost.setHostDesp(request.getParameter("desp"));
			fileHost.setFileRootDir(request.getParameter("rootdir"));
			fileHost.setRealHttpUrlRoot(request.getParameterDef(
					"realhttpurlroot", null));
			fileHost.setHttpUrlPrefix(request.getParameterDef("httpurlprefix",
					null));
			fileHost.setFtpHost(request.getParameterDef("ftphost", null));
			fileHost.setFtpUser(request.getParameterDef("ftpuser", null));
			if (added)
				fileHost.setFtpPasswd(request.getParameter("ftppasswd"));
			else
				fileHost.setFtpPasswd(request
						.getParameterDef("ftppasswd", null));
		} else {
			getFileHost(request.getParameterShortDef("id", null));
		}
	}

	private void getFileHost(Short id) throws Throwable {
		if (id == null)
			return;
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			FileHostBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-fileHostBean", FileHostBeanRemote.class);
			fileHost = bean.find(id);
		} finally {
			context.close();
		}
	}

	private void addOrEditFileHost() throws Throwable {
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			FileHostBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-fileHostBean", FileHostBeanRemote.class);
			if (added) {
				fileHost.setId((short) bean.add(((AdminSessionObject) session)
						.getLoginUser().getUserId(), fileHost));
			} else {
				bean.edit(((AdminSessionObject) session).getLoginUser()
						.getUserId(), fileHost);
			}
		} finally {
			context.close();
		}
	}

	public String getSuccessScript() {
		String script = "var items={id:'" + fileHost.getIdString() + "',desp:'"
				+ fileHost.getHostDesp() + "',columns:[";
		script += "'" + fileHost.getIdString() + "',";
		script += "'" + fileHost.getHostDesp() + "',";
		script += "'" + fileHost.getFileRootDir() + "',";
		script += "]};";
		if (added)
			script += "top.ajax_table.addsuccess(items);";
		else
			script += "top.ajax_table.editsuccess(items);";
		return script;
	}

	public BaseFileHost getFileHost() {
		if (fileHost == null) {
			fileHost = new BaseFileHost();
		}
		return fileHost;
	}

	public void setFileHost(BaseFileHost fileHost) {
		this.fileHost = fileHost;
	}

	@Override
	public int getEditRight() {
		return UserRight.FILEHOST_EDIT;
	}

	@Override
	public int getQueryRight() {
		return UserRight.FILEHOST;
	}
}
