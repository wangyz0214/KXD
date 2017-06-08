package kxd.engine.scs.monitor;

/**
 * 终端状态改变事件
 * 
 * @author zhaom
 * 
 */
public interface StatusEventListener {
	public void EnabledStatusChanged(MonitoredTerm o, boolean enabled);

	public void AlarmStatusChanged(MonitoredTerm o, boolean alarmed);

	public void OnlineStatusChanged(MonitoredTerm o, boolean online);

	public void HasTradeChanged(MonitoredTerm o, boolean hasTrade);
}
