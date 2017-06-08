package kxd.net.connection;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

import kxd.util.ExceptionCreator;

/**
 * 连接池列表，用于分布式连接池调用，轮循访问不同的连接池
 * 
 * @param <C>
 */
public class LoopConnectionPoolList<C extends PooledConnection<E>, E extends Throwable>
		extends ConnectionPoolList<C, E> {
	private static final long serialVersionUID = 1L;
	private AtomicInteger index = new AtomicInteger(0);

	public LoopConnectionPoolList(ExceptionCreator<E> exceptionCreator) {
		super(exceptionCreator);
	}

	public LoopConnectionPoolList(ExceptionCreator<E> exceptionCreator,
			Collection<? extends ConnectionPool<C, E>> c) {
		super(exceptionCreator, c);
	}

	public LoopConnectionPoolList(ExceptionCreator<E> exceptionCreator,
			ConnectionPool<C, E>[] toCopyIn) {
		super(exceptionCreator, toCopyIn);
	}

	/**
	 * 轮循访问可用的连接池，并从连接池获取一个连接
	 * 
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public C getConnection() throws InterruptedException, E {
		int i = index.getAndAdd(1);
		if (i >= size()) {
			i = 0;
			index.set(1);
		}
		if (size() == 0) {
			throw exceptionCreator.createException("无可用的连接池", null);
		}
		return get(i).getConnection();
	}

	@Override
	protected void changed() {

	}
}
