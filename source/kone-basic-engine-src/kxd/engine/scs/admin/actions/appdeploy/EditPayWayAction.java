package kxd.engine.scs.admin.actions.appdeploy;

import javax.servlet.http.HttpServletResponse;

import kxd.engine.scs.admin.AdminSessionObject;
import kxd.engine.scs.admin.util.UserRight;
import kxd.engine.ui.core.EditAction;
import kxd.net.HttpRequest;
import kxd.net.connection.jndi.Lookuper;
import kxd.net.naming.LoopNamingContext;
import kxd.remote.scs.beans.BasePayWay;
import kxd.remote.scs.interfaces.PayWayBeanRemote;
import kxd.remote.scs.util.emun.PayWayType;

public class EditPayWayAction extends EditAction {
	private BasePayWay payWay;
	boolean added;

	@Override
	protected String doExecute(boolean isFormSubmit, HttpRequest request,
			HttpServletResponse response) throws Throwable {
		if (isFormSubmit) {
			addOrEditPayWay();
			return "success";
		} else
			return null;
	}

	@Override
	protected void setParameters(boolean isFormSubmit, HttpRequest request)
			throws Throwable {
		super.setParameters(isFormSubmit, request);
		if (isFormSubmit) {
			added = request.getParameterBoolean("add");
			payWay = getPayWay();
			payWay.setId(request.getParameterShort("id"));
			payWay.setPayWayDesp(request.getParameter("desp"));
			payWay.setNeedTrade(request.getParameterBoolean("needtrade"));
			payWay.setType(PayWayType.valueOf(request.getParameterInt("type")));
		} else {
			getPayWay(request.getParameterIntDef("id", null));
		}
	}

	private void getPayWay(Integer id) throws Throwable {
		if (id == null)
			return;
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			PayWayBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-payWayBean", PayWayBeanRemote.class);
			payWay = bean.find(id);
		} finally {
			context.close();
		}
	}

	private void addOrEditPayWay() throws Throwable {
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			PayWayBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-payWayBean", PayWayBeanRemote.class);
			if (added) {
				payWay.setId(bean.add(((AdminSessionObject) session)
						.getLoginUser().getUserId(), payWay));
			} else {
				bean.edit(((AdminSessionObject) session).getLoginUser()
						.getUserId(), payWay);
			}
		} finally {
			context.close();
		}
	}

	public String getSuccessScript() {
		String script = "var items={id:'" + payWay.getIdString() + "',desp:'"
				+ payWay.getPayWayDesp() + "',columns:[";
		script += "'" + payWay.getIdString() + "'";
		script += ",'" + payWay.getPayWayDesp() + "'";
		script += ",'" + payWay.getType().toString() + "'";
		script += ",'" + (payWay.isNeedTrade() ? "是" : "否") + "'";
		script += "]};";
		if (added)
			script += "top.ajax_table.addsuccess(items);";
		else
			script += "top.ajax_table.editsuccess(items);";
		return script;
	}

	public BasePayWay getPayWay() {
		if (payWay == null) {
			payWay = new BasePayWay();
		}
		return payWay;
	}

	public void setPayWay(BasePayWay payWay) {
		this.payWay = payWay;
	}

	@Override
	public int getEditRight() {
		return UserRight.PAYWAY_EDIT;
	}

	@Override
	public int getQueryRight() {
		return UserRight.PAYWAY;
	}
}
