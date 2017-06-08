package kxd.engine.scs.monitor;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import kxd.engine.cache.ejb.beans.EjbCachedOrgNode;
import kxd.engine.scs.trade.drivers.InterfaceEventData;
import kxd.engine.scs.trade.drivers.TradeEventData;
import kxd.remote.scs.util.emun.SettlementType;
import kxd.util.DateTime;
import kxd.util.IdableObject;
import kxd.util.TreeNode;

import org.apache.log4j.Logger;

public class TermMonitoredOrgNode extends EjbCachedOrgNode implements
		StatusEventListener {
	private static final long serialVersionUID = 1L;
	private CopyOnWriteArrayList<CachedMonitoredTerm> terms = new CopyOnWriteArrayList<CachedMonitoredTerm>();
	private ConcurrentHashMap<Short, CachedInterfaceMonitorData> interfaceMonitorMap = new ConcurrentHashMap<Short, CachedInterfaceMonitorData>();
	private ConcurrentHashMap<Integer, CachedTradeMonitorData> tradeStatusMap = new ConcurrentHashMap<Integer, CachedTradeMonitorData>();
	private ConcurrentHashMap<Integer, TermMonitorData> termStatusMap = new ConcurrentHashMap<Integer, TermMonitorData>();

	public TermMonitoredOrgNode() {
		super();
	}

	public void addMonitoredTerm(CachedMonitoredTerm o) {
		if (!terms.contains(o))
			terms.add(o);
		if (o.getStatusEventlistener() == null)
			o.setStatusEventlistener(this);
		TermMonitorData data = termStatusMap.get(o.getManufId());
		if (data == null) {
			data = new TermMonitorData();
			termStatusMap.put(o.getManufId(), data);
		}
		data.addMonitorTerm(o);
		if (getParent() != null && getParent().getId() != null)
			((TermMonitoredOrgNode) getParent()).addMonitoredTerm(o);
	}

	public void removeMonitoredTerm(CachedMonitoredTerm o) {
		terms.remove(o);
		o.setStatusEventlistener(null);
		TermMonitorData data = termStatusMap.get(o.getManufId());
		if (data == null) {
			data = new TermMonitorData();
			termStatusMap.put(o.getManufId(), data);
		}
		data.removeMonitorTerm(o);
		if (getParent() != null && getParent().getId() != null)
			((TermMonitoredOrgNode) getParent()).removeMonitoredTerm(o);
	}

	public void logTree(String prefix, Logger logger) {
		logger.debug(prefix + "-" + getOrgFullName() + "[" + getId()
				+ "][terms=" + terms.size() + "][" + getParent() + "]");
		Iterator<?> it = getChildren().iterator();
		while (it.hasNext()) {
			((TermMonitoredOrgNode) it.next()).logTree(prefix + "\t", logger);
		}
	}

	private void addTermToTerms(CachedMonitoredTerm o,
			MonitoredOrgManufTerms terms) {
		switch (o.getStatus()) {
		case DISUSE:
			terms.pauseTerms.add(o);
			break;
		case NOTACTIVE:
			terms.notActiveTerms.add(o);
			break;
		case NOTINSTALL:
			terms.notInstallTerms.add(o);
			break;
		default:
			terms.normalTerms.add(o);
			break;
		}
	}

	public void inGetMonitoredTerms(MonitoredOrgManufTerms ls, Integer manufId,
			SettlementType settlementType) {
		for (CachedMonitoredTerm o : terms) {
			if ((manufId == null || manufId.equals(o.getManufId()))
					&& (settlementType == null || settlementType.equals(o
							.getSettlementType())))
				addTermToTerms(o, ls);
		}

		// for (Object o : getChildren()) {
		// TermMonitoredOrgNode node = (TermMonitoredOrgNode) o;
		// node.inGetMonitoredTerms(ls, manufId, settlementType);
		// }
	}

	public void getMonitoredTerms(CachedMonitoredTermMap termsMap,
			MonitoredOrgManufTerms ls, Integer manufId,
			SettlementType settlementType) {
		if (getParent() == null || getParent().getId() == null) {
			Enumeration<CachedMonitoredTerm> en = termsMap.elements();
			while (en.hasMoreElements()) {
				CachedMonitoredTerm o = (CachedMonitoredTerm) en.nextElement();
				if ((manufId == null || manufId.equals(o.getManufId()))
						&& (settlementType == null || settlementType.equals(o
								.getSettlementType())))
					addTermToTerms(o, ls);
			}
		} else {
			inGetMonitoredTerms(ls, manufId, settlementType);
		}
	}

	@Override
	public IdableObject<Integer> createObject() {
		return new TermMonitoredOrgNode();
	}

	@Override
	public TermMonitoredOrgNode getGlobal(int orgId) {
		return (TermMonitoredOrgNode) super.getGlobal(orgId);
	}

	public ConcurrentHashMap<Integer, CachedTradeMonitorData> getTradeStatusMap() {
		return tradeStatusMap;
	}

	public ConcurrentHashMap<Integer, TermMonitorData> getTermStatusMap() {
		return termStatusMap;
	}

	public ConcurrentHashMap<Short, CachedInterfaceMonitorData> getInterfaceMonitorMap() {
		return interfaceMonitorMap;
	}

	public CachedTradeMonitorData getCacheTradeData(int tradeCodeId) {
		CachedTradeMonitorData data = getTradeStatusMap().get(tradeCodeId);
		if (data == null) {
			data = new CachedTradeMonitorData();
			tradeStatusMap.put(tradeCodeId, data);
		}
		return data;
	}

	public CachedInterfaceMonitorData getCacheInterfaceData(short interfaceId) {
		CachedInterfaceMonitorData data = getInterfaceMonitorMap().get(
				interfaceId);
		if (data == null) {
			data = new CachedInterfaceMonitorData();
			interfaceMonitorMap.put(interfaceId, data);
		}
		return data;
	}

	public void addCacheTradeData(int tradeCodeId, TradeMonitorDataItem day,
			TradeMonitorDataItem hour, TradeMonitorDataItem halfHour,
			TradeMonitorDataItem tenMinutes) {
		CachedTradeMonitorData data = getCacheTradeData(tradeCodeId);
		if (day != null)
			data.getDayData().addData(day);
		if (hour != null)
			data.getHourData().addData(hour);
		if (halfHour != null)
			data.getHalfHourData().addData(halfHour);
		if (tenMinutes != null)
			data.getTenMinutesData().addData(tenMinutes);
		if (getParent() != null && getParent().getId() != null) {
			((TermMonitoredOrgNode) getParent()).addCacheTradeData(tradeCodeId,
					day, hour, halfHour, tenMinutes);
		}
	}

	public void addCacheInterfaceData(short infId,
			InterfaceMonitorDataItem day, InterfaceMonitorDataItem hour,
			InterfaceMonitorDataItem halfHour,
			InterfaceMonitorDataItem tenMinutes) {
		CachedInterfaceMonitorData data = getCacheInterfaceData(infId);
		if (day != null)
			data.getDayData().addData(day);
		if (hour != null)
			data.getHourData().addData(hour);
		if (halfHour != null)
			data.getHalfHourData().addData(halfHour);
		if (tenMinutes != null)
			data.getTenMinutesData().addData(tenMinutes);
		if (getParent() != null && getParent().getId() != null) {
			((TermMonitoredOrgNode) getParent()).addCacheInterfaceData(infId,
					day, hour, halfHour, tenMinutes);
		}
	}

	private TermMonitorData getManufMonitorData(int manufId) {
		TermMonitorData d = termStatusMap.get(manufId);
		if (d == null) {
			d = new TermMonitorData();
			termStatusMap.put(manufId, d);
		}
		return d;
	}

	@Override
	public void EnabledStatusChanged(MonitoredTerm o, boolean enabled) {
		TermMonitorData d = getManufMonitorData(o.getManufId());
		if (enabled) {
			d.enabledCount++;
		} else
			d.enabledCount--;
		if (getParent() != null && getParent().getId() != null)
			((TermMonitoredOrgNode) getParent()).EnabledStatusChanged(o,
					enabled);
	}

	@Override
	public void AlarmStatusChanged(MonitoredTerm o, boolean alarmed) {
		TermMonitorData d = getManufMonitorData(o.getManufId());
		if (alarmed) {
			d.alarmCount++;
		} else
			d.alarmCount--;
		if (getParent() != null && getParent().getId() != null)
			((TermMonitoredOrgNode) getParent()).AlarmStatusChanged(o, alarmed);
	}

	private void DoOnlineStatusChanged(MonitoredTerm o, boolean isonline) {
		TermMonitorData d = getManufMonitorData(o.getManufId());
		if (isonline) {
			d.onlineCount++;
		} else
			d.onlineCount--;
		if (getParent() != null && getParent().getId() != null)
			((TermMonitoredOrgNode) getParent()).DoOnlineStatusChanged(o,
					isonline);
	}

	@Override
	public void OnlineStatusChanged(MonitoredTerm o, boolean isonline) {
		DoOnlineStatusChanged(o, isonline);
		if (isonline) {
			CachedMonitoredTermMap.addOnlineTerm((CachedMonitoredTerm) o);
		} else {
			CachedMonitoredTermMap.removeOnlineTerm((CachedMonitoredTerm) o);
		}
	}

	@Override
	public void HasTradeChanged(MonitoredTerm o, boolean hasTrade) {
		TermMonitorData d = getManufMonitorData(o.getManufId());
		if (hasTrade) {
			d.hasTradeCount++;
		} else
			d.hasTradeCount--;
		if (getParent() != null && getParent().getId() != null)
			((TermMonitoredOrgNode) getParent()).HasTradeChanged(o, hasTrade);
	}

	public synchronized void dayChanged() {
		for (TermMonitorData o : termStatusMap.values()) {
			o.setHasTradeCount(0);
		}
		tradeStatusMap.clear();
		interfaceMonitorMap.clear();
		for (TreeNode<?> o : getChildren()) {
			((TermMonitoredOrgNode) o).dayChanged();
		}
	}

	/**
	 * 更新交易监控数据
	 */
	public synchronized void resetTradeMonitorData() {
		for (CachedTradeMonitorData o : tradeStatusMap.values()) {
			o.reset(false);
		}
		for (TreeNode<?> o : getChildren()) {
			((TermMonitoredOrgNode) o).resetTradeMonitorData();
		}
	}

	public synchronized void resetInterfaceMonitorData() {
		for (CachedInterfaceMonitorData o : interfaceMonitorMap.values()) {
			o.reset(false);
		}
		for (TreeNode<?> o : getChildren()) {
			((TermMonitoredOrgNode) o).resetInterfaceMonitorData();
		}
	}

	private void updateTradeMointorData(int tradeCodeId,
			CachedTradeMonitorData o) {
		CachedTradeMonitorData d = getCacheTradeData(tradeCodeId);
		d.getHalfHourData().addData(o.getHalfHourData());
		d.getHourData().addData(o.getHourData());
		d.getTenMinutesData().addData(o.getTenMinutesData());
		if (getParent() != null && getParent().getId() != null)
			((TermMonitoredOrgNode) getParent()).updateTradeMointorData(
					tradeCodeId, o);
	}

	/**
	 * 更新交易监控数据
	 */
	public synchronized void updateTradeMonitorData(DateTime hourBefore,
			DateTime halfHourBefore, DateTime tenMinutesBefore) {
		Enumeration<Integer> en = tradeStatusMap.keys();
		while (en.hasMoreElements()) {
			int tid = en.nextElement();
			CachedTradeMonitorData o = tradeStatusMap.get(tid);
			if (o.updateMonitorData(hourBefore, halfHourBefore,
					tenMinutesBefore)) {
				if (getParent() != null && getParent().getId() != null)
					((TermMonitoredOrgNode) getParent())
							.updateTradeMointorData(tid, o);
			}
		}
		for (TreeNode<?> o : getChildren()) {
			((TermMonitoredOrgNode) o).updateTradeMonitorData(hourBefore,
					halfHourBefore, tenMinutesBefore);
		}
	}

	private void updateInterfaceMointorData(short id,
			CachedInterfaceMonitorData o) {
		CachedInterfaceMonitorData d = getCacheInterfaceData(id);
		d.getHalfHourData().addData(o.getHalfHourData());
		d.getHourData().addData(o.getHourData());
		d.getTenMinutesData().addData(o.getTenMinutesData());
		if (getParent() != null && getParent().getId() != null)
			((TermMonitoredOrgNode) getParent()).updateInterfaceMointorData(id,
					o);
	}

	public synchronized void updateIntefaceMonitorData(DateTime hourBefore,
			DateTime halfHourBefore, DateTime tenMinutesBefore) {
		Enumeration<Short> en = interfaceMonitorMap.keys();
		while (en.hasMoreElements()) {
			short tid = en.nextElement();
			CachedInterfaceMonitorData o = interfaceMonitorMap.get(tid);
			if (o.updateMonitorData(hourBefore, halfHourBefore,
					tenMinutesBefore)) {
				if (getParent() != null && getParent().getId() != null)
					((TermMonitoredOrgNode) getParent())
							.updateInterfaceMointorData(tid, o);
			}
		}
		for (TreeNode<?> o : getChildren()) {
			((TermMonitoredOrgNode) o).updateIntefaceMonitorData(hourBefore,
					halfHourBefore, tenMinutesBefore);
		}
	}

	/**
	 * 数据采集
	 * 
	 * @param now
	 * @param event
	 */
	public void dataAcquisition(DateTime now, TradeEventData event) {
		CachedTradeMonitorData o = getCacheTradeData(event.getTradeCode()
				.getTradeCodeId());
		event.setDuration((long) DateTime.milliSecondsBetween(event
				.getTradeResult().getTradeStartTime(), event.getTradeResult()
				.getTradeEndTime()));
		o.dataAcquisition(now, event);
		o.getDayData().addData(event);
		TermMonitoredOrgNode p = (TermMonitoredOrgNode) getParent();
		while (p != null && p.getId() != null) {
			p.getCacheTradeData(event.getTradeCode().getTradeCodeId())
					.getDayData().addData(event);
			p = (TermMonitoredOrgNode) p.getParent();
		}
	}

	public void refundAcquisition(DateTime now, int tradeCodeId,
			boolean isCancel, long amount, long count) {
		CachedTradeMonitorData o = getCacheTradeData(tradeCodeId);
		o.refundAcquisition(now, isCancel, amount, count);
		o.getDayData().addRefund(isCancel, count, amount);
		TermMonitoredOrgNode p = (TermMonitoredOrgNode) getParent();
		while (p != null && p.getId() != null) {
			p.getCacheTradeData(tradeCodeId).getDayData()
					.addRefund(isCancel, count, amount);
			p = (TermMonitoredOrgNode) p.getParent();
		}
	}

	public void addTradeMonitorData(int tradeCodeId, boolean success,
			long amount, long count, long duration) {
		CachedTradeMonitorData o = getCacheTradeData(tradeCodeId);
		o.getDayData().add(success, count, amount, duration);
		TermMonitoredOrgNode p = (TermMonitoredOrgNode) getParent();
		while (p != null && p.getId() != null) {
			p.getCacheTradeData(tradeCodeId).getDayData()
					.add(success, count, amount, duration);
			p = (TermMonitoredOrgNode) p.getParent();
		}
	}

	public void addInterfaceMonitorData(short interfaceId, long sucCount,
			long errorCount, long timeoutCount, long duration) {
		CachedInterfaceMonitorData o = getCacheInterfaceData(interfaceId);
		o.getDayData().add(sucCount, errorCount, timeoutCount, duration);
		TermMonitoredOrgNode p = (TermMonitoredOrgNode) getParent();
		while (p != null && p.getId() != null) {
			p.getCacheInterfaceData(interfaceId).getDayData()
					.add(sucCount, errorCount, timeoutCount, duration);
			p = (TermMonitoredOrgNode) p.getParent();
		}
	}

	public void addRefundData(int tradeCodeId, boolean isCancel, long amount,
			long count) {
		CachedTradeMonitorData o = getCacheTradeData(tradeCodeId);
		o.getDayData().addRefund(isCancel, count, amount);
		TermMonitoredOrgNode p = (TermMonitoredOrgNode) getParent();
		while (p != null && p.getId() != null) {
			p.getCacheTradeData(tradeCodeId).getDayData()
					.addRefund(isCancel, count, amount);
			p = (TermMonitoredOrgNode) p.getParent();
		}
	}

	public void dataAcquisition(DateTime now, InterfaceEventData event) {
		CachedInterfaceMonitorData o = getCacheInterfaceData(event
				.getInterfaceId());
		o.dataAcquisition(now, event);
		o.getDayData().addData(event);
		TermMonitoredOrgNode p = (TermMonitoredOrgNode) getParent();
		while (p != null && p.getId() != null) {
			p.getCacheInterfaceData(event.getInterfaceId()).getDayData()
					.addData(event);
			p = (TermMonitoredOrgNode) p.getParent();
		}
	}
}
