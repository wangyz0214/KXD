package kxd.engine.scs.monitor;

import java.util.concurrent.CopyOnWriteArrayList;

import kxd.engine.scs.trade.drivers.TradeEventData;
import kxd.util.DateTime;

public class CachedTradeMonitorData extends TradeMonitorData {
	private static final long serialVersionUID = 1L;
	private CopyOnWriteArrayList<CachedTradeMonitorDataItem> dataList = new CopyOnWriteArrayList<CachedTradeMonitorDataItem>();

	public CachedTradeMonitorData() {
	}

	public void reset(boolean all) {
		if (all)
			dataList.clear();
		getHourData().reset();
		if (all)
			getDayData().reset();
		getHalfHourData().reset();
		getTenMinutesData().reset();
	}

	/**
	 * 更新监控数据
	 */
	public boolean updateMonitorData(DateTime hourBefore,
			DateTime halfHourBefore, DateTime tenMinutesBefore) {
		getHourData().reset();
		getHalfHourData().reset();
		getTenMinutesData().reset();
		for (int i = 0; i < dataList.size(); i++) {
			CachedTradeMonitorDataItem item = dataList.get(i);
			if (item.getCreateTime().before(hourBefore)) {
				dataList.remove(item);
				i--;
			} else {
				getHourData().addData(item);
				if (item.getCreateTime().after(halfHourBefore)) {
					getHalfHourData().addData(item);
					if (item.getCreateTime().after(tenMinutesBefore))
						getTenMinutesData().addData(item);
				}
			}
		}
		return dataList.size() > 0;
	}

	/**
	 * 获取数据记录项，5分钟更新一条记录
	 * 
	 * @param now
	 * @param event
	 */
	private CachedTradeMonitorDataItem getDataItem(DateTime now) {
		CachedTradeMonitorDataItem item;
		if (dataList.size() == 0) {
			item = new CachedTradeMonitorDataItem();
			dataList.add(item);
		} else {
			item = dataList.get(dataList.size() - 1);
			if (item.getCreateTime().secondsBetween(now) >= 300) {
				item = new CachedTradeMonitorDataItem();
				dataList.add(item);
			}
		}
		return item;
	}

	/**
	 * 数据采集，5分钟更新一条记录
	 * 
	 * @param now
	 * @param event
	 */
	public void dataAcquisition(DateTime now, TradeEventData event) {
		getDataItem(now).addData(event);
	}

	public void refundAcquisition(DateTime now, boolean isCancel, long amount,
			long count) {
		getDataItem(now).addRefund(isCancel, count, amount);
	}

}
