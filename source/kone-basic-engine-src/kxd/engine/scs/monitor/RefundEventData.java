package kxd.engine.scs.monitor;

import java.io.Serializable;

public class RefundEventData implements Serializable {
	private static final long serialVersionUID = 1L;
	private int orgId;
	private int tradeCodeId;
	private int amount;

	public int getOrgId() {
		return orgId;
	}

	public void setOrgId(int orgId) {
		this.orgId = orgId;
	}

	public int getTradeCodeId() {
		return tradeCodeId;
	}

	public void setTradeCodeId(int tradeCodeId) {
		this.tradeCodeId = tradeCodeId;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

}
