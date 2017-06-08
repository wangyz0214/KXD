package kxd.engine.scs.monitor;

import java.io.Serializable;

public class TradeMonitorData implements Serializable {
	private static final long serialVersionUID = 1L;
	private TradeMonitorDataItem dayData, hourData, halfHourData,
			tenMinutesData;

	public TradeMonitorData() {
		dayData = new TradeMonitorDataItem();
		hourData = new TradeMonitorDataItem();
		halfHourData = new TradeMonitorDataItem();
		tenMinutesData = new TradeMonitorDataItem();
	}

	public TradeMonitorData(TradeMonitorData src) {
		dayData = src.dayData;
		hourData = src.hourData;
		halfHourData = src.halfHourData;
		tenMinutesData = src.tenMinutesData;
	}

	public TradeMonitorDataItem getDayData() {
		return dayData;
	}

	public void setDayData(TradeMonitorDataItem dayData) {
		this.dayData = dayData;
	}

	public TradeMonitorDataItem getHourData() {
		return hourData;
	}

	public void setHourData(TradeMonitorDataItem hourData) {
		this.hourData = hourData;
	}

	public TradeMonitorDataItem getHalfHourData() {
		return halfHourData;
	}

	public void setHalfHourData(TradeMonitorDataItem halfHourData) {
		this.halfHourData = halfHourData;
	}

	public TradeMonitorDataItem getTenMinutesData() {
		return tenMinutesData;
	}

	public void setTenMinutesData(TradeMonitorDataItem tenMinutesData) {
		this.tenMinutesData = tenMinutesData;
	}

}
