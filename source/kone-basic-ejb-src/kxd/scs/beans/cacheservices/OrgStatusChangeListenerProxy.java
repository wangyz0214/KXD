package kxd.scs.beans.cacheservices;

import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

import kxd.engine.cache.ejb.beans.EjbCachedOrgNode;
import kxd.engine.cache.ejb.interfaces.OrgChangeNotifyBeanRemote;
import kxd.engine.scs.monitor.OrgStatusEventListener;
import kxd.net.connection.jndi.Lookuper;
import kxd.net.naming.LoopNamingContext;

import org.apache.log4j.Logger;

public class OrgStatusChangeListenerProxy {
	final public CopyOnWriteArrayList<OrgStatusEventListener> orgStatusEventListener = new CopyOnWriteArrayList<OrgStatusEventListener>();
	final public CopyOnWriteArrayList<String[]> orgNotifies = new CopyOnWriteArrayList<String[]>();
	final static private Logger logger = Logger
			.getLogger(OrgStatusChangeListenerProxy.class);

	/**
	 * 有新的机构加入
	 * 
	 * @param org
	 */
	public void orgAdded(EjbCachedOrgNode org) {
		Iterator<String[]> it = orgNotifies.iterator();
		while (it.hasNext()) {
			try {
				String[] a = it.next();
				LoopNamingContext context = new LoopNamingContext(a[0], a[1]);
				try {
					OrgChangeNotifyBeanRemote bean = context.lookup(
							Lookuper.JNDI_TYPE_EJB,
							"kxd-ejb-orgChangeNotifyBean",
							OrgChangeNotifyBeanRemote.class);
					bean.orgAdded(org.toEditedOrg());
				} finally {
					context.close();
				}
			} catch (Throwable e) {
				logger.error("org[id=" + org.getId() + "] added[listener]:", e);
			}
		}
		Iterator<OrgStatusEventListener> it1 = orgStatusEventListener
				.iterator();
		while (it1.hasNext()) {
			try {
				it1.next().orgAdded(org);
			} catch (Throwable e) {
				logger.error("org[id=" + org.getId() + "] added[listener]:", e);
			}
		}
	}

	/**
	 * 机构信息被修改，更新缓存
	 * 
	 * @param org
	 */
	public void orgModified(EjbCachedOrgNode org) {
		Iterator<String[]> it = orgNotifies.iterator();
		while (it.hasNext()) {
			try {
				String[] a = it.next();
				LoopNamingContext context = new LoopNamingContext(a[0], a[1]);
				try {
					OrgChangeNotifyBeanRemote bean = context.lookup(
							Lookuper.JNDI_TYPE_EJB,
							"kxd-ejb-orgChangeNotifyBean",
							OrgChangeNotifyBeanRemote.class);
					bean.orgModified(org.toEditedOrg());
				} finally {
					context.close();
				}
			} catch (Throwable e) {
				logger.error("org[id=" + org.getId() + "] modified[listener]:",
						e);
			}
		}
		Iterator<OrgStatusEventListener> it1 = orgStatusEventListener
				.iterator();
		while (it1.hasNext()) {
			try {
				it1.next().orgModified(org);
			} catch (Throwable e) {
				logger.error("org[id=" + org.getId() + "] modified[listener]:",
						e);
			}
		}
	}

	/**
	 * 机构信息被移除，更新缓存
	 * 
	 * @param orgId
	 */
	public void orgRemoved(EjbCachedOrgNode org) {
		Iterator<String[]> it = orgNotifies.iterator();
		while (it.hasNext()) {
			try {
				String[] a = it.next();
				LoopNamingContext context = new LoopNamingContext(a[0], a[1]);
				try {
					OrgChangeNotifyBeanRemote bean = context.lookup(
							Lookuper.JNDI_TYPE_EJB,
							"kxd-ejb-orgChangeNotifyBean",
							OrgChangeNotifyBeanRemote.class);
					bean.orgRemoved(org.getId());
				} finally {
					context.close();
				}
			} catch (Throwable e) {
				logger.error("org[id=" + org.getId() + "] removed[listener]:",
						e);
			}
		}
		Iterator<OrgStatusEventListener> it1 = orgStatusEventListener
				.iterator();
		while (it1.hasNext()) {
			try {
				it1.next().orgRemoved(org);
			} catch (Throwable e) {
				logger.error("org[id=" + org.getId() + "] removed[listener]:",
						e);
			}
		}
	}

	/**
	 * 机构迁移
	 * 
	 * @param dao
	 * @param org
	 * @param newParentOrg
	 */
	public void orgMoved(EjbCachedOrgNode org, EjbCachedOrgNode newParentOrg) {
		Iterator<String[]> it = orgNotifies.iterator();
		while (it.hasNext()) {
			try {
				String[] a = it.next();
				LoopNamingContext context = new LoopNamingContext(a[0], a[1]);
				try {
					OrgChangeNotifyBeanRemote bean = context.lookup(
							Lookuper.JNDI_TYPE_EJB,
							"kxd-ejb-orgChangeNotifyBean",
							OrgChangeNotifyBeanRemote.class);
					bean.orgMoved(org.getId(), newParentOrg.getId());
				} finally {
					context.close();
				}
			} catch (Throwable e) {
				logger.error(
						"org[id=" + org.getId() + ",newid="
								+ newParentOrg.getId() + "] moved[listener]:",
						e);
			}
		}
		Iterator<OrgStatusEventListener> it1 = orgStatusEventListener
				.iterator();
		while (it1.hasNext()) {
			try {
				it1.next().orgMoved(org, newParentOrg);
			} catch (Throwable e) {
				logger.error(
						"org[id=" + org.getId() + ",newid="
								+ newParentOrg.getId() + "] moved[listener]:",
						e);
			}
		}
	}

}
