package kxd.engine.scs.admin.actions.appdeploy;

import javax.servlet.http.HttpServletResponse;

import kxd.engine.scs.admin.AdminSessionObject;
import kxd.engine.scs.admin.util.UserRight;
import kxd.engine.ui.core.EditAction;
import kxd.net.HttpRequest;
import kxd.net.connection.jndi.Lookuper;
import kxd.net.naming.LoopNamingContext;
import kxd.remote.scs.beans.BaseBusinessCategory;
import kxd.remote.scs.beans.appdeploy.EditedBusiness;
import kxd.remote.scs.interfaces.BusinessBeanRemote;

public class EditBusinessAction extends EditAction {
	private EditedBusiness business;
	boolean added;

	@Override
	protected String doExecute(boolean isFormSubmit, HttpRequest request,
			HttpServletResponse response) throws Throwable {
		if (isFormSubmit) {
			addOrEditBusiness();
			return "success";
		} else
			return null;
	}

	@Override
	protected void setParameters(boolean isFormSubmit, HttpRequest request)
			throws Throwable {
		super.setParameters(isFormSubmit, request);
		if (isFormSubmit) {
			business = getBusiness();
			business.setId(request.getParameterIntDef("id", null));
			BaseBusinessCategory b = new BaseBusinessCategory(request
					.getParameterInt("businesscategoryid"));
			b.setBusinessCategoryDesp(request
					.getParameter("businesscategoryid_desp"));
			business.setBusinessCategory(b);
			business.setBusinessDesp(request.getParameter("desp"));
		} else {
			getBusiness(request.getParameterIntDef("id", null));
		}
	}

	private void getBusiness(Integer id) throws Throwable {
		if (id == null)
			return;
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			BusinessBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-businessBean", BusinessBeanRemote.class);
			business = bean.find(id);
		} finally {
			context.close();
		}
	}

	private void addOrEditBusiness() throws Throwable {
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			BusinessBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-businessBean", BusinessBeanRemote.class);
			if (getBusiness().getId() == null) {
				business.setId(bean.add(((AdminSessionObject) session)
						.getLoginUser().getUserId(), business));
				added = true;
			} else {
				bean.edit(((AdminSessionObject) session).getLoginUser()
						.getUserId(), business);
				added = false;
			}
		} finally {
			context.close();
		}
	}

	public String getSuccessScript() {
		String script = "var items={id:'" + business.getIdString() + "',desp:'"
				+ business.getBusinessDesp() + "',columns:[";
		script += "'" + business.getIdString() + "',";
		script += "'" + business.getBusinessDesp() + "',";
		script += "'"
				+ business.getBusinessCategory().getBusinessCategoryDesp()
				+ "'";
		script += "]};";
		if (added)
			script += "top.ajax_table.addsuccess(items);";
		else
			script += "top.ajax_table.editsuccess(items);";
		return script;
	}

	public EditedBusiness getBusiness() {
		if (business == null) {
			business = new EditedBusiness();
		}
		return business;
	}

	public void setBusiness(EditedBusiness business) {
		this.business = business;
	}

	@Override
	public int getEditRight() {
		return UserRight.BUSINESS_EDIT;
	}

	@Override
	public int getQueryRight() {
		return UserRight.BUSINESS;
	}
}
