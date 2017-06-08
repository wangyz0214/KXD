package kxd.engine.scs.monitor;

import kxd.remote.scs.beans.right.QueryedOrg;

public class OrgTradeMonitorData extends QueryedOrg {
	private static final long serialVersionUID = 1L;
	private TradeMonitorDataItem dayData, hourData, halfHourData,
			tenMinutesData;

	public void addData(TradeMonitorData o) {
		dayData.addData(o.getDayData());
		hourData.addData(o.getHourData());
		halfHourData.addData(o.getHalfHourData());
		tenMinutesData.addData(o.getTenMinutesData());
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
