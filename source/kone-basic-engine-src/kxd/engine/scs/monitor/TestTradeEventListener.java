package kxd.engine.scs.monitor;

import java.util.List;

import kxd.engine.scs.trade.drivers.TradeEventData;
import kxd.util.DateTime;

import org.apache.log4j.Logger;

public class TestTradeEventListener implements TradeEventListener {
	private static Logger logger = Logger
			.getLogger(TestTradeEventListener.class);

	@Override
	public void traded(List<TradeEventData> tradeEvents) {
		for (TradeEventData o : tradeEvents) {
			logger.debug("trade event[termid="
					+ o.getTradeCode().getTermId()
					+ ",tradecodeid="
					+ o.getTradeCode().getTradeCodeId()
					+ ",result="
					+ o.getTradeResult().getResult()
					+ ",times="
					+ DateTime.milliSecondsBetween(o.getTradeResult()
							.getTradeStartTime(), o.getTradeResult()
							.getTradeEndTime()) + "]");
		}
	}

}
