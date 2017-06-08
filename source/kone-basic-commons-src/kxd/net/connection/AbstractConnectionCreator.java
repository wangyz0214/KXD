package kxd.net.connection;


/**
 * 抽象连接构造器
 * 
 * @author zhaom
 * 
 * @param <C>
 * @since 4.1
 */
abstract public class AbstractConnectionCreator<C extends PooledConnection<E>, E extends Throwable>
		implements ConnectionCreator<C, E> {
	private ConnectionPool<C, E> pool;
	private volatile String groupName;
	private Class<C> clazz;

	/**
	 * 
	 * @param clazz
	 *            要生成的类
	 * 
	 * @param groupName
	 *            连接分组名称
	 */
	public AbstractConnectionCreator(Class<C> clazz, String groupName) {
		super();
		this.groupName = groupName;
		this.clazz = clazz;
	}

	public ConnectionPool<C, E> getConnectionPool() {
		return pool;
	}

	public void setConnectionPool(ConnectionPool<C, E> pool) {
		this.pool = pool;
	}

	public String getGroupName() {
		return groupName;
	}

	public String toString() {
		return getConnectionUrl() + "[" + groupName + "]";
	}

	@Override
	public C newConnection() throws E {
		C e;
		try {
			e = clazz.newInstance();
		} catch (Throwable e1) {
			throw createException("New connection failure: ", e1);
		}
		e.connectionCreated(this);
		return e;
	}

	public Class<C> getClazz() {
		return clazz;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		String url = getConnectionUrl();
		result = prime * result + ((url == null) ? 0 : url.hashCode());
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
		AbstractConnectionCreator<C, E> other = (AbstractConnectionCreator<C, E>) obj;
		String url = getConnectionUrl();
		String otherUrl = other.getConnectionUrl();
		if (url == null) {
			if (otherUrl != null)
				return false;
		} else if (!url.equals(otherUrl))
			return false;
		return true;
	}

}
