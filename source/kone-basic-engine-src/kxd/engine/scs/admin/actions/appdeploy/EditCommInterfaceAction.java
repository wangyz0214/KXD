package kxd.engine.scs.admin.actions.appdeploy;

import javax.servlet.http.HttpServletResponse;

import kxd.engine.scs.admin.AdminSessionObject;
import kxd.engine.scs.admin.util.UserRight;
import kxd.engine.ui.core.EditAction;
import kxd.net.HttpRequest;
import kxd.net.connection.jndi.Lookuper;
import kxd.net.naming.LoopNamingContext;
import kxd.remote.scs.beans.BaseCommInterface;
import kxd.remote.scs.interfaces.CommInterfaceBeanRemote;

public class EditCommInterfaceAction extends EditAction {
	private BaseCommInterface commInterface;
	boolean added;

	@Override
	protected String doExecute(boolean isFormSubmit, HttpRequest request,
			HttpServletResponse response) throws Throwable {
		if (isFormSubmit) {
			addOrEditCommInterface();
			return "success";
		} else
			return null;
	}

	@Override
	protected void setParameters(boolean isFormSubmit, HttpRequest request)
			throws Throwable {
		super.setParameters(isFormSubmit, request);
		if (isFormSubmit) {
			commInterface = getCommInterface();
			commInterface.setId(request.getParameterShortDef("id", null));
			commInterface.setType(request.getParameterIntDef("type", 0));
			commInterface.setDesp(request.getParameter("desp"));
		} else {
			getCommInterface(request.getParameterShortDef("id", null));
		}
	}

	private void getCommInterface(Short id) throws Throwable {
		if (id == null)
			return;
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			CommInterfaceBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-commInterfaceBean",
					CommInterfaceBeanRemote.class);
			commInterface = bean.find(id);
		} finally {
			context.close();
		}
	}

	private void addOrEditCommInterface() throws Throwable {
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			CommInterfaceBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-commInterfaceBean",
					CommInterfaceBeanRemote.class);
			if (getCommInterface().getId() == null) {
				commInterface.setId(bean.add(((AdminSessionObject) session)
						.getLoginUser().getUserId(), commInterface));
				added = true;
			} else {
				bean.edit(((AdminSessionObject) session).getLoginUser()
						.getUserId(), commInterface);
				added = false;
			}
		} finally {
			context.close();
		}
	}

	public String getSuccessScript() {
		String script = "var items={id:'" + commInterface.getIdString()
				+ "',desp:'" + commInterface.getDesp() + "',columns:[";
		script += "'" + commInterface.getIdString() + "',";
		script += "'" + commInterface.getDesp() + "'";
		script += "]};";
		if (added)
			script += "top.ajax_table.addsuccess(items);";
		else
			script += "top.ajax_table.editsuccess(items);";
		return script;
	}

	public BaseCommInterface getCommInterface() {
		if (commInterface == null) {
			commInterface = new BaseCommInterface();
		}
		return commInterface;
	}

	public void setCommInterface(BaseCommInterface commInterface) {
		this.commInterface = commInterface;
	}

	@Override
	public int getEditRight() {
		return UserRight.COMMINTERFACE_EDIT;
	}

	@Override
	public int getQueryRight() {
		return UserRight.COMMINTERFACE;
	}
}
