package kxd.engine.scs.admin.actions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;

import javax.naming.NamingException;
import javax.servlet.http.HttpServletResponse;

import kxd.engine.cache.beans.sts.CachedTerm;
import kxd.engine.cache.beans.sts.MonitoredDeviceStatus;
import kxd.engine.helper.CacheHelper;
import kxd.engine.helper.MonitorHelper;
import kxd.engine.scs.admin.util.UserRight;
import kxd.engine.ui.core.FacesError;
import kxd.engine.ui.core.QueryAction;
import kxd.net.HttpRequest;

public class QueryTermDeviceStatusAction extends QueryAction {
	private List<MonitoredDeviceStatus> deviceStatusList = new ArrayList<MonitoredDeviceStatus>();
	int termId;

	@Override
	protected String doExecute(boolean isFormSubmit, HttpRequest request,
			HttpServletResponse response) throws Throwable {
		if (!isFormSubmit) {
			Enumeration<?> en = request.getParameterNames();
			while (en.hasMoreElements()) {
				String name = (String) en.nextElement();
				request.setAttribute(name, request.getParameter(name));
				if (name.equals("online")) {
					request.setAttribute("onlinestatus", request
							.getParameterBooleanDef("online", false) ? "在线"
							: "离线");
				}
			}
			queryDevcieStatus(request);
		}
		return null;
	}

	@Override
	protected void setParameters(boolean isFormSubmit, HttpRequest request)
			throws Throwable {
		super.setParameters(isFormSubmit, request);
		if (!isFormSubmit) {
			termId = Integer.valueOf(request.getParameter("termid"));
		}
	}

	public void queryDevcieStatus(HttpRequest request) throws NamingException,
			IOException, FacesError {
		CachedTerm term = CacheHelper.termMap.getTerm(termId);
		if (term != null) {
			MonitorHelper.getMonitoredDeviceStatus(termId, deviceStatusList);
		}
	}

	public int getTermId() {
		return termId;
	}

	public void setTermId(int termId) {
		this.termId = termId;
	}

	public Collection<MonitoredDeviceStatus> getDeviceStatusList() {
		return deviceStatusList;
	}

	@Override
	public int getEditRight() {
		return UserRight.TERMMONITOR;
	}

	@Override
	public int getQueryRight() {
		return UserRight.TERMMONITOR;
	}

}
