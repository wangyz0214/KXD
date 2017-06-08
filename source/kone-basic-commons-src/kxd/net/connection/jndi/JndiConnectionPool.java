package kxd.net.connection.jndi;

import java.util.concurrent.locks.ReentrantLock;

import kxd.net.connection.ConnectionCreator;
import kxd.net.connection.ConnectionFailureEventListener;
import kxd.net.connection.ConnectionPool;
import kxd.net.connection.PooledConnection;

import org.apache.log4j.Logger;

public class JndiConnectionPool<C extends PooledConnection<E>, E extends Throwable>
		implements ConnectionPool<C, E> {
	private final static Logger logger = Logger
			.getLogger(JndiConnectionPool.class);
	private volatile int weights = 1;
	private volatile long lastAliveTime;
	private volatile boolean available;
	ConnectionCreator<C, E> connectionCreator;
	ConnectionFailureEventListener connectionFailureEventListener;
	C connection;
	private ReentrantLock lock = new ReentrantLock();

	/**
	 * 构建一个连接池
	 * 
	 * @param creator
	 *            连接构造器
	 * @param maxConnectionSize
	 *            最大的连接数
	 */
	public JndiConnectionPool(ConnectionCreator<C, E> creator) {
		super();
		this.connectionCreator = creator;
		creator.setConnectionPool(this);
	}

	@Override
	public void keepAlive() {
		try {
			// logger.info("alive test");
			getConnection().keepAlive();
		} catch (Throwable e) {
			// logger.error("alive test failure:", e);
		}
	}

	@Override
	public C getConnection() throws InterruptedException, E {
		final ReentrantLock lock = this.lock;
		lock.lock();
		try {
			if (connection == null) {
				connection = connectionCreator.newConnection();
			}
			return connection;
		} finally {
			lock.unlock();
		}
	}

	@Override
	public boolean returnConnection(Object o) {
		return true;
	}

	@Override
	public void removeConnection(Object o) {
	}

	@Override
	public int getWeights() {
		return weights;
	}

	@Override
	public long getLastAliveTime() {
		return lastAliveTime;
	}

	@Override
	public void setLastAliveTime(long newValue) {
		lastAliveTime = newValue;
	}

	@Override
	public boolean isAvailable() {
		return available;
	}

	@Override
	public void setAvailable(boolean newValue) {
		available = newValue;
	}

	@Override
	public void cleanAllConnections() {
		try {
			if (connection != null) {
				connection.dispose();
				connection = null;
			}
		} catch (Throwable e) {
			connection = null;
			logger.debug("clean connections failure:", e);
		}
	}

	@Override
	public ConnectionFailureEventListener getConnectionFailureEventListener() {
		return connectionFailureEventListener;
	}

	@Override
	public void setConnectionFailureEventListener(
			ConnectionFailureEventListener connectionFailureEventListener) {
		this.connectionFailureEventListener = connectionFailureEventListener;
	}

	@Override
	public <Cx extends PooledConnection<Ex>, Ex extends Throwable> void connectionFailure(
			Cx connection, Ex throwable) {
		if (connectionFailureEventListener != null)
			connectionFailureEventListener
					.connectFailure(connection, throwable);
	}

	@Override
	public ConnectionCreator<C, E> getConnectionCreator() {
		return connectionCreator;
	}

	@Override
	public void setWeights(int newValue) {
		this.weights = newValue;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((connectionCreator == null) ? 0 : connectionCreator
						.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		@SuppressWarnings("unchecked")
		JndiConnectionPool<C, E> other = (JndiConnectionPool<C, E>) obj;
		if (connectionCreator == null) {
			if (other.connectionCreator != null)
				return false;
		} else if (!connectionCreator.equals(other.connectionCreator))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return connectionCreator.toString();
	}

}
