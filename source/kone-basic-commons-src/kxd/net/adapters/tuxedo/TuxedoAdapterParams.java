package kxd.net.adapters.tuxedo;

import java.io.Serializable;
import java.util.concurrent.ArrayBlockingQueue;

import kxd.net.adapters.AdapterParams;
import kxd.net.adapters.IdGenerator;
import kxd.net.adapters.NetPack;
import kxd.net.adapters.NetResponsePack;
import kxd.net.adapters.TimeoutCleanHashMap;
import kxd.net.adapters.tuxedo.atmi.TuxedoConfig;

public class TuxedoAdapterParams<K extends Serializable, S extends Serializable, R extends Serializable>
		extends AdapterParams<K, S, R> {
	/**
	 * Tuxedo配置
	 */
	final TuxedoConfig config;

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
	public TuxedoAdapterParams(TuxedoConfig config, int aliveCheckTime,
			int timeout, IdGenerator<K> idGenerator,
			TimeoutCleanHashMap<K, NetResponsePack<K, R>> responses,
			ArrayBlockingQueue<NetPack<K, S>> requestQueue) {
		super(aliveCheckTime, timeout, idGenerator, responses, requestQueue);
		this.config = config;
	}

	public TuxedoConfig getConfig() {
		return config;
	}

}
