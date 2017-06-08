package kxd.engine.scs.admin;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import kxd.engine.cache.beans.sts.CachedAlarmCategory;
import kxd.engine.cache.beans.sts.CachedApp;
import kxd.engine.cache.beans.sts.CachedDeviceDriver;
import kxd.engine.cache.beans.sts.CachedDeviceType;
import kxd.engine.cache.beans.sts.CachedDeviceTypeDriver;
import kxd.engine.cache.beans.sts.CachedFileOwner;
import kxd.engine.cache.beans.sts.CachedManuf;
import kxd.engine.cache.beans.sts.CachedPrintType;
import kxd.engine.cache.beans.sts.CachedTermType;
import kxd.engine.helper.AdminHelper;
import kxd.engine.helper.CacheHelper;
import kxd.engine.ui.core.ControlServlet;
import kxd.engine.ui.core.SessionObject;
import kxd.engine.ui.tags.website.FuncTreeNode;
import kxd.engine.ui.tags.website.OrgTreeNode;
import kxd.json.JSONException;
import kxd.json.JSONObject;
import kxd.net.HttpRequest;
import kxd.net.connection.jndi.Lookuper;
import kxd.net.naming.LoopNamingContext;
import kxd.net.naming.NamingContext;
import kxd.remote.scs.beans.BaseAdCategory;
import kxd.remote.scs.beans.BaseAppCategory;
import kxd.remote.scs.beans.BaseBusiness;
import kxd.remote.scs.beans.BaseBusinessCategory;
import kxd.remote.scs.beans.BaseInfoCategory;
import kxd.remote.scs.beans.BasePayItem;
import kxd.remote.scs.beans.BasePayWay;
import kxd.remote.scs.beans.BasePrintAdCategory;
import kxd.remote.scs.beans.right.EditedFunction;
import kxd.remote.scs.beans.right.LoginUser;
import kxd.remote.scs.beans.right.QueryedOrg;
import kxd.remote.scs.beans.right.QueryedUser;
import kxd.remote.scs.interfaces.AdCategoryBeanRemote;
import kxd.remote.scs.interfaces.AppCategoryBeanRemote;
import kxd.remote.scs.interfaces.BusinessBeanRemote;
import kxd.remote.scs.interfaces.BusinessCategoryBeanRemote;
import kxd.remote.scs.interfaces.InfoCategoryBeanRemote;
import kxd.remote.scs.interfaces.PayItemBeanRemote;
import kxd.remote.scs.interfaces.PayWayBeanRemote;
import kxd.remote.scs.interfaces.PrintAdCategoryBeanRemote;
import kxd.remote.scs.interfaces.UserBeanRemote;
import kxd.remote.scs.interfaces.service.SystemServiceBeanRemote;
import kxd.remote.scs.util.emun.AlarmLevel;
import kxd.remote.scs.util.emun.CashFlag;
import kxd.remote.scs.util.emun.FaultPromptOption;
import kxd.remote.scs.util.emun.FileCachedType;
import kxd.remote.scs.util.emun.FileVisitRight;
import kxd.remote.scs.util.emun.FixType;
import kxd.remote.scs.util.emun.PayWayType;
import kxd.remote.scs.util.emun.RecStatus;
import kxd.remote.scs.util.emun.RefundStatus;
import kxd.remote.scs.util.emun.SettlementType;
import kxd.remote.scs.util.emun.TermStatus;
import kxd.remote.scs.util.emun.UserGroup;
import kxd.util.DataUnit;
import kxd.util.DateTime;
import kxd.util.session.MemCachedHttpSessionWrapper;

import org.apache.log4j.Logger;

public class AdminSessionObject implements SessionObject {
	static Logger logger = Logger.getLogger(AdminSessionObject.class);
	LoginUser loginUser;
	static FuncTreeNode funcTree = null;
	static FuncTreeNode optionFuncTree = null;
	// UserAttribute attr;
	QueryedUser detailUser;
	String funclogintreedata;
	HttpSession session;
	String regTime;
	String lastInlineTime;
	static private Map<Integer, Map<String, String>> paramsMap = null;

	public static synchronized Map<String, String> getParamConfig(
			int paramCategoryId) {
		if (paramsMap == null) {
			paramsMap = new ConcurrentHashMap<Integer, Map<String, String>>();
			try {
				NamingContext context = new LoopNamingContext("db");
				try {
					SystemServiceBeanRemote bean = (SystemServiceBeanRemote) context
							.lookup(Lookuper.JNDI_TYPE_EJB,
									"kxd-ejb-systemServiceBean",
									SystemServiceBeanRemote.class);
					Integer v = null;
					ConcurrentHashMap<String, String> map = null;
					for (Object o : bean.getParamsConfig()) {
						Object[] s = (Object[]) o;
						Integer index = Integer.valueOf(s[0].toString());
						if (v == null || !v.equals(index)) {
							v = index;
							map = new ConcurrentHashMap<String, String>();
							paramsMap.put(v, map);
						}
						map.put(s[1].toString(), s[2].toString());
					}
				} finally {
					context.close();
				}
			} catch (Throwable e) {
			}
		}
		return paramsMap.get(paramCategoryId);
	}

	public String getRegTime() {
		return DataUnit.formatDateTime(detailUser.getRegTime(),
				"yyyy-MM-dd HH:mm:ss");
	}

	public String getLastInlineTime() {
		return DataUnit.formatDateTime(detailUser.getLastInlineTime(),
				"yyyy-MM-dd HH:mm:ss");
	}

	public static void setFuncTreeNull() {
		funcTree = null;
		optionFuncTree = null;
	}

	public AdminSessionObject(HttpRequest request, HttpServletResponse response) {
		init();
		session = request.getRequest().getSession();
		loginUser = (LoginUser) getAttribute("loginUser");
		funclogintreedata = (String) session.getAttribute("logintreedata");
		// attr = (UserAttribute) getAttribute("loginAttr");
		// if (attr == null) {
		// attr = new UserAttribute();
		// setAttribute("loginAttr", attr);
		// }
	}

	public Serializable getAttribute(String name) {
		return (Serializable) session.getAttribute(name);
	}

	public void setAttribute(String name, Serializable value) {
		session.setAttribute(name, value);
	}

	public Serializable removeAttribute(String name) {
		Serializable v = (Serializable) session.getAttribute(name);
		session.removeAttribute(name);
		return v;
	}

	public void clearAttributes() throws NamingException {
		ArrayList<Serializable> c = new ArrayList<Serializable>();
		c.add("loginUser");
		// c.add("loginattr");
		c.add("logintreedata");
		((MemCachedHttpSessionWrapper) session).clearAttributes(c);
	}

	public static AdminSessionObject getCurrentSession(
			HttpServletRequest request) {
		return (AdminSessionObject) request.getAttribute("managerSession");
	}

	public boolean hasRight(int right) {
		return loginUser != null
				&& (loginUser.getUserGroup().isSuperManager() || loginUser
						.getFuncCollection().contains(right));
	}

	/**
	 * 获取系统的所有功能树
	 * 
	 * @throws NamingException
	 */
	public static synchronized FuncTreeNode getFuncTree()
			throws NamingException {
		if (funcTree == null) {
			LoopNamingContext context = new LoopNamingContext("db");
			try {
				UserBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
						"kxd-ejb-userBean", UserBeanRemote.class);
				FuncTreeNode node = new FuncTreeNode();
				node.setExpanded(true);
				Collection<EditedFunction> c = bean.getAllFunction();
				node.add(c, true);
				funcTree = node;
				optionFuncTree = new FuncTreeNode();
				optionFuncTree.addRighted(c);

			} catch (Throwable e) {
				if (logger.isDebugEnabled())
					e.printStackTrace();
			} finally {
				if (context != null)
					context.close();
			}
		}
		return funcTree;
	}

	/**
	 * 获取操作的功能树
	 * 
	 * @return
	 * @throws NamingException
	 */

	private FuncTreeNode getOptionFuncTree() throws NamingException {
		LoginUser user = loginUser;
		if (user == null)
			return null;
		getFuncTree();
		if (optionFuncTree == null)
			return null;
		FuncTreeNode root = (FuncTreeNode) optionFuncTree.copy();
		root.disableAll();
		Collection<Integer> c = user.getFuncCollection();
		if (c != null) {
			Iterator<Integer> it = c.iterator();
			while (it.hasNext()) {
				Integer id = it.next();
				FuncTreeNode node = (FuncTreeNode) root.find(id, true);
				if (node != null)
					node.setDisabled(false);
			}
			root.cleanChildrenDisabledNodes();
			return root;
		}
		return null;
	}

	public FuncTreeNode getLoginUserFuncTree() throws NamingException {
		LoginUser user = loginUser;
		if (user == null)
			return null;
		FuncTreeNode root = getFuncTree();
		if (root == null)
			return null;
		root = (FuncTreeNode) root.copy();
		root.disableAll();
		Collection<Integer> c = user.getFuncCollection();
		if (c != null) {
			Iterator<Integer> it = c.iterator();
			while (it.hasNext()) {
				Integer id = it.next();
				FuncTreeNode node = (FuncTreeNode) root.find(id, true);
				if (node != null)
					node.setDisabled(false);
			}
			root.cleanChildrenDisabledNodes();
			return root;
		}
		return null;
	}

	public OrgTreeNode getLoginUserOrgTree() throws NamingException {
		LoginUser user = loginUser;
		if (user == null)
			return null;
		Integer orgId = null;
		if (loginUser.getOrg().getId() != null)
			orgId = loginUser.getOrg().getId();
		Collection<QueryedOrg> c = AdminHelper.getOrgTreeItems(orgId, 2, null,
				true, null);
		OrgTreeNode node = new OrgTreeNode();
		node.add(c);
		return node;

	}

	/**
	 * 获取当前用户可以赋权的权限
	 * 
	 * @return
	 * @throws NamingException
	 */
	public FuncTreeNode getCurCustomFuncTree() throws NamingException {
		FuncTreeNode curFuncTree = null;
		LoginUser user = getLoginUser();
		if (user == null)
			return null;
		FuncTreeNode root = getFuncTree();
		if (root == null)
			return null;
		root = (FuncTreeNode) root.copy();
		root.disableAll();
		Collection<Integer> c = user.getFuncCollection();
		if (c != null) {
			Iterator<Integer> it = c.iterator();
			while (it.hasNext()) {
				Integer id = it.next();
				FuncTreeNode node = (FuncTreeNode) root.find(id, true);
				if (node != null && node.isCustomEnabled())
					node.setDisabled(false);
			}
			root.cleanChildrenDisabledNodes();
			curFuncTree = root;
		}
		return curFuncTree;
	}

	public LoginUser getLoginUser() {
		return loginUser;
	}

	public String getFuncLoginTreedata() throws JSONException {
		return funclogintreedata;
	}

	public void setLoginUser(LoginUser loginUser) throws NamingException,
			JSONException {
		this.loginUser = loginUser;
		setAttribute("loginUser", loginUser);
		if (loginUser != null) {
			ArrayList<EditedFunction> funcList = new ArrayList<EditedFunction>();
			getOptionFuncTree().addEnabledChildrenDataTo(funcList);
			Iterator<EditedFunction> it = funcList.iterator();
			while (it.hasNext()) {
				EditedFunction ef = it.next();
				if (ef.getFuncUrl() != null && ef.getFuncUrl().endsWith(".jsf")) {
					ef.setFuncUrl(ef.getFuncUrl().substring(0,
							ef.getFuncUrl().length() - 4)
							+ ".go");
				}
			}
			FuncTreeNode root = new FuncTreeNode();
			root.add((Collection<EditedFunction>) funcList, false);
			root.setExpanded(true);
			JSONObject o = new JSONObject();
			root.toJson(o, 0, 10000);
			setAttribute("logintreedata", o.getJSONArray("children").toString());
		} else
			setAttribute("logintreedata", null);
	}

	public boolean isLogined() {
		return loginUser != null && loginUser.getUserId() != null;
	}

	// public UserAttribute getAttr() {
	// return attr;
	// }

	public QueryedUser getDetailUser() throws NamingException {
		if (detailUser == null) {
			LoopNamingContext context = new LoopNamingContext("db");
			try {
				UserBeanRemote userBean = context.lookup(
						Lookuper.JNDI_TYPE_EJB, "kxd-ejb-userBean",
						UserBeanRemote.class);
				detailUser = userBean.find(loginUser.getId());
			} finally {
				context.close();
			}
		}
		return detailUser;
	}

	// public ArrayList<EditedFunction> getFuncList() {
	// return funcList;
	// }

	public Collection<CachedManuf> getManufList() throws NamingException {
		if (loginUser.isLogined()
				&& (loginUser.getUserGroup().isSystemManager() || loginUser
						.getManuf().getId() == null)) {
			Collection<CachedManuf> c = new ArrayList<CachedManuf>();
			Enumeration<Integer> en = CacheHelper.manufMap.keys();
			while (en.hasMoreElements()) {
				c.add(CacheHelper.manufMap.get(en.nextElement()));
			}
			return c;
		} else if (loginUser.getManuf().getManufId() != null) {
			ArrayList<CachedManuf> ls = new ArrayList<CachedManuf>();
			CachedManuf manuf = new CachedManuf();
			manuf.setId(loginUser.getManuf().getId());
			manuf.setManufName(loginUser.getManuf().getManufName());
			ls.add(manuf);
			return ls;
		} else
			return null;
	}

	public List<BaseAppCategory> getAppCategoryList() throws NamingException {
		if (loginUser.isLogined()) {
			LoopNamingContext context = new LoopNamingContext("db");
			try {
				AppCategoryBeanRemote bean = context.lookup(
						Lookuper.JNDI_TYPE_EJB, "kxd-ejb-appCategoryBean",
						AppCategoryBeanRemote.class);
				return bean.getAppCategoryList(loginUser.getUserId(), null);
			} finally {
				context.close();
			}
		} else
			return null;
	}

	public List<BaseBusinessCategory> getBusinessCategoryList()
			throws NamingException {
		if (loginUser.isLogined()) {
			LoopNamingContext context = new LoopNamingContext("db");
			try {
				BusinessCategoryBeanRemote bean = context.lookup(
						Lookuper.JNDI_TYPE_EJB, "kxd-ejb-businessCategoryBean",
						BusinessCategoryBeanRemote.class);
				return bean
						.getBusinessCategoryList(loginUser.getUserId(), null);
			} finally {
				context.close();
			}
		} else
			return null;
	}

	static protected List<BooleanItem> booleanItems = new CopyOnWriteArrayList<BooleanItem>();
	static protected List<ShortItem> notifyOptionItems = new CopyOnWriteArrayList<ShortItem>();
	static protected List<IntegerItem> alarmLevelItems = new CopyOnWriteArrayList<IntegerItem>();
	static protected List<IntegerItem> faultPromptOptionItems = new CopyOnWriteArrayList<IntegerItem>();
	static protected List<IntegerItem> fixTypeItems = new CopyOnWriteArrayList<IntegerItem>();
	static protected List<IntegerItem> cashFlagItems = new CopyOnWriteArrayList<IntegerItem>();
	static protected List<IntegerItem> termStatusItems = new CopyOnWriteArrayList<IntegerItem>();
	static protected List<IntegerItem> comPortItems = new CopyOnWriteArrayList<IntegerItem>();
	static protected List<IntegerItem> reportScopeItems = new CopyOnWriteArrayList<IntegerItem>();
	static protected List<IntegerItem> dayReportScopeItems = new CopyOnWriteArrayList<IntegerItem>();
	static protected List<IntegerItem> fileVisitRightItems = new CopyOnWriteArrayList<IntegerItem>();
	static protected List<IntegerItem> fileCachedTypeItems = new CopyOnWriteArrayList<IntegerItem>();
	static protected List<StringItem> timeItems = new CopyOnWriteArrayList<StringItem>();
	static protected List<IntegerItem> payStatusItems = new CopyOnWriteArrayList<IntegerItem>();
	static protected List<IntegerItem> refundStatusItems = new CopyOnWriteArrayList<IntegerItem>();
	static protected List<IntegerItem> printTypeItems = new CopyOnWriteArrayList<IntegerItem>();
	static protected List<IntegerItem> tradeStatusItems = new CopyOnWriteArrayList<IntegerItem>();
	static protected List<IntegerItem> recStatusItems = new CopyOnWriteArrayList<IntegerItem>();
	static protected List<IntegerItem> settlementTypeItems = new CopyOnWriteArrayList<IntegerItem>();
	static protected List<IntegerItem> payWayTypeItems = new CopyOnWriteArrayList<IntegerItem>();

	static protected boolean inited = false;

	synchronized protected static void init() {
		if (!inited) {
			try {
				payStatusItems.clear();
				payStatusItems.add(new IntegerItem(0, "未支付"));
				payStatusItems.add(new IntegerItem(1, "支付成功"));
				payStatusItems.add(new IntegerItem(2, "对账后支付成功"));
				payStatusItems.add(new IntegerItem(3, "支付超时"));
				payStatusItems.add(new IntegerItem(4, "支付失败"));

				refundStatusItems.clear();
				RefundStatus[] rss = RefundStatus.values();
				for (int i = 0; i < rss.length; i++) {
					refundStatusItems.add(new IntegerItem(i, RefundStatus
							.valueOf(rss[i].getValue()).toString()));
				}

				tradeStatusItems.clear();
				tradeStatusItems.add(new IntegerItem(0, "未交易"));
				tradeStatusItems.add(new IntegerItem(1, "交易成功"));
				tradeStatusItems.add(new IntegerItem(2, "补交后交易成功"));
				tradeStatusItems.add(new IntegerItem(3, "对账后交易成功"));
				tradeStatusItems.add(new IntegerItem(4, "交易超时"));
				tradeStatusItems.add(new IntegerItem(5, "交易失败"));

				recStatusItems.clear();
				RecStatus[] rec = RecStatus.values();
				for (int i = 0; i < rec.length; i++) {
					recStatusItems.add(new IntegerItem(i, RecStatus.valueOf(
							rec[i].getValue()).toString()));
				}

				timeItems.clear();
				for (int i = 0; i < 24; i++) {
					String h = Integer.toString(i);
					if (i < 10)
						h = "0" + i;
					timeItems.add(new StringItem(h + ":00", h + ":00"));
					timeItems.add(new StringItem(h + ":30", h + ":30"));
				}
				timeItems.add(new StringItem("23:59", "23:59"));

				booleanItems.clear();
				booleanItems.add(new BooleanItem(true));
				booleanItems.add(new BooleanItem(false));

				dayReportScopeItems.clear();
				dayReportScopeItems.add(new IntegerItem(0, "日报表"));

				reportScopeItems.clear();
				reportScopeItems.add(new IntegerItem(0, "日报表"));
				reportScopeItems.add(new IntegerItem(1, "月报表"));
				reportScopeItems.add(new IntegerItem(2, "年报表"));

				notifyOptionItems.clear();
				notifyOptionItems.add(new ShortItem((short) -1, "不通知"));
				notifyOptionItems.add(new ShortItem((short) 0, "通知到维护人员"));
				notifyOptionItems.add(new ShortItem((short) 1, "通知到营业厅管理员"));
				notifyOptionItems.add(new ShortItem((short) 2, "通知到地市管理员"));
				notifyOptionItems.add(new ShortItem((short) 3, "通知到省分中心管理员"));
				notifyOptionItems.add(new ShortItem((short) 4, "通知到总部管理员"));
				notifyOptionItems.add(new ShortItem((short) 5, "通知到系统管理员"));

				payWayTypeItems.clear();
				PayWayType[] pws = PayWayType.values();
				for (int i = 0; i < pws.length; i++)
					payWayTypeItems.add(new IntegerItem(pws[i].ordinal(),
							pws[i].toString()));

				faultPromptOptionItems.clear();
				FaultPromptOption[] vs = FaultPromptOption.values();
				for (int i = 0; i < vs.length; i++)
					faultPromptOptionItems.add(new IntegerItem(vs[i].ordinal(),
							vs[i].toString()));

				fixTypeItems.clear();
				FixType[] fvs = FixType.values();
				for (int i = 0; i < fvs.length; i++)
					fixTypeItems.add(new IntegerItem(fvs[i].ordinal(), fvs[i]
							.toString()));

				cashFlagItems.clear();
				CashFlag[] cvs = CashFlag.values();
				for (int i = 0; i < cvs.length; i++)
					cashFlagItems.add(new IntegerItem(cvs[i].ordinal(), cvs[i]
							.toString()));

				termStatusItems.clear();
				TermStatus[] tvs = TermStatus.values();
				for (int i = 0; i < tvs.length; i++)
					termStatusItems.add(new IntegerItem(tvs[i].ordinal(),
							tvs[i].toString()));

				comPortItems.clear();
				for (int i = 1; i < 16; i++)
					comPortItems.add(new IntegerItem(i, "COM" + i));
				for (int i = 1; i < 8; i++)
					comPortItems.add(new IntegerItem(i + 100, "LPT" + i));

				alarmLevelItems.clear();
				AlarmLevel[] als = AlarmLevel.values();
				for (int i = 0; i < als.length; i++)
					alarmLevelItems.add(new IntegerItem(als[i].ordinal(),
							als[i].toString()));

				fileVisitRightItems.clear();
				FileVisitRight[] fvr = FileVisitRight.values();
				for (int i = 0; i < fvr.length; i++)
					fileVisitRightItems.add(new IntegerItem(fvr[i].ordinal(),
							fvr[i].toString()));

				fileCachedTypeItems.clear();
				FileCachedType[] fct = FileCachedType.values();
				for (int i = 0; i < fct.length; i++)
					fileCachedTypeItems.add(new IntegerItem(fct[i].ordinal(),
							fct[i].toString()));

				settlementTypeItems.clear();
				SettlementType[] st = SettlementType.values();
				for (int i = 0; i < st.length; i++)
					settlementTypeItems.add(new IntegerItem(st[i].ordinal(),
							st[i].toString()));

				printTypeItems.clear();
				Collection<CachedPrintType> cachedPrint = CacheHelper.printTypeMap
						.values();
				Iterator<CachedPrintType> iter = cachedPrint.iterator();
				while (iter.hasNext()) {
					CachedPrintType printType = iter.next();
					printTypeItems.add(new IntegerItem(printType.getId()
							.intValue(), printType.getPrintTypeDesp()));
				}
			} finally {
				inited = true;
			}
		}
	}

	public List<IntegerItem> getRecStatusItems() {
		return recStatusItems;
	}

	public List<IntegerItem> getTradeStatusItems() {
		return tradeStatusItems;
	}

	public List<IntegerItem> getPrintTypeItems() {
		return printTypeItems;
	}

	public List<IntegerItem> getSettlementTypeItems() {
		return settlementTypeItems;
	}

	public List<IntegerItem> getPayWayTypeItems() {
		return payWayTypeItems;
	}

	public List<IntegerItem> getPayStatusItems() {
		return payStatusItems;
	}

	public List<IntegerItem> getRefundStatusItems() {
		return refundStatusItems;
	}

	public List<StringItem> getTimeItems() {
		return timeItems;
	}

	public List<IntegerItem> getFileCachedTypeItems() {
		return fileCachedTypeItems;
	}

	public List<IntegerItem> getFileVisitRightItems() {
		return fileVisitRightItems;
	}

	public List<IntegerItem> getDayReportScopeItems() {
		return dayReportScopeItems;
	}

	public List<IntegerItem> getReportScopeItems() {
		return reportScopeItems;
	}

	public List<IntegerItem> getAlarmLevelItems() {
		return alarmLevelItems;
	}

	public List<BooleanItem> getBooleanList() {
		return booleanItems;
	}

	public List<ShortItem> getNotifyOptionItems() {
		return notifyOptionItems;
	}

	public List<IntegerItem> getFaultPromptOptionItems() {
		return faultPromptOptionItems;
	}

	public List<IntegerItem> getFixTypeItems() {
		return fixTypeItems;
	}

	public List<IntegerItem> getCashFlagItems() {
		return cashFlagItems;
	}

	public List<IntegerItem> getTermStatusItems() {
		return termStatusItems;
	}

	public List<IntegerItem> getComPortItems() {
		return comPortItems;
	}

	public Collection<UserGroupItem> getUserGroupList() {
		Collection<UserGroupItem> c = new ArrayList<UserGroupItem>();
		UserGroup g = loginUser.getUserGroup();
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
		return c;
	}

	public Collection<CachedTermType> getTermTypeList() {
		if (loginUser.isLogined()) {
			Collection<CachedTermType> c = new ArrayList<CachedTermType>();
			Enumeration<Integer> en = CacheHelper.termTypeMap.keys();
			while (en.hasMoreElements()) {
				CachedTermType termType = CacheHelper.termTypeMap.get(en
						.nextElement());
				if (loginUser.getManuf().getId() != null) {
					if (loginUser.getManuf().getId()
							.equals(termType.getManuf().getId()))
						c.add(termType);
				} else
					c.add(termType);
			}
			return c;
		} else
			return null;
	}

	public Collection<BaseAdCategory> getAdCategoryList()
			throws NamingException {
		if (loginUser.isLogined()) {
			LoopNamingContext context = new LoopNamingContext("db");
			try {
				AdCategoryBeanRemote bean = context.lookup(
						Lookuper.JNDI_TYPE_EJB, "kxd-ejb-adCategoryBean",
						AdCategoryBeanRemote.class);
				return bean.getAdCategoryList(loginUser.getUserId(), null);
			} finally {
				context.close();
			}
		} else
			return null;
	}

	public Collection<BasePrintAdCategory> getPrintAdCategoryList()
			throws NamingException {
		if (loginUser.isLogined()) {
			LoopNamingContext context = new LoopNamingContext("db");
			try {
				PrintAdCategoryBeanRemote bean = context.lookup(
						Lookuper.JNDI_TYPE_EJB, "kxd-ejb-printAdCategoryBean",
						PrintAdCategoryBeanRemote.class);
				return bean.getPrintAdCategoryList(loginUser.getUserId(), null);
			} finally {
				context.close();
			}
		} else
			return null;
	}

	public Collection<BaseInfoCategory> getInfoCategoryList()
			throws NamingException {
		if (loginUser.isLogined()) {
			LoopNamingContext context = new LoopNamingContext("db");
			try {
				InfoCategoryBeanRemote bean = context.lookup(
						Lookuper.JNDI_TYPE_EJB, "kxd-ejb-infoCategoryBean",
						InfoCategoryBeanRemote.class);
				return bean.getInfoCategoryList(loginUser.getUserId(), null);
			} finally {
				context.close();
			}
		} else
			return null;
	}

	public Collection<CachedFileOwner> getFileOwnerList() {
		if (loginUser.isLogined()
				&& (loginUser.getUserGroup().isSystemManager())) {
			Collection<CachedFileOwner> c = new ArrayList<CachedFileOwner>();
			c.addAll(CacheHelper.fileOwnerMap.values());
			return c;
		} else
			return null;
	}

	public Collection<CachedAlarmCategory> getAlarmCategoryList()
			throws NamingException {
		if (loginUser.isLogined()) {
			Collection<CachedAlarmCategory> c = new ArrayList<CachedAlarmCategory>();
			c.addAll(CacheHelper.alarmCategoryMap.values());
			return c;
		} else
			return null;
	}

	public List<BaseBusiness> getBusinessList() throws NamingException {
		if (loginUser.isLogined()) {
			LoopNamingContext context = new LoopNamingContext("db");
			try {
				BusinessBeanRemote bean = context.lookup(
						Lookuper.JNDI_TYPE_EJB, "kxd-ejb-businessBean",
						BusinessBeanRemote.class);
				return bean.getBusinessList(loginUser.getUserId(), null);
			} finally {
				context.close();
			}
		} else
			return null;
	}

	public List<BasePayWay> getPayWayList() throws NamingException {
		if (loginUser.isLogined()) {
			LoopNamingContext context = new LoopNamingContext("db");
			try {
				PayWayBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
						"kxd-ejb-payWayBean", PayWayBeanRemote.class);
				return bean.getPayWayList(loginUser.getUserId(), null);
			} finally {
				context.close();
			}
		} else
			return null;
	}

	public List<BasePayItem> getPayItemList() throws NamingException {
		if (loginUser.isLogined()) {
			LoopNamingContext context = new LoopNamingContext("db");
			try {
				PayItemBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
						"kxd-ejb-payItemBean", PayItemBeanRemote.class);
				return bean.getPayItemList(loginUser.getUserId(), null);
			} finally {
				context.close();
			}
		} else
			return null;
	}

	public Collection<CachedApp> getAppList() throws NamingException {
		if (loginUser.isLogined()) {
			Collection<CachedApp> c = new ArrayList<CachedApp>();
			c.addAll(CacheHelper.appMap.values());
			return c;
		} else
			return null;
	}

	public Collection<CachedDeviceTypeDriver> getDeviceTypeDriverList()
			throws NamingException {
		if (loginUser.isLogined()) {
			Collection<CachedDeviceTypeDriver> c = new ArrayList<CachedDeviceTypeDriver>();
			c.addAll(CacheHelper.deviceTypeDriverMap.values());
			return c;
		} else
			return null;
	}

	public Collection<CachedDeviceType> getDeviceTypeList()
			throws NamingException {
		if (loginUser.isLogined()) {
			Collection<CachedDeviceType> c = new ArrayList<CachedDeviceType>();
			c.addAll(CacheHelper.deviceTypeMap.values());
			return c;
		} else
			return null;
	}

	public Collection<CachedPrintType> getPrintTypeList()
			throws NamingException {
		if (loginUser.isLogined()) {
			Collection<CachedPrintType> c = new ArrayList<CachedPrintType>();
			c.addAll(CacheHelper.printTypeMap.values());
			return c;
		} else
			return null;
	}

	public Collection<CachedDeviceDriver> getDeviceDriverList()
			throws NamingException {
		if (loginUser.isLogined()) {
			Collection<CachedDeviceDriver> c = new ArrayList<CachedDeviceDriver>();
			c.addAll(CacheHelper.deviceDriverMap.values());
			return c;
		} else
			return null;
	}

	public String getCurrentDate() {
		return new DateTime().format("yyyy-MM-dd");
	}

	public String getCurrentTime() {
		return new DateTime().format("HH:mm:ss");
	}

	public String getCurrentDateTime() {
		return new DateTime().format("yyyy-MM-dd HH:mm:ss");
	}

	@Override
	public boolean handleRequest(String url, ControlServlet servlet,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		if (!url.equals("/login.go")) {
			if (!isLogined()) {
				servlet.forward(request, response, "/login.jsp", true);
				return true;
			} else if (url.equals("/logout.go")) {
				try {
					setLoginUser(null);
				} catch (Throwable e) {
				}
				servlet.forward(request, response, "/login.jsp", true);
				return true;
			}
		}
		return false;
	}
}
