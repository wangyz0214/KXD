package kxd.engine.scs.admin.actions.appdeploy;

import javax.servlet.http.HttpServletResponse;

import kxd.engine.scs.admin.AdminSessionObject;
import kxd.engine.scs.admin.util.UserRight;
import kxd.engine.ui.core.EditAction;
import kxd.net.HttpRequest;
import kxd.net.connection.jndi.Lookuper;
import kxd.net.naming.LoopNamingContext;
import kxd.remote.scs.beans.BaseBusinessCategory;
import kxd.remote.scs.interfaces.BusinessCategoryBeanRemote;

public class EditBusinessCategoryAction extends EditAction {
	private BaseBusinessCategory businessCategory;
	boolean added;

	@Override
	protected String doExecute(boolean isFormSubmit, HttpRequest request,
			HttpServletResponse response) throws Throwable {
		if (isFormSubmit) {
			addOrEditBusinessCategory();
			return "success";
		} else
			return null;
	}

	@Override
	protected void setParameters(boolean isFormSubmit, HttpRequest request)
			throws Throwable {
		super.setParameters(isFormSubmit, request);
		if (isFormSubmit) {
			businessCategory = getBusinessCategory();
			added = request.getParameterBoolean("add");
			businessCategory.setId(request.getParameterIntDef("id", null));
			businessCategory.setBusinessCategoryDesp(request
					.getParameter("desp"));
		} else {
			getBusinessCategory(request.getParameterIntDef("id", null));
		}
	}

	private void getBusinessCategory(Integer id) throws Throwable {
		if (id == null)
			return;
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			BusinessCategoryBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-businessCategoryBean",
					BusinessCategoryBeanRemote.class);
			businessCategory = bean.find(id);
		} finally {
			context.close();
		}
	}

	private void addOrEditBusinessCategory() throws Throwable {
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			BusinessCategoryBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-businessCategoryBean",
					BusinessCategoryBeanRemote.class);
			if (added) {
				businessCategory.setId(bean.add(((AdminSessionObject) session)
						.getLoginUser().getUserId(), businessCategory));
			} else {
				bean.edit(((AdminSessionObject) session).getLoginUser()
						.getUserId(), businessCategory);
			}
		} finally {
			context.close();
		}
	}

	public String getSuccessScript() {
		String script = "var items={id:'" + businessCategory.getIdString()
				+ "',desp:'" + businessCategory.getBusinessCategoryDesp()
				+ "',columns:[";
		script += "'" + businessCategory.getIdString() + "',";
		script += "'" + businessCategory.getBusinessCategoryDesp() + "'";
		script += "]};";
		if (added)
			script += "top.ajax_table.addsuccess(items);";
		else
			script += "top.ajax_table.editsuccess(items);";
		return script;
	}

	public BaseBusinessCategory getBusinessCategory() {
		if (businessCategory == null) {
			businessCategory = new BaseBusinessCategory();
		}
		return businessCategory;
	}

	public void setBusinessCategory(BaseBusinessCategory businessCategory) {
		this.businessCategory = businessCategory;
	}

	@Override
	public int getEditRight() {
		return UserRight.BUSINESSCATEGORY_EDIT;
	}

	@Override
	public int getQueryRight() {
		return UserRight.BUSINESSCATEGORY;
	}
}
