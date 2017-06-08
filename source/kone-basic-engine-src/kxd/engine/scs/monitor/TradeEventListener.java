package kxd.engine.scs.monitor;

import java.util.List;

import kxd.engine.scs.trade.drivers.TradeEventData;

public interface TradeEventListener {
	/**
	 * 多笔交易完成
	 */
	public void traded(List<TradeEventData> tradeEvents);
}
