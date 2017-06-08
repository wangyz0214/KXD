package kxd.net.adapters;

import java.io.Serializable;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * 适配器参数配置
 * 
 * @author zhaom
 * @since 4.1
 * @param <K>
 * @param <S>
 * @param <R>
 */
public class AdapterParams<K extends Serializable, S extends Serializable, R extends Serializable> {
	/**
	 * 心跳检查时间，以毫秒为单位
	 */
	final int aliveCheckTime;
	/**
	 * 执行超时时间，以毫秒为单位
	 */
	final int timeout;
	/**
	 * 包ID生成器
	 */
	final IdGenerator<K> idGenerator;
	/**
	 * 请求队列，用于提交请求包
	 */
	final TimeoutCleanHashMap<K, NetResponsePack<K, R>> responses;
	/**
	 * 响应结果集，每次执行的结果会插入到该哈希结果集中
	 */
	final ArrayBlockingQueue<NetPack<K, S>> requestQueue;

	public AdapterParams(int aliveCheckTime, int timeout,
			IdGenerator<K> idGenerator,
			TimeoutCleanHashMap<K, NetResponsePack<K, R>> responses,
			ArrayBlockingQueue<NetPack<K, S>> requestQueue) {
		super();
		if (requestQueue == null || responses == null || idGenerator == null)
			throw new NullPointerException(
					"responses、requestQueue、idGenerator不能为null");
		if (aliveCheckTime < 1000)
			throw new IllegalArgumentException("aliveCheckTime值不能小于1000");
		if (timeout < 100)
			throw new IllegalArgumentException("timeout值不能小于100");
		this.aliveCheckTime = aliveCheckTime;
		this.timeout = timeout;
		this.idGenerator = idGenerator;
		this.responses = responses;
		this.requestQueue = requestQueue;
	}

	/**
	 * 心跳检查时间，以毫秒为单位
	 */
	public int getAliveCheckTime() {
		return aliveCheckTime;
	}

	/**
	 * 执行超时时间，以毫秒为单位
	 */
	public int getTimeout() {
		return timeout;
	}

	/**
	 * 包ID生成器
	 */
	public IdGenerator<K> getIdGenerator() {
		return idGenerator;
	}

	/**
	 * 请求队列，用于提交请求包
	 */
	public TimeoutCleanHashMap<K, NetResponsePack<K, R>> getResponses() {
		return responses;
	}

	/**
	 * 响应结果集，每次执行的结果会插入到该哈希结果集中
	 */
	public ArrayBlockingQueue<NetPack<K, S>> getRequestQueue() {
		return requestQueue;
	}

}
