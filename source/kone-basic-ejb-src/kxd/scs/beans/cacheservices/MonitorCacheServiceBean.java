package kxd.scs.beans.cacheservices;

import java.util.Collection;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import kxd.engine.cache.beans.sts.CacheTermMonitorResult;
import kxd.engine.cache.beans.sts.CachedDeviceStatus;
import kxd.engine.dao.BaseBean;
import kxd.engine.scs.monitor.MonitorServiceBeanRemote;
import kxd.engine.scs.monitor.OrgInterfaceMonitorDataMap;
import kxd.engine.scs.monitor.OrgTermMonitorDataMap;
import kxd.engine.scs.monitor.OrgTradeMonitorDataMap;
import kxd.engine.scs.monitor.RefundEventData;
import kxd.engine.scs.monitor.TermStatusCommand;
import kxd.engine.scs.monitor.TermSubmittedStatus;
import kxd.engine.scs.trade.drivers.InterfaceEventData;
import kxd.engine.scs.trade.drivers.TradeEventData;
import kxd.remote.scs.beans.BaseMonitorData;
import kxd.remote.scs.beans.device.EditedTerm;
import kxd.remote.scs.util.emun.SettlementType;
import kxd.scs.dao.cache.CacheMonitorDao;
import kxd.util.KeyValue;

@Stateless(name = "kxd-ejb-monitorServiceBean", mappedName = "kxd-ejb-monitorServiceBean")
public class MonitorCacheServiceBean extends BaseBean implements
		MonitorServiceBeanRemote {

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public void termAdded(EditedTerm term) {
		CacheMonitorDao.termAdded(getDao(), term);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public void termModified(EditedTerm term) {
		CacheMonitorDao.termModified(getDao(), term);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public void termRemoved(int termId) {
		CacheMonitorDao.termRemoved(getDao(), termId);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public void termTypeDeviceAdded(int termType, int deviceId) {
		CacheMonitorDao.termTypeDeviceAdded(getDao(), termType, deviceId);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public void termTypeDeviceRemoved(int termType, int deviceId) {
		CacheMonitorDao.termTypeDeviceRemoved(getDao(), termType, deviceId);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public KeyValue<BaseMonitorData, CacheTermMonitorResult> getMonitorTermList(
			Integer orgId, Integer manufId, boolean includeChildren,
			int termStatus, int alarmStatus, int onlineStatus,
			SettlementType settlementType, int page,
			boolean includeDeviceStatusList, String filter) {
		return CacheMonitorDao.getMonitorTermList(getDao(), orgId, manufId,
				includeChildren, termStatus, alarmStatus, onlineStatus,
				settlementType, page, includeDeviceStatusList, filter);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public KeyValue<BaseMonitorData, Collection<CachedDeviceStatus>> getMonitoredDeviceStatus(
			int termId) {
		return CacheMonitorDao.getMonitorTermDeviceStatusList(getDao(), termId);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public void termPauseResume(Collection<Integer> terms, boolean pause) {
		CacheMonitorDao.termPauseResume(getDao(), terms, pause);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public void updateTermStatus(List<TermStatusCommand> statusList) {
		for (TermStatusCommand c : statusList) {
			try {
				switch (c.getCommand()) {
				case 0: {
					TermSubmittedStatus st = (TermSubmittedStatus) c.getData();
					CacheMonitorDao.updateTermStatus(getDao(), st.getTermId(),
							st.isPause(), st.getBusyTimes(), st.getIdleTimes(),
							st.getStatus());
				}
					break;
				case 2:
					CacheMonitorDao
							.termActived(getDao(), (Integer) c.getData());
					break;
				case 1:
					CacheMonitorDao
							.termLogined(getDao(), (Integer) c.getData());
					break;
				}
			} catch (Throwable e) {

			}
		}
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public void onTradeEvents(List<TradeEventData> tradeEvents) {
		CacheMonitorDao.onTradeEvents(getDao(), tradeEvents);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public BaseMonitorData getBasicMonitorData() {
		return CacheMonitorDao.getBasicMonitorData();
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public KeyValue<BaseMonitorData, Collection<OrgTradeMonitorDataMap>> getMapMonitorTradeList(
			Integer orgId, boolean includeSelf) {
		KeyValue<BaseMonitorData, Collection<OrgTradeMonitorDataMap>> r = CacheMonitorDao
				.getMapMonitorTradeList(getDao(), orgId, includeSelf);
		return r;
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public KeyValue<BaseMonitorData, Collection<OrgTermMonitorDataMap>> getMapMonitorTermList(
			Integer orgId, boolean includeSelf) {
		KeyValue<BaseMonitorData, Collection<OrgTermMonitorDataMap>> r = CacheMonitorDao
				.getMapMonitorTermList(getDao(), orgId, includeSelf);
		return r;
	}

	@Override
	public void updateRefundStatus(RefundEventData o) {
		CacheMonitorDao.updateRefundStatus(getDao(), o);
	}

	@Override
	public void updateCancelRefundStatus(RefundEventData o) {
		CacheMonitorDao.updateCancelRefundStatus(getDao(), o);
	}

	@Override
	public KeyValue<BaseMonitorData, Collection<OrgInterfaceMonitorDataMap>> getMonitorInterfaceList() {
		return CacheMonitorDao.getMonitorInterfaceList(getDao());
	}

	@Override
	public void onInterfaceEvents(List<InterfaceEventData> events) {
		CacheMonitorDao.onInterfaceEvents(getDao(), events);
	}

}
