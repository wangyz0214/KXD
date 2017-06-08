package kxd.engine.scs.admin.actions.report;

import javax.servlet.http.HttpServletResponse;

import kxd.engine.scs.admin.util.UserRight;
import kxd.net.HttpRequest;
import kxd.net.connection.jndi.Lookuper;
import kxd.net.naming.LoopNamingContext;
import kxd.remote.scs.interfaces.ReportBeanRemote;
import kxd.util.DateTime;

public class QueryReceivableReportAction extends BasePaywayReportAction {

	protected String queryReport(HttpRequest request,
			HttpServletResponse response) throws Throwable {
		boolean isReport = request.getParameter("isreport").equals("true");
		if (isReport) {
			LoopNamingContext context = new LoopNamingContext("db");
			try {
				ReportBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
						"kxd-ejb-reportBean", ReportBeanRemote.class);
				DateTime startDateTime = new DateTime(
						request.getParameter("startDate"), "yyyy-MM-dd");
				DateTime endDateTime = new DateTime(
						request.getParameter("endDate"), "yyyy-MM-dd");
				if (isReport) {
					table = bean.queryOrgReceivableReport(orgid, paywayIdList,
							startDateTime.getTime(), endDateTime.getTime());
				} else {
				}
			} finally {
				context.close();
			}
		}
		return null;
	}

	@Override
	public int getEditRight() {
		return UserRight.RECEIVABLE_REPORT;
	}

	@Override
	public int getQueryRight() {
		return UserRight.RECEIVABLE_REPORT;
	}

}
