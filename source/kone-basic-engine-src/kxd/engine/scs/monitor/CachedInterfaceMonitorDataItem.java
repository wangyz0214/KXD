package kxd.engine.scs.monitor;

import kxd.util.DateTime;

public class CachedInterfaceMonitorDataItem extends InterfaceMonitorDataItem {
	private static final long serialVersionUID = 1L;
	private DateTime createTime = new DateTime();

	public CachedInterfaceMonitorDataItem() {

	}

	public DateTime getCreateTime() {
		return createTime;
	}

}
