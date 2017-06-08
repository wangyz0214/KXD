package kxd.engine.scs.monitor;

import java.io.Serializable;
import java.util.List;

public class TermSubmittedStatus implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int termId;
	boolean pause;
	int busyTimes;
	int idleTimes;
	List<?> status;

	public TermSubmittedStatus(int termId, boolean pause, int busyTimes,
			int idleTimes, List<?> status) {
		super();
		this.termId = termId;
		this.pause = pause;
		this.busyTimes = busyTimes;
		this.idleTimes = idleTimes;
		this.status = status;
	}

	public int getTermId() {
		return termId;
	}

	public void setTermId(int termId) {
		this.termId = termId;
	}

	public boolean isPause() {
		return pause;
	}

	public void setPause(boolean pause) {
		this.pause = pause;
	}

	public int getBusyTimes() {
		return busyTimes;
	}

	public void setBusyTimes(int busyTimes) {
		this.busyTimes = busyTimes;
	}

	public int getIdleTimes() {
		return idleTimes;
	}

	public void setIdleTimes(int idleTimes) {
		this.idleTimes = idleTimes;
	}

	public List<?> getStatus() {
		return status;
	}

	public void setStatus(List<?> status) {
		this.status = status;
	}
}
