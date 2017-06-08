package kxd.engine.scs.admin.actions.report;

import javax.servlet.http.HttpServletResponse;

import kxd.engine.scs.admin.util.UserRight;
import kxd.net.HttpRequest;
import kxd.net.connection.jndi.Lookuper;
import kxd.net.naming.LoopNamingContext;
import kxd.remote.scs.interfaces.ReportBeanRemote;
import kxd.util.DateTime;

public class QueryBusinessVisitReportAction extends BaseBusinessReportAction {

	protected String queryReport(HttpRequest request,
			HttpServletResponse response) throws Throwable {
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			ReportBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-reportBean", ReportBeanRemote.class);
			DateTime startDateTime = new DateTime(startDate, "yyyy-MM-dd");
			DateTime endDateTime = new DateTime(endDate, "yyyy-MM-dd");
			table = bean.queryOrgBusinessVisitReport(
					request.getParameterInt("orgid"), businessIdList,
					startDateTime.getTime(), endDateTime.getTime());
		} finally {
			context.close();
		}
		return null;
	}

	@Override
	public int getEditRight() {
		return UserRight.BUSINESSVISIT_REPORT;
	}

	@Override
	public int getQueryRight() {
		return UserRight.BUSINESSVISIT_REPORT;
	}
}
