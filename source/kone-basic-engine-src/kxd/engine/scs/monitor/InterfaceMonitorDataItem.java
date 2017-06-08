package kxd.engine.scs.monitor;

import java.io.Serializable;

import kxd.engine.scs.trade.drivers.InterfaceEventData;

public class InterfaceMonitorDataItem implements Serializable {
	private static final long serialVersionUID = 1L;
	public long sucCount;
	public long errorCount;
	public long timeoutCount;
	public long duration;

	public InterfaceMonitorDataItem() {

	}

	public long getSucCount() {
		return sucCount;
	}

	public void setSucCount(long sucCount) {
		this.sucCount = sucCount;
	}

	public long getErrorCount() {
		return errorCount;
	}

	public void setErrorCount(long errorCount) {
		this.errorCount = errorCount;
	}

	public long getTimeoutCount() {
		return timeoutCount;
	}

	public void setTimeoutCount(long timeoutCount) {
		this.timeoutCount = timeoutCount;
	}

	public InterfaceMonitorDataItem(Object o[], int startIndex) {
		sucCount = Long.valueOf(o[startIndex++].toString());
		errorCount = Long.valueOf(o[startIndex++].toString());
		timeoutCount = Long.valueOf(o[startIndex++].toString());
		duration = Long.valueOf(o[startIndex++].toString());
	}

	public void add(long sucCount, long errCount, long timeoutCount,
			long duration) {
		this.sucCount += sucCount;
		this.errorCount += errCount;
		this.duration += duration;
		this.timeoutCount += timeoutCount;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public void addData(InterfaceMonitorDataItem o) {
		setErrorCount(errorCount + o.getErrorCount());
		setSucCount(sucCount + o.getSucCount());
		setTimeoutCount(timeoutCount + o.getTimeoutCount());
		setDuration(duration + o.getDuration());
	}

	public void addData(InterfaceEventData o) {
		switch (o.getResult()) {
		case SUCCESS:
			sucCount++;
			break;
		case FAILURE:
			errorCount++;
			break;
		default:
			timeoutCount++;
		}
		duration += o.getDuration();
	}

	public void reset() {
		errorCount = 0;
		sucCount = 0;
		duration = 0;
		timeoutCount = 0;
	}
}
