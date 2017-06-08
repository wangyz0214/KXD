package kxd.engine.scs.admin.actions.report;

import javax.servlet.http.HttpServletResponse;

import kxd.engine.scs.admin.util.UserRight;
import kxd.net.HttpRequest;

public class QueryUseRateReportAction extends BaseManufReportAction {

	@Override
	public int getEditRight() {
		return UserRight.USERATE_REPORT;
	}

	@Override
	public int getQueryRight() {
		return UserRight.USERATE_REPORT;
	}

	@Override
	protected String queryReport(HttpRequest request,
			HttpServletResponse response) throws Throwable {
		return null;
	}

}
