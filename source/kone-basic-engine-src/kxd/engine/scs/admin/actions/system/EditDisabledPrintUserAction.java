package kxd.engine.scs.admin.actions.system;

import javax.servlet.http.HttpServletResponse;

import kxd.engine.scs.admin.AdminSessionObject;
import kxd.engine.scs.admin.util.UserRight;
import kxd.engine.ui.core.EditAction;
import kxd.net.HttpRequest;
import kxd.net.connection.jndi.Lookuper;
import kxd.net.naming.LoopNamingContext;
import kxd.remote.scs.beans.BaseDisabledPrintUser;
import kxd.remote.scs.interfaces.DisabledPrintUserBeanRemote;

public class EditDisabledPrintUserAction extends EditAction {
	private BaseDisabledPrintUser disabledPrintUser;
	boolean added;

	@Override
	protected String doExecute(boolean isFormSubmit, HttpRequest request,
			HttpServletResponse response) throws Throwable {
		if (isFormSubmit) {
			addOrEditDisabledPrintUser();
			return "success";
		} else
			return null;
	}

	@Override
	protected void setParameters(boolean isFormSubmit, HttpRequest request)
			throws Throwable {
		super.setParameters(isFormSubmit, request);
		if (isFormSubmit) {
			disabledPrintUser = getDisabledPrintUser();
			added = request.getParameterBoolean("add");
			disabledPrintUser.setId(request.getParameterLongDef("id", null));
			disabledPrintUser.setUserno(request.getParameter("desp"));
		} else {
			getDisabledPrintUser(request.getParameterIntDef("id", null));
		}
	}

	private void getDisabledPrintUser(Integer id) throws Throwable {
		if (id == null)
			return;
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			DisabledPrintUserBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-disabledPrintUserBean",
					DisabledPrintUserBeanRemote.class);
			disabledPrintUser = bean.find(id);
		} finally {
			context.close();
		}
	}

	private void addOrEditDisabledPrintUser() throws Throwable {
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			DisabledPrintUserBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-disabledPrintUserBean",
					DisabledPrintUserBeanRemote.class);
			if (added) {
				disabledPrintUser.setId(bean.add(((AdminSessionObject) session)
						.getLoginUser().getUserId(), disabledPrintUser));
			} else {
				bean.edit(((AdminSessionObject) session).getLoginUser()
						.getUserId(), disabledPrintUser);
			}
		} finally {
			context.close();
		}
	}

	public String getSuccessScript() {
		String script = "var items={id:'" + disabledPrintUser.getIdString()
				+ "',desp:'" + disabledPrintUser.getUserno() + "',columns:[";
		script += "'" + disabledPrintUser.getUserno() + "'";
		script += "]};";
		if (added)
			script += "top.ajax_table.addsuccess(items);";
		else
			script += "top.ajax_table.editsuccess(items);";
		return script;
	}

	public BaseDisabledPrintUser getDisabledPrintUser() {
		if (disabledPrintUser == null) {
			disabledPrintUser = new BaseDisabledPrintUser();
		}
		return disabledPrintUser;
	}

	public void setDisabledPrintUser(BaseDisabledPrintUser disabledPrintUser) {
		this.disabledPrintUser = disabledPrintUser;
	}

	@Override
	public int getEditRight() {
		return UserRight.DISABLEPRINTACCOUNT_EDIT;
	}

	@Override
	public int getQueryRight() {
		return UserRight.DISABLEPRINTACCOUNT;
	}
}
