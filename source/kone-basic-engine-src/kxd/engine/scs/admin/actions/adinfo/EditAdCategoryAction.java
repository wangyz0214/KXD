package kxd.engine.scs.admin.actions.adinfo;

import javax.servlet.http.HttpServletResponse;

import kxd.engine.scs.admin.AdminSessionObject;
import kxd.engine.scs.admin.util.UserRight;
import kxd.engine.ui.core.EditAction;
import kxd.net.HttpRequest;
import kxd.net.connection.jndi.Lookuper;
import kxd.net.naming.LoopNamingContext;
import kxd.remote.scs.beans.BaseAdCategory;
import kxd.remote.scs.interfaces.AdCategoryBeanRemote;

public class EditAdCategoryAction extends EditAction {
	private BaseAdCategory adCategory;
	boolean added;

	@Override
	protected String doExecute(boolean isFormSubmit, HttpRequest request,
			HttpServletResponse response) throws Throwable {
		if (isFormSubmit) {
			addOrEditAdCategory();
			return "success";
		} else
			return null;
	}

	@Override
	protected void setParameters(boolean isFormSubmit, HttpRequest request)
			throws Throwable {
		super.setParameters(isFormSubmit, request);
		if (isFormSubmit) {
			adCategory = getAdCategory();
			added = request.getParameterBoolean("add");
			adCategory.setId(request.getParameterShortDef("id", null));
			adCategory.setAdCategoryDesp(request.getParameter("desp"));
		} else {
			getAdCategory(request.getParameterShortDef("id", null));
		}
	}

	private void getAdCategory(Short id) throws Throwable {
		if (id == null)
			return;
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			AdCategoryBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-adCategoryBean", AdCategoryBeanRemote.class);
			adCategory = bean.find(id);
		} finally {
			context.close();
		}
	}

	private void addOrEditAdCategory() throws Throwable {
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			AdCategoryBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-adCategoryBean", AdCategoryBeanRemote.class);
			if (added) {
				bean.add(((AdminSessionObject) session).getLoginUser()
						.getUserId(), adCategory);
			} else {
				bean.edit(((AdminSessionObject) session).getLoginUser()
						.getUserId(), adCategory);
			}
		} finally {
			context.close();
		}
	}

	public String getSuccessScript() {
		String script = "var items={id:'" + adCategory.getIdString()
				+ "',desp:'" + adCategory.getAdCategoryDesp() + "',columns:[";
		script += "'" + adCategory.getIdString() + "',";
		script += "'" + adCategory.getAdCategoryDesp() + "'";
		script += "]};";
		if (added)
			script += "top.ajax_table.addsuccess(items);";
		else
			script += "top.ajax_table.editsuccess(items);";
		return script;
	}

	public BaseAdCategory getAdCategory() {
		if (adCategory == null) {
			adCategory = new BaseAdCategory();
		}
		return adCategory;
	}

	public void setAdCategory(BaseAdCategory adCategory) {
		this.adCategory = adCategory;
	}

	@Override
	public int getEditRight() {
		return UserRight.PRINTADCATEGORY_EDIT;
	}

	@Override
	public int getQueryRight() {
		return UserRight.PRINTADCATEGORY;
	}
}
