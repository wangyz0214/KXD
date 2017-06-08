package kxd.engine.scs.admin.drivers;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import javax.naming.NamingException;
import javax.servlet.http.HttpServletResponse;

import kxd.engine.cache.beans.sts.CacheTermMonitorResult;
import kxd.engine.cache.beans.sts.CachedBusiness;
import kxd.engine.cache.beans.sts.CachedBusinessCategory;
import kxd.engine.cache.beans.sts.CachedCommInterface;
import kxd.engine.cache.beans.sts.CachedManuf;
import kxd.engine.cache.beans.sts.CachedPayItem;
import kxd.engine.cache.beans.sts.CachedPayWay;
import kxd.engine.cache.beans.sts.CachedTradeCode;
import kxd.engine.helper.AdminHelper;
import kxd.engine.helper.CacheHelper;
import kxd.engine.helper.MonitorHelper;
import kxd.engine.scs.admin.AdminSessionObject;
import kxd.engine.scs.admin.AdminTradeDriver;
import kxd.engine.scs.admin.util.UserRight;
import kxd.engine.scs.monitor.InterfaceMonitorData;
import kxd.engine.scs.monitor.InterfaceMonitorDataItem;
import kxd.engine.scs.monitor.OrgInterfaceMonitorDataMap;
import kxd.engine.scs.monitor.OrgTermMonitorDataMap;
import kxd.engine.scs.monitor.OrgTradeMonitorDataMap;
import kxd.engine.scs.monitor.TermMonitorData;
import kxd.engine.scs.monitor.TradeMonitorData;
import kxd.engine.scs.monitor.TradeMonitorDataItem;
import kxd.net.HttpRequest;
import kxd.remote.scs.beans.BaseMonitorData;
import kxd.remote.scs.beans.right.LoginUser;
import kxd.remote.scs.beans.right.QueryedOrg;
import kxd.remote.scs.util.emun.SettlementType;
import kxd.util.KeyValue;
import kxd.util.KoneException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class MonitorTradeDriver implements AdminTradeDriver {
	LoginUser user;

	@Override
	public void execute(AdminSessionObject session, HttpRequest request,
			HttpServletResponse response, Document xmlDoc, Element content,
			Element result) throws Throwable {
		user = session.getLoginUser();
		if (user == null || !user.isLogined())
			throw new KoneException("用户必须登录才能使用");
		String cmd = request.getParameter("cmd");
		if (cmd.equals("getadminbasicdata")) {
			getAdminBasicData(session, request, xmlDoc, content);
		} else if (cmd.equals("getMapMoniortTermInfo")) {
			getMapMonitorTermInfo(session, request, xmlDoc, content);
		} else if (cmd.equals("getMapMoniortTradeInfo")) {
			getMapMonitorTradeInfo(session, request, xmlDoc, content);
		} else if (cmd.equals("getMonitorInterfaceInfo")) {
			getMonitorInterfaceInfo(session, request, xmlDoc, content);
		} else
			throw new KoneException("无效的交易代码");
	}

	private void getMonitorInterfaceInfo(AdminSessionObject session,
			HttpRequest request, Document xmlDoc, Element content)
			throws Throwable {
		if (!((AdminSessionObject) session).hasRight(UserRight.TERMMONITOR))
			throw new KoneException("您没有终端监控的权限.");
		KeyValue<BaseMonitorData, Collection<OrgInterfaceMonitorDataMap>> r = MonitorHelper
				.getMonitorInterfaceList();
		appendBasicInfoData(content, r.getKey());
		Collection<OrgInterfaceMonitorDataMap> c = r.getValue();
		for (OrgInterfaceMonitorDataMap o : c) {
			Element el = xmlDoc.createElement("org");
			content.appendChild(el);
			el.setAttribute("orgid", o.getOrg().getIdString());
			if (o.getOrg().getStandardAreaCode() != null)
				el.setAttribute("areacode", o.getOrg().getStandardAreaCode());
			if (o.getOrg().getSimpleName() != null
					&& !o.getOrg().getSimpleName().trim().isEmpty())
				el.setAttribute("orgname", o.getOrg().getSimpleName().trim());
			else
				el.setAttribute("orgname", o.getOrg().getOrgName());
			Iterator<Short> it = o.keySet().iterator();
			while (it.hasNext()) {
				short interfaceId = it.next();
				CachedCommInterface inf = CacheHelper.commInterfaceMap
						.get(interfaceId);
				if (inf != null) {
					Element ee = xmlDoc.createElement("interface");
					el.appendChild(ee);
					ee.setAttribute("id", Integer.toString(inf.getId()));
					ee.setAttribute("desp", inf.getDesp());
					ee.setAttribute("type", Integer.toString(inf.getType()));

					InterfaceMonitorData d = o.get(interfaceId);
					Element e1 = xmlDoc.createElement("today");
					ee.appendChild(e1);
					appendInterfaceMonitorDataItem(e1, d.getDayData());
					e1 = xmlDoc.createElement("hour");
					ee.appendChild(e1);
					appendInterfaceMonitorDataItem(e1, d.getHourData());
					e1 = xmlDoc.createElement("halfhour");
					ee.appendChild(e1);
					appendInterfaceMonitorDataItem(e1, d.getHalfHourData());
					e1 = xmlDoc.createElement("tenminutes");
					ee.appendChild(e1);
					appendInterfaceMonitorDataItem(e1, d.getTenMinutesData());
				}
			}
		}
	}

	private void appendInterfaceMonitorDataItem(Element el,
			InterfaceMonitorDataItem item) {
		el.setAttribute("suc_count", Long.toString(item.sucCount));
		el.setAttribute("err_count", Long.toString(item.getErrorCount()));
		el.setAttribute("timeout_count", Long.toString(item.getTimeoutCount()));
		el.setAttribute("duration", Long.toString(item.duration));
	}

	private void getMapMonitorTradeInfo(AdminSessionObject session,
			HttpRequest request, Document xmlDoc, Element content)
			throws Throwable {
		if (!((AdminSessionObject) session).hasRight(UserRight.TERMMONITOR))
			throw new KoneException("您没有终端监控的权限.");
		Integer orgId = request.getParameterIntDef("orgid", null);

		if (!((AdminSessionObject) session).getLoginUser().getUserGroup()
				.isSystemManager()) {
			if (orgId == null)
				orgId = ((AdminSessionObject) session).getLoginUser().getOrg()
						.getId();
		}

		KeyValue<BaseMonitorData, Collection<OrgTradeMonitorDataMap>> r = MonitorHelper
				.getMapMonitorTradeList(orgId, true);
		appendBasicInfoData(content, r.getKey());
		Collection<OrgTradeMonitorDataMap> c = r.getValue();
		for (OrgTradeMonitorDataMap o : c) {
			Element el = xmlDoc.createElement("org");
			content.appendChild(el);
			el.setAttribute("orgid", o.getOrg().getIdString());
			if (o.getOrg().getStandardAreaCode() != null)
				el.setAttribute("areacode", o.getOrg().getStandardAreaCode());
			if (o.getOrg().getSimpleName() != null
					&& !o.getOrg().getSimpleName().trim().isEmpty())
				el.setAttribute("orgname", o.getOrg().getSimpleName().trim());
			else
				el.setAttribute("orgname", o.getOrg().getOrgName());
			Iterator<Integer> it = o.keySet().iterator();
			while (it.hasNext()) {
				Element ee = xmlDoc.createElement("tradecode");
				el.appendChild(ee);
				Integer tradeCodeid = it.next();
				CachedTradeCode code = CacheHelper.tradeCodeMap
						.get(tradeCodeid);
				if (code.getPayItemId() != null) {
					ee.setAttribute("payitemid",
							Integer.toString(code.getPayItemId()));
					ee.setAttribute("payitemdesp", code.getPayItem()
							.getPayItemDesp());
				}
				if (code.getPayItemId() != null) {
					ee.setAttribute("paywayid",
							Integer.toString(code.getPayWayId()));
					ee.setAttribute("paywaydesp", code.getPayWay()
							.getPayWayDesp());
				}
				ee.setAttribute("businessid",
						Integer.toString(code.getBusinessId()));
				ee.setAttribute("businessdesp", "");
				ee.setAttribute("businesscategoryid", "");
				ee.setAttribute("businesscategorydesp", "");
				if(code.getBusiness() != null){
					ee.setAttribute("businessdesp", code.getBusiness().getBusinessDesp());
					ee.setAttribute("businesscategoryid", Integer.toString(code.getBusiness().getBusinessCategoryId()));
					ee.setAttribute("businesscategorydesp", code.getBusiness().getBusinessCategory().getBusinessCategoryDesp());
				}
				ee.setAttribute("tradecodeid", Integer.toString(tradeCodeid));
				TradeMonitorData d = o.get(tradeCodeid);
				Element e1 = xmlDoc.createElement("today");
				ee.appendChild(e1);
				appendTradeMonitorDataItem(e1, d.getDayData());
				e1 = xmlDoc.createElement("hour");
				ee.appendChild(e1);
				appendTradeMonitorDataItem(e1, d.getHourData());
				e1 = xmlDoc.createElement("halfhour");
				ee.appendChild(e1);
				appendTradeMonitorDataItem(e1, d.getHalfHourData());
				e1 = xmlDoc.createElement("tenminutes");
				ee.appendChild(e1);
				appendTradeMonitorDataItem(e1, d.getTenMinutesData());
			}
		}
	}

	private void appendTradeMonitorDataItem(Element el,
			TradeMonitorDataItem item) {
		el.setAttribute("amount", Long.toString(item.getAmount()));
		el.setAttribute("suc_amount", Long.toString(item.getSucAmount()));
		el.setAttribute("suc_count", Long.toString(item.getSucCount()));
		el.setAttribute("count", Long.toString(item.getCount()));
		el.setAttribute("duration", Long.toString(item.getDuration()));
		el.setAttribute("refund_amount", Long.toString(item.getReturnAmount()));
		el.setAttribute("refund_count", Long.toString(item.getReturnCount()));
		el.setAttribute("cancel_refund_amount",
				Long.toString(item.getCancelReturnAmount()));
		el.setAttribute("cancel_refund_count",
				Long.toString(item.getCancelReturnCount()));
	}

	private void getMapMonitorTermInfo(AdminSessionObject session,
			HttpRequest request, Document xmlDoc, Element content)
			throws Throwable {
		if (!((AdminSessionObject) session).hasRight(UserRight.TERMMONITOR))
			throw new KoneException("您没有终端监控的权限.");
		Integer orgId = request.getParameterIntDef("orgid", null);
		if (!((AdminSessionObject) session).getLoginUser().getUserGroup()
				.isSystemManager()) {
			if (((AdminSessionObject) session).getLoginUser().getManuf() != null)
				if (orgId == null)
					orgId = ((AdminSessionObject) session).getLoginUser()
							.getOrg().getId();
		}

		KeyValue<BaseMonitorData, Collection<OrgTermMonitorDataMap>> r = MonitorHelper
				.getMapMonitorTermList(orgId, true);
		Collection<OrgTermMonitorDataMap> c = r.getValue();
		appendBasicInfoData(content, r.getKey());
		for (OrgTermMonitorDataMap o : c) {
			Element el = xmlDoc.createElement("org");
			content.appendChild(el);
			el.setAttribute("orgid", o.getOrg().getIdString());
			if (o.getOrg().getStandardAreaCode() != null)
				el.setAttribute("areacode", o.getOrg().getStandardAreaCode());
			if (o.getOrg().getSimpleName() != null
					&& !o.getOrg().getSimpleName().trim().isEmpty())
				el.setAttribute("orgname", o.getOrg().getSimpleName().trim());
			else
				el.setAttribute("orgname", o.getOrg().getOrgName());
			Iterator<Integer> it = o.keySet().iterator();
			while (it.hasNext()) {
				Element ee = xmlDoc.createElement("manuf");
				el.appendChild(ee);
				int mid = it.next();
				CachedManuf manuf = CacheHelper.manufMap.get(mid);
				TermMonitorData d = o.get(mid);
				ee.setAttribute("manufid", Integer.toString(mid));
				ee.setAttribute("manufname", manuf.getManufName());
				ee.setAttribute("count", Long.toString(d.count));
				ee.setAttribute("trade_count", Long.toString(d.hasTradeCount));
				ee.setAttribute("alarm_count", Long.toString(d.alarmCount));
				ee.setAttribute("online_count", Long.toString(d.onlineCount));
				ee.setAttribute("enabled_count", Long.toString(d.enabledCount));
				getFaultTermCount(session,request,ee,mid);
				//add by snail
				//ee.setAttribute("", d.);
			}
		}
	}
	
	//--------------------------add by snail---------------------
	private void getFaultTermCount(AdminSessionObject session,HttpRequest request,Element content,Integer manufid) throws NamingException, InterruptedException, IOException, InstantiationException, IllegalAccessException{
		Integer orgId = request.getParameterIntDef("orgid", null), manufId = request
		.getParameterIntDef("manufid", null);
		int termStatus = request.getParameterIntDef("termstatus", 0);
		int alarmStatus = request.getParameterIntDef("alarmstatus", 7);
		int onlineStatus = request.getParameterIntDef("onlinestatus", 3);
		int page = request.getParameterIntDef("page", 1);
		String filter = request.getParameterDef("filter", null);
		if (filter != null && filter.trim().isEmpty())
			filter = null;
		boolean includeDeviceStatusList = request.getParameterBooleanDef(
				"returndevicestatus", false);
		boolean includeChildren = request.getParameterBooleanDef(
				"includechildren", true);
		if (!((AdminSessionObject) session).getLoginUser().getUserGroup()
				.isSystemManager()) {
			if (((AdminSessionObject) session).getLoginUser().getManuf() != null)
				manufId = ((AdminSessionObject) session).getLoginUser()
						.getManuf().getId();
			if (orgId == null)
				orgId = ((AdminSessionObject) session).getLoginUser().getOrg()
						.getId();
		}
		SettlementType settlementType = null;
		Integer type1 = request.getParameterIntDef("settlement_type", null);
		if (type1 != null)
			settlementType = SettlementType.valueOfIntString(type1);
		KeyValue<BaseMonitorData, CacheTermMonitorResult> r = MonitorHelper
				.getMonitorTermList(orgId, manufId, includeChildren,
						termStatus, alarmStatus, onlineStatus, settlementType,
						page, includeDeviceStatusList, filter);
		CacheTermMonitorResult c = r.getValue();
		content.setAttribute("fault_count", Integer.toString(c.getFalutCount()));
	}
	//----------------------------end-------------------------------

	private void getAdminBasicData(AdminSessionObject session,
			HttpRequest request, Document xmlDoc, Element content) {
		try {
			appendBasicInfoData(content, MonitorHelper.getBasicMonitorData());
			Integer orgId = (user.getUserGroup().isSystemManager()
					|| user.getOrg() == null || user.getOrg().getOrgId() == null) ? null
					: user.getOrg().getOrgId();
			Collection<QueryedOrg> c = AdminHelper.getOrgTreeItems(orgId, 2,
					null, false, null);
			Element p = xmlDoc.createElement("orgtree");
			content.appendChild(p);
			for (QueryedOrg org : c) {
				Element el = xmlDoc.createElement("treenode");
				p.appendChild(el);
				el.setAttribute("id", org.getIdString());
				el.setAttribute("depth", Integer.toString(org.getDepth()));
				el.setAttribute("haschildren", org.isHasChildren() ? "true"
						: "false");
				el.setAttribute("simplename", org.getSimpleName());
				el.setTextContent(org.getOrgName());
				if (org.getStandardAreaCode() != null)
					el.setAttribute("standardareacode",
							org.getStandardAreaCode());
			}
			p = xmlDoc.createElement("payway");
			content.appendChild(p);
			for (CachedPayWay o : CacheHelper.payWayMap.values()) {
				Element el = xmlDoc.createElement("item");
				p.appendChild(el);
				el.setAttribute("id", o.getIdString());
				el.setAttribute("text", o.getPayWayDesp());
			}
			p = xmlDoc.createElement("payitem");
			content.appendChild(p);
			for (CachedPayItem o : CacheHelper.payItemMap.values()) {
				Element el = xmlDoc.createElement("item");
				p.appendChild(el);
				el.setAttribute("id", o.getIdString());
				el.setAttribute("text", o.getPayItemDesp());
			}
			p = xmlDoc.createElement("manuf");
			content.appendChild(p);
			for (CachedManuf o : CacheHelper.manufMap.values()) {
				Element el = xmlDoc.createElement("item");
				p.appendChild(el);
				el.setAttribute("id", o.getIdString());
				el.setAttribute("text", o.getManufName());
			}
			p = xmlDoc.createElement("businesscategory");
			content.appendChild(p);
			for (CachedBusinessCategory o : CacheHelper.businessCategoryMap
					.values()) {
				Element el = xmlDoc.createElement("item");
				p.appendChild(el);
				el.setAttribute("id", o.getIdString());
				el.setAttribute("text", o.getBusinessCategoryDesp());
			}
			p = xmlDoc.createElement("business");
			content.appendChild(p);
			for (CachedBusiness o : CacheHelper.businessMap.values()) {
				Element el = xmlDoc.createElement("item");
				p.appendChild(el);
				el.setAttribute("id", o.getIdString());
				el.setAttribute("categoryid",
						Integer.toString(o.getBusinessCategoryId()));
				el.setAttribute("text", o.getBusinessDesp());
			}
			p = xmlDoc.createElement("tradecode");
			content.appendChild(p);
			for (CachedTradeCode o : CacheHelper.tradeCodeMap.values()) {
				Element el = xmlDoc.createElement("item");
				p.appendChild(el);
				el.setAttribute("id", o.getIdString());
				el.setAttribute("text", o.getTradeDesp());
				el.setAttribute("businessid",
						Integer.toString(o.getBusinessId()));
				if (o.getPayWayId() != null)
					el.setAttribute("payway", Integer.toString(o.getPayWayId()));
				if (o.getPayItemId() != null)
					el.setAttribute("payitem",
							Integer.toString(o.getPayItemId()));
			}
			p = xmlDoc.createElement("comminterface");
			content.appendChild(p);
			for (CachedCommInterface o : CacheHelper.commInterfaceMap.values()) {
				Element el = xmlDoc.createElement("item");
				p.appendChild(el);
				el.setAttribute("id", o.getIdString());
				el.setAttribute("text", o.getDesp());
				el.setAttribute("type", Integer.toString(o.getType()));
			}

		} catch (NamingException e) {
			throw new KoneException(e);
		}
	}

	protected void appendBasicInfoData(Element content, BaseMonitorData data) {
		Element el = content.getOwnerDocument().createElement("monitorinfo");
		content.appendChild(el);
		el.setAttribute("termcount", Long.toString(data.getTermCount()));
		el.setAttribute("tradecount", Long.toString(data.getTradeCount()));
		el.setAttribute("suctradecount", Long.toString(data.getSucTradeCount()));
		el.setAttribute("trademoney", Long.toString(data.getTradeMoney()));
	}

	protected void appendCountElement(boolean firstQuery, Element content,
			Document document, int count) {
		if (firstQuery) {
			Element el = document.createElement("count");
			el.setAttribute("value", Integer.toString(count));
			content.appendChild(el);
		}
	}

}
