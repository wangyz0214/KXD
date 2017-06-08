package kxd.engine.helper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.naming.NamingException;

import kxd.engine.cache.beans.sts.CachedBusinessOpenClose;
import kxd.engine.cache.beans.sts.CachedBusinessOpenClose.Item;
import kxd.engine.cache.beans.sts.CachedBusinessOpenCloseList;
import kxd.engine.cache.beans.sts.CachedOrg;
import kxd.engine.cache.beans.sts.CachedOrgCode;
import kxd.engine.cache.beans.sts.CachedTerm;
import kxd.engine.cache.beans.sts.CachedTermConfig;
import kxd.engine.cache.beans.sts.CachedTradeCode;
import kxd.engine.cache.interfaces.CacheServiceBeanRemote;
import kxd.engine.scs.monitor.MonitorServiceBeanRemote;
import kxd.engine.scs.monitor.TermStatusCommand;
import kxd.engine.scs.monitor.TermSubmittedStatus;
import kxd.net.connection.jndi.Lookuper;
import kxd.net.naming.LoopNamingContext;
import kxd.net.naming.NamingContext;
import kxd.remote.scs.beans.BaseAd;
import kxd.remote.scs.beans.BaseDeviceStatus;
import kxd.remote.scs.beans.BaseOrgAd;
import kxd.remote.scs.beans.BaseTermAd;
import kxd.remote.scs.beans.adinfo.EditedInfo;
import kxd.remote.scs.beans.adinfo.EditedPrintAd;
import kxd.remote.scs.interfaces.InfoBeanRemote;
import kxd.remote.scs.interfaces.OrgAdBeanRemote;
import kxd.remote.scs.interfaces.PrintAdBeanRemote;
import kxd.remote.scs.interfaces.TermAdBeanRemote;
import kxd.remote.scs.interfaces.service.SerialNoServiceBeanRemote;
import kxd.remote.scs.util.AppException;
import kxd.remote.scs.util.emun.BusinessOpenCloseMode;
import kxd.util.ArrayUtil;
import kxd.util.DateTime;
import kxd.util.memcached.Cacheable;
import kxd.util.memcached.MemCachedClient;

import org.apache.log4j.Logger;

public class TermHelper {
	static final String DB_JNDI = "db";
	static final String MONITOR_JNDI = "monitor";
	static private final Logger logger = Logger.getLogger(TermHelper.class);
	static ArrayBlockingQueue<TermStatusCommand> statusQueue = new ArrayBlockingQueue<TermStatusCommand>(
			5000);
	private static String RETRADE_PREFIX = "$cache.retrade.";
	public static MemCachedClient adMc = Cacheable.memCachedClientMap.get("ad");
	public static MemCachedClient infoMc = Cacheable.memCachedClientMap
			.get("info");
	/**
	 * 指示当前系统是否已经启动成功
	 * 
	 * @return
	 */
	static AtomicBoolean systemStarted = new AtomicBoolean(false);
	static AtomicBoolean systemStarting = new AtomicBoolean(true);
	static Date systemCreateTime = new Date();
	static Date lastCheckSystemStartedTime = new Date(0);

	private static synchronized void inCheckSystemStarted()
			throws NamingException {
		lastCheckSystemStartedTime = new Date();
		if (!systemStarted.get()) {
			logger.debug("调用EJB检查缓存是否加载完毕...");
			NamingContext context = new LoopNamingContext("cache");
			try {
				CacheServiceBeanRemote bean = context.lookup(
						Lookuper.JNDI_TYPE_EJB, "kxd-ejb-termCacheBean",
						CacheServiceBeanRemote.class);
				systemStarted.set(bean.loadInitCache());
				if (!systemStarted.get()) { // 确保下次继续调用EJB检查
					systemStarting.set(false);
				}
			} catch (Throwable e) {
				logger.error("调用EJB检查缓存失败：", e);
				systemStarting.set(true); // 允许再次加载
			} finally {
				context.close();
			}
		}
	}

	public static void checkSystemStarted() throws IOException {
		if (!systemStarted.get()) {
			logger.debug("系统未启动完毕，检查...");
			if (systemStarting.getAndSet(false)) {
				logger.debug("调用checkSystemStarted()检查缓存...");
				try {
					inCheckSystemStarted();
				} catch (Throwable e) {
					throw new IOException(e);
				}
			} else {
				if (DateTime.secondsBetween(systemCreateTime, new Date()) >= 120) { // 10分钟，默认系统启动完毕
					logger.debug("调用checkSystemStarted()超过10分钟未返回，设为系统已启动...");
					systemStarted.set(true);
				}
			}
		}
		if (!systemStarted.get()) {
			throw new IOException("系统尚未启动完毕，请稍候...");
		}
	}

	static {
		new Thread(new Runnable() {

			@Override
			public void run() {
				List<TermStatusCommand> ls = new ArrayList<TermStatusCommand>();
				try {
					while (!Thread.currentThread().isInterrupted()) {
						ls.clear();
						ls.add(statusQueue.take());
						int c = statusQueue.size();
						if (c > 500)
							c = 500;
						while (c > 0) {
							ls.add(statusQueue.take());
							c--;
						}
						try {
							LoopNamingContext context = new LoopNamingContext(
									MONITOR_JNDI);
							try {
								MonitorServiceBeanRemote bean = context.lookup(
										Lookuper.JNDI_TYPE_EJB,
										"kxd-ejb-monitorServiceBean",
										MonitorServiceBeanRemote.class);
								bean.updateTermStatus(ls);
							} finally {
								context.close();
							}
						} catch (Throwable e) {
							logger.error("update term status error: ", e);
						}
					}
				} catch (InterruptedException e) {
					logger.error(e);
				}
			}
		}).start();
	}

	/**
	 * 根据地市编码，获取地市机构ID
	 * 
	 * @param userNoCityCode
	 *            地市编码
	 * @return 地市编码对应的机构ID
	 */
	static public Integer getUserNoOrgId(String userNoCityCode) {
		CachedOrgCode o = CacheHelper.orgCodeMap.get(userNoCityCode);
		if (o != null) {
			return o.getOrgId();
		}
		return null;
	}

	/**
	 * 检查某笔交易是否正在补交，正在补交时，不允许退款<BR>
	 * <font color='red'>注：本函数仅限在管理平台使用</font>
	 * 
	 * @param termGlide
	 *            终端流水
	 * @return 当前交易是否正在补交
	 * @throws InterruptedException
	 * @throws IOException
	 */
	static public boolean isReTrading(String termGlide)
			throws InterruptedException, IOException {
		return CacheHelper.termMc.get(RETRADE_PREFIX + termGlide) != null;
	}

	/**
	 * 将一条交易置为正在补交<BR>
	 * <font color='red'>注：本函数由后台补交线程自动调用</font>
	 * 
	 * @param termGlide
	 *            终端流水
	 * @param trading
	 *            是否正在补交
	 * @throws InterruptedException
	 * @throws IOException
	 */
	static public void setReTrading(String termGlide, boolean trading,
			Date expire) throws InterruptedException, IOException {
		if (!trading)
			CacheHelper.termMc.delete(RETRADE_PREFIX + termGlide);
		else
			CacheHelper.termMc.set(RETRADE_PREFIX + termGlide, true, expire);
	}

	/**
	 * 当终端登录成功后，调用本函数更新缓存
	 * 
	 * @param termId
	 *            登录的终端ID
	 * @throws NamingException
	 */
	static public void termLogined(int termId) throws NamingException {
		try {
			statusQueue.offer(new TermStatusCommand(1, termId), 3,
					TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			logger.error(e);
		}
	}

	static public void updateTermStatus(CachedTerm term, boolean pause,
			int busyTimes, int idleTimes, List<BaseDeviceStatus> status) {
		TermStatusCommand command = new TermStatusCommand(0,
				new TermSubmittedStatus(term.getId(), pause, busyTimes,
						idleTimes, status));
		try {
			statusQueue.offer(command, 3, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			logger.error(e);
		}

	}

	/**
	 * 当终端激活时，调用本函数，更新缓存
	 * 
	 * @param termId
	 *            激活的终端ID
	 * @throws NamingException
	 */
	static public void termActived(int termId) throws NamingException {
		try {
			statusQueue.offer(new TermStatusCommand(2, termId), 3,
					TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			logger.error(e);
		}
	}

	static public String getTradeGlide() throws NamingException {
		LoopNamingContext context = new LoopNamingContext("cache");
		try {
			SerialNoServiceBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-serialNoServiceBean",
					SerialNoServiceBeanRemote.class);
			// int day = Integer.valueOf(new DateTime().format("yyyyMMdd"));
			String id = Long.toString(bean.getNextDaySerial("trade", 1,
					99999999));
			// while (id.length() < 8)
			// id = "0" + id;
			return id;
		} finally {
			context.close();
		}
	}

	/**
	 * 检查终端业务开停，如果检查不通过，则抛出AppException异常
	 * 
	 * @param term
	 *            终端对象
	 * @param tradeCodeId
	 *            交易代码ID
	 * @throws AppException
	 *             当不允许开放时，抛出此异常
	 * @throws NamingException
	 *             当通信遇到异常时抛出
	 */
	static public void checkBusinessOpenClose(CachedTermConfig term,
			Integer tradeCodeId) throws NamingException {
		List<CachedBusinessOpenClose> list = new ArrayList<CachedBusinessOpenClose>();
		CachedBusinessOpenCloseList config = (CachedBusinessOpenCloseList) term
				.getMaintConfig().get("businessopenclose");
		if (config != null)
			list.addAll(config.getConfigList());
		CachedOrg org = term.getTerm().getOrg();
		while (org != null && org.getId() != null) {
			config = (CachedBusinessOpenCloseList) org.getMaintConfigMap().get(
					"businessopenclose");
			if (config != null)
				list.addAll(config.getConfigList());
			org = org.getParentOrg();
		}
		if (list.size() == 0)
			return;
		CachedTradeCode tc = CacheHelper.tradeCodeMap.get(tradeCodeId);
		DateTime now = new DateTime();
		int time = Integer.valueOf(now.format("HHmm"));
		int businessCategory = tc.getBusiness().getBusinessCategoryId();
		// 检查全部开放或全部关闭
		for (int i = list.size() - 1; i >= 0; i--) {
			CachedBusinessOpenClose oc = list.get(i);
			for (Item o : oc.getConfigList()) {
				if (now.getTimeInMillis() < o.startDate.getTime()
						|| now.getTimeInMillis() > o.endDate.getTime()) {
					continue;
				}
				boolean exists = true;
				for (int[] t : o.openTimes) {
					exists = time >= t[0] && time < t[1];
					if (exists)
						break;
				}
				if (exists) {
					boolean payItemNotConfig = tc.getPayItemId() == null
							|| o.payitems == null || o.payitems.length == 0;
					boolean payWayNotConfig = tc.getPayWayId() == null
							|| o.payways == null || o.payways.length == 0;
					if (o.openMode == BusinessOpenCloseMode.FORCE_OPEN) { // 强制开放
						payItemNotConfig = payItemNotConfig
								|| ArrayUtil.contains(o.payitems,
										tc.getPayItemId());
						if (payItemNotConfig
								&& (payWayNotConfig || ArrayUtil.contains(
										o.payways, tc.getPayWayId()))) {
							if (o.businessIds != null
									&& o.businessIds.length > 0
									&& ArrayUtil.contains(o.businessIds,
											tc.getBusinessId())) {
								return;
							}
							if (o.businessCategoryIds != null
									&& o.businessCategoryIds.length > 0
									&& ArrayUtil.contains(
											o.businessCategoryIds,
											businessCategory)) {
								return;
							}
						}
					} else if (o.openMode == BusinessOpenCloseMode.FORCE_STOP) { // 强制关闭
						if (!payItemNotConfig) {
							if (ArrayUtil.contains(o.payitems,
									tc.getPayItemId())) {
								if (payWayNotConfig
										|| ArrayUtil.contains(o.payways,
												tc.getPayWayId())) {
									throw new AppException(o.reason);
								} else
									continue;
							} else
								continue;
						} else if (!payWayNotConfig) {
							if (ArrayUtil.contains(o.payways, tc.getPayWayId())) {
								throw new AppException(o.reason);
							} else
								continue;
						}
						if (o.businessIds != null
								&& o.businessIds.length > 0
								&& ArrayUtil.contains(o.businessIds,
										tc.getBusinessId()))
							throw new AppException(o.reason);
						if (o.businessCategoryIds != null
								&& o.businessCategoryIds.length > 0
								&& ArrayUtil.contains(o.businessCategoryIds,
										businessCategory))
							throw new AppException(o.reason);
					}
				}
			}
		}
		for (CachedBusinessOpenClose oc : list) {
			for (Item o : oc.getConfigList()) {
				if (now.getTimeInMillis() < o.startDate.getTime()
						|| now.getTimeInMillis() > o.endDate.getTime()) {
					continue;
				}
				boolean exists = true;
				for (int[] t : o.openTimes) {
					exists = time >= t[0] && time < t[1];
					if (exists)
						break;
				}
				if (exists) {
					boolean payItemNotConfig = tc.getPayItemId() == null
							|| o.payitems == null || o.payitems.length == 0;
					boolean payWayNotConfig = tc.getPayWayId() == null
							|| o.payways == null || o.payways.length == 0;
					if (o.openMode == BusinessOpenCloseMode.OPEN) { // 开放
						payItemNotConfig = payItemNotConfig
								|| ArrayUtil.contains(o.payitems,
										tc.getPayItemId());
						if (payItemNotConfig
								&& (payWayNotConfig || ArrayUtil.contains(
										o.payways, tc.getPayWayId()))) {
							if (o.businessIds != null
									&& o.businessIds.length > 0
									&& ArrayUtil.contains(o.businessIds,
											tc.getBusinessId())) {
								return;
							}
							if (o.businessCategoryIds != null
									&& o.businessCategoryIds.length > 0
									&& ArrayUtil.contains(
											o.businessCategoryIds,
											businessCategory)) {
								return;
							}
						}
					} else if (o.openMode == BusinessOpenCloseMode.STOP) { // 关闭
						if (!payItemNotConfig) {
							if (ArrayUtil.contains(o.payitems,
									tc.getPayItemId())) {
								if (payWayNotConfig
										|| ArrayUtil.contains(o.payways,
												tc.getPayWayId())) {
									throw new AppException(o.reason);
								} else
									continue;
							} else
								continue;
						} else if (!payWayNotConfig) {
							if (ArrayUtil.contains(o.payways, tc.getPayWayId())) {
								throw new AppException(o.reason);
							} else
								continue;
						}
						if (o.businessIds != null
								&& o.businessIds.length > 0
								&& ArrayUtil.contains(o.businessIds,
										tc.getBusinessId()))
							throw new AppException(o.reason);
						if (o.businessCategoryIds != null
								&& o.businessCategoryIds.length > 0
								&& ArrayUtil.contains(o.businessCategoryIds,
										businessCategory))
							throw new AppException(o.reason);
					}
				}
			}
		}
	}

	/**
	 * 获取机构下指定的广告分类的广告列表
	 * 
	 * @param categoryId
	 *            广告分类ID
	 * @return
	 * @throws InterruptedException
	 * @throws IOException
	 * @throws NamingException
	 */
	@SuppressWarnings("unchecked")
	static private List<BaseOrgAd> getOrgAdList(int orgId, int categoryId)
			throws InterruptedException, IOException, NamingException {
		List<BaseOrgAd> ls = (List<BaseOrgAd>) adMc.get("dc.orgad." + orgId
				+ "." + categoryId);
		if (ls == null) {
			LoopNamingContext context = new LoopNamingContext("db");
			try {
				OrgAdBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
						"kxd-ejb-orgAdBean", OrgAdBeanRemote.class);
				ls = bean.getUsingAdList(orgId, categoryId);
				adMc.set("dc.orgad." + orgId + "." + categoryId, ls);
			} finally {
				context.close();
			}
		}
		return ls;
	}

	@SuppressWarnings("unchecked")
	static private List<BaseTermAd> getTermAdList(int termId, int categoryId)
			throws InterruptedException, IOException, NamingException {
		List<BaseTermAd> ls = (List<BaseTermAd>) adMc.get("dc.termad." + termId
				+ "." + categoryId);
		if (ls == null) {
			LoopNamingContext context = new LoopNamingContext("db");
			try {
				TermAdBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
						"kxd-ejb-termAdBean", TermAdBeanRemote.class);
				ls = bean.getTermAdUsingList(termId, categoryId);
				adMc.set("dc.termad." + termId + "." + categoryId, ls);
			} finally {
				context.close();
			}
		}
		return ls;
	}

	/**
	 * 获取终端广告列表
	 * 
	 * @param org
	 * @param categoryId
	 * @param ls
	 * @throws InterruptedException
	 * @throws IOException
	 * @throws NamingException
	 */
	static public List<BaseAd> getAdList(CachedTerm term, int categoryId)
			throws InterruptedException, IOException, NamingException {
		List<BaseAd> ls = new ArrayList<BaseAd>();
		List<BaseTermAd> tl = getTermAdList(term.getId(), categoryId);
		if (tl != null)
			ls.addAll(tl);
		List<BaseOrgAd> l = getOrgAdList(term.getOrgId(), categoryId);
		if (l != null)
			ls.addAll(l);
		for (Integer id : term.getOrg().getParents()) {
			l = getOrgAdList(id, categoryId);
			if (l != null)
				ls.addAll(l);
		}
		return ls;
	}

	@SuppressWarnings("unchecked")
	static private List<EditedInfo> getOrgInfoList(int orgId, int categoryId)
			throws InterruptedException, IOException, NamingException {
		List<EditedInfo> ls = (List<EditedInfo>) infoMc.get("dc.info." + orgId
				+ "." + categoryId);
		if (ls == null) {
			LoopNamingContext context = new LoopNamingContext("db");
			try {
				InfoBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
						"kxd-ejb-infoBean", InfoBeanRemote.class);
				ls = bean.getUsingInfoList(orgId, categoryId);
				infoMc.set("dc.info." + orgId + "." + categoryId, ls);
			} finally {
				context.close();
			}
		}
		return ls;
	}

	/**
	 * 获取终端的信息列表
	 * 
	 * @param term
	 * @param categoryId
	 * @param ls
	 * @throws InterruptedException
	 * @throws IOException
	 * @throws NamingException
	 */
	static public List<EditedInfo> getInfoList(CachedTerm term, int categoryId)
			throws InterruptedException, IOException, NamingException {
		List<EditedInfo> ls = getOrgInfoList(term.getOrgId(), categoryId);
		if (ls == null)
			ls = new ArrayList<EditedInfo>();
		for (Integer id : term.getOrg().getParents()) {
			List<EditedInfo> l = getOrgInfoList(id, categoryId);
			if (l != null)
				ls.addAll(l);
		}
		return ls;
	}

	@SuppressWarnings("unchecked")
	static private EditedPrintAd getOrgPrintAd(int orgId, int categoryId)
			throws InterruptedException, IOException, NamingException {
		List<EditedPrintAd> ls = (List<EditedPrintAd>) adMc.get("dc.printad."
				+ orgId + "." + categoryId);
		if (ls == null) {
			LoopNamingContext context = new LoopNamingContext("db");
			try {
				PrintAdBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
						"kxd-ejb-printAdBean", PrintAdBeanRemote.class);
				ls = bean.getUsingAdList(orgId, categoryId);
				adMc.set("dc.printad." + orgId + "." + categoryId, ls);
			} finally {
				context.close();
			}
		}
		if (ls.size() > 0)
			return ls.get(0);
		else
			return null;
	}

	/**
	 * 获取终端打印广告
	 * 
	 * @param term
	 * @param categoryId
	 * @return
	 * @throws InterruptedException
	 * @throws IOException
	 * @throws NamingException
	 */
	public static EditedPrintAd getPrintAd(CachedTerm term, int categoryId)
			throws InterruptedException, IOException, NamingException {
		EditedPrintAd ad = getOrgPrintAd(term.getOrgId(), categoryId);
		if (ad != null)
			return ad;
		for (Integer id : term.getOrg().getParents()) {
			ad = getOrgPrintAd(id, categoryId);
			if (ad != null)
				return ad;
		}
		return null;
	}

}