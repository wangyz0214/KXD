package kxd.engine.scs.admin.actions.fileservice;

import javax.servlet.http.HttpServletResponse;

import kxd.engine.scs.admin.AdminSessionObject;
import kxd.engine.scs.admin.util.UserRight;
import kxd.engine.ui.core.EditAction;
import kxd.net.HttpRequest;
import kxd.net.connection.jndi.Lookuper;
import kxd.net.naming.LoopNamingContext;
import kxd.remote.scs.beans.BaseFileCategory;
import kxd.remote.scs.interfaces.FileCategoryBeanRemote;
import kxd.remote.scs.util.emun.FileCachedType;

public class EditFileCategoryAction extends EditAction {
	private BaseFileCategory fileCategory;
	boolean added;

	@Override
	protected String doExecute(boolean isFormSubmit, HttpRequest request,
			HttpServletResponse response) throws Throwable {
		if (isFormSubmit) {
			addOrEditFileCategory();
			return "success";
		} else
			return null;
	}

	@Override
	protected void setParameters(boolean isFormSubmit, HttpRequest request)
			throws Throwable {
		super.setParameters(isFormSubmit, request);
		if (isFormSubmit) {
			fileCategory = getFileCategory();
			added = request.getParameterBoolean("add");
			fileCategory.setId(request.getParameterShort("id"));
			fileCategory.setFileCategoryDesp(request.getParameter("desp"));
			fileCategory.setCachedType(FileCachedType.valueOf(request
					.getParameterInt("cachedtype")));
			fileCategory.setFileHost(request.getParameterShort("filehost"));
		} else {
			getFileCategory(request.getParameterShortDef("id", null));
		}
	}

	private void getFileCategory(Short id) throws Throwable {
		if (id == null)
			return;
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			FileCategoryBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-fileCategoryBean",
					FileCategoryBeanRemote.class);
			fileCategory = bean.find(id);
		} finally {
			context.close();
		}
	}

	private void addOrEditFileCategory() throws Throwable {
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			FileCategoryBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-fileCategoryBean",
					FileCategoryBeanRemote.class);
			if (added) {
				fileCategory.setId((short) bean.add(
						((AdminSessionObject) session).getLoginUser()
								.getUserId(), fileCategory));
			} else {
				bean.edit(((AdminSessionObject) session).getLoginUser()
						.getUserId(), fileCategory);
			}
		} finally {
			context.close();
		}
	}

	public String getSuccessScript() {
		String script = "var items={id:'" + fileCategory.getIdString()
				+ "',desp:'" + fileCategory.getFileCategoryDesp()
				+ "',columns:[";
		script += "'" + fileCategory.getIdString() + "',";
		script += "'" + fileCategory.getFileCategoryDesp() + "',";
		script += "'" + fileCategory.getCachedType() + "',";
		script += "]};";
		if (added)
			script += "top.ajax_table.addsuccess(items);";
		else
			script += "top.ajax_table.editsuccess(items);";
		return script;
	}

	public BaseFileCategory getFileCategory() {
		if (fileCategory == null) {
			fileCategory = new BaseFileCategory();
		}
		return fileCategory;
	}

	public void setFileCategory(BaseFileCategory fileCategory) {
		this.fileCategory = fileCategory;
	}

	@Override
	public int getEditRight() {
		return UserRight.FILECATEGORY_EDIT;
	}

	@Override
	public int getQueryRight() {
		return UserRight.FILECATEGORY;
	}
}
