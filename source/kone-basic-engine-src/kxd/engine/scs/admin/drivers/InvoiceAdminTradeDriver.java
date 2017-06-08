package kxd.engine.scs.admin.drivers;

import java.util.Iterator;
import java.util.Map;

import javax.naming.NamingException;
import javax.servlet.http.HttpServletResponse;

import kxd.engine.scs.admin.AdminSessionObject;
import kxd.engine.scs.admin.AdminTradeDriver;
import kxd.engine.scs.admin.util.UserRight;
import kxd.net.HttpRequest;
import kxd.net.connection.jndi.Lookuper;
import kxd.net.naming.LoopNamingContext;
import kxd.remote.scs.beans.invoice.EditedInvoiceConfig;
import kxd.remote.scs.beans.invoice.EditedInvoiceTemplate;
import kxd.remote.scs.interfaces.invoice.InvoiceConfigBeanRemote;
import kxd.remote.scs.interfaces.invoice.InvoiceTemplateBeanRemote;
import kxd.remote.scs.util.QueryResult;
import kxd.util.KoneException;
import kxd.util.StringUnit;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class InvoiceAdminTradeDriver implements AdminTradeDriver {

	@Override
	public void execute(AdminSessionObject session, HttpRequest request,
			HttpServletResponse response, Document xmlDoc, Element content,
			Element result) throws Throwable {
		String cmd = request.getParameter("cmd");
		String cmdgroup = request.getParameterDef("cmdgroup", "");
		if (cmdgroup.equals("query")) {
			executeQuery(session, cmd, request, response, xmlDoc, content,
					result);
		} else if (cmdgroup.equals("delete")) {
			executeDelete(session, cmd, request, response, xmlDoc, content,
					result);
		} else if (cmdgroup.equals("other")) {
			executeOther(session, cmd, request, response, xmlDoc, content,
					result);
		}
	}

	public Document executeQuery(AdminSessionObject session, String cmd,
			HttpRequest request, HttpServletResponse response, Document xmlDoc,
			Element content, Element result) throws Throwable {
		boolean firstQuery = request.getParameterBooleanDef("firstquery", true);
		int maxresults = request.getParameterIntDef("maxresults", 12);
		String keyword = request.getParameterDef("keyword", "").trim();
		if (keyword.length() == 0) {
			keyword = null;
		}
		int page = request.getParameterIntDef("page", 0);
		int firstResult = page * maxresults;
		if (cmd.equals("queryinvoicetemplatelist")) { // 查询发票模板信息
			queryInvoiceTemplateList(firstQuery, keyword, maxresults,
					firstResult, session, request, xmlDoc, content);
		} else if (cmd.equals("queryinvoiceconfiglist")) { // 查询发票模板信息
			queryInvoiceConfigList(firstQuery, keyword, maxresults,
					firstResult, session, request, xmlDoc, content);
		} else {
			throw new KoneException("无效的交易代码");
		}
		return null;
	}

	public Document executeDelete(AdminSessionObject session, String cmd,
			HttpRequest request, HttpServletResponse response, Document xmlDoc,
			Element content, Element result) throws Throwable {
		String id = request.getParameter("id");
		if (cmd.equals("deleteinvoicetemplate")) { // 删除发票模板信息
			deleteInvoiceTemplate(session, id);
		} else if (cmd.equals("deleteinvoiceconfig")) { // 删除发票模板信息
			deleteInvoiceConfig(session, id);
		} else
			throw new KoneException("无效的交易代码");
		return null;
	}

	public Document executeOther(AdminSessionObject session, String cmd,
			HttpRequest request, HttpServletResponse response, Document xmlDoc,
			Element content, Element result) throws Throwable {
		// String id = request.getParameter("id");
		if (cmd.equals("numbersonline")) {
			// numbersOnline(session, id, 1);
		} else
			throw new KoneException("无效的交易代码");
		return null;
	}

	/**
	 * 查询发票模板信息
	 * 
	 * @param firstQuery
	 * @param keyword
	 * @param maxresults
	 * @param firstResult
	 * @param session
	 * @param request
	 * @param xmlDoc
	 * @param content
	 * @throws NamingException
	 * @throws NoSuchFieldException
	 */
	private void queryInvoiceTemplateList(boolean firstQuery, String keyword,
			int maxresults, int firstResult, AdminSessionObject session,
			HttpRequest request, Document xmlDoc, Element content)
			throws NamingException, NoSuchFieldException {
		if (!((AdminSessionObject) session)
				.hasRight(UserRight.INVOICE_TEMPLATE))
			throw new KoneException("您没有查询发票模板的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			InvoiceTemplateBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-invoiceTemplateBean",
					InvoiceTemplateBeanRemote.class);
			QueryResult<EditedInvoiceTemplate> r = bean.queryInvoiceTemplate(
					firstQuery, ((AdminSessionObject) session).getLoginUser()
							.getUserId(), keyword, firstResult, maxresults);
			appendCountElement(firstQuery, content, xmlDoc, r.getCount());
			Iterator<EditedInvoiceTemplate> it = r.getResultList().iterator();
			while (it.hasNext()) {
				Element el = xmlDoc.createElement("record");
				EditedInvoiceTemplate o = it.next();
				el.setAttribute("id", o.getIdString());
				el.setAttribute("desp", o.getTemplateDesp());
				el.setAttribute("code", o.getTemplateCode());
				// el.setAttribute("templateContent", "<![CDATA[" +
				// o.getTemplateContent() + "]]>");
				content.appendChild(el);
			}
		} finally {
			if (context != null) {
				context.close();
			}
		}
	}

	private void deleteInvoiceTemplate(AdminSessionObject session, String id)
			throws NamingException {
		if (!((AdminSessionObject) session)
				.hasRight(UserRight.INVOICE_TEMPLATE_EDIT))
			throw new KoneException("您没有删除发票模板的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			InvoiceTemplateBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-invoiceTemplateBean",
					InvoiceTemplateBeanRemote.class);
			bean.delete(((AdminSessionObject) session).getLoginUser()
					.getUserId(), StringUnit.splitToInt2(id, ","));
		} finally {
			if (context != null) {
				context.close();
			}
		}
	}

	/**
	 * 查询发票模板信息
	 * 
	 * @param firstQuery
	 * @param keyword
	 * @param maxresults
	 * @param firstResult
	 * @param session
	 * @param request
	 * @param xmlDoc
	 * @param content
	 * @throws NamingException
	 * @throws NoSuchFieldException
	 */
	private void queryInvoiceConfigList(boolean firstQuery, String keyword,
			int maxresults, int firstResult, AdminSessionObject session,
			HttpRequest request, Document xmlDoc, Element content)
			throws NamingException, NoSuchFieldException {
		if (!((AdminSessionObject) session).hasRight(UserRight.INVOICE_CONFIG))
			throw new KoneException("您没有查询发票配置的权限.");

		Integer orgId = request.getParameterIntDef("orgid_value", null);
		if (orgId == null) {
			if (firstQuery) {
				Element el = xmlDoc.createElement("count");
				el.setAttribute("value", "0");
				content.appendChild(el);
			}
			return;
		}

		LoopNamingContext context = new LoopNamingContext("db");
		try {
			InvoiceConfigBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-invoiceConfigBean",
					InvoiceConfigBeanRemote.class);
			QueryResult<EditedInvoiceConfig> r = bean.queryInvoiceConfig(
					firstQuery, ((AdminSessionObject) session).getLoginUser()
							.getUserId(), orgId, 0, keyword, firstResult,
					maxresults);
			appendCountElement(firstQuery, content, xmlDoc, r.getCount());
			Iterator<EditedInvoiceConfig> it = r.getResultList().iterator();
			Map<String, String> typeMap = AdminSessionObject
					.getParamConfig(100);
			Map<String, String> taxMap = AdminSessionObject.getParamConfig(101);
			Map<String, String> awayMap = AdminSessionObject
					.getParamConfig(102);
			while (it.hasNext()) {
				Element el = xmlDoc.createElement("record");
				EditedInvoiceConfig o = it.next();
				el.setAttribute("id", o.getIdString());
				el.setAttribute("desp", o.getConfigDesp());
				el.setAttribute("configcode", o.getConfigCode());
				el.setAttribute("orgid", o.getOrg().getOrgId() + "");
				el.setAttribute("orgname", o.getOrg().getOrgName());
				el.setAttribute("invoicetype",
						typeMap.get(Integer.toString(o.getInvoiceType())));
				el.setAttribute("templateid", o.getInvoiceTemplate()
						.getIdString());
				el.setAttribute("templatedesp", o.getInvoiceTemplate()
						.getTemplateDesp() + "");
				el.setAttribute("taxflag",
						taxMap.get(Integer.toString(o.getTaxFlag())));
				el.setAttribute("awayflag",
						awayMap.get(Integer.toString(o.getAwayFlag())));
				el.setAttribute("logged", o.isLogged() + "");
				el.setAttribute("alertcount", o.getAlertCount() + "");
				el.setAttribute("extdata0", o.getExtdata0());
				el.setAttribute("extdata1", o.getExtdata1());
				content.appendChild(el);
			}
		} finally {
			if (context != null) {
				context.close();
			}
		}
	}

	private void deleteInvoiceConfig(AdminSessionObject session, String id)
			throws NamingException {
		if (!((AdminSessionObject) session)
				.hasRight(UserRight.INVOICE_CONFIG_EDIT))
			throw new KoneException("您没有删除发票模板的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			InvoiceConfigBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-invoiceConfigBean",
					InvoiceConfigBeanRemote.class);
			bean.delete(((AdminSessionObject) session).getLoginUser()
					.getUserId(), StringUnit.splitToInt2(id, ","));
		} finally {
			if (context != null) {
				context.close();
			}
		}
	}

	private void appendCountElement(boolean firstQuery, Element content,
			Document document, int count) {
		if (firstQuery) {
			Element el = document.createElement("count");
			el.setAttribute("value", Integer.toString(count));
			content.appendChild(el);
		}
	}
}
