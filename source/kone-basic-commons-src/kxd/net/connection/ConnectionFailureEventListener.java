package kxd.net.connection;

/**
 * 连接失败事件侦听器
 * 
 * @author zhaom
 * @since 4.2
 */
public interface ConnectionFailureEventListener {
	/**
	 * 连接失败
	 * 
	 * @param <E>
	 * @param connection
	 *            连接失败的连接
	 */
	public <C extends PooledConnection<E>, E extends Throwable> void connectFailure(
			C connection,E throwable);
}
