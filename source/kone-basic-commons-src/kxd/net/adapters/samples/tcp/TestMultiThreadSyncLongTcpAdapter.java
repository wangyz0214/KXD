package kxd.net.adapters.samples.tcp;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import kxd.net.adapters.IdGenerator;
import kxd.net.adapters.NetAdapterException;
import kxd.net.adapters.NetAdapterResult;
import kxd.net.adapters.NetPack;
import kxd.net.adapters.NetResponsePack;
import kxd.net.adapters.TimeoutCleanHashMap;
import kxd.net.adapters.samples.SamplePacket;
import kxd.net.adapters.tcp.MultiThreadSyncQueueTcpAdapter;
import kxd.net.adapters.tcp.MultiThreadTcpAdapterParams;
import kxd.net.adapters.tcp.SocketAddress;
import kxd.net.connection.tcp.SocketStream;

import org.apache.log4j.Logger;

public class TestMultiThreadSyncLongTcpAdapter extends
		MultiThreadSyncQueueTcpAdapter<Integer, SamplePacket, SamplePacket> {
	static Logger logger = Logger
			.getLogger(TestMultiThreadSyncLongTcpAdapter.class);

	public TestMultiThreadSyncLongTcpAdapter(
			MultiThreadTcpAdapterParams<Integer, SamplePacket, SamplePacket> params) {
		super(params);
	}

	@Override
	protected SamplePacket doExecute(Integer id, SocketStream stream,
			SamplePacket req) throws NetAdapterException {
		try {
			req.setId(id);
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
		IdGenerator<Integer> idGenerator = new IdGenerator<Integer>() {
			AtomicInteger serial = new AtomicInteger(0);

			@Override
			public Integer getNextId() {
				Integer r = serial.addAndGet(1);
				if (r >= Integer.MAX_VALUE)
					serial.set(0);
				TestMultiThreadSyncLongTcpAdapter.logger.debug(r);
				return r;
			}
		};
		ArrayBlockingQueue<NetPack<Integer, SamplePacket>> requestQueue = new ArrayBlockingQueue<NetPack<Integer, SamplePacket>>(
				10000);
		TimeoutCleanHashMap<Integer, NetResponsePack<Integer, SamplePacket>> responses = new TimeoutCleanHashMap<Integer, NetResponsePack<Integer, SamplePacket>>(
				logger, 30000);
		MultiThreadTcpAdapterParams<Integer, SamplePacket, SamplePacket> params = new MultiThreadTcpAdapterParams<Integer, SamplePacket, SamplePacket>(
				30000, 60000, 30000, idGenerator, responses, requestQueue);
		params.getAddressList().add(new SocketAddress("127.0.0.1", 8999, 30));
		params.getAddressList().add(new SocketAddress("127.0.0.1", 9000, 30));
		TestMultiThreadSyncLongTcpAdapter adapter = new TestMultiThreadSyncLongTcpAdapter(
				params);
		for (int i = 0; i < 30; i++) {
			new Thread(new TestMultiThreadSyncRunnable(adapter)).start();
		}
	}

	@Override
	public Logger getLogger() {
		return logger;
	}
}

class TestMultiThreadSyncRunnable implements Runnable {
	TestMultiThreadSyncLongTcpAdapter adapter;

	public TestMultiThreadSyncRunnable(TestMultiThreadSyncLongTcpAdapter adapter) {
		this.adapter = adapter;
	}

	@Override
	public void run() {
		SamplePacket p = new SamplePacket("testdata");
		for (int i = 0; i < 200; i++) {
			try {
				TestMultiThreadSyncLongTcpAdapter.logger.debug(i + "-"
						+ adapter.execute(p).getData());
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

}
