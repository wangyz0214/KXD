package kxd.engine.scs.admin.actions.report;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import kxd.engine.ui.core.QueryAction;
import kxd.net.HttpRequest;

abstract public class BaseReportAction extends QueryAction {
	protected Integer orgid;
	protected String orgDesp;
	protected List<?> table;
	protected String startDate, endDate;
	protected String province, city, hall;

	abstract protected String queryReport(HttpRequest request,
			HttpServletResponse response) throws Throwable;

	@Override
	protected String doExecute(boolean isFormSubmit, HttpRequest request,
			HttpServletResponse response) throws Throwable {
		if (orgid != null) {
			return queryReport(request, response);
		}
		return null;
	}

	@Override
	protected void setParameters(boolean isFormSubmit, HttpRequest request)
			throws Throwable {
		super.setParameters(isFormSubmit, request);
		orgid = request.getParameterIntDef("orgid", null);
		orgDesp = request.getParameterDef("orgdesp", null);
		if (orgid != null) {
			startDate = request.getParameter("startDate");
			endDate = request.getParameterDef("endDate", startDate);
			province = request.getParameterDef("province", null);
			city = request.getParameterDef("city", null);
			hall = request.getParameterDef("hall", null);
		}
	}

	public String getOrgDesp() {
		return orgDesp;
	}

	public void setOrgDesp(String orgDesp) {
		this.orgDesp = orgDesp;
	}

	public List<?> getTable() {
		return table;
	}

	public void setTable(List<?> table) {
		this.table = table;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public Integer getOrgid() {
		return orgid;
	}

	public void setOrgid(Integer orgid) {
		this.orgid = orgid;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getHall() {
		return hall;
	}

	public void setHall(String hall) {
		this.hall = hall;
	}

}
