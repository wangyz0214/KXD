package kxd.net.adapters;

import java.io.Serializable;

/**
 * 网络通信响应包
 * 
 * @author zhaom
 * @since 4.1
 * @param <K>
 *            包ID类
 * @param <V>
 *            包数据类
 */
public class NetResponsePack<K extends Serializable, V extends Serializable>
		extends NetPack<K, V> {
	private static final long serialVersionUID = -1443962539204310158L;
	final NetAdapterException exception;

	public NetResponsePack(K id, NetAdapterException exception) {
		super(id);
		this.exception = exception;
	}

	public NetResponsePack(K id, V value) {
		super(id, value);
		this.exception = null;
	}

	public synchronized NetAdapterException getException() {
		return exception;
	}

	@Override
	public String toString() {
		return "NetResponsePack [exception=" + exception + "["
				+ super.toString() + "]]";
	}

}
