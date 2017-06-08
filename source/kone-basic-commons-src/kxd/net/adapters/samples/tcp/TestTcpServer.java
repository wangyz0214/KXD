package kxd.net.adapters.samples.tcp;

import java.io.IOException;

import kxd.net.adapters.samples.SamplePacket;
import kxd.util.server.WorkServer;
import kxd.util.server.WorkTask;
import kxd.util.stream.SocketStream;

public class TestTcpServer extends WorkServer {

	@Override
	public WorkTask createTask() {
		return new TestTcpWorkTask();
	}

	static public void main(String[] args) {
		new TestTcpServer().start("TCPTESTSERVER", 8999, 1, 300, 1000, 300);
		new TestTcpServer().start("TCPTESTSERVER", 9000, 1, 300, 1000, 300);
	}
}

class TestTcpWorkTask extends WorkTask {

	public void run() {
		if (runStatus.get() >= TERMINATING) // 如果已经被外部终止，则直接返回
			return;
		SocketStream stream = null;
		Throwable ex = null;
		try {
			stream = new SocketStream(socket);
			while (true) {
				runStatus.set(PREARING);
				executeBefore(stream);
				runStatus.set(RUNING);
				execute(stream);
				socket.getOutputStream().flush();
			}
		} catch (Throwable e) {
			ex = e;
		} finally {
			executeAfter(stream, ex);
			try {
				logger.debug(socket + " closed.");
				socket.close();
			} catch (Throwable e) {
			}
			this.runStatus.set(TERMINATED);
		}
	}

	@Override
	public void execute(SocketStream stream) throws IOException,
			InterruptedException {
		SamplePacket packet = new SamplePacket();
		logger.debug("executing...");
		packet.read(stream, 300000);
		logger.debug(packet.getData());
		packet.setData(packet.getData().toUpperCase());
		packet.write(stream, 30000);
		logger.debug("send message[" + packet.getData() + "] success.");
	}

}