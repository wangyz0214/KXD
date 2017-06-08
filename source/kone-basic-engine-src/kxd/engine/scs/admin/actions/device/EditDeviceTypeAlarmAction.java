package kxd.engine.scs.admin.actions.device;

import javax.servlet.http.HttpServletResponse;

import kxd.engine.scs.admin.AdminSessionObject;
import kxd.engine.scs.admin.util.UserRight;
import kxd.engine.ui.core.EditAction;
import kxd.net.HttpRequest;
import kxd.net.connection.jndi.Lookuper;
import kxd.net.naming.LoopNamingContext;
import kxd.remote.scs.beans.BaseAlarmCategory;
import kxd.remote.scs.beans.BaseAlarmCode;
import kxd.remote.scs.interfaces.DeviceTypeBeanRemote;

public class EditDeviceTypeAlarmAction extends EditAction {
	private BaseAlarmCode alarmCode;
	boolean added;

	@Override
	protected String doExecute(boolean isFormSubmit, HttpRequest request,
			HttpServletResponse response) throws Throwable {
		if (isFormSubmit) {
			addOrEditDeviceTypeAlarm(request);
			return "success";
		} else
			return null;
	}

	@Override
	protected void setParameters(boolean isFormSubmit, HttpRequest request)
			throws Throwable {
		super.setParameters(isFormSubmit, request);
		if (isFormSubmit) {
			added = request.getParameterBooleanDef("add", true);
			getAlarmCode().setDeviceType(request.getParameterInt("devicetype"));
			getAlarmCode().setAlarmCategory(
					new BaseAlarmCategory(request
							.getParameterInt("alarmcategory")));
			getAlarmCode().setAlarmCode(request.getParameterInt("alarmcode"));
			getAlarmCode().setAlarmDesp(request.getParameter("alarmdesp"));
		} else {
			getDeviceTypeAlarm(request);
		}
	}

	private void getDeviceTypeAlarm(HttpRequest request) throws Throwable {
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			DeviceTypeBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-deviceTypeBean", DeviceTypeBeanRemote.class);
			Integer devicetype = request.getParameterIntDef("devicetype", null);
			Integer alarmcode = request.getParameterIntDef("alarmcode", null);
			if (alarmcode != null)
				alarmCode = bean.findAlarmCode(devicetype, alarmcode);
			else {
				getAlarmCode().setDeviceType(
						request.getParameterInt("devicetype"));
			}
		} finally {
			if (context != null)
				context.close();
		}
	}

	private void addOrEditDeviceTypeAlarm(HttpRequest request) throws Throwable {
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			DeviceTypeBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-deviceTypeBean", DeviceTypeBeanRemote.class);
			if (added) {
				bean.addAlarmCode(((AdminSessionObject) session).getLoginUser()
						.getUserId(), alarmCode);
			} else {
				bean.editAlarmCode(((AdminSessionObject) session)
						.getLoginUser().getUserId(), alarmCode);
			}
		} finally {
			if (context != null)
				context.close();
		}
	}

	public String getSuccessScript() {
		return "";
	}

	public BaseAlarmCode getAlarmCode() {
		if (alarmCode == null) {
			alarmCode = new BaseAlarmCode();
		}
		return alarmCode;
	}

	public void setAlarmCode(BaseAlarmCode alarmCode) {
		this.alarmCode = alarmCode;
	}

	@Override
	public int getEditRight() {
		return UserRight.DEVICETYPE_EDIT;
	}

	@Override
	public int getQueryRight() {
		return UserRight.DEVICETYPE;
	}
}
