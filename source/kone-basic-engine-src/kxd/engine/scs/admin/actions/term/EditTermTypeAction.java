package kxd.engine.scs.admin.actions.term;

import javax.servlet.http.HttpServletResponse;

import kxd.engine.scs.admin.AdminSessionObject;
import kxd.engine.scs.admin.util.UserRight;
import kxd.engine.ui.core.EditAction;
import kxd.net.HttpRequest;
import kxd.net.connection.jndi.Lookuper;
import kxd.net.naming.LoopNamingContext;
import kxd.remote.scs.beans.BaseApp;
import kxd.remote.scs.beans.BaseManuf;
import kxd.remote.scs.beans.device.EditedTermType;
import kxd.remote.scs.interfaces.TermTypeBeanRemote;
import kxd.remote.scs.util.emun.CashFlag;
import kxd.remote.scs.util.emun.FixType;

public class EditTermTypeAction extends EditAction {
	private EditedTermType termType;
	boolean added;

	@Override
	protected String doExecute(boolean isFormSubmit, HttpRequest request,
			HttpServletResponse response) throws Throwable {
		if (isFormSubmit) {
			addOrEditTermType();
			return "success";
		} else
			return null;
	}

	@Override
	protected void setParameters(boolean isFormSubmit, HttpRequest request)
			throws Throwable {
		super.setParameters(isFormSubmit, request);
		if (isFormSubmit) {
			termType = getTermType();
			termType.setId(request.getParameterIntDef("id", null));
			termType.setTypeCode(request.getParameter("code"));
			termType.setTypeDesp(request.getParameter("desp"));
			BaseApp app = new BaseApp(request.getParameterInt("appid"), request
					.getParameter("appid_desp"));
			termType.setApp(app);
			BaseManuf manuf = new BaseManuf(request.getParameterInt("manufid"));
			termType.setManuf(manuf);
			termType.setCashFlag(CashFlag.valueOf(request
					.getParameterInt("cashflag")));
			termType.setFixType(FixType.valueOf(request
					.getParameterInt("fixtype")));
		} else {
			getTermType(request.getParameterIntDef("id", null));
		}
	}

	private void getTermType(Integer id) throws Throwable {
		if (id == null)
			return;
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			TermTypeBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-termTypeBean", TermTypeBeanRemote.class);
			termType = bean.find(id);
		} finally {
			context.close();
		}
	}

	private void addOrEditTermType() throws Throwable {
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			TermTypeBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-termTypeBean", TermTypeBeanRemote.class);
			if (getTermType().getId() == null) {
				termType.setId(bean.add(((AdminSessionObject) session)
						.getLoginUser().getUserId(), termType));
				added = true;
			} else {
				bean.edit(((AdminSessionObject) session).getLoginUser()
						.getUserId(), termType);
				added = false;
			}
		} finally {
			context.close();
		}
	}

	public String getSuccessScript() {
		String script = "var items={id:'" + termType.getIdString() + "',desp:'"
				+ termType.getTypeDesp() + "',columns:[";
		script += "'" + termType.getIdString() + "',";
		script += "'" + termType.getTypeCode() + "',";
		script += "'" + termType.getTypeDesp() + "',";
		script += "'" + termType.getApp().getAppDesp() + "',";
		script += "'" + termType.getManuf().getManufName() + "'";
		script += "]};";
		if (added)
			script += "top.ajax_table.addsuccess(items);";
		else
			script += "top.ajax_table.editsuccess(items);";
		return script;
	}

	public EditedTermType getTermType() {
		if (termType == null) {
			termType = new EditedTermType();
		}
		return termType;
	}

	public void setTermType(EditedTermType termType) {
		this.termType = termType;
	}

	@Override
	public int getEditRight() {
		return UserRight.TERMTYPE_EDIT;
	}

	@Override
	public int getQueryRight() {
		return UserRight.TERMTYPE;
	}
}
