package kxd.engine.scs.monitor;

import java.util.Collection;

import kxd.engine.cache.beans.sts.MonitoredDeviceStatus;
import kxd.remote.scs.util.emun.AlarmStatus;
import kxd.remote.scs.util.emun.TermRunStatus;
import kxd.remote.scs.util.emun.TermStatus;
import kxd.util.IdableObject;

public class MonitoredTermResult extends MonitoredTerm {
	private static final long serialVersionUID = 1L;
	Collection<MonitoredDeviceStatus> deviceStatusList;

	public MonitoredTermResult() {
		super();
	}

	public MonitoredTermResult(Integer id, TermStatus status,
			TermRunStatus runStatus, AlarmStatus alarmStatus) {
		super(id, status, runStatus, alarmStatus);
	}

	@Override
	public void copyData(Object src) {
		super.copyData(src);
		if (!(src instanceof MonitoredTermResult))
			return;
		MonitoredTermResult d = (MonitoredTermResult) src;
		deviceStatusList = d.deviceStatusList;
	}

	@Override
	public IdableObject<Integer> createObject() {
		return new MonitoredTermResult();
	}

	public Collection<MonitoredDeviceStatus> getDeviceStatusList() {
		return deviceStatusList;
	}

	public void setDeviceStatusList(
			Collection<MonitoredDeviceStatus> deviceStatusList) {
		this.deviceStatusList = deviceStatusList;
	}

}
