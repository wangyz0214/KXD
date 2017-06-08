package kxd.engine.scs.monitor;

public interface TermStatusEventListener {
	/**
	 * 终端状态改变
	 * 
	 * @param termId
	 *            终端ID
	 * @param status
	 *            设备状态列表
	 */
	public void statusChange(CachedMonitoredTerm term);

	/**
	 * 有新的终端加入
	 * 
	 * @param termId
	 */
	public void termAdded(CachedMonitoredTerm term);

	/**
	 * 终端被修改
	 * 
	 * @param termId
	 */
	public void termModified(CachedMonitoredTerm term, int oldParentOrgId);

	/**
	 * 终端被移除
	 * 
	 * @param termId
	 */
	public void termRemoved(CachedMonitoredTerm term);

	/**
	 * 当一个终端型号增加设备时，调用本方法通知监控服务器，处理该事件，修正监控的设备
	 * 
	 * @param termType
	 *            型号ID
	 * @param device
	 *            增加的设备ID
	 */
	public void termDeviceAdded(CachedMonitoredTerm term, int deviceId);

	/**
	 * 当一个终端型号删除设备时，调用本方法通知监控服务器，处理该事件，修正监控的设备
	 * 
	 * @param termType
	 *            型号ID
	 * @param device
	 *            删除的设备ID
	 */
	public void termDeviceRemoved(CachedMonitoredTerm termType, int deviceId);

	/**
	 * 终端登录事件，登记终端最后在线时间
	 * 
	 * @param termId
	 */
	public void termLogined(CachedMonitoredTerm term);

	/**
	 * 当终端激活时，调用本函数，更新缓存
	 * 
	 * @param termId
	 *            激活的终端ID
	 */
	public void termActived(CachedMonitoredTerm term);

	/**
	 * 当终端暂停和恢复服务，调用本函数，更新缓存
	 * 
	 * @param terms
	 *            暂停或恢复服务的终端列表
	 * @param pause
	 *            true - 暂停；false - 恢复
	 */
	public void termPauseResume(CachedMonitoredTerm term, boolean pause);

}
