package kxd.engine.scs.admin.actions.report;

import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import kxd.engine.cache.beans.sts.CachedPrintType;
import kxd.engine.helper.CacheHelper;
import kxd.engine.scs.admin.util.UserRight;
import kxd.net.HttpRequest;
import kxd.net.connection.jndi.Lookuper;
import kxd.net.naming.LoopNamingContext;
import kxd.remote.scs.interfaces.ReportBeanRemote;
import kxd.util.DateTime;
import kxd.util.StringUnit;

public class QueryPrintReportAction extends BaseManufReportAction {
	Collection<CachedPrintType> printTypeList;
	List<Short> printTypeIdList;
	String printtype;

	public String getPrinttype() {
		return printtype;
	}

	protected String queryReport(HttpRequest request,
			HttpServletResponse response) throws Throwable {
		boolean isreport = request.getParameter("isreport").equals("true");
		if (isreport) {
			LoopNamingContext context = new LoopNamingContext("db");
			try {
				ReportBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
						"kxd-ejb-reportBean", ReportBeanRemote.class);
				DateTime startDateTime = new DateTime(startDate, "yyyy-MM-dd");
				DateTime endDateTime = new DateTime(endDate, "yyyy-MM-dd");
				table = bean.queryOrgPrintReport(orgid, manufid,
						printTypeIdList, startDateTime.getTime(),
						endDateTime.getTime());

			} finally {
				context.close();
			}
		}
		return null;
	}

	@Override
	protected void setParameters(boolean isFormSubmit, HttpRequest request)
			throws Throwable {
		super.setParameters(isFormSubmit, request);
		if (orgid == null) {
			printTypeList = CacheHelper.printTypeMap.values();
		} else {
			printtype = request.getParameter("printtype");
			printTypeIdList = StringUnit.splitToShort1(printtype, ",");
			printTypeList = CacheHelper.printTypeMap.values(printTypeIdList);
		}
	}

	@Override
	public int getEditRight() {
		return UserRight.PRINT_REPORT;
	}

	@Override
	public int getQueryRight() {
		return UserRight.PRINT_REPORT;
	}

	public Collection<CachedPrintType> getPrintTypeList() {
		return printTypeList;
	}

	public void setPrintTypeList(Collection<CachedPrintType> printTypeList) {
		this.printTypeList = printTypeList;
	}

	public List<Short> getPrintTypeIdList() {
		return printTypeIdList;
	}

	public void setPrintTypeIdList(List<Short> printTypeIdList) {
		this.printTypeIdList = printTypeIdList;
	}

	public void setPrinttype(String printtype) {
		this.printtype = printtype;
	}

}
