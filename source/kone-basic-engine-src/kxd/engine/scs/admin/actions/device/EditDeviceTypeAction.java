package kxd.engine.scs.admin.actions.device;

import javax.servlet.http.HttpServletResponse;

import kxd.engine.scs.admin.AdminSessionObject;
import kxd.engine.scs.admin.util.UserRight;
import kxd.engine.ui.core.EditAction;
import kxd.net.HttpRequest;
import kxd.net.connection.jndi.Lookuper;
import kxd.net.naming.LoopNamingContext;
import kxd.remote.scs.beans.BaseDeviceTypeDriver;
import kxd.remote.scs.beans.device.EditedDeviceType;
import kxd.remote.scs.interfaces.DeviceTypeBeanRemote;
import kxd.remote.scs.util.emun.FaultPromptOption;

public class EditDeviceTypeAction extends EditAction {
	private EditedDeviceType deviceType;
	boolean added;

	@Override
	protected String doExecute(boolean isFormSubmit, HttpRequest request,
			HttpServletResponse response) throws Throwable {
		if (isFormSubmit) {
			addOrEditDeviceType();
			return "success";
		} else
			return null;
	}

	@Override
	protected void setParameters(boolean isFormSubmit, HttpRequest request)
			throws Throwable {
		super.setParameters(isFormSubmit, request);
		if (isFormSubmit) {
			deviceType = getDeviceType();
			deviceType.setId(request.getParameterIntDef("id", null));
			BaseDeviceTypeDriver b = new BaseDeviceTypeDriver(request
					.getParameterInt("driverid"));
			b.setDeviceTypeDriverDesp(request.getParameter("driverid_desp"));
			deviceType.setDriver(b);
			deviceType.setDeviceTypeDesp(request.getParameter("desp"));
			deviceType.setAlarmNotifyOption(request
					.getParameterShort("alarmnotifyoption"));
			deviceType.setAlarmSendForm(request
					.getParameterBoolean("alarmsendform"));
			deviceType.setDeviceTypeCode(request.getParameter("code"));
			deviceType.setFaultSendForm(request
					.getParameterBoolean("faultsendform"));
			deviceType.setFaultNotifyOption(request
					.getParameterShort("faultnotifyoption"));
			deviceType.setFaultPromptOption(FaultPromptOption.valueOf(request
					.getParameterInt("faultpromptoption")));
		} else {
			getDeviceType(request.getParameterIntDef("id", null));
		}
	}

	private void getDeviceType(Integer id) throws Throwable {
		if (id == null)
			return;
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			DeviceTypeBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-deviceTypeBean", DeviceTypeBeanRemote.class);
			deviceType = bean.find(id);
		} finally {
			context.close();
		}
	}

	private void addOrEditDeviceType() throws Throwable {
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			DeviceTypeBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-deviceTypeBean", DeviceTypeBeanRemote.class);
			if (getDeviceType().getId() == null) {
				deviceType.setId(bean.add(((AdminSessionObject) session)
						.getLoginUser().getUserId(), deviceType));
				added = true;
			} else {
				bean.edit(((AdminSessionObject) session).getLoginUser()
						.getUserId(), deviceType);
				added = false;
			}
		} finally {
			context.close();
		}
	}

	public String getSuccessScript() {
		String script = "var items={id:'" + deviceType.getIdString()
				+ "',desp:'" + deviceType.getDeviceTypeDesp() + "',columns:[";
		script += "'" + deviceType.getIdString() + "',";
		script += "'" + deviceType.getDeviceTypeCode() + "',";
		script += "'" + deviceType.getDeviceTypeDesp() + "',";
		script += "'" + deviceType.getDriver().getDeviceTypeDriverDesp() + "'";
		script += "]};";
		if (added)
			script += "top.ajax_table.addsuccess(items);";
		else
			script += "top.ajax_table.editsuccess(items);";
		return script;
	}

	public EditedDeviceType getDeviceType() {
		if (deviceType == null) {
			deviceType = new EditedDeviceType();
		}
		return deviceType;
	}

	public void setDeviceType(EditedDeviceType deviceType) {
		this.deviceType = deviceType;
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
