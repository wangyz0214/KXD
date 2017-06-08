package kxd.net.adapters;

import java.io.Serializable;

import kxd.net.NetResponse;

/**
 * 同步队列通信器<br>
 * 本类提供一个后台线程，检查队列是否有通信请求，然后，执行同步通信:
 * <ol>
 * <li>如果有请求: 则立即调用<code>doExecute()</code>
 * 接口执行通信，并将通信返回放入结果集中，如果通信失败，则在结果集中放入错误的响应包。</li>
 * <li>如果在规定的时间(idleCheckTime)内没有请求: 则调用<code>keepAlive()</code>
 * 接口执行心跳，保持适配器的活动状态，心跳通信结果不会放入到响应包中。</li>
 * </ol>
 * 注意：接口通信方式为同步，即发送请求后，接收到响应或超时才能返回。继承时实现<code>doExecute()</code>,
 * <code>keepAlive()</code>接口时，都需要采用这种方式通信
 * 
 * @author zhaom
 * @since 4.1
 */
abstract public class SyncQueueAdapter<K extends Serializable, S extends QueueRequest, R extends NetResponse>
		extends QueueAdapter<K, S, R> {

	class ThreadRunnable implements Runnable {

		@Override
		public void run() {
			threadRun();
		}
	}

	/**
	 * 构造器。
	 * 
	 * @param aliveCheckTime
	 *            心跳检查时间，以毫秒为单位
	 * @param idGenerator
	 *            包ID生成器
	 * @param requestQueue
	 *            请求队列，用于提交请求包
	 * @param responses
	 *            响应结果集，每次执行的结果会插入到该哈希结果集中
	 * @param timeout
	 *            超时时间，以毫秒为单位
	 */

	public SyncQueueAdapter(AdapterParams<K, S, R> params) {
		super(params);
		new Thread(new ThreadRunnable()).start();
	}

	/**
	 * 发送心跳消息，保持连接
	 */
	abstract protected void keepAlive();

	/**
	 * 执行一次通信
	 * 
	 * @param id
	 *            包ID，除了用于记录日志外，通常没有其他用处
	 * @param req
	 *            包请求
	 * @return 包响应
	 * @throws NetAdapterException
	 *             通信失败时，抛出此异常
	 */
	abstract protected R doExecute(K id, S req) throws NetAdapterException;

	/**
	 * 线程的执行函数
	 */
	protected void threadRun() {
		while (!Thread.interrupted()) {
			try {
				NetPack<K, S> req = getRequest(params.aliveCheckTime);
				if (req != null) {
					try {
						R resp = doExecute(req.getId(), req.getValue());
						putResponse(req.getId(), resp);
					} catch (NetAdapterException e) { // 出现通信故障，则设置错误的响应并断开连接
						putErrorResponse(req.getId(), e);
					}
				} else {
					// 否则，发送心跳检查
					keepAlive();
				}
			} catch (InterruptedException e) {
				break;
			} catch (Throwable e) {
			}
		}
	}
}
