package kxd.engine.scs.admin.drivers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletResponse;
import jxl.common.Logger;
import kxd.engine.cache.beans.sts.CachedAlarmCategory;
import kxd.engine.cache.beans.sts.CachedApp;
import kxd.engine.cache.beans.sts.CachedAppCategory;
import kxd.engine.cache.beans.sts.CachedBusinessCategory;
import kxd.engine.cache.beans.sts.CachedCommInterface;
import kxd.engine.cache.beans.sts.CachedDevice;
import kxd.engine.cache.beans.sts.CachedDeviceDriver;
import kxd.engine.cache.beans.sts.CachedDeviceType;
import kxd.engine.cache.beans.sts.CachedDeviceTypeDriver;
import kxd.engine.cache.beans.sts.CachedFileCategory;
import kxd.engine.cache.beans.sts.CachedFileContentType;
import kxd.engine.cache.beans.sts.CachedFileHost;
import kxd.engine.cache.beans.sts.CachedFileOwner;
import kxd.engine.cache.beans.sts.CachedFileUser;
import kxd.engine.cache.beans.sts.CachedManuf;
import kxd.engine.cache.beans.sts.CachedOrg;
import kxd.engine.cache.beans.sts.CachedPayItem;
import kxd.engine.cache.beans.sts.CachedPayWay;
import kxd.engine.cache.beans.sts.CachedPrintType;
import kxd.engine.cache.beans.sts.CachedBusiness;
import kxd.engine.cache.beans.sts.CachedTermType;
import kxd.engine.cache.beans.sts.CachedTradeCode;
import kxd.engine.helper.CacheHelper;
import kxd.engine.scs.admin.AdminSessionObject;
import kxd.engine.scs.admin.AdminTradeDriver;
import kxd.engine.scs.admin.UserGroupItem;
import kxd.net.HttpRequest;
import kxd.net.connection.jndi.Lookuper;
import kxd.net.naming.LoopNamingContext;
import kxd.remote.scs.beans.BaseAdCategory;
import kxd.remote.scs.beans.BaseInfoCategory;
import kxd.remote.scs.beans.BasePrintAdCategory;
import kxd.remote.scs.beans.BaseRole;
import kxd.remote.scs.beans.invoice.EditedInvoiceTemplate;
import kxd.remote.scs.beans.right.LoginUser;
import kxd.remote.scs.interfaces.AdCategoryBeanRemote;
import kxd.remote.scs.interfaces.InfoCategoryBeanRemote;
import kxd.remote.scs.interfaces.PrintAdCategoryBeanRemote;
import kxd.remote.scs.interfaces.UserBeanRemote;
import kxd.remote.scs.interfaces.invoice.InvoiceTemplateBeanRemote;
import kxd.remote.scs.interfaces.service.SystemServiceBeanRemote;
import kxd.remote.scs.util.QueryResult;
import kxd.remote.scs.util.emun.UserGroup;
import kxd.util.KoneException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * 管理平台2.0获取缓存类数据交易驱动
 * 
 * @author liyy
 * 
 */

public class CachedAdminTradeDriver implements AdminTradeDriver {

	Logger logger = Logger.getLogger(EditedAdminTradeDriver.class);

	@Override
	public void execute(AdminSessionObject session, HttpRequest request,
			HttpServletResponse response, Document xmlDoc, Element content,
			Element result) throws Throwable {
		// String cmdgroup = request.getParameterDef("cmdgroup", "");
		String cmd = request.getParameter("cmd");
		if (cmd.equals("alarmcategory")) {
			CachedAlarmCategoryList(session, request, response, xmlDoc,
					content, result);
		} else if (cmd.equals("app")) {
			CachedAppList(session, request, response, xmlDoc, content, result);
		} else if (cmd.equals("appcategory")) {
			CachedAppCategoryList(session, request, response, xmlDoc, content,
					result);
		} else if (cmd.equals("comminterface")) {
			CachedCommInterfaceList(session, request, response, xmlDoc,
					content, result);
		} else if (cmd.equals("businesscategory")) {
			CachedBusinessCategoryList(session, request, response, xmlDoc,
					content, result);
		} else if (cmd.equals("business")) {
			CachedBusinessList(session, request, response, xmlDoc, content,
					result);
		} else if (cmd.equals("device")) {
			CachedDeviceList(session, request, response, xmlDoc, content,
					result);
		} else if (cmd.equals("devicetype")) {
			CachedDeviceTypeList(session, request, response, xmlDoc, content,
					result);
		} else if (cmd.equals("devicedriver")) {
			CachedDeviceDriverList(session, request, response, xmlDoc, content,
					result);
		} else if (cmd.equals("devicetypedriver")) {
			CachedDeviceTypeDriverList(session, request, response, xmlDoc,
					content, result);
		} else if (cmd.equals("filecategory")) {
			CachedFileCategoryList(session, request, response, xmlDoc, content,
					result);
		} else if (cmd.equals("fileContenttype")) {
			CachedFileContentTypeList(session, request, response, xmlDoc,
					content, result);
		} else if (cmd.equals("filehost")) {
			CachedFileHostList(session, request, response, xmlDoc, content,
					result);
		} else if (cmd.equals("fileowner")) {
			CachedFileOwnerList(session, request, response, xmlDoc, content,
					result);
		} else if (cmd.equals("fileuser")) {
			CachedFileUserList(session, request, response, xmlDoc, content,
					result);
		} else if (cmd.equals("manuf")) {
			CachedManufList(session, request, response, xmlDoc, content, result);
		} else if (cmd.equals("printtype")) {
			CachedPrintTypeList(session, request, response, xmlDoc, content,
					result);
		} else if (cmd.equals("termtype")) {
			CachedTermTypeList(session, request, response, xmlDoc, content,
					result);
		} else if (cmd.equals("tradecode")) {
			CachedTradeCodeList(session, request, response, xmlDoc, content,
					result);
		} else if (cmd.equals("payway")) {
			CachedPayWayList(session, request, response, xmlDoc, content,
					result);
		} else if (cmd.equals("payitem")) {
			CachedPayItemList(session, request, response, xmlDoc, content,
					result);
		} else if (cmd.equals("printadcategory")) {
			CachedPrintAdCategoryList(session, request, response, xmlDoc,
					content, result);
		} else if (cmd.equals("adcategory")) {
			CachedAdCategoryList(session, request, response, xmlDoc, content,
					result);
		} else if (cmd.equals("infocategory")) {
			CachedInfoCategoryList(session, request, response, xmlDoc, content,
					result);
		} else if (cmd.equals("invoicetemplate")) {
			CachedInvoiceTemplateList(session, request, response, xmlDoc,
					content, result);
		} else if (cmd.equals("paramconfig")) {
			CachedParamConfigList(session, request, response, xmlDoc, content,
					result);
		} else if (cmd.equals("role")) {
			CachedRoleList(session, request, response, xmlDoc, content, result);
		} else if (cmd.equals("usermanuf")) {
			CachedUserManufList(session, request, response, xmlDoc, content,
					result);
		} else if (cmd.equals("usergroup")) {
			CachedUserGroupList(session, request, response, xmlDoc, content,
					result);
		} else if (cmd.equals("orginfo")) {
			CachedOrgInfo(session, request, response, xmlDoc, content,
					result);
		}

	}

	// 告警分类
	public void CachedAlarmCategoryList(AdminSessionObject session,
			HttpRequest request, HttpServletResponse response, Document xmlDoc,
			Element content, Element result) throws Throwable {
		for (CachedAlarmCategory o : CacheHelper.alarmCategoryMap.values()) {
			Element el = xmlDoc.createElement("record");
			el.setAttribute("id", o.getIdString());
			el.setAttribute("text", o.getAlarmCategoryDesp());
			el.setAttribute("alarmlevel", String.valueOf(o.getAlarmLevel().getValue()));
			content.appendChild(el);
		}
	}

	// 应用
	public void CachedAppList(AdminSessionObject session, HttpRequest request,
			HttpServletResponse response, Document xmlDoc, Element content,
			Element result) throws Throwable {
		for (CachedApp o : CacheHelper.appMap.values()) {
			Element el = xmlDoc.createElement("record");
			el.setAttribute("id", o.getIdString());
			el.setAttribute("text", o.getAppDesp());
			content.appendChild(el);
		}
	}

	// 应用分类
	public void CachedAppCategoryList(AdminSessionObject session,
			HttpRequest request, HttpServletResponse response, Document xmlDoc,
			Element content, Element result) throws Throwable {
		for (CachedAppCategory o : CacheHelper.appCategoryMap.values()) {
			Element el = xmlDoc.createElement("record");
			el.setAttribute("id", o.getIdString());
			el.setAttribute("text", o.getAppCategoryDesp());
			content.appendChild(el);
		}
	}

	// 接口
	public void CachedCommInterfaceList(AdminSessionObject session,
			HttpRequest request, HttpServletResponse response, Document xmlDoc,
			Element content, Element result) throws Throwable {
		for (CachedCommInterface o : CacheHelper.commInterfaceMap.values()) {
			Element el = xmlDoc.createElement("record");
			el.setAttribute("id", o.getIdString());
			el.setAttribute("text", o.getDesp());
			content.appendChild(el);
		}
	}

	// 业务
	public void CachedBusinessList(AdminSessionObject session,
			HttpRequest request, HttpServletResponse response, Document xmlDoc,
			Element content, Element result) throws Throwable {
		for (CachedBusiness o : CacheHelper.businessMap.values()) {
			Element el = xmlDoc.createElement("record");
			el.setAttribute("id", o.getIdString());
			el.setAttribute("text", o.getBusinessDesp());
			content.appendChild(el);
		}
	}

	// 业务分类
	public void CachedBusinessCategoryList(AdminSessionObject session,
			HttpRequest request, HttpServletResponse response, Document xmlDoc,
			Element content, Element result) throws Throwable {
		for (CachedBusinessCategory o : CacheHelper.businessCategoryMap
				.values()) {
			Element el = xmlDoc.createElement("record");
			el.setAttribute("id", o.getIdString());
			el.setAttribute("text", o.getBusinessCategoryDesp());
			content.appendChild(el);
		}
	}

	//
	public void CachedDeviceList(AdminSessionObject session,
			HttpRequest request, HttpServletResponse response, Document xmlDoc,
			Element content, Element result) throws Throwable {
		for (CachedDevice o : CacheHelper.deviceMap.values()) {
			Element el = xmlDoc.createElement("record");
			el.setAttribute("id", o.getIdString());
			el.setAttribute("text", o.getDeviceName());
			content.appendChild(el);
		}
	}

	//
	public void CachedDeviceTypeList(AdminSessionObject session,
			HttpRequest request, HttpServletResponse response, Document xmlDoc,
			Element content, Element result) throws Throwable {
		for (CachedDeviceType o : CacheHelper.deviceTypeMap.values()) {
			Element el = xmlDoc.createElement("record");
			el.setAttribute("id", o.getIdString());
			el.setAttribute("text", o.getDeviceTypeDesp());
			content.appendChild(el);
		}
	}

	//
	public void CachedDeviceDriverList(AdminSessionObject session,
			HttpRequest request, HttpServletResponse response, Document xmlDoc,
			Element content, Element result) throws Throwable {
		for (CachedDeviceDriver o : CacheHelper.deviceDriverMap.values()) {
			Element el = xmlDoc.createElement("record");
			el.setAttribute("id", o.getIdString());
			el.setAttribute("text", o.getDeviceDriverDesp());
			content.appendChild(el);
		}
	}

	//
	public void CachedDeviceTypeDriverList(AdminSessionObject session,
			HttpRequest request, HttpServletResponse response, Document xmlDoc,
			Element content, Element result) throws Throwable {
		for (CachedDeviceTypeDriver o : CacheHelper.deviceTypeDriverMap
				.values()) {
			Element el = xmlDoc.createElement("record");
			el.setAttribute("id", o.getIdString());
			el.setAttribute("text", o.getDeviceTypeDriverDesp());
			content.appendChild(el);
		}
	}

	//
	public void CachedFileCategoryList(AdminSessionObject session,
			HttpRequest request, HttpServletResponse response, Document xmlDoc,
			Element content, Element result) throws Throwable {
		for (CachedFileCategory o : CacheHelper.fileCategoryMap.values()) {
			Element el = xmlDoc.createElement("record");
			el.setAttribute("id", o.getIdString());
			el.setAttribute("text", o.getFileCategoryDesp());
			content.appendChild(el);
		}
	}

	//
	public void CachedFileContentTypeList(AdminSessionObject session,
			HttpRequest request, HttpServletResponse response, Document xmlDoc,
			Element content, Element result) throws Throwable {
		for (CachedFileContentType o : CacheHelper.fileContentTypeMap.values()) {
			Element el = xmlDoc.createElement("record");
			el.setAttribute("id", o.getIdString());
			el.setAttribute("text", o.getContentType());
			content.appendChild(el);
		}
	}

	//
	public void CachedFileHostList(AdminSessionObject session,
			HttpRequest request, HttpServletResponse response, Document xmlDoc,
			Element content, Element result) throws Throwable {
		for (CachedFileHost o : CacheHelper.fileHostMap.values()) {
			Element el = xmlDoc.createElement("record");
			el.setAttribute("id", o.getIdString());
			el.setAttribute("text", o.getHostDesp());
			content.appendChild(el);
		}
	}

	// 文件属主
	public void CachedFileOwnerList(AdminSessionObject session,
			HttpRequest request, HttpServletResponse response, Document xmlDoc,
			Element content, Element result) throws Throwable {
		for (CachedFileOwner o : CacheHelper.fileOwnerMap.values()) {
			Element el = xmlDoc.createElement("record");
			el.setAttribute("id", o.getIdString());
			el.setAttribute("text", o.getFileOwnerDesp());
			content.appendChild(el);
		}
	}

	// 文件用户
	public void CachedFileUserList(AdminSessionObject session,
			HttpRequest request, HttpServletResponse response, Document xmlDoc,
			Element content, Element result) throws Throwable {
		for (CachedFileUser o : CacheHelper.fileUserMap.values()) {
			Element el = xmlDoc.createElement("record");
			el.setAttribute("id", o.getIdString());
			el.setAttribute("text", o.getText());
			content.appendChild(el);
		}
	}

	//
	public void CachedManufList(AdminSessionObject session,
			HttpRequest request, HttpServletResponse response, Document xmlDoc,
			Element content, Element result) throws Throwable {
		for (CachedManuf o : CacheHelper.manufMap.values()) {
			Element el = xmlDoc.createElement("record");
			el.setAttribute("id", o.getIdString());
			el.setAttribute("text", o.getManufName());
			content.appendChild(el);
		}
	}

	// 打印类型
	public void CachedPrintTypeList(AdminSessionObject session,
			HttpRequest request, HttpServletResponse response, Document xmlDoc,
			Element content, Element result) throws Throwable {
		for (CachedPrintType o : CacheHelper.printTypeMap.values()) {
			Element el = xmlDoc.createElement("record");
			el.setAttribute("id", o.getIdString());
			el.setAttribute("text", o.getPrintTypeDesp());
			content.appendChild(el);
		}
	}

	//
	public void CachedTermTypeList(AdminSessionObject session,
			HttpRequest request, HttpServletResponse response, Document xmlDoc,
			Element content, Element result) throws Throwable {
		for (CachedTermType o : CacheHelper.termTypeMap.values()) {
			Element el = xmlDoc.createElement("record");
			el.setAttribute("id", o.getIdString());
			el.setAttribute("text", o.getTypeDesp());
			content.appendChild(el);
		}
	}

	// //
	// public void CachedTradeCodeList(AdminSessionObject session,
	// HttpRequest request, HttpServletResponse response, Document xmlDoc,
	// Element content, Element result) throws Throwable {
	// for (CachedTradeCodeHashMap o : CacheHelper.tradeCodeMap.values()) {
	// Element el = xmlDoc.createElement("record");
	// el.setAttribute("id", o.getIdString());
	// el.setAttribute("text", o.getTypeDesp());
	// content.appendChild(el);
	// }
	// }

	//
	public void CachedPayWayList(AdminSessionObject session,
			HttpRequest request, HttpServletResponse response, Document xmlDoc,
			Element content, Element result) throws Throwable {
		for (CachedPayWay o : CacheHelper.payWayMap.values()) {
			Element el = xmlDoc.createElement("record");
			el.setAttribute("id", o.getIdString());
			el.setAttribute("text", o.getPayWayDesp());
			content.appendChild(el);
		}
	}

	//
	public void CachedPayItemList(AdminSessionObject session,
			HttpRequest request, HttpServletResponse response, Document xmlDoc,
			Element content, Element result) throws Throwable {
		for (CachedPayItem o : CacheHelper.payItemMap.values()) {
			Element el = xmlDoc.createElement("record");
			el.setAttribute("id", o.getIdString());
			el.setAttribute("text", o.getPayItemDesp());
			content.appendChild(el);
		}
	}

	//
	public void CachedTradeCodeList(AdminSessionObject session,
			HttpRequest request, HttpServletResponse response, Document xmlDoc,
			Element content, Element result) throws Throwable {
		Iterator<Entry<Integer, CachedTradeCode>> it = CacheHelper.tradeCodeMap
				.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Integer, CachedTradeCode> map = it.next();
			Element el = xmlDoc.createElement("record");
			el.setAttribute("id", map.getValue().getIdString());
			el.setAttribute("text", map.getValue().getTradeDesp());
			content.appendChild(el);
		}
	}

	// 打印广告分类
	public void CachedPrintAdCategoryList(AdminSessionObject session,
			HttpRequest request, HttpServletResponse response, Document xmlDoc,
			Element content, Element result) throws Throwable {
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			PrintAdCategoryBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-printAdCategoryBean",
					PrintAdCategoryBeanRemote.class);
			QueryResult<BasePrintAdCategory> r = bean.queryPrintAdCategory(
					true, ((AdminSessionObject) session).getLoginUser()
							.getUserId(), 0, "", 0, 999);
			Iterator<BasePrintAdCategory> it = r.getResultList().iterator();
			while (it.hasNext()) {
				Element el = xmlDoc.createElement("record");
				BasePrintAdCategory o = it.next();
				el.setAttribute("id", Long.toString(o.getId()));
				el.setAttribute("text", o.getPrintAdCategoryDesp());
				content.appendChild(el);
			}
		} finally {
			if (context != null)
				context.close();
		}
	}

	// 广告分类
	public void CachedAdCategoryList(AdminSessionObject session,
			HttpRequest request, HttpServletResponse response, Document xmlDoc,
			Element content, Element result) throws Throwable {
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			AdCategoryBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-adCategoryBean", AdCategoryBeanRemote.class);
			QueryResult<BaseAdCategory> r = bean.queryAdCategory(true,
					((AdminSessionObject) session).getLoginUser().getUserId(),
					0, "", 0, 999);
			Iterator<BaseAdCategory> it = r.getResultList().iterator();
			while (it.hasNext()) {
				Element el = xmlDoc.createElement("record");
				BaseAdCategory o = it.next();
				el.setAttribute("id", Long.toString(o.getId()));
				el.setAttribute("text", o.getAdCategoryDesp());
				content.appendChild(el);
			}
		} finally {
			if (context != null)
				context.close();
		}
	}

	// 信息分类
	public void CachedInfoCategoryList(AdminSessionObject session,
			HttpRequest request, HttpServletResponse response, Document xmlDoc,
			Element content, Element result) throws Throwable {
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			InfoCategoryBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-infoCategoryBean",
					InfoCategoryBeanRemote.class);
			QueryResult<BaseInfoCategory> r = bean.queryInfoCategory(true,
					((AdminSessionObject) session).getLoginUser().getUserId(),
					0, "", 0, 999);
			Iterator<BaseInfoCategory> it = r.getResultList().iterator();
			while (it.hasNext()) {
				Element el = xmlDoc.createElement("record");
				BaseInfoCategory o = it.next();
				el.setAttribute("id", Long.toString(o.getId()));
				el.setAttribute("text", o.getInfoCategoryDesp());
				content.appendChild(el);
			}
		} finally {
			if (context != null)
				context.close();
		}

	}

	// 发票模板
	public void CachedInvoiceTemplateList(AdminSessionObject session,
			HttpRequest request, HttpServletResponse response, Document xmlDoc,
			Element content, Element result) throws Throwable {
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			InvoiceTemplateBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-invoiceTemplateBean",
					InvoiceTemplateBeanRemote.class);
			QueryResult<EditedInvoiceTemplate> r = bean.queryInvoiceTemplate(
					true, ((AdminSessionObject) session).getLoginUser()
							.getUserId(), "", 0, 999);
			Iterator<EditedInvoiceTemplate> it = r.getResultList().iterator();
			while (it.hasNext()) {
				Element el = xmlDoc.createElement("record");
				EditedInvoiceTemplate o = it.next();
				el.setAttribute("id", o.getIdString());
				el.setAttribute("text", o.getTemplateDesp());
				content.appendChild(el);
			}
		} finally {
			if (context != null) {
				context.close();
			}
		}
	}

	// 参数表数据
	public void CachedParamConfigList(AdminSessionObject session,
			HttpRequest request, HttpServletResponse response, Document xmlDoc,
			Element content, Element result) throws Throwable {
		LoopNamingContext context = new LoopNamingContext("db");
		Integer id = request.getParameterIntDef("id", null);
		try {
			SystemServiceBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-systemServiceBean",
					SystemServiceBeanRemote.class);
			Iterator<?> it = ((List<?>) bean.getParamsConfig()).iterator();
			while (it.hasNext()) {

				Object[] o = (Object[]) it.next();
				if (o != null && o.length >= 3)
					if (id != null && Integer.parseInt(o[0].toString()) == id) {
						Element el = xmlDoc.createElement("record");
						el.setAttribute("id", o[1].toString());
						el.setAttribute("text", o[2].toString());
						content.appendChild(el);
					}
			}
		} finally {
			if (context != null) {
				context.close();
			}
		}
	}

	// 角色列表
	public void CachedRoleList(AdminSessionObject session, HttpRequest request,
			HttpServletResponse response, Document xmlDoc, Element content,
			Element result) throws Throwable {
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			LoginUser loginUser = ((AdminSessionObject) session).getLoginUser();
			UserBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-userBean", UserBeanRemote.class);
			Collection<BaseRole> roleList = bean.getUserManagedRoles(
					loginUser.getUserId(), null, null);
			Iterator<BaseRole> it = roleList.iterator();
			while (it.hasNext()) {
				BaseRole o = (BaseRole) it.next();
				Element el = xmlDoc.createElement("record");
				el.setAttribute("id", o.getIdString());
				el.setAttribute("text", o.getRoleName());
				content.appendChild(el);
			}
		} finally {
			if (context != null) {
				context.close();
			}
		}
	}

	// 编辑用户信息时厂商下拉框列表
	public void CachedUserManufList(AdminSessionObject session,
			HttpRequest request, HttpServletResponse response, Document xmlDoc,
			Element content, Element result) throws Throwable {
		Collection<CachedManuf> manufList;
		try {
			LoginUser user = ((AdminSessionObject) session).getLoginUser();
			if (user.getUserGroup().isSystemManager()) {
				manufList = CacheHelper.manufMap.values();
				CachedManuf nm = new CachedManuf();
				nm.setManufName("");
				((List<CachedManuf>) manufList).add(0, nm);
			} else if (user.getManuf().getId() != null) {
				manufList = new ArrayList<CachedManuf>();
				CachedManuf manuf = new CachedManuf();
				manuf.setId(user.getManuf().getId());
				manuf.setManufName(user.getManuf().getManufName());
				manufList.add(manuf);
				CachedManuf nm = new CachedManuf();
				nm.setManufName("");
				((List<CachedManuf>) manufList).add(0, nm);
			} else
				manufList = new ArrayList<CachedManuf>();

			Iterator<CachedManuf> it = manufList.iterator();
			while (it.hasNext()) {
				CachedManuf o = (CachedManuf) it.next();
				Element el = xmlDoc.createElement("record");
				el.setAttribute("id", o.getIdString());
				el.setAttribute("text", o.getManufName());
				content.appendChild(el);
			}
		} finally {

		}
	}

	//从缓存中获取机构信息
	public void CachedOrgInfo(AdminSessionObject session,
			HttpRequest request, HttpServletResponse response, Document xmlDoc,
			Element content, Element result) throws Throwable {
		Integer orgId = request.getParameterIntDef("orgId", null);
		if(orgId==null)
			throw new KoneException("无效的机构ID[orgId]");
		try {
			CachedOrg org = CacheHelper.orgMap.get(orgId);
			if (org!=null) {
				Element el = xmlDoc.createElement("record");
				el.setAttribute("id", org.getIdString());
				el.setAttribute("orgName", org.getOrgName());
				el.setAttribute("depth", String.valueOf(org.getDepth()));
				el.setAttribute("orgtype", String.valueOf(org.getOrgType()));				
				content.appendChild(el);
			}
		} finally {

		}
	}
	
	// 用户组
	public void CachedUserGroupList(AdminSessionObject session,
			HttpRequest request, HttpServletResponse response, Document xmlDoc,
			Element content, Element result) throws Throwable {
		try {
			LoginUser user = ((AdminSessionObject) session).getLoginUser();
			Collection<UserGroupItem> c = new ArrayList<UserGroupItem>();
			UserGroup g = user.getUserGroup();
			UserGroup gs[] = UserGroup.values();
			for (int i = 0; i < gs.length; i++) {
				UserGroup g1 = gs[i];
				if (g1.compareTo(g) > 0) {
					c.add(new UserGroupItem(g1.getValue(), g1.toString()));
				}
			}
			if (c.size() == 0)
				c.add(new UserGroupItem(UserGroup.CUSTOMER.getValue(),
						UserGroup.CUSTOMER.toString()));
			Iterator<UserGroupItem> it = c.iterator();
			while (it.hasNext()) {
				UserGroupItem o = (UserGroupItem) it.next();
				Element el = xmlDoc.createElement("record");
				el.setAttribute("id", o.getIdString());
				el.setAttribute("text", o.getText());
				content.appendChild(el);
			}
		} finally {

		}
	}	
}