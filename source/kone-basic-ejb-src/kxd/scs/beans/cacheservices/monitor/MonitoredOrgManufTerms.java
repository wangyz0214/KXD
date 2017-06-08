package kxd.scs.beans.cacheservices.monitor;

import java.util.concurrent.CopyOnWriteArrayList;

public class MonitoredOrgManufTerms {
	private static final long serialVersionUID = 1L;
	CopyOnWriteArrayList<CachedMonitoredTerm> notInstallTerms = new CopyOnWriteArrayList<CachedMonitoredTerm>();
	CopyOnWriteArrayList<CachedMonitoredTerm> notActiveTerms = new CopyOnWriteArrayList<CachedMonitoredTerm>();
	CopyOnWriteArrayList<CachedMonitoredTerm> normalTerms = new CopyOnWriteArrayList<CachedMonitoredTerm>();
	CopyOnWriteArrayList<CachedMonitoredTerm> pauseTerms = new CopyOnWriteArrayList<CachedMonitoredTerm>();
	private long createTime = System.currentTimeMillis();

	public MonitoredOrgManufTerms() {
		super();
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public long getCreateTime() {
		return createTime;
	}

	public CopyOnWriteArrayList<CachedMonitoredTerm> getNotInstallTerms() {
		return notInstallTerms;
	}

	public CopyOnWriteArrayList<CachedMonitoredTerm> getNotActiveTerms() {
		return notActiveTerms;
	}

	public CopyOnWriteArrayList<CachedMonitoredTerm> getNormalTerms() {
		return normalTerms;
	}

	public CopyOnWriteArrayList<CachedMonitoredTerm> getPauseTerms() {
		return pauseTerms;
	}

}
