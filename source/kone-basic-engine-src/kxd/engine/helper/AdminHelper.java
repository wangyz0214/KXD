package kxd.engine.helper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.naming.NamingException;

import kxd.engine.cache.beans.sts.CachedManuf;
import kxd.engine.cache.ejb.interfaces.EjbCacheServiceBeanRemote;
import kxd.engine.dao.Dao;
import kxd.engine.helper.beans.IdentOrg;
import kxd.engine.helper.beans.IdentOrgScope;
import kxd.engine.scs.monitor.MonitorServiceBeanRemote;
import kxd.engine.scs.monitor.RefundEventData;
import kxd.net.connection.jndi.Lookuper;
import kxd.net.naming.LoopNamingContext;
import kxd.net.naming.NamingContext;
import kxd.remote.scs.beans.device.EditedTerm;
import kxd.remote.scs.beans.right.EditedOrg;
import kxd.remote.scs.beans.right.QueryedOrg;
import kxd.remote.scs.interfaces.service.TermServiceBeanRemote;
import kxd.remote.scs.util.AppException;
import kxd.util.DateTime;
import kxd.util.memcached.Cacheable;
import kxd.util.memcached.MemCachedClient;

/**
 * 管理平台工具类，此类的函数仅限在管理平台使用，在终端侧使用会影响性能
 * 
 * @author zhaom
 * 
 */
public class AdminHelper {
	static final String DB_JNDI = "db";
	static final String MONITOR_JNDI = "monitor";
	static final String EJBCACHE_JNDI = "ejbcache";
	static final MemCachedClient adminMc = Cacheable.memCachedClientMap
			.get("admin");
	static final String ORGLASTVERIONKEY = "$cache.orglastversion";

	static public String getOrgLastVersionKeyPrefix() {
		if (adminMc == null)
			return null;
		try {
			Object o = adminMc.get(ORGLASTVERIONKEY);
			if (o == null)
				return "";
			else
				return (String) o;
		} catch (Throwable e) {
			return null;
		}
	}

	static public void updateOrgLastVersionKeyPrefix() {
		if (adminMc == null)
			return;
		try {
			adminMc.set(ORGLASTVERIONKEY, UUID.randomUUID().toString());
		} catch (Throwable e) {
			throw new AppException(e);
		}
	}

	/**
	 * 当在管理平台有新的机构加入时，调用本函数，更新缓存<BR>
	 * <font color='red'>注：本函数仅限在管理平台使用</font>
	 * 
	 * @param org
	 *            新加入的机构
	 * @throws NamingException
	 */
	static public void orgAdded(EditedOrg org) throws NamingException {
		updateOrgLastVersionKeyPrefix();
		LoopNamingContext context = new LoopNamingContext(EJBCACHE_JNDI);
		try {
			EjbCacheServiceBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-ejbCacheServiceBean",
					EjbCacheServiceBeanRemote.class);
			bean.orgAdded(org);
		} finally {
			context.close();
		}
	}

	/**
	 * 当在管理修改机构信息后，调用本函数，更新缓存<BR>
	 * <font color='red'>注：本函数仅限在管理平台使用</font>
	 * 
	 * @param org
	 *            被修改的机构
	 * @throws NamingException
	 */
	static public void orgModified(EditedOrg org) throws NamingException {
		LoopNamingContext context = new LoopNamingContext(EJBCACHE_JNDI);
		try {
			EjbCacheServiceBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-ejbCacheServiceBean",
					EjbCacheServiceBeanRemote.class);
			bean.orgModified(org);
		} finally {
			context.close();
		}
	}

	/**
	 * 当在管理平台删除机构信息后，更新缓存<BR>
	 * <font color='red'>注：本函数仅限在管理平台使用</font>
	 * 
	 * @param orgId
	 *            被移除的机构ID
	 * @throws NamingException
	 */
	static public void orgRemoved(int orgId) throws NamingException {
		updateOrgLastVersionKeyPrefix();
		LoopNamingContext context = new LoopNamingContext(EJBCACHE_JNDI);
		try {
			EjbCacheServiceBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-ejbCacheServiceBean",
					EjbCacheServiceBeanRemote.class);
			bean.orgRemoved(orgId);
		} finally {
			context.close();
		}
	}

	static public void orgMoved(int orgId, int newParentOrgId)
			throws NamingException {
		updateOrgLastVersionKeyPrefix();
		LoopNamingContext context = new LoopNamingContext(EJBCACHE_JNDI);
		try {
			EjbCacheServiceBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-ejbCacheServiceBean",
					EjbCacheServiceBeanRemote.class);
			bean.orgMoved(orgId, newParentOrgId);
		} finally {
			context.close();
		}
	}

	/**
	 * 从缓存中获取树列表<BR>
	 * <font color='red'>注：本函数仅限在管理平台使用</font>
	 * 
	 * @param parentOrgId
	 *            父机构ID，null表示从根结点开始
	 * @param depth
	 *            查询深度，表示获取至第几级子机构
	 * @param selectedOrgId
	 *            选定的orgid，null表示没有选定的机构
	 * @return 获取的机构树列表
	 * @throws NamingException
	 */
	static public Collection<QueryedOrg> getOrgTreeItems(Integer parentOrgId,
			int depth, Integer selectedOrgId, boolean includeSelf,
			String keyword) throws NamingException {

		LoopNamingContext context = new LoopNamingContext(EJBCACHE_JNDI);
		try {
			EjbCacheServiceBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-ejbCacheServiceBean",
					EjbCacheServiceBeanRemote.class);
			return bean.getOrgTreeItems(parentOrgId, depth, selectedOrgId,
					includeSelf, keyword);
		} finally {
			context.close();
		}
	}

	/**
	 * 从缓存中获取机构树路径<BR>
	 * <font color='red'>注：本函数仅限在管理平台使用</font>
	 * 
	 * @param orgId
	 *            机构ID
	 * @param separator
	 *            路径分隔符
	 * @return 路径字串
	 * @throws NamingException
	 */
	static public String getOrgTreePath(int orgId, String separator)
			throws NamingException {
		LoopNamingContext context = new LoopNamingContext(EJBCACHE_JNDI);
		try {
			EjbCacheServiceBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-ejbCacheServiceBean",
					EjbCacheServiceBeanRemote.class);
			return bean.getOrgTreePath(orgId, separator);
		} finally {
			context.close();
		}
	}

	/**
	 * 获取机构所属的省、市<BR>
	 * <font color='red'>注：本函数仅限在管理平台使用</font>
	 * 
	 * @param orgIdList
	 *            要获取机构的ID列表
	 * @return 基于Object[]的Map，返回对应机构的数据，定义如下： <br>
	 *         object[0] - 该机构所属省名称<br>
	 *         object[1] - 该机构所属地市名称<br>
	 *         object[2] - 机构名称<br>
	 */
	static public Map<?, ?> getOrgProvinceCity(List<?> orgIdList)
			throws NamingException {
		LoopNamingContext context = new LoopNamingContext(EJBCACHE_JNDI);
		try {
			EjbCacheServiceBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-ejbCacheServiceBean",
					EjbCacheServiceBeanRemote.class);
			return bean.getOrgProvinceCity(orgIdList);
		} finally {
			context.close();
		}
	}

	/**
	 * 获取机构下的全部终端ID列表<br>
	 * <font color='red'>注：本函数直接查询数据库，有可能影响系统性能，仅限在管理平台使用</font>
	 * 
	 * @param orgId
	 *            要获取的机构ID
	 * @return 终端ID列表
	 * @throws NamingException
	 */
	static public List<Integer> getOrgTerms(int orgId) throws NamingException {
		NamingContext context = new LoopNamingContext(DB_JNDI);
		try {
			TermServiceBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-termServiceBean", TermServiceBeanRemote.class);
			return bean.getOrgTerms(orgId);
		} finally {
			context.close();
		}
	}

	/**
	 * 获取指定机构的父机构ID列表<br>
	 * <font color='red'>注：本函数直接查询数据库，有可能影响系统性能，仅限在管理平台使用</font>
	 * 
	 * @param orgId
	 *            指定的机构ID
	 * @return 父机构ID列表，级别由近及远
	 * @throws NamingException
	 */
	// static public List<Integer> getOrgParents(int orgId) throws
	// NamingException {
	// NamingContext context = new LoopNamingContext(DB_JNDI);
	// try {
	// TermServiceBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
	// "kxd-ejb-termServiceBean", TermServiceBeanRemote.class);
	// return bean.getOrgParents(orgId);
	// } finally {
	// context.close();
	// }
	// }

	/**
	 * 获取指定机构下的全部机构ID列表<br>
	 * <font color='red'>注：本函数直接查询数据库，有可能影响系统性能，仅限在管理平台使用</font>
	 * 
	 * @param orgId
	 *            指定的机构ID
	 * @return 该机构下的终端ID列表，含子机构
	 * @throws NamingException
	 */
	static public List<Integer> getOrgChildren(int orgId)
			throws NamingException {
		NamingContext context = new LoopNamingContext(DB_JNDI);
		try {
			TermServiceBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-termServiceBean", TermServiceBeanRemote.class);
			return bean.getOrgChildren(orgId);
		} finally {
			context.close();
		}
	}

	/**
	 * 获取一个机构的起始和终止序号<br>
	 * <font color=red>此函数仅用于EJB容器模块，不适用WEB容器</font>
	 * 
	 * @param dao
	 *            数据访问对象
	 * @param orgId
	 *            机构ID
	 */
	static public IdentOrgScope getOrgIdentScope(String orgVersionPrefix,
			Dao dao, int orgId) {
		try {
			String key = null;
			if (adminMc != null && orgVersionPrefix != null) {
				key = "$cache.orgidenscope." + orgVersionPrefix + "." + orgId;
				Object r = (Object) adminMc.get(key);
				if (r != null)
					return (IdentOrgScope) r;
			}
			Iterator<?> it = dao
					.query("select ident,lastchildorgid,depth,orgname from org where orgid=?1",
							orgId).iterator();
			if (it.hasNext()) {
				IdentOrgScope is = new IdentOrgScope();
				Object o[] = (Object[]) it.next();
				is.setId(orgId);
				is.setStartIdent(Integer.valueOf(o[0].toString()));
				is.setDepth(Integer.valueOf(o[2].toString()));
				is.setOrgDesp((String) o[3]);
				if (o[1] == null) {
					is.setEndIdent(is.getStartIdent());
				} else {
					it = dao.query("select ident from org where orgid=" + o[1])
							.iterator();
					if (it.hasNext()) {
						is.setEndIdent(Integer.valueOf(it.next().toString()));
					}
				}
				if (key != null) {
					adminMc.set(key, is, new DateTime().addHours(1).getTime());
				}
				return is;
			}
		} catch (Throwable e) {
			throw new AppException(e);
		}
		throw new AppException("获取机构[id=" + orgId + "]信息失败.");
	}

	/**
	 * 获取机构下 一级的子机构及序号列表<br>
	 * <font color=red>此函数仅用于EJB容器模块，不适用WEB容器</font>
	 * 
	 * @param dao
	 *            数据访问对象
	 * @param orgIdentScope
	 *            机构序号范围
	 * @return 子机构列表
	 */
	static public List<IdentOrg> getOrgChildIdents(String orgVersionPrefix,
			Dao dao, IdentOrgScope orgIdentScope) {
		try {
			String key = null;
			if (adminMc != null && orgVersionPrefix != null) {
				key = "$cache.orgchildidents." + orgVersionPrefix + "."
						+ orgIdentScope.getIdString();
				List<IdentOrg> ls = adminMc.getStreamableList(key,
						IdentOrg.class);
				if (ls != null)
					return ls;
			}
			Iterator<?> it = dao.query(
					"select orgid,orgname,ident from org where ident>?1 and ident<=?2 and depth="
							+ (orgIdentScope.getDepth() + 1)
							+ " order by ident", orgIdentScope.getStartIdent(),
					orgIdentScope.getEndIdent()).iterator();
			if (it.hasNext()) {
				List<IdentOrg> ret = new ArrayList<IdentOrg>();
				while (it.hasNext()) {
					IdentOrg is = new IdentOrg();
					Object o[] = (Object[]) it.next();
					is.setOrgId(Integer.valueOf(o[0].toString()));
					is.setOrgDesp((String) o[1]);
					is.setIdent(Integer.valueOf(o[2].toString()));
					ret.add(is);
				}
				if (key != null) {
					adminMc.setStreamableList(key, ret, new DateTime()
							.addHours(1).getTime());
				}
				return ret;
			} else
				return new ArrayList<IdentOrg>();
		} catch (Throwable e) {
			throw new AppException(e);
		}
	}

	/**
	 * 从按ident排序的机构列表中，找出ident所在的机构
	 * 
	 * @param ls
	 *            机构列表
	 * @param ident
	 *            目标序号
	 * @return 找到的机构
	 */
	static public <E extends IdentOrg> E getOrgIndexIdInIdents(List<E> ls,
			int ident) {
		for (int i = ls.size() - 1; i >= 0; i--) {
			E o = ls.get(i);
			if (ident >= o.getIdent())
				return o;
		}
		return null;
	}

	/**
	 * 清除机构下面的终端缓存，注意，此函数只在管理平台使用
	 * 
	 * @param orgId
	 * @throws NamingException
	 */
	static public void cleanOrgTermsCache(int orgId) {
		List<Integer> ls;
		try {
			ls = getOrgTerms(orgId);
		} catch (NamingException e) {
			throw new AppException(e);
		}
		CacheHelper.termMap.removeAll(ls);
	}

	/**
	 * 当在管理平台有新的终端加入时，调用本函数，更新缓存<BR>
	 * <font color='red'>注：本函数仅限在管理平台使用</font>
	 * 
	 * @param term
	 *            新加入的终端
	 * @throws NamingException
	 */
	static public void termAdded(EditedTerm term) throws NamingException {
		CachedManuf manuf = CacheHelper.termTypeMap.get(
				term.getTermType().getId()).getManuf();
		if (manuf.getManufType() != 0) // 非本系统终端，不参与监控
			return;
		LoopNamingContext context = new LoopNamingContext(MONITOR_JNDI);
		try {
			MonitorServiceBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-monitorServiceBean",
					MonitorServiceBeanRemote.class);
			bean.termAdded(term);
		} finally {
			context.close();
		}
	}

	/**
	 * 当在管理平台修改终端信息时，调用本函数，更新缓存<BR>
	 * <font color='red'>注：本函数仅限在管理平台使用</font>
	 * 
	 * @param termId
	 *            被修改的终端信息
	 * @throws NamingException
	 */
	static public void termModified(EditedTerm oldTerm, EditedTerm newTerm)
			throws NamingException {
		CachedManuf oldManuf = CacheHelper.termTypeMap.get(
				oldTerm.getTermType().getId()).getManuf();
		CachedManuf newManuf = CacheHelper.termTypeMap.get(
				newTerm.getTermType().getId()).getManuf();
		if (oldManuf != null && newManuf != null) {
			boolean oldIsLocal = oldManuf.getManufType() == 0;
			boolean newIsLocal = newManuf.getManufType() == 0;
			if (!oldIsLocal) { // 假如之前是第三方厂商
				if (!newIsLocal)
					return; // 现在依然是第三方厂商，直接返回，不监控
				else {// 现在非第三方厂商，则添加监控
					LoopNamingContext context = new LoopNamingContext(
							MONITOR_JNDI);
					try {
						MonitorServiceBeanRemote bean = context.lookup(
								Lookuper.JNDI_TYPE_EJB,
								"kxd-ejb-monitorServiceBean",
								MonitorServiceBeanRemote.class);
						bean.termAdded(newTerm);
					} finally {
						context.close();
					}
					return;
				}
			} else { // 假如之前不是第三方厂商
				if (!newIsLocal) { // 现在是第三方厂商，则移除之前的监控
					LoopNamingContext context = new LoopNamingContext(
							MONITOR_JNDI);
					try {
						MonitorServiceBeanRemote bean = context.lookup(
								Lookuper.JNDI_TYPE_EJB,
								"kxd-ejb-monitorServiceBean",
								MonitorServiceBeanRemote.class);
						bean.termRemoved(oldTerm.getId());
					} finally {
						context.close();
					}
					return;
				} else {// 现在依然不是第三方厂商，则更新监控
					LoopNamingContext context = new LoopNamingContext(
							MONITOR_JNDI);
					try {
						MonitorServiceBeanRemote bean = context.lookup(
								Lookuper.JNDI_TYPE_EJB,
								"kxd-ejb-monitorServiceBean",
								MonitorServiceBeanRemote.class);
						bean.termModified(newTerm);
					} finally {
						context.close();
					}
				}
			}
		}
	}

	/**
	 * 当在管理平台删除终端时，调用本函数，更新缓存<BR>
	 * <font color='red'>注：本函数仅限在管理平台使用</font>
	 * 
	 * @param termId
	 *            被移除的终端ID
	 * @throws NamingException
	 */
	static public void termRemoved(EditedTerm term) throws NamingException {
		CachedManuf manuf = CacheHelper.termTypeMap.get(
				term.getTermType().getId()).getManuf();
		if (manuf.getManufType() != 0)
			return;
		LoopNamingContext context = new LoopNamingContext(MONITOR_JNDI);
		try {
			MonitorServiceBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-monitorServiceBean",
					MonitorServiceBeanRemote.class);
			bean.termRemoved(term.getId());
		} finally {
			context.close();
		}
	}

	/**
	 * 终端型号增加设备，更新缓存<BR>
	 * <font color='red'>注：本函数仅限在管理平台使用</font>
	 * 
	 * @param termType
	 *            增加设备的型号ID
	 * @param deviceId
	 *            增加的设备ID
	 * @throws NamingException
	 */
	static public void termTypeDeviceAdded(int termType, int deviceId)
			throws NamingException {
		LoopNamingContext context = new LoopNamingContext(MONITOR_JNDI);
		try {
			MonitorServiceBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-monitorServiceBean",
					MonitorServiceBeanRemote.class);
			bean.termTypeDeviceAdded(termType, deviceId);
		} finally {
			context.close();
		}
	}

	/**
	 * 终端型号删除设备，更新缓存<BR>
	 * <font color='red'>注：本函数仅限在管理平台使用</font>
	 * 
	 * @param termType
	 *            删除设备的型号ID
	 * @param deviceId
	 *            被删除的设备ID
	 * @throws NamingException
	 */
	static public void termTypeDeviceRemoved(int termType, int deviceId)
			throws NamingException {
		LoopNamingContext context = new LoopNamingContext(MONITOR_JNDI);
		try {
			MonitorServiceBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-monitorServiceBean",
					MonitorServiceBeanRemote.class);
			bean.termTypeDeviceRemoved(termType, deviceId);
		} finally {
			context.close();
		}
	}

	/**
	 * 当终端暂停和恢复服务，调用本函数，更新缓存
	 * 
	 * @param terms
	 *            暂停或恢复服务的终端列表
	 * @param pause
	 *            true - 暂停；false - 恢复
	 * @throws NamingException
	 */
	static public void termPauseResume(Collection<Integer> terms, boolean pause)
			throws NamingException {
		LoopNamingContext context = new LoopNamingContext(MONITOR_JNDI);
		try {
			MonitorServiceBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-monitorServiceBean",
					MonitorServiceBeanRemote.class);
			bean.termPauseResume(terms, pause);
		} finally {
			context.close();
		}
	}

	static public void updateRefundStatus(RefundEventData o)
			throws NamingException {
		LoopNamingContext context = new LoopNamingContext(MONITOR_JNDI);
		try {
			MonitorServiceBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-monitorServiceBean",
					MonitorServiceBeanRemote.class);
			bean.updateRefundStatus(o);
		} finally {
			context.close();
		}
	}

	static public void updateCancelRefundStatus(RefundEventData o)
			throws NamingException {
		LoopNamingContext context = new LoopNamingContext(MONITOR_JNDI);
		try {
			MonitorServiceBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-monitorServiceBean",
					MonitorServiceBeanRemote.class);
			bean.updateCancelRefundStatus(o);
		} finally {
			context.close();
		}
	}

	static public void infoChanged(int orgId, int category) {
		try {
			TermHelper.infoMc.delete("dc.info." + orgId + "." + category); // 删除缓存，下次取时会重新获取
		} catch (Throwable e) {
		}
	}

	static public void orgAdChanged(int orgId, int category) {
		try {
			TermHelper.adMc.delete("dc.orgad." + orgId + "." + category); // 删除缓存，下次取时会重新获取
		} catch (Throwable e) {
		}
	}

	static public void printAdChanged(int orgId, int category) {
		try {
			TermHelper.adMc.delete("dc.printad." + orgId + "." + category); // 删除缓存，下次取时会重新获取
		} catch (Throwable e) {
		}
	}

	static public void termAdChanged(int termId, int category) {
		try {
			TermHelper.adMc.delete("dc.termad." + termId + "." + category); // 删除缓存，下次取时会重新获取
		} catch (Throwable e) {
		}
	}

}
