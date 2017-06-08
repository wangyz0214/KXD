package kxd.engine.scs.admin.actions.device;

import javax.servlet.http.HttpServletResponse;

import kxd.engine.scs.admin.AdminSessionObject;
import kxd.engine.scs.admin.util.UserRight;
import kxd.engine.ui.core.EditAction;
import kxd.net.HttpRequest;
import kxd.net.connection.jndi.Lookuper;
import kxd.net.naming.LoopNamingContext;
import kxd.remote.scs.beans.BaseAlarmCategory;
import kxd.remote.scs.interfaces.AlarmCategoryBeanRemote;
import kxd.remote.scs.util.emun.AlarmLevel;

public class EditAlarmCategoryAction extends EditAction {
	private BaseAlarmCategory alarmCategory;
	boolean added;

	@Override
	protected String doExecute(boolean isFormSubmit, HttpRequest request,
			HttpServletResponse response) throws Throwable {
		if (isFormSubmit) {
			addOrEditAlarmCategory();
			return "success";
		} else
			return null;
	}

	@Override
	protected void setParameters(boolean isFormSubmit, HttpRequest request)
			throws Throwable {
		super.setParameters(isFormSubmit, request);
		if (isFormSubmit) {
			alarmCategory = getAlarmCategory();
			added = request.getParameterBoolean("add");
			alarmCategory.setId(request.getParameterIntDef("id", null));
			alarmCategory.setAlarmCategoryDesp(request.getParameter("desp"));
			alarmCategory.setAlarmLevel(AlarmLevel.valueOf(request
					.getParameterInt("alarmlevel")));
		} else {
			getAlarmCategory(request.getParameterShortDef("id", null));
		}
	}

	private void getAlarmCategory(Short id) throws Throwable {
		if (id == null)
			return;
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			AlarmCategoryBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-alarmCategoryBean",
					AlarmCategoryBeanRemote.class);
			alarmCategory = bean.find(id);
		} finally {
			context.close();
		}
	}

	private void addOrEditAlarmCategory() throws Throwable {
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			AlarmCategoryBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-alarmCategoryBean",
					AlarmCategoryBeanRemote.class);
			if (added) {
				bean.add(((AdminSessionObject) session).getLoginUser()
						.getUserId(), alarmCategory);
			} else {
				bean.edit(((AdminSessionObject) session).getLoginUser()
						.getUserId(), alarmCategory);
			}
		} finally {
			context.close();
		}
	}

	public String getSuccessScript() {
		String script = "var items={id:'" + alarmCategory.getIdString()
				+ "',desp:'" + alarmCategory.getAlarmCategoryDesp()
				+ "',columns:[";
		script += "'" + alarmCategory.getIdString() + "',";
		script += "'" + alarmCategory.getAlarmCategoryDesp() + "',";
		script += "'" + alarmCategory.getAlarmLevel() + "'";
		script += "]};";
		if (added)
			script += "top.ajax_table.addsuccess(items);";
		else
			script += "top.ajax_table.editsuccess(items);";
		return script;
	}

	public BaseAlarmCategory getAlarmCategory() {
		if (alarmCategory == null) {
			alarmCategory = new BaseAlarmCategory();
		}
		return alarmCategory;
	}

	public void setAlarmCategory(BaseAlarmCategory alarmCategory) {
		this.alarmCategory = alarmCategory;
	}

	@Override
	public int getEditRight() {
		return UserRight.ALARMCATEGORY_EDIT;
	}

	@Override
	public int getQueryRight() {
		return UserRight.ALARMCATEGORY;
	}
}
