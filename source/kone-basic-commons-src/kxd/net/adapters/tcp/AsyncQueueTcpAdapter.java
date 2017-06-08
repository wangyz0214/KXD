package kxd.net.adapters.tcp;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;

import kxd.net.adapters.AsynResponse;
import kxd.net.adapters.AsyncQueueAdapter;
import kxd.net.adapters.NetAdapterException;
import kxd.net.adapters.QueueRequest;
import kxd.net.connection.tcp.SocketStream;

/**
 * 基于TCP套接字协议的异步队列通信适配器
 * 
 * @author zhaom
 * @since 4.1
 * @param <K>
 * @param <S>
 * @param <R>
 */
abstract public class AsyncQueueTcpAdapter<K extends Serializable, S extends QueueRequest, R extends AsynResponse<K>>
		extends AsyncQueueAdapter<K, S, R> {
	final AsynSocketStream stream;

	/**
	 * 构造器
	 * 
	 * @param params
	 *            同步TCP适配器参数
	 */
	public AsyncQueueTcpAdapter(TcpAdapterParams<K, S, R> params) {
		super(params);
		stream = new AsynSocketStream(params.address, params.connectTimeout);
	}

	/**
	 * 发送一次数据，如data为null，则发送心跳数据
	 * 
	 * @param id
	 *            包ID
	 * @param data
	 *            包数据
	 * @throws NetAdapterException
	 * @throws InterruptedException
	 */
	@Override
	protected void send(K id, S data) throws Throwable {
		try {
			if (data != null)
				data.setSendTime(new Date());
			doSend(stream, id, data);
		} catch (Throwable e) { // 意外异常按操作超时抛出异常
			stream.close();
			throw e;
		}
	}

	/**
	 * 接收一次数据
	 * 
	 * @return 包响应<br>
	 *         null 表示心跳包返回
	 * @throws NetAdapterException
	 *             通信失败时，抛出此异常
	 * @throws InterruptedException
	 */
	@Override
	protected R recv() throws Throwable {
		try {
			R r = doRecv(stream);
			if (r != null)
				r.setRecvTime(new Date());
			return r;
		} catch (Throwable e) { // 意外异常按操作超时抛出异常
			stream.close();
			throw e;
		}
	}

	/**
	 * 发送数据
	 * 
	 * @param stream
	 *            套接字流
	 * @param req
	 *            通信请求
	 * @return 通信返回包
	 * @throws IOException
	 */
	protected abstract void doSend(SocketStream stream, K id, S req)
			throws IOException;

	/**
	 * 接收数据
	 * 
	 * @param stream
	 *            套接字流
	 * @return R 接收到的响应包
	 * @throws IOException
	 */
	protected abstract R doRecv(SocketStream stream) throws IOException;

}
