package kxd.engine.scs.trade.drivers;

import java.io.Serializable;

import kxd.remote.scs.util.emun.TradeResult;
import kxd.util.DateTime;

public class InterfaceEventData implements Serializable {
	private static final long serialVersionUID = 1L;
	short interfaceId;
	int orgId;
	TradeResult Result;
	long duration;
	DateTime createTime = new DateTime();

	public InterfaceEventData(int orgId, short interfaceId, TradeResult result,
			long duration) {
		super();
		this.interfaceId = interfaceId;
		Result = result;
		this.duration = duration;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public short getInterfaceId() {
		return interfaceId;
	}

	public void setInterfaceId(short interfaceId) {
		this.interfaceId = interfaceId;
	}

	public TradeResult getResult() {
		return Result;
	}

	public void setResult(TradeResult result) {
		Result = result;
	}

	public int getOrgId() {
		return orgId;
	}

	public void setOrgId(int orgId) {
		this.orgId = orgId;
	}

	public DateTime getCreateTime() {
		return createTime;
	}

}
