package kxd.net.adapters.tcp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import kxd.net.connection.tcp.SocketStream;

public class SyncSocketStream extends SocketStream {
	private final InetSocketAddress addr;
	private final int connectTimeout;

	public SyncSocketStream(InetSocketAddress addr, int connectTimeout) {
		super(null);
		this.addr = addr;
		this.connectTimeout = connectTimeout;
	}

	public void connect() throws IOException {
		try {
			if (isConnected())
				return;
		} catch (IOException e2) {
			close();
		}
		socket = new Socket();
		try {
			socket.connect(addr, connectTimeout);
		} catch (IOException e) {
			close();
			throw e;
		}

	}
}
