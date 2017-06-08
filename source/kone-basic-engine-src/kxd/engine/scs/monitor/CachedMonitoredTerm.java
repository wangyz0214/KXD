package kxd.engine.scs.monitor;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.concurrent.ConcurrentHashMap;

import kxd.engine.cache.beans.sts.CachedAlarmCode;
import kxd.engine.cache.beans.sts.CachedDeviceStatus;
import kxd.engine.cache.beans.sts.CachedDeviceType;
import kxd.engine.cache.beans.sts.CachedTermType;
import kxd.engine.cache.beans.sts.CachedTermTypeDevice;
import kxd.engine.cache.beans.sts.MonitoredDeviceStatus;
import kxd.engine.helper.CacheHelper;
import kxd.engine.scs.monitor.MonitoredTerm;
import kxd.remote.scs.beans.BaseOrg;
import kxd.remote.scs.beans.device.EditedTerm;
import kxd.remote.scs.util.emun.AlarmLevel;
import kxd.remote.scs.util.emun.AlarmStatus;
import kxd.remote.scs.util.emun.TermRunStatus;
import kxd.remote.scs.util.emun.TermStatus;
import kxd.util.KeyValue;

public class CachedMonitoredTerm extends MonitoredTerm {
	private static final long serialVersionUID = 1L;
	private ConcurrentHashMap<Integer, CachedDeviceStatus> deviceStatusMap = new ConcurrentHashMap<Integer, CachedDeviceStatus>();
	private long lastUpdateTime;
	private Object data;

	public CachedMonitoredTerm() {
		super();
	}

	public CachedMonitoredTerm(Integer id, TermStatus status,
			TermRunStatus runStatus, AlarmStatus alarmStatus) {
		super(id, status, runStatus, alarmStatus);
	}

	public void copyFromEditedTerm(EditedTerm o) {
		if (o.getAlarmStatus() != null)
			setAlarmStatus(o.getAlarmStatus());
		if (o.getApp() != null)
			setAppId(o.getApp().getId());
		setId(o.getId());
		if (o.getOrg() != null)
			setOrg(new BaseOrg(o.getOrg().getId()));
		if (o.getStatus() != null)
			setStatus(o.getStatus());
		if (o.getRunStatus() != null)
			setRunStatus(o.getRunStatus());
		setTermCode(o.getTermCode());
		setTermDesp(o.getTermDesp());
		if (o.getTermType() != null)
			setTermTypeId(o.getTermType().getId());
		if (o.getTermType() != null && o.getTermType().getManuf() != null)
			setManufId(o.getTermType().getManuf().getId());
		if (o.getIp() !=null)// add  by jurstone 20120611
			setIp(o.getIp());
	}

	public ConcurrentHashMap<Integer, CachedDeviceStatus> getDeviceStatusMap() {
		return deviceStatusMap;
	}

	public void removeDevice(int deviceId) {
		deviceStatusMap.remove(deviceId);
		refresAlarmStatus();
	}

	public void clearDevices() {
		deviceStatusMap.clear();
		setAlarmStatus(AlarmStatus.NORMAL);
	}

	public void getMonitoredDeviceStatus(Collection<MonitoredDeviceStatus> c) {
		c.clear();
		Enumeration<CachedDeviceStatus> en = getDeviceStatusMap().elements();
		Hashtable<Integer, CachedTermTypeDevice> map = CacheHelper.termTypeMap
				.get(getTermTypeId()).getDeviceMap();
		while (en.hasMoreElements()) {
			CachedDeviceStatus st = en.nextElement();
			CachedTermTypeDevice device = map.get(st.getId());
			if (device != null) {
				MonitoredDeviceStatus o = new MonitoredDeviceStatus();
				o.setId(device.getDevice().getId());
				o.setStatus(st.getStatus());
				o.setPort(device.getPort());
				o.setDeviceDesp(device.getDevice().getDeviceName());
				o.setDeviceTypeDesp(device.getDevice().getDeviceType()
						.getDeviceTypeDesp());
				o.setExtConfig(device.getExtConfig());
				o.setMessage(st.getMessage());
				c.add(o);
			}
		}
		if (c.size() == map.size())
			return;
		Enumeration<Integer> en1 = map.keys();
		while (en1.hasMoreElements()) {
			int k = en1.nextElement();
			MonitoredDeviceStatus o = new MonitoredDeviceStatus(k);
			CachedTermTypeDevice device = map.get(k);
			if (!c.contains(o)) {
				o.setPort(device.getPort());
				o.setDeviceDesp(device.getDevice().getDeviceName());
				o.setDeviceTypeDesp(device.getDevice().getDeviceType()
						.getDeviceTypeDesp());
				o.setExtConfig(device.getExtConfig());
				c.add(o);
			}
		}
	}

	public long getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(long lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	private void refresAlarmStatus() {
		CachedTermType termType = CacheHelper.termTypeMap.get(getTermTypeId());
		Enumeration<CachedDeviceStatus> en = deviceStatusMap.elements();
		AlarmLevel level = AlarmLevel.NORMAL;
		while (en.hasMoreElements()) {
			CachedDeviceStatus st = en.nextElement();
			CachedTermTypeDevice device = termType.getDeviceMap().get(
					st.getId());
			if (device == null)
				continue;
			CachedDeviceType deviceType = device.getDevice().getDeviceType();
			KeyValue<CachedAlarmCode, AlarmLevel> l = deviceType
					.getAlarmLevel(st.getStatus());
			if (l.getValue().getValue() > level.getValue()) {
				level = l.getValue();
				if (level.equals(AlarmLevel.FAULT))
					break;
			}
		}
		setAlarmStatus(AlarmStatus.valueOf(level.getValue()));
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
}
