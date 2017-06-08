package kxd.net.connection.db;

import java.sql.SQLException;

import kxd.net.connection.ConnectionCreator;
import kxd.net.connection.ConnectionPool;
import kxd.net.connection.PooledConnection;

abstract public class PooledDatabaseConnection extends DatabaseConnection
		implements PooledConnection<SQLException> {
	ConnectionPool<? extends PooledConnection<SQLException>, SQLException> connectionPool;
	private volatile long lastAliveTime = System.currentTimeMillis();

	protected PooledDatabaseConnection() {
		super();
	}

	@Override
	public void connectionCreated(
			ConnectionCreator<? extends PooledConnection<SQLException>, SQLException> creator)
			throws SQLException {
		connectionPool = creator.getConnectionPool();
		DatabaseConnectionCreator<? extends PooledConnection<SQLException>> c = (DatabaseConnectionCreator<? extends PooledConnection<SQLException>>) creator;
		this.setClassName(c.getClassName());
		this.setUrl(c.getUrl());
		this.setPasswd(c.getPasswd());
		this.setUser(c.getUser());
	}

	@Override
	public ConnectionPool<? extends PooledConnection<SQLException>, SQLException> getConnectionPool() {
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
	public void dispose() throws SQLException {
		try {
			disconnect();
		} finally {
			connectionPool.removeConnection(this);
		}
	}

	/**
	 * 将连接归还连接池，并不真正断开连接
	 */
	@Override
	public void close() throws SQLException {
		connectionPool.returnConnection(this);
	}

	@Override
	public void open() throws SQLException {
		if (isConnected())
			return;
		try {
			super.open();
			setLastAliveTime(System.currentTimeMillis());
			getConnectionPool().setAvailable(true);
			getConnectionPool().setLastAliveTime(lastAliveTime);
		} catch (SQLException e) {
			disconnect();
			getConnectionPool().connectionFailure(this, e);
			throw e;
		}
	}
}
