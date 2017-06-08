package kxd.remote.scs.transaction;

public class TradeTimeoutError extends TradeError {
	private static final long serialVersionUID = 1L;

	public TradeTimeoutError(Result result, String errorCode, String errorDesp) {
		super(result, errorCode, errorDesp);
	}

	public TradeTimeoutError(Result result, Throwable e) {
		super(result, e);
	}

	public TradeTimeoutError(String errorCode, String errorDesp) {
		super(errorCode, errorDesp);
	}

}
