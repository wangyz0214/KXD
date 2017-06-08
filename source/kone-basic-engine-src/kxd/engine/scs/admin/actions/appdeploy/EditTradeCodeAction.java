package kxd.engine.scs.admin.actions.appdeploy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.naming.NamingException;
import javax.servlet.http.HttpServletResponse;

import kxd.engine.scs.admin.AdminSessionObject;
import kxd.engine.scs.admin.IntegerItem;
import kxd.engine.scs.admin.util.UserRight;
import kxd.engine.ui.core.EditAction;
import kxd.net.HttpRequest;
import kxd.net.connection.jndi.Lookuper;
import kxd.net.naming.LoopNamingContext;
import kxd.remote.scs.beans.BaseBusiness;
import kxd.remote.scs.beans.BasePayItem;
import kxd.remote.scs.beans.BasePayWay;
import kxd.remote.scs.beans.BaseTradeCode;
import kxd.remote.scs.beans.appdeploy.EditedTradeCode;
import kxd.remote.scs.interfaces.TradeCodeBeanRemote;
import kxd.util.StringUnit;

public class EditTradeCodeAction extends EditAction {
	private EditedTradeCode tradeCode;
	boolean added;
	private String refundMode;
	private String cancelRefundMode;
	private List<IntegerItem> refundModeList;
	private List<IntegerItem> cancelRefundModeList;

	@Override
	protected String doExecute(boolean isFormSubmit, HttpRequest request,
			HttpServletResponse response) throws Throwable {
		if (isFormSubmit) {
			addOrEditTradeCode();
			return "success";
		} else
			return null;
	}

	@Override
	protected void setParameters(boolean isFormSubmit, HttpRequest request)
			throws Throwable {
		super.setParameters(isFormSubmit, request);
		if (isFormSubmit) {
			tradeCode = getTradeCode();
			tradeCode.setId(request.getParameterIntDef("id", null));
			BaseBusiness business = new BaseBusiness(
					request.getParameterInt("businessid"),
					request.getParameter("businessid_desp"));
			tradeCode.setBusiness(business);
			Short pw = request.getParameterShortDef("payway", null);
			if (pw != null) {
				BasePayWay payWay = new BasePayWay(pw);
				payWay.setPayWayDesp(request.getParameter("payway_desp"));
				tradeCode.setPayWay(payWay);
			} else
				tradeCode.setPayWay(new BasePayWay());
			Short pi = request.getParameterShortDef("payitem", null);
			if (pi != null) {
				BasePayItem payItem = new BasePayItem(pi);
				payItem.setPayItemDesp(request.getParameter("payitem_desp"));
				tradeCode.setPayItem(payItem);
			} else
				tradeCode.setPayItem(new BasePayItem());
			Integer id = request.getParameterIntDef("strikeid", null);
			if (id != null) {
				BaseTradeCode o = new BaseTradeCode(id);
				o.setTradeCodeDesp(request.getParameter("strikeid_desp"));
				tradeCode.setStrikeTadeCode(o);
			} else
				tradeCode.setStrikeTadeCode(new BaseTradeCode());
			tradeCode.setLogged(request.getParameterBoolean("logged"));
			tradeCode.setStated(request.getParameterBoolean("stated"));
			tradeCode
					.setRedoEnabled(request.getParameterBoolean("redoenabled"));
			int[] v = StringUnit.splitToInt(
					request.getParameterDef("refundmode", null), ",");
			int iv = 0;
			if (v != null) {
				for (int i : v)
					iv |= i;
			}
			tradeCode.setRefundMode(iv);
			v = StringUnit.splitToInt(
					request.getParameterDef("cancelrefundmode", null), ",");
			iv = 0;
			if (v != null) {
				for (int i : v)
					iv |= i;
			}
			tradeCode.setCancelRefundMode(iv);
			tradeCode.setTradeService(request.getParameter("service"));
			tradeCode.setTradeCode(request.getParameter("code"));
			tradeCode.setTradeCodeDesp(request.getParameter("desp"));
		} else {
			getTradeCode(request.getParameterIntDef("id", null));
			refundMode = "";
			cancelRefundMode = "";
			if (tradeCode != null) {
				if ((tradeCode.getRefundMode() & 1) == 1)
					refundMode += "1,";
				if ((tradeCode.getRefundMode() & 2) == 2)
					refundMode += "2,";
				if ((tradeCode.getRefundMode() & 4) == 4)
					refundMode += "4,";
				if ((tradeCode.getCancelRefundMode() & 1) == 1)
					cancelRefundMode += "1,";
				if ((tradeCode.getCancelRefundMode() & 2) == 2)
					cancelRefundMode += "2,";
				if ((tradeCode.getCancelRefundMode() & 4) == 4)
					cancelRefundMode += "4,";
			}
			refundModeList = new ArrayList<IntegerItem>();
			cancelRefundModeList = new ArrayList<IntegerItem>();
			refundModeList.add(new IntegerItem(1, "允许当日退款"));
			refundModeList.add(new IntegerItem(2, "允许隔日退款"));
			refundModeList.add(new IntegerItem(4, "允许隔月退款"));
			cancelRefundModeList.add(new IntegerItem(1, "允许当日取消退款"));
			cancelRefundModeList.add(new IntegerItem(2, "允许隔日取消退款"));
			cancelRefundModeList.add(new IntegerItem(4, "允许隔月取消退款"));
		}
	}

	private void getTradeCode(Integer id) throws Throwable {
		if (id == null)
			return;
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			TradeCodeBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-tradeCodeBean", TradeCodeBeanRemote.class);
			tradeCode = bean.find(id);
		} finally {
			context.close();
		}
	}

	private void addOrEditTradeCode() throws Throwable {
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			TradeCodeBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-tradeCodeBean", TradeCodeBeanRemote.class);
			if (getTradeCode().getId() == null) {
				tradeCode.setId(bean.add(((AdminSessionObject) session)
						.getLoginUser().getUserId(), tradeCode));
				added = true;
			} else {
				bean.edit(((AdminSessionObject) session).getLoginUser()
						.getUserId(), tradeCode);
				added = false;
			}
		} finally {
			context.close();
		}
	}

	public Collection<BasePayWay> getPayWayList() throws NamingException {
		List<BasePayWay> ret = ((AdminSessionObject) session).getPayWayList();
		ret.add(0, new BasePayWay(null, "无"));
		return ret;
	}

	public Collection<BasePayItem> getPayItemList() throws NamingException {
		List<BasePayItem> ret = ((AdminSessionObject) session).getPayItemList();
		ret.add(0, new BasePayItem(null, "无"));
		return ret;
	}

	public Collection<BaseTradeCode> getTradeCodeList() throws NamingException {
		if (((AdminSessionObject) session).getLoginUser().isLogined()
				&& ((AdminSessionObject) session).getLoginUser().getUserGroup()
						.isSystemManager()) {
			LoopNamingContext context = new LoopNamingContext("db");
			try {
				TradeCodeBeanRemote bean = context.lookup(
						Lookuper.JNDI_TYPE_EJB, "kxd-ejb-tradeCodeBean",
						TradeCodeBeanRemote.class);
				List<BaseTradeCode> ret = bean.getTradeCodeList(
						((AdminSessionObject) session).getLoginUser()
								.getUserId(), null);
				if (getTradeCode().getId() != null) {
					Integer id = getTradeCode().getId();
					Iterator<BaseTradeCode> it = ret.iterator();
					while (it.hasNext()) {
						BaseTradeCode o = it.next();
						if (o.getId().equals(id)) {
							ret.remove(o);
							break;
						}
					}
				}
				ret.add(0, new BaseTradeCode(null, "无"));
				return ret;
			} finally {
				context.close();
			}
		} else
			return null;
	}

	public String getSuccessScript() {
		String script = "var items={id:'" + tradeCode.getIdString()
				+ "',desp:'" + tradeCode.getTradeCodeDesp() + "',columns:[";
		script += "'" + tradeCode.getIdString() + "',";
		script += "'" + tradeCode.getTradeCodeDesp() + "',";
		script += "'" + tradeCode.getTradeService() + "',";
		script += "'" + tradeCode.getTradeCode() + "',";
		script += "'" + (tradeCode.isStated() ? "是" : "否") + "',";
		script += "'" + (tradeCode.isLogged() ? "是" : "否") + "',";
		if (tradeCode.getPayWay() != null
				&& tradeCode.getPayWay().getPayWayDesp() != null)
			script += "'" + tradeCode.getPayWay().getPayWayDesp() + "',";
		else
			script += "'',";
		if (tradeCode.getPayItem() != null
				&& tradeCode.getPayItem().getPayItemDesp() != null)
			script += "'" + tradeCode.getPayItem().getPayItemDesp() + "'";
		else
			script += "''";
		script += "]};";
		if (added)
			script += "top.ajax_table.addsuccess(items);";
		else
			script += "top.ajax_table.editsuccess(items);";
		return script;
	}

	public EditedTradeCode getTradeCode() {
		if (tradeCode == null) {
			tradeCode = new EditedTradeCode();
		}
		return tradeCode;
	}

	public void setTradeCode(EditedTradeCode tradeCode) {
		this.tradeCode = tradeCode;
	}

	@Override
	public int getEditRight() {
		return UserRight.TRADECODE_EDIT;
	}

	@Override
	public int getQueryRight() {
		return UserRight.TRADECODE;
	}

	public String getRefundMode() {
		return refundMode;
	}

	public void setRefundMode(String refundMode) {
		this.refundMode = refundMode;
	}

	public String getCancelRefundMode() {
		return cancelRefundMode;
	}

	public void setCancelRefundMode(String cancelRefundMode) {
		this.cancelRefundMode = cancelRefundMode;
	}

	public boolean isAdded() {
		return added;
	}

	public List<IntegerItem> getRefundModeList() {
		return refundModeList;
	}

	public List<IntegerItem> getCancelRefundModeList() {
		return cancelRefundModeList;
	}
}
