package kxd.engine.scs.admin.actions.settlement;

import javax.servlet.http.HttpServletResponse;

import kxd.engine.scs.admin.actions.report.BaseBusinessReportAction;
import kxd.engine.scs.admin.util.UserRight;
import kxd.net.HttpRequest;

import org.apache.log4j.Logger;

public class QuerySettlementAction extends BaseBusinessReportAction {
	static Logger logger = Logger.getLogger(QuerySettlementAction.class);

	@Override
	public int getEditRight() {
		return UserRight.TRADEPROC;
	}

	@Override
	public int getQueryRight() {
		return UserRight.TRADEPROC;
	}

	@Override
	protected String queryReport(HttpRequest request,
			HttpServletResponse response) throws Throwable {
		return null;
	}
}
