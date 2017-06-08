package kxd.net.adapters.tcp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.locks.ReentrantLock;

import kxd.net.connection.tcp.SocketStream;

public class AsynSocketStream extends SocketStream {
	private final InetSocketAddress addr;
	private final int connectTimeout;
	private final ReentrantLock lock = new ReentrantLock();

	public AsynSocketStream(InetSocketAddress addr, int connectTimeout) {
		super(null);
		this.addr = addr;
		this.connectTimeout = connectTimeout;
	}

	@Override
	public boolean isConnected() throws IOException {
		final ReentrantLock lock = this.lock;
		lock.lock();
		try {
			if (super.isConnected())
				return true;
			if (this.addr == null) {
				return false;
			} else {
				super.close();
				socket = new Socket();
				socket.connect(addr, connectTimeout);
				return true;
			}
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void close() {
		final ReentrantLock lock = this.lock;
		lock.lock();
		try {
			super.close();
		} finally {
			lock.unlock();
		}
	}

}
