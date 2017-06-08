package kxd.engine.scs.admin.actions.term;

import javax.servlet.http.HttpServletResponse;

import kxd.engine.scs.admin.AdminSessionObject;
import kxd.engine.scs.admin.util.UserRight;
import kxd.engine.ui.core.EditAction;
import kxd.net.HttpRequest;
import kxd.net.connection.jndi.Lookuper;
import kxd.net.naming.LoopNamingContext;
import kxd.remote.scs.beans.device.EditedTerm;
import kxd.remote.scs.interfaces.TermBeanRemote;

public class EditTermExtAction extends EditAction {
	private EditedTerm term;

	@Override
	protected String doExecute(boolean isFormSubmit, HttpRequest request,
			HttpServletResponse response) throws Throwable {
		if (isFormSubmit) {
			addOrEditTerm(request);
			return "success";
		} else
			return null;
	}

	@Override
	protected void setParameters(boolean isFormSubmit, HttpRequest request)
			throws Throwable {
		super.setParameters(isFormSubmit, request);
		if (isFormSubmit) {
			term = getTerm();
			term.setId(request.getParameterIntDef("id", null));
			term.setExtField0(request.getParameterDef("extfield0", ""));
			term.setExtField1(request.getParameterDef("extfield1", ""));
			term.setExtField2(request.getParameterDef("extfield2", ""));
			term.setExtField3(request.getParameterDef("extfield3", ""));
			term.setExtField4(request.getParameterDef("extfield4", ""));
		} else {
			getTerm(request.getParameterIntDef("id", null));
		}
	}

	private void getTerm(Integer id) throws Throwable {
		if (id == null)
			return;
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			TermBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-termBean", TermBeanRemote.class);
			term = bean.find(id);
		} finally {
			context.close();
		}
	}

	private void addOrEditTerm(HttpRequest request) throws Throwable {
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			TermBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-termBean", TermBeanRemote.class);
			bean.edit(
					((AdminSessionObject) session).getLoginUser().getUserId(),
					term);
		} finally {
			context.close();
		}
	}

	public String getSuccessScript() {
		return "";
	}

	public EditedTerm getTerm() {
		if (term == null) {
			term = new EditedTerm();
		}
		return term;
	}

	public void setTerm(EditedTerm term) {
		this.term = term;
	}

	@Override
	public int getEditRight() {
		return UserRight.TERM_EDIT;
	}

	@Override
	public int getQueryRight() {
		return UserRight.TERM;
	}
}
