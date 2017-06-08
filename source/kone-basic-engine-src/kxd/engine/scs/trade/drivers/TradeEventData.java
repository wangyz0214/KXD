package kxd.engine.scs.trade.drivers;

import java.io.Serializable;

import kxd.remote.scs.transaction.Result;
import kxd.remote.scs.transaction.TradeCode;

public class TradeEventData implements Serializable {
	private static final long serialVersionUID = 1L;
	TradeCode tradeCode;
	Result tradeResult;
	long duration;

	public TradeCode getTradeCode() {
		return tradeCode;
	}

	public void setTradeCode(TradeCode tradeCode) {
		this.tradeCode = tradeCode;
	}

	public Result getTradeResult() {
		return tradeResult;
	}

	public void setTradeResult(Result tradeResult) {
		this.tradeResult = tradeResult;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

}
