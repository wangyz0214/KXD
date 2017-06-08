package kxd.engine.scs.admin.actions.fileservice;

import javax.servlet.http.HttpServletResponse;

import kxd.engine.scs.admin.AdminSessionObject;
import kxd.engine.scs.admin.util.UserRight;
import kxd.engine.ui.core.EditAction;
import kxd.net.HttpRequest;
import kxd.net.connection.jndi.Lookuper;
import kxd.net.naming.LoopNamingContext;
import kxd.remote.scs.beans.BaseFileOwner;
import kxd.remote.scs.interfaces.FileOwnerBeanRemote;
import kxd.remote.scs.util.emun.FileVisitRight;

public class EditFileOwnerAction extends EditAction {
	private BaseFileOwner fileOwner;
	boolean added;

	@Override
	protected String doExecute(boolean isFormSubmit, HttpRequest request,
			HttpServletResponse response) throws Throwable {
		if (isFormSubmit) {
			addOrEditFileOwner();
			return "success";
		} else
			return null;
	}

	@Override
	protected void setParameters(boolean isFormSubmit, HttpRequest request)
			throws Throwable {
		super.setParameters(isFormSubmit, request);
		if (isFormSubmit) {
			fileOwner = getFileOwner();
			added = request.getParameterBoolean("add");
			fileOwner.setId(request.getParameterShort("id"));
			fileOwner.setFileOwnerDesp(request.getParameter("desp"));
			fileOwner.setVisitRight(FileVisitRight.valueOf(request
					.getParameterInt("visitright")));
		} else {
			getFileOwner(request.getParameterShortDef("id", null));
		}
	}

	private void getFileOwner(Short id) throws Throwable {
		if (id == null)
			return;
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			FileOwnerBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-fileOwnerBean", FileOwnerBeanRemote.class);
			fileOwner = bean.find(id);
		} finally {
			context.close();
		}
	}

	private void addOrEditFileOwner() throws Throwable {
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			FileOwnerBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-fileOwnerBean", FileOwnerBeanRemote.class);
			if (added) {
				fileOwner.setId((short) bean.add(((AdminSessionObject) session)
						.getLoginUser().getUserId(), fileOwner));
			} else {
				bean.edit(((AdminSessionObject) session).getLoginUser()
						.getUserId(), fileOwner);
			}
		} finally {
			context.close();
		}
	}

	public String getSuccessScript() {
		String script = "var items={id:'" + fileOwner.getIdString()
				+ "',desp:'" + fileOwner.getFileOwnerDesp() + "',columns:[";
		script += "'" + fileOwner.getIdString() + "',";
		script += "'" + fileOwner.getFileOwnerDesp() + "',";
		script += "'" + fileOwner.getVisitRight() + "',";
		script += "]};";
		if (added)
			script += "top.ajax_table.addsuccess(items);";
		else
			script += "top.ajax_table.editsuccess(items);";
		return script;
	}

	public BaseFileOwner getFileOwner() {
		if (fileOwner == null) {
			fileOwner = new BaseFileOwner();
		}
		return fileOwner;
	}

	public void setFileOwner(BaseFileOwner fileOwner) {
		this.fileOwner = fileOwner;
	}

	@Override
	public int getEditRight() {
		return UserRight.FILEOWNER_EDIT;
	}

	@Override
	public int getQueryRight() {
		return UserRight.FILEOWNER;
	}
}
