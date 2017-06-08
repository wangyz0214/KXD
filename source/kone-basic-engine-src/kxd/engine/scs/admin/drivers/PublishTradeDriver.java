package kxd.engine.scs.admin.drivers;

import java.util.Iterator;

import javax.naming.NamingException;
import javax.servlet.http.HttpServletResponse;

import kxd.engine.fileservice.FileProcessor;
import kxd.engine.scs.admin.AdminSessionObject;
import kxd.engine.scs.admin.AdminTradeDriver;
import kxd.engine.scs.admin.AdminTradeExecutor;
import kxd.engine.scs.admin.util.UserRight;
import kxd.net.HttpRequest;
import kxd.net.connection.jndi.Lookuper;
import kxd.net.naming.LoopNamingContext;
import kxd.remote.scs.beans.BaseAdCategory;
import kxd.remote.scs.beans.BaseInfo;
import kxd.remote.scs.beans.BaseInfoCategory;
import kxd.remote.scs.beans.BaseOrgAd;
import kxd.remote.scs.beans.BasePrintAd;
import kxd.remote.scs.beans.BasePrintAdCategory;
import kxd.remote.scs.beans.BaseTermAd;
import kxd.remote.scs.beans.adinfo.EditedInfo;
import kxd.remote.scs.interfaces.AdCategoryBeanRemote;
import kxd.remote.scs.interfaces.InfoBeanRemote;
import kxd.remote.scs.interfaces.InfoCategoryBeanRemote;
import kxd.remote.scs.interfaces.OrgAdBeanRemote;
import kxd.remote.scs.interfaces.PrintAdBeanRemote;
import kxd.remote.scs.interfaces.PrintAdCategoryBeanRemote;
import kxd.remote.scs.interfaces.TermAdBeanRemote;
import kxd.remote.scs.interfaces.service.SystemServiceBeanRemote;
import kxd.remote.scs.util.QueryResult;
import kxd.remote.scs.util.emun.AuditStatus;
import kxd.util.DataUnit;
import kxd.util.DateTime;
import kxd.util.KoneException;
import kxd.util.StringUnit;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * 信息发布类驱动
 * 
 * @author zhaom
 * 
 */
public class PublishTradeDriver implements AdminTradeDriver {

	protected void appendCountElement(boolean firstQuery, Element content,
			Document document, int count) {
		if (firstQuery) {
			Element el = document.createElement("count");
			el.setAttribute("value", Integer.toString(count));
			content.appendChild(el);
		}
	}

	protected void appendAuditStatus(boolean continueUpload,
			AuditStatus status, Element el) {
		StringBuffer buf = new StringBuffer();
		switch (status) {
		case OFFLINE_REQ:
			buf.append("<a href=\"#\" onclick=\"preview(arguments[0]);return false;\">预览</a>");
			buf.append(" <a href=\"#\" onclick=\"cancelOfflineReq(arguments[0]);return false;\">撤消下线申请</a>");
			break;
		case ONLINE:
			buf.append("<a href=\"#\" onclick=\"preview(arguments[0]);return false;\">预览</a>");
			buf.append(" <a href=\"#\" onclick=\"offlineReq(arguments[0]);return false;\">申请下线</a>");
			break;
		case ONLINE_REQ:
			buf.append("<a href=\"#\" onclick=\"preview(arguments[0]);return false;\">预览</a>");
			buf.append(" <a href=\"#\" onclick=\"cancelOnlineReq(arguments[0]);return false;\">撤消上线申请</a>");
			break;
		default:
			if (!continueUpload)
				buf.append("<a href=\"#\" onclick=\"ajax_table.showEditDialog(arguments[0]);return false;\">编辑</a>");
			else
				buf.append("<a href=\"#\" onclick=\"showContinueDialog(arguments[0]);return false;\">续传</a>");
			buf.append(" <a href=\"#\" onclick=\"ajax_table.deleteSelected(arguments[0]);return false;\">删除</a>");
			if (!continueUpload)
				buf.append(" <a href=\"#\" onclick=\"onlineReq(arguments[0]);return false;\">申请上线</a>");
		}
		el.setAttribute("option", buf.toString());
	}

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
		} else {
			executeOther(session, cmd, request, response, xmlDoc, content,
					result);
		}
	}

	public Document executeQuery(AdminSessionObject session, String cmd,
			HttpRequest request, HttpServletResponse response, Document xmlDoc,
			Element content, Element result) throws Throwable {
		boolean firstQuery = request.getParameterBooleanDef("firstquery", true);
		String keyword = request.getParameterDef("keyword", "").trim();
		if (keyword.length() == 0)
			keyword = null;
		int maxresults = request.getParameterIntDef("maxresults", 12);
		int page = request.getParameterIntDef("page", 0);
		if (cmd.equals("queryprintadcategorylist")) {
			queryPrintAdCategoryList(firstQuery, keyword, maxresults, page,
					session, request, xmlDoc, content);
		} else if (cmd.equals("queryprintadlist")) {
			queryPrintAdList(firstQuery, keyword, maxresults, page, session,
					request, xmlDoc, content);
		} else if (cmd.equals("queryadcategorylist")) {
			queryAdCategoryList(firstQuery, keyword, maxresults, page, session,
					request, xmlDoc, content);
		} else if (cmd.equals("queryorgadlist")) {
			queryOrgAdList(firstQuery, keyword, maxresults, page, session,
					request, xmlDoc, content);
		} else if (cmd.equals("querytermadlist")) {
			getTermAdList(session, request.getParameter("id"),
					request.getParameterIntDef("adCategoryId", null), keyword,
					xmlDoc, content);
		} else if (cmd.equals("queryinfocategorylist")) {
			queryInfoCategoryList(firstQuery, keyword, maxresults, page,
					session, request, xmlDoc, content);
		} else if (cmd.equals("queryinfolist")) {
			queryInfoList(firstQuery, keyword, maxresults, page, session,
					request, xmlDoc, content);
		} else if (cmd.equals("queryneedauditadinfolist")) {
			queryNeedAuditAdInfoList(firstQuery, keyword, maxresults, page,
					session, request, xmlDoc, content);
		} else
			throw new KoneException("无效的交易代码");
		return null;
	}

	public Document executeDelete(AdminSessionObject session, String cmd,
			HttpRequest request, HttpServletResponse response, Document xmlDoc,
			Element content, Element result) throws Throwable {
		String id = request.getParameter("id");
		if (cmd.equals("deleteprintad")) {
			deletePrintAdList(session, id);
		} else if (cmd.equals("deleteorgad")) {
			deleteOrgAdList(session, id);
		} else if (cmd.equals("deleteinfo")) {
			deleteInfoList(session, id);
		} else if (cmd.equals("deleteprintadcategory")) {
			deletePrintAdCategory(session, id);
		} else if (cmd.equals("deleteadcategory")) {
			deleteAdCategory(session, id);
		} else if (cmd.equals("deleteinfocategory")) {
			deleteInfoCategory(session, id);
		} else if (cmd.equals("deletetermadlist")) {
			deleteTermAdList(session, id);
		} else
			throw new KoneException("无效的交易代码");
		return null;
	}

	public Document executeOther(AdminSessionObject session, String cmd,
			HttpRequest request, HttpServletResponse response, Document xmlDoc,
			Element content, Element result) throws Throwable {
		String id = request.getParameter("id");
		if (cmd.equals("printadonlinereq")) {
			printAdOnlineReq(session, request, id);
		} else if (cmd.equals("printadofflinereq")) {
			printAdOfflineReq(session, request, id);
		} else if (cmd.equals("printadofflineaudit")) {
			printAdOfflineAudit(session, request, id);
		} else if (cmd.equals("printadonlineaudit")) {
			printAdOnlineAudit(session, request, id);
		} else if (cmd.equals("orgadonlinereq")) {
			orgAdOnlineReq(session, request, id);
		} else if (cmd.equals("orgadofflinereq")) {
			orgAdOfflineReq(session, request, id);
		} else if (cmd.equals("orgadofflineaudit")) {
			orgAdOfflineAudit(session, request, id);
		} else if (cmd.equals("orgadonlineaudit")) {
			orgAdOnlineAudit(session, request, id);
		} else if (cmd.equals("termadonlinereq")) {
			termAdOnlineReq(session, request, id);
		} else if (cmd.equals("termadofflinereq")) {
			termAdOfflineReq(session, request, id);
		} else if (cmd.equals("termadofflineaudit")) {
			termAdOfflineAudit(session, request, id);
		} else if (cmd.equals("termadonlineaudit")) {
			termAdOnlineAudit(session, request, id);
		} else if (cmd.equals("infoonlinereq")) {
			infoOnlineReq(session, request, id);
		} else if (cmd.equals("infoofflinereq")) {
			infoOfflineReq(session, request, id);
		} else if (cmd.equals("infoofflineaudit")) {
			infoOfflineAudit(session, request, id);
		} else if (cmd.equals("infoonlineaudit")) {
			infoOnlineAudit(session, request, id);
		} else
			throw new KoneException("无效的交易代码");
		return null;
	}

	protected void queryPrintAdCategoryList(boolean firstQuery, String keyword,
			int maxresults, int page, AdminSessionObject session,
			HttpRequest request, Document xmlDoc, Element content)
			throws NamingException, NoSuchFieldException {
		if (!((AdminSessionObject) session).hasRight(UserRight.PRINTADCATEGORY))
			throw new KoneException("您没有查询打印广告分类的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			PrintAdCategoryBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-printAdCategoryBean",
					PrintAdCategoryBeanRemote.class);
			QueryResult<BasePrintAdCategory> r = bean.queryPrintAdCategory(
					firstQuery, ((AdminSessionObject) session).getLoginUser()
							.getUserId(), 0, keyword, page * maxresults,
					maxresults);
			appendCountElement(firstQuery, content, xmlDoc, r.getCount());
			Iterator<BasePrintAdCategory> it = r.getResultList().iterator();
			while (it.hasNext()) {
				Element el = xmlDoc.createElement("record");
				BasePrintAdCategory o = it.next();
				el.setAttribute("id", Long.toString(o.getId()));
				el.setAttribute("desp", o.getPrintAdCategoryDesp());
				content.appendChild(el);
			}
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void deletePrintAdCategory(AdminSessionObject session, String id)
			throws NamingException {
		if (!((AdminSessionObject) session)
				.hasRight(UserRight.PRINTADCATEGORY_EDIT))
			throw new KoneException("您无删除打印广告类型的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			PrintAdCategoryBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-printAdCategoryBean",
					PrintAdCategoryBeanRemote.class);
			bean.delete(((AdminSessionObject) session).getLoginUser()
					.getUserId(), StringUnit.splitToShort(id, ","));
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void queryPrintAdList(boolean firstQuery, String keyword,
			int maxresults, int page, AdminSessionObject session,
			HttpRequest request, Document xmlDoc, Element content)
			throws NamingException, NoSuchFieldException {
		if (!((AdminSessionObject) session).hasRight(UserRight.PRINTAD))
			throw new KoneException("您没有查询打印广告的权限.");
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
			PrintAdBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-printAdBean", PrintAdBeanRemote.class);
			QueryResult<BasePrintAd> r = bean.queryPrintAd(firstQuery, session
					.getLoginUser().getUserId(), orgId, 0, keyword, page
					* maxresults, maxresults);
			appendCountElement(firstQuery, content, xmlDoc, r.getCount());
			Iterator<BasePrintAd> it = r.getResultList().iterator();
			while (it.hasNext()) {
				Element el = xmlDoc.createElement("record");
				BasePrintAd o = (BasePrintAd) it.next();
				el.setAttribute("id", Long.toString(o.getId()));
				el.setAttribute("desp", o.getAdCategory()
						.getPrintAdCategoryDesp());
				el.setAttribute("orgdesp", o.getOrg().getOrgName());
				el.setAttribute("auditstatus", o.getAuditStatus().toString());
				appendAuditStatus(false, o.getAuditStatus(), el);
				content.appendChild(el);
			}
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void deletePrintAdList(AdminSessionObject session, String id)
			throws NamingException {
		if (!((AdminSessionObject) session).hasRight(UserRight.PRINTAD_EDIT))
			throw new KoneException("您没有删除打印广告的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			PrintAdBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-printAdBean", PrintAdBeanRemote.class);
			bean.delete(((AdminSessionObject) session).getLoginUser()
					.getUserId(), Integer.valueOf(id));
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void printAdOnlineReq(AdminSessionObject session,
			HttpRequest request, String id) throws NamingException,
			NoSuchFieldException {
		if (!((AdminSessionObject) session).hasRight(UserRight.PRINTAD_EDIT))
			throw new KoneException("您没有权限执行该操作.");
		boolean iscancel = request.getParameterBoolean("iscancel");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			PrintAdBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-printAdBean", PrintAdBeanRemote.class);
			bean.onlineReq(((AdminSessionObject) session).getLoginUser()
					.getUserId(), Integer.valueOf(id), iscancel);
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void printAdOfflineReq(AdminSessionObject session,
			HttpRequest request, String id) throws NamingException,
			NoSuchFieldException {
		if (!((AdminSessionObject) session).hasRight(UserRight.PRINTAD_EDIT))
			throw new KoneException("您没有权限执行该操作.");
		boolean iscancel = request.getParameterBoolean("iscancel");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			PrintAdBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-printAdBean", PrintAdBeanRemote.class);
			bean.offlineReq(((AdminSessionObject) session).getLoginUser()
					.getUserId(), Integer.valueOf(id), iscancel);
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void printAdOfflineAudit(AdminSessionObject session,
			HttpRequest request, String id) throws NamingException,
			NoSuchFieldException {
		if (!((AdminSessionObject) session).hasRight(UserRight.PRINTAD_EDIT))
			throw new KoneException("您没有权限执行该操作.");
		boolean auditOk = request.getParameterBoolean("auditok");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			PrintAdBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-printAdBean", PrintAdBeanRemote.class);
			bean.offlineAudit(((AdminSessionObject) session).getLoginUser()
					.getUserId(), Integer.valueOf(id), auditOk);
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void printAdOnlineAudit(AdminSessionObject session,
			HttpRequest request, String id) throws NamingException,
			NoSuchFieldException {
		if (!((AdminSessionObject) session).hasRight(UserRight.PRINTAD_EDIT))
			throw new KoneException("您没有权限执行该操作.");
		boolean auditOk = request.getParameterBoolean("auditok");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			PrintAdBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-printAdBean", PrintAdBeanRemote.class);
			bean.onlineAudit(((AdminSessionObject) session).getLoginUser()
					.getUserId(), Integer.valueOf(id), auditOk);
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void queryAdCategoryList(boolean firstQuery, String keyword,
			int maxresults, int page, AdminSessionObject session,
			HttpRequest request, Document xmlDoc, Element content)
			throws NamingException, NoSuchFieldException {
		if (!((AdminSessionObject) session).hasRight(UserRight.ADCATEGORY))
			throw new KoneException("您没有查询广告分类的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			AdCategoryBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-adCategoryBean", AdCategoryBeanRemote.class);
			QueryResult<BaseAdCategory> r = bean.queryAdCategory(firstQuery,
					((AdminSessionObject) session).getLoginUser().getUserId(),
					0, keyword, page * maxresults, maxresults);
			appendCountElement(firstQuery, content, xmlDoc, r.getCount());
			Iterator<BaseAdCategory> it = r.getResultList().iterator();
			while (it.hasNext()) {
				Element el = xmlDoc.createElement("record");
				BaseAdCategory o = it.next();
				el.setAttribute("id", Long.toString(o.getId()));
				el.setAttribute("desp", o.getAdCategoryDesp());
				content.appendChild(el);
			}
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void deleteAdCategory(AdminSessionObject session, String id)
			throws NamingException {
		if (!((AdminSessionObject) session).hasRight(UserRight.ADCATEGORY_EDIT))
			throw new KoneException("您无删除广告类型的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			AdCategoryBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-adCategoryBean", AdCategoryBeanRemote.class);
			bean.delete(((AdminSessionObject) session).getLoginUser()
					.getUserId(), StringUnit.splitToShort(id, ","));
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void queryOrgAdList(boolean firstQuery, String keyword,
			int maxresults, int page, AdminSessionObject session,
			HttpRequest request, Document xmlDoc, Element content)
			throws NamingException, NoSuchFieldException {
		if (!((AdminSessionObject) session).hasRight(UserRight.ORGAD))
			throw new KoneException("您没有查询机构广告的权限.");
		Integer orgId = request.getParameterIntDef("orgid_value", null);
		Integer adCategoryId = request.getParameterIntDef("adCategoryId", null);
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
			OrgAdBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-orgAdBean", OrgAdBeanRemote.class);
			QueryResult<BaseOrgAd> r = bean.queryOrgAd(firstQuery, session
					.getLoginUser().getUserId(), orgId, 0, keyword,
					adCategoryId, page * maxresults, maxresults);
			appendCountElement(firstQuery, content, xmlDoc, r.getCount());
			Iterator<BaseOrgAd> it = r.getResultList().iterator();
			while (it.hasNext()) {
				Element el = xmlDoc.createElement("record");
				BaseOrgAd o = (BaseOrgAd) it.next();
				el.setAttribute("id", Long.toString(o.getId()));
				el.setAttribute("desp", o.getAdDesp());
				el.setAttribute("category", o.getAdCategory()
						.getAdCategoryDesp());
				el.setAttribute("filename", o.getFileName());
				el.setAttribute("orgdesp", o.getOrg().getOrgName());
				el.setAttribute("auditstatus", o.getAuditStatus().toString());
				el.setAttribute("isuploadcomplete",
						String.valueOf(o.isUploadComplete()));
				appendAuditStatus(!o.isUploadComplete(), o.getAuditStatus(), el);
				content.appendChild(el);
			}
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void deleteOrgAdList(AdminSessionObject session, String id)
			throws NamingException {
		if (!((AdminSessionObject) session).hasRight(UserRight.ORGAD_EDIT))
			throw new KoneException("您没有删除机构广告的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			OrgAdBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-orgAdBean", OrgAdBeanRemote.class);
			BaseOrgAd ad = bean.delete(((AdminSessionObject) session)
					.getLoginUser().getUserId(), Integer.valueOf(id));
			if (ad != null) {
				FileProcessor processor = new FileProcessor(
						AdminTradeExecutor.fileUserId,
						AdminTradeExecutor.filePwd, (short) 4);
				try {
					processor.deleteFile(((AdminSessionObject) session)
							.getLoginUser().getUserId(), ad.getFileName(), !ad
							.isUploadComplete());
				} catch (Throwable e) {
				}
			}
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void orgAdOnlineReq(AdminSessionObject session,
			HttpRequest request, String id) throws NamingException,
			NoSuchFieldException {
		if (!((AdminSessionObject) session).hasRight(UserRight.ORGAD_EDIT))
			throw new KoneException("您没有权限执行该操作.");
		boolean iscancel = request.getParameterBoolean("iscancel");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			OrgAdBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-orgAdBean", OrgAdBeanRemote.class);
			bean.onlineReq(((AdminSessionObject) session).getLoginUser()
					.getUserId(), Integer.valueOf(id), iscancel);
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void orgAdOfflineReq(AdminSessionObject session,
			HttpRequest request, String id) throws NamingException,
			NoSuchFieldException {
		if (!((AdminSessionObject) session).hasRight(UserRight.ORGAD_EDIT))
			throw new KoneException("您没有权限执行该操作.");
		boolean iscancel = request.getParameterBoolean("iscancel");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			OrgAdBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-orgAdBean", OrgAdBeanRemote.class);
			bean.offlineReq(((AdminSessionObject) session).getLoginUser()
					.getUserId(), Integer.valueOf(id), iscancel);
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void orgAdOfflineAudit(AdminSessionObject session,
			HttpRequest request, String id) throws NamingException,
			NoSuchFieldException {
		if (!((AdminSessionObject) session).hasRight(UserRight.ORGAD_EDIT))
			throw new KoneException("您没有权限执行该操作.");
		boolean auditOk = request.getParameterBoolean("auditok");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			OrgAdBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-orgAdBean", OrgAdBeanRemote.class);
			bean.offlineAudit(((AdminSessionObject) session).getLoginUser()
					.getUserId(), Integer.valueOf(id), auditOk);
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void orgAdOnlineAudit(AdminSessionObject session,
			HttpRequest request, String id) throws NamingException,
			NoSuchFieldException {
		if (!((AdminSessionObject) session).hasRight(UserRight.ORGAD_EDIT))
			throw new KoneException("您没有权限执行该操作.");
		boolean auditOk = request.getParameterBoolean("auditok");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			OrgAdBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-orgAdBean", OrgAdBeanRemote.class);
			bean.onlineAudit(((AdminSessionObject) session).getLoginUser()
					.getUserId(), Integer.valueOf(id), auditOk);
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void getTermAdList(AdminSessionObject session, String id,
			Integer adCategoryId, String keyword, Document xmlDoc,
			Element content) throws NamingException {
		if (!((AdminSessionObject) session).hasRight(UserRight.TERMAD_EDIT))
			throw new KoneException("您没有编辑终端广告的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			TermAdBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-termAdBean", TermAdBeanRemote.class);
			Iterator<BaseTermAd> it = bean.getAdList(
					((AdminSessionObject) session).getLoginUser().getUserId(),
					Integer.valueOf(id), adCategoryId, keyword).iterator();
			while (it.hasNext()) {
				BaseTermAd t = (BaseTermAd) it.next();
				Element e = xmlDoc.createElement("ad");
				content.appendChild(e);
				e.setAttribute("id", t.getIdString());
				e.setAttribute("category", t.getAdCategory()
						.getAdCategoryDesp());
				e.setAttribute("uploadcomplete", t.isUploadComplete() ? "true"
						: "false");
				e.setAttribute(
						"desp",
						t.getAdDesp()
								+ " ["
								+ t.getFileName()
								+ " , "
								+ new DateTime(t.getStartDate())
										.format("yyyy-MM-dd")
								+ "~"
								+ new DateTime(t.getEndDate())
										.format("yyyy-MM-dd") + "]");
				e.setAttribute("status",
						Integer.toString(t.getAuditStatus().getValue()));
				e.setAttribute("auditstatus", t.getAuditStatus().toString());
				e.setAttribute("uploadcompletedesp", t.isUploadComplete() ? "是"
						: "否");
			}
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void deleteTermAdList(AdminSessionObject session, String id)
			throws NamingException {
		if (!((AdminSessionObject) session).hasRight(UserRight.TERMAD_EDIT))
			throw new KoneException("您没有删除终端广告的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			TermAdBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-termAdBean", TermAdBeanRemote.class);
			BaseTermAd file = bean.delete(((AdminSessionObject) session)
					.getLoginUser().getUserId(), Integer.valueOf(id));
			if (file != null) {
				FileProcessor processor = new FileProcessor(
						AdminTradeExecutor.fileUserId,
						AdminTradeExecutor.filePwd, (short) 5);
				try {
					processor.deleteFile(((AdminSessionObject) session)
							.getLoginUser().getUserId(), file.getFileName(),
							!file.isUploadComplete());
				} catch (Throwable e) {
				}
			}
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void termAdOnlineReq(AdminSessionObject session,
			HttpRequest request, String id) throws NamingException,
			NoSuchFieldException {
		if (!((AdminSessionObject) session).hasRight(UserRight.TERMAD_EDIT))
			throw new KoneException("您没有权限执行该操作.");
		boolean iscancel = request.getParameterBoolean("iscancel");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			TermAdBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-termAdBean", TermAdBeanRemote.class);
			bean.onlineReq(((AdminSessionObject) session).getLoginUser()
					.getUserId(), Integer.valueOf(id), iscancel);
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void termAdOfflineReq(AdminSessionObject session,
			HttpRequest request, String id) throws NamingException,
			NoSuchFieldException {
		if (!((AdminSessionObject) session).hasRight(UserRight.TERMAD_EDIT))
			throw new KoneException("您没有权限执行该操作.");
		boolean iscancel = request.getParameterBoolean("iscancel");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			TermAdBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-termAdBean", TermAdBeanRemote.class);
			bean.offlineReq(((AdminSessionObject) session).getLoginUser()
					.getUserId(), Integer.valueOf(id), iscancel);
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void termAdOfflineAudit(AdminSessionObject session,
			HttpRequest request, String id) throws NamingException,
			NoSuchFieldException {
		if (!((AdminSessionObject) session).hasRight(UserRight.TERMAD_EDIT))
			throw new KoneException("您没有权限执行该操作.");
		boolean auditOk = request.getParameterBoolean("auditok");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			TermAdBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-termAdBean", TermAdBeanRemote.class);
			bean.offlineAudit(((AdminSessionObject) session).getLoginUser()
					.getUserId(), Integer.valueOf(id), auditOk);
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void termAdOnlineAudit(AdminSessionObject session,
			HttpRequest request, String id) throws NamingException,
			NoSuchFieldException {
		if (!((AdminSessionObject) session).hasRight(UserRight.TERMAD_EDIT))
			throw new KoneException("您没有权限执行该操作.");
		boolean auditOk = request.getParameterBoolean("auditok");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			TermAdBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-termAdBean", TermAdBeanRemote.class);
			bean.onlineAudit(((AdminSessionObject) session).getLoginUser()
					.getUserId(), Integer.valueOf(id), auditOk);
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void queryInfoCategoryList(boolean firstQuery, String keyword,
			int maxresults, int page, AdminSessionObject session,
			HttpRequest request, Document xmlDoc, Element content)
			throws NamingException, NoSuchFieldException {
		if (!((AdminSessionObject) session).hasRight(UserRight.INFOCATEGORY))
			throw new KoneException("您没有查询信息分类的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			InfoCategoryBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-infoCategoryBean",
					InfoCategoryBeanRemote.class);
			QueryResult<BaseInfoCategory> r = bean.queryInfoCategory(
					firstQuery, ((AdminSessionObject) session).getLoginUser()
							.getUserId(), 0, keyword, page * maxresults,
					maxresults);
			appendCountElement(firstQuery, content, xmlDoc, r.getCount());
			Iterator<BaseInfoCategory> it = r.getResultList().iterator();
			while (it.hasNext()) {
				Element el = xmlDoc.createElement("record");
				BaseInfoCategory o = it.next();
				el.setAttribute("id", Long.toString(o.getId()));
				el.setAttribute("desp", o.getInfoCategoryDesp());
				content.appendChild(el);
			}
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void deleteInfoCategory(AdminSessionObject session, String id)
			throws NamingException {
		if (!((AdminSessionObject) session)
				.hasRight(UserRight.INFOCATEGORY_EDIT))
			throw new KoneException("您无删除广告类型的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			InfoCategoryBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-infoCategoryBean",
					InfoCategoryBeanRemote.class);
			bean.delete(((AdminSessionObject) session).getLoginUser()
					.getUserId(), StringUnit.splitToShort(id, ","));
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void queryInfoList(boolean firstQuery, String keyword,
			int maxresults, int page, AdminSessionObject session,
			HttpRequest request, Document xmlDoc, Element content)
			throws NamingException, NoSuchFieldException {
		if (!((AdminSessionObject) session).hasRight(UserRight.INFO))
			throw new KoneException("您没有查询信息的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			InfoBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-infoBean", InfoBeanRemote.class);
			QueryResult<EditedInfo> r = bean.queryInfo(firstQuery, session
					.getLoginUser().getUserId(), request.getParameterIntDef(
					"orgid_value", 0), 0, keyword, request.getParameterIntDef(
					"infoCategoryId", null), page * maxresults, maxresults);
			appendCountElement(firstQuery, content, xmlDoc, r.getCount());
			Iterator<EditedInfo> it = r.getResultList().iterator();
			while (it.hasNext()) {
				Element el = xmlDoc.createElement("record");
				EditedInfo o = (EditedInfo) it.next();
				el.setAttribute("id", Long.toString(o.getId()));
				el.setAttribute("title", o.getTitle());
				el.setAttribute("time", DataUnit.formatDateTime(
						o.getPublishTime(), "MM-dd HH:mm"));
				el.setAttribute("orgdesp", o.getOrg().getOrgName());
				el.setAttribute("auditstatusdesp", o.getAuditStatus()
						.toString());
				el.setAttribute("infocategorydesp", o.getInfoCategory()
						.getInfoCategoryDesp());
				appendAuditStatus(false, o.getAuditStatus(), el);
				content.appendChild(el);
			}
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void deleteInfoList(AdminSessionObject session, String id)
			throws NamingException {
		if (!((AdminSessionObject) session).hasRight(UserRight.ORGAD_EDIT))
			throw new KoneException("您没有删除信息的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			InfoBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-infoBean", InfoBeanRemote.class);
			String file[] = bean.delete(((AdminSessionObject) session)
					.getLoginUser().getUserId(), Long.valueOf(id));
			if (file != null) {
				FileProcessor processor = new FileProcessor(
						AdminTradeExecutor.fileUserId,
						AdminTradeExecutor.filePwd, (short) 6);
				for (int i = 0; i < file.length; i++)
					try {
						processor.deleteFile(((AdminSessionObject) session)
								.getLoginUser().getUserId(), file[i], false);
					} catch (Throwable e) {
					}
			}
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void infoOnlineReq(AdminSessionObject session,
			HttpRequest request, String id) throws NamingException,
			NoSuchFieldException {
		if (!((AdminSessionObject) session).hasRight(UserRight.INFO_EDIT))
			throw new KoneException("您没有权限执行该操作.");
		boolean iscancel = request.getParameterBoolean("iscancel");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			InfoBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-infoBean", InfoBeanRemote.class);
			bean.onlineReq(((AdminSessionObject) session).getLoginUser()
					.getUserId(), Integer.valueOf(id), iscancel);
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void infoOfflineReq(AdminSessionObject session,
			HttpRequest request, String id) throws NamingException,
			NoSuchFieldException {
		if (!((AdminSessionObject) session).hasRight(UserRight.INFO_EDIT))
			throw new KoneException("您没有权限执行该操作.");
		boolean iscancel = request.getParameterBoolean("iscancel");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			InfoBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-infoBean", InfoBeanRemote.class);
			bean.offlineReq(((AdminSessionObject) session).getLoginUser()
					.getUserId(), Integer.valueOf(id), iscancel);
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void infoOfflineAudit(AdminSessionObject session,
			HttpRequest request, String id) throws NamingException,
			NoSuchFieldException {
		if (!((AdminSessionObject) session).hasRight(UserRight.INFO_EDIT))
			throw new KoneException("您没有权限执行该操作.");
		boolean auditOk = request.getParameterBoolean("auditok");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			InfoBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-infoBean", InfoBeanRemote.class);
			bean.offlineAudit(((AdminSessionObject) session).getLoginUser()
					.getUserId(), Integer.valueOf(id), auditOk);
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void infoOnlineAudit(AdminSessionObject session,
			HttpRequest request, String id) throws NamingException,
			NoSuchFieldException {
		if (!((AdminSessionObject) session).hasRight(UserRight.INFO_EDIT))
			throw new KoneException("您没有权限执行该操作.");
		boolean auditOk = request.getParameterBoolean("auditok");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			InfoBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-infoBean", InfoBeanRemote.class);
			bean.onlineAudit(((AdminSessionObject) session).getLoginUser()
					.getUserId(), Integer.valueOf(id), auditOk);
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void queryNeedAuditAdInfoList(boolean firstQuery, String keyword,
			int maxresults, int page, AdminSessionObject session,
			HttpRequest request, Document xmlDoc, Element content)
			throws NamingException, NoSuchFieldException {
		if (!((AdminSessionObject) session).hasRight(UserRight.AUDIT))
			throw new KoneException("您没有审核的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			SystemServiceBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-systemServiceBean",
					SystemServiceBeanRemote.class);
			int option = request.getParameterInt("option");
			QueryResult<?> r = bean.queryAuditInfo(option, firstQuery, session
					.getLoginUser().getUserId(), request.getParameterIntDef(
					"orgid_value", 0), 0, keyword, page * maxresults,
					maxresults);
			appendCountElement(firstQuery, content, xmlDoc, r.getCount());
			Iterator<?> it = r.getResultList().iterator();
			while (it.hasNext()) {
				Element el = xmlDoc.createElement("record");
				AuditStatus status = null;
				if (option == 0) {
					BasePrintAd o = (BasePrintAd) it.next();
					el.setAttribute("id", Long.toString(o.getId()));
					el.setAttribute("desp", o.getAdCategory()
							.getPrintAdCategoryDesp());
					el.setAttribute("auditstatus", o.getAuditStatus()
							.toString());
					el.setAttribute("orgdesp", o.getOrg().getOrgName());
					status = o.getAuditStatus();
				} else if (option == 1) {
					BaseOrgAd o = (BaseOrgAd) it.next();
					el.setAttribute("id", Long.toString(o.getId()));
					el.setAttribute("desp", o.getAdCategory()
							.getAdCategoryDesp());
					el.setAttribute("auditstatus", o.getAuditStatus()
							.toString());
					el.setAttribute("orgdesp", o.getOrg().getOrgName());
					status = o.getAuditStatus();
				} else if (option == 2) {
					BaseTermAd o = (BaseTermAd) it.next();
					el.setAttribute("id", Long.toString(o.getId()));
					el.setAttribute("desp", o.getAdCategory()
							.getAdCategoryDesp());
					el.setAttribute("auditstatus", o.getAuditStatus()
							.toString());
					el.setAttribute("orgdesp", o.getTerm().getTermDesp());
					status = o.getAuditStatus();
				} else if (option == 3) {
					BaseInfo o = (BaseInfo) it.next();
					el.setAttribute("id", Long.toString(o.getId()));
					el.setAttribute("desp", o.getInfoCategory()
							.getInfoCategoryDesp());
					el.setAttribute("auditstatus", o.getAuditStatus()
							.toString());
					el.setAttribute("orgdesp", o.getOrg().getOrgName());
					status = o.getAuditStatus();
				}
				if (status != null) {
					StringBuffer sb = new StringBuffer();
					switch (status) {
					case OFFLINE_REQ:
						sb.append("<a href=\"#\" onclick=\"preview(arguments[0]);return false;\">预览</a>");
						sb.append(" <a href=\"#\" onclick=\"offlineAuditOk(arguments[0]);return false;\">允许下线</a>");
						sb.append(" <a href=\"#\" onclick=\"offlineAuditFalse(arguments[0]);return false;\">禁止下线</a>");
						break;
					case ONLINE_REQ:
						sb.append("<a href=\"#\" onclick=\"preview(arguments[0]);return false;\">预览</a>");
						sb.append(" <a href=\"#\" onclick=\"onlineAuditOk(arguments[0]);return false;\">允许上线</a>");
						sb.append(" <a href=\"#\" onclick=\"onlineAuditFalse(arguments[0]);return false;\">禁止上线</a>");
						break;
					}
					el.setAttribute("option", sb.toString());
					content.appendChild(el);
				}
			}
		} finally {
			if (context != null)
				context.close();
		}
	}

}
