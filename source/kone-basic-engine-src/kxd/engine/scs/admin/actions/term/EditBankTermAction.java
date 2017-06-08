package kxd.engine.scs.admin.actions.term;

import javax.servlet.http.HttpServletResponse;

import kxd.engine.scs.admin.AdminSessionObject;
import kxd.engine.scs.admin.util.UserRight;
import kxd.engine.ui.core.EditAction;
import kxd.net.HttpRequest;
import kxd.net.connection.jndi.Lookuper;
import kxd.net.naming.LoopNamingContext;
import kxd.remote.scs.beans.device.EditedBankTerm;
import kxd.remote.scs.interfaces.BankTermBeanRemote;

public class EditBankTermAction extends EditAction {
	private EditedBankTerm bankTerm;
	boolean added;

	@Override
	protected String doExecute(boolean isFormSubmit, HttpRequest request,
			HttpServletResponse response) throws Throwable {
		if (isFormSubmit) {
			addOrEditBankTerm();
			return "success";
		} else
			return null;
	}

	@Override
	protected void setParameters(boolean isFormSubmit, HttpRequest request)
			throws Throwable {
		super.setParameters(isFormSubmit, request);
		if (isFormSubmit) {
			bankTerm = getBankTerm();
			bankTerm.setId(request.getParameterIntDef("id", null));
			bankTerm.setBankTermCode(request.getParameter("code"));
			bankTerm.setBankTermDesp(request.getParameter("desp"));
			bankTerm.setWorkKey(" ");
			bankTerm.setBatch(" ");
			bankTerm.setMacKey(" ");
			bankTerm
					.setMerchantAccount(request.getParameter("merchantaccount"));
			bankTerm.setExtField(request.getParameter("extfield"));
			added = bankTerm.getId() == null;
		} else {
			getBankTerm(request.getParameterIntDef("id", null));
		}
	}

	private void getBankTerm(Integer id) throws Throwable {
		if (id == null)
			return;
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			BankTermBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-bankTermBean", BankTermBeanRemote.class);
			bankTerm = bean.find(id);
		} finally {
			context.close();
		}
	}

	private void addOrEditBankTerm() throws Throwable {
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			BankTermBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-bankTermBean", BankTermBeanRemote.class);
			if (added) {
				bankTerm.setId(bean.add(((AdminSessionObject) session)
						.getLoginUser().getUserId(), bankTerm));
			} else {
				bean.edit(((AdminSessionObject) session).getLoginUser()
						.getUserId(), bankTerm);
			}
		} finally {
			context.close();
		}
	}

	public String getSuccessScript() {
		String script = "var items={id:'" + bankTerm.getIdString() + "',desp:'"
				+ bankTerm.getBankTermDesp() + "',columns:[";
		script += "'" + bankTerm.getIdString() + "',";
		script += "'" + bankTerm.getBankTermCode() + "',";
		script += "'" + bankTerm.getBankTermDesp() + "',";
		script += "'"
				+ (bankTerm.getMerchantAccount() == null ? "" : bankTerm
						.getMerchantAccount()) + "',";
		script += "'"
				+ (bankTerm.getExtField() == null ? "" : bankTerm.getExtField())
				+ "',";
		script += "]};";
		if (added)
			script += "top.ajax_table.addsuccess(items);";
		else
			script += "top.ajax_table.editsuccess(items);";
		return script;
	}

	public EditedBankTerm getBankTerm() {
		if (bankTerm == null) {
			bankTerm = new EditedBankTerm();
		}
		return bankTerm;
	}

	public void setBankTerm(EditedBankTerm bankTerm) {
		this.bankTerm = bankTerm;
	}

	@Override
	public int getEditRight() {
		return UserRight.BANKTERM_EDIT;
	}

	@Override
	public int getQueryRight() {
		return UserRight.BANKTERM;
	}
}
