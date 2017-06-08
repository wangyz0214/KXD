package kxd.net.adapters.tcp;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.concurrent.ArrayBlockingQueue;

import kxd.net.adapters.AdapterParams;
import kxd.net.adapters.IdGenerator;
import kxd.net.adapters.NetPack;
import kxd.net.adapters.NetResponsePack;
import kxd.net.adapters.TimeoutCleanHashMap;

public class TcpAdapterParams<K extends Serializable, S extends Serializable, R extends Serializable>
		extends AdapterParams<K, S, R> {
	/**
	 * 套接字地址
	 */
	final InetSocketAddress address;
	/**
	 * 连接超时，以毫秒为单位
	 */
	final int connectTimeout;

	/**
	 * Tcp适配器参数
	 * 
	 * @param address
	 *            主机地址
	 * @param connectTimeout
	 *            连接超时
	 * @param aliveCheckTime
	 *            心跳包检查时间
	 * @param timeout
	 *            读写超时
	 * @param idGenerator
	 *            序列号生成器
	 * @param responses
	 *            响应MAP
	 * @param requestQueue
	 *            请求队列
	 */
	public TcpAdapterParams(InetSocketAddress address, int connectTimeout,
			int aliveCheckTime, int timeout, IdGenerator<K> idGenerator,
			TimeoutCleanHashMap<K, NetResponsePack<K, R>> responses,
			ArrayBlockingQueue<NetPack<K, S>> requestQueue) {
		super(aliveCheckTime, timeout, idGenerator, responses, requestQueue);
		this.address = address;
		this.connectTimeout = connectTimeout;
	}

	/**
	 * 套接字地址
	 */
	public InetSocketAddress getAddress() {
		return address;
	}

	/**
	 * 连接超时，以毫秒为单位
	 */
	public int getConnectTimeout() {
		return connectTimeout;
	}

}
