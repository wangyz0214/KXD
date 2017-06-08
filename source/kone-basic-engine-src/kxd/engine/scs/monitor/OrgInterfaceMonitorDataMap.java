package kxd.engine.scs.monitor;

import java.util.HashMap;

import kxd.remote.scs.beans.right.QueryedOrg;

public class OrgInterfaceMonitorDataMap extends
		HashMap<Short, InterfaceMonitorData> {
	private static final long serialVersionUID = 1L;
	private long createTime = System.currentTimeMillis();
	private QueryedOrg org;

	public long getCreateTime() {
		return createTime;
	}

	public InterfaceMonitorData getIfNotExistsAdd(Short k) {
		InterfaceMonitorData d = get(k);
		if (d == null) {
			d = new InterfaceMonitorData();
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
