package kxd.engine.scs.admin.actions.right;

import javax.servlet.http.HttpServletResponse;

import kxd.engine.scs.admin.AdminSessionObject;
import kxd.engine.scs.admin.util.UserRight;
import kxd.engine.ui.core.EditAction;
import kxd.net.HttpRequest;
import kxd.net.connection.jndi.Lookuper;
import kxd.net.naming.LoopNamingContext;
import kxd.remote.scs.beans.right.EditedManuf;
import kxd.remote.scs.interfaces.ManufBeanRemote;

public class EditManufAction extends EditAction {
	private EditedManuf manuf;
	boolean added;

	@Override
	protected String doExecute(boolean isFormSubmit, HttpRequest request,
			HttpServletResponse response) throws Throwable {
		if (isFormSubmit) {
			addOrEditManuf();
			return "success";
		} else
			return null;
	}

	@Override
	protected void setParameters(boolean isFormSubmit, HttpRequest request)
			throws Throwable {
		super.setParameters(isFormSubmit, request);
		if (isFormSubmit) {
			manuf = getManuf();
			manuf.setId(request.getParameterIntDef("id", null));
			manuf.setManufCode(request.getParameter("code"));
			manuf.setManufName(request.getParameter("desp"));
			manuf.setSerialNumber(request.getParameterIntDef("serialnumber", 0));
			manuf.setManufType(request.getParameterShortDef("manuftype",(short)0));
		} else {
			getManuf(request.getParameterIntDef("id", null));
		}
	}

	private void getManuf(Integer id) throws Throwable {
		if (id == null)
			return;
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			ManufBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-manufBean", ManufBeanRemote.class);
			manuf = bean.find(id);
		} finally {
			context.close();
		}
	}

	private void addOrEditManuf() throws Throwable {
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			ManufBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-manufBean", ManufBeanRemote.class);
			if (getManuf().getId() == null) {
				manuf.setId(bean.add(((AdminSessionObject) session)
						.getLoginUser().getUserId(), manuf));
				added = true;
			} else {
				bean.edit(((AdminSessionObject) session).getLoginUser()
						.getUserId(), manuf);
				added = false;
			}
		} finally {
			context.close();
		}
	}

	public String getSuccessScript() {
		String script = "var items={id:'" + manuf.getIdString() + "',desp:'"
				+ manuf.getManufName() + "',columns:[";
		script += "'" + manuf.getIdString() + "',";
		script += "'" + manuf.getManufCode() + "',";
		script += "'" + manuf.getManufName() + "'";
		script += "]};";
		if (added)
			script += "top.ajax_table.addsuccess(items);";
		else
			script += "top.ajax_table.editsuccess(items);";
		return script;
	}

	public EditedManuf getManuf() {
		if (manuf == null) {
			manuf = new EditedManuf();
		}
		return manuf;
	}

	public void setManuf(EditedManuf manuf) {
		this.manuf = manuf;
	}

	@Override
	public int getEditRight() {
		return UserRight.MANUF_EDIT;
	}

	@Override
	public int getQueryRight() {
		return UserRight.MANUF;
	}
}
