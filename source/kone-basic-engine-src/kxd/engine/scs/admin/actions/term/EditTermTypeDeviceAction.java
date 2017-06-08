package kxd.engine.scs.admin.actions.term;

import java.util.List;

import javax.naming.NamingException;
import javax.servlet.http.HttpServletResponse;

import kxd.engine.scs.admin.AdminSessionObject;
import kxd.engine.scs.admin.util.UserRight;
import kxd.engine.ui.core.EditAction;
import kxd.net.HttpRequest;
import kxd.net.connection.jndi.Lookuper;
import kxd.net.naming.LoopNamingContext;
import kxd.remote.scs.beans.BaseDevice;
import kxd.remote.scs.beans.BaseTermType;
import kxd.remote.scs.beans.device.EditedTermTypeDevice;
import kxd.remote.scs.interfaces.DeviceBeanRemote;
import kxd.remote.scs.interfaces.TermTypeBeanRemote;

public class EditTermTypeDeviceAction extends EditAction {
	private EditedTermTypeDevice termTypeDevice;
	boolean added;
	List<BaseDevice> deviceList;

	@Override
	protected String doExecute(boolean isFormSubmit, HttpRequest request,
			HttpServletResponse response) throws Throwable {
		if (isFormSubmit) {
			addOrEditTermTypeDevice(request);
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
			termTypeDevice = getTermTypeDevice();
			termTypeDevice.setTermType(new BaseTermType(request
					.getParameterInt("typeid"), request
					.getParameter("typeid_desp")));
			termTypeDevice.setDevice(new BaseDevice(request
					.getParameterInt("deviceid"), request
					.getParameter("deviceid_desp")));
			termTypeDevice.setPort(request.getParameterInt("port"));
			termTypeDevice.setExtConfig(request.getParameterDef("extconfig",
					null));
		} else {
			getTermTypeDevice(request);
		}
	}

	private void getTermTypeDevice(HttpRequest request) throws Throwable {
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			TermTypeBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-termTypeBean", TermTypeBeanRemote.class);
			Integer deviceid = request.getParameterIntDef("deviceid", null);
			if (deviceid != null)
				termTypeDevice = bean.findDevice(((AdminSessionObject) session)
						.getLoginUser().getUserId(), request
						.getParameterInt("typeid"), deviceid);
			else {
				getTermTypeDevice().setTermType(
						new BaseTermType(request.getParameterInt("typeid"),
								request.getParameter("typeid_desp")));
			}
		} finally {
			if (context != null)
				context.close();
		}
	}

	private void addOrEditTermTypeDevice(HttpRequest request) throws Throwable {
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			TermTypeBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-termTypeBean", TermTypeBeanRemote.class);
			if (added) {
				bean.addTermTypeDevice(((AdminSessionObject) session)
						.getLoginUser().getUserId(), termTypeDevice);
			} else {
				bean.editTermTypeDevice(((AdminSessionObject) session)
						.getLoginUser().getUserId(), termTypeDevice);
			}
		} finally {
			if (context != null)
				context.close();
		}
	}

	public String getSuccessScript() {
		return "";
	}

	public EditedTermTypeDevice getTermTypeDevice() {
		if (termTypeDevice == null) {
			termTypeDevice = new EditedTermTypeDevice();
		}
		return termTypeDevice;
	}

	public void setTermTypeDevice(EditedTermTypeDevice termTypeDevice) {
		this.termTypeDevice = termTypeDevice;
	}

	public List<BaseDevice> getDeviceList() throws NamingException {
		if (deviceList == null) {
			LoopNamingContext context = new LoopNamingContext("db");
			try {
				DeviceBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
						"kxd-ejb-deviceBean", DeviceBeanRemote.class);
				deviceList = bean.getDeviceList(((AdminSessionObject) session)
						.getLoginUser().getUserId(), termTypeDevice
						.getTermType().getId(), null);

			} finally {
				if (context != null)
					context.close();
			}
		}
		return deviceList;
	}

	@Override
	public int getEditRight() {
		return UserRight.TERMTYPE_EDIT;
	}

	@Override
	public int getQueryRight() {
		return UserRight.TERMTYPE;
	}
}
