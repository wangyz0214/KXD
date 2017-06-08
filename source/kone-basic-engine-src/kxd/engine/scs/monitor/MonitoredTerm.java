package kxd.engine.scs.monitor;

import java.util.Date;

import kxd.remote.scs.beans.BaseOrg;
import kxd.remote.scs.beans.BaseTerm;
import kxd.remote.scs.util.emun.AlarmStatus;
import kxd.remote.scs.util.emun.FaultProcFlag;
import kxd.remote.scs.util.emun.SettlementType;
import kxd.remote.scs.util.emun.TermRunStatus;
import kxd.remote.scs.util.emun.TermStatus;
import kxd.util.DateTime;
import kxd.util.IdableObject;

import org.apache.log4j.Logger;

public class MonitoredTerm extends BaseTerm {
	private static final long serialVersionUID = 1L;
	private int termTypeId;
	private int appId;
	private BaseOrg org;
	private int manufId;
	private TermStatus status;
	private TermRunStatus runStatus;
	private AlarmStatus alarmStatus;
	private Date lastOnlineTime, lastTradeTime;
	private int busyTimes, idleTimes;
	private FaultProcFlag processFlag;
	private Date lastFaultTime, lastProcessTime, lastFaultRecordTime;
	private Boolean online;
	private SettlementType settlementType;
	private StatusEventListener statusEventlistener;
	private String ip;

	@Override
	protected void loggingContent(Logger logger, String prefix) {
		super.loggingContent(logger, prefix);
	}

	public MonitoredTerm() {
		super();
	}

	public MonitoredTerm(Integer id, TermStatus status,
			TermRunStatus runStatus, AlarmStatus alarmStatus) {
		super(id);
		this.status = status;
		this.runStatus = runStatus;
		this.alarmStatus = alarmStatus;
	}

	public StatusEventListener getStatusEventlistener() {
		return statusEventlistener;
	}

	public void setStatusEventlistener(StatusEventListener statusEventlistener) {
		this.statusEventlistener = statusEventlistener;
	}

	@Override
	public void copyData(Object src) {
		super.copyData(src);
		if (!(src instanceof MonitoredTerm))
			return;
		MonitoredTerm d = (MonitoredTerm) src;
		termTypeId = d.termTypeId;
		org = d.org;
		status = d.status;
		appId = d.appId;
		manufId = d.manufId;
		runStatus = d.runStatus;
		lastOnlineTime = d.lastOnlineTime;
		alarmStatus = d.alarmStatus;
		busyTimes = d.busyTimes;
		idleTimes = d.idleTimes;
		processFlag = d.processFlag;
		lastFaultRecordTime = d.lastFaultRecordTime;
		lastFaultTime = d.lastFaultTime;
		lastProcessTime = d.lastProcessTime;
		lastTradeTime = d.lastTradeTime;
		online = d.online;
		settlementType = d.settlementType;
	}

	@Override
	public IdableObject<Integer> createObject() {
		return new MonitoredTerm();
	}

	public TermStatus getStatus() {
		return status;
	}

	public void setStatus(TermStatus status) {
		boolean oldEnabled = !TermStatus.NORMAL.equals(this.status);
		boolean newEnabled = !TermStatus.NORMAL.equals(status);
		if (oldEnabled != newEnabled) {
			if (statusEventlistener != null)
				statusEventlistener.EnabledStatusChanged(this, newEnabled);
		}
		this.status = status;
	}

	public TermRunStatus getRunStatus() {
		return runStatus;
	}

	public void setRunStatus(TermRunStatus runStatus) {
		this.runStatus = runStatus;
	}

	public AlarmStatus getAlarmStatus() {
		return alarmStatus;
	}

	public void setAlarmStatus(AlarmStatus alarmStatus) {
		boolean oldAlarm = !AlarmStatus.NORMAL.equals(this.alarmStatus);
		boolean newAlarm = !AlarmStatus.NORMAL.equals(alarmStatus);
		if (oldAlarm != newAlarm) {
			if (statusEventlistener != null)
				statusEventlistener.AlarmStatusChanged(this, newAlarm);
		}
		this.alarmStatus = alarmStatus;
	}

	public Date getLastOnlineTime() {
		return lastOnlineTime;
	}

	public void setLastOnlineTime(Date lastOnlineTime) {
		this.lastOnlineTime = lastOnlineTime;
		boolean b = getOnlineStatus() == 1;
		if (online == null)
			online = b;
		else {
			if (!online.equals(b)) {
				if (statusEventlistener != null)
					statusEventlistener.OnlineStatusChanged(this, b);
				online = b;
			}
		}
	}

	public Boolean checkOnline() {
		if (Boolean.TRUE.equals(online)) {
			if (getOnlineStatus() != 1) { // 在线变离线
				online = false;
				if (statusEventlistener != null)
					statusEventlistener.OnlineStatusChanged(this, false);
			}
		}
		return online;
	}

	public Boolean isOnline() {
		return online;
	}

	private int getOnlineStatus(long curTime) {
		if (getLastOnlineTime() == null || getLastOnlineTime().getTime() == 0)
			return -1;
		return DateTime.minutesBetween(curTime, getLastOnlineTime().getTime()) < 10 ? 1
				: 0;
	}

	/**
	 * 获取在线状态
	 * 
	 * @param curTime
	 *            当前时间
	 * @return 0 - 离线；1 - 在线；-1 - 尚未注册激活
	 */
	public int getOnlineStatus() {
		return getOnlineStatus(System.currentTimeMillis());
	}

	public int getBusyTimes() {
		return busyTimes;
	}

	public void setBusyTimes(int busyTimes) {
		this.busyTimes = busyTimes;
	}

	public int getIdleTimes() {
		return idleTimes;
	}

	public void setIdleTimes(int idleTimes) {
		this.idleTimes = idleTimes;
	}

	public FaultProcFlag getProcessFlag() {
		if (processFlag == null) {
			processFlag = FaultProcFlag.UNPROCESS;
		}
		return processFlag;
	}

	public void setProcessFlag(FaultProcFlag processFlag) {
		this.processFlag = processFlag;
	}

	public Date getLastFaultTime() {
		return lastFaultTime;
	}

	public void setLastFaultTime(Date lastFaultTime) {
		this.lastFaultTime = lastFaultTime;
	}

	public Date getLastProcessTime() {
		return lastProcessTime;
	}

	public void setLastProcessTime(Date lastProcessTime) {
		this.lastProcessTime = lastProcessTime;
	}

	public Date getLastFaultRecordTime() {
		return lastFaultRecordTime;
	}

	public void setLastFaultRecordTime(Date lastFaultRecordTime) {
		this.lastFaultRecordTime = lastFaultRecordTime;
	}

	public int getTermTypeId() {
		return termTypeId;
	}

	public void setTermTypeId(int termTypeId) {
		this.termTypeId = termTypeId;
	}

	public int getAppId() {
		return appId;
	}

	public void setAppId(int appId) {
		this.appId = appId;
	}

	public BaseOrg getOrg() {
		return org;
	}

	public void setOrg(BaseOrg org) {
		this.org = org;
	}

	public int getManufId() {
		return manufId;
	}

	public void setManufId(int manufId) {
		this.manufId = manufId;
	}

	public Date getLastTradeTime() {
		return lastTradeTime;
	}

	public void setLastTradeTime(Date lastTradeTime) {
		boolean toDayTraded = new DateTime(lastTradeTime)
				.isSameDay(new DateTime());
		if (this.toDayTraded != toDayTraded) {
			if (statusEventlistener != null)
				statusEventlistener.HasTradeChanged(this, toDayTraded);
			this.toDayTraded = toDayTraded;
		}
		this.lastTradeTime = lastTradeTime;
	}

	private boolean toDayTraded;

	public boolean isToDayTraded() {
		return toDayTraded;
	}

	public void setToDayTraded(boolean toDayTraded) {
		this.toDayTraded = toDayTraded;
	}

	public SettlementType getSettlementType() {
		return settlementType;
	}

	public void setSettlementType(SettlementType settlementType) {
		this.settlementType = settlementType;
	}
	
	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

}
