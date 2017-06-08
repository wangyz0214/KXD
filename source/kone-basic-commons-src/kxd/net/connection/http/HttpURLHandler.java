package kxd.net.connection.http;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;

import sun.net.www.protocol.http.Handler;

public class HttpURLHandler extends Handler {
	private HttpConnectionEventListener openedListener;

	public HttpURLHandler(HttpConnectionEventListener openedListener) {
		super();
		this.openedListener = openedListener;
	}

	@Override
	protected URLConnection openConnection(URL u, Proxy p) throws IOException {
		URLConnection con = super.openConnection(u, p);
		if (!(con instanceof HttpURLConnection))
			throw new IOException("不是一个HTTP连接");
		openedListener.connectionOpened((HttpURLConnection) con);
		return con;
	}

}
