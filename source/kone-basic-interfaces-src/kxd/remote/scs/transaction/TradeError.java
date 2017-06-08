package kxd.remote.scs.transaction;

import kxd.util.StringUnit;

public class TradeError extends Exception {
	private static final long serialVersionUID = 1L;
	String errorCode, errorDesp;
	Result result;
	Object data;

	public TradeError(Result result, String errorCode, String errorDesp) {
		super(errorDesp);
		this.errorCode = errorCode;
		this.errorDesp = errorDesp;
		this.result = result;
	}

	public TradeError(Result result, Throwable e) {
		super(e);
		this.result = result;
		this.errorCode = "yw";
		this.errorDesp = StringUnit.getExceptionMessage(e);
	}

	public TradeError(String errCode, String msg, Throwable e) {
		super(msg, e);
		this.errorCode = errCode;
		this.errorDesp = msg;
	}

	public TradeError(String errorCode, String errorDesp) {
		super(errorDesp);
		this.errorCode = errorCode;
		this.errorDesp = errorDesp;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorDesp() {
		return errorDesp;
	}

	public void setErrorDesp(String errorDesp) {
		this.errorDesp = errorDesp;
	}

	public Result getResult() {
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
}
