package kxd.engine.scs.admin.actions.report;

import java.util.Collection;
import java.util.List;

import kxd.engine.cache.beans.sts.CachedPayWay;
import kxd.engine.helper.CacheHelper;
import kxd.net.HttpRequest;
import kxd.util.StringUnit;

abstract public class BasePaywayReportAction extends BaseReportAction {
	Collection<CachedPayWay> paywayList;
	String paywayid;
	List<Short> paywayIdList;

	protected Collection<CachedPayWay> buildPayWayList() {
		return CacheHelper.payWayMap.values();
	}

	@Override
	protected void setParameters(boolean isFormSubmit, HttpRequest request)
			throws Throwable {
		super.setParameters(isFormSubmit, request);
		if (orgid == null) {
			paywayList = buildPayWayList();
		} else {
			paywayid = request.getParameter("paywayid");
			paywayIdList = StringUnit.splitToShort1(paywayid, ",");
			paywayList = CacheHelper.payWayMap.values(paywayIdList);
		}
	}

	public Collection<CachedPayWay> getPaywayList() {
		return paywayList;
	}

	public void setPaywayList(Collection<CachedPayWay> paywayList) {
		this.paywayList = paywayList;
	}

	public String getPaywayid() {
		return paywayid;
	}

	public void setPaywayid(String paywayid) {
		this.paywayid = paywayid;
	}

}
