package kxd.net.connection.ftp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import kxd.net.connection.ConnectionCreator;
import kxd.net.connection.ConnectionPool;
import kxd.net.connection.PooledConnection;

import org.apache.log4j.Logger;

public class PooledFtpConnection extends FtpConnection implements
		PooledConnection<IOException> {
	ConnectionPool<? extends PooledConnection<IOException>, IOException> connectionPool;
	private volatile long lastAliveTime = System.currentTimeMillis();
	private final static Logger logger = Logger
			.getLogger(PooledFtpConnection.class);

	public PooledFtpConnection(String host, int port, String user, String pwd) {
		super(host, port, user, pwd);
	}

	public PooledFtpConnection() {
		super(null, 0, null, null);
	}

	@Override
	public void connectionCreated(
			ConnectionCreator<? extends PooledConnection<IOException>, IOException> creator)
			throws IOException {
		connectionPool = creator.getConnectionPool();
		FtpConnectionCreator<? extends PooledConnection<IOException>> c = (FtpConnectionCreator<? extends PooledConnection<IOException>>) creator;
		this.host = c.getHost();
		this.user = c.getUser();
		this.pwd = c.getPwd();
		this.port = c.getPort();
	}

	@Override
	public ConnectionPool<? extends PooledConnection<IOException>, IOException> getConnectionPool() {
		return connectionPool;
	}

	@Override
	public void keepAlive() throws IOException {
		ftp.pwd();
	}

	@Override
	public long getLastAliveTime() {
		return lastAliveTime;
	}

	@Override
	public void setLastAliveTime(long time) {
		lastAliveTime = time;
	}

	@Override
	public void dispose() throws IOException {
		try {
			close();
		} finally {
			connectionPool.removeConnection(this);
		}
	}

	@Override
	public void close() {
		connectionPool.returnConnection(this);
	}

	@Override
	public void open() throws IOException {
		if (isConnected())
			return;
		try {
			super.open();
			logger.info(this + " open [" + host + ":" + port + "]");
			setLastAliveTime(System.currentTimeMillis());
			getConnectionPool().setAvailable(true);
			getConnectionPool().setLastAliveTime(lastAliveTime);
		} catch (IOException e) {
			disconnect();
			getConnectionPool().connectionFailure(this, e);
			throw e;
		}
	}

	@Override
	public long lastModified(String remoteFile) throws IOException {
		try {
			return super.lastModified(remoteFile);
		} catch (IOException e) { // 出问题后，重连接再试
			super.disconnect();
			return super.lastModified(remoteFile);
		}

	}

	@Override
	public long[] lastModifiedAndSize(String remoteFile) throws IOException {
		try {
			return super.lastModifiedAndSize(remoteFile);
		} catch (IOException e) {
			super.disconnect();
			return super.lastModifiedAndSize(remoteFile);
		}
	}

	@Override
	public void download(String remoteFile, OutputStream stream)
			throws IOException {
		try {
			super.download(remoteFile, stream);
		} catch (IOException e) {
			super.disconnect();
			super.download(remoteFile, stream);
		}
	}

	@Override
	public void mkdirs(String remotePath) throws IOException {
		try {
			super.mkdirs(remotePath);
		} catch (IOException e) {
			super.disconnect();
			super.mkdirs(remotePath);
		}
	}

	@Override
	public void upload(String remoteFile, InputStream stream)
			throws IOException {
		try {
			super.upload(remoteFile, stream);
		} catch (IOException e) {
			super.disconnect();
			super.upload(remoteFile, stream);
		}
	}

	@Override
	public void delete(String remoteFile) throws IOException {
		try {
			super.delete(remoteFile);
		} catch (IOException e) {
			super.disconnect();
			super.delete(remoteFile);
		}
	}

}
