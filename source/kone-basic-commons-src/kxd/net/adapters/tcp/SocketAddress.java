package kxd.net.adapters.tcp;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public class SocketAddress extends InetSocketAddress {
	private static final long serialVersionUID = 1L;
	int enabledConnectionCount;

	public SocketAddress(int port, int enabledConnectionCount) {
		super(port);
		this.enabledConnectionCount = enabledConnectionCount;
	}

	public SocketAddress(InetAddress addr, int port, int enabledConnectionCount) {
		super(addr, port);
		this.enabledConnectionCount = enabledConnectionCount;
	}

	public SocketAddress(String hostname, int port, int enabledConnectionCount) {
		super(hostname, port);
		this.enabledConnectionCount = enabledConnectionCount;
	}

	public int getEnabledConnectionCount() {
		return enabledConnectionCount;
	}

}
