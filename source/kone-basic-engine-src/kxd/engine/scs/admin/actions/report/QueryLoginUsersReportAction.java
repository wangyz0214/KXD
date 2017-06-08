package kxd.engine.scs.admin.actions.report;

import javax.servlet.http.HttpServletResponse;

import kxd.engine.scs.admin.util.UserRight;
import kxd.net.HttpRequest;
import kxd.net.connection.jndi.Lookuper;
import kxd.net.naming.LoopNamingContext;
import kxd.remote.scs.interfaces.ReportBeanRemote;
import kxd.util.DateTime;

public class QueryLoginUsersReportAction extends BaseReportAction {

	protected String queryReport(HttpRequest request,
			HttpServletResponse response) throws Throwable {
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			ReportBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-reportBean", ReportBeanRemote.class);
			DateTime t = new DateTime(startDate, "yyyy-MM-dd");
			table = bean.queryLoginUsersDayReport(orgid, t.getTime());
		} finally {
			context.close();
		}
		return null;
	}

	@Override
	public int getEditRight() {
		return UserRight.LOGINUSERS_REPORT;
	}

	@Override
	public int getQueryRight() {
		return UserRight.LOGINUSERS_REPORT;
	}

}
