package kxd.remote.scs.beans;

import java.io.Serializable;

public class BaseMonitorData implements Serializable {
	private static final long serialVersionUID = 1L;
	private long termCount;
	private long tradeCount;
	private long sucTradeCount;
	private long tradeMoney;

	public BaseMonitorData(long termCount, long tradeCount, long sucTradeCount,
			long tradeMoney) {
		super();
		this.termCount = termCount;
		this.tradeCount = tradeCount;
		this.sucTradeCount = sucTradeCount;
		this.tradeMoney = tradeMoney;
	}

	public long getTermCount() {
		return termCount;
	}

	public void setTermCount(long termCount) {
		this.termCount = termCount;
	}

	public long getTradeCount() {
		return tradeCount;
	}

	public void setTradeCount(long tradeCount) {
		this.tradeCount = tradeCount;
	}

	public long getSucTradeCount() {
		return sucTradeCount;
	}

	public void setSucTradeCount(long sucTradeCount) {
		this.sucTradeCount = sucTradeCount;
	}

	public long getTradeMoney() {
		return tradeMoney;
	}

	public void setTradeMoney(long tradeMoney) {
		this.tradeMoney = tradeMoney;
	}
}
