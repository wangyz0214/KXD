package kxd.engine.scs.monitor;

import java.util.HashMap;

import kxd.remote.scs.beans.right.QueryedOrg;

public class OrgTradeMonitorDataMap extends HashMap<Integer, TradeMonitorData> {
	private static final long serialVersionUID = 1L;
	private long createTime = System.currentTimeMillis();
	private QueryedOrg org;

	public long getCreateTime() {
		return createTime;
	}

	public TradeMonitorData getIfNotExistsAdd(Integer k) {
		TradeMonitorData d = get(k);
		if (d == null) {
			d = new TradeMonitorData();
			put(k, d);
		}
		return d;
	}

	public QueryedOrg getOrg() {
		return org;
	}

	public void setOrg(QueryedOrg org) {
		this.org = org;
	}
}
