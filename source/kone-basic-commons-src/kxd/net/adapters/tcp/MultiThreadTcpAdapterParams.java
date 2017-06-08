package kxd.net.adapters.tcp;

import java.io.Serializable;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;

import kxd.net.adapters.AdapterParams;
import kxd.net.adapters.IdGenerator;
import kxd.net.adapters.NetPack;
import kxd.net.adapters.NetResponsePack;
import kxd.net.adapters.TimeoutCleanHashMap;

public class MultiThreadTcpAdapterParams<K extends Serializable, S extends Serializable, R extends Serializable>
		extends AdapterParams<K, S, R> {
	/**
	 * 套接字地址
	 */
	final CopyOnWriteArrayList<SocketAddress> addressList = new CopyOnWriteArrayList<SocketAddress>();
	/**
	 * 连接超时，以毫秒为单位
	 */
	final int connectTimeout;

	/**
	 * Tcp适配器参数
	 * 
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
	public MultiThreadTcpAdapterParams(int connectTimeout, int aliveCheckTime,
			int timeout, IdGenerator<K> idGenerator,
			TimeoutCleanHashMap<K, NetResponsePack<K, R>> responses,
			ArrayBlockingQueue<NetPack<K, S>> requestQueue) {
		super(aliveCheckTime, timeout, idGenerator, responses, requestQueue);
		this.connectTimeout = connectTimeout;
	}

	/**
	 * 连接超时，以毫秒为单位
	 */
	public int getConnectTimeout() {
		return connectTimeout;
	}

	public CopyOnWriteArrayList<SocketAddress> getAddressList() {
		return addressList;
	}

}
