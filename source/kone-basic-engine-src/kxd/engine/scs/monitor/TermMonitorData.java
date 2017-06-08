package kxd.engine.scs.monitor;

import java.io.Serializable;

import kxd.remote.scs.util.emun.AlarmStatus;
import kxd.remote.scs.util.emun.TermStatus;

public class TermMonitorData implements Serializable {
	private static final long serialVersionUID = 1L;
	public long count;
	public long hasTradeCount;
	public long enabledCount;
	public long alarmCount;
	public long onlineCount;

	public void addMonitorTerm(CachedMonitoredTerm o) {
		count++;
		if (o.isToDayTraded())
			hasTradeCount++;
		if (Boolean.TRUE.equals(o.isOnline()))
			onlineCount++;
		if (o.getStatus().equals(TermStatus.NORMAL))
			enabledCount++;
		if (!o.getAlarmStatus().equals(AlarmStatus.NORMAL))
			alarmCount++;
	}

	public void removeMonitorTerm(CachedMonitoredTerm o) {
		count--;
		if (o.isToDayTraded())
			hasTradeCount--;
		if (Boolean.TRUE.equals(o.isOnline()))
			onlineCount--;
		if (o.getStatus().equals(TermStatus.NORMAL))
			enabledCount--;
		if (!o.getAlarmStatus().equals(AlarmStatus.NORMAL))
			alarmCount--;
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	public long getHasTradeCount() {
		return hasTradeCount;
	}

	public void setHasTradeCount(long hasTradeCount) {
		this.hasTradeCount = hasTradeCount;
	}

	public long getEnabledCount() {
		return enabledCount;
	}

	public void setEnabledCount(long enabledCount) {
		this.enabledCount = enabledCount;
	}

	public long getAlarmCount() {
		return alarmCount;
	}

	public void setAlarmCount(long alarmCount) {
		this.alarmCount = alarmCount;
	}

	public long getOnlineCount() {
		return onlineCount;
	}

	public void setOnlineCount(long onlineCount) {
		this.onlineCount = onlineCount;
	}

	public void addDataTo(OrgTermMonitorData o) {
		o.setCount(o.getCount() + count);
		o.setAlarmCount(o.getAlarmCount() + alarmCount);
		o.setEnabledCount(o.getEnabledCount() + enabledCount);
		o.setHasTradeCount(o.getHasTradeCount() + hasTradeCount);
		o.setOnlineCount(o.getOnlineCount() + onlineCount);
	}

	public void addDataTo(TermMonitorData o) {
		o.setCount(o.getCount() + count);
		o.setAlarmCount(o.getAlarmCount() + alarmCount);
		o.setEnabledCount(o.getEnabledCount() + enabledCount);
		o.setHasTradeCount(o.getHasTradeCount() + hasTradeCount);
		o.setOnlineCount(o.getOnlineCount() + onlineCount);
	}

}
