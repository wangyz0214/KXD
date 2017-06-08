package kxd.net.adapters;

import java.io.Serializable;

/**
 * 异步队列通信器<br>
 * 本类提供两个后台线程，发检查队列是否有通信请求，然后，执行异步通信:
 * <ol>
 * <li>一个线程实时检查请求队列中是否有新的数据发送，有则立即调用<code>send()</code> 接口发送数据。</li>
 * <li>一个线程实时接收通信的响应，并将收到的响应放入请求结果集中</li>
 * </ol>
 * 注意：接口通信方式为异步，即发送请求后，不等收到数据立即返回。
 * 
 * @author zhaom
 * @since 4.1
 */
abstract public class AsyncQueueAdapter<K extends Serializable, S extends QueueRequest, R extends AsynResponse<K>>
		extends QueueAdapter<K, S, R> {

	class SendRunnable implements Runnable {

		@Override
		public void run() {
			sendThreadRun();
		}
	}

	class RecvRunnable implements Runnable {

		@Override
		public void run() {
			recvThreadRun();
		}
	}

	/**
	 * 构造器
	 * 
	 * @param params
	 *            适配器参数
	 */
	public AsyncQueueAdapter(AdapterParams<K, S, R> params) {
		super(params);
		new Thread(new SendRunnable()).start();
		new Thread(new RecvRunnable()).start();
	}

	/**
	 * 发送一次数据，如data为null，则发送心跳数据
	 * 
	 * @param id
	 *            包ID
	 * @param data
	 *            包数据
	 * @throws Throwable
	 */
	abstract protected void send(K id, S data) throws Throwable;

	/**
	 * 接收一次数据
	 * 
	 * @return 包响应<br>
	 *         null 表示心跳包返回
	 * @throws Throwable
	 */
	abstract protected R recv() throws Throwable;

	/**
	 * 发送线程的执行函数
	 */
	protected void sendThreadRun() {
		while (!Thread.interrupted()) {
			try {
				NetPack<K, S> req = getRequest(params.aliveCheckTime);
				if (req != null) {
					send(req.getId(), req.getValue());
				} else {
					// 否则，发送心跳检查
					send(getNextId(), null);
				}
			} catch (InterruptedException e) {
				break;
			} catch (Throwable e) {
			}
		}
	}

	/**
	 * 接收线程的执行函数
	 */
	protected void recvThreadRun() {
		while (!Thread.interrupted()) {
			try {
				R r = recv();
				if (r != null) {
					putResponse(r.getId(), r);
				}
			} catch (InterruptedException e) {
				break;
			} catch (Throwable e) {
			}
		}
	}
}
