package kxd.engine.scs.admin.actions.report;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;

import javax.servlet.http.HttpServletResponse;

import kxd.engine.cache.beans.sts.CachedAlarmCategory;
import kxd.engine.helper.CacheHelper;
import kxd.engine.scs.admin.util.UserRight;
import kxd.net.HttpRequest;
import kxd.remote.scs.util.emun.AlarmLevel;
import kxd.util.StringUnit;

public class QueryFaultReportAction extends BaseManufReportAction {
	Collection<CachedAlarmCategory> alarmCategoryList;
	String alarmcategoryid;

	public String getAlarmcategoryid() {
		return alarmcategoryid;
	}

	public Collection<CachedAlarmCategory> getAlarmCategoryList() {
		return alarmCategoryList;
	}

	@Override
	protected void setParameters(boolean isFormSubmit, HttpRequest request)
			throws Throwable {
		super.setParameters(isFormSubmit, request);
		if (orgid == null) {
			alarmCategoryList = new ArrayList<CachedAlarmCategory>();
			Enumeration<Integer> enc = CacheHelper.alarmCategoryMap.keys();
			while (enc.hasMoreElements()) {
				CachedAlarmCategory c = CacheHelper.alarmCategoryMap.get(enc
						.nextElement());
				if (c.getAlarmLevel().equals(AlarmLevel.FAULT))
					alarmCategoryList.add(c);
			}
		} else {
			alarmcategoryid = request.getParameter("alarmcategoryid");
			alarmCategoryList = CacheHelper.alarmCategoryMap.values(StringUnit
					.splitToInt1(alarmcategoryid, ","));
		}
	}

	@Override
	public int getEditRight() {
		return UserRight.FAULT_REPORT;
	}

	@Override
	public int getQueryRight() {
		return UserRight.FAULT_REPORT;
	}

	@Override
	protected String queryReport(HttpRequest request,
			HttpServletResponse response) throws Throwable {
		return null;
	}

}
