package kxd.scs.beans.cacheservices.monitor;

import java.util.concurrent.CopyOnWriteArrayList;

public class MonitoredTerms {
	private static final long serialVersionUID = 1L;
	CopyOnWriteArrayList<CachedMonitoredTerm> terms = new CopyOnWriteArrayList<CachedMonitoredTerm>();
	private long createTime = System.currentTimeMillis();

	public MonitoredTerms() {
		super();
	}

	public CopyOnWriteArrayList<CachedMonitoredTerm> getTerms() {
		return terms;
	}

	public long getCreateTime() {
		return createTime;
	}

	int allCount;
	int notInstallCount;
	int notActiveCount;
	int activedCount;
	int pauseCount;
	int normalCount;
	int alarmCount;
	int falutCount;
	int onlineCount;
	int offlineCount;

	public int getNormalCount() {
		return normalCount;
	}

	public void setNormalCount(int normalCount) {
		this.normalCount = normalCount;
	}

	public int getAlarmCount() {
		return alarmCount;
	}

	public void setAlarmCount(int alarmCount) {
		this.alarmCount = alarmCount;
	}

	public int getFalutCount() {
		return falutCount;
	}

	public void setFalutCount(int falutCount) {
		this.falutCount = falutCount;
	}

	public int getOnlineCount() {
		return onlineCount;
	}

	public void setOnlineCount(int onlineCount) {
		this.onlineCount = onlineCount;
	}

	public int getOfflineCount() {
		return offlineCount;
	}

	public void setOfflineCount(int offlineCount) {
		this.offlineCount = offlineCount;
	}

	public int getAllCount() {
		return allCount;
	}

	public int getNotInstallCount() {
		return notInstallCount;
	}

	public int getNotActiveCount() {
		return notActiveCount;
	}

	public int getActivedCount() {
		return activedCount;
	}

	public int getPauseCount() {
		return pauseCount;
	}

	public void setAllCount(int allCount) {
		this.allCount = allCount;
	}

	public void setNotInstallCount(int notInstallCount) {
		this.notInstallCount = notInstallCount;
	}

	public void setNotActiveCount(int notActiveCount) {
		this.notActiveCount = notActiveCount;
	}

	public void setActivedCount(int activedCount) {
		this.activedCount = activedCount;
	}

	public void setPauseCount(int pauseCount) {
		this.pauseCount = pauseCount;
	}

}
