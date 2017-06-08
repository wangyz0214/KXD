package kxd.net.adapters.tcp;

import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.util.Date;

import kxd.net.NetResponse;
import kxd.net.adapters.NetAdapterException;
import kxd.net.adapters.NetAdapterResult;
import kxd.net.adapters.QueueRequest;
import kxd.net.adapters.SyncQueueAdapter;
import kxd.net.connection.tcp.SocketStream;

/**
 * 基于TCP套接字协议的同步队列通信适配器
 * 
 * @author zhaom
 * @since 4.1
 * @param <K>
 * @param <S>
 * @param <R>
 */
abstract public class SyncQueueTcpAdapter<K extends Serializable, S extends QueueRequest, R extends NetResponse>
		extends SyncQueueAdapter<K, S, R> {
	SocketStream stream;

	/**
	 * 构造器
	 * 
	 * @param params
	 *            同步TCP适配器参数
	 */
	public SyncQueueTcpAdapter(TcpAdapterParams<K, S, R> params) {
		super(params);
	}

	protected void connect(TcpAdapterParams<K, S, R> params)
			throws NetAdapterException {
		if (stream == null) {
			getLogger().debug(
					"connect to host[" + params.address.getHostName() + ","
							+ params.address.getPort() + "]...");
			Socket socket = new Socket();
			try {
				socket.connect(params.getAddress(), params.connectTimeout);
			} catch (IOException e) {
				getLogger().error(
						"connect to host[" + params.address.getHostName() + ","
								+ params.address.getPort() + "] error:", e);
				throw new NetAdapterException(NetAdapterResult.NOTARRIVALED, e);
			}
			getLogger().debug(
					"host [" + params.address.getHostName() + ","
							+ params.address.getPort() + "] connected.");
			stream = new SocketStream(socket);
		}
	}

	protected void disconnect() {
		if (stream != null) {
			stream.close();
			stream = null;
			TcpAdapterParams<K, S, R> params = (TcpAdapterParams<K, S, R>) getParams();
			getLogger().debug(
					"host [" + params.address.getHostName() + ","
							+ params.address.getPort() + "] disconnected.");
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
	protected R doExecute(K id, S req) throws NetAdapterException {
		TcpAdapterParams<K, S, R> p = (TcpAdapterParams<K, S, R>) getParams();
		connect(p);
		try {
			req.setSendTime(new Date());
			R ret = doExecute(id, stream, req);
			ret.setRecvTime(new Date());
			return ret;
		} catch (NetAdapterException e) {
			switch (e.getResult()) {
			case TIMEOUT:
				disconnect();
				break;
			}
			throw e;
		} catch (Throwable e) { // 意外异常按操作超时抛出异常
			disconnect();
			throw new NetAdapterException(NetAdapterResult.TIMEOUT, e);
		}
	}

	@Override
	protected void keepAlive() {
		getLogger().debug("keep alive.");
		try {
			if (stream != null) {
				doKeepAlive(stream);
			}
		} catch (NetAdapterException e) {
			switch (e.getResult()) {
			case TIMEOUT:
				disconnect();
				break;
			}
		} catch (Throwable e) { // 意外异常按操作超时抛出异常
			disconnect();
		}
	}
}
