package kxd.engine.scs.admin.drivers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.naming.NamingException;
import javax.servlet.http.HttpServletResponse;

import kxd.engine.cache.beans.sts.CacheTermMonitorResult;
import kxd.engine.cache.beans.sts.CachedApp;
import kxd.engine.cache.beans.sts.CachedBusiness;
import kxd.engine.cache.beans.sts.CachedBusinessCategory;
import kxd.engine.cache.beans.sts.CachedManuf;
import kxd.engine.cache.beans.sts.CachedPayItem;
import kxd.engine.cache.beans.sts.CachedPayWay;
import kxd.engine.cache.beans.sts.CachedTerm;
import kxd.engine.cache.beans.sts.CachedTermCommand;
import kxd.engine.cache.beans.sts.CachedTermType;
import kxd.engine.cache.beans.sts.CachedTradeCode;
import kxd.engine.cache.beans.sts.MonitoredDeviceStatus;
import kxd.engine.fileservice.FileProcessor;
import kxd.engine.helper.AdminHelper;
import kxd.engine.helper.CacheHelper;
import kxd.engine.helper.MonitorHelper;
import kxd.engine.scs.admin.AdminSessionObject;
import kxd.engine.scs.admin.AdminTradeDriver;
import kxd.engine.scs.admin.AdminTradeExecutor;
import kxd.engine.scs.admin.util.UserRight;
import kxd.engine.scs.monitor.MonitoredTermResult;
import kxd.engine.ui.tags.website.FuncTreeNode;
import kxd.json.JSONArray;
import kxd.json.JSONException;
import kxd.json.JSONObject;
import kxd.net.HttpRequest;
import kxd.net.connection.jndi.Lookuper;
import kxd.net.naming.LoopNamingContext;
import kxd.remote.scs.beans.BaseAlarmCategory;
import kxd.remote.scs.beans.BaseAlarmCode;
import kxd.remote.scs.beans.BaseAppFile;
import kxd.remote.scs.beans.BaseBusinessCategory;
import kxd.remote.scs.beans.BaseCommInterface;
import kxd.remote.scs.beans.BaseDevice;
import kxd.remote.scs.beans.BaseDeviceDriver;
import kxd.remote.scs.beans.BaseDeviceDriverFile;
import kxd.remote.scs.beans.BaseDeviceTypeDriver;
import kxd.remote.scs.beans.BaseDisabledPrintUser;
import kxd.remote.scs.beans.BaseFileCategory;
import kxd.remote.scs.beans.BaseFileHost;
import kxd.remote.scs.beans.BaseFileOwner;
import kxd.remote.scs.beans.BaseFileUser;
import kxd.remote.scs.beans.BaseInfoFile;
import kxd.remote.scs.beans.BaseMonitorData;
import kxd.remote.scs.beans.BaseOrgBusinessOpenClose;
import kxd.remote.scs.beans.BasePayWay;
import kxd.remote.scs.beans.BasePrintType;
import kxd.remote.scs.beans.BaseRole;
import kxd.remote.scs.beans.BaseTermBusinessOpenClose;
import kxd.remote.scs.beans.BaseTermMoveItem;
import kxd.remote.scs.beans.BaseTermType;
import kxd.remote.scs.beans.BaseTermTypeDevice;
import kxd.remote.scs.beans.appdeploy.EditedApp;
import kxd.remote.scs.beans.appdeploy.EditedAppCategory;
import kxd.remote.scs.beans.appdeploy.EditedBusiness;
import kxd.remote.scs.beans.appdeploy.EditedPageElement;
import kxd.remote.scs.beans.appdeploy.EditedPayItem;
import kxd.remote.scs.beans.appdeploy.EditedTradeCode;
import kxd.remote.scs.beans.device.EditedBankTerm;
import kxd.remote.scs.beans.device.EditedDevice;
import kxd.remote.scs.beans.device.EditedDeviceType;
import kxd.remote.scs.beans.device.EditedTermType;
import kxd.remote.scs.beans.device.EditedTermTypeDevice;
import kxd.remote.scs.beans.device.QueryedDevice;
import kxd.remote.scs.beans.device.QueryedTerm;
import kxd.remote.scs.beans.right.EditedManuf;
import kxd.remote.scs.beans.right.EditedUser;
import kxd.remote.scs.beans.right.LoginUser;
import kxd.remote.scs.beans.right.QueryedOrg;
import kxd.remote.scs.interfaces.AlarmCategoryBeanRemote;
import kxd.remote.scs.interfaces.AppBeanRemote;
import kxd.remote.scs.interfaces.AppCategoryBeanRemote;
import kxd.remote.scs.interfaces.BankTermBeanRemote;
import kxd.remote.scs.interfaces.BusinessBeanRemote;
import kxd.remote.scs.interfaces.BusinessCategoryBeanRemote;
import kxd.remote.scs.interfaces.CommInterfaceBeanRemote;
import kxd.remote.scs.interfaces.DeviceBeanRemote;
import kxd.remote.scs.interfaces.DeviceDriverBeanRemote;
import kxd.remote.scs.interfaces.DeviceTypeBeanRemote;
import kxd.remote.scs.interfaces.DeviceTypeDriverBeanRemote;
import kxd.remote.scs.interfaces.DisabledPrintUserBeanRemote;
import kxd.remote.scs.interfaces.FileCategoryBeanRemote;
import kxd.remote.scs.interfaces.FileHostBeanRemote;
import kxd.remote.scs.interfaces.FileOwnerBeanRemote;
import kxd.remote.scs.interfaces.FileUserBeanRemote;
import kxd.remote.scs.interfaces.InfoBeanRemote;
import kxd.remote.scs.interfaces.ManufBeanRemote;
import kxd.remote.scs.interfaces.OrgBeanRemote;
import kxd.remote.scs.interfaces.OrgBusinessOpenCloseRemote;
import kxd.remote.scs.interfaces.PageElementBeanRemote;
import kxd.remote.scs.interfaces.PayItemBeanRemote;
import kxd.remote.scs.interfaces.PayWayBeanRemote;
import kxd.remote.scs.interfaces.PrintTypeBeanRemote;
import kxd.remote.scs.interfaces.RoleBeanRemote;
import kxd.remote.scs.interfaces.SettlementBeanRemote;
import kxd.remote.scs.interfaces.TermBeanRemote;
import kxd.remote.scs.interfaces.TermBusinessOpenCloseRemote;
import kxd.remote.scs.interfaces.TermTypeBeanRemote;
import kxd.remote.scs.interfaces.TradeCodeBeanRemote;
import kxd.remote.scs.interfaces.UserBeanRemote;
import kxd.remote.scs.interfaces.service.TermServiceBeanRemote;
import kxd.remote.scs.util.QueryResult;
import kxd.remote.scs.util.emun.AlarmStatus;
import kxd.remote.scs.util.emun.PayWayType;
import kxd.remote.scs.util.emun.RefundStatus;
import kxd.remote.scs.util.emun.SettlementType;
import kxd.remote.scs.util.emun.TermStatus;
import kxd.remote.scs.util.emun.TradeStatus;
import kxd.util.DataUnit;
import kxd.util.DateTime;
import kxd.util.KeyValue;
import kxd.util.KoneException;
import kxd.util.SimpleTreeNode;
import kxd.util.StringUnit;
import kxd.util.TreeNode;
import kxd.util.memcached.MemCachedClient;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class DefaultAdminTradeDriver implements AdminTradeDriver {

	@Override
	public void execute(AdminSessionObject session, HttpRequest request,
			HttpServletResponse response, Document xmlDoc, Element content,
			Element result) throws Throwable {
		String cmdgroup = request.getParameterDef("cmdgroup", "");
		String cmd = request.getParameter("cmd");
		if (cmdgroup.equals("query")) {
			executeQuery(session, cmd, request, response, xmlDoc, content,
					result);
		} else if (cmd.equals("deletecache")) {
			executeDeleteCache(session, request, response, xmlDoc, content,
					result);
		} else if (cmdgroup.equals("delete")) {
			executeDelete(session, cmd, request, response, xmlDoc, content,
					result);
		} else if (cmd.equals("getdevicetypealarms")) {
			getDeviceTypeAlarms(session, request.getParameter("id"), xmlDoc,
					content);
		} else if (cmd.equals("gettermtypedevices")) {
			getTermTypeDevices(session, request.getParameter("id"), xmlDoc,
					content);
		} else if (cmd.equals("gettermmovelist")) {
			getTermMoveList(session, request.getParameter("id"), xmlDoc,
					content);
		} else if (cmd.equals("addtermtypedevice")) {
			addTermTypeDevice(session, request, xmlDoc, content);
		} else if (cmd.equals("edittermtypedevice")) {
			editTermTypeDevice(session, request, xmlDoc, content);
		} else if (cmd.equals("edituserorgs")) {
			editUserResources(0, session, request, xmlDoc, content);
		} else if (cmd.equals("edituserterms")) {
			editUserResources(2, session, request, xmlDoc, content);
		} else if (cmd.equals("edituserroles")) {
			editUserResources(1, session, request, xmlDoc, content);
		} else if (cmd.equals("getappfiles")) {
			getAppFiles(session, request.getParameter("id"), xmlDoc, content);
		} else if (cmd.equals("getdevicedriverfiles")) {
			getDeviceDriverFiles(session, request.getParameter("id"), xmlDoc,
					content);
		} else if (cmd.equals("getorgchildren")) {
			getOrgChildren(session, request.getParameterIntDef("id", null),
					request.getParameterIntDef("depth", 1), request, xmlDoc,
					content);
		} else if (cmd.equals("getadminbasicdata")) {
			getAdminBasicData(session, request, xmlDoc, content);
		} else if (cmd.equals("monitortermlist")) {
			monitorTermList(session, request, xmlDoc, content);
		} else if (cmd.equals("gettermdevicestatus")) {
			getTermDeviceStatus(session, request, xmlDoc, content);
		} else if (cmd.equals("sendtermcommand")) {
			sendTermCommand(session, request, xmlDoc, content);
		} else if (cmd.equals("sendtermscommand")) {// 新增加的终端操作
			sendtermscommand(session, request, xmlDoc, content);
		} else if (cmd.equals("returnmoney")) {
			returnMoney(session, request, xmlDoc, content);
		} else if (cmd.equals("cancelreturnmoney")) {
			cancelReturnMoney(session, request, xmlDoc, content);
		} else if (cmd.equals("proclist")) {
			procListMoney(session, request, xmlDoc, content);
		} else if (cmd.equals("getinfofiles")) {
			getInfoFileList(session, request.getParameter("id"),
					request.getParameterIntDef("filetype", null), xmlDoc,
					content);
		} else if (cmd.equals("getuserorgtree")) { // 获取用户具有权限的机构树
			getUserOrgTreeList(session, request, xmlDoc, content);
		} else if (cmd.equals("getbusinesstree")) { // 获取业务类别树
			getBusinessTreeList(session, request, xmlDoc, content);
		} else
			throw new KoneException("无效的交易代码");
	}

	private void getAdminBasicData(AdminSessionObject session,
			HttpRequest request, Document xmlDoc, Element content) {
		try {
			Collection<QueryedOrg> c = AdminHelper.getOrgTreeItems(null, 2,
					null, false, null);
			Element p = xmlDoc.createElement("orgtree");
			content.appendChild(p);
			for (QueryedOrg org : c) {
				Element el = xmlDoc.createElement("treenode");
				p.appendChild(el);
				el.setAttribute("id", org.getIdString());
				el.setAttribute("depth", Integer.toString(org.getDepth()));
				el.setAttribute("haschildren", !org.isHasChildren() ? "true"
						: "false");
				el.setTextContent(org.getOrgName());
				if (org.getStandardAreaCode() != null)
					el.setAttribute("standardareacode",
							org.getStandardAreaCode());
			}
			p = xmlDoc.createElement("functree");
			content.appendChild(p);
			p.setAttribute("userfunclist", session.getLoginUser()
					.getFuncCollection().toString());
			List<Object> ls = new ArrayList<Object>();
			AdminSessionObject.getFuncTree().getAll(ls);
			for (Object o : ls) {
				FuncTreeNode node = (FuncTreeNode) o;
				if (node.getId() != null) {
					Element el = xmlDoc.createElement("functreeitem");
					p.appendChild(el);
					el.setAttribute("id", node.getIdString());
					el.setAttribute("customenabled", node.getFunction()
							.isCustomEnabled() ? "true" : "false");
					el.setAttribute("desp", node.getFunction().getFuncDesp());
					el.setAttribute("depth", node.getFunction().getFuncDepth()
							.toString());
					el.setAttribute("usergroup", node.getFunction()
							.getUserGroup().getValue()
							+ "");

				}
			}
			// session.getFuncLoginTreedata();
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

		} catch (NamingException e) {
			throw new KoneException(e);
		}
	}

	public Document executeDeleteCache(AdminSessionObject session,
			HttpRequest request, HttpServletResponse response, Document xmlDoc,
			Element content, Element result) throws Throwable {
		String name = request.getParameter("name");
		MemCachedClient mc = MemCachedClient.getInstance(name);
		if (mc == null)
			throw new Exception("名称为[" + name + "]的缓存配置不存在");
		String key = request.getParameterDef("key", null);
		if (key == null) {
			mc.flushAll();
		} else {
			mc.delete(key);
		}
		return null;
	}

	protected void procListMoney(AdminSessionObject session,
			HttpRequest request, Document xmlDoc, Element content)
			throws NamingException, NoSuchFieldException {
		if (!((AdminSessionObject) session).hasRight(UserRight.TRADEPROC))
			throw new KoneException("您没有查看处理记录的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			SettlementBeanRemote bean = context
					.lookup(Lookuper.JNDI_TYPE_EJB,
							"kxd-ejb-settlementServiceBean",
							SettlementBeanRemote.class);
			Iterator<?> it = bean.queryTradeProcList(
					((AdminSessionObject) session).getLoginUser().getId(),
					request.getParameter("termglide")).iterator();
			while (it.hasNext()) {
				Object[] t = (Object[]) it.next();
				Element e = xmlDoc.createElement("procitem");
				content.appendChild(e);
				e.setAttribute("usercode", t[0].toString());
				e.setAttribute("adjusttime", t[1].toString());
				e.setAttribute("adjustmoney", t[2].toString());
				e.setAttribute("adjustreason", t[3].toString());
			}
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void returnMoney(AdminSessionObject session, HttpRequest request,
			Document xmlDoc, Element content) throws NamingException,
			NoSuchFieldException {
		boolean hasReturnMoneyToday = ((AdminSessionObject) session)
				.hasRight(UserRight.RETURN_MONEY_TODAY);
		boolean hasReturnMoneyNextday = ((AdminSessionObject) session)
				.hasRight(UserRight.RETURN_MONEY_NEXTDAY);
		boolean hasReturnMoneyNextMonth = ((AdminSessionObject) session)
				.hasRight(UserRight.RETURN_MONEY_NEXTMONTH);
		boolean hasSuccessReturn = ((AdminSessionObject) session)
				.hasRight(UserRight.SUCCESS_TRADE_RETURN);
		boolean hasUnionPayReturn = ((AdminSessionObject) session)
				.hasRight(UserRight.UNIONPAY_TRADE_RETURN);
		boolean hasCancelReturnMoneyToday = ((AdminSessionObject) session)
				.hasRight(UserRight.UNDO_RETURN_MONEY_TODAY);
		if (!(hasReturnMoneyToday || hasReturnMoneyNextday
				|| hasReturnMoneyNextMonth || hasSuccessReturn || hasUnionPayReturn))
			throw new KoneException("您没有退款的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			SettlementBeanRemote bean = context
					.lookup(Lookuper.JNDI_TYPE_EJB,
							"kxd-ejb-settlementServiceBean",
							SettlementBeanRemote.class);
			Object o = bean.returnMoney(((AdminSessionObject) session)
					.getLoginUser().getId(), hasReturnMoneyToday,
					hasReturnMoneyNextday, hasReturnMoneyNextMonth,
					hasSuccessReturn, hasUnionPayReturn, request
							.getParameter("termglide"), request
							.getParameterInt("money"), request
							.getParameter("reason"));
			int[] r = StringUnit.splitToInt((String) o, ",");
			RefundStatus status = RefundStatus.valueOf(r[0]);
			CachedTradeCode code = CacheHelper.tradeCodeMap.get(r[1]);
			TradeStatus tradeStatus = TradeStatus.valueOf(r[2]);
			// PayStatus payStatus = PayStatus.valueOf(r[3]);
			boolean enabled = hasCancelReturnMoneyToday;
			if (enabled
					&& tradeStatus.getValue() >= TradeStatus.TRADE_SUCCESS
							.getValue()
					&& tradeStatus.getValue() < TradeStatus.TRADE_TIMEOUT
							.getValue() && !hasSuccessReturn) // 无成功交易处理权限
				enabled = false;
			if (enabled
					&& code.getPayWay().getType().equals(PayWayType.UNIONPAY)
					&& !hasUnionPayReturn) // 无银联处理权限
				enabled = false;
			content.setAttribute("cancelenabled", enabled ? "true" : "false");
			content.setAttribute("refundstatuscode",
					Integer.toString(status.getValue()));
			content.setAttribute("result", status.toString());
		} finally {
			context.close();
		}
	}

	protected void cancelReturnMoney(AdminSessionObject session,
			HttpRequest request, Document xmlDoc, Element content)
			throws Throwable {
		boolean hasCancelReturnMoneyToday = ((AdminSessionObject) session)
				.hasRight(UserRight.UNDO_RETURN_MONEY_TODAY);
		boolean hasCancelReturnMoneyNextday = ((AdminSessionObject) session)
				.hasRight(UserRight.UNDO_RETURN_MONEY_NEXTDAY);
		boolean hasCancelReturnMoneyNextMonth = ((AdminSessionObject) session)
				.hasRight(UserRight.UNDO_RETURN_MONEY_NEXTMONTH);
		boolean hasReturnMoneyToday = ((AdminSessionObject) session)
				.hasRight(UserRight.RETURN_MONEY_TODAY);
		boolean hasReturnMoneyNextday = ((AdminSessionObject) session)
				.hasRight(UserRight.RETURN_MONEY_NEXTDAY);
		boolean hasReturnMoneyNextMonth = ((AdminSessionObject) session)
				.hasRight(UserRight.RETURN_MONEY_NEXTMONTH);
		boolean hasSuccessReturn = ((AdminSessionObject) session)
				.hasRight(UserRight.SUCCESS_TRADE_RETURN);
		boolean hasUnionPayReturn = ((AdminSessionObject) session)
				.hasRight(UserRight.UNIONPAY_TRADE_RETURN);
		if (!(hasCancelReturnMoneyToday || hasCancelReturnMoneyNextday || hasCancelReturnMoneyNextMonth))
			throw new KoneException("您没有退款的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			SettlementBeanRemote bean = context
					.lookup(Lookuper.JNDI_TYPE_EJB,
							"kxd-ejb-settlementServiceBean",
							SettlementBeanRemote.class);
			Object o = bean.cancelOption(((AdminSessionObject) session)
					.getLoginUser().getId(), hasCancelReturnMoneyToday,
					hasCancelReturnMoneyNextday, hasCancelReturnMoneyNextMonth,
					request.getParameter("termglide"), request
							.getParameter("reason"));
			String[] r = StringUnit.split((String) o, ",");
			CachedTradeCode code = CacheHelper.tradeCodeMap.get(Integer
					.valueOf(r[0]));
			TradeStatus tradeStatus = TradeStatus.valueOfIntString(r[1]);
			// PayStatus payStatus = PayStatus.valueOfIntString(r[2]);
			DateTime tradeTime = new DateTime(r[3], "yyyy-MM-dd HH:mm:ss");
			DateTime now = new DateTime();
			int toDay = now.getFullDay();
			int month = now.getFullMonth();
			boolean nextDay = tradeTime.getFullDay() != toDay;
			boolean nextMonth = tradeTime.getFullMonth() != month;
			boolean refundEnabled = true;
			boolean success = tradeStatus.getValue() >= TradeStatus.TRADE_SUCCESS
					.getValue()
					&& tradeStatus.getValue() < TradeStatus.TRADE_TIMEOUT
							.getValue();
			if (success && !hasSuccessReturn) // 检查成功交易退款权限
				refundEnabled = false;
			if (refundEnabled
					&& code.getPayWay().getType().equals(PayWayType.UNIONPAY) // 检查银联交易退款权限
					&& !hasUnionPayReturn)
				refundEnabled = false;
			if (refundEnabled) {
				if (nextMonth) {
					refundEnabled = hasReturnMoneyNextMonth
							&& (code.getRefundMode() & 4) == 4;
				} else if (nextDay) {
					refundEnabled = hasReturnMoneyNextday
							&& (code.getRefundMode() & 2) == 2;
				} else {
					refundEnabled = hasReturnMoneyToday
							&& (code.getRefundMode() & 1) == 1;
				}
			}
			content.setAttribute("refundenabled", refundEnabled ? "true"
					: "false");
		} finally {
			context.close();
		}
	}

	public Document executeDelete(AdminSessionObject session, String cmd,
			HttpRequest request, HttpServletResponse response, Document xmlDoc,
			Element content, Element result) throws Throwable {
		String id = request.getParameter("id");
		if (cmd.equals("deleteappcategory")) {
			deleteAppCategory(session, id);
		} else if (cmd.equals("deleteapp")) {
			deleteApp(session, id);
		} else if (cmd.equals("deletebusiness")) {
			deleteBusiness(session, id);
		} else if (cmd.equals("deletebusinesscategory")) {
			deleteBusinessCategory(session, id);
		} else if (cmd.equals("deletepageelement")) {
			deletePageElement(session, id);
		} else if (cmd.equals("deletepayitem")) {
			deletePayItem(session, id);
		} else if (cmd.equals("deletepayway")) {
			deletePayWay(session, id);
		} else if (cmd.equals("deletetradecode")) {
			deleteTradeCode(session, id);
		} else if (cmd.equals("deletedevicetypedriver")) {
			deleteDeviceTypeDriver(session, id);
		} else if (cmd.equals("deletedevicedriver")) {
			deleteDeviceDriver(session, id);
		} else if (cmd.equals("deletedevice")) {
			deleteDevice(session, id);
		} else if (cmd.equals("deletedevicetype")) {
			deleteDeviceType(session, id);
		} else if (cmd.equals("deletetermtypedevice")) {
			deleteTermTypeDevice(session, id, request.getParameter("deviceid"));
		} else if (cmd.equals("deleteterm")) {
			deleteTerm(session, id);
		} else if (cmd.equals("deleteBankTerm")) {
			deleteBankTerm(session, id);
		} else if (cmd.equals("deletetermtype")) {
			deleteTermType(session, id);
		} else if (cmd.equals("deletemanuf")) {
			deleteManuf(session, id);
		} else if (cmd.equals("deleteorg")) {
			deleteOrg(session, id);
		} else if (cmd.equals("deleterole")) {
			deleteRole(session, id);
		} else if (cmd.equals("deleteuser")) {
			deleteUser(session, id);
		} else if (cmd.equals("deletebankterm")) {
			deleteBankTerm(session, id);
		} else if (cmd.equals("deletetermtype")) {
			deleteTermType(session, id);
		} else if (cmd.equals("deleteprinttype")) {
			deletePrintType(session, id);
		} else if (cmd.equals("deletedisabledprintuser")) {
			deleteDisabledPrintUser(session, id);
		} else if (cmd.equals("deleteappfiles")) {
			deleteAppFiles(session, id);
		} else if (cmd.equals("deletedevicedriverfiles")) {
			deleteDeviceDriverFiles(session, id);
		} else if (cmd.equals("deletedevicetypealarms")) {
			deleteDeviceTypeAlarms(session,
					request.getParameterInt("devicetype"), id);
		} else if (cmd.equals("deletealarmcategory")) {
			deleteAlarmCategory(session, id);
		} else if (cmd.equals("deletefilecategory")) {
			deleteFileCategory(session, id);
		} else if (cmd.equals("deletefileowner")) {
			deleteFileOwner(session, id);
		} else if (cmd.equals("deletefilehost")) {
			deleteFileHost(session, id);
		} else if (cmd.equals("deletefileuser")) {
			deleteFileUser(session, id);
		} else if (cmd.equals("deleteorgbusinessopenclose")) {
			deleteOrgBusinessOpenClose(session, id);
		} else if (cmd.equals("deletetermbusinessopenclose")) {
			deleteTermBusinessOpenClose(session, id);
		} else
			throw new KoneException("无效的交易代码");
		return null;
	}

	protected void deleteTermBusinessOpenClose(AdminSessionObject session,
			String id) throws Throwable {
		if (!((AdminSessionObject) session)
				.hasRight(UserRight.TERMBUSINESSOPENCLOSE_EDIT))
			throw new KoneException("您没有删除业务停开配置的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			TermBusinessOpenCloseRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-termBusinessOpenCloseBean",
					TermBusinessOpenCloseRemote.class);
			bean.delete(((AdminSessionObject) session).getLoginUser()
					.getUserId(), StringUnit.splitToLong2(id, ","));
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void deleteOrgBusinessOpenClose(AdminSessionObject session,
			String id) throws Throwable {
		if (!((AdminSessionObject) session)
				.hasRight(UserRight.ORGBUSINESSOPENCLOSE_EDIT))
			throw new KoneException("您没有删除业务停开配置的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			OrgBusinessOpenCloseRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-orgBusinessOpenCloseBean",
					OrgBusinessOpenCloseRemote.class);
			bean.delete(((AdminSessionObject) session).getLoginUser()
					.getUserId(), StringUnit.splitToLong2(id, ","));
		} finally {
			if (context != null)
				context.close();
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
		if (cmd.equals("querycomminterfacelist")) {
			queryCommInterfaceList(firstQuery, keyword, maxresults, page,
					session, request, xmlDoc, content);
		} else if (cmd.equals("queryappcategorylist")) {
			queryAppCategoryList(firstQuery, keyword, maxresults, page,
					session, request, xmlDoc, content);
		} else if (cmd.equals("queryuserlist")) {
			queryUserList(firstQuery, keyword, maxresults, page, session,
					request, xmlDoc, content);
		} else if (cmd.equals("queryapplist")) {
			queryAppList(firstQuery, keyword, maxresults, page, session,
					request, xmlDoc, content);
		} else if (cmd.equals("querypaywaylist")) {
			queryPayWayList(firstQuery, keyword, maxresults, page, session,
					request, xmlDoc, content);
		} else if (cmd.equals("querypayitemlist")) {
			queryPayItemList(firstQuery, keyword, maxresults, page, session,
					request, xmlDoc, content);
		} else if (cmd.equals("querybusinesscategorylist")) {
			queryBusinessCategoryList(firstQuery, keyword, maxresults, page,
					session, request, xmlDoc, content);
		} else if (cmd.equals("querybusinesslist")) {
			queryBusinessList(firstQuery, keyword, maxresults, page, session,
					request, xmlDoc, content);
		} else if (cmd.equals("querypageelementlist")) {
			queryPageElementList(firstQuery, keyword, maxresults, page,
					session, request, xmlDoc, content);
		} else if (cmd.equals("querytradecodelist")) {
			queryTradeCodeList(firstQuery, keyword, maxresults, page, session,
					request, xmlDoc, content);
		} else if (cmd.equals("queryrolelist")) {
			queryRoleList(firstQuery, keyword, maxresults, page, session,
					request, xmlDoc, content);
		} else if (cmd.equals("querymanuflist")) {
			queryManufList(firstQuery, keyword, maxresults, page, session,
					request, xmlDoc, content);
		} else if (cmd.equals("querydevicetypedriverlist")) {
			queryDeviceTypeDriverList(firstQuery, keyword, maxresults, page,
					session, request, xmlDoc, content);
		} else if (cmd.equals("querydevicedriverlist")) {
			queryDeviceDriverList(firstQuery, keyword, maxresults, page,
					session, request, xmlDoc, content);
		} else if (cmd.equals("querydevicetypelist")) {
			queryDeviceTypeList(firstQuery, keyword, maxresults, page, session,
					request, xmlDoc, content);
		} else if (cmd.equals("querydevicelist")) {
			queryDeviceList(firstQuery, keyword, maxresults, page, session,
					request, xmlDoc, content);
		} else if (cmd.equals("querybanktermlist")) {
			queryBankTermList(firstQuery, keyword, maxresults, page, session,
					request, xmlDoc, content);
		} else if (cmd.equals("querytermtypelist")) {
			queryTermTypeList(firstQuery, keyword, maxresults, page, session,
					request, xmlDoc, content);
		} else if (cmd.equals("querytermlist")) {
			if (!((AdminSessionObject) session).hasRight(UserRight.TERM))
				throw new KoneException("您没有查询终端的权限.");
			queryTermList(null, null, firstQuery, keyword, maxresults, page,
					session, request, xmlDoc, content);
		} else if (cmd.equals("queryadtermlist")) {
			if (!((AdminSessionObject) session).hasRight(UserRight.TERMAD))
				throw new KoneException("您没有查询终端广告的权限.");
			String t = null, w = null;
			if (request.getParameterDef("hasadterm", "").equals("on")) {
				t = "termad f";
				w = "a.termid=f.termid";
			}
			queryTermList(t, w, firstQuery, keyword, maxresults, page, session,
					request, xmlDoc, content);
		} else if (cmd.equals("querybusinessopenclosetermlist")) {
			if (!((AdminSessionObject) session)
					.hasRight(UserRight.TERMBUSINESSOPENCLOSE))
				throw new KoneException("您没有查询终端业务开停配置的权限.");
			String t = null, w = null;
			if (request.getParameterDef("hasconfigterm", "").equals("on")) {
				t = "termbusinessclose f";
				w = "a.termid=f.termid";
			}
			queryTermList(t, w, firstQuery, keyword, maxresults, page, session,
					request, xmlDoc, content);
		} else if (cmd.equals("queryprinttypelist")) {
			queryPrintTypeList(firstQuery, keyword, maxresults, page, session,
					request, xmlDoc, content);
		} else if (cmd.equals("queryalarmcategorylist")) {
			queryAlarmCategoryList(firstQuery, keyword, maxresults, page,
					session, request, xmlDoc, content);
		} else if (cmd.equals("querydisabledprintuserlist")) {
			queryDisabledPrintUserList(firstQuery, keyword, maxresults, page,
					session, request, xmlDoc, content);
		} else if (cmd.equals("queryfilecategorylist")) {
			queryFileCategoryList(firstQuery, keyword, maxresults, page,
					session, request, xmlDoc, content);
		} else if (cmd.equals("queryfileownerlist")) {
			queryFileOwnerList(firstQuery, keyword, maxresults, page, session,
					request, xmlDoc, content);
		} else if (cmd.equals("queryfilehostlist")) {
			queryFileHostList(firstQuery, keyword, maxresults, page, session,
					request, xmlDoc, content);
		} else if (cmd.equals("queryfileuserlist")) {
			queryFileUserList(firstQuery, keyword, maxresults, page, session,
					request, xmlDoc, content);
		} else if (cmd.equals("queryorgbusinessopencloselist")) {
			queryOrgBusinessOpenCloseList(firstQuery, keyword, maxresults,
					page, session, request, xmlDoc, content);
		} else if (cmd.equals("querytermbusinessopencloselist")) {
			queryTermBusinessOpenCloseList(firstQuery, session, request,
					xmlDoc, content);
		} else
			throw new KoneException("无效的交易代码");
		return null;
	}

	protected void appendCountElement(boolean firstQuery, Element content,
			Document document, int count) {
		if (firstQuery) {
			Element el = document.createElement("count");
			el.setAttribute("value", Integer.toString(count));
			content.appendChild(el);
		}
	}

	protected void queryTermBusinessOpenCloseList(boolean firstQuery,
			AdminSessionObject session, HttpRequest request, Document xmlDoc,
			Element content) throws Throwable {
		if (!((AdminSessionObject) session)
				.hasRight(UserRight.TERMBUSINESSOPENCLOSE))
			throw new KoneException("您没有查询终端业务停开配置的权限.");
		Integer termId = request.getParameterIntDef("termid", null);
		if (termId == null) {
			if (firstQuery) {
				Element el = xmlDoc.createElement("count");
				el.setAttribute("value", "0");
				content.appendChild(el);
			}
			return;
		}
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			TermBusinessOpenCloseRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-termBusinessOpenCloseBean",
					TermBusinessOpenCloseRemote.class);
			QueryResult<BaseTermBusinessOpenClose> r = bean.query(firstQuery,
					((AdminSessionObject) session).getLoginUser().getUserId(),
					termId, 0, null, 0, 0);
			appendCountElement(firstQuery, content, xmlDoc, r.getCount());
			Iterator<BaseTermBusinessOpenClose> it = r.getResultList()
					.iterator();
			while (it.hasNext()) {
				Element el = xmlDoc.createElement("record");
				BaseTermBusinessOpenClose o = (BaseTermBusinessOpenClose) it
						.next();
				el.setAttribute("id", Long.toString(o.getId()));
				el.setAttribute("startdate",
						DataUnit.formatDateTime(o.getStartDate(), "yyyy-MM-dd"));
				el.setAttribute("enddate",
						DataUnit.formatDateTime(o.getEndDate(), "yyyy-MM-dd"));
				el.setAttribute("time", o.getOpenTimes());
				el.setAttribute("category", o.getBusinessCategoryIds());
				el.setAttribute("buiness", o.getBusinessIds());
				el.setAttribute("open", o.getOpenMode().toString());
				content.appendChild(el);
			}
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void queryOrgBusinessOpenCloseList(boolean firstQuery,
			String keyword, int maxresults, int page,
			AdminSessionObject session, HttpRequest request, Document xmlDoc,
			Element content) throws Throwable {
		if (!((AdminSessionObject) session)
				.hasRight(UserRight.ORGBUSINESSOPENCLOSE))
			throw new KoneException("您没有查询业务停开配置的权限.");
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
			OrgBusinessOpenCloseRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-orgBusinessOpenCloseBean",
					OrgBusinessOpenCloseRemote.class);
			QueryResult<BaseOrgBusinessOpenClose> r = bean.query(firstQuery,
					((AdminSessionObject) session).getLoginUser().getUserId(),
					orgId, 0, keyword, page * maxresults, maxresults);
			appendCountElement(firstQuery, content, xmlDoc, r.getCount());
			Iterator<BaseOrgBusinessOpenClose> it = r.getResultList()
					.iterator();
			while (it.hasNext()) {
				Element el = xmlDoc.createElement("record");
				BaseOrgBusinessOpenClose o = (BaseOrgBusinessOpenClose) it
						.next();
				el.setAttribute("id", Long.toString(o.getId()));
				el.setAttribute("startdate",
						DataUnit.formatDateTime(o.getStartDate(), "yyyy-MM-dd"));
				el.setAttribute("enddate",
						DataUnit.formatDateTime(o.getEndDate(), "yyyy-MM-dd"));
				el.setAttribute("time", o.getOpenTimes());
				el.setAttribute("category", o.getBusinessCategoryIds());
				el.setAttribute("buiness", o.getBusinessIds());
				content.appendChild(el);
			}
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void queryTermList(String extTables, String extWheres,
			boolean firstQuery, String keyword, int maxresults, int page,
			AdminSessionObject session, HttpRequest request, Document xmlDoc,
			Element content) throws NamingException, NoSuchFieldException {
		Integer manufId = request.getParameterIntDef("manufid", null);
		Integer orgId = request.getParameterIntDef("orgid_value", null);
		if (orgId == null) {
			if (firstQuery) {
				Element el = xmlDoc.createElement("count");
				el.setAttribute("value", "0");
				content.appendChild(el);
			}
			return;
		}
		String bankTermCode = request.getParameterDef("banktermcode", "");
		String manufNo = request.getParameterDef("manufno", "");
		Integer typeid = request.getParameterIntDef("typeid", null);
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			TermBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-termBean", TermBeanRemote.class);

			QueryResult<QueryedTerm> r = bean.queryTerm(extTables, extWheres,
					firstQuery, ((AdminSessionObject) session).getLoginUser()
							.getUserId(), orgId, manufId, typeid, bankTermCode,
					manufNo, 0, keyword, page * maxresults, maxresults);
			appendCountElement(firstQuery, content, xmlDoc, r.getCount());
			Iterator<QueryedTerm> it = r.getResultList().iterator();
			while (it.hasNext()) {
				Element el = xmlDoc.createElement("record");
				QueryedTerm o = (QueryedTerm) it.next();
				el.setAttribute("id", Long.toString(o.getId()));
				el.setAttribute("code", o.getTermCode());
				el.setAttribute("desp", o.getTermDesp());
				el.setAttribute("type", o.getTermType().getDisplayLabel());
				el.setAttribute("org", o.getOrg().getOrgFullName());
				content.appendChild(el);
			}
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void queryTermTypeList(boolean firstQuery, String keyword,
			int maxresults, int page, AdminSessionObject session,
			HttpRequest request, Document xmlDoc, Element content)
			throws NamingException, NoSuchFieldException {
		if (!((AdminSessionObject) session).hasRight(UserRight.TERMTYPE))
			throw new KoneException("您没有查询终端型号的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		String desp = request.getParameterDef("desp", "");
		Integer appId = request.getParameterIntDef("appid", null);
		Integer manufId = request.getParameterIntDef("manufid", null);
		Integer cashFlag = request.getParameterIntDef("cashflag", null);
		Integer fixType = request.getParameterIntDef("fixtype", null);
		try {
			TermTypeBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-termTypeBean", TermTypeBeanRemote.class);
			QueryResult<EditedTermType> r = bean.queryTermType(firstQuery,
					((AdminSessionObject) session).getLoginUser().getUserId(),
					0, keyword, desp, appId, manufId, cashFlag, fixType, page
							* maxresults, maxresults);
			appendCountElement(firstQuery, content, xmlDoc, r.getCount());
			Iterator<EditedTermType> it = r.getResultList().iterator();
			while (it.hasNext()) {
				Element el = xmlDoc.createElement("record");
				EditedTermType o = (EditedTermType) it.next();
				el.setAttribute("id", Integer.toString(o.getId()));
				el.setAttribute("code", o.getTypeCode());
				el.setAttribute("desp", o.getTypeDesp());
				el.setAttribute("appdesp", o.getApp().getAppDesp());
				el.setAttribute("manufdesp", o.getManuf().getManufName());
				el.setAttribute("cashflag", o.getCashFlag().toString());
				el.setAttribute("fixtype", o.getFixType().toString());
				content.appendChild(el);
			}
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void queryBankTermList(boolean firstQuery, String keyword,
			int maxresults, int page, AdminSessionObject session,
			HttpRequest request, Document xmlDoc, Element content)
			throws NamingException, NoSuchFieldException {
		if (!((AdminSessionObject) session).hasRight(UserRight.BANKTERM))
			throw new KoneException("您没有查询银联终端的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			BankTermBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-bankTermBean", BankTermBeanRemote.class);
			QueryResult<EditedBankTerm> r = bean.queryBankTerm(firstQuery,
					((AdminSessionObject) session).getLoginUser().getUserId(),
					0, keyword, page * maxresults, maxresults);
			appendCountElement(firstQuery, content, xmlDoc, r.getCount());
			Iterator<EditedBankTerm> it = r.getResultList().iterator();
			while (it.hasNext()) {
				Element el = xmlDoc.createElement("record");
				EditedBankTerm o = (EditedBankTerm) it.next();
				el.setAttribute("id", Integer.toString(o.getId()));
				el.setAttribute("code", o.getBankTermCode());
				el.setAttribute("desp", o.getBankTermDesp());
				el.setAttribute("merchantaccount", o.getMerchantAccount());
				el.setAttribute("extfield", o.getExtField());
				content.appendChild(el);
			}
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void queryDeviceList(boolean firstQuery, String keyword,
			int maxresults, int page, AdminSessionObject session,
			HttpRequest request, Document xmlDoc, Element content)
			throws NamingException, NoSuchFieldException {
		if (!((AdminSessionObject) session).hasRight(UserRight.DEVICE))
			throw new KoneException("您没有查询设备的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		Integer typeId = request.getParameterIntDef("typeid", null);
		try {
			DeviceBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-deviceBean", DeviceBeanRemote.class);
			QueryResult<EditedDevice> r = bean.queryDevice(firstQuery, session
					.getLoginUser().getUserId(), 0, keyword, typeId, page
					* maxresults, maxresults);
			appendCountElement(firstQuery, content, xmlDoc, r.getCount());
			Iterator<EditedDevice> it = r.getResultList().iterator();
			while (it.hasNext()) {
				Element el = xmlDoc.createElement("record");
				QueryedDevice o = (QueryedDevice) it.next();
				el.setAttribute("id", Integer.toString(o.getId()));
				el.setAttribute("desp", o.getDeviceName());
				el.setAttribute("typedesp", o.getDeviceType()
						.getDeviceTypeDesp());
				el.setAttribute("driverdesp", o.getDriver()
						.getDeviceDriverDesp());
				content.appendChild(el);
			}
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void queryDeviceTypeList(boolean firstQuery, String keyword,
			int maxresults, int page, AdminSessionObject session,
			HttpRequest request, Document xmlDoc, Element content)
			throws NamingException, NoSuchFieldException {
		if (!((AdminSessionObject) session).hasRight(UserRight.DEVICETYPE))
			throw new KoneException("您没有查询模块的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		String code = request.getParameterDef("code", null);
		Integer deviceTypeDriverId = request.getParameterIntDef(
				"devicetypedriverid", null);
		try {
			DeviceTypeBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-deviceTypeBean", DeviceTypeBeanRemote.class);
			QueryResult<EditedDeviceType> r = bean.queryDeviceType(firstQuery,
					((AdminSessionObject) session).getLoginUser().getUserId(),
					0, keyword, code, deviceTypeDriverId, page * maxresults,
					maxresults);
			appendCountElement(firstQuery, content, xmlDoc, r.getCount());
			Iterator<EditedDeviceType> it = r.getResultList().iterator();
			while (it.hasNext()) {
				Element el = xmlDoc.createElement("record");
				EditedDeviceType o = it.next();
				el.setAttribute("id", Integer.toString(o.getId()));
				el.setAttribute("code", o.getDeviceTypeCode());
				el.setAttribute("desp", o.getDeviceTypeDesp());
				el.setAttribute("driverdesp", o.getDriver()
						.getDeviceTypeDriverDesp());
				content.appendChild(el);
			}
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void queryDeviceDriverList(boolean firstQuery, String keyword,
			int maxresults, int page, AdminSessionObject session,
			HttpRequest request, Document xmlDoc, Element content)
			throws NamingException, NoSuchFieldException {
		if (!((AdminSessionObject) session).hasRight(UserRight.DEVICEDRIVER))
			throw new KoneException("您没有查询设备驱动程序的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			DeviceDriverBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-deviceDriverBean",
					DeviceDriverBeanRemote.class);
			QueryResult<BaseDeviceDriver> r = bean.queryDeviceDriver(
					firstQuery, ((AdminSessionObject) session).getLoginUser()
							.getUserId(), 0, keyword, page * maxresults,
					maxresults);
			appendCountElement(firstQuery, content, xmlDoc, r.getCount());
			Iterator<BaseDeviceDriver> it = r.getResultList().iterator();
			while (it.hasNext()) {
				Element el = xmlDoc.createElement("record");
				BaseDeviceDriver o = it.next();
				el.setAttribute("id", Integer.toString(o.getId()));
				el.setAttribute("desp", o.getDeviceDriverDesp());
				el.setAttribute("filename", o.getDriverFileDesp());
				content.appendChild(el);
			}
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void queryDeviceTypeDriverList(boolean firstQuery,
			String keyword, int maxresults, int page,
			AdminSessionObject session, HttpRequest request, Document xmlDoc,
			Element content) throws NamingException, NoSuchFieldException {
		if (!((AdminSessionObject) session)
				.hasRight(UserRight.DEVICETYPEDRIVER))
			throw new KoneException("您没有查询模块驱动程序的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			DeviceTypeDriverBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-deviceTypeDriverBean",
					DeviceTypeDriverBeanRemote.class);
			QueryResult<BaseDeviceTypeDriver> r = bean.queryDeviceTypeDriver(
					firstQuery, ((AdminSessionObject) session).getLoginUser()
							.getUserId(), 0, keyword, page * maxresults,
					maxresults);
			appendCountElement(firstQuery, content, xmlDoc, r.getCount());
			Iterator<BaseDeviceTypeDriver> it = r.getResultList().iterator();
			while (it.hasNext()) {
				Element el = xmlDoc.createElement("record");
				BaseDeviceTypeDriver o = it.next();
				el.setAttribute("id", Integer.toString(o.getId()));
				el.setAttribute("desp", o.getDeviceTypeDriverDesp());
				el.setAttribute("filename", o.getDriverFileDesp());
				content.appendChild(el);
			}
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void queryManufList(boolean firstQuery, String keyword,
			int maxresults, int page, AdminSessionObject session,
			HttpRequest request, Document xmlDoc, Element content)
			throws NamingException, NoSuchFieldException {
		if (!((AdminSessionObject) session).hasRight(UserRight.MANUF))
			throw new KoneException("您没有查询厂商的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			ManufBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-manufBean", ManufBeanRemote.class);
			QueryResult<EditedManuf> r = bean.queryManuf(firstQuery, session
					.getLoginUser().getUserId(), 0, keyword, page * maxresults,
					maxresults);
			appendCountElement(firstQuery, content, xmlDoc, r.getCount());
			Iterator<EditedManuf> it = r.getResultList().iterator();
			while (it.hasNext()) {
				Element el = xmlDoc.createElement("record");
				EditedManuf o = it.next();
				el.setAttribute("id", Integer.toString(o.getId()));
				el.setAttribute("code", o.getManufCode());
				el.setAttribute("desp", o.getManufName());
				content.appendChild(el);
			}
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void queryRoleList(boolean firstQuery, String keyword,
			int maxresults, int page, AdminSessionObject session,
			HttpRequest request, Document xmlDoc, Element content)
			throws NamingException, NoSuchFieldException {
		if (!((AdminSessionObject) session).hasRight(UserRight.ROLE))
			throw new KoneException("您没有查询角色的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			RoleBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-roleBean", RoleBeanRemote.class);
			QueryResult<BaseRole> r = bean.queryRole(firstQuery, session
					.getLoginUser().getUserId(), 0, keyword, page * maxresults,
					maxresults);
			appendCountElement(firstQuery, content, xmlDoc, r.getCount());
			Iterator<BaseRole> it = r.getResultList().iterator();
			while (it.hasNext()) {
				Element el = xmlDoc.createElement("record");
				BaseRole o = it.next();
				el.setAttribute("id", Integer.toString(o.getId()));
				el.setAttribute("desp", o.getRoleName());
				content.appendChild(el);
			}
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void queryTradeCodeList(boolean firstQuery, String keyword,
			int maxresults, int page, AdminSessionObject session,
			HttpRequest request, Document xmlDoc, Element content)
			throws NamingException, NoSuchFieldException {
		if (!((AdminSessionObject) session).hasRight(UserRight.TRADECODE))
			throw new KoneException("您没有查询交易代码的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			TradeCodeBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-tradeCodeBean", TradeCodeBeanRemote.class);
			QueryResult<EditedTradeCode> r = bean.queryTradeCode(firstQuery,
					((AdminSessionObject) session).getLoginUser().getUserId(),
					0, keyword, page * maxresults, maxresults);
			appendCountElement(firstQuery, content, xmlDoc, r.getCount());
			Iterator<EditedTradeCode> it = r.getResultList().iterator();
			while (it.hasNext()) {
				Element el = xmlDoc.createElement("record");
				EditedTradeCode o = (EditedTradeCode) it.next();
				el.setAttribute("id", Long.toString(o.getId()));
				el.setAttribute("desp", o.getTradeCodeDesp());
				el.setAttribute("service", o.getTradeService());
				el.setAttribute("tradecode", o.getTradeCode());
				el.setAttribute("stated", o.isStated() ? "是" : "否");
				el.setAttribute("logged", o.isLogged() ? "是" : "否");
				if (o.getPayWay() != null)
					el.setAttribute("payway", o.getPayWay().getPayWayDesp());
				else
					el.setAttribute("payway", "");
				if (o.getPayItem() != null)
					el.setAttribute("payitem", o.getPayItem().getPayItemDesp());
				else
					el.setAttribute("payitem", "");
				content.appendChild(el);
			}
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void queryPageElementList(boolean firstQuery, String keyword,
			int maxresults, int page, AdminSessionObject session,
			HttpRequest request, Document xmlDoc, Element content)
			throws NamingException, NoSuchFieldException {
		if (!((AdminSessionObject) session).hasRight(UserRight.PAGEELEMENT))
			throw new KoneException("您没有查询页面元素的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			PageElementBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-pageElementBean", PageElementBeanRemote.class);
			QueryResult<EditedPageElement> r = bean.queryPageElement(
					firstQuery, ((AdminSessionObject) session).getLoginUser()
							.getUserId(), 0, keyword, page * maxresults,
					maxresults);
			appendCountElement(firstQuery, content, xmlDoc, r.getCount());
			Iterator<EditedPageElement> it = r.getResultList().iterator();
			while (it.hasNext()) {
				Element el = xmlDoc.createElement("record");
				EditedPageElement o = (EditedPageElement) it.next();
				el.setAttribute("id", Long.toString(o.getId()));
				el.setAttribute("code", o.getPageCode());
				el.setAttribute("desp", o.getPageDesp());
				el.setAttribute("business", o.getBusiness().getBusinessDesp());
				content.appendChild(el);
			}
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void queryBusinessList(boolean firstQuery, String keyword,
			int maxresults, int page, AdminSessionObject session,
			HttpRequest request, Document xmlDoc, Element content)
			throws NamingException, NoSuchFieldException {
		if (!((AdminSessionObject) session).hasRight(UserRight.BUSINESS))
			throw new KoneException("您没有查询业务分类的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			BusinessBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-businessBean", BusinessBeanRemote.class);
			QueryResult<EditedBusiness> r = bean.queryBusiness(firstQuery,
					((AdminSessionObject) session).getLoginUser().getUserId(),
					0, keyword, page * maxresults, maxresults);
			appendCountElement(firstQuery, content, xmlDoc, r.getCount());
			Iterator<EditedBusiness> it = r.getResultList().iterator();
			while (it.hasNext()) {
				Element el = xmlDoc.createElement("record");
				EditedBusiness o = (EditedBusiness) it.next();
				el.setAttribute("id", Long.toString(o.getId()));
				el.setAttribute("desp", o.getBusinessDesp());
				el.setAttribute("category", o.getBusinessCategory()
						.getBusinessCategoryDesp());
				content.appendChild(el);
			}
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void queryBusinessCategoryList(boolean firstQuery,
			String keyword, int maxresults, int page,
			AdminSessionObject session, HttpRequest request, Document xmlDoc,
			Element content) throws NamingException, NoSuchFieldException {
		if (!((AdminSessionObject) session)
				.hasRight(UserRight.BUSINESSCATEGORY))
			throw new KoneException("您没有查询业务分类的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			BusinessCategoryBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-businessCategoryBean",
					BusinessCategoryBeanRemote.class);
			QueryResult<BaseBusinessCategory> r = bean.queryBusinessCategory(
					firstQuery, ((AdminSessionObject) session).getLoginUser()
							.getUserId(), 0, keyword, page * maxresults,
					maxresults);
			appendCountElement(firstQuery, content, xmlDoc, r.getCount());
			Iterator<BaseBusinessCategory> it = r.getResultList().iterator();
			while (it.hasNext()) {
				Element el = xmlDoc.createElement("record");
				BaseBusinessCategory o = it.next();
				el.setAttribute("id", Long.toString(o.getId()));
				el.setAttribute("desp", o.getBusinessCategoryDesp());
				content.appendChild(el);
			}
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void queryPayItemList(boolean firstQuery, String keyword,
			int maxresults, int page, AdminSessionObject session,
			HttpRequest request, Document xmlDoc, Element content)
			throws NamingException, NoSuchFieldException {
		if (!((AdminSessionObject) session).hasRight(UserRight.PAYITEM))
			throw new KoneException("您没有查询收费项目的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			PayItemBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-payItemBean", PayItemBeanRemote.class);
			QueryResult<EditedPayItem> r = bean.queryPayItem(firstQuery,
					session.getLoginUser().getUserId(), 0, keyword, page
							* maxresults, maxresults);
			appendCountElement(firstQuery, content, xmlDoc, r.getCount());
			Iterator<EditedPayItem> it = r.getResultList().iterator();
			while (it.hasNext()) {
				Element el = xmlDoc.createElement("record");
				EditedPayItem o = (EditedPayItem) it.next();
				el.setAttribute("id", Long.toString(o.getId()));
				el.setAttribute("desp", o.getPayItemDesp());
				el.setAttribute("price", Long.toString(o.getPrice()));
				el.setAttribute("memo", o.getMemo());
				content.appendChild(el);
			}
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void queryDisabledPrintUserList(boolean firstQuery,
			String keyword, int maxresults, int page,
			AdminSessionObject session, HttpRequest request, Document xmlDoc,
			Element content) throws NamingException, NoSuchFieldException {
		if (!((AdminSessionObject) session)
				.hasRight(UserRight.DISABLEPRINTACCOUNT))
			throw new KoneException("您没有查询的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			DisabledPrintUserBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-disabledPrintUserBean",
					DisabledPrintUserBeanRemote.class);
			QueryResult<BaseDisabledPrintUser> r = bean.queryDisabledPrintUser(
					firstQuery, ((AdminSessionObject) session).getLoginUser()
							.getUserId(), 0, keyword, page * maxresults,
					maxresults);
			appendCountElement(firstQuery, content, xmlDoc, r.getCount());
			Iterator<BaseDisabledPrintUser> it = r.getResultList().iterator();
			while (it.hasNext()) {
				Element el = xmlDoc.createElement("record");
				BaseDisabledPrintUser o = it.next();
				el.setAttribute("id", Long.toString(o.getId()));
				el.setAttribute("desp", o.getUserno());
				content.appendChild(el);
			}
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void queryAlarmCategoryList(boolean firstQuery, String keyword,
			int maxresults, int page, AdminSessionObject session,
			HttpRequest request, Document xmlDoc, Element content)
			throws NamingException, NoSuchFieldException {
		if (!((AdminSessionObject) session).hasRight(UserRight.ALARMCATEGORY))
			throw new KoneException("您没有查询告警类型的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			AlarmCategoryBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-alarmCategoryBean",
					AlarmCategoryBeanRemote.class);
			QueryResult<BaseAlarmCategory> r = bean.queryAlarmCategory(
					firstQuery, ((AdminSessionObject) session).getLoginUser()
							.getUserId(), 0, keyword, page * maxresults,
					maxresults);
			appendCountElement(firstQuery, content, xmlDoc, r.getCount());
			Iterator<BaseAlarmCategory> it = r.getResultList().iterator();
			while (it.hasNext()) {
				Element el = xmlDoc.createElement("record");
				BaseAlarmCategory o = it.next();
				el.setAttribute("id", Long.toString(o.getId()));
				el.setAttribute("desp", o.getAlarmCategoryDesp());
				el.setAttribute("alarmlevel", o.getAlarmLevel().toString());
				content.appendChild(el);
			}
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void queryFileCategoryList(boolean firstQuery, String keyword,
			int maxresults, int page, AdminSessionObject session,
			HttpRequest request, Document xmlDoc, Element content)
			throws NamingException, NoSuchFieldException {
		if (!((AdminSessionObject) session).hasRight(UserRight.FILECATEGORY))
			throw new KoneException("您没有查询文件分类的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			FileCategoryBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-fileCategoryBean",
					FileCategoryBeanRemote.class);
			QueryResult<BaseFileCategory> r = bean.queryFileCategory(
					firstQuery, ((AdminSessionObject) session).getLoginUser()
							.getUserId(), 0, keyword, page * maxresults,
					maxresults);
			appendCountElement(firstQuery, content, xmlDoc, r.getCount());
			Iterator<BaseFileCategory> it = r.getResultList().iterator();
			while (it.hasNext()) {
				Element el = xmlDoc.createElement("record");
				BaseFileCategory o = it.next();
				el.setAttribute("id", Long.toString(o.getId()));
				el.setAttribute("desp", o.getFileCategoryDesp());
				el.setAttribute("cachedtype", o.getCachedType().toString());
				content.appendChild(el);
			}
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void queryFileOwnerList(boolean firstQuery, String keyword,
			int maxresults, int page, AdminSessionObject session,
			HttpRequest request, Document xmlDoc, Element content)
			throws NamingException, NoSuchFieldException {
		if (!((AdminSessionObject) session).hasRight(UserRight.FILEOWNER))
			throw new KoneException("您没有查询文件属主的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			FileOwnerBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-fileOwnerBean", FileOwnerBeanRemote.class);
			QueryResult<BaseFileOwner> r = bean.queryFileOwner(firstQuery,
					((AdminSessionObject) session).getLoginUser().getUserId(),
					0, keyword, page * maxresults, maxresults);
			appendCountElement(firstQuery, content, xmlDoc, r.getCount());
			Iterator<BaseFileOwner> it = r.getResultList().iterator();
			while (it.hasNext()) {
				Element el = xmlDoc.createElement("record");
				BaseFileOwner o = it.next();
				el.setAttribute("id", Long.toString(o.getId()));
				el.setAttribute("desp", o.getFileOwnerDesp());
				el.setAttribute("visitright", o.getVisitRight().toString());
				content.appendChild(el);
			}
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void queryFileHostList(boolean firstQuery, String keyword,
			int maxresults, int page, AdminSessionObject session,
			HttpRequest request, Document xmlDoc, Element content)
			throws NamingException, NoSuchFieldException {
		if (!((AdminSessionObject) session).hasRight(UserRight.FILEHOST))
			throw new KoneException("您没有查询文件主机的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			FileHostBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-fileHostBean", FileHostBeanRemote.class);
			QueryResult<BaseFileHost> r = bean.queryFileHost(firstQuery,
					((AdminSessionObject) session).getLoginUser().getUserId(),
					0, keyword, page * maxresults, maxresults);
			appendCountElement(firstQuery, content, xmlDoc, r.getCount());
			Iterator<BaseFileHost> it = r.getResultList().iterator();
			while (it.hasNext()) {
				Element el = xmlDoc.createElement("record");
				BaseFileHost o = it.next();
				el.setAttribute("id", Long.toString(o.getId()));
				el.setAttribute("desp", o.getHostDesp());
				el.setAttribute("rootdir", o.getFileRootDir());
				content.appendChild(el);
			}
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void queryFileUserList(boolean firstQuery, String keyword,
			int maxresults, int page, AdminSessionObject session,
			HttpRequest request, Document xmlDoc, Element content)
			throws NamingException, NoSuchFieldException {
		if (!((AdminSessionObject) session).hasRight(UserRight.FILEUSER))
			throw new KoneException("您没有查询文件用户的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			FileUserBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-fileUserBean", FileUserBeanRemote.class);
			QueryResult<BaseFileUser> r = bean.queryFileUser(firstQuery,
					((AdminSessionObject) session).getLoginUser().getUserId(),
					0, keyword, page * maxresults, maxresults);
			appendCountElement(firstQuery, content, xmlDoc, r.getCount());
			Iterator<BaseFileUser> it = r.getResultList().iterator();
			while (it.hasNext()) {
				Element el = xmlDoc.createElement("record");
				BaseFileUser o = it.next();
				el.setAttribute("id", o.getId());
				el.setAttribute("fileowner", o.getFileOwner()
						.getFileOwnerDesp());
				content.appendChild(el);
			}
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void queryPrintTypeList(boolean firstQuery, String keyword,
			int maxresults, int page, AdminSessionObject session,
			HttpRequest request, Document xmlDoc, Element content)
			throws NamingException, NoSuchFieldException {
		if (!((AdminSessionObject) session).hasRight(UserRight.PRINTTYPE))
			throw new KoneException("您没有查询打印类型的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			PrintTypeBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-printTypeBean", PrintTypeBeanRemote.class);
			QueryResult<BasePrintType> r = bean.queryPrintType(firstQuery,
					((AdminSessionObject) session).getLoginUser().getUserId(),
					0, keyword, page * maxresults, maxresults);
			appendCountElement(firstQuery, content, xmlDoc, r.getCount());
			Iterator<BasePrintType> it = r.getResultList().iterator();
			while (it.hasNext()) {
				Element el = xmlDoc.createElement("record");
				BasePrintType o = it.next();
				el.setAttribute("id", Long.toString(o.getId()));
				el.setAttribute("desp", o.getPrintTypeDesp());
				content.appendChild(el);
			}
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void queryPayWayList(boolean firstQuery, String keyword,
			int maxresults, int page, AdminSessionObject session,
			HttpRequest request, Document xmlDoc, Element content)
			throws NamingException, NoSuchFieldException {
		if (!((AdminSessionObject) session).hasRight(UserRight.PAYWAY))
			throw new KoneException("您没有查询收费渠道的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			PayWayBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-payWayBean", PayWayBeanRemote.class);
			QueryResult<BasePayWay> r = bean.queryPayWay(firstQuery, session
					.getLoginUser().getUserId(), 0, keyword, page * maxresults,
					maxresults);
			appendCountElement(firstQuery, content, xmlDoc, r.getCount());
			Iterator<BasePayWay> it = r.getResultList().iterator();
			while (it.hasNext()) {
				Element el = xmlDoc.createElement("record");
				BasePayWay o = it.next();
				el.setAttribute("id", Long.toString(o.getId()));
				el.setAttribute("desp", o.getPayWayDesp());
				el.setAttribute("needtrade", o.isNeedTrade() ? "是" : "否");
				el.setAttribute("type", o.getType().toString());
				content.appendChild(el);
			}
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void queryAppList(boolean firstQuery, String keyword,
			int maxresults, int page, AdminSessionObject session,
			HttpRequest request, Document xmlDoc, Element content)
			throws NamingException, NoSuchFieldException {
		if (!((AdminSessionObject) session).hasRight(UserRight.APP))
			throw new KoneException("您没有查询应用的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			AppBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-appBean", AppBeanRemote.class);
			QueryResult<EditedApp> r = bean.queryApp(firstQuery, session
					.getLoginUser().getUserId(), 0, keyword, page * maxresults,
					maxresults);
			appendCountElement(firstQuery, content, xmlDoc, r.getCount());
			Iterator<EditedApp> it = r.getResultList().iterator();
			while (it.hasNext()) {
				Element el = xmlDoc.createElement("record");
				EditedApp o = (EditedApp) it.next();
				el.setAttribute("id", Long.toString(o.getId()));
				el.setAttribute("code", o.getAppCode());
				el.setAttribute("desp", o.getAppDesp());
				el.setAttribute("category", o.getAppCategory()
						.getAppCategoryDesp());
				content.appendChild(el);
			}
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void queryCommInterfaceList(boolean firstQuery, String keyword,
			int maxresults, int page, AdminSessionObject session,
			HttpRequest request, Document xmlDoc, Element content)
			throws NamingException, NoSuchFieldException {
		if (!((AdminSessionObject) session).hasRight(UserRight.COMMINTERFACE))
			throw new KoneException("您没有查询接口配置的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			CommInterfaceBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-commInterfaceBean",
					CommInterfaceBeanRemote.class);
			QueryResult<BaseCommInterface> r = bean.queryCommInterface(
					firstQuery, ((AdminSessionObject) session).getLoginUser()
							.getUserId(), 0, keyword, page * maxresults,
					maxresults);
			appendCountElement(firstQuery, content, xmlDoc, r.getCount());
			Iterator<BaseCommInterface> it = r.getResultList().iterator();
			while (it.hasNext()) {
				Element el = xmlDoc.createElement("record");
				BaseCommInterface o = it.next();
				el.setAttribute("id", Long.toString(o.getId()));
				el.setAttribute("type", Integer.toString(o.getType()));
				el.setAttribute("desp", o.getDesp());
				content.appendChild(el);
			}
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void queryAppCategoryList(boolean firstQuery, String keyword,
			int maxresults, int page, AdminSessionObject session,
			HttpRequest request, Document xmlDoc, Element content)
			throws NamingException, NoSuchFieldException {
		if (!((AdminSessionObject) session).hasRight(UserRight.APPCATEGORY))
			throw new KoneException("您没有查询应用分类的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			AppCategoryBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-appCategoryBean", AppCategoryBeanRemote.class);
			QueryResult<EditedAppCategory> r = bean.queryAppCategory(
					firstQuery, ((AdminSessionObject) session).getLoginUser()
							.getUserId(), 0, keyword, page * maxresults,
					maxresults);
			appendCountElement(firstQuery, content, xmlDoc, r.getCount());
			Iterator<EditedAppCategory> it = r.getResultList().iterator();
			while (it.hasNext()) {
				Element el = xmlDoc.createElement("record");
				EditedAppCategory o = it.next();
				el.setAttribute("id", Long.toString(o.getId()));
				el.setAttribute("code", o.getAppCategoryCode());
				el.setAttribute("desp", o.getAppCategoryDesp());
				content.appendChild(el);
			}
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void queryUserList(boolean firstQuery, String keyword,
			int maxresults, int page, AdminSessionObject session,
			HttpRequest request, Document xmlDoc, Element content)
			throws NamingException, NoSuchFieldException {
		if (!((AdminSessionObject) session).hasRight(UserRight.USER))
			throw new KoneException("您没有查询用户的权限.");
		Integer manufId = request.getParameterIntDef("manufid", null);
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
			UserBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-userBean", UserBeanRemote.class);
			QueryResult<EditedUser> r = bean.queryUser(firstQuery, session
					.getLoginUser().getUserId(), orgId, manufId, 0, keyword,
					page * maxresults, maxresults);
			appendCountElement(firstQuery, content, xmlDoc, r.getCount());
			Iterator<EditedUser> it = r.getResultList().iterator();
			while (it.hasNext()) {
				Element el = xmlDoc.createElement("record");
				EditedUser user = it.next();
				el.setAttribute("id", Long.toString(user.getUserId()));
				el.setAttribute("code", user.getUserCode());
				el.setAttribute("desp", user.getUserName());
				el.setAttribute("rightvalue",
						Integer.toString(user.getUserGroup().getValue()));
				if (user.getUserGroup().isCustomer()) {
					if (user.getRole().getId() != null)
						el.setAttribute("right", user.getRole().getRoleName());
					else
						el.setAttribute("right", user.getUserGroup()
								.getString());
				} else
					el.setAttribute("right", user.getUserGroup().toString());
				if (user.getManuf().getId() != null)
					el.setAttribute("manuf", user.getManuf().getManufName());
				else
					el.setAttribute("manuf", "");
				if (user.getOrg().getId() != null)
					el.setAttribute("org", user.getOrg().getOrgFullName());
				else
					el.setAttribute("org", "");
				content.appendChild(el);
			}
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void sendTermCommand(AdminSessionObject session,
			HttpRequest request, Document xmlDoc, Element content)
			throws NamingException, NoSuchFieldException, InterruptedException,
			IOException {
		if (!((AdminSessionObject) session).hasRight(UserRight.TERMMONITOR))
			throw new KoneException("您没有终端监控的权限.");
		Integer termId = request.getParameterIntDef("termid", null);
		Integer command = request.getParameterInt("command");
		if (command > 4) {
			if (!((AdminSessionObject) session).getLoginUser().getUserGroup()
					.isSystemManager())
				throw new KoneException("您必须具备系统管理员以上权限，才能发出本远程控制命令.");
		}
		CachedTermCommand cmd = new CachedTermCommand();
		cmd.setCommand(command);
		cmd.setCommandTime(new Date());
		cmd.setParam(request.getParameterDef("param1", null));
		cmd.setParam1(request.getParameterDef("param2", null));
		List<CachedTermCommand> ls = new ArrayList<CachedTermCommand>();
		ls.add(cmd);
		CachedTerm term = CacheHelper.termMap.getTerm(termId);
		if (term != null) {
			if (term.getOnlineStatus(System.currentTimeMillis()) > 0) {
				CacheHelper.termMc.setStreamableList(
						CachedTermCommand.KEY_PREFIX + termId, ls, null);
			} else
				CacheHelper.termMc
						.delete(CachedTermCommand.KEY_PREFIX + termId);
			if (command < 2) {
				List<Integer> terms = new ArrayList<Integer>();
				terms.add(term.getId());
				LoopNamingContext context = new LoopNamingContext("db");
				try {
					TermServiceBeanRemote bean = context.lookup(
							Lookuper.JNDI_TYPE_EJB, "kxd-ejb-termServiceBean",
							TermServiceBeanRemote.class);
					bean.termPauseResume(terms, command == 0);
				} finally {
					if (context != null)
						context.close();
				}
			}
		}
	}

	/**
	 * 对选定的终端进行操作
	 * 
	 * @param session
	 * @param request
	 * @param xmlDoc
	 * @param content
	 * @throws NamingException
	 * @throws NoSuchFieldException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	protected void sendtermscommand(AdminSessionObject session,
			HttpRequest request, Document xmlDoc, Element content)
			throws NamingException, NoSuchFieldException, InterruptedException,
			IOException {
		if (!((AdminSessionObject) session).hasRight(UserRight.TERMMONITOR))
			throw new KoneException("您没有终端监控的权限.");
		String termIds = request.getParameter("termids");
		Integer command = request.getParameterInt("command");
		if (command > 4) {
			if (!((AdminSessionObject) session).getLoginUser().getUserGroup()
					.isSystemManager())
				throw new KoneException("您必须具备系统管理员以上权限，才能发出本远程控制命令.");
		}
		CachedTermCommand cmd = new CachedTermCommand();
		cmd.setCommand(command);
		cmd.setCommandTime(new Date());
		cmd.setParam(request.getParameterDef("param1", null));
		cmd.setParam1(request.getParameterDef("param2", null));
		List<CachedTermCommand> ls = new ArrayList<CachedTermCommand>();
		ls.add(cmd);
		String[] ids = termIds.substring(0, termIds.length() - 1).split(",");
		List<Integer> terms = new ArrayList<Integer>();
		for (int i = 0; i < ids.length; i++) {
			Integer termId = Integer.valueOf(ids[i]);
			CachedTerm term = CacheHelper.termMap.getTerm(termId);
			if (term != null) {
				terms.add(termId);
				cmd.setId(termId);
				if (term.getOnlineStatus(System.currentTimeMillis()) > 0) {
					CacheHelper.termMc.setStreamableList(
							CachedTermCommand.KEY_PREFIX + termId, ls, null);
				} else
					CacheHelper.termMc.delete(CachedTermCommand.KEY_PREFIX
							+ termId);
			}
		}
		if (command < 2) {
			LoopNamingContext context = new LoopNamingContext("db");
			try {
				TermServiceBeanRemote bean = context.lookup(
						Lookuper.JNDI_TYPE_EJB, "kxd-ejb-termServiceBean",
						TermServiceBeanRemote.class);
				bean.termPauseResume(terms, command == 0);
			} finally {
				if (context != null)
					context.close();
			}
		}
	}

	protected void getTermDeviceStatus(AdminSessionObject session,
			HttpRequest request, Document xmlDoc, Element content)
			throws NamingException, NoSuchFieldException {
		if (!((AdminSessionObject) session).hasRight(UserRight.TERMMONITOR))
			throw new KoneException("您没有终端监控的权限.");
		CachedTerm term = CacheHelper.termMap.getTerm(request
				.getParameterInt("termid"));
		if (term != null) {
			Collection<MonitoredDeviceStatus> c = new ArrayList<MonitoredDeviceStatus>();
			// term.getMonitoredDeviceStatus(c);
			Iterator<MonitoredDeviceStatus> en = c.iterator();
			while (en.hasNext()) {
				MonitoredDeviceStatus st = en.next();
				Element el = xmlDoc.createElement("treenode");
				content.appendChild(el);
				el.setAttribute("id", st.getIdString());
				el.setAttribute("status", Integer.toString(st.getStatus()));
				el.setAttribute("port", Integer.toString(st.getPort()));
				el.setAttribute("extconfig", st.getExtConfig());
				el.setAttribute("devicedesp", st.getDeviceDesp());
				el.setAttribute("devicetypedesp", st.getDeviceTypeDesp());
				el.setAttribute("message", st.getMessage());
			}
		}
	}

	protected void monitorTermList(AdminSessionObject session,
			HttpRequest request, Document xmlDoc, Element content)
			throws NamingException, InterruptedException, IOException,
			InstantiationException, IllegalAccessException {
		if (!((AdminSessionObject) session).hasRight(UserRight.TERMMONITOR))
			throw new KoneException("您没有终端监控的权限.");
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
		Element el = xmlDoc.createElement("monitorinfo");
		content.appendChild(el);
		el.setAttribute("termcount", Long.toString(r.getKey().getTermCount()));
		el.setAttribute("tradecount", Long.toString(r.getKey().getTradeCount()));
		el.setAttribute("suctradecount",
				Long.toString(r.getKey().getSucTradeCount()));
		el.setAttribute("trademoney", Long.toString(r.getKey().getTradeMoney()));
		Iterator<MonitoredTermResult> it = c.getTerms().iterator();
		el = xmlDoc.createElement("info");
		content.appendChild(el);
		el.setAttribute("allcount", Integer.toString(c.getAllCount()));
		el.setAttribute("notinstallcount",
				Integer.toString(c.getNotInstallCount()));
		el.setAttribute("notactivecount",
				Integer.toString(c.getNotActiveCount()));
		el.setAttribute("activedcount", Integer.toString(c.getActivedCount()));
		el.setAttribute("normalcount", Integer.toString(c.getNormalCount()));
		el.setAttribute("alarmcount", Integer.toString(c.getAlarmCount()));
		el.setAttribute("faultcount", Integer.toString(c.getFalutCount()));
		el.setAttribute("onlinecount", Integer.toString(c.getOnlineCount()));
		el.setAttribute("offlinecount", Integer.toString(c.getOfflineCount()));
		el.setAttribute("pausecount", Integer.toString(c.getPauseCount()));
		el.setAttribute("pages", Integer.toString(c.getPages()));
		el.setAttribute("recordCount", Integer.toString(c.getRecordCount()));
		while (it.hasNext()) {
			MonitoredTermResult o = (MonitoredTermResult) it.next();
			el = xmlDoc.createElement("treenode");
			content.appendChild(el);
			boolean online = Boolean.TRUE.equals(o.isOnline());
			el.setAttribute("id", o.getIdString());
			el.setAttribute("code", o.getTermCode());
			el.setAttribute("termstatuscode",
					Integer.toString(o.getStatus().getValue()));
			el.setAttribute("alarmstatuscode",
					Integer.toString(o.getAlarmStatus().getValue() + 1));
			el.setAttribute("orgid", Integer.toString(o.getOrg().getOrgId()));
			el.setAttribute("orgname", o.getOrg().getOrgName());
			CachedTermType type = CacheHelper.termTypeMap
					.get(o.getTermTypeId());
			el.setAttribute("termtypedesp", type.getTypeDesp());
			CachedApp app = CacheHelper.appMap.get(o.getAppId());
			el.setAttribute("appdesp", app.getAppDesp());
			el.setAttribute("manufdesp", type.getManuf().getManufName());
			el.setAttribute("termstatus", o.getStatus().toString());
			if (o.getLastOnlineTime() == null
					|| o.getLastOnlineTime().getTime() == 0)
				el.setAttribute("lastinlinetime", "尚未注册激活");
			else
				el.setAttribute("lastinlinetime", DataUnit.formatDateTime(
						o.getLastOnlineTime(), "yyyy-MM-dd HH:mm:ss"));
			el.setAttribute("termstatus", o.getStatus().toString());
			if (o.getStatus().equals(TermStatus.NORMAL)) {
				if (o.getAlarmStatus().equals(AlarmStatus.NORMAL) && !online)
					el.setAttribute("alarmstatus", "离线");
				else
					el.setAttribute("alarmstatus", o.getAlarmStatus()
							.toString());
			} else
				el.setAttribute("alarmstatus", o.getStatus().toString());
			el.setAttribute("online", online ? "true" : "false");
			el.setAttribute("termdesp", o.getTermDesp());
			if (o.getDeviceStatusList() == null)
				el.setTextContent(o.getTermDesp());
			else {
				for (MonitoredDeviceStatus st : o.getDeviceStatusList()) {
					Element el1 = xmlDoc.createElement("device");
					el.appendChild(el1);
					el1.setAttribute("id", st.getIdString());
					el1.setAttribute("status", Integer.toString(st.getStatus()));
					el1.setAttribute("port", Integer.toString(st.getPort()));
					el1.setAttribute("extconfig", st.getExtConfig());
					el1.setAttribute("devicedesp", st.getDeviceDesp());
					el1.setAttribute("devicetypedesp", st.getDeviceTypeDesp());
					el1.setAttribute("message", st.getMessage());
				}
			}
		}

	}

	protected void getOrgChildren(AdminSessionObject session, Integer orgId,
			int depth, HttpRequest request, Document xmlDoc, Element content)
			throws NamingException, JSONException {
		Collection<QueryedOrg> c = AdminHelper.getOrgTreeItems(orgId, 1, null,
				false, null);
		Iterator<QueryedOrg> it = c.iterator();
		if (!request.getParameterBooleanDef("returnjson", false)) {
			Element p = xmlDoc.createElement("orgtree");
			content.appendChild(p);
			while (it.hasNext()) {
				QueryedOrg org = it.next();
				Element el = xmlDoc.createElement("treenode");
				content.appendChild(el);
				el.setAttribute("id", org.getIdString());
				el.setAttribute("simplename", org.getSimpleName());
				el.setAttribute("depth", Integer.toString(org.getDepth()));
				el.setAttribute("haschildren", org.isHasChildren() ? "true"
						: "false");
				if (org.getStandardAreaCode() != null)
					el.setAttribute("stardardareacode",
							org.getStandardAreaCode());
				el.setTextContent(org.getOrgName());
			}
		} else {
			JSONArray array = new JSONArray();
			while (it.hasNext()) {
				QueryedOrg org = it.next();
				JSONObject o = new JSONObject();
				array.put(o);
				o.put("nodeid", org.getId());
				o.put("disabled", false);
				o.put("expanded", true);
				o.put("text", org.getOrgName());
				o.put("icon", "org.gif");
				if (org.getStandardAreaCode() != null)
					o.put("standardareacode", org.getStandardAreaCode());
				if (org.isHasChildren())
					o.put("loadurl", "id=" + org.getId());
			}
			content.appendChild(xmlDoc.createCDATASection(array.toString()));
		}
	}

	protected void editUserResources(int type, AdminSessionObject session,
			HttpRequest request, Document xmlDoc, Element content)
			throws NamingException, NoSuchFieldException {
		if (!((AdminSessionObject) session).hasRight(UserRight.USER_EDIT))
			throw new KoneException("您没有编辑用户的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			int userId = request.getParameterInt("form:userid");
			String oldParams = request.getParameter("form:oldparams");
			String newParams = request.getParameter("form:newparams_hidden");
			if (oldParams.endsWith(","))
				oldParams = oldParams.substring(0, oldParams.length() - 1);
			if (newParams.endsWith(","))
				newParams = newParams.substring(0, newParams.length() - 1);
			String[] os = StringUnit.split(oldParams, ",");
			String[] ns = StringUnit.split(newParams, ",");
			ArrayList<Integer> olist = new ArrayList<Integer>();
			ArrayList<Integer> nlist = new ArrayList<Integer>();
			for (int i = 0; i < os.length; i++)
				olist.add(Integer.valueOf(os[i]));
			for (int i = 0; i < ns.length; i++)
				nlist.add(Integer.valueOf(ns[i]));
			for (int i = 0; i < olist.size(); i++) {
				Integer l = olist.get(i);
				if (nlist.contains(l)) {
					olist.remove(i);
					nlist.remove(l);
					i--;
				}
			}
			if (nlist.size() > 0 || olist.size() > 0) {
				UserBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
						"kxd-ejb-userBean", UserBeanRemote.class);
				switch (type) {
				case 0:
					break;
				case 1:
					bean.setUserManagedRoles(((AdminSessionObject) session)
							.getLoginUser().getUserId(), userId, nlist, olist);
					break;
				case 2:
					break;
				}
			}
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void editTermTypeDevice(AdminSessionObject session,
			HttpRequest request, Document xmlDoc, Element content)
			throws NamingException, NumberFormatException, NoSuchFieldException {
		if (!((AdminSessionObject) session).hasRight(UserRight.TERMTYPE_EDIT))
			throw new KoneException("您无编辑终端型号的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			TermTypeBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-termTypeBean", TermTypeBeanRemote.class);
			EditedTermTypeDevice typeDevice = new EditedTermTypeDevice();
			typeDevice.setTermType(new BaseTermType(request
					.getParameterInt("id")));
			typeDevice.setDevice(new BaseDevice(Integer.valueOf(request
					.getParameter("deviceid"))));
			typeDevice.setExtConfig(request.getParameter("form:extconfig"));
			typeDevice.setPort(Integer.valueOf(request
					.getParameter("form:port")));
			bean.editTermTypeDevice(((AdminSessionObject) session)
					.getLoginUser().getUserId(), typeDevice);
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void addTermTypeDevice(AdminSessionObject session,
			HttpRequest request, Document xmlDoc, Element content)
			throws NamingException, NumberFormatException, NoSuchFieldException {
		if (!((AdminSessionObject) session).hasRight(UserRight.TERMTYPE_EDIT))
			throw new KoneException("您无编辑终端型号的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			TermTypeBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-termTypeBean", TermTypeBeanRemote.class);
			EditedTermTypeDevice typeDevice = new EditedTermTypeDevice();
			typeDevice.setTermType(new BaseTermType(Integer.valueOf(request
					.getParameter("id"))));
			typeDevice.setDevice(new BaseDevice(Integer.valueOf(request
					.getParameter("form:deviceid"))));
			typeDevice.setExtConfig(request.getParameter("form:extconfig"));
			typeDevice.setPort(Integer.valueOf(request
					.getParameter("form:port")));
			bean.addTermTypeDevice(((AdminSessionObject) session)
					.getLoginUser().getUserId(), typeDevice);
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void getDeviceDriverFiles(AdminSessionObject session, String id,
			Document xmlDoc, Element content) throws NamingException {
		if (!((AdminSessionObject) session)
				.hasRight(UserRight.DEVICEDRIVER_EDIT))
			throw new KoneException("您没有编辑硬件驱动的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			DeviceDriverBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-deviceDriverBean",
					DeviceDriverBeanRemote.class);
			Iterator<BaseDeviceDriverFile> it = bean.getDeviceDriverFiles(
					Integer.valueOf(id)).iterator();
			while (it.hasNext()) {
				BaseDeviceDriverFile t = it.next();
				Element e = xmlDoc.createElement("file");
				content.appendChild(e);
				e.setAttribute("id", t.getIdString());
				e.setAttribute("filename", t.getFileName());
			}
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void getAppFiles(AdminSessionObject session, String id,
			Document xmlDoc, Element content) throws NamingException {
		if (!((AdminSessionObject) session).hasRight(UserRight.APP_EDIT))
			throw new KoneException("您没有编辑应用的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			AppBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-appBean", AppBeanRemote.class);
			Iterator<BaseAppFile> it = bean.getAppFileList(
					((AdminSessionObject) session).getLoginUser().getUserId(),
					Integer.valueOf(id)).iterator();
			while (it.hasNext()) {
				BaseAppFile t = it.next();
				Element e = xmlDoc.createElement("file");
				content.appendChild(e);
				e.setAttribute("id", t.getIdString());
				e.setAttribute("filename", t.getAppFilename());
			}
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void getTermMoveList(AdminSessionObject session, String id,
			Document xmlDoc, Element content) throws NamingException {
		if (!((AdminSessionObject) session).hasRight(UserRight.TERM))
			throw new KoneException("您没有查看和操作终端的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			TermBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-termBean", TermBeanRemote.class);
			Iterator<BaseTermMoveItem> it = bean.getTermMoveList(
					((AdminSessionObject) session).getLoginUser().getUserId(),
					Integer.valueOf(id)).iterator();
			while (it.hasNext()) {
				BaseTermMoveItem t = it.next();
				Element e = xmlDoc.createElement("moveitem");
				content.appendChild(e);
				e.setAttribute("orgname", t.getOrg().getOrgFullName());
				e.setAttribute("datetime", new DateTime(t.getMoveTime())
						.format("yyyy-MM-dd HH:mm:ss"));
			}
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void getInfoFileList(AdminSessionObject session, String id,
			Integer infoType, Document xmlDoc, Element content)
			throws NamingException {
		if (!((AdminSessionObject) session).hasRight(UserRight.INFO))
			throw new KoneException("您没有查看和操作信息的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			InfoBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-infoBean", InfoBeanRemote.class);
			Iterator<BaseInfoFile> it = bean.queryInfoFile(Integer.valueOf(id),
					infoType, 0, null).iterator();
			while (it.hasNext()) {
				BaseInfoFile t = it.next();
				Element e = xmlDoc.createElement("file");
				content.appendChild(e);
				e.setAttribute("fileid", Long.toString(t.getInfoFileId()));
				e.setAttribute("filename", t.getFileName());
			}
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void getUserOrgTreeList(AdminSessionObject session,
			HttpRequest request, Document xmlDoc, Element content)
			throws NamingException, NoSuchFieldException {
		// if (!((AdminSessionObject) session).hasRight(UserRight.TRADEPROC))
		// throw new KoneException("您没有查看处理记录的权限.");
		// LoopNamingContext context = new LoopNamingContext("db");
		LoginUser user = ((AdminSessionObject) session).getLoginUser();
		Integer porgId = request.getParameterIntDef("orgid", null);
		try {
			if (porgId==null&&user.getOrg().getId() != null)
				porgId = user.getOrg().getId();

			Collection<QueryedOrg> c = AdminHelper.getOrgTreeItems(porgId, 5,
					null, true, "");
			Iterator<QueryedOrg> it = c.iterator();
			while (it.hasNext()) {
				QueryedOrg e = it.next();
				Element el = xmlDoc.createElement("org");
				content.appendChild(el);
				el.setAttribute("orgid", e.getIdString());
				el.setAttribute("orgname", e.getOrgName());
				el.setAttribute("depth", String.valueOf(e.getDepth()));
				el.setAttribute("parentid", e.getParentOrg().getIdString());
				el.setAttribute(
						"parentname",
						null != e.getParentOrg().getIdString() ? CacheHelper.orgMap
								.get(new Integer(e.getParentOrg().getIdString()))
								.getOrgName()
								: null);
				el.setAttribute("isHaveChildren", String.valueOf(e.isHasChildren()));
			}
		} finally {

		}
	}

	protected void getBusinessTreeList(AdminSessionObject session,
			HttpRequest request, Document xmlDoc, Element content)
			throws NamingException, NoSuchFieldException {
		List<CachedBusiness> businessList;
		try {
			List<CachedBusinessCategory> businessCategoryList = (List<CachedBusinessCategory>) CacheHelper.businessCategoryMap
					.values();
			businessList = (List<CachedBusiness>) CacheHelper.businessMap
					.values();
			TreeNode<Integer> businessTree = new SimpleTreeNode<Integer, String>();
			SimpleTreeNode<Integer, String> root = new SimpleTreeNode<Integer, String>();
			businessTree.add(root);
			for (CachedBusinessCategory o : businessCategoryList) {
				if (businessList.size() == 0)
					break;
				SimpleTreeNode<Integer, String> p = new SimpleTreeNode<Integer, String>(
						o.getId(), o.getBusinessCategoryDesp());
				p.setExpanded(true);
				for (int i = 0; i < businessList.size(); i++) {
					CachedBusiness b = businessList.get(i);
					if (b.getBusinessCategoryId() == o.getId()) {
						SimpleTreeNode<Integer, String> cp = new SimpleTreeNode<Integer, String>(
								b.getId(), b.getBusinessDesp());
						p.add(cp);
						businessList.remove(i);
						i--;
					}
				}
				if (p.getChildren().size() > 0)
					root.add(p);
			}

			Iterator<TreeNode<Integer>> it = root.getChildren().iterator();
			while (it.hasNext()) {
				TreeNode<Integer> o = it.next();
				Element el = xmlDoc.createElement("record");
				content.appendChild(el);
				el.setAttribute("id", o.getIdString());
				el.setAttribute("name", o.getText());
				Iterator<TreeNode<Integer>> its = o.getChildren().iterator();
				while (its.hasNext()) {
					TreeNode<Integer> d = its.next();
					Element els = xmlDoc.createElement("details");
					el.appendChild(els);
					els.setAttribute("id", d.getIdString());
					els.setAttribute("name", d.getText());
				}
			}
		} finally {

		}
	}

	protected void getDeviceTypeAlarms(AdminSessionObject session, String id,
			Document xmlDoc, Element content) throws NamingException {
		if (!((AdminSessionObject) session).hasRight(UserRight.DEVICETYPE_EDIT))
			throw new KoneException("您无编辑模块的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			DeviceTypeBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-deviceTypeBean", DeviceTypeBeanRemote.class);
			Iterator<?> it = bean.getAlarmCodeList(
					((AdminSessionObject) session).getLoginUser().getUserId(),
					Integer.valueOf(id)).iterator();
			while (it.hasNext()) {
				BaseAlarmCode t = (BaseAlarmCode) it.next();
				Element e = xmlDoc.createElement("alarmcode");
				content.appendChild(e);
				e.setAttribute("id", Integer.toString(t.getAlarmCode()));
				e.setAttribute("alarmcategoryid", t.getAlarmCategory()
						.getIdString());
				e.setAttribute("desp",
						t.getAlarmDesp() + "[" + t.getAlarmCode() + "]");
				e.setAttribute("alarmcategory", t.getAlarmCategory()
						.getAlarmCategoryDesp());
			}
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void getTermTypeDevices(AdminSessionObject session, String id,
			Document xmlDoc, Element content) throws NamingException {
		if (!((AdminSessionObject) session).hasRight(UserRight.TERMTYPE_EDIT))
			throw new KoneException("您无编辑终端型号的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			TermTypeBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-termTypeBean", TermTypeBeanRemote.class);
			Iterator<BaseTermTypeDevice> it = bean.getTermTypeDeviceList(
					((AdminSessionObject) session).getLoginUser().getUserId(),
					Integer.valueOf(id)).iterator();
			while (it.hasNext()) {
				BaseTermTypeDevice t = it.next();
				Element e = xmlDoc.createElement("device");
				content.appendChild(e);
				e.setAttribute("id", t.getDevice().getIdString());
				e.setAttribute("name", t.getDevice().getDeviceName());
			}
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void deleteAppCategory(AdminSessionObject session, String id)
			throws NamingException {
		if (!((AdminSessionObject) session)
				.hasRight(UserRight.APPCATEGORY_EDIT))
			throw new KoneException("您无删除应用分类的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			AppCategoryBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-appCategoryBean", AppCategoryBeanRemote.class);
			bean.delete(((AdminSessionObject) session).getLoginUser()
					.getUserId(), StringUnit.splitToInt2(id, ","));
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void deleteApp(AdminSessionObject session, String id)
			throws NamingException {
		if (!((AdminSessionObject) session).hasRight(UserRight.APP_EDIT))
			throw new KoneException("您无删除应用的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			AppBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-appBean", AppBeanRemote.class);
			bean.delete(((AdminSessionObject) session).getLoginUser()
					.getUserId(), StringUnit.splitToInt2(id, ","));
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void deleteBusiness(AdminSessionObject session, String id)
			throws NamingException {
		if (!((AdminSessionObject) session).hasRight(UserRight.BUSINESS_EDIT))
			throw new KoneException("您无删除业务的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			BusinessBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-businessBean", BusinessBeanRemote.class);
			bean.delete(((AdminSessionObject) session).getLoginUser()
					.getUserId(), StringUnit.splitToInt2(id, ","));
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void deleteBusinessCategory(AdminSessionObject session, String id)
			throws NamingException {
		if (!((AdminSessionObject) session)
				.hasRight(UserRight.BUSINESSCATEGORY_EDIT))
			throw new KoneException("您无删除业务分类的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			BusinessCategoryBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-businessCategoryBean",
					BusinessCategoryBeanRemote.class);
			bean.delete(((AdminSessionObject) session).getLoginUser()
					.getUserId(), StringUnit.splitToInt2(id, ","));
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void deletePageElement(AdminSessionObject session, String id)
			throws NamingException {
		if (!((AdminSessionObject) session)
				.hasRight(UserRight.PAGEELEMENT_EDIT))
			throw new KoneException("您无删除页面元素的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			PageElementBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-pageElementBean", PageElementBeanRemote.class);
			bean.delete(((AdminSessionObject) session).getLoginUser()
					.getUserId(), StringUnit.splitToInt(id, ","));
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void deletePayItem(AdminSessionObject session, String id)
			throws NamingException {
		if (!((AdminSessionObject) session).hasRight(UserRight.PAYITEM_EDIT))
			throw new KoneException("您无删除交费项目的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			PayItemBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-payItemBean", PayItemBeanRemote.class);
			bean.delete(((AdminSessionObject) session).getLoginUser()
					.getUserId(), StringUnit.splitToShort2(id, ","));
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void deletePayWay(AdminSessionObject session, String id)
			throws NamingException {
		if (!((AdminSessionObject) session).hasRight(UserRight.PAYWAY_EDIT))
			throw new KoneException("您无删除交费渠道的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			PayWayBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-payWayBean", PayWayBeanRemote.class);
			bean.delete(((AdminSessionObject) session).getLoginUser()
					.getUserId(), StringUnit.splitToShort2(id, ","));
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void deleteTradeCode(AdminSessionObject session, String id)
			throws NamingException {
		if (!((AdminSessionObject) session).hasRight(UserRight.TRADECODE_EDIT))
			throw new KoneException("您无删除交易代码的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			TradeCodeBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-tradeCodeBean", TradeCodeBeanRemote.class);
			bean.delete(((AdminSessionObject) session).getLoginUser()
					.getUserId(), StringUnit.splitToInt2(id, ","));
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void deleteDeviceTypeDriver(AdminSessionObject session, String id)
			throws NamingException {
		if (!((AdminSessionObject) session).hasRight(UserRight.DEVICE_EDIT))
			throw new KoneException("您无删除硬件的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			DeviceTypeDriverBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-deviceTypeDriverBean",
					DeviceTypeDriverBeanRemote.class);
			String[] ls = bean.delete(((AdminSessionObject) session)
					.getLoginUser().getUserId(), StringUnit
					.splitToInt2(id, ","));
			FileProcessor processor = new FileProcessor(
					AdminTradeExecutor.fileUserId, AdminTradeExecutor.filePwd,
					(short) 2);
			for (int i = 0; i < ls.length; i++) {
				try {
					processor.deleteFile(((AdminSessionObject) session)
							.getLoginUser().getUserId(), ls[i], false);
				} catch (Throwable e) {
				}
			}
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void deleteDeviceDriver(AdminSessionObject session, String id)
			throws NamingException {
		if (!((AdminSessionObject) session).hasRight(UserRight.DEVICE_EDIT))
			throw new KoneException("您无删除硬件的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			DeviceDriverBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-deviceDriverBean",
					DeviceDriverBeanRemote.class);
			String[] ls = bean.delete(((AdminSessionObject) session)
					.getLoginUser().getUserId(), StringUnit
					.splitToInt2(id, ","));
			FileProcessor processor = new FileProcessor(
					AdminTradeExecutor.fileUserId, AdminTradeExecutor.filePwd,
					(short) 3);
			for (int i = 0; i < ls.length; i++)
				try {
					processor.deleteFile(((AdminSessionObject) session)
							.getLoginUser().getUserId(), ls[i], false);
				} catch (Throwable e) {
				}
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void deleteDevice(AdminSessionObject session, String id)
			throws NamingException {
		if (!((AdminSessionObject) session).hasRight(UserRight.DEVICE_EDIT))
			throw new KoneException("您无删除硬件的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			DeviceBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-deviceBean", DeviceBeanRemote.class);
			bean.delete(((AdminSessionObject) session).getLoginUser()
					.getUserId(), StringUnit.splitToInt2(id, ","));
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void deleteDeviceType(AdminSessionObject session, String id)
			throws NamingException {
		if (!((AdminSessionObject) session).hasRight(UserRight.DEVICETYPE_EDIT))
			throw new KoneException("您无删除模块的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			DeviceTypeBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-deviceTypeBean", DeviceTypeBeanRemote.class);
			bean.delete(((AdminSessionObject) session).getLoginUser()
					.getUserId(), StringUnit.splitToInt2(id, ","));
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void deleteTermTypeDevice(AdminSessionObject session,
			String typeId, String id) throws NamingException {
		if (!((AdminSessionObject) session).hasRight(UserRight.TERMTYPE_EDIT))
			throw new KoneException("您没有删除型号模块的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			TermTypeBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-termTypeBean", TermTypeBeanRemote.class);
			bean.deleteTermTypeDevice(((AdminSessionObject) session)
					.getLoginUser().getUserId(), Integer.parseInt(typeId),
					StringUnit.splitToInt(id, ","));
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void deleteDeviceTypeAlarms(AdminSessionObject session,
			int deviceType, String id) throws NamingException {
		if (!((AdminSessionObject) session).hasRight(UserRight.TERMTYPE_EDIT))
			throw new KoneException("您没有删除模块的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			DeviceTypeBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-deviceTypeBean", DeviceTypeBeanRemote.class);
			bean.deleteAlarmCode(((AdminSessionObject) session).getLoginUser()
					.getUserId(), deviceType, StringUnit.splitToInt(id, ","));
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void deleteAppFiles(AdminSessionObject session, String id)
			throws NamingException {
		if (!((AdminSessionObject) session).hasRight(UserRight.TERMTYPE_EDIT))
			throw new KoneException("您没有删除应用文件的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			AppBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-appBean", AppBeanRemote.class);
			String[] ls = bean
					.deleteAppFile(((AdminSessionObject) session)
							.getLoginUser().getUserId(), StringUnit.splitToInt(
							id, ","));
			FileProcessor processor = new FileProcessor(
					AdminTradeExecutor.fileUserId, AdminTradeExecutor.filePwd,
					(short) 1);
			for (int i = 0; i < ls.length; i++)
				try {
					processor.deleteFile(((AdminSessionObject) session)
							.getLoginUser().getUserId(), ls[i], false);
				} catch (Throwable e) {
				}
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void deleteDeviceDriverFiles(AdminSessionObject session, String id)
			throws NamingException {
		if (!((AdminSessionObject) session)
				.hasRight(UserRight.DEVICEDRIVER_EDIT))
			throw new KoneException("您没有删除驱动文件的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			DeviceDriverBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-deviceDriverBean",
					DeviceDriverBeanRemote.class);
			String f = bean.deleteDeviceDriverFile(
					((AdminSessionObject) session).getLoginUser().getUserId(),
					Integer.valueOf(id));
			FileProcessor processor = new FileProcessor(
					AdminTradeExecutor.fileUserId, AdminTradeExecutor.filePwd,
					(short) 3);
			try {
				processor.deleteFile(((AdminSessionObject) session)
						.getLoginUser().getUserId(), f, false);
			} catch (Throwable e) {
			}
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void deleteTerm(AdminSessionObject session, String id)
			throws NamingException {
		if (!((AdminSessionObject) session).hasRight(UserRight.TERM_EDIT))
			throw new KoneException("您无删除终端的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			TermBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-termBean", TermBeanRemote.class);
			bean.delete(((AdminSessionObject) session).getLoginUser()
					.getUserId(), StringUnit.splitToInt2(id, ","));
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void deleteBankTerm(AdminSessionObject session, String id)
			throws NamingException {
		if (!((AdminSessionObject) session).hasRight(UserRight.BANKTERM_EDIT))
			throw new KoneException("您无删除银联终端的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			BankTermBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-bankTermBean", BankTermBeanRemote.class);
			bean.delete(((AdminSessionObject) session).getLoginUser()
					.getUserId(), StringUnit.splitToInt2(id, ","));
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void deleteDisabledPrintUser(AdminSessionObject session, String id)
			throws NamingException {
		if (!((AdminSessionObject) session).hasRight(UserRight.PRINTTYPE_EDIT))
			throw new KoneException("您无删除的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			DisabledPrintUserBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-disabledPrintUserBean",
					DisabledPrintUserBeanRemote.class);
			bean.delete(((AdminSessionObject) session).getLoginUser()
					.getUserId(), StringUnit.splitToLong(id, ","));
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void deleteAlarmCategory(AdminSessionObject session, String id)
			throws NamingException {
		if (!((AdminSessionObject) session)
				.hasRight(UserRight.ALARMCATEGORY_EDIT))
			throw new KoneException("您无删除告警类型的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			AlarmCategoryBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-alarmCategoryBean",
					AlarmCategoryBeanRemote.class);
			bean.delete(((AdminSessionObject) session).getLoginUser()
					.getUserId(), StringUnit.splitToInt2(id, ","));
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void deleteFileCategory(AdminSessionObject session, String id)
			throws NamingException {
		if (!((AdminSessionObject) session)
				.hasRight(UserRight.FILECATEGORY_EDIT))
			throw new KoneException("您无删除文件类型的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			FileCategoryBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-fileCategoryBean",
					FileCategoryBeanRemote.class);
			bean.delete(((AdminSessionObject) session).getLoginUser()
					.getUserId(), StringUnit.splitToShort2(id, ","));
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void deleteFileOwner(AdminSessionObject session, String id)
			throws NamingException {
		if (!((AdminSessionObject) session).hasRight(UserRight.FILEOWNER_EDIT))
			throw new KoneException("您无删除文件属主的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			FileOwnerBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-fileOwnerBean", FileOwnerBeanRemote.class);
			bean.delete(((AdminSessionObject) session).getLoginUser()
					.getUserId(), StringUnit.splitToShort2(id, ","));
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void deleteFileHost(AdminSessionObject session, String id)
			throws NamingException {
		if (!((AdminSessionObject) session).hasRight(UserRight.FILEHOST_EDIT))
			throw new KoneException("您无删除文件主机的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			FileHostBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-fileHostBean", FileHostBeanRemote.class);
			bean.delete(((AdminSessionObject) session).getLoginUser()
					.getUserId(), StringUnit.splitToShort2(id, ","));
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void deleteFileUser(AdminSessionObject session, String id)
			throws NamingException {
		if (!((AdminSessionObject) session).hasRight(UserRight.FILEUSER_EDIT))
			throw new KoneException("您无删除文件用户的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			FileUserBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-fileUserBean", FileUserBeanRemote.class);
			bean.delete(((AdminSessionObject) session).getLoginUser()
					.getUserId(), StringUnit.split(id, ","));
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void deletePrintType(AdminSessionObject session, String id)
			throws NamingException {
		if (!((AdminSessionObject) session).hasRight(UserRight.PRINTTYPE_EDIT))
			throw new KoneException("您无删除打印类型的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			PrintTypeBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-printTypeBean", PrintTypeBeanRemote.class);
			bean.delete(((AdminSessionObject) session).getLoginUser()
					.getUserId(), StringUnit.splitToShort2(id, ","));
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void deleteTermType(AdminSessionObject session, String id)
			throws NamingException {
		if (!((AdminSessionObject) session).hasRight(UserRight.TERMTYPE_EDIT))
			throw new KoneException("您无删除终端型号的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			TermTypeBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-termTypeBean", TermTypeBeanRemote.class);
			bean.delete(((AdminSessionObject) session).getLoginUser()
					.getUserId(), StringUnit.splitToInt2(id, ","));
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void deleteManuf(AdminSessionObject session, String id)
			throws NamingException {
		if (!((AdminSessionObject) session).hasRight(UserRight.MANUF_EDIT))
			throw new KoneException("您无删除厂商的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			ManufBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-manufBean", ManufBeanRemote.class);
			bean.delete(((AdminSessionObject) session).getLoginUser()
					.getUserId(), StringUnit.splitToInt2(id, ","));
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void deleteOrg(AdminSessionObject session, String id)
			throws NamingException {
		if (!((AdminSessionObject) session).hasRight(UserRight.ORG_EDIT))
			throw new KoneException("您无删除机构的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			OrgBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-orgBean", OrgBeanRemote.class);
			bean.delete(((AdminSessionObject) session).getLoginUser()
					.getUserId(), StringUnit.splitToInt2(id, ","));
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void deleteRole(AdminSessionObject session, String id)
			throws NamingException {
		if (!((AdminSessionObject) session).hasRight(UserRight.ROLE_EDIT))
			throw new KoneException("您无删除角色的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			RoleBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-roleBean", RoleBeanRemote.class);
			bean.delete(((AdminSessionObject) session).getLoginUser()
					.getUserId(), StringUnit.splitToInt(id, ","));
		} finally {
			if (context != null)
				context.close();
		}
	}

	protected void deleteUser(AdminSessionObject session, String id)
			throws NamingException {
		if (!((AdminSessionObject) session).hasRight(UserRight.USER_EDIT))
			throw new KoneException("您无删除用户的权限.");
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			UserBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-userBean", UserBeanRemote.class);
			bean.delete(((AdminSessionObject) session).getLoginUser()
					.getUserId(), StringUnit.splitToLong(id, ","));
		} finally {
			if (context != null)
				context.close();
		}
	}

}
