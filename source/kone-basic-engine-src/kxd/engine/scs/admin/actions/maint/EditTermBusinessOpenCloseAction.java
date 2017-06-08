package kxd.engine.scs.admin.actions.maint;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import kxd.engine.cache.beans.sts.CachedBusiness;
import kxd.engine.cache.beans.sts.CachedBusinessCategory;
import kxd.engine.helper.CacheHelper;
import kxd.engine.scs.admin.AdminSessionObject;
import kxd.engine.scs.admin.util.UserRight;
import kxd.engine.ui.core.EditAction;
import kxd.net.HttpRequest;
import kxd.net.connection.jndi.Lookuper;
import kxd.net.naming.LoopNamingContext;
import kxd.remote.scs.beans.BaseTermBusinessOpenClose;
import kxd.remote.scs.interfaces.TermBusinessOpenCloseRemote;
import kxd.remote.scs.util.emun.BusinessOpenCloseMode;
import kxd.util.DataUnit;

public class EditTermBusinessOpenCloseAction extends EditAction {
	private BaseTermBusinessOpenClose value;
	boolean added;
	List<CachedBusinessCategory> businessCategoryList;
	List<CachedBusiness> businessList;
	int termid;

	@Override
	protected String doExecute(boolean isFormSubmit, HttpRequest request,
			HttpServletResponse response) throws Throwable {
		if (isFormSubmit) {
			addOrEditInfo(request);
			return "success";
		} else
			return null;
	}

	@Override
	protected void setParameters(boolean isFormSubmit, HttpRequest request)
			throws Throwable {
		super.setParameters(isFormSubmit, request);
		added = request.getParameterBooleanDef("added", false);
		if (!isFormSubmit) {
			Long id = request.getParameterLongDef("id", null);
			if (id != null) {
				LoopNamingContext context = new LoopNamingContext("db");
				try {
					TermBusinessOpenCloseRemote bean = context.lookup(
							Lookuper.JNDI_TYPE_EJB,
							"kxd-ejb-termBusinessOpenCloseBean",
							TermBusinessOpenCloseRemote.class);
					value = bean.find(id);
				} finally {
					if (context != null)
						context.close();
				}
			} else {
				getValue()
						.setTermId(request.getParameterIntDef("termid", null));
			}
			businessCategoryList = (List<CachedBusinessCategory>) CacheHelper.businessCategoryMap
					.values();
			businessList = (List<CachedBusiness>) CacheHelper.businessMap
					.values();
		}
	}

	private void addOrEditInfo(HttpRequest request) throws Throwable {
		getValue().setId(request.getParameterLongDef("id", null));
		getValue().setBusinessCategoryIds(
				request.getParameter("businesscategoryids"));
		value.setBusinessIds(request.getParameter("businessids"));
		value.setOpenTimes(request.getParameter("opentimes"));
		value.setPayWays(request.getParameter("payways"));
		value.setPayItems(request.getParameter("payitems"));
		value.setOpenMode(BusinessOpenCloseMode.valueOfIntString(request
				.getParameter("openmode")));
		value.setStartDate(DataUnit.parseDateTime(
				request.getParameter("startdate"), "yyyy-MM-dd"));
		value.setEndDate(DataUnit.parseDateTime(
				request.getParameter("enddate"), "yyyy-MM-dd"));
		value.setReason(request.getParameter("reason"));
		value.setTermId(request.getParameterInt("termid"));
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			TermBusinessOpenCloseRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-termBusinessOpenCloseBean",
					TermBusinessOpenCloseRemote.class);
			if (added) {
				bean.add(((AdminSessionObject) session).getLoginUser()
						.getUserId(), getValue());
			} else {
				bean.edit(((AdminSessionObject) session).getLoginUser()
						.getUserId(), getValue());
			}
		} finally {
			if (context != null)
				context.close();
		}
	}

	public String getSuccessScript() {
		String script = "var items={id:'" + value.getIdString() + "',desp:'"
				+ value.getIdString() + "',columns:[";
		script += "'"
				+ DataUnit.formatDateTime(value.getStartDate(), "yyyy-MM-dd")
				+ "','";
		script += DataUnit.formatDateTime(value.getEndDate(), "yyyy-MM-dd")
				+ "',";
		script += "'" + value.getOpenTimes() + "'";
		script += ",'" + value.getBusinessCategoryIds() + "'";
		script += ",'" + value.getBusinessIds() + "'";
		script += "]};";
		if (added)
			script += "top.ajax_table.addsuccess(items);";
		else
			script += "top.ajax_table.editsuccess(items);";
		return script;
	}

	public BaseTermBusinessOpenClose getValue() {
		if (value == null) {
			value = new BaseTermBusinessOpenClose();
		}
		return value;
	}

	@Override
	public int getEditRight() {
		return UserRight.TERMBUSINESSOPENCLOSE_EDIT;
	}

	@Override
	public int getQueryRight() {
		return UserRight.TERMBUSINESSOPENCLOSE;
	}

	public boolean isAdded() {
		return added;
	}

	public void setAdded(boolean added) {
		this.added = added;
	}

	public List<CachedBusinessCategory> getBusinessCategoryList() {
		return businessCategoryList;
	}

	public void setBusinessCategoryList(
			List<CachedBusinessCategory> businessCategoryList) {
		this.businessCategoryList = businessCategoryList;
	}

	public List<CachedBusiness> getBusinessList() {
		return businessList;
	}

	public void setBusinessList(List<CachedBusiness> businessList) {
		this.businessList = businessList;
	}

	public void setValue(BaseTermBusinessOpenClose value) {
		this.value = value;
	}

	public int getOrgid() {
		return termid;
	}

	public void setOrgid(int termid) {
		this.termid = termid;
	}

	public String getStartDate() {
		if (getValue().getStartDate() == null)
			return null;
		return DataUnit.formatDateTime(getValue().getStartDate(), "yyyy-MM-dd");
	}

	public String getEndDate() {
		if (getValue().getEndDate() == null)
			return null;
		return DataUnit.formatDateTime(getValue().getEndDate(), "yyyy-MM-dd");
	}
}
