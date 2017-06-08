package kxd.net.adapters;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import kxd.net.NetRequest;
import kxd.net.NetResponse;
import kxd.net.connection.ConnectionPoolListMap;
import kxd.net.connection.LoopConnectionPoolList;
import kxd.net.connection.PooledConnection;

/**
 * 基于轮循式分布式访问的连接池适配器
 * 
 * @author zhaom
 * @since 4.1
 * 
 * @param <S>
 *            发送包类
 * @param <R>
 *            接收包类
 */
abstract public class LoopPooledAdapter<S extends NetRequest, R extends NetResponse, C extends PooledConnection<E>, E extends Throwable>
		implements NetAdapter<S, R> {
	static ConcurrentHashMap<String, Object> maps = new ConcurrentHashMap<String, Object>();
	static ReentrantLock lock = new ReentrantLock();

	/**
	 * 添加一个连接池列表
	 * 
	 * @param group
	 *            连接池组名，该名称全局定义，取组名，需要注意不要与其他连接池适配器重复，最好用类似包的方式取名，确保不会重复
	 * @param poolsName
	 *            一个组内的连接池列表名称
	 * @param watchInterval
	 *            心跳监视间隔，以秒为单位
	 * @param pools
	 *            连接池列表
	 */
	@SuppressWarnings("unchecked")
	static public void addPools(String group, String poolsName,
			int watchInterval, LoopConnectionPoolList<?, ?> pools) {
		lock.lock();
		try {
			ConnectionPoolListMap<LoopConnectionPoolList<?, ?>> map = (ConnectionPoolListMap<LoopConnectionPoolList<?, ?>>) maps
					.get(group);
			if (map == null) {
				map = new ConnectionPoolListMap<LoopConnectionPoolList<?, ?>>(
						watchInterval);
				maps.put(group, map);
			}
			map.put(poolsName, pools);
		} finally {
			lock.unlock();
		}
	}

	@SuppressWarnings("unchecked")
	static public LoopConnectionPoolList<?, ?> removePools(String group,
			String poolsName) {
		lock.lock();
		try {
			ConnectionPoolListMap<LoopConnectionPoolList<?, ?>> map = (ConnectionPoolListMap<LoopConnectionPoolList<?, ?>>) maps
					.get(group);
			if (map == null)
				return null;
			return map.remove(poolsName);
		} finally {
			lock.unlock();
		}
	}

	@SuppressWarnings("unchecked")
	static public LoopConnectionPoolList<?, ?> getPools(String group,
			String poolsName) {
		ConnectionPoolListMap<LoopConnectionPoolList<?, ?>> map = (ConnectionPoolListMap<LoopConnectionPoolList<?, ?>>) maps
				.get(group);
		if (map == null)
			return null;
		return map.get(poolsName);
	}

	private LoopConnectionPoolList<C, E> pools;

	/**
	 * 构建一个适配器
	 * 
	 * @param group
	 *            连接池组名，该名称全局定义，取组名，需要注意不要与其他连接池适配器重复，最好用类似包的方式取名，确保不会重复
	 * @param poolsName
	 *            一个组内的连接池列表名称
	 */
	@SuppressWarnings("unchecked")
	public LoopPooledAdapter(String group, String poolsName) {
		this.pools = (LoopConnectionPoolList<C, E>) getPools(group, poolsName);
	}

	public LoopPooledAdapter(LoopConnectionPoolList<C, E> pools) {
		super();
		this.pools = pools;
	}

	/**
	 * 获取连接池连接列表
	 * 
	 */
	public LoopConnectionPoolList<C, E> getPools() {
		return pools;
	}

	/**
	 * 执行具体的接口
	 * 
	 * @param con
	 *            用于接口处理的连接
	 * @param data
	 *            请求数据
	 * @return 返回数据
	 */
	public abstract R doExecute(C con, S data) throws NetAdapterException;

	@Override
	public R execute(S data) throws NetAdapterException, InterruptedException {
		C con = null;
		try {
			try {
				con = pools.getConnection();
				con.open();
			} catch (InterruptedException e) {
				throw e;
			} catch (Throwable e) {
				throw new NetAdapterException(NetAdapterResult.NOTARRIVALED, e);
			}
			data.setSendTime(new Date());
			R ret = doExecute(con, data);
			ret.setRecvTime(new Date());
			return ret;
		} finally {
			try {
				if (con != null)
					con.close();
			} catch (Throwable e) {
			}
		}
	}
}
