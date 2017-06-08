package kxd.net.adapters.tcp;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;

import kxd.net.NetResponse;
import kxd.net.adapters.MultiThreadSyncQueueAdapter;
import kxd.net.adapters.NetAdapterException;
import kxd.net.adapters.NetAdapterResult;
import kxd.net.adapters.QueueRequest;
import kxd.net.connection.tcp.SocketStream;

/**
 * 基于TCP套接字协议的多线程同步队列通信适配器
 * 
 * @author zhaom
 * @since 4.1
 * @param <K>
 * @param <S>
 * @param <R>
 */
abstract public class MultiThreadSyncQueueTcpAdapter<K extends Serializable, S extends QueueRequest, R extends NetResponse>
		extends MultiThreadSyncQueueAdapter<K, S, R> {

	/**
	 * 构造器
	 * 
	 * @param params
	 *            同步TCP适配器参数
	 */
	public MultiThreadSyncQueueTcpAdapter(
			MultiThreadTcpAdapterParams<K, S, R> params) {
		super(params);
		for (SocketAddress o : params.addressList) {
			for (int i = 0; i < o.enabledConnectionCount; i++) {
				SyncSocketStream stream = new SyncSocketStream(o,
						params.connectTimeout);
				newThread(stream);
			}
		}
	}

	/**
	 * 执行一次通信
	 * 
	 * @param id
	 *            包ID，除了用于记录日志外，通常没有其他用处
	 * @param stream
	 *            套接字流
	 * @param req
	 *            通信请求
	 * @return 通信返回包
	 * @throws NetAdapterException
	 */
	protected abstract R doExecute(K id, SocketStream stream, S req)
			throws NetAdapterException;

	/**
	 * 保持连接的通信
	 * 
	 * @param stream
	 *            套接字流
	 */
	protected abstract void doKeepAlive(SocketStream stream)
			throws NetAdapterException;

	@Override
	protected R doExecute(Object context, K id, S req)
			throws NetAdapterException {
		SyncSocketStream stream = (SyncSocketStream) context;
		try {
			stream.connect();
		} catch (IOException e) {
			throw new NetAdapterException(NetAdapterResult.NOTARRIVALED, e);
		}
		try {
			req.setSendTime(new Date());
			R ret = doExecute(id, stream, req);
			ret.setRecvTime(new Date());
			return ret;
		} catch (NetAdapterException e) {
			switch (e.getResult()) {
			case TIMEOUT:
				stream.close();
				break;
			}
			throw e;
		} catch (Throwable e) { // 意外异常按操作超时抛出异常
			stream.close();
			throw new NetAdapterException(NetAdapterResult.TIMEOUT, e);
		}
	}

	@Override
	protected void keepAlive(Object context) {
		getLogger().debug("keep alive.");
		SyncSocketStream stream = (SyncSocketStream) context;
		try {
			stream.connect();
			doKeepAlive(stream);
		} catch (NetAdapterException e) {
			switch (e.getResult()) {
			case TIMEOUT:
				stream.close();
				break;
			}
		} catch (Throwable e) { // 意外异常按操作超时抛出异常
			stream.close();
		}
	}
}
