package kxd.engine.scs.monitor;

import java.util.Collection;

import javax.ejb.Remote;

import kxd.engine.cache.beans.sts.CacheTermMonitorResult;
import kxd.engine.cache.beans.sts.CachedDeviceStatus;
import kxd.remote.scs.beans.BaseMonitorData;
import kxd.remote.scs.util.emun.SettlementType;
import kxd.util.KeyValue;

/**
 * 
 * @author 赵明
 */
@Remote
public interface MonitorServiceBeanRemote extends MonitorStatusNotifyBeanRemote {
	public BaseMonitorData getBasicMonitorData();

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
	public KeyValue<BaseMonitorData, CacheTermMonitorResult> getMonitorTermList(
			Integer orgId, Integer manufId, boolean includeChildren,
			int termStatus, int alarmStatus, int onlineStatus,
			SettlementType settlementType, int page,
			boolean includeDeviceStatusList, String filter);

	/**
	 * 获取监控终端的详细状态
	 * 
	 * @param termId
	 *            终端ID
	 * @return 设备状态列表
	 */
	public KeyValue<BaseMonitorData, Collection<CachedDeviceStatus>> getMonitoredDeviceStatus(
			int termId);

	/**
	 * 获取地图交易监控信息
	 * 
	 * @param dao
	 * @param orgId
	 * @param payItem
	 * @param payWay
	 * @param businessCategory
	 * @return
	 */
	public KeyValue<BaseMonitorData, Collection<OrgTradeMonitorDataMap>> getMapMonitorTradeList(
			Integer orgId, boolean includeSelf);

	/**
	 * 获取地图终端监控信息
	 * 
	 * @param dao
	 * @param orgId
	 * @param payItem
	 * @param payWay
	 * @param businessCategory
	 * @return
	 */
	public KeyValue<BaseMonitorData, Collection<OrgTermMonitorDataMap>> getMapMonitorTermList(
			Integer orgId, boolean includeSelf);

	public KeyValue<BaseMonitorData, Collection<OrgInterfaceMonitorDataMap>> getMonitorInterfaceList();

}
