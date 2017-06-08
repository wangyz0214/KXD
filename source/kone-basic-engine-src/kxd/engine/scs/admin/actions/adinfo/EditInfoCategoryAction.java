package kxd.engine.scs.admin.actions.adinfo;

import javax.servlet.http.HttpServletResponse;

import kxd.engine.scs.admin.AdminSessionObject;
import kxd.engine.scs.admin.util.UserRight;
import kxd.engine.ui.core.EditAction;
import kxd.net.HttpRequest;
import kxd.net.connection.jndi.Lookuper;
import kxd.net.naming.LoopNamingContext;
import kxd.remote.scs.beans.BaseInfoCategory;
import kxd.remote.scs.interfaces.InfoCategoryBeanRemote;

public class EditInfoCategoryAction extends EditAction {
	private BaseInfoCategory infoCategory;
	boolean added;

	@Override
	protected String doExecute(boolean isFormSubmit, HttpRequest request,
			HttpServletResponse response) throws Throwable {
		if (isFormSubmit) {
			addOrEditInfoCategory();
			return "success";
		} else
			return null;
	}

	@Override
	protected void setParameters(boolean isFormSubmit, HttpRequest request)
			throws Throwable {
		super.setParameters(isFormSubmit, request);
		if (isFormSubmit) {
			infoCategory = getInfoCategory();
			added = request.getParameterBoolean("add");
			infoCategory.setId(request.getParameterShortDef("id", null));
			infoCategory.setInfoCategoryDesp(request.getParameter("desp"));
		} else {
			getInfoCategory(request.getParameterShortDef("id", null));
		}
	}

	private void getInfoCategory(Short id) throws Throwable {
		if (id == null)
			return;
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			InfoCategoryBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-infoCategoryBean",
					InfoCategoryBeanRemote.class);
			infoCategory = bean.find(id);
		} finally {
			context.close();
		}
	}

	private void addOrEditInfoCategory() throws Throwable {
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			InfoCategoryBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-infoCategoryBean",
					InfoCategoryBeanRemote.class);
			if (added) {
				bean.add(((AdminSessionObject) session).getLoginUser()
						.getUserId(), infoCategory);
			} else {
				bean.edit(((AdminSessionObject) session).getLoginUser()
						.getUserId(), infoCategory);
			}
		} finally {
			context.close();
		}
	}

	public String getSuccessScript() {
		String script = "var items={id:'" + infoCategory.getIdString()
				+ "',desp:'" + infoCategory.getInfoCategoryDesp()
				+ "',columns:[";
		script += "'" + infoCategory.getIdString() + "',";
		script += "'" + infoCategory.getInfoCategoryDesp() + "'";
		script += "]};";
		if (added)
			script += "top.ajax_table.addsuccess(items);";
		else
			script += "top.ajax_table.editsuccess(items);";
		return script;
	}

	public BaseInfoCategory getInfoCategory() {
		if (infoCategory == null) {
			infoCategory = new BaseInfoCategory();
		}
		return infoCategory;
	}

	public void setInfoCategory(BaseInfoCategory infoCategory) {
		this.infoCategory = infoCategory;
	}

	@Override
	public int getEditRight() {
		return UserRight.INFOCATEGORY_EDIT;
	}

	@Override
	public int getQueryRight() {
		return UserRight.INFOCATEGORY;
	}
}
