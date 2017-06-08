package kxd.engine.scs.monitor;

import java.util.concurrent.CopyOnWriteArrayList;

import kxd.engine.scs.trade.drivers.InterfaceEventData;
import kxd.util.DateTime;

public class CachedInterfaceMonitorData extends InterfaceMonitorData {
	private static final long serialVersionUID = 1L;
	private CopyOnWriteArrayList<CachedInterfaceMonitorDataItem> dataList = new CopyOnWriteArrayList<CachedInterfaceMonitorDataItem>();

	public CachedInterfaceMonitorData() {
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
			CachedInterfaceMonitorDataItem item = dataList.get(i);
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
	 * 获取数据记录项，1分钟更新一条记录
	 * 
	 * @param now
	 * @param event
	 */
	private CachedInterfaceMonitorDataItem getDataItem(DateTime now) {
		CachedInterfaceMonitorDataItem item;
		if (dataList.size() == 0) {
			item = new CachedInterfaceMonitorDataItem();
			dataList.add(item);
		} else {
			item = dataList.get(dataList.size() - 1);
			if (item.getCreateTime().secondsBetween(now) >= 60) {
				item = new CachedInterfaceMonitorDataItem();
				dataList.add(item);
			}
		}
		return item;
	}

	/**
	 * 数据采集，1分钟更新一条记录
	 * 
	 * @param now
	 * @param event
	 */
	public void dataAcquisition(DateTime now, InterfaceEventData event) {
		getDataItem(now).addData(event);
	}

}
