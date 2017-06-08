package kxd.net.connection.http;

import java.io.IOException;
import java.net.HttpURLConnection;

public interface HttpConnectionEventListener {
	/**
	 * 连接打开
	 * 
	 * @param con
	 *            打开的连接
	 */
	public void connectionOpened(HttpURLConnection con) throws IOException;
}
