package kxd.net.adapters.tcp;

import java.io.IOException;

import kxd.net.NetRequest;
import kxd.net.NetResponse;
import kxd.net.adapters.LoopPooledAdapter;
import kxd.net.connection.LoopConnectionPoolList;
import kxd.net.connection.tcp.PooledTcpConnection;

/**
 * 基于轮循式分布式访问的TCP连接池适配器
 * 
 * @author zhaom
 * 
 * @param <S>
 * @param <R>
 * @param <C>
 */
abstract public class LoopPooledTcpAdapter<S extends NetRequest, R extends NetResponse, C extends PooledTcpConnection>
		extends LoopPooledAdapter<S, R, C, IOException> {

	public LoopPooledTcpAdapter(LoopConnectionPoolList<C, IOException> pools) {
		super(pools);
	}

	public LoopPooledTcpAdapter(String group, String poolsName) {
		super(group, poolsName);
	}

	@Override
	public boolean isAvailable() {
		return true;
	}

}
