package kxd.engine.scs.admin.actions.term;

import java.util.ArrayList;
import java.util.Collection;

import javax.naming.NamingException;
import javax.servlet.http.HttpServletResponse;

import kxd.engine.cache.beans.sts.CachedManuf;
import kxd.engine.helper.CacheHelper;
import kxd.engine.scs.admin.AdminSessionObject;
import kxd.engine.scs.admin.util.UserRight;
import kxd.engine.ui.core.EditAction;
import kxd.net.HttpRequest;
import kxd.net.connection.jndi.Lookuper;
import kxd.net.naming.LoopNamingContext;
import kxd.remote.scs.beans.BaseApp;
import kxd.remote.scs.beans.BaseBankTerm;
import kxd.remote.scs.beans.BaseOrg;
import kxd.remote.scs.beans.BaseTermType;
import kxd.remote.scs.beans.device.EditedTerm;
import kxd.remote.scs.beans.right.LoginUser;
import kxd.remote.scs.interfaces.TermBeanRemote;
import kxd.remote.scs.util.emun.SettlementType;
import kxd.remote.scs.util.emun.TermStatus;
import kxd.util.DateTime;

public class EditTermAction extends EditAction {
	private EditedTerm term;
	boolean added;
	private boolean activeDisabled;

	@Override
	protected String doExecute(boolean isFormSubmit, HttpRequest request,
			HttpServletResponse response) throws Throwable {
		if (isFormSubmit) {
			addOrEditTerm(request);
			return "success";
		} else
			return null;
	}

	@Override
	protected void setParameters(boolean isFormSubmit, HttpRequest request)
			throws Throwable {
		super.setParameters(isFormSubmit, request);
		if (isFormSubmit) {
			term = getTerm();
			activeDisabled = request.getParameterBooleanDef("activedisabled",
					false);
			if (activeDisabled)
				term.setStatus(TermStatus.NOTACTIVE);
			term.setId(request.getParameterIntDef("id", null));
			term.setTermCode(request.getParameter("code"));
			term.setTermDesp(request.getParameter("desp"));
			term.setBankTerm(new BaseBankTerm());
			term.getBankTerm().setBankTermCode(
					request.getParameterDef("banktermcode", null));
			term.getBankTerm().setMerchantAccount(
					request.getParameterDef("merchantaccount", null));
			Integer tid = request.getParameterIntDef("typeid", null);
			if (tid != null)
				term.setTermType(new BaseTermType(tid, request
						.getParameter("typeid_desp")));
			term.setAddress(request.getParameterDef("address", null));
			term.setAreaCode(request.getParameterDef("areacode", null));
			term.setContacter(request.getParameterDef("contacter", null));
			term.setApp(new BaseApp(request.getParameterInt("appid"), request
					.getParameter("appid_desp")));
			DateTime opentime = new DateTime(request.getParameter("opentime"),
					"HHmm");
			DateTime closetime = new DateTime(
					request.getParameter("closetime"), "HHmm");
			term.setCloseTime(closetime.format("HHmm"));
			term.setOpenTime(opentime.format("HHmm"));
			term.setDayRunTime((short) opentime.hoursBetween(closetime));
			term.setGuid(request.getParameterDef("guid", null));
			term.setManufNo(request.getParameter("manufno"));
			term.setTermType(new BaseTermType(
					request.getParameterInt("typeid"), request
							.getParameter("typeid_desp")));
			term.setSettlementType(SettlementType.valueOf(request
					.getParameterInt("settlementtype")));
			if (term.getTermId() == null) {
				Integer orgid = request.getParameterInt("orgid");
				term.setOrg(new BaseOrg(orgid, request
						.getParameter("orgid_desp")));
			} else
				term.setOrg(new BaseOrg());
		} else {
			getTerm(request.getParameterIntDef("id", null));
			if (term == null) {
				getTerm().setOrg(
						new BaseOrg(request.getParameterInt("orgid"), request
								.getParameter("orgid_desp")));
			}
		}
	}

	private void getTerm(Integer id) throws Throwable {
		if (id == null)
			return;
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			TermBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-termBean", TermBeanRemote.class);
			term = bean.find(id);
		} finally {
			context.close();
		}
	}

	private void addOrEditTerm(HttpRequest request) throws Throwable {
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			TermBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-termBean", TermBeanRemote.class);
			if (getTerm().getId() == null) {
				term.setId(bean.add(((AdminSessionObject) session)
						.getLoginUser().getUserId(), term));
				added = true;
			} else {
				bean.edit(((AdminSessionObject) session).getLoginUser()
						.getUserId(), term);
				added = false;
			}
		} finally {
			context.close();
		}
	}

	public String getSuccessScript() {
		String script = "var items={id:'" + term.getIdString() + "',desp:'"
				+ term.getTermDesp() + "',columns:[";
		script += "'" + term.getIdString() + "',";
		script += "'" + term.getTermDesp() + "',";
		if (term.getOrg().getId() != null) {
			if (term.getOrg().getOrgFullName() == null
					|| term.getOrg().getOrgFullName().trim().length() == 0)
				script += "'" + term.getOrg().getOrgName() + "',";
			else
				script += "'" + term.getOrg().getOrgFullName() + "',";
		} else
			script += "null,";
		script += "'" + term.getTermType().getTypeDesp() + "'";
		script += "]};";
		if (added)
			script += "top.ajax_table.addsuccess(items);";
		else
			script += "top.ajax_table.editsuccess(items);";
		return script;
	}

	public EditedTerm getTerm() {
		if (term == null) {
			term = new EditedTerm();
		}
		return term;
	}

	public void setTerm(EditedTerm term) {
		this.term = term;
	}

	@Override
	public int getEditRight() {
		return UserRight.TERM_EDIT;
	}

	@Override
	public int getQueryRight() {
		return UserRight.TERM;
	}

	private Boolean guidEditEnabled;

	public boolean isGuidEditEnabled() {
		if (guidEditEnabled == null) {
			guidEditEnabled = ((AdminSessionObject) session)
					.hasRight(UserRight.TERM_EDITGUID);
		}
		return guidEditEnabled;
	}

	private Collection<CachedManuf> manufList;

	public Collection<CachedManuf> getManufList() throws NamingException {
		if (manufList == null) {
			LoginUser user = ((AdminSessionObject) session).getLoginUser();
			if (user.getManuf().getId() != null) {
				manufList = new ArrayList<CachedManuf>();
				CachedManuf manuf = new CachedManuf();
				manuf.setId(user.getManuf().getId());
				manuf.setManufName(user.getManuf().getManufName());
				manufList.add(manuf);
			} else {
				manufList = CacheHelper.manufMap.values();
			}
		}
		return manufList;
	}

	public boolean isActiveDisabled() {
		return activeDisabled;
	}

	public void setActiveDisabled(boolean activeDisabled) {
		this.activeDisabled = activeDisabled;
	}
}
