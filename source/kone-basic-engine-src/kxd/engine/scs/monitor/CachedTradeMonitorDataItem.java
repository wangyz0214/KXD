package kxd.engine.scs.monitor;

import kxd.util.DateTime;

public class CachedTradeMonitorDataItem extends TradeMonitorDataItem {
	private static final long serialVersionUID = 1L;
	private DateTime createTime = new DateTime();

	public CachedTradeMonitorDataItem() {

	}

	public DateTime getCreateTime() {
		return createTime;
	}

}
