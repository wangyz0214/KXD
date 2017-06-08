package kxd.net.adapters;

import java.io.Serializable;

import kxd.net.NetResponse;

/**
 * 多线程同步队列通信器<br>
 * 
 * @author zhaom
 * @since 4.1
 */
abstract public class MultiThreadSyncQueueAdapter<K extends Serializable, S extends QueueRequest, R extends NetResponse>
		extends QueueAdapter<K, S, R> {

	class ThreadRunnable implements Runnable {
		Object context;

		public ThreadRunnable(Object context) {
			this.context = context;
		}

		@Override
		public void run() {
			threadRun(context);
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

	public MultiThreadSyncQueueAdapter(AdapterParams<K, S, R> params) {
		super(params);
		// new Thread(new ThreadRunnable()).start();
	}

	protected void newThread(Object context) {
		new Thread(new ThreadRunnable(context)).start();
	}

	/**
	 * 发送心跳消息，保持连接
	 */
	abstract protected void keepAlive(Object context);

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
	abstract protected R doExecute(Object context, K id, S req)
			throws NetAdapterException;

	/**
	 * 线程的执行函数
	 */
	protected void threadRun(Object context) {
		while (!Thread.interrupted()) {
			try {
				NetPack<K, S> req = getRequest(params.aliveCheckTime);
				if (req != null) {
					try {
						R resp = doExecute(context, req.getId(), req.getValue());
						putResponse(req.getId(), resp);
					} catch (NetAdapterException e) { // 出现通信故障，则设置错误的响应并断开连接
						putErrorResponse(req.getId(), e);
					}
				} else {
					// 否则，发送心跳检查
					keepAlive(context);
				}
			} catch (InterruptedException e) {
				break;
			} catch (Throwable e) {
			}
		}
	}
}
