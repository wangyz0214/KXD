package kxd.scs.dao.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import javax.naming.NamingException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import kxd.engine.cache.beans.sts.CacheTermMonitorResult;
import kxd.engine.cache.beans.sts.CachedAlarmCode;
import kxd.engine.cache.beans.sts.CachedCommInterface;
import kxd.engine.cache.beans.sts.CachedDeviceStatus;
import kxd.engine.cache.beans.sts.CachedDeviceType;
import kxd.engine.cache.beans.sts.CachedOrg;
import kxd.engine.cache.beans.sts.CachedTermType;
import kxd.engine.cache.beans.sts.CachedTermTypeDevice;
import kxd.engine.cache.beans.sts.MonitoredDeviceStatus;
import kxd.engine.cache.ejb.beans.EjbCachedOrgNode;
import kxd.engine.cache.ejb.interfaces.EjbCacheServiceBeanRemote;
import kxd.engine.dao.ArrayParameter;
import kxd.engine.dao.Dao;
import kxd.engine.helper.CacheHelper;
import kxd.engine.helper.EjbCacheHelper;
import kxd.engine.helper.MessageHelper;
import kxd.engine.scs.monitor.CachedInterfaceMonitorDataItem;
import kxd.engine.scs.monitor.CachedMonitoredTerm;
import kxd.engine.scs.monitor.CachedMonitoredTermMap;
import kxd.engine.scs.monitor.CachedTradeMonitorDataItem;
import kxd.engine.scs.monitor.InterfaceMonitorData;
import kxd.engine.scs.monitor.MonitoredOrgManufTerms;
import kxd.engine.scs.monitor.MonitoredTermResult;
import kxd.engine.scs.monitor.MonitoredTerms;
import kxd.engine.scs.monitor.OrgInterfaceMonitorDataMap;
import kxd.engine.scs.monitor.OrgStatusEventListener;
import kxd.engine.scs.monitor.OrgTermMonitorDataMap;
import kxd.engine.scs.monitor.OrgTradeMonitorDataMap;
import kxd.engine.scs.monitor.RefundEventData;
import kxd.engine.scs.monitor.TermMonitoredOrgNode;
import kxd.engine.scs.monitor.TermStatusEventListener;
import kxd.engine.scs.monitor.TradeMonitorData;
import kxd.engine.scs.trade.drivers.InterfaceEventData;
import kxd.engine.scs.trade.drivers.TradeEventData;
import kxd.net.connection.jndi.Lookuper;
import kxd.net.naming.LoopNamingContext;
import kxd.remote.scs.beans.BaseDeviceStatus;
import kxd.remote.scs.beans.BaseManuf;
import kxd.remote.scs.beans.BaseMonitorData;
import kxd.remote.scs.beans.device.EditedTerm;
import kxd.remote.scs.beans.right.EditedOrg;
import kxd.remote.scs.beans.right.QueryedOrg;
import kxd.remote.scs.util.AppException;
import kxd.remote.scs.util.emun.AlarmLevel;
import kxd.remote.scs.util.emun.AlarmStatus;
import kxd.remote.scs.util.emun.FaultProcFlag;
import kxd.remote.scs.util.emun.SettlementType;
import kxd.remote.scs.util.emun.TermStatus;
import kxd.remote.scs.util.emun.TradeResult;
import kxd.scs.beans.cacheservices.OrgStatusChangeListenerProxy;
import kxd.scs.beans.cacheservices.TermStatusChangeListenerProxy;
import kxd.scs.dao.BaseDao;
import kxd.scs.dao.cache.converters.CachedMonitoredTermConverter;
import kxd.scs.dao.right.OrgDao;
import kxd.scs.dao.term.TermDao;
import kxd.util.DateTime;
import kxd.util.KeyValue;
import kxd.util.KoneUtil;
import kxd.util.TreeNode;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class CacheMonitorDao extends BaseDao {
	final static private Logger logger = Logger
			.getLogger(CacheMonitorDao.class);
	final private static String SQL_QUERY_TERMS = TermDao.FIELDS_QUERY_CACHED_TERM_BASIC
			+ ",b.deviceid,b.status devicestatus,b.statusdesp,c.manufid,b.faultformid,a.settlement_type,a.ip from"// add by jurstone 20120611
			+ " term a,termdevicestatus b,termtype c,manuf d where c.manufid=d.manufid and"
			+ " d.manuftype=0 and a.typeid=c.typeid and b.termid(+)=a.termid order by a.termid";
	/**
	 * 指示初始时，是否载入当天的实时交易监控数据
	 */
	static private boolean loadHistoryTradeData = true;
	static private boolean isEjbCache = true;
	static private boolean isTermMonitor = true;
	static private Class<TermMonitoredOrgNode> orgNodeClazz = TermMonitoredOrgNode.class;
	final private static CachedMonitoredTermMap termsMap = new CachedMonitoredTermMap();
	final public static CachedMonitoredTermConverter monitoredTermConverter = new CachedMonitoredTermConverter();
	final private static ConcurrentHashMap<String, MonitoredOrgManufTerms> orgManufTermsMap = new ConcurrentHashMap<String, MonitoredOrgManufTerms>();
	final private static ConcurrentHashMap<String, MonitoredTerms> filteredMonitoredTermsMap = new ConcurrentHashMap<String, MonitoredTerms>();
	final public static OrgStatusChangeListenerProxy orgStatusEventListenerProxy = new OrgStatusChangeListenerProxy();
	final public static TermStatusChangeListenerProxy termStatusEventListenerProxy = new TermStatusChangeListenerProxy();
	final public static AtomicLong termCount = new AtomicLong(0);
	final public static AtomicLong tradeCount = new AtomicLong(0);
	final public static AtomicLong sucTradeCount = new AtomicLong(0);
	final public static AtomicLong tradeMoney = new AtomicLong(0);
	static {

		try {
			loadConfig(KoneUtil.getConfigPath() + "monitor-service-config.xml");
		} catch (Throwable e) {
			logger.error("load [monitor-service-config.xml] failed: ", e);
		}

		new Thread(new Runnable() {
			@Override
			public void run() {
				DateTime today = new DateTime();
				while (!Thread.interrupted()) {
					if (inited.get()) {
						DateTime d = new DateTime();
						if (!d.isSameDay(today)) { // 不是同一天，则复位有交易终端数
							today = d;
							try {
								tradeCount.set(0);
								tradeMoney.set(0);
								sucTradeCount.set(0);
								for (CachedMonitoredTerm o : termsMap.values()) {
									o.setToDayTraded(false);
								}
								((TermMonitoredOrgNode) EjbCacheHelper.cachedOrgNode)
										.dayChanged();
							} catch (Throwable e) {
							}
						}
						try {
							List<CachedMonitoredTerm> ls = CachedMonitoredTermMap
									.checkOfflineTerms();
							for (CachedMonitoredTerm o : ls)
								o.checkOnline();
						} catch (Throwable e) {
						}
						try {
							Thread.sleep(60000);
						} catch (InterruptedException e) {
							break;
						}
					} else {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							break;
						}
					}
				}
			}
		}).start();
	}

	@SuppressWarnings("unchecked")
	static void loadConfig(String file) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(file);
			NodeList groups = doc.getElementsByTagName("monitorbase");
			if (groups.getLength() > 0) {
				Element group = (Element) groups.item(0);
				NodeList list = group.getElementsByTagName("isejbcache");
				if (list.getLength() > 0) {
					Element node = (Element) list.item(0);
					if (node.hasAttribute("value")) {
						isEjbCache = "true".equals(node.getAttribute("value"));
						logger.debug("isejbcache configed:" + isEjbCache);
					}
				}
				list = group.getElementsByTagName("istermmonitor");
				if (list.getLength() > 0) {
					Element node = (Element) list.item(0);
					if (node.hasAttribute("value")) {
						isTermMonitor = "true".equals(node
								.getAttribute("value"));
						logger.debug("istermmonitor configed:" + isTermMonitor);
					}
				}
				list = group.getElementsByTagName("needloadtradehistorydata");
				if (list.getLength() > 0) {
					Element node = (Element) list.item(0);
					if (node.hasAttribute("value")) {
						loadHistoryTradeData = "true".equals(node
								.getAttribute("value"));
						logger.debug("loadHistoryTradeData configed:"
								+ loadHistoryTradeData);
					}
				}
				list = group.getElementsByTagName("orgnodeclass");
				if (list.getLength() > 0) {
					Element node = (Element) list.item(0);
					if (node.hasAttribute("value")) {
						orgNodeClazz = (Class<TermMonitoredOrgNode>) Class
								.forName(node.getAttribute("value"));
						logger.debug("orgnodeclass config:" + orgNodeClazz);
					}
				}
			}
			NodeList list = doc
					.getElementsByTagName("orgstatuschangelisteners");
			for (int i = 0; i < list.getLength(); i++) {
				Element node = (Element) list.item(i);
				NodeList list1 = node.getElementsByTagName("listener");
				for (int j = 0; j < list1.getLength(); j++) {
					node = (Element) list1.item(j);
					if (node.hasAttribute("class")
							&& !node.getAttribute("class").trim().isEmpty()) {
						String cn = node.getAttribute("class").trim();
						try {
							orgStatusEventListenerProxy.orgStatusEventListener
									.add((OrgStatusEventListener) Class
											.forName(cn).newInstance());
							logger.debug("orgStatusEventListener  added:" + cn);
						} catch (Throwable e) {
							logger.error("orgstatuschangelistener[class=" + cn
									+ "] init error:", e);
						}
					}
				}
			}
			list = doc.getElementsByTagName("termstatuschangelisteners");
			for (int i = 0; i < list.getLength(); i++) {
				Element node = (Element) list.item(i);
				NodeList list1 = node.getElementsByTagName("listener");
				for (int j = 0; j < list1.getLength(); j++) {
					node = (Element) list1.item(j);
					if (node.hasAttribute("class")
							&& !node.getAttribute("class").trim().isEmpty()) {
						String cn = node.getAttribute("class").trim();
						try {
							termStatusEventListenerProxy.termStatusEventListener
									.add((TermStatusEventListener) Class
											.forName(cn).newInstance());
							logger.debug("termStatusEventListener  added:" + cn);
						} catch (Throwable e) {
							logger.error("termstatuschangelistener[class=" + cn
									+ "] init error:", e);
						}
					}
				}
			}
			list = doc.getElementsByTagName("orgstatuschangenotifyhosts");
			for (int i = 0; i < list.getLength(); i++) {
				Element node = (Element) list.item(i);
				NodeList list1 = node.getElementsByTagName("notifyhost");
				for (int j = 0; j < list1.getLength(); j++) {
					node = (Element) list1.item(j);
					orgStatusEventListenerProxy.orgNotifies.add(new String[] {
							node.getAttribute("jndigroupname"),
							node.getAttribute("jndiname") });
					logger.debug("orgnotifyhost  added:"
							+ node.getAttribute("jndigroupname") + ":"
							+ node.getAttribute("jndiname"));
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	static private EjbCachedOrgNode loadMonitoredOrgTree(Dao dao)
			throws InstantiationException, IllegalAccessException {
		EjbCachedOrgNode root = orgNodeClazz.newInstance();
		if (!isEjbCache) { // 假如此服务器不是EJB缓存服务器
			logger.debug("org tree loading from ejb cache...");
			try {
				LoopNamingContext context = new LoopNamingContext("ejbcache");
				try {
					EjbCacheServiceBeanRemote bean = (EjbCacheServiceBeanRemote) context
							.lookup(Lookuper.JNDI_TYPE_EJB,
									"kxd-ejb-ejbCacheServiceBean",
									EjbCacheServiceBeanRemote.class);
					root.addAll(bean.getOrgTree());
				} finally {
					context.close();
				}
			} catch (NamingException e) {
				throw new AppException(e);
			}
		} else {
			logger.debug("org tree loading from database...");
			List<CachedOrg> ls = dao.query(OrgDao.cachedOrgConverter,
					OrgDao.SQL_QUERY_ALL_CACHED_ORG);
			root.addAll(ls);
		}
		logger.debug("org tree loaded.");
		EjbCacheHelper.setCachedOrgNode(root);
		return root;
	}

	static private AtomicBoolean inited = new AtomicBoolean(false);

	static private void loadTermsMap(Dao dao) {
		if (isTermMonitor) {
			logger.debug("term map loading...");
			List<CachedMonitoredTerm> ls = dao.query(monitoredTermConverter,
					SQL_QUERY_TERMS);
			for (CachedMonitoredTerm o : ls) {
				termsMap.putOnly(o.getId(), o);
				TermMonitoredOrgNode node = ((TermMonitoredOrgNode) EjbCacheHelper.cachedOrgNode
						.getGlobal(o.getOrg().getId()));
				if (node == null)
					throw new AppException("org[" + o.getOrg().getId()
							+ "] is null.");
				node.addMonitoredTerm(o);
				if (o.getStatus().equals(TermStatus.NORMAL))
					termCount.addAndGet(1);
			}

			logger.debug("term map loaded");
			// EjbCacheHelper.cachedOrgNode.logTree("", logger);
		}
	}

	static private void loadTradeHistory(Dao dao) {
		if (loadHistoryTradeData) {
			logger.debug("history trade data loading...");
			DateTime now = new DateTime();
			String day = Integer.valueOf(now.format("dd")).toString();
			String sql = "select orgid,tradecodeid,suc_count_" + day
					+ ",err_count_" + day + ",suc_amount_" + day
					+ ",err_amount_" + day + ",refund_count_" + day
					+ ",refund_amount_" + day + ",cancel_refund_count_" + day
					+ ",cancel_refund_amount_" + day + ",duration_" + day
					+ " from report_trade_org_" + now.format("yyyyMM");
			dao.execProcedure("kxd_self_trade.update_real_trade_report()"); // 先更新实时，确保得到最新的统计数据
			List<?> ls = dao.query(sql);
			for (Object o : ls) {
				Object[] a = (Object[]) o;
				int orgId = Integer.valueOf(a[0].toString());
				int tradeCodeId = Integer.valueOf(a[1].toString());
				CachedTradeMonitorDataItem data = new CachedTradeMonitorDataItem();
				data.sucCount = Long.valueOf(a[2].toString());
				data.count = data.sucCount + Long.valueOf(a[3].toString());
				data.sucAmount = Long.valueOf(a[4].toString());
				data.amount = data.sucAmount + Long.valueOf(a[5].toString());
				data.returnCount = Long.valueOf(a[6].toString());
				data.returnAmount = Long.valueOf(a[7].toString());
				data.cancelReturnCount = Long.valueOf(a[8].toString());
				data.cancelReturnAmount = Long.valueOf(a[9].toString());
				data.duration = Long.valueOf(a[10].toString());
				tradeCount.addAndGet(data.count);
				sucTradeCount.addAndGet(data.sucCount);
				tradeMoney.addAndGet(data.amount);
				TermMonitoredOrgNode node = (TermMonitoredOrgNode) EjbCacheHelper.cachedOrgNode
						.getGlobal(orgId);
				if (node == null)
					logger.warn("load trade monitor data: org[" + a[0]
							+ "] is null.");
				else {
					node.addCacheTradeData(tradeCodeId, data, null, null, null);
				}
			}
			sql = "select orgid,interface_id,sum(suc_count),sum(err_count),sum(timeout_count),sum(duration) from "
					+ "interface_log where log_day="
					+ now.format("yyyyMMdd")
					+ " group by orgid,interface_id";
			ls = dao.query(sql);
			for (Object o : ls) {
				Object[] a = (Object[]) o;
				int orgId = Integer.valueOf(a[0].toString());
				short infId = Short.valueOf(a[1].toString());
				CachedCommInterface inf = CacheHelper.commInterfaceMap
						.get(infId);
				if (inf == null)
					continue;
				CachedInterfaceMonitorDataItem data = new CachedInterfaceMonitorDataItem();
				data.sucCount = Long.valueOf(a[2].toString());
				data.errorCount = Long.valueOf(a[3].toString());
				data.timeoutCount = Long.valueOf(a[4].toString());
				data.duration = Long.valueOf(a[5].toString());
				TermMonitoredOrgNode node = (TermMonitoredOrgNode) EjbCacheHelper.cachedOrgNode
						.getGlobal(orgId);
				if (node == null)
					logger.warn("load interface data: org[" + a[0]
							+ "] is null.");
				else
					node.addCacheInterfaceData(infId, data, null, null, null);
			}
			logger.debug("history trade data loaded");
		}
	}

	static synchronized public void init(Dao dao) {
		if (EjbCacheHelper.cachedOrgNode == null) {
			try {
				loadMonitoredOrgTree(dao);
				EjbCacheHelper.cachedOrgNode.sort(true);
				loadTermsMap(dao);
				loadTradeHistory(dao);
				inited.set(true);
			} catch (Throwable e) {
				termsMap.clear();
				EjbCachedOrgNode.clearNodesMap();
				EjbCacheHelper.cachedOrgNode = null;
				logger.error("init failed:", e);
				throw new AppException(e);
			}
		}
	}

	static public EjbCachedOrgNode getOrgTree(Dao dao) {
		init(dao);
		return EjbCacheHelper.cachedOrgNode;
	}

	static public EjbCachedOrgNode getOrg(Dao dao, int orgId) {
		init(dao);
		return EjbCacheHelper.cachedOrgNode.getGlobal(orgId);
	}

	static public CachedMonitoredTermMap getTermsMap(Dao dao) {
		init(dao);
		return termsMap;
	}

	static public void updateTermStatus(Dao dao, int termId, boolean pause,
			int busyTimes, int idleTimes, List<?> statusList) {
		//begin	2012-12-12 	modified by zhangjb
		//EjbCacheHelper.cachedOrgNode必须点击管理平台跟机构相关的功能节点后才会初始化，
		//终端登录后，如果不操作管理平台，此函数一直不起作用。
		//init(dao);
		//end 2012-12-12
		if (EjbCacheHelper.cachedOrgNode == null)
			return;
		CachedMonitoredTerm term = termsMap.get(termId);
		if (term == null)
			throw new AppException("非法终端");
		if (statusList == null) { // 只更新在线状态
			term.setLastOnlineTime(new Date());
			term.setLastUpdateTime(System.currentTimeMillis());
			TermDao.updateTermStatus(dao, term);
			termStatusEventListenerProxy.statusChange(term);
			return;
		}
		Map<Integer, CachedDeviceStatus> termDeviceStatusMap = term
				.getDeviceStatusMap();
		CachedTermType termType = statusList.size() > 0 ? CacheHelper.termTypeMap
				.get(term.getTermTypeId()) : null;
		KeyValue<CachedAlarmCode, AlarmLevel> newLevel = null, oldLevel = null;
		for (Object o : statusList) {
			BaseDeviceStatus baseStatus = (BaseDeviceStatus) o;
			CachedTermTypeDevice device = termType.getDeviceMap().get(
					baseStatus.getId());
			if (device == null)
				continue;
			CachedDeviceType deviceType = device.getDevice().getDeviceType();
			CachedDeviceStatus status = termDeviceStatusMap.get(baseStatus
					.getId());
			boolean needUpdate = false;
			if (status == null) {
				status = new CachedDeviceStatus(baseStatus.getId());
				termDeviceStatusMap.put(status.getId(), status);
				newLevel = deviceType.getAlarmLevel(baseStatus.getStatus());
				needUpdate = true;
			} else {
				if (status.isNotUploadStatus()
						|| status.getStatus() != baseStatus.getStatus()) {
					newLevel = deviceType.getAlarmLevel(baseStatus.getStatus());
					oldLevel = deviceType.getAlarmLevel(status.getStatus());
					needUpdate = true;
				}
			}
			status.setStatus(baseStatus.getStatus());
			status.setMessage(baseStatus.getMessage());
			if (needUpdate) {
				status.setLevel(newLevel.getValue());
				TermDao.updateTermDeviceStatus(dao, term, status, oldLevel,
						newLevel);
				if (newLevel.getValue().getValue() > AlarmLevel.NORMAL
						.getValue()) {
					//begin	2012-12-12 	modified by zhangjb
					// 添加发送告警的代码，在后台的message-service-config.xml中配置名为"kxd.alarm"的发送者
					try {
						MessageHelper.sendMsg("kxd.alarm",status.getMessage(), term, status);
					} catch (Throwable e) {
						if (logger.isDebugEnabled())
							logger.error("[kxd.alarm] sendMsg failed: ", e);
					}
					//end 2012-12-12					
				}
			}
			//Dao dao,int termId,DateTime now,Integer deviceId, int status
			updateTermFaultStatusDetail(dao,termId,new DateTime(),status.getId(),status.getStatus(),status.getMessage());
		}
		AlarmStatus alarmStatus = AlarmStatus.NORMAL;
		for (CachedDeviceStatus o : termDeviceStatusMap.values()) {
			if (alarmStatus.getValue() < o.getLevel().getValue())
				alarmStatus = AlarmStatus.valueOf(o.getLevel().getValue());
		}
		term.setBusyTimes(term.getBusyTimes() + busyTimes);
		term.setIdleTimes(term.getIdleTimes() + idleTimes);
		boolean oldPause = term.getStatus().equals(TermStatus.DISUSE);
		term.setLastOnlineTime(new Date());
		if (!term.getAlarmStatus().equals(alarmStatus)
				|| pause != oldPause
				|| DateTime.secondsBetween(System.currentTimeMillis(),
						term.getLastUpdateTime()) > 300) {
			term.setAlarmStatus(alarmStatus);
			term.setLastUpdateTime(System.currentTimeMillis());
			if (pause) {
				term.setStatus(TermStatus.DISUSE);
			} else {
				term.setStatus(TermStatus.NORMAL);
			}
			if (term.getLastFaultTime() == null) {
				if (alarmStatus.equals(AlarmStatus.FAULT)) {
					term.setLastFaultTime(new Date());
					term.setLastProcessTime(null);
					term.setProcessFlag(FaultProcFlag.UNPROCESS);
				}
			} else {
				if (!alarmStatus.equals(AlarmStatus.FAULT)) {
					term.setProcessFlag(FaultProcFlag.UNPROCESS);
					term.setLastFaultTime(null);
				}
			}
			TermDao.updateTermStatus(dao, term);
			// 更新使用率报表
			TermDao.updateTermOpenRateReport(dao, termId, term.getBusyTimes(),
					term.getIdleTimes());
			term.setBusyTimes(0);
			term.setIdleTimes(0);
			termStatusEventListenerProxy.statusChange(term);
		}
	}
	
	/**
	 * 记录终端设备状态故障时间及恢复时间明细
	 * @param termId 终端ID
	 * @param now 当前时间
	 * @param deviceId 设备ID
	 * @param status 终端当前状态
	 * @author 冯耀瑨
	 */
	private static void updateTermFaultStatusDetail(Dao dao,int termId,DateTime now,Integer deviceId, int status,String message){
		StringBuffer sql = new StringBuffer();
		
	
		//首先判断是否已经有存在的记录，判断条件为 终端id 设备id对应上 同时 他的故障时间不为空，而回复时间为空
		sql.append("select * from termFaultStatusDetail where termId=");
		sql.append(termId);
		sql.append(" and device = ");
		sql.append(deviceId);
		sql.append(" and faultDate is not null and resumeDate is null ");
		
		if(dao.query(sql.toString()).isEmpty()){
			if(status!=0){
				dao.execute("insert into termFaultStatusDetail (termID,device,status,statusdesp,faultDate)"+
						"values(?1,?2,?3,?4,?5)",new Object[]{termId,deviceId,status,message,now.getTime()});
			}
		}else{
			if(status==0){
				dao.execute("update termFaultStatusDetail set resumeDate=?1 where termId=?2 and device=?3 " +
						"and faultDate is not null and resumeDate is null",new Object[]{now.getTime(),termId,deviceId});
			}
		}
	}

	static public void onTradeEvents(Dao dao, List<TradeEventData> tradeEvents) {
		EjbCachedOrgNode root = getOrgTree(dao);
		if (EjbCacheHelper.cachedOrgNode == null)
			return;
		DateTime now = new DateTime();
		for (TradeEventData o : tradeEvents) {
			if (o.getTradeResult().getResult().equals(TradeResult.SUCCESS))
				sucTradeCount.addAndGet(1);
			tradeCount.addAndGet(1);
			tradeMoney.addAndGet(o.getTradeCode().getAmount());
			CachedMonitoredTerm term = termsMap.get(o.getTradeCode()
					.getTermId());
			if (term != null) {
				term.setLastTradeTime(new Date());
				TermMonitoredOrgNode node = (TermMonitoredOrgNode) root
						.getGlobal(term.getOrg().getId());
				node.dataAcquisition(now, o);
			}
		}
	}

	static public void updateRefundStatus(Dao dao, RefundEventData o) {
		if (EjbCacheHelper.cachedOrgNode == null)
			return;
		EjbCachedOrgNode root = getOrgTree(dao);
		TermMonitoredOrgNode node = (TermMonitoredOrgNode) root.getGlobal(o
				.getOrgId());
		if (node != null) {
			node.refundAcquisition(new DateTime(), o.getTradeCodeId(), false,
					o.getAmount(), 1);
		}
	}

	static public void updateCancelRefundStatus(Dao dao, RefundEventData o) {
		if (EjbCacheHelper.cachedOrgNode == null)
			return;
		EjbCachedOrgNode root = getOrgTree(dao);
		TermMonitoredOrgNode node = (TermMonitoredOrgNode) root.getGlobal(o
				.getOrgId());
		if (node != null) {
			node.refundAcquisition(new DateTime(), o.getTradeCodeId(), true,
					o.getAmount(), 1);
		}
	}

	/**
	 * 获取终端监控列表
	 * 
	 * @param orgId
	 *            机构ID
	 * @param manufId
	 *            厂商ID
	 * @param includeChildren
	 *            是否包含子机构
	 * @param termStatus
	 *            终端状态
	 * @param alarmStatus
	 *            告警状态
	 * @param onlineStatus
	 *            在线状态
	 * @param page
	 *            第几页
	 * @return 查询结果
	 */
	static final private int PAGE_RECORDS = 30;
	static private long lastCleanTime = System.currentTimeMillis();
	private static AtomicLong termsLastModifiedTime = new AtomicLong(0);

	static private void cleanTimeoutTimes() {
		long now = System.currentTimeMillis();
		if (termsLastModifiedTime.get() > 0
				&& DateTime.secondsBetween(now, termsLastModifiedTime.get()) > 60) { // 终端状态改变后1分钟内全部清除一次
			lastCleanTime = System.currentTimeMillis();
			termsLastModifiedTime.set(0);
			filteredMonitoredTermsMap.clear();
			orgManufTermsMap.clear();
			logger.debug("Terminal state has changed, clear the cache");
		} else if (DateTime.secondsBetween(now, lastCleanTime) > 600) { // 10分钟
			logger.debug("Clear the cache has timed out");
			lastCleanTime = System.currentTimeMillis();
			Enumeration<String> en = filteredMonitoredTermsMap.keys();
			while (en.hasMoreElements()) {
				String key = en.nextElement();
				MonitoredTerms s = filteredMonitoredTermsMap.get(key);
				if (s != null
						&& DateTime.secondsBetween(s.getCreateTime(), now) > 60)
					filteredMonitoredTermsMap.remove(key);
			}
			en = orgManufTermsMap.keys();
			while (en.hasMoreElements()) {
				String key = en.nextElement();
				MonitoredOrgManufTerms s = orgManufTermsMap.get(key);
				if (s != null
						&& DateTime.secondsBetween(s.getCreateTime(), now) > 600)
					filteredMonitoredTermsMap.remove(key);
			}
		}
	}

	static public MonitoredTerms getMonitorTermFilterTerms(Dao dao,
			Integer orgId, Integer manufId, boolean includeChildren,
			int termStatus, int alarmStatus, int onlineStatus,
			SettlementType settlementType, String filter) {
		init(dao);
		cleanTimeoutTimes();
		if (orgId == null)
			orgId = 0;
		TermMonitoredOrgNode node = (TermMonitoredOrgNode) getOrgTree(dao)
				.getGlobal(orgId);
		if (node == null) {
			throw new AppException("机构[id=" + orgId + "]不存在.");
		}
		
		//edit by snail
		if (filter != null) {
			MonitoredTerms filteredTerms = new MonitoredTerms();
			CachedMonitoredTerm cacheterm = null;
			
			if(filter.indexOf(":")>=0){
				String[] tmp = filter.split(":");
				if(tmp.length==2)
					filter = tmp[1];
				if("orgname".equals(tmp[0])){
					ArrayList<CachedMonitoredTerm> termList = termsMap.getTermsByName(filter);
					int _notActiveCount = 0, _activeCount = 0, _onlineCount = 0, _offlineCount = 0, _normalCount = 0,
					_alarmCount = 0, _tradeCount = 0, _noTradeCount = 0;
					Iterator<CachedMonitoredTerm> it = termList.iterator();
					while(it.hasNext()){
						CachedMonitoredTerm term = (CachedMonitoredTerm)it.next();
						if(term!=null){
							if(!term.getStatus().equals(TermStatus.NORMAL)){
								_notActiveCount++;
							}else{
								_activeCount++;
								if(Boolean.TRUE.equals(term.isOnline())){
									_onlineCount++;
								}else{
									_offlineCount++;
								}
								if(term.getAlarmStatus().equals(AlarmStatus.NORMAL)){
									_normalCount++;
								}else{
									_alarmCount++;
								}
								if(term.getLastTradeTime()!=null&& new DateTime().isSameDay(new DateTime(term
										.getLastTradeTime()))){
									_tradeCount++;
								}else{
									_noTradeCount++;
								}
							}
						}
						filteredTerms.getTerms().add(term);
					}
					
					filteredTerms.setNotActiveCount(_notActiveCount);
					filteredTerms.setActivedCount(_activeCount);
					filteredTerms.setOnlineCount(_onlineCount);
					filteredTerms.setOfflineCount(_offlineCount);
					filteredTerms.setNormalCount(_normalCount);
					filteredTerms.setAlarmCount(_alarmCount);
					filteredTerms.setTradeCount(_tradeCount);
					filteredTerms.setNoTradeCount(_noTradeCount);
					
					return filteredTerms;
				}else if("termip".equals(tmp[0])){
					cacheterm = termsMap.getTermByIP(filter);
				}
			}else{
				cacheterm = termsMap.getTermByCode(filter);
			}
			
			if (cacheterm == null) {
				try {
					cacheterm = termsMap.get(Integer.valueOf(filter.trim()));
				} catch (Throwable e) {
				}
			}
			if (cacheterm != null) {
				if (!cacheterm.getStatus().equals(TermStatus.NORMAL))
					filteredTerms.setNotActiveCount(1);
				else {
					filteredTerms.setActivedCount(1);
					if (Boolean.TRUE.equals(cacheterm.isOnline()))
						filteredTerms.setOnlineCount(1);
					else
						filteredTerms.setOfflineCount(1);
					if (cacheterm.getAlarmStatus().equals(AlarmStatus.NORMAL))
						filteredTerms.setNormalCount(1);
					else
						filteredTerms.setAlarmCount(1);
					if (cacheterm.getLastTradeTime() != null
							&& new DateTime().isSameDay(new DateTime(cacheterm
									.getLastTradeTime()))) {
						filteredTerms.setTradeCount(1);
					} else
						filteredTerms.setNoTradeCount(1);
				}
				filteredTerms.getTerms().add(cacheterm);
			}
			return filteredTerms;
			
		}
		String key = orgId + "." + manufId + "." + includeChildren + "."
				+ termStatus + "." + alarmStatus + "." + onlineStatus + "."
				+ (settlementType == null ? "null" : settlementType.getValue());
		MonitoredTerms filteredTerms = filteredMonitoredTermsMap.get(key);
		if (filteredTerms != null
				&& DateTime.secondsBetween(System.currentTimeMillis(),
						filteredTerms.getCreateTime()) > 60) { // 1分钟更新
			logger.debug("[" + key + "] timeout! removed.");
			filteredMonitoredTermsMap.remove(key);
			filteredTerms = null;
		}
		if (filteredTerms == null) {
			filteredTerms = new MonitoredTerms();
			logger.debug("[" + key + "] not exists! rebuilding...");
			filteredMonitoredTermsMap.put(key, filteredTerms);
			key = orgId + "." + manufId;
			MonitoredOrgManufTerms terms = orgManufTermsMap.get(key);
			if (terms != null
					&& DateTime.secondsBetween(System.currentTimeMillis(),
							terms.getCreateTime()) > 600) { // 10
				// 分钟更新机构和厂商下的终端列表
				logger.debug("[" + key + "] timeout! removed.");
				orgManufTermsMap.remove(key);
				terms = null;
			}
			if (terms == null) {
				logger.debug("[" + key + "] not exists! rebuilding...");
				terms = new MonitoredOrgManufTerms();
				node.getMonitoredTerms(termsMap, terms, manufId, settlementType);
				orgManufTermsMap.put(key, terms);
			}
			filteredTerms.setActivedCount(terms.getNormalTerms().size());
			filteredTerms.setNotActiveCount(terms.getNotActiveTerms().size());
			filteredTerms.setNotInstallCount(terms.getNotInstallTerms().size());
			filteredTerms.setPauseCount(terms.getPauseTerms().size());
			boolean needOnline = termStatus == 3 && (onlineStatus & 1) == 1;
			boolean needOffline = termStatus == 3 && (onlineStatus & 2) == 2;

			boolean needNormal = termStatus == 3 && (alarmStatus & 1) == 1;
			boolean needAlarm = termStatus == 3 && (alarmStatus & 2) == 2;
			boolean needFault = termStatus == 3 && (alarmStatus & 4) == 4;
			int onlineCount = 0, offlineCount = 0, normalCount = 0, alarmCount = 0, falutCount = 0, tradeCount = 0, noTradeCount = 0;
			for (CachedMonitoredTerm o : terms.getNormalTerms()) {
				boolean online = o.checkOnline();
				if (online) {
					onlineCount++;
				} else {
					offlineCount++;
				}
				if (o.isToDayTraded())
					tradeCount++;
				else
					noTradeCount++;
				if ((online && needOnline) || (!online && needOffline)) {
					boolean added = false;
					switch (o.getAlarmStatus()) {
					case ALARM:
						alarmCount++;
						if (needAlarm)
							added = true;
						break;
					case FAULT:
						falutCount++;
						if (needFault)
							added = true;
						break;
					default:
						if (needNormal)
							added = true;
						normalCount++;
						break;
					}
					if (added) {
						filteredTerms.getTerms().add(o);
					}
				}
			}
			filteredTerms.setNormalCount(normalCount);
			filteredTerms.setOfflineCount(offlineCount);
			filteredTerms.setOnlineCount(onlineCount);
			filteredTerms.setAlarmCount(alarmCount);
			filteredTerms.setFalutCount(falutCount);
			filteredTerms.setTradeCount(tradeCount);
			filteredTerms.setNoTradeCount(noTradeCount);
			switch (termStatus) {
			case 0:
				for (CachedMonitoredTerm o : terms.getNotInstallTerms()) {
					filteredTerms.getTerms().add(o);
				}
				for (CachedMonitoredTerm o : terms.getNotActiveTerms()) {
					filteredTerms.getTerms().add(o);
				}
				for (CachedMonitoredTerm o : terms.getPauseTerms()) {
					filteredTerms.getTerms().add(o);
				}
				break;
			case 1:
				for (CachedMonitoredTerm o : terms.getNotInstallTerms()) {
					filteredTerms.getTerms().add(o);
				}
				break;
			case 2:
				for (CachedMonitoredTerm o : terms.getNotActiveTerms()) {
					filteredTerms.getTerms().add(o);
				}
				break;
			case 4:
				for (CachedMonitoredTerm o : terms.getPauseTerms()) {
					filteredTerms.getTerms().add(o);
				}
				break;
			}
		}
		return filteredTerms;
	}

	/**
	 * 获取地图监控的交易列表
	 * 
	 * @param dao
	 * @param orgId
	 * @param includeSelf
	 * @return
	 */
	static private Date lastMonitorTradeUpdateTime = new Date(0);

	static private synchronized TermMonitoredOrgNode checkMonitorTradeUpdateTime(
			Dao dao) {
		TermMonitoredOrgNode node = (TermMonitoredOrgNode) getOrgTree(dao);
		if (DateTime.secondsBetween(new Date(), lastMonitorTradeUpdateTime) > 60) {
			DateTime t = new DateTime();
			node.resetTradeMonitorData();
			node.updateTradeMonitorData(t.addHours(-1), t.addMinutes(-30),
					t.addMinutes(-10));
			lastMonitorTradeUpdateTime = new Date();
		}
		return node;
	}

	static public KeyValue<BaseMonitorData, Collection<OrgTradeMonitorDataMap>> getMapMonitorTradeList(
			Dao dao, Integer orgId, boolean includeSelf) {
		TermMonitoredOrgNode node = checkMonitorTradeUpdateTime(dao);
		if (orgId != null)
			node = node.getGlobal(orgId);
		else
			node = node.getGlobal(0);
		List<OrgTradeMonitorDataMap> ls = new ArrayList<OrgTradeMonitorDataMap>();
		OrgTradeMonitorDataMap map = new OrgTradeMonitorDataMap();
		if (includeSelf) {
			map.setOrg(node.toQueryedOrg());
			Enumeration<Integer> en = node.getTradeStatusMap().keys();
			while (en.hasMoreElements()) {
				int tradeCodeId = en.nextElement();
				map.put(tradeCodeId, new TradeMonitorData(node
						.getTradeStatusMap().get(tradeCodeId)));
			}
			ls.add(map);
		}
		Iterator<?> it = node.getChildren().iterator();
		while (it.hasNext()) {
			TermMonitoredOrgNode d = (TermMonitoredOrgNode) it.next();
			map = new OrgTradeMonitorDataMap();
			map.setOrg(d.toQueryedOrg());
			Enumeration<Integer> en = d.getTradeStatusMap().keys();
			while (en.hasMoreElements()) {
				int tradeCodeId = en.nextElement();
				map.put(tradeCodeId, new TradeMonitorData(d.getTradeStatusMap()
						.get(tradeCodeId)));
			}
			ls.add(map);
		}
		return new KeyValue<BaseMonitorData, Collection<OrgTradeMonitorDataMap>>(
				new BaseMonitorData(termCount.get(), tradeCount.get(),
						sucTradeCount.get(), tradeMoney.get()), ls);
	}

	/**
	 * 获取地图监控的交易列表
	 * 
	 * @param dao
	 * @param orgId
	 * @param includeSelf
	 * @return
	 */
	static public KeyValue<BaseMonitorData, Collection<OrgTermMonitorDataMap>> getMapMonitorTermList(
			Dao dao, Integer orgId, boolean includeSelf) {
		TermMonitoredOrgNode node = (TermMonitoredOrgNode) getOrgTree(dao);
		if (orgId != null)
			node = node.getGlobal(orgId);
		else
			node = node.getGlobal(0);
		List<OrgTermMonitorDataMap> ls = new ArrayList<OrgTermMonitorDataMap>();
		OrgTermMonitorDataMap map = new OrgTermMonitorDataMap();
		if (includeSelf) {
			map.setOrg(node.toQueryedOrg());
			map.putAll(node.getTermStatusMap());
			ls.add(map);
		}
		Iterator<?> it = node.getChildren().iterator();
		while (it.hasNext()) {
			TermMonitoredOrgNode d = (TermMonitoredOrgNode) it.next();
			map = new OrgTermMonitorDataMap();
			map.setOrg(d.toQueryedOrg());
			map.putAll(d.getTermStatusMap());
			ls.add(map);
		}
		return new KeyValue<BaseMonitorData, Collection<OrgTermMonitorDataMap>>(
				new BaseMonitorData(termCount.get(), tradeCount.get(),
						sucTradeCount.get(), tradeMoney.get()), ls);
	}

	static public KeyValue<BaseMonitorData, CacheTermMonitorResult> getMonitorTermList(
			Dao dao, Integer orgId, Integer manufId, boolean includeChildren,
			int termStatus, int alarmStatus, int onlineStatus,
			SettlementType settlementType, int page,
			boolean includeTermDeviceStatusList, String filter) {
		MonitoredTerms filteredTerms = getMonitorTermFilterTerms(dao, orgId,
				manufId, includeChildren, termStatus, alarmStatus,
				onlineStatus, settlementType, filter);
		page--;
		int start = page * PAGE_RECORDS;
		int end = start + PAGE_RECORDS;
		int count = filteredTerms.getTerms().size();
		if (end > count)
			end = count;
		CacheTermMonitorResult result = new CacheTermMonitorResult();
		result.setActivedCount(filteredTerms.getActivedCount());
		result.setNotActiveCount(filteredTerms.getNotActiveCount());
		result.setNotInstallCount(filteredTerms.getNotInstallCount());
		result.setPauseCount(filteredTerms.getPauseCount());
		result.setOfflineCount(filteredTerms.getOfflineCount());
		result.setOnlineCount(filteredTerms.getOnlineCount());
		result.setNormalCount(filteredTerms.getNormalCount());
		result.setFalutCount(filteredTerms.getFalutCount());
		result.setAlarmCount(filteredTerms.getAlarmCount());
		result.setAllCount(filteredTerms.getActivedCount()
				+ result.getNotActiveCount() + result.getPauseCount()
				+ result.getNotInstallCount());
		result.setRecordCount(count);
		result.setPages(count / PAGE_RECORDS);
		if ((count % PAGE_RECORDS) != 0)
			result.setPages(result.getPages() + 1);
		for (int i = start; i < end; i++) {
			CachedMonitoredTerm o = filteredTerms.getTerms().get(i);
			MonitoredTermResult term = new MonitoredTermResult();
			term.copyData(o);
			if (includeTermDeviceStatusList) {
				ArrayList<MonitoredDeviceStatus> c = new ArrayList<MonitoredDeviceStatus>();
				o.getMonitoredDeviceStatus(c);
				term.setDeviceStatusList(c);
			}
			TermMonitoredOrgNode on = (TermMonitoredOrgNode) EjbCacheHelper.cachedOrgNode
					.getGlobal(o.getOrg().getId());
			term.getOrg().setOrgName(on.getOrgName());
			term.getOrg().setOrgFullName(on.getOrgFullName());// modify by jurstone 20120611
			result.getTerms().add(term);
		}
		return new KeyValue<BaseMonitorData, CacheTermMonitorResult>(
				new BaseMonitorData(termCount.get(), tradeCount.get(),
						sucTradeCount.get(), tradeMoney.get()), result);
	}

	static public KeyValue<BaseMonitorData, Collection<CachedDeviceStatus>> getMonitorTermDeviceStatusList(
			Dao dao, int termId) {
		init(dao);
		CachedMonitoredTerm o = termsMap.get(termId);
		if (o != null) {
			Collection<CachedDeviceStatus> r = new ArrayList<CachedDeviceStatus>();
			r.addAll(o.getDeviceStatusMap().values());
			return new KeyValue<BaseMonitorData, Collection<CachedDeviceStatus>>(
					new BaseMonitorData(termCount.get(), tradeCount.get(),
							sucTradeCount.get(), tradeMoney.get()), r);
		} else
			return new KeyValue<BaseMonitorData, Collection<CachedDeviceStatus>>(
					new BaseMonitorData(termCount.get(), tradeCount.get(),
							sucTradeCount.get(), tradeMoney.get()), null);
	}

	static public List<QueryedOrg> getOrgTreeItems(Dao dao,
			Integer parentOrgId, int depths, Integer selectedOrgId,
			boolean includeSelf, String keyword) {
		init(dao);
		EjbCachedOrgNode root = getOrgTree(dao);
		EjbCachedOrgNode org;
		if (parentOrgId != null) {
			org = (EjbCachedOrgNode) EjbCacheHelper.cachedOrgNode
					.getGlobal(parentOrgId);
			if (org == null)
				return new ArrayList<QueryedOrg>();
		} else
			org = root;
		return org.getOrgChildren(selectedOrgId, depths, includeSelf, keyword);
	}

	static public String getOrgTreePath(Dao dao, int orgId, String separator) {
		init(dao);
		EjbCachedOrgNode org = getOrg(dao, orgId);
		if (org != null)
			return org.getPath(separator, 0);
		else
			return null;
	}

	static public void orgAdded(Dao dao, EditedOrg org) {
		init(dao);
		EjbCachedOrgNode o = getOrg(dao, org.getParentOrg().getId());
		EjbCachedOrgNode node = (EjbCachedOrgNode) o.add(org.getId());
		node.copyDataFromEditedOrg(org);
		o.sort(false);
		orgStatusEventListenerProxy.orgAdded(node);
	}

	static public void orgModified(Dao dao, EditedOrg org) {
		init(dao);
		EjbCachedOrgNode o = getOrg(dao, org.getId());
		if (o != null) {
			int s = o.getSerialNumber();
			o.copyDataFromEditedOrg(org);
			if (s != org.getSerialNumber() && o.getParent() != null)
				o.getParent().sort(false);
			orgStatusEventListenerProxy.orgModified(o);
		}
	}

	static public void orgRemoved(Dao dao, int orgId) {
		init(dao);
		EjbCachedOrgNode o = getOrg(dao, orgId);
		if (o != null) {
			orgStatusEventListenerProxy.orgRemoved(o);
			o.getParent().remove(o);
		}
	}

	static public void termAdded(Dao dao, EditedTerm term) {
		init(dao);
		if (term.getTermType().getManuf() == null
				|| term.getTermType().getManuf().getId() == null) {
			CachedTermType t = CacheHelper.termTypeMap.get(term.getTermType()
					.getId());
			if (t == null)
				throw new AppException("找不到终端型号[id="
						+ term.getTermType().getId());
			term.getTermType().setManuf(new BaseManuf(t.getManufId()));
		}
		CachedMonitoredTerm o = new CachedMonitoredTerm();
		o.copyFromEditedTerm(term);
		getTermsMap(dao).put(term.getId(), o);
		TermMonitoredOrgNode node = (TermMonitoredOrgNode) EjbCacheHelper.cachedOrgNode
				.getGlobal(o.getOrg().getId());
		if (node == null)
			throw new AppException("缓存中找不到机构ID[" + o.getOrg().getIdString()
					+ "]");
		node.addMonitoredTerm(o);
		if (termsLastModifiedTime.get() == 0)
			termsLastModifiedTime.set(System.currentTimeMillis());
		termStatusEventListenerProxy.termAdded(o);
	}

	static public void termModified(Dao dao, EditedTerm term) {
		init(dao);
		if (term.getTermType().getManuf() == null
				|| term.getTermType().getManuf().getId() == null) {
			CachedTermType t = CacheHelper.termTypeMap.get(term.getTermType()
					.getId());
			if (t == null)
				throw new AppException("找不到终端型号[id="
						+ term.getTermType().getId());
			term.getTermType().setManuf(new BaseManuf(t.getManufId()));
		}
		CachedMonitoredTerm o = termsMap.get(term.getId());
		if (o != null) {
			int oldOrgId = o.getOrg().getId();
			if (!o.getOrg().equals(term.getOrg())) { // 父机构变化
				TermMonitoredOrgNode node = (TermMonitoredOrgNode) EjbCacheHelper.cachedOrgNode
						.getGlobal(oldOrgId);
				if (node != null)
					node.removeMonitoredTerm(o);
				node = (TermMonitoredOrgNode) EjbCacheHelper.cachedOrgNode
						.getGlobal(term.getOrg().getId());
				if (node != null)
					node.addMonitoredTerm(o);
				if (termsLastModifiedTime.get() == 0)
					termsLastModifiedTime.set(System.currentTimeMillis());
			}
			boolean oldActived = o.getStatus().equals(TermStatus.NORMAL);
			boolean newActived = term.getStatus().equals(TermStatus.NORMAL);
			if (oldActived != newActived) {
				if (oldActived)
					termCount.addAndGet(-1);
				else
					termCount.addAndGet(1);
			}
			if (!o.getStatus().equals(term.getStatus())) { // 终端状态改变
				if (termsLastModifiedTime.get() == 0)
					termsLastModifiedTime.set(System.currentTimeMillis());
			}
			o.copyFromEditedTerm(term);
			termsMap.put(term.getId(), o);
			termStatusEventListenerProxy.termModified(o, oldOrgId);
		}
	}

	static public void termRemoved(Dao dao, int termId) {
		init(dao);
		CachedMonitoredTerm o = termsMap.remove(termId);
		if (o != null) {
			if (o.getStatus().equals(TermStatus.NORMAL))
				termCount.addAndGet(-1);
			TermMonitoredOrgNode node = (TermMonitoredOrgNode) getOrgTree(dao)
					.getGlobal(o.getOrg().getId());
			if (node != null) {
				node.removeMonitoredTerm(o);
				if (termsLastModifiedTime.get() == 0)
					termsLastModifiedTime.set(System.currentTimeMillis());
				termStatusEventListenerProxy.termRemoved(o);
			}
		}
	}

	static public List<QueryedOrg> getParentOrgs(Dao dao, Integer orgId,
			boolean includeSelf) throws NamingException {
		init(dao);
		EjbCachedOrgNode node = getOrgTree(dao).getGlobal(orgId);
		List<QueryedOrg> ls = new ArrayList<QueryedOrg>();
		if (node != null) {
			if (includeSelf)
				ls.add(node.toQueryedOrg());
			while (node.getParent() != null && node.getParent().getId() != null) {
				node = (EjbCachedOrgNode) node.getParent();
				ls.add(node.toQueryedOrg());
			}
		}
		return ls;
	}

	static public void termTypeDeviceAdded(Dao dao, int termType, int deviceId) {
		init(dao);
		termsMap.termTypeAddDevice(termType, deviceId);
		CopyOnWriteArrayList<CachedMonitoredTerm> s = termsMap
				.getTermTypeTerms(termType);
		if (s != null) {
			for (CachedMonitoredTerm o : s) {
				termStatusEventListenerProxy.termDeviceAdded(o, deviceId);
			}
		}
	}

	static public void termTypeDeviceRemoved(Dao dao, int termType, int deviceId) {
		init(dao);
		termsMap.termTypeDeleteDevice(termType, deviceId);
		CopyOnWriteArrayList<CachedMonitoredTerm> s = termsMap
				.getTermTypeTerms(termType);
		if (s != null) {
			for (CachedMonitoredTerm o : s) {
				termStatusEventListenerProxy.termDeviceRemoved(o, deviceId);
			}
		}
	}

	static public void termLogined(Dao dao, int termId) {
		logger.debug("term[id=" + termId + "] logined.");
		if (EjbCacheHelper.cachedOrgNode == null)
			return;
		// init(dao);
		CachedMonitoredTerm o = termsMap.get(termId);
		if (o != null) {
			o.setLastOnlineTime(new Date());
		}
		termStatusEventListenerProxy.termLogined(o);
	}

	static public Map<?, ?> getOrgProvinceCity(Dao dao, List<?> orgIdList) {
		init(dao);
		HashMap<Integer, Object[]> map = new HashMap<Integer, Object[]>();
		for (Object o : orgIdList) {
			int orgId = (Integer) o;
			String province, city, name;
			EjbCachedOrgNode org = (EjbCachedOrgNode) EjbCacheHelper.cachedOrgNode
					.getGlobal(orgId);
			if (org.getDepth() <= 2) {
				province = org.getOrgName();
				city = "";
				name = "";
			} else {
				name = org.getOrgName();
				while (org.getDepth() > 3) {
					org = (EjbCachedOrgNode) org.getParent();
				}
				city = org.getOrgName();
				province = ((EjbCachedOrgNode) org.getParent()).getOrgName();
			}
			map.put(orgId, new Object[] { province, city, name });
		}
		return map;
	}

	static public void termActived(Dao dao, int termId) {
		if (EjbCacheHelper.cachedOrgNode == null)
			return;
		// init(dao);
		CachedMonitoredTerm o = termsMap.get(termId);
		if (o != null && !o.getStatus().equals(TermStatus.NORMAL)) {
			termCount.addAndGet(1);
			if (termsLastModifiedTime.get() == 0)
				termsLastModifiedTime.set(System.currentTimeMillis());
			o.setStatus(TermStatus.NORMAL);
			termStatusEventListenerProxy.termActived(o);
		}
	}

	static public void termPauseResume(Dao dao, Collection<Integer> terms,
			boolean pause) {
		if (EjbCacheHelper.cachedOrgNode == null)
			return;
		// init(dao);
		for (Integer id : terms) {
			CachedMonitoredTerm o = termsMap.get(id);
			if (o != null
					&& o.getStatus().getValue() >= TermStatus.NORMAL.getValue()) {
				if (pause) {
					if (!o.getStatus().equals(TermStatus.DISUSE)) {
						termCount.addAndGet(-1);
						o.setStatus(TermStatus.DISUSE);
						if (termsLastModifiedTime.get() == 0)
							termsLastModifiedTime.set(System
									.currentTimeMillis());
					}
				} else {
					if (!o.getStatus().equals(TermStatus.NORMAL)) {
						termCount.addAndGet(1);
						o.setStatus(TermStatus.NORMAL);
						if (termsLastModifiedTime.get() == 0)
							termsLastModifiedTime.set(System
									.currentTimeMillis());
					}
				}
				termStatusEventListenerProxy.termPauseResume(o, pause);
			}
		}
	}

	static public void orgMoved(Dao dao, int orgId, int newParentOrgId) {
		init(dao);
		TreeNode<Integer> node = EjbCacheHelper.cachedOrgNode.find(orgId, true);
		TreeNode<Integer> pnode = EjbCacheHelper.cachedOrgNode.find(
				newParentOrgId, true);
		node.getParent().remove(node);
		pnode.add(node);
		pnode.sort(false);
	}

	public static BaseMonitorData getBasicMonitorData() {
		return new BaseMonitorData(termCount.get(), tradeCount.get(),
				sucTradeCount.get(), tradeMoney.get());
	}

	static private Date lastMonitorInterfaceUpdateTime = new Date(0);

	static private synchronized TermMonitoredOrgNode checkMonitorInterfaceUpdateTime(
			Dao dao) {
		TermMonitoredOrgNode node = (TermMonitoredOrgNode) getOrgTree(dao);
		if (DateTime.secondsBetween(new Date(), lastMonitorInterfaceUpdateTime) > 30) {
			DateTime t = new DateTime();
			node.resetInterfaceMonitorData();
			node.updateIntefaceMonitorData(t.addHours(-1), t.addMinutes(-30),
					t.addMinutes(-10));
			lastMonitorInterfaceUpdateTime = new Date();
		}
		return node;
	}

	public static KeyValue<BaseMonitorData, Collection<OrgInterfaceMonitorDataMap>> getMonitorInterfaceList(
			Dao dao) {
		TermMonitoredOrgNode node = checkMonitorInterfaceUpdateTime(dao)
				.getGlobal(0);
		List<OrgInterfaceMonitorDataMap> ls = new ArrayList<OrgInterfaceMonitorDataMap>();
		OrgInterfaceMonitorDataMap map = new OrgInterfaceMonitorDataMap();
		;
		map.setOrg(node.toQueryedOrg());
		Enumeration<Short> en = node.getInterfaceMonitorMap().keys();
		while (en.hasMoreElements()) {
			short id = en.nextElement();
			map.put(id, new InterfaceMonitorData(node.getInterfaceMonitorMap()
					.get(id)));
		}
		ls.add(map);
		Iterator<?> it = node.getChildren().iterator();
		while (it.hasNext()) {
			TermMonitoredOrgNode d = (TermMonitoredOrgNode) it.next();
			map = new OrgInterfaceMonitorDataMap();
			map.setOrg(d.toQueryedOrg());
			en = d.getInterfaceMonitorMap().keys();
			while (en.hasMoreElements()) {
				short id = en.nextElement();
				map.put(id, new InterfaceMonitorData(d.getInterfaceMonitorMap()
						.get(id)));
			}
			ls.add(map);
		}
		return new KeyValue<BaseMonitorData, Collection<OrgInterfaceMonitorDataMap>>(
				new BaseMonitorData(termCount.get(), tradeCount.get(),
						sucTradeCount.get(), tradeMoney.get()), ls);
	}

	public static void onInterfaceEvents(Dao dao,
			List<InterfaceEventData> events) {
		TermMonitoredOrgNode root = (TermMonitoredOrgNode) getOrgTree(dao);
		if (EjbCacheHelper.cachedOrgNode == null)
			return;
		DateTime now = new DateTime();
		int minute = Integer.valueOf(now.format("HHmm"));
		int day = Integer.valueOf(now.format("yyyyMMdd"));
		Object values[] = new Object[events.size()];
		int i = 0;
		for (InterfaceEventData o : events) {
			TermMonitoredOrgNode node = (TermMonitoredOrgNode) root.getGlobal(o
					.getOrgId());
			if (node != null)
				node.dataAcquisition(now, o);
			values[i++] = new Object[] { o.getOrgId(),
					(int) o.getInterfaceId(), minute, o.getResult().getValue(),
					(int) o.getDuration() };
		}
		ArrayParameter p = new ArrayParameter("INTERFACE_LOG_DATA",
				"INTERFACE_LOG_ARRAY", values);
		dao.execProcedure("kxd_self_trade.update_interface_log(?1,?2)", day, p);
	}
}
