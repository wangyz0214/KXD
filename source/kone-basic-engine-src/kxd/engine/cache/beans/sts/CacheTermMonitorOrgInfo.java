package kxd.engine.cache.beans.sts;

import kxd.remote.scs.beans.right.QueryedOrg;

public class CacheTermMonitorOrgInfo extends QueryedOrg {
	private static final long serialVersionUID = 9125361003929615499L;
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
	String orgCode;
	int recordCount;
	int tradeCount, noTradeCount;

	public int getAllCount() {
		return allCount;
	}

	public void setAllCount(int allCount) {
		this.allCount = allCount;
	}

	public int getNotInstallCount() {
		return notInstallCount;
	}

	public void setNotInstallCount(int notInstallCount) {
		this.notInstallCount = notInstallCount;
	}

	public int getNotActiveCount() {
		return notActiveCount;
	}

	public void setNotActiveCount(int notActiveCount) {
		this.notActiveCount = notActiveCount;
	}

	public int getActivedCount() {
		return activedCount;
	}

	public void setActivedCount(int activedCount) {
		this.activedCount = activedCount;
	}

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

	public int getPauseCount() {
		return pauseCount;
	}

	public void setPauseCount(int pauseCount) {
		this.pauseCount = pauseCount;
	}

	public String getOrgCode() {
		return orgCode;
	}

	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}

	public int getRecordCount() {
		return recordCount;
	}

	public void setRecordCount(int recordCount) {
		this.recordCount = recordCount;
	}

	public int getTradeCount() {
		return tradeCount;
	}

	public void setTradeCount(int tradeCount) {
		this.tradeCount = tradeCount;
	}

	public int getNoTradeCount() {
		return noTradeCount;
	}

	public void setNoTradeCount(int noTradeCount) {
		this.noTradeCount = noTradeCount;
	}

}
