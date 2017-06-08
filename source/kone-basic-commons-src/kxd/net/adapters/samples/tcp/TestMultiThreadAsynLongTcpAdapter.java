package kxd.net.adapters.samples.tcp;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import kxd.net.adapters.IdGenerator;
import kxd.net.adapters.NetPack;
import kxd.net.adapters.NetResponsePack;
import kxd.net.adapters.TimeoutCleanHashMap;
import kxd.net.adapters.samples.SamplePacket;
import kxd.net.adapters.tcp.MultiThreadAsyncQueueTcpAdapter;
import kxd.net.adapters.tcp.MultiThreadTcpAdapterParams;
import kxd.net.adapters.tcp.SocketAddress;
import kxd.net.connection.tcp.SocketStream;

import org.apache.log4j.Logger;

public class TestMultiThreadAsynLongTcpAdapter extends
		MultiThreadAsyncQueueTcpAdapter<Integer, SamplePacket, SamplePacket> {
	static Logger logger = Logger
			.getLogger(TestMultiThreadAsynLongTcpAdapter.class);

	public TestMultiThreadAsynLongTcpAdapter(
			MultiThreadTcpAdapterParams<Integer, SamplePacket, SamplePacket> params) {
		super(params);
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
				TestMultiThreadAsynLongTcpAdapter.logger.debug(r);
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
		TestMultiThreadAsynLongTcpAdapter adapter = new TestMultiThreadAsynLongTcpAdapter(
				params);
		for (int i = 0; i < 30; i++) {
			new Thread(new TestMultiThreadAsyncRunnable(adapter)).start();
		}
	}

	@Override
	public Logger getLogger() {
		return logger;
	}

	@Override
	protected void doSend(SocketStream stream, Integer id, SamplePacket req)
			throws IOException {
		if (req == null) {
			// 心跳包
			req = new SamplePacket("alivedatatest");
		}
		req.setId(id);
		req.write(stream, 3000);
	}

	@Override
	protected SamplePacket doRecv(SocketStream stream) throws IOException {
		SamplePacket packet = new SamplePacket();
		packet.read(stream, 300000);
		if (!packet.getData().equalsIgnoreCase("alivedatatest"))
			return packet;
		else
			// 心跳包
			return null;
	}
}

class TestMultiThreadAsyncRunnable implements Runnable {
	TestMultiThreadAsynLongTcpAdapter adapter;

	public TestMultiThreadAsyncRunnable(
			TestMultiThreadAsynLongTcpAdapter adapter) {
		this.adapter = adapter;
	}

	@Override
	public void run() {
		SamplePacket p = new SamplePacket("testdata");
		for (int i = 0; i < 200; i++) {
			try {
				TestMultiThreadAsynLongTcpAdapter.logger.debug(i + "-"
						+ adapter.execute(p).getData());
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

}
