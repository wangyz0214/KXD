package kxd.engine.scs.report.beans;

import java.io.Serializable;

/**
 * 故障统计项
 * 
 * @author zhaom
 * 
 */
public class FaultStatField1 implements Serializable {
	private static final long serialVersionUID = 1L;
	private long count = 0;
	private long prevMonthCount = 0;
	private String rate;

	public FaultStatField1() {
		super();
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	public long getPrevMonthCount() {
		return prevMonthCount;
	}

	public void setPrevMonthCount(long prevMonthCount) {
		this.prevMonthCount = prevMonthCount;
	}

	public String getRate() {
		return rate;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}

}
