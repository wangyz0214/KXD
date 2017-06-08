package kxd.net.connection;

import java.io.IOException;

/**
 * 连接池，一个连接池可以管理若干个连接，这些连接都指向同一个Url。即一个连接池，不管有多少连接，都只访问一个服务器，不能实现负载均衡和分布式调用
 * 
 * @author zhaom
 * 
 * @param <C>
 */
public interface ConnectionPool<C extends PooledConnection<E>, E extends Throwable> {
	/**
	 * 检查连接池中的所有空闲连接，发送保持连接命令
	 */
	public void keepAlive();

	/**
	 * 获取一个连接
	 * 
	 * @param timeout
	 *            等待空闲连接的时间，单位：毫秒
	 * @return 如果获取成功，则返回获取的连接对象，否则，返回null
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public C getConnection() throws InterruptedException, E;

	/**
	 * 归还连接
	 * 
	 * @param o
	 *            要归还的连接
	 */
	public boolean returnConnection(Object o);

	/**
	 * 移除连接
	 * 
	 * @param o
	 *            要移除的连接
	 */
	public void removeConnection(Object o);

	/**
	 * 获取该连接池在连接池列表中的权重，只有使用分布式的连接池列表才有用
	 * 
	 */
	public int getWeights();

	public void setWeights(int newValue);

	/**
	 * 获取最后活动时间
	 * 
	 * @return
	 */
	public long getLastAliveTime();

	public void setLastAliveTime(long newValue);

	/**
	 * 当前是否活动
	 * 
	 * @return
	 */
	public boolean isAvailable();

	public void setAvailable(boolean newValue);

	/**
	 * 清除所有连接
	 */
	public void cleanAllConnections();

	public ConnectionFailureEventListener getConnectionFailureEventListener();

	public void setConnectionFailureEventListener(
			ConnectionFailureEventListener connectionFailureEventListener);

	public <Cx extends PooledConnection<Ex>, Ex extends Throwable> void connectionFailure(
			Cx connection, Ex throwable);

	/**
	 * 获取连接创建器
	 * 
	 * @return
	 */
	public ConnectionCreator<C, E> getConnectionCreator();
}
