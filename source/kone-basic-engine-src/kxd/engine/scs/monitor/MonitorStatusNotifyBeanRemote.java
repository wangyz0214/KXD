package kxd.engine.scs.monitor;

import java.util.Collection;
import java.util.List;

import javax.ejb.Remote;

import kxd.engine.scs.trade.drivers.InterfaceEventData;
import kxd.engine.scs.trade.drivers.TradeEventData;
import kxd.remote.scs.beans.device.EditedTerm;

/**
 * 监控发生变化时的事件通知
 * 
 * @author zhaom
 * 
 */
@Remote
public interface MonitorStatusNotifyBeanRemote {

	/**
	 * 提交终端的状态
	 * 
	 */
	public void updateTermStatus(List<TermStatusCommand> statusList);

	/**
	 * 有新的终端加入，更新缓存
	 * 
	 * @param termId
	 */
	public void termAdded(EditedTerm term);

	/**
	 * 终端被修改，更新缓存
	 * 
	 * @param termId
	 */
	public void termModified(EditedTerm term);

	/**
	 * 终端被移除，更新缓存
	 * 
	 * @param termId
	 */
	public void termRemoved(int termId);

	/**
	 * 当一个终端型号增加设备时，调用本方法通知监控服务器，处理该事件，修正监控的设备
	 * 
	 * @param termType
	 *            型号ID
	 * @param device
	 *            增加的设备ID
	 */
	public void termTypeDeviceAdded(int termType, int deviceId);

	/**
	 * 当一个终端型号删除设备时，调用本方法通知监控服务器，处理该事件，修正监控的设备
	 * 
	 * @param termType
	 *            型号ID
	 * @param device
	 *            删除的设备ID
	 */
	public void termTypeDeviceRemoved(int termType, int deviceId);

	/**
	 * 当终端暂停和恢复服务，调用本函数，更新缓存
	 * 
	 * @param terms
	 *            暂停或恢复服务的终端列表
	 * @param pause
	 *            true - 暂停；false - 恢复
	 */
	public void termPauseResume(Collection<Integer> terms, boolean pause);

	/**
	 * 更新终端交易状态
	 * 
	 * @param tradeEvents
	 */
	public void onTradeEvents(List<TradeEventData> tradeEvents);

	/**
	 * 接口事件
	 * 
	 * @param tradeEvents
	 */
	public void onInterfaceEvents(List<InterfaceEventData> tradeEvents);

	public void updateRefundStatus(RefundEventData o);

	public void updateCancelRefundStatus(RefundEventData o);
}
