package kxd.net.adapters;

import kxd.util.KoneException;

/**
 * 网络适配器异常
 * 
 * @author zhaom
 * @since 4.1
 */
public class NetAdapterException extends KoneException {
	private static final long serialVersionUID = -1765371479449073758L;
	NetAdapterResult result;

	public NetAdapterException(NetAdapterResult result) {
		this.result = result;
	}

	public NetAdapterException(NetAdapterResult result, String msg) {
		super(msg);
		this.result = result;
	}

	public NetAdapterException(NetAdapterResult result, Throwable cause) {
		super(cause);
		this.result = result;
	}

	public NetAdapterException(NetAdapterResult result, String msg,
			Throwable cause) {
		super(msg, cause);
		this.result = result;
	}

	@Override
	public String toString() {
		return "通信异常 [result=" + result + "] " + super.toString();
	}

	public synchronized NetAdapterResult getResult() {
		return result;
	}

}
