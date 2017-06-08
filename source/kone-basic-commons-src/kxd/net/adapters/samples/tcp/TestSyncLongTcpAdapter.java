package kxd.net.adapters.samples.tcp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import kxd.net.adapters.IdGenerator;
import kxd.net.adapters.NetAdapterException;
import kxd.net.adapters.NetAdapterResult;
import kxd.net.adapters.NetPack;
import kxd.net.adapters.NetResponsePack;
import kxd.net.adapters.TimeoutCleanHashMap;
import kxd.net.adapters.samples.SamplePacket;
import kxd.net.adapters.tcp.SyncQueueTcpAdapter;
import kxd.net.adapters.tcp.TcpAdapterParams;
import kxd.net.connection.tcp.SocketStream;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class TestSyncLongTcpAdapter extends
		SyncQueueTcpAdapter<Integer, SamplePacket, SamplePacket> {
	static Logger logger = Logger.getLogger(TestSyncLongTcpAdapter.class);

	public TestSyncLongTcpAdapter(
			TcpAdapterParams<Integer, SamplePacket, SamplePacket> params) {
		super(params);
	}

	@Override
	protected SamplePacket doExecute(Integer id, SocketStream stream,
			SamplePacket req) throws NetAdapterException {
		try {
			req.write(stream, 30000);
			req.read(stream, 30000);
		} catch (NetAdapterException e) {
			throw e;
		} catch (IOException e) {
			throw new NetAdapterException(NetAdapterResult.TIMEOUT, e);
		}
		return req;
	}

	@Override
	protected void doKeepAlive(SocketStream stream) throws NetAdapterException {
		SamplePacket packet = new SamplePacket("keepalive");
		try {
			packet.write(stream, 30000);
			packet.read(stream, 30000);
			logger.debug("keepalive");
			this.putRequest(this.getNextId(), packet);
		} catch (IOException e) {
			throw new NetAdapterException(NetAdapterResult.TIMEOUT, e);
		}
	}

	@Override
	public boolean isAvailable() {
		return true;
	}

	public static void main(String[] args) {
		PropertyConfigurator
				.configure("E:\\work\\KXD\\project\\china-unicom\\log4j.properties");
		IdGenerator<Integer> idGenerator = new IdGenerator<Integer>() {
			AtomicInteger serial = new AtomicInteger(0);

			@Override
			public Integer getNextId() {
				Integer r = serial.addAndGet(1);
				if (r >= Integer.MAX_VALUE)
					serial.set(0);
				TestSyncLongTcpAdapter.logger.debug(r);
				return r;
			}
		};
		ArrayBlockingQueue<NetPack<Integer, SamplePacket>> requestQueue = new ArrayBlockingQueue<NetPack<Integer, SamplePacket>>(
				10000);
		TimeoutCleanHashMap<Integer, NetResponsePack<Integer, SamplePacket>> responses = new TimeoutCleanHashMap<Integer, NetResponsePack<Integer, SamplePacket>>(
				logger, 30000);
		TcpAdapterParams<Integer, SamplePacket, SamplePacket> params = new TcpAdapterParams<Integer, SamplePacket, SamplePacket>(
				new InetSocketAddress("127.0.0.1", 8999), 30000, 60000, 30000,
				idGenerator, responses, requestQueue);
		TestSyncLongTcpAdapter adapter = new TestSyncLongTcpAdapter(params);
		for (int i = 0; i < 30; i++) {
			new Thread(new TestRunnable(adapter)).start();
		}
	}

	@Override
	public Logger getLogger() {
		return logger;
	}
}

class TestRunnable implements Runnable {
	TestSyncLongTcpAdapter adapter;

	public TestRunnable(TestSyncLongTcpAdapter adapter) {
		this.adapter = adapter;
	}

	@Override
	public void run() {
		SamplePacket p = new SamplePacket("testdata");
		for (int i = 0; i < 200; i++) {
			try {
				TestSyncLongTcpAdapter.logger.debug(i + "-"
						+ adapter.execute(p).getData());
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

}
