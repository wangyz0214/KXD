package kxd.engine.scs.monitor;

import kxd.remote.scs.beans.right.QueryedOrg;

public class OrgTermMonitorData extends QueryedOrg {
	private static final long serialVersionUID = 1L;
	public long count;
	public long hasTradeCount;
	public long enabledCount;
	public long alarmCount;
	public long onlineCount;
	public long createTime;

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

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

}
