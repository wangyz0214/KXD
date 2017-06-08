package kxd.engine.scs.monitor;

import java.io.Serializable;

import kxd.engine.scs.trade.drivers.TradeEventData;
import kxd.remote.scs.util.emun.TradeResult;

public class TradeMonitorDataItem implements Serializable {
	private static final long serialVersionUID = 1L;
	public long count;
	public long sucCount;
	public long amount;
	public long sucAmount;
	public long duration;
	public long returnCount;
	public long returnAmount;
	public long cancelReturnCount;
	public long cancelReturnAmount;

	public TradeMonitorDataItem() {

	}

	public TradeMonitorDataItem(Object o[], int startIndex) {
		count = Long.valueOf(o[startIndex++].toString());
		sucCount = Long.valueOf(o[startIndex++].toString());
		amount = Long.valueOf(o[startIndex++].toString());
		sucAmount = Long.valueOf(o[startIndex++].toString());
		duration = Long.valueOf(o[startIndex++].toString());
		returnCount = Long.valueOf(o[startIndex++].toString());
		returnAmount = Long.valueOf(o[startIndex++].toString());
		cancelReturnCount = Long.valueOf(o[startIndex++].toString());
		cancelReturnAmount = Long.valueOf(o[startIndex++].toString());
	}

	public void add(boolean success, long count, long amount, long duration) {
		this.count += count;
		this.amount += amount;
		this.duration += duration;
		if (success) {
			this.sucAmount += amount;
			this.sucCount += count;
		}
	}

	public void addRefund(boolean iscancel, long count, long amount) {
		if (iscancel) {
			this.cancelReturnCount += count;
			this.cancelReturnAmount += amount;
		} else {
			this.returnCount += count;
			this.returnAmount += amount;
		}
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	public long getSucCount() {
		return sucCount;
	}

	public void setSucCount(long sucCount) {
		this.sucCount = sucCount;
	}

	public long getAmount() {
		return amount;
	}

	public void setAmount(long amount) {
		this.amount = amount;
	}

	public long getSucAmount() {
		return sucAmount;
	}

	public void setSucAmount(long sucAmount) {
		this.sucAmount = sucAmount;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public long getReturnCount() {
		return returnCount;
	}

	public void setReturnCount(long returnCount) {
		this.returnCount = returnCount;
	}

	public long getReturnAmount() {
		return returnAmount;
	}

	public void setReturnAmount(long returnAmount) {
		this.returnAmount = returnAmount;
	}

	public long getCancelReturnCount() {
		return cancelReturnCount;
	}

	public void setCancelReturnCount(long cancelReturnCount) {
		this.cancelReturnCount = cancelReturnCount;
	}

	public long getCancelReturnAmount() {
		return cancelReturnAmount;
	}

	public void setCancelReturnAmount(long cancelReturnAmount) {
		this.cancelReturnAmount = cancelReturnAmount;
	}

	public void addData(TradeMonitorDataItem o) {
		setCount(count + o.getCount());
		setSucCount(sucCount + o.getSucCount());
		setAmount(amount + o.getAmount());
		setSucAmount(sucAmount + o.getSucAmount());
		setCancelReturnAmount(cancelReturnAmount + o.getCancelReturnAmount());
		setCancelReturnCount(cancelReturnCount + o.getCancelReturnCount());
		setReturnCount(returnCount + o.getReturnCount());
		setReturnAmount(returnAmount + o.getReturnAmount());
		setDuration(duration + o.getDuration());
	}

	public void addData(TradeEventData o) {
		count++;
		amount += o.getTradeCode().getAmount();
		if (o.getTradeResult().getResult().equals(TradeResult.SUCCESS)) {
			sucCount++;
			sucAmount += o.getTradeCode().getAmount();
		}
		duration += o.getDuration();
	}

	public void reset() {
		count = 0;
		sucCount = 0;
		cancelReturnAmount = 0;
		cancelReturnCount = 0;
		duration = 0;
		returnCount = 0;
		amount = 0;
		sucAmount = 0;
		returnAmount = 0;
	}
}
