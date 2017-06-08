package kxd.net.connection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import kxd.util.ExceptionCreator;

import org.apache.log4j.Logger;

/**
 * 连接池列表，支持多不同主机的连接池管理，用于负载均衡调用
 * 
 * @param <C>
 */
abstract public class ConnectionPoolList<C extends PooledConnection<E>, E extends Throwable>
		extends CopyOnWriteArrayList<ConnectionPool<C, E>> implements
		ConnectionFailureEventListener {
	private static final Logger logger = Logger
			.getLogger(ConnectionPoolList.class);
	private static final long serialVersionUID = 1L;
	/**
	 * 异常构造器
	 */
	ExceptionCreator<E> exceptionCreator;
	/**
	 * 坏连接池列表
	 */
	CopyOnWriteArrayList<ConnectionPool<C, E>> badConnectionPools = new CopyOnWriteArrayList<ConnectionPool<C, E>>();
	public String name;

	protected ConnectionPoolList(ExceptionCreator<E> exceptionCreator) {
		super();
		if (exceptionCreator == null)
			throw new NullPointerException("exceptionCreator is null");
		this.exceptionCreator = exceptionCreator;
	}

	protected ConnectionPoolList(ExceptionCreator<E> exceptionCreator,
			Collection<? extends ConnectionPool<C, E>> c) {
		super(c);
		if (exceptionCreator == null)
			throw new NullPointerException("exceptionCreator is null");
		this.exceptionCreator = exceptionCreator;
	}

	protected ConnectionPoolList(ExceptionCreator<E> exceptionCreator,
			ConnectionPool<C, E>[] toCopyIn) {
		super(toCopyIn);
		if (exceptionCreator == null)
			throw new NullPointerException("exceptionCreator is null");
		this.exceptionCreator = exceptionCreator;
	}

	/**
	 * 当连接池发生变化（添加，删除）时，调用此函数，重新组织分布式访问
	 */
	abstract protected void changed();

	@SuppressWarnings("unchecked")
	@Override
	public <Cx extends PooledConnection<Ex>, Ex extends Throwable> void connectFailure(
			Cx connection, Ex throwable) {
		if (size() > 1) {
			ConnectionPool<C, E> pool = (ConnectionPool<C, E>) connection
					.getConnectionPool();
			badConnectionPools.add(pool);
			remove(pool);
			try {
				pool.cleanAllConnections();
			} catch (Throwable e) {
			}
		}
		logger.error(connection.getConnectionPool().toString()
				+ ": Connection has been damaged: ", throwable);
	}

	/**
	 * 检查连接池列表中的全部连接，发送保持连接的命令
	 */
	protected void keepAlive() {
		// logger.info("[" + name + "] -> alive test start[" + size() + ","
		// + badConnectionPools.size() + "]...");
		List<ConnectionPool<C, E>> ls = new ArrayList<ConnectionPool<C, E>>();
		ls.addAll(this);
		Iterator<ConnectionPool<C, E>> it = ls.iterator();
		while (it.hasNext()) {
			try {
				it.next().keepAlive();
			} catch (Throwable e) {
			}
		}
		ls.clear();
		ls = null;
		checkBadConnectionPools();
		// logger.info("[" + name + "] -> alive test end.");
	}

	/**
	 * 检查连接池列表中的全部连接，发送保持连接的命令
	 */
	protected void checkBadConnectionPools() {
		List<ConnectionPool<C, E>> ls = new ArrayList<ConnectionPool<C, E>>();
		ls.addAll(badConnectionPools);
		Iterator<ConnectionPool<C, E>> it = ls.iterator();
		while (it.hasNext()) {
			try {
				ConnectionPool<C, E> p = it.next();
				C con = p.getConnection();
				try {
					con.open();
					// 打开连接成功，则将连接池置为有效连接
					badConnectionPools.remove(p);
					add(p);
					logger.debug(p.toString() + ": connection restored.");
				} finally {
					con.close();
				}
			} catch (Throwable e) {
			}
		}
		ls.clear();
		ls = null;
	}

	@Override
	public ConnectionPool<C, E> set(int index, ConnectionPool<C, E> element) {
		try {
			element.setConnectionFailureEventListener(this);
			return super.set(index, element);
		} finally {
			changed();
		}
	}

	@Override
	public boolean add(ConnectionPool<C, E> e) {
		if (super.add(e)) {
			e.setConnectionFailureEventListener(this);
			changed();
			return true;
		} else
			return false;
	}

	@Override
	public void add(int index, ConnectionPool<C, E> element) {
		super.add(index, element);
		element.setConnectionFailureEventListener(this);
		changed();
	}

	@Override
	public ConnectionPool<C, E> remove(int index) {
		try {
			ConnectionPool<C, E> o = super.remove(index);
			if (o != null)
				o.setConnectionFailureEventListener(null);
			return o;
		} finally {
			changed();
		}
	}

	@Override
	public boolean remove(Object o) {
		if (super.remove(o)) {
			((ConnectionPool<?, ?>) o).setConnectionFailureEventListener(null);
			changed();
			return true;
		} else
			return false;
	}

	@Override
	public boolean addIfAbsent(ConnectionPool<C, E> e) {
		if (super.addIfAbsent(e)) {
			e.setConnectionFailureEventListener(this);
			changed();
			return true;
		} else
			return false;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		if (super.removeAll(c)) {
			for (Object o : c)
				((ConnectionPool<?, ?>) o)
						.setConnectionFailureEventListener(null);
			changed();
			return true;
		} else
			return false;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int addAllAbsent(Collection<? extends ConnectionPool<C, E>> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		for (ConnectionPool<C, E> o : this)
			o.setConnectionFailureEventListener(null);
		super.clear();
		changed();
	}

	@Override
	public boolean addAll(Collection<? extends ConnectionPool<C, E>> c) {
		if (super.addAll(c)) {
			for (Object o : c)
				((ConnectionPool<?, ?>) o)
						.setConnectionFailureEventListener(this);
			changed();
			return true;
		} else
			return false;
	}

	@Override
	public boolean addAll(int index,
			Collection<? extends ConnectionPool<C, E>> c) {
		if (super.addAll(index, c)) {
			for (Object o : c)
				((ConnectionPool<?, ?>) o)
						.setConnectionFailureEventListener(this);
			changed();
			return true;
		} else
			return false;
	}

	public ExceptionCreator<E> getExceptionCreator() {
		return exceptionCreator;
	}

	public void setExceptionCreator(ExceptionCreator<E> exceptionCreator) {
		this.exceptionCreator = exceptionCreator;
	}

}
