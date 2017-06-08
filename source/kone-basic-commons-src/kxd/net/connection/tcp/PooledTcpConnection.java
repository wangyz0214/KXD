package kxd.net.connection.tcp;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.log4j.Logger;

import kxd.net.connection.ConnectionCreator;
import kxd.net.connection.ConnectionPool;
import kxd.net.connection.PooledConnection;

abstract public class PooledTcpConnection extends TcpConnection implements
		PooledConnection<IOException> {
	ConnectionPool<? extends PooledConnection<IOException>, IOException> connectionPool;
	private volatile long lastAliveTime = System.currentTimeMillis();

	protected PooledTcpConnection() {
		super();
	}

	@Override
	public void connectionCreated(
			ConnectionCreator<? extends PooledConnection<IOException>, IOException> creator)
			throws IOException {
		connectionPool = creator.getConnectionPool();
		TcpConnectionCreator<? extends PooledConnection<IOException>> c = (TcpConnectionCreator<? extends PooledConnection<IOException>>) creator;
		address = new InetSocketAddress(c.getAddress(), c.getPort());
		connectTimeout = c.getConnectionTimeout();
	}

	@Override
	public ConnectionPool<? extends PooledConnection<IOException>, IOException> getConnectionPool() {
		return connectionPool;
	}

	@Override
	public long getLastAliveTime() {
		return lastAliveTime;
	}

	@Override
	public void setLastAliveTime(long time) {
		lastAliveTime = time;
	}

	@Override
	public void dispose() throws IOException {
		try {
			closeSocket();
		} finally {
			connectionPool.removeConnection(this);
		}
	}

	private final static Logger logger = Logger
			.getLogger(PooledTcpConnection.class);

	/**
	 * 将连接归还连接池，并不真正断开连接
	 */
	@Override
	public void close() throws IOException {
		connectionPool.returnConnection(this);
	}

	@Override
	public void open() throws IOException {
		if (isConnected())
			return;
		try {
			super.open();
			logger.info(this + " open [" + stream.getSocket() + "]");
			setLastAliveTime(System.currentTimeMillis());
			getConnectionPool().setAvailable(true);
			getConnectionPool().setLastAliveTime(lastAliveTime);
		} catch (IOException e) {
			closeSocket();
			getConnectionPool().connectionFailure(this, e);
			throw e;
		}
	}
}
