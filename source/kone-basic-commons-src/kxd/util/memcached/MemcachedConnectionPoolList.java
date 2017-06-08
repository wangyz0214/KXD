package kxd.util.memcached;

import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import kxd.net.connection.ConnectionPool;
import kxd.net.connection.HashConnectionPoolList;
import kxd.util.ExceptionCreator;

public class MemcachedConnectionPoolList extends
		HashConnectionPoolList<MemcachedConnection, IOException> {
	private static final long serialVersionUID = 1L;
	public Object localCache;
	public ConcurrentHashMap<String, String> configMap = new ConcurrentHashMap<String, String>();
	public volatile String executorClass;

	protected MemcachedConnectionPoolList(
			ExceptionCreator<IOException> exceptionCreator) {
		super(exceptionCreator);
		setAdjustForBadConnection(true);
	}

	/**
	 * 删除连接池列表中的所有服务器的缓存对象
	 * 
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public void flushAll() throws InterruptedException, IOException {
		Iterator<ConnectionPool<MemcachedConnection, IOException>> it = iterator();
		while (it.hasNext()) {
			MemcachedConnection con = it.next().getConnection();
			try {
				con.flushAll();
			} finally {
				con.close();
			}
		}
	}

}
