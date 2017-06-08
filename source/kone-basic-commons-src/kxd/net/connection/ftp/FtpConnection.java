package kxd.net.connection.ftp;

import java.io.IOException;

import kxd.net.client.FtpClient;
import kxd.net.connection.Connection;

public class FtpConnection extends FtpClient implements Connection<IOException> {

	public FtpConnection(String host, int port, String user, String pwd) {
		super(host, port, user, pwd);
	}

	@Override
	public void open() throws IOException {
		connect();
	}

	@Override
	public void close() throws IOException {
		close();
	}

}
