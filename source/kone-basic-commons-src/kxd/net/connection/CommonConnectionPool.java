package kxd.net.connection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

/**
 * 连接池，一个连接池可以管理若干个连接，这些连接都指向同一个Url。即一个连接池，不管有多少连接，都只访问一个服务器，不能实现负载均衡和分布式调用
 * 
 * @author zhaom
 * 
 * @param <C>
 */
public class CommonConnectionPool<C extends PooledConnection<E>, E extends Throwable>
		implements ConnectionPool<C, E> {
	private final static Logger logger = Logger.getLogger(ConnectionPool.class);
	ConnectionCreator<C, E> connectionCreator;
	ConnectionFailureEventListener connectionFailureEventListener;
	/**
	 * 最大连接数目，默认为30个
	 */
	protected volatile int maxConnectionSize = 30;
	/**
	 * 连接池使用次数
	 */
	protected volatile long times = 0;
	/**
	 * 空闲连接列表
	 */
	ArrayList<C> idleConnections = new ArrayList<C>();
	/**
	 * 工作连接列表
	 */
	ArrayList<C> workConnections = new ArrayList<C>();
	/**
	 * 连接池在连接池列表中的权重
	 */
	protected volatile int weights = 1;
	/**
	 * 最后的活动时间
	 */
	protected volatile long lastAliveTime = System.currentTimeMillis();
	/**
	 * 连接池当前是否可用
	 */
	protected volatile boolean available;
	private ReentrantLock lock = new ReentrantLock();

	/**
	 * 构建一个连接池
	 * 
	 * @param creator
	 *            连接构造器
	 * @param maxConnectionSize
	 *            最大的连接数
	 */
	public CommonConnectionPool(ConnectionCreator<C, E> creator,
			int maxConnectionSize) {
		super();
		this.connectionCreator = creator;
		creator.setConnectionPool(this);
		this.maxConnectionSize = maxConnectionSize;
	}

	/**
	 * 检查连接池中的所有空闲连接，发送保持连接命令
	 */
	public void keepAlive() {
		List<C> ls = new ArrayList<C>();
		final ReentrantLock lock = this.lock;
		lock.lock();
		try {
			ls.addAll(idleConnections);
		} finally {
			lock.unlock();
		}
		Iterator<C> it = ls.iterator();
		while (it.hasNext()) {
			try {
				it.next().keepAlive();
			} catch (Throwable e) {
			}
		}
		ls.clear();
		ls = null;
	}

	/**
	 * 获取一个连接
	 * 
	 * @param timeout
	 *            等待空闲连接的时间，单位：毫秒
	 * @return 如果获取成功，则返回获取的连接对象，否则，返回null
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public C getConnection() throws InterruptedException, E {
		times++;
		C o = getIdleConnection();
		if (o != null)
			return o;
		o = newConnection();
		if (o != null) {
			return o;
		}
		// 在指定的时间内继续等待空闲连接
		long time = System.currentTimeMillis();
		while (System.currentTimeMillis() - time < getConnectionCreator()
				.getConnectionTimeout()) {
			Thread.sleep(100);
			o = getIdleConnection();
			if (o != null) {
				return o;
			}
		}
		debug("Not enough connections");
		throw getConnectionCreator().createException("Not enough connections",
				null);
	}

	/**
	 * 创建一个新连接
	 * 
	 * @return 新创建的连接
	 * @throws IOException
	 */
	protected C newConnection() throws E {
		final ReentrantLock lock = this.lock;
		lock.lock();
		try {
			int c = idleConnections.size() + workConnections.size();
			if (c < maxConnectionSize) {
				C o = connectionCreator.newConnection();
				o.setLastAliveTime(System.currentTimeMillis());
				workConnections.add(o);
				debug("Get a new connection");
				return o;
			} else
				return null;
		} finally {
			lock.unlock();
		}
	}

	/**
	 * 获得连接总数
	 */
	protected int getConnectionCount() {
		int c;
		final ReentrantLock lock = this.lock;
		lock.lock();
		try {
			c = workConnections.size() + idleConnections.size();
		} finally {
			lock.unlock();
		}
		return c;
	}

	protected int getIdleConnectionCount() {
		int c;
		final ReentrantLock lock = this.lock;
		lock.lock();
		try {
			c = idleConnections.size();
		} finally {
			lock.unlock();
		}
		return c;
	}

	protected int getWorkCount() {
		int c;
		final ReentrantLock lock = this.lock;
		lock.lock();
		try {
			c = workConnections.size();
		} finally {
			lock.unlock();
		}
		return c;
	}

	/**
	 * 获取空闲连接
	 * 
	 * @return 如果有空闲连接，则返回，否则，返回null
	 */
	private C getIdleConnection() {
		final ReentrantLock lock = this.lock;
		lock.lock();
		try {
			if (idleConnections.size() > 0) {
				C o = idleConnections.remove(0);
				workConnections.add(o);
				o.setLastAliveTime(System.currentTimeMillis());
				debug("Get a idle connection");
				return o;
			} else
				return null;
		} finally {
			lock.unlock();
		}
	}

	/**
	 * 归还连接
	 * 
	 * @param o
	 *            要归还的连接
	 */
	@SuppressWarnings("unchecked")
	public boolean returnConnection(Object o) {
		final ReentrantLock lock = this.lock;
		lock.lock();
		try {
			if (workConnections.remove(o)) {
				idleConnections.add((C) o);
				((C) o).setLastAliveTime(System.currentTimeMillis());
				debug("Return a connection");
				return true;
			} else
				return false;
		} finally {
			lock.unlock();
		}
	}

	/**
	 * 移除连接
	 * 
	 * @param o
	 *            要移除的连接
	 */
	public void removeConnection(Object o) {
		final ReentrantLock lock = this.lock;
		lock.lock();
		try {
			workConnections.remove(o);
			idleConnections.remove(o);
			debug("Remove a connection");
		} finally {
			lock.unlock();
		}
	}

	public long getTimes() {
		return times;
	}

	/**
	 * 获取该连接池在连接池列表中的权重，只有使用分布式的连接池列表才有用
	 * 
	 */
	public int getWeights() {
		return weights;
	}

	/**
	 * 设备该连接池在连接池列表中的权重，只有使用分布式的连接池列表才有用
	 * 
	 */
	public void setWeights(int weights) {
		if (weights < 1)
			this.weights = 1;
		else
			this.weights = weights;
	}

	public ConnectionCreator<C, E> getConnectionCreator() {
		return connectionCreator;
	}

	public String toString() {
		final ReentrantLock lock = this.lock;
		lock.lock();
		try {
			int works = workConnections.size();
			int idles = idleConnections.size();
			return getConnectionCreator() + "({busy=" + works + ",idle="
					+ idles + ",max=" + maxConnectionSize + ",total="
					+ (works + idles) + "})";

		} finally {
			lock.unlock();
		}
	}

	public void debug(String msg) {
		if (logger.isDebugEnabled()) {
			logger.debug(toString() + ": " + msg);
		}
	}

	public void debug(String msg, Throwable e) {
		if (logger.isDebugEnabled()) {
			logger.error(toString() + ": " + msg, e);
		}
	}

	public long getLastAliveTime() {
		return lastAliveTime;
	}

	public void setLastAliveTime(long lastAliveTime) {
		this.lastAliveTime = lastAliveTime;
	}

	public boolean isAvailable() {
		return available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}

	public ConnectionFailureEventListener getConnectionFailureEventListener() {
		return connectionFailureEventListener;
	}

	public void setConnectionFailureEventListener(
			ConnectionFailureEventListener connectionFailureEventListener) {
		this.connectionFailureEventListener = connectionFailureEventListener;
	}

	/**
	 * 处理连接失败事件
	 * 
	 * @param connection
	 *            失败的连接
	 */
	public <Cx extends PooledConnection<Ex>, Ex extends Throwable> void connectionFailure(
			Cx connection, Ex throwable) {
		if (connectionFailureEventListener != null)
			connectionFailureEventListener
					.connectFailure(connection, throwable);
	}

	public void cleanAllConnections() {
		List<C> list = new ArrayList<C>();
		final ReentrantLock lock = this.lock;
		lock.lock();
		try {
			list.addAll(workConnections);
			list.addAll(idleConnections);
		} finally {
			lock.unlock();
		}
		for (C o : list) {
			try {
				o.dispose();
			} catch (Throwable e) {
			}
		}
		list.clear();
		list = null;
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
		CommonConnectionPool<C, E> other = (CommonConnectionPool<C, E>) obj;
		if (connectionCreator == null) {
			if (other.connectionCreator != null)
				return false;
		} else if (!connectionCreator.equals(other.connectionCreator))
			return false;
		return true;
	}

	public int getMaxConnectionSize() {
		return maxConnectionSize;
	}
}
