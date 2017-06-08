package kxd.engine.scs.admin.actions.device;

import javax.servlet.http.HttpServletResponse;

import kxd.engine.scs.admin.AdminSessionObject;
import kxd.engine.scs.admin.util.UserRight;
import kxd.engine.ui.core.EditAction;
import kxd.net.HttpRequest;
import kxd.net.connection.jndi.Lookuper;
import kxd.net.naming.LoopNamingContext;
import kxd.remote.scs.beans.BaseDeviceDriver;
import kxd.remote.scs.beans.BaseDeviceType;
import kxd.remote.scs.beans.device.EditedDevice;
import kxd.remote.scs.interfaces.DeviceBeanRemote;

public class EditDeviceAction extends EditAction {
	private EditedDevice device;
	boolean added;

	@Override
	protected String doExecute(boolean isFormSubmit, HttpRequest request,
			HttpServletResponse response) throws Throwable {
		if (isFormSubmit) {
			addOrEditDevice();
			return "success";
		} else
			return null;
	}

	@Override
	protected void setParameters(boolean isFormSubmit, HttpRequest request)
			throws Throwable {
		super.setParameters(isFormSubmit, request);
		if (isFormSubmit) {
			device = getDevice();
			device.setId(request.getParameterIntDef("id", null));
			BaseDeviceDriver b = new BaseDeviceDriver(request
					.getParameterInt("driverid"));
			b.setDeviceDriverDesp(request.getParameter("driverid_desp"));
			device.setDriver(b);
			BaseDeviceType t = new BaseDeviceType(request
					.getParameterInt("typeid"));
			t.setDeviceTypeDesp(request.getParameter("typeid_desp"));
			device.setDeviceType(t);
			device.setDeviceName(request.getParameter("desp"));
		} else {
			getDevice(request.getParameterIntDef("id", null));
		}
	}

	private void getDevice(Integer id) throws Throwable {
		if (id == null)
			return;
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			DeviceBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-deviceBean", DeviceBeanRemote.class);
			device = bean.find(id);
		} finally {
			context.close();
		}
	}

	private void addOrEditDevice() throws Throwable {
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			DeviceBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-deviceBean", DeviceBeanRemote.class);
			if (getDevice().getId() == null) {
				device.setId(bean.add(((AdminSessionObject) session)
						.getLoginUser().getUserId(), device));
				added = true;
			} else {
				bean.edit(((AdminSessionObject) session).getLoginUser()
						.getUserId(), device);
				added = false;
			}
		} finally {
			context.close();
		}
	}

	public String getSuccessScript() {
		String script = "var items={id:'" + device.getIdString() + "',desp:'"
				+ device.getDeviceName() + "',columns:[";
		script += "'" + device.getIdString() + "',";
		script += "'" + device.getDeviceName() + "',";
		script += "'" + device.getDeviceType().getDeviceTypeDesp() + "',";
		script += "'" + device.getDriver().getDeviceDriverDesp() + "'";
		script += "]};";
		if (added)
			script += "top.ajax_table.addsuccess(items);";
		else
			script += "top.ajax_table.editsuccess(items);";
		return script;
	}

	public EditedDevice getDevice() {
		if (device == null) {
			device = new EditedDevice();
		}
		return device;
	}

	public void setDevice(EditedDevice device) {
		this.device = device;
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
