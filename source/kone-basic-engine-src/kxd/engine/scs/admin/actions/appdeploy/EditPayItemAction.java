package kxd.engine.scs.admin.actions.appdeploy;

import javax.servlet.http.HttpServletResponse;

import kxd.engine.scs.admin.AdminSessionObject;
import kxd.engine.scs.admin.util.UserRight;
import kxd.engine.ui.core.EditAction;
import kxd.net.HttpRequest;
import kxd.net.connection.jndi.Lookuper;
import kxd.net.naming.LoopNamingContext;
import kxd.remote.scs.beans.appdeploy.EditedPayItem;
import kxd.remote.scs.interfaces.PayItemBeanRemote;

public class EditPayItemAction extends EditAction {
	private EditedPayItem payItem;
	boolean added;

	@Override
	protected String doExecute(boolean isFormSubmit, HttpRequest request,
			HttpServletResponse response) throws Throwable {
		if (isFormSubmit) {
			addOrEditPayItem();
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
			payItem = getPayItem();
			payItem.setId(request.getParameterShort("id"));
			payItem.setPayItemDesp(request.getParameter("desp"));
			payItem.setPrice(request.getParameterLongDef("price", (long) 0));
			payItem.setMemo(request.getParameterDef("memo", null));
			payItem.setNeedTrade(request.getParameterBoolean("needtrade"));
		} else {
			getPayItem(request.getParameterIntDef("id", null));
		}
	}

	private void getPayItem(Integer id) throws Throwable {
		if (id == null)
			return;
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			PayItemBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-payItemBean", PayItemBeanRemote.class);
			payItem = bean.find(id);
		} finally {
			context.close();
		}
	}

	private void addOrEditPayItem() throws Throwable {
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			PayItemBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-payItemBean", PayItemBeanRemote.class);
			if (added) {
				payItem.setId(bean.add(((AdminSessionObject) session)
						.getLoginUser().getUserId(), payItem));
			} else {
				bean.edit(((AdminSessionObject) session).getLoginUser()
						.getUserId(), payItem);
			}
		} finally {
			context.close();
		}
	}

	public String getSuccessScript() {
		String script = "var items={id:'" + payItem.getIdString() + "',desp:'"
				+ payItem.getPayItemDesp() + "',columns:[";
		script += "'" + payItem.getIdString() + "',";
		script += "'" + payItem.getPayItemDesp() + "',";
		script += "'" + payItem.getPrice() + "',";
		script += "'" + payItem.getMemo() + "'";
		script += "]};";
		if (added)
			script += "top.ajax_table.addsuccess(items);";
		else
			script += "top.ajax_table.editsuccess(items);";
		return script;
	}

	public EditedPayItem getPayItem() {
		if (payItem == null) {
			payItem = new EditedPayItem();
		}
		return payItem;
	}

	public void setPayItem(EditedPayItem payItem) {
		this.payItem = payItem;
	}

	@Override
	public int getEditRight() {
		return UserRight.PAYITEM_EDIT;
	}

	@Override
	public int getQueryRight() {
		return UserRight.PAYITEM;
	}
}
