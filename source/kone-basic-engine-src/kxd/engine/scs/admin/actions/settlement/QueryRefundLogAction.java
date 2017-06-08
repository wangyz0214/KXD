package kxd.engine.scs.admin.actions.settlement;

import javax.servlet.http.HttpServletResponse;

import kxd.engine.scs.admin.actions.report.BaseReportAction;
import kxd.engine.scs.admin.util.UserRight;
import kxd.net.HttpRequest;

import org.apache.log4j.Logger;

public class QueryRefundLogAction extends BaseReportAction {
	static Logger logger = Logger.getLogger(QueryRefundLogAction.class);

	@Override
	public int getEditRight() {
		return UserRight.REFUND_LOG;
	}

	@Override
	public int getQueryRight() {
		return UserRight.REFUND_LOG;
	}

	@Override
	protected String queryReport(HttpRequest request,
			HttpServletResponse response) throws Throwable {
		return null;
	}
}
