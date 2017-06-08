package kxd.engine.scs.monitor;

import java.io.Serializable;

public class InterfaceMonitorData implements Serializable {
	private static final long serialVersionUID = 1L;
	private InterfaceMonitorDataItem dayData, hourData, halfHourData,
			tenMinutesData;

	public InterfaceMonitorData() {
		dayData = new InterfaceMonitorDataItem();
		hourData = new InterfaceMonitorDataItem();
		halfHourData = new InterfaceMonitorDataItem();
		tenMinutesData = new InterfaceMonitorDataItem();
	}

	public InterfaceMonitorData(InterfaceMonitorData src) {
		dayData = src.dayData;
		hourData = src.hourData;
		halfHourData = src.halfHourData;
		tenMinutesData = src.tenMinutesData;
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
