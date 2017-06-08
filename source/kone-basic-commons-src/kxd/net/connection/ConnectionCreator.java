package kxd.net.connection;

import kxd.util.ExceptionCreator;

/**
 * 连接构造器
 * 
 * @author zhaom
 * 
 * @param <C>
 */
public interface ConnectionCreator<C extends PooledConnection<E>, E extends Throwable>
		extends ExceptionCreator<E> {
	/**
	 * 新生成一个连接
	 */
	public C newConnection() throws E;

	/**
	 * 获取连接Url
	 */
	public String getConnectionUrl();

	/**
	 * 获取连接超时，毫秒
	 */
	public int getConnectionTimeout();

	public ConnectionPool<C, E> getConnectionPool();

	public void setConnectionPool(ConnectionPool<C, E> pool);
}
