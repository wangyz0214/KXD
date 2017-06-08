package kxd.net.adapters;

import java.io.Serializable;

/**
 * 多线程组异步队列通信器<br>
 * 
 * @author zhaom
 * @since 4.2
 */
abstract public class MultiThreadAsyncQueueAdapter<K extends Serializable, S extends QueueRequest, R extends AsynResponse<K>>
		extends QueueAdapter<K, S, R> {

	class SendRunnable implements Runnable {
		Object context;

		public SendRunnable(Object context) {
			this.context = context;
		}

		@Override
		public void run() {
			sendThreadRun(context);
		}
	}

	class RecvRunnable implements Runnable {
		Object context;

		public RecvRunnable(Object context) {
			this.context = context;
		}

		@Override
		public void run() {
			recvThreadRun(context);
		}
	}

	/**
	 * 构造器
	 * 
	 * @param params
	 *            适配器参数
	 */
	public MultiThreadAsyncQueueAdapter(AdapterParams<K, S, R> params) {
		super(params);
	}

	/**
	 * 新建一个线程组
	 * 
	 * @param context
	 *            线程处理的上下文，用于发送和接收数据
	 */
	protected void newThreadGroup(Object context) {
		new Thread(new SendRunnable(context)).start();
		new Thread(new RecvRunnable(context)).start();
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
	abstract protected void send(Object context, K id, S data) throws Throwable;

	/**
	 * 接收一次数据
	 * 
	 * @return 包响应<br>
	 *         null 表示心跳包返回
	 * @throws Throwable
	 */
	abstract protected R recv(Object context) throws Throwable;

	/**
	 * 发送线程的执行函数
	 */
	protected void sendThreadRun(Object context) {
		while (!Thread.interrupted()) {
			try {
				NetPack<K, S> req = getRequest(params.aliveCheckTime);
				if (req != null) {
					send(context, req.getId(), req.getValue());
				} else {
					// 否则，发送心跳检查
					send(context, getNextId(), null);
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
	protected void recvThreadRun(Object context) {
		while (!Thread.interrupted()) {
			try {
				R r = recv(context);
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
