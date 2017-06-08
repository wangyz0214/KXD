package kxd.engine.scs.monitor;

import kxd.remote.scs.beans.right.QueryedOrg;

public class OrgInterfaceMonitorData extends QueryedOrg {
	private static final long serialVersionUID = 1L;
	private InterfaceMonitorDataItem dayData, hourData, halfHourData,
			tenMinutesData;

	public void addData(InterfaceMonitorData o) {
		dayData.addData(o.getDayData());
		hourData.addData(o.getHourData());
		halfHourData.addData(o.getHalfHourData());
		tenMinutesData.addData(o.getTenMinutesData());
	}

	public InterfaceMonitorDataItem getDayData() {
		return dayData;
	}

	public void setDayData(InterfaceMonitorDataItem dayData) {
		this.dayData = dayData;
	}

	public InterfaceMonitorDataItem getHourData() {
		return hourData;
	}

	public void setHourData(InterfaceMonitorDataItem hourData) {
		this.hourData = hourData;
	}

	public InterfaceMonitorDataItem getHalfHourData() {
		return halfHourData;
	}

	public void setHalfHourData(InterfaceMonitorDataItem halfHourData) {
		this.halfHourData = halfHourData;
	}

	public InterfaceMonitorDataItem getTenMinutesData() {
		return tenMinutesData;
	}

	public void setTenMinutesData(InterfaceMonitorDataItem tenMinutesData) {
		this.tenMinutesData = tenMinutesData;
	}

}
