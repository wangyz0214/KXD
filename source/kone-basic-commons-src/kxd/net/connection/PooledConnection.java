package kxd.net.connection;

/**
 * 受连接池管理的连接
 * 
 * @author zhaom
 * 
 */
public interface PooledConnection<E extends Throwable> extends Connection<E> {
	/**
	 * 连接创建后，调用此函数，初始化连接参数
	 * 
	 * @param creator
	 *            连接构建器
	 */
	public void connectionCreated(
			ConnectionCreator<? extends PooledConnection<E>, E> creator)
			throws E;

	/**
	 * 获取当前连接的连接池
	 */
	public ConnectionPool<? extends PooledConnection<E>, E> getConnectionPool();

	/**
	 * 保持连接的活动状态
	 */
	public void keepAlive() throws E;

	/**
	 * 获取连接的最后活动时间
	 */
	public long getLastAliveTime();

	/**
	 * 设置连接的最后活动时间
	 * 
	 * @param time
	 */
	public void setLastAliveTime(long time);

	/**
	 * 断开连接并从连接池中移走连接
	 */
	public void dispose() throws E;
}
