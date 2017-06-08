package kxd.engine.scs.admin.actions.invoice;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import kxd.engine.scs.admin.AdminSessionObject;
import kxd.engine.scs.admin.util.UserRight;
import kxd.engine.ui.core.EditAction;
import kxd.net.HttpRequest;
import kxd.net.connection.jndi.Lookuper;
import kxd.net.naming.LoopNamingContext;
import kxd.remote.scs.beans.BaseOrg;
import kxd.remote.scs.beans.invoice.BaseInvoiceTemplate;
import kxd.remote.scs.beans.invoice.EditedInvoiceConfig;
import kxd.remote.scs.beans.invoice.EditedInvoiceTemplate;
import kxd.remote.scs.interfaces.invoice.InvoiceConfigBeanRemote;
import kxd.remote.scs.interfaces.invoice.InvoiceTemplateBeanRemote;

public class EditInvoiceConfigAction extends EditAction {
	private EditedInvoiceConfig invoiceConfig;
	boolean added;
	private List<BaseInvoiceTemplate> templates;

	@Override
	protected String doExecute(boolean isFormSubmit, HttpRequest request,
			HttpServletResponse response) throws Throwable {
		if (isFormSubmit) {
			addOrEditInvoiceConfig();
			return "success";
		} else
			return null;
	}

	@Override
	protected void setParameters(boolean isFormSubmit, HttpRequest request)
			throws Throwable {
		super.setParameters(isFormSubmit, request);
		invoiceConfig = getInvoiceConfig();
		invoiceConfig.setId(request.getParameterIntDef("id", null));
		BaseOrg b = new BaseOrg(request.getParameterIntDef("orgid", null));
		b.setOrgName(request.getParameterDef("orgid_desp", null));
		invoiceConfig.setOrg(b);

		if (isFormSubmit) {
			invoiceConfig.setConfigDesp(request.getParameter("desp"));
			invoiceConfig.setConfigCode(request.getParameter("code"));
			invoiceConfig
					.setInvoiceType(request.getParameterInt("invoicetype"));
			invoiceConfig.setInvoiceTemplate(new EditedInvoiceTemplate(request
					.getParameterInt("templateid")));
			invoiceConfig.setTaxFlag(request.getParameterInt("taxflag"));
			invoiceConfig.setAwayFlag(request.getParameterInt("awayflag"));
			invoiceConfig.setAlertCount(request.getParameterInt("alertcount"));
			invoiceConfig.setLogged(request.getParameterBoolean("logged"));
			invoiceConfig.setExtdata0(request.getParameter("extdata0"));
			invoiceConfig.setExtdata1(request.getParameter("extdata1"));
		} else {
			getInvoiceConfig(request.getParameterIntDef("id", null));
			getTemplateList();
		}
	}

	private void getInvoiceConfig(Integer id) throws Throwable {
		if (id == null)
			return;
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			InvoiceConfigBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-invoiceConfigBean",
					InvoiceConfigBeanRemote.class);
			invoiceConfig = bean.find(id);
		} finally {
			context.close();
		}
	}

	private void getTemplateList() throws Throwable {
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			InvoiceTemplateBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-invoiceTemplateBean",
					InvoiceTemplateBeanRemote.class);
			templates = bean.getInvoiceTemplateList(
					((AdminSessionObject) session).getLoginUser().getUserId(),
					null);
		} finally {
			context.close();
		}
	}

	private void addOrEditInvoiceConfig() throws Throwable {
		added = getInvoiceConfig().getId() == null;
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			InvoiceConfigBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-invoiceConfigBean",
					InvoiceConfigBeanRemote.class);
			try {
				if (added) {
					invoiceConfig.setId(bean.add(((AdminSessionObject) session)
							.getLoginUser().getUserId(), invoiceConfig));
				} else {
					bean.edit(((AdminSessionObject) session).getLoginUser()
							.getUserId(), invoiceConfig);
				}
			} catch (Throwable e) {
				getTemplateList();
				throw e;
			}
		} finally {
			context.close();
		}
	}

	protected String getTemplateDesp(Integer templateid) throws Throwable {

		LoopNamingContext context = new LoopNamingContext("db");
		try {
			InvoiceTemplateBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-invoiceTemplateBean",
					InvoiceTemplateBeanRemote.class);
			EditedInvoiceTemplate it = bean.find(templateid);
			return it.getTemplateDesp();
		} finally {
			context.close();
		}
	}

	public String getSuccessScript() {
		String invoicetype = invoiceConfig.getInvoiceType() + "";
		invoicetype = invoicetype == null ? "" : AdminSessionObject
				.getParamConfig(100).get(
						Integer.toString(invoiceConfig.getInvoiceType()));

		String taxflag = invoiceConfig.getTaxFlag() + "";
		taxflag = taxflag == null ? "" : AdminSessionObject.getParamConfig(101)
				.get(Integer.toString(invoiceConfig.getTaxFlag()));

		String awayflag = invoiceConfig.getAwayFlag() + "";
		awayflag = awayflag == null ? "" : AdminSessionObject.getParamConfig(
				102).get(Integer.toString(invoiceConfig.getAwayFlag()));

		String templateDesp = null;
		try {
			// templateDesp =
			// getTemplateDesp(invoiceConfig.getInvoiceTemplate().getId());
			// templateDesp = InvoiceHelper.getInvoiceTemplate(
			// invoiceConfig.getInvoiceTemplate().getId())
			// .getTemplateDesp();
		} catch (Throwable e) {
		}
		;

		// CachedInvoiceConfig testCached =
		// InvoiceHelper.getInvoiceConfig(invoiceConfig.getOrg().getOrgId(),
		// invoiceConfig.getInvoiceType());
		// String tt = testCached.getConfigCode() +
		// testCached.getInvoiceTemplate().getTemplateContent();
		/*
		 * String keys=""; try { InvoiceHelper.getAllInvoiceConfig();
		 * Set<String> skey = InvoiceHelper.getInvoiceConfigMap().keySet(); for
		 * (Iterator<String> iter = skey.iterator(); iter.hasNext();) { String
		 * element = iter.next(); keys += element + ",";
		 * 
		 * } System.out.println(keys); } catch (Exception e) {
		 * e.printStackTrace(); }
		 */

		String script = "var items={id:'" + invoiceConfig.getIdString()
				+ "',desp:'" + invoiceConfig.getConfigDesp() + "',columns:[";
		script += "'" + invoiceConfig.getIdString() + "',";
		// script += "'" + invoiceConfig.getConfigCode()+ "',";
		script += "'" + invoiceConfig.getConfigDesp() + "',";
		script += "'" + invoicetype + "',";
		script += "'" + templateDesp + "',";
		script += "'" + taxflag + "',";
		script += "'" + awayflag + "',";
		script += "'" + invoiceConfig.getOrg().getOrgName() + "'";
		// script += "'" + invoiceConfig.getAlertCount() + "',";
		// script += "'" + invoiceConfig.isLogged()?"是":"否" + "',";
		// script += "'" + invoiceConfig.getExtdata0() + "',";
		// script += "'" + invoiceConfig.getExtdata1() + "'";
		script += "]};";
		if (added)
			script += "top.ajax_table.addsuccess(items);";
		else
			script += "top.ajax_table.editsuccess(items);";
		return script;
	}

	public EditedInvoiceConfig getInvoiceConfig() {
		if (invoiceConfig == null) {
			invoiceConfig = new EditedInvoiceConfig();
		}
		return invoiceConfig;
	}

	public void setInvoiceConfig(EditedInvoiceConfig invoiceConfig) {
		this.invoiceConfig = invoiceConfig;
	}

	@Override
	public int getEditRight() {
		return UserRight.INVOICE_CONFIG_EDIT;
	}

	@Override
	public int getQueryRight() {
		return UserRight.INVOICE_CONFIG;
	}

	public List<BaseInvoiceTemplate> getTemplates() {
		return templates;
	}

}
