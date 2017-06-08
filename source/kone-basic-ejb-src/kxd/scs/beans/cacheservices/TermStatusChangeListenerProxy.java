package kxd.scs.beans.cacheservices;

import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

import kxd.engine.scs.monitor.CachedMonitoredTerm;
import kxd.engine.scs.monitor.TermStatusEventListener;

import org.apache.log4j.Logger;

public class TermStatusChangeListenerProxy {
	final public CopyOnWriteArrayList<TermStatusEventListener> termStatusEventListener = new CopyOnWriteArrayList<TermStatusEventListener>();
	final static private Logger logger = Logger
			.getLogger(TermStatusChangeListenerProxy.class);

	/**
	 * 终端状态改变
	 * 
	 * @param termId
	 *            终端ID
	 * @param status
	 *            设备状态列表
	 */
	public void statusChange(CachedMonitoredTerm term) {
		Iterator<TermStatusEventListener> it = termStatusEventListener
				.iterator();
		while (it.hasNext()) {
			try {
				it.next().statusChange(term);
			} catch (Throwable e) {
				logger.error("term[id=" + term.getId()
						+ "] status changed[listener]:", e);
			}
		}
	}

	/**
	 * 有新的终端加入
	 * 
	 * @param termId
	 */
	public void termAdded(CachedMonitoredTerm term) {
		Iterator<TermStatusEventListener> it = termStatusEventListener
				.iterator();
		while (it.hasNext()) {
			try {
				it.next().termAdded(term);
			} catch (Throwable e) {
				logger.error("term[id=" + term.getId() + "] added[listener]:",
						e);
			}
		}
	}

	/**
	 * 终端被修改
	 * 
	 * @param termId
	 */
	public void termModified(CachedMonitoredTerm term, int oldParentOrgId) {
		Iterator<TermStatusEventListener> it = termStatusEventListener
				.iterator();
		while (it.hasNext()) {
			try {
				it.next().termModified(term, oldParentOrgId);
			} catch (Throwable e) {
				logger.error("term[id=" + term.getId()
						+ "] modified[listener]:", e);
			}
		}
	}

	/**
	 * 终端被移除
	 * 
	 * @param termId
	 */
	public void termRemoved(CachedMonitoredTerm term) {
		Iterator<TermStatusEventListener> it = termStatusEventListener
				.iterator();
		while (it.hasNext()) {
			try {
				it.next().termRemoved(term);
			} catch (Throwable e) {
				logger.error(
						"term[id=" + term.getId() + "] removed[listener]:", e);
			}
		}
	}

	/**
	 * 当一个终端型号增加设备时，调用本方法通知监控服务器，处理该事件，修正监控的设备
	 * 
	 * @param termType
	 *            型号ID
	 * @param device
	 *            增加的设备ID
	 */
	public void termDeviceAdded(CachedMonitoredTerm term, int deviceId) {
		Iterator<TermStatusEventListener> it = termStatusEventListener
				.iterator();
		while (it.hasNext()) {
			try {
				it.next().termDeviceAdded(term, deviceId);
			} catch (Throwable e) {
				logger.error("term[id=" + term.getId() + "] device[id="
						+ deviceId + "] added[listener]:", e);
			}
		}
	}

	/**
	 * 当一个终端型号删除设备时，调用本方法通知监控服务器，处理该事件，修正监控的设备
	 * 
	 * @param termType
	 *            型号ID
	 * @param device
	 *            删除的设备ID
	 */
	public void termDeviceRemoved(CachedMonitoredTerm term, int deviceId) {
		Iterator<TermStatusEventListener> it = termStatusEventListener
				.iterator();
		while (it.hasNext()) {
			try {
				it.next().termDeviceRemoved(term, deviceId);
			} catch (Throwable e) {
				logger.error("term[id=" + term.getId() + "] device[id="
						+ deviceId + "] removed[listener]:", e);
			}
		}
	}

	/**
	 * 终端登录事件，登记终端最后在线时间
	 * 
	 * @param termId
	 */
	public void termLogined(CachedMonitoredTerm term) {
		Iterator<TermStatusEventListener> it = termStatusEventListener
				.iterator();
		while (it.hasNext()) {
			try {
				it.next().termLogined(term);
			} catch (Throwable e) {
				logger.error(
						"term[id=" + term.getId() + "] logined[listener]:", e);
			}
		}
	}

	/**
	 * 当终端激活时，调用本函数，更新缓存
	 * 
	 * @param termId
	 *            激活的终端ID
	 */
	public void termActived(CachedMonitoredTerm term) {
		Iterator<TermStatusEventListener> it = termStatusEventListener
				.iterator();
		while (it.hasNext()) {
			try {
				it.next().termActived(term);
			} catch (Throwable e) {
				logger.error(
						"term[id=" + term.getId() + "] actived[listener]:", e);
			}
		}
	}

	/**
	 * 当终端暂停和恢复服务，调用本函数，更新缓存
	 * 
	 * @param terms
	 *            暂停或恢复服务的终端列表
	 * @param pause
	 *            true - 暂停；false - 恢复
	 */
	public void termPauseResume(CachedMonitoredTerm term, boolean pause) {
		Iterator<TermStatusEventListener> it = termStatusEventListener
				.iterator();
		while (it.hasNext()) {
			try {
				it.next().termPauseResume(term, pause);
			} catch (Throwable e) {
				logger.error("term[id=" + term.getId()
						+ "] pause or resume[listener]:", e);
			}
		}
	}

}
