package kxd.net.adapters.samples.tcp;

import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;

import kxd.net.adapters.NetAdapterException;
import kxd.net.adapters.NetAdapterResult;
import kxd.net.adapters.samples.SamplePacket;
import kxd.net.adapters.tcp.LoopPooledTcpAdapter;
import kxd.net.connection.CommonConnectionPool;
import kxd.net.connection.ConnectionPool;
import kxd.net.connection.LoopConnectionPoolList;
import kxd.net.connection.tcp.PooledTcpConnection;
import kxd.net.connection.tcp.SocketStream;
import kxd.net.connection.tcp.TcpConnectionCreator;
import kxd.util.ExceptionCreator;

import org.apache.log4j.Logger;

public class TestLoopPooledTcpAdapter extends
		LoopPooledTcpAdapter<SamplePacket, SamplePacket, PooledTcpConnection> {
	static Logger logger = Logger.getLogger(TestLoopPooledTcpAdapter.class);

	static public class MyTcpConnection extends PooledTcpConnection {
		ReentrantLock lock = new ReentrantLock();

		@Override
		public void keepAlive() throws IOException {
			lock.lock();
			try {
				SamplePacket packet = new SamplePacket("keepalive");
				try {
					packet.write(stream, 30000);
					packet.read(stream, 30000);
					TestLoopPooledTcpAdapter.logger.debug("keepalive");
				} catch (IOException e) {
					throw new NetAdapterException(NetAdapterResult.TIMEOUT, e);
				}
			} finally {
				lock.unlock();
			}
		}

		public SamplePacket execute(SamplePacket req) {
			lock.lock();
			try {
				try {
					SocketStream stream = getStream();
					logger.info(getConnectionPool() + " ==> " + stream + "["
							+ stream.getSocket() + "].");
					req.write(stream, 30000);
					stream.getSocket().getOutputStream().flush();
					req.read(stream, 30000);
				} catch (NetAdapterException e) {
					throw e;
				} catch (IOException e) {
					logger.info(this + "[" + getStream().getSocket()
							+ "] error.");
					try {
						this.dispose();
					} catch (Throwable ex) {
					}
					throw new NetAdapterException(NetAdapterResult.TIMEOUT, e);
				}
				return req;
			} finally {
				lock.unlock();
			}
		}
	}

	static {
		// 初始化连接池列表
		LoopConnectionPoolList<MyTcpConnection, IOException> pools = new LoopConnectionPoolList<MyTcpConnection, IOException>(
				new ExceptionCreator<IOException>() {
					@Override
					public IOException createException(String msg, Throwable t) {
						return new IOException(msg, t);
					}
				});
		TcpConnectionCreator<MyTcpConnection> creator = new TcpConnectionCreator<MyTcpConnection>(
				MyTcpConnection.class, "test", "127.0.0.1", 8999, 60000);
		creator.setSoTimeout(60000);
		creator.setNoDelay(true);
		creator.setDataTimeout(60000);
		ConnectionPool<MyTcpConnection, IOException> pool = new CommonConnectionPool<MyTcpConnection, IOException>(
				creator, 18);
		pools.add(pool);
		addPools("testgroup", "testpool", 60, pools);
	}

	public TestLoopPooledTcpAdapter() {
		super("testgroup", "testpool");
	}

	@Override
	public SamplePacket execute(SamplePacket data) throws NetAdapterException,
			InterruptedException {
		return super.execute(data);
	}

	@Override
	public SamplePacket doExecute(PooledTcpConnection con, SamplePacket req) {
		return ((MyTcpConnection) con).execute(req);
	}

	public static void main(String[] args) {
		for (int i = 0; i < 30; i++) {
			new Thread(new TTestRunnable()).start();
		}
	}

	@Override
	public Logger getLogger() {
		return logger;
	}
}

class TTestRunnable implements Runnable {

	public TTestRunnable() {
	}

	@Override
	public void run() {
		SamplePacket p = new SamplePacket("testdata");
		for (int i = 0; i < 200; i++) {
			try {
				p.setData("testdata_" + i);
				TestLoopPooledTcpAdapter.logger.debug(Thread.currentThread()
						+ "-"
						+ new TestLoopPooledTcpAdapter().execute(p).getData());
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

}
