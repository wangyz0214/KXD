package kxd.engine.scs.admin.actions.system;

import javax.servlet.http.HttpServletResponse;

import kxd.engine.scs.admin.AdminSessionObject;
import kxd.engine.scs.admin.util.UserRight;
import kxd.engine.ui.core.EditAction;
import kxd.net.HttpRequest;
import kxd.net.connection.jndi.Lookuper;
import kxd.net.naming.LoopNamingContext;
import kxd.remote.scs.beans.BasePrintType;
import kxd.remote.scs.interfaces.PrintTypeBeanRemote;

public class EditPrintTypeAction extends EditAction {
	private BasePrintType printType;
	boolean added;

	@Override
	protected String doExecute(boolean isFormSubmit, HttpRequest request,
			HttpServletResponse response) throws Throwable {
		if (isFormSubmit) {
			addOrEditPrintType();
			return "success";
		} else
			return null;
	}

	@Override
	protected void setParameters(boolean isFormSubmit, HttpRequest request)
			throws Throwable {
		super.setParameters(isFormSubmit, request);
		if (isFormSubmit) {
			added = request.getParameterBoolean("add");
			printType = getPrintType();
			printType.setId(request.getParameterShort("id"));
			printType.setPrintTypeDesp(request.getParameter("desp"));
		} else {
			getPrintType(request.getParameterIntDef("id", null));
		}
	}

	private void getPrintType(Integer id) throws Throwable {
		if (id == null)
			return;
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			PrintTypeBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-printTypeBean", PrintTypeBeanRemote.class);
			printType = bean.find(id);
		} finally {
			context.close();
		}
	}

	private void addOrEditPrintType() throws Throwable {
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			PrintTypeBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-printTypeBean", PrintTypeBeanRemote.class);
			if (added) {
				bean.add(((AdminSessionObject) session).getLoginUser()
						.getUserId(), printType);
			} else {
				bean.edit(((AdminSessionObject) session).getLoginUser()
						.getUserId(), printType);
			}
		} finally {
			context.close();
		}
	}

	public String getSuccessScript() {
		String script = "var items={id:'" + printType.getIdString()
				+ "',desp:'" + printType.getPrintTypeDesp() + "',columns:[";
		script += "'" + printType.getIdString() + "',";
		script += "'" + printType.getPrintTypeDesp() + "'";
		script += "]};";
		if (added)
			script += "top.ajax_table.addsuccess(items);";
		else
			script += "top.ajax_table.editsuccess(items);";
		return script;
	}

	public BasePrintType getPrintType() {
		if (printType == null) {
			printType = new BasePrintType();
		}
		return printType;
	}

	public void setPrintType(BasePrintType printType) {
		this.printType = printType;
	}

	@Override
	public int getEditRight() {
		return UserRight.PRINTTYPE_EDIT;
	}

	@Override
	public int getQueryRight() {
		return UserRight.PRINTTYPE;
	}
}
