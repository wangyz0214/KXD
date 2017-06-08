package kxd.engine.helper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.naming.NamingException;

import kxd.engine.cache.beans.sts.CacheTermMonitorResult;
import kxd.engine.cache.beans.sts.CachedDeviceStatus;
import kxd.engine.cache.beans.sts.CachedTerm;
import kxd.engine.cache.beans.sts.CachedTermTypeDevice;
import kxd.engine.cache.beans.sts.MonitoredDeviceStatus;
import kxd.engine.scs.monitor.MonitorServiceBeanRemote;
import kxd.engine.scs.monitor.OrgInterfaceMonitorDataMap;
import kxd.engine.scs.monitor.OrgTermMonitorDataMap;
import kxd.engine.scs.monitor.OrgTradeMonitorDataMap;
import kxd.engine.scs.trade.drivers.InterfaceEventData;
import kxd.engine.scs.trade.drivers.TradeEventData;
import kxd.net.connection.jndi.Lookuper;
import kxd.net.naming.LoopNamingContext;
import kxd.remote.scs.beans.BaseMonitorData;
import kxd.remote.scs.transaction.Result;
import kxd.remote.scs.transaction.TradeCode;
import kxd.remote.scs.util.emun.SettlementType;
import kxd.util.KeyValue;

import org.apache.log4j.Logger;

public class MonitorHelper {
	static final String DB_JNDI = "db";
	static final String MONITOR_JNDI = "monitor";
	static Logger logger = Logger.getLogger(MonitorHelper.class);

	/**
	 * 获取地图交易监控信息
	 * 
	 * @param orgId
	 * @param payItem
	 * @param payWay
	 * @param businessCategory
	 * @return
	 * @throws NamingException
	 * @throws InterruptedException
	 * @throws IOException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public static KeyValue<BaseMonitorData, Collection<OrgTradeMonitorDataMap>> getMapMonitorTradeList(
			Integer orgId, boolean includeSelf) throws NamingException,
			InterruptedException, IOException, InstantiationException,
			IllegalAccessException {
		LoopNamingContext context = new LoopNamingContext(MONITOR_JNDI);
		try {
			MonitorServiceBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-monitorServiceBean",
					MonitorServiceBeanRemote.class);
			return bean.getMapMonitorTradeList(orgId, includeSelf);
		} finally {
			context.close();
		}
	}

	public static KeyValue<BaseMonitorData, Collection<OrgTermMonitorDataMap>> getMapMonitorTermList(
			Integer orgId, boolean includeSelf) throws NamingException,
			InterruptedException, IOException, InstantiationException,
			IllegalAccessException {
		LoopNamingContext context = new LoopNamingContext(MONITOR_JNDI);
		try {
			MonitorServiceBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-monitorServiceBean",
					MonitorServiceBeanRemote.class);
			return bean.getMapMonitorTermList(orgId, includeSelf);
		} finally {
			context.close();
		}
	}

	public static KeyValue<BaseMonitorData, CacheTermMonitorResult> getMonitorTermList(
			Integer orgId, Integer manufId, boolean includeChildren,
			int termStatus, int alarmStatus, int onlineStatus,
			SettlementType settlementType, int page,
			boolean includeDeviceStatusList, String filter)
			throws NamingException, InterruptedException, IOException,
			InstantiationException, IllegalAccessException {
		LoopNamingContext context = new LoopNamingContext(MONITOR_JNDI);
		try {
			MonitorServiceBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-monitorServiceBean",
					MonitorServiceBeanRemote.class);
			return bean.getMonitorTermList(orgId, manufId, includeChildren,
					termStatus, alarmStatus, onlineStatus, settlementType,
					page, includeDeviceStatusList, filter);
		} finally {
			context.close();
		}
	}

	static public BaseMonitorData getBasicMonitorData() throws NamingException {
		LoopNamingContext context = new LoopNamingContext(MONITOR_JNDI);
		try {
			MonitorServiceBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-monitorServiceBean",
					MonitorServiceBeanRemote.class);
			return bean.getBasicMonitorData();
		} finally {
			context.close();
		}
	}

	static public BaseMonitorData getMonitoredDeviceStatus(int termId,
			List<MonitoredDeviceStatus> c) throws NamingException {
		CachedTerm term = CacheHelper.termMap.getTerm(termId);
		if (term == null)
			throw new NamingException("找不到终端[id" + termId + "]");
		LoopNamingContext context = new LoopNamingContext(MONITOR_JNDI);
		try {
			MonitorServiceBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-monitorServiceBean",
					MonitorServiceBeanRemote.class);
			KeyValue<BaseMonitorData, Collection<CachedDeviceStatus>> ls = bean
					.getMonitoredDeviceStatus(termId);
			c.clear();
			Hashtable<Integer, CachedTermTypeDevice> map = term.getTermType()
					.getDeviceMap();
			for (CachedDeviceStatus st : ls.getValue()) {
				CachedTermTypeDevice device = map.get(st.getId());
				if (device != null) {
					MonitoredDeviceStatus o = new MonitoredDeviceStatus();
					o.setId(device.getDevice().getId());
					o.setStatus(st.getStatus());
					o.setPort(device.getPort());
					o.setDeviceDesp(device.getDevice().getDeviceName());
					o.setDeviceTypeDesp(device.getDevice().getDeviceType()
							.getDeviceTypeDesp());
					o.setExtConfig(device.getExtConfig());
					o.setMessage(st.getMessage());
					c.add(o);
				}
			}
			if (c.size() == map.size())
				return ls.getKey();
			Enumeration<Integer> en1 = map.keys();
			while (en1.hasMoreElements()) {
				int k = en1.nextElement();
				MonitoredDeviceStatus o = new MonitoredDeviceStatus(k);
				CachedTermTypeDevice device = map.get(k);
				if (!c.contains(o)) {
					o.setPort(device.getPort());
					o.setDeviceDesp(device.getDevice().getDeviceName());
					o.setDeviceTypeDesp(device.getDevice().getDeviceType()
							.getDeviceTypeDesp());
					o.setExtConfig(device.getExtConfig());
					c.add(o);
				}
			}
			return ls.getKey();
		} finally {
			context.close();
		}
	}

	public static KeyValue<BaseMonitorData, Collection<OrgInterfaceMonitorDataMap>> getMonitorInterfaceList()
			throws NamingException {
		LoopNamingContext context = new LoopNamingContext(MONITOR_JNDI);
		try {
			MonitorServiceBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-monitorServiceBean",
					MonitorServiceBeanRemote.class);
			return bean.getMonitorInterfaceList();
		} finally {
			context.close();
		}
	}

	/**
	 * 交易事件侦听器，用于发送特别的监控事件
	 */
	// final public static CopyOnWriteArrayList<TradeEventListener>
	// tradeEventListeners = new CopyOnWriteArrayList<TradeEventListener>();
	static ArrayBlockingQueue<TradeEventData> tradeEventQueue = new ArrayBlockingQueue<TradeEventData>(
			10000);
	static {
		new Thread(new Runnable() {
			@Override
			public void run() {
				List<TradeEventData> ls = new ArrayList<TradeEventData>();
				while (!Thread.currentThread().isInterrupted()) {
					try {
						ls.clear();
						TradeEventData data = tradeEventQueue.take();
						ls.add(data);
						int c = 0;
						for (int i = 0; i < 300; i++) {
							c = tradeEventQueue.size();
							if (c < 1000)
								Thread.sleep(100);
						}
						if (c > 1000)
							c = 1000;
						for (int i = 0; i < c; i++) {
							data = tradeEventQueue.poll();
							if (data != null)
								ls.add(data);
							else
								break;
						}
						try {
							LoopNamingContext context = new LoopNamingContext(
									MONITOR_JNDI);
							try {
								MonitorServiceBeanRemote bean = context.lookup(
										Lookuper.JNDI_TYPE_EJB,
										"kxd-ejb-monitorServiceBean",
										MonitorServiceBeanRemote.class);
								bean.onTradeEvents(ls);
							} finally {
								context.close();
							}
						} catch (Throwable e) {
							logger.error("update trade status error: ", e);
						}
						/*
						 * Iterator<TradeEventListener> it = tradeEventListeners
						 * .iterator(); while (it.hasNext()) { try {
						 * it.next().traded(ls); } catch (Throwable e) { } }
						 */
					} catch (Throwable e) {
					}
				}
			}
		}).start();
	}

	public static void onTradeEvent(TradeCode tradeCode, Result tradeResult) {
		try {
			if (tradeCode.getTradeCodeId() != null) {
				TradeEventData data = new TradeEventData();
				data.setTradeCode(tradeCode);
				data.setTradeResult(tradeResult);
				tradeEventQueue.offer(data, 10, TimeUnit.SECONDS);
			}
		} catch (Throwable e) {
		}
	}

	/**
	 * 交易事件侦听器，用于发送特别的监控事件
	 */
	static ArrayBlockingQueue<InterfaceEventData> interfaceEventQueue = new ArrayBlockingQueue<InterfaceEventData>(
			10000);
	static {
		new Thread(new Runnable() {
			@Override
			public void run() {
				List<InterfaceEventData> ls = new ArrayList<InterfaceEventData>();
				while (!Thread.currentThread().isInterrupted()) {
					try {
						ls.clear();
						InterfaceEventData data = interfaceEventQueue.take();
						ls.add(data);
						int c = 0;
						for (int i = 0; i < 300; i++) {
							c = interfaceEventQueue.size();
							if (c < 1000)
								Thread.sleep(100);
						}
						if (c > 1000)
							c = 1000;
						for (int i = 0; i < c; i++) {
							data = interfaceEventQueue.poll();
							if (data != null)
								ls.add(data);
							else
								break;
						}
						try {
							LoopNamingContext context = new LoopNamingContext(
									MONITOR_JNDI);
							try {
								MonitorServiceBeanRemote bean = context.lookup(
										Lookuper.JNDI_TYPE_EJB,
										"kxd-ejb-monitorServiceBean",
										MonitorServiceBeanRemote.class);
								bean.onInterfaceEvents(ls);
							} finally {
								context.close();
							}
						} catch (Throwable e) {
							logger.error("update interface status error: ", e);
						}
					} catch (Throwable e) {
					}
				}
			}
		}).start();
	}

	public static void onInterfaceEvent(InterfaceEventData data) {
		try {
			interfaceEventQueue.offer(data, 10, TimeUnit.SECONDS);
		} catch (Throwable e) {
		}
	}
}
