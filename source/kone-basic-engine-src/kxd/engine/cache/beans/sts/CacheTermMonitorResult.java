package kxd.engine.cache.beans.sts;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import kxd.engine.scs.monitor.MonitoredTermResult;
import kxd.util.Streamable;
import kxd.util.stream.Stream;

public class CacheTermMonitorResult implements Streamable, Serializable {
	private static final long serialVersionUID = 9125361003929615499L;
	int allCount;
	int notInstallCount;
	int notActiveCount;
	int activedCount;
	int pauseCount;
	int normalCount;
	int alarmCount;
	int falutCount;
	int onlineCount;
	int offlineCount;
	int pages, recordCount;
	static public final int PAGE_RECORDCOUNT = 50;
	List<MonitoredTermResult> terms = new ArrayList<MonitoredTermResult>();

	public int getRecordCount() {
		return recordCount;
	}

	public void setRecordCount(int recordCount) {
		this.recordCount = recordCount;
	}

	@Override
	public void readData(Stream stream) throws IOException {
		allCount = stream.readInt(false, 3000);
		notInstallCount = stream.readInt(false, 3000);
		notActiveCount = stream.readInt(false, 3000);
		activedCount = stream.readInt(false, 3000);
		normalCount = stream.readInt(false, 3000);
		alarmCount = stream.readInt(false, 3000);
		falutCount = stream.readInt(false, 3000);
		onlineCount = stream.readInt(false, 3000);
		offlineCount = stream.readInt(false, 3000);
		pauseCount = stream.readInt(false, 3000);
		pages = stream.readInt(false, 3000);
		recordCount = stream.readInt(false, 3000);
	}

	@Override
	public void writeData(Stream stream) throws IOException {
		stream.writeInt(allCount, false, 3000);
		stream.writeInt(notInstallCount, false, 3000);
		stream.writeInt(notActiveCount, false, 3000);
		stream.writeInt(activedCount, false, 3000);
		stream.writeInt(normalCount, false, 3000);
		stream.writeInt(alarmCount, false, 3000);
		stream.writeInt(falutCount, false, 3000);
		stream.writeInt(onlineCount, false, 3000);
		stream.writeInt(offlineCount, false, 3000);
		stream.writeInt(pauseCount, false, 3000);
		stream.writeInt(pages, false, 3000);
		stream.writeInt(recordCount, false, 3000);
	}

	public int getAllCount() {
		return allCount;
	}

	public void setAllCount(int allCount) {
		this.allCount = allCount;
	}

	public int getNotInstallCount() {
		return notInstallCount;
	}

	public void setNotInstallCount(int notInstallCount) {
		this.notInstallCount = notInstallCount;
	}

	public int getNotActiveCount() {
		return notActiveCount;
	}

	public void setNotActiveCount(int notActiveCount) {
		this.notActiveCount = notActiveCount;
	}

	public int getActivedCount() {
		return activedCount;
	}

	public void setActivedCount(int activedCount) {
		this.activedCount = activedCount;
	}

	public int getNormalCount() {
		return normalCount;
	}

	public void setNormalCount(int normalCount) {
		this.normalCount = normalCount;
	}

	public int getAlarmCount() {
		return alarmCount;
	}

	public void setAlarmCount(int alarmCount) {
		this.alarmCount = alarmCount;
	}

	public int getFalutCount() {
		return falutCount;
	}

	public void setFalutCount(int falutCount) {
		this.falutCount = falutCount;
	}

	public int getOnlineCount() {
		return onlineCount;
	}

	public void setOnlineCount(int onlineCount) {
		this.onlineCount = onlineCount;
	}

	public int getOfflineCount() {
		return offlineCount;
	}

	public void setOfflineCount(int offlineCount) {
		this.offlineCount = offlineCount;
	}

	public List<MonitoredTermResult> getTerms() {
		return terms;
	}

	public int getPauseCount() {
		return pauseCount;
	}

	public void setPauseCount(int pauseCount) {
		this.pauseCount = pauseCount;
	}

	public int getPages() {
		return pages;
	}

	public void setPages(int pages) {
		this.pages = pages;
	}

}
