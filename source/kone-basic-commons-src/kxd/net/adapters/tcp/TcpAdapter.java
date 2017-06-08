package kxd.net.adapters.tcp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Date;

import kxd.net.NetRequest;
import kxd.net.NetResponse;
import kxd.net.adapters.NetAdapter;
import kxd.net.adapters.NetAdapterException;
import kxd.net.adapters.NetAdapterResult;
import kxd.net.connection.tcp.SocketStream;

/**
 * 基于TCP套接字短连接的通信器
 * 
 * @author zhaom
 * @since 4.1
 * @param <S>
 * @param <R>
 */
abstract public class TcpAdapter<S extends NetRequest, R extends NetResponse>
		implements NetAdapter<S, R> {
	InetSocketAddress address;
	int connectTimeout;
	boolean available;

	/**
	 * 短连接适配器构造器
	 * 
	 * @param address
	 *            地址
	 * @param connectTimeout
	 *            连接超时
	 */
	public TcpAdapter(InetSocketAddress address, int connectTimeout) {
		this.address = address;
		this.connectTimeout = connectTimeout;
	}

	/**
	 * 执行一次通信，继承类应该实现本函数
	 * 
	 * @param stream
	 *            套接字流
	 * @param data
	 *            发送数据
	 * @return 接收数据
	 * @throws NetAdapterException
	 *             通信失败时，抛出此异常
	 */
	abstract protected R doExecute(SocketStream stream, S data)
			throws NetAdapterException;

	protected SocketStream connect() throws IOException {
		Socket socket = new Socket();
		socket.connect(address, connectTimeout);
		available = true;
		return new SocketStream(socket);
	}

	@Override
	public R execute(S data) throws NetAdapterException {
		SocketStream stream = null;
		NetAdapterResult r = NetAdapterResult.NOTARRIVALED;
		try {
			stream = connect();
			r = NetAdapterResult.TIMEOUT;
			data.setSendTime(new Date());
			R ret = doExecute(stream, data);
			ret.setRecvTime(new Date());
			return ret;
		} catch (NetAdapterException e) {
			throw e;
		} catch (Throwable e) {
			throw new NetAdapterException(r, e);
		} finally {
			if (stream != null)
				stream.close();
		}
	}

	@Override
	public synchronized boolean isAvailable() {
		return available;
	}

}
