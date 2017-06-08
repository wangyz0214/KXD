package kxd.net.adapters;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import kxd.net.NetResponse;

/**
 * 基于队列的网络适配器的抽象基本实现<br>
 * 此类适配器可保证收发在固定的后台线程内运行，对调用者来说，相当于队列调用
 * 
 * @author zhaom
 * @since 4.1
 * 
 * @param <K>
 *            包ID类
 * @param <S>
 *            发送包类
 * @param <R>
 *            接收包类
 */
abstract public class QueueAdapter<K extends Serializable, S extends QueueRequest, R extends NetResponse>
		implements NetAdapter<S, R> {
	/**
	 * 适配器参数
	 */
	AdapterParams<K, S, R> params;

	/**
	 * 构造器
	 * 
	 * @param params
	 *            适配器参数
	 */
	public QueueAdapter(AdapterParams<K, S, R> params) {
		super();
		if (params == null)
			throw new NullPointerException("params不能为null");
		this.params = params;
	}

	protected K getNextId() {
		return params.idGenerator.getNextId();
	}

	@Override
	final public R execute(S data) throws NetAdapterException {
		K id = getNextId();
		NetPack<K, S> req = putRequest(id, data);
		try {
			return getResponse(id);
		} finally { // 如果超时返回后，确保发送队列取到后，不再继续交易
			req.setAvailable(false);
		}
	}

	/**
	 * 设置获取响应包的间隔时间，时间越短，查询响应的次数越多，速度越快，但性能低了，可以重载合理设置<br>
	 * 默认值得为200毫秒
	 * 
	 */
	protected int getResponseSleep() {
		return 200;
	}

	/**
	 * 获取响应包信息
	 * 
	 * @param id
	 *            包ID
	 * @return 响应包
	 * @throws NetAdapterException
	 */
	protected R getResponse(K id) throws NetAdapterException {
		long now = System.currentTimeMillis();
		while ((System.currentTimeMillis() - now) <= params.timeout) {
			NetResponsePack<K, R> r = params.responses.remove(id);
			if (r != null) {
				if (r.getException() != null)
					throw r.getException();
				getLogger().debug(
						"Response packet[id=" + id + "] has been received");
				return r.getValue();
			}
			try {
				Thread.sleep(getResponseSleep());
			} catch (InterruptedException e) {
				getLogger()
						.debug("Response packet[id=" + id + "] not received");
				throw new NetAdapterException(NetAdapterResult.TIMEOUT, e);
			}
		}
		getLogger().debug("Response packet[id=" + id + "] not received");
		throw new NetAdapterException(NetAdapterResult.TIMEOUT, "获取响应超时");
	}

	/**
	 * 向请求队列中放入请求包信息
	 * 
	 * @param id
	 *            包ID
	 * @param value
	 *            包数据
	 * @throws NetAdapterException
	 */
	protected NetPack<K, S> putRequest(K id, S value)
			throws NetAdapterException {
		try {
			NetPack<K, S> pack = new NetPack<K, S>(id, value);
			if (!params.requestQueue.offer(pack, params.timeout,
					TimeUnit.MILLISECONDS)) {
				getLogger().debug(
						"Request packet[id=" + id + "] not putted,timeout");
				throw new NetAdapterException(NetAdapterResult.NOTARRIVALED,
						"系统忙，请求队列已满。");
			}
			getLogger().debug("Request packet[id=" + id + "] has putted");
			return pack;
		} catch (InterruptedException e) {
			getLogger().debug(
					"Request packet[id=" + id + "] not putted,interrupted");
			throw new NetAdapterException(NetAdapterResult.NOTARRIVALED, e);
		}
	}

	/**
	 * 向响应结果MAP中放入响应结果，供继承类使用
	 * 
	 * @param id
	 *            包ID
	 * @param value
	 *            包返回数据
	 */
	protected void putResponse(K id, R value) {
		NetResponsePack<K, R> pack = new NetResponsePack<K, R>(id, value);
		params.responses.put(id, pack);
	}

	/**
	 * 向结果集中添加错误响应包，供继承类使用
	 * 
	 * @param id
	 *            包ID
	 * @param exception
	 *            适配通信异常
	 */
	protected void putErrorResponse(K id, NetAdapterException exception) {
		NetResponsePack<K, R> pack = new NetResponsePack<K, R>(id, exception);
		params.responses.put(id, pack);
	}

	/**
	 * 向响应结果MAP中放入响应结果，供继承类使用
	 * 
	 * @param timeout
	 *            超时时间，以毫秒为单位。超时未得到请求，则返回null
	 * @return 请求包
	 * @throws InterruptedException
	 */
	protected NetPack<K, S> getRequest(int timeout) throws InterruptedException {
		NetPack<K, S> r = params.requestQueue.poll(timeout,
				TimeUnit.MILLISECONDS);
		if (r != null) {
			if (r.isAvailable())
				return r;
			else if (!r.value.isTimeoutDiscarded()) {
				getLogger().debug(
						"Request packet[id=" + r.id + "] has timed out, "
								+ "Continue to send requests.");
				return r;
			} else {
				getLogger().debug(
						"Request packet[id=" + r.id
								+ "] has timed out, discarded");
				return null;
			}
		} else
			return null;
	}

	public AdapterParams<K, S, R> getParams() {
		return params;
	}

}
