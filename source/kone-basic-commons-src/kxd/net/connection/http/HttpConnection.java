package kxd.net.connection.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.util.HashMap;

import kxd.net.connection.Connection;

public class HttpConnection implements Connection<IOException>,
		HttpConnectionEventListener {
	private volatile int connectTimeout;
	private volatile int readTimeout;
	private HttpURLConnection urlConnection;
	private URL url;
	private Proxy proxy;
	private HashMap<String, String> headers = new HashMap<String, String>();

	public HttpConnection() {

	}

	public HttpConnection(String url) throws IOException {
		this.url = new URL(null, url, new HttpURLHandler(this));
	}

	public HttpConnection(String url, Proxy proxy) throws IOException {
		this.proxy = proxy;
		this.url = new URL(null, url, new HttpURLHandler(this));
	}

	@Override
	public void open() throws IOException {
		if (proxy != null)
			url.openConnection(proxy);
		else
			url.openConnection();
	}

	@Override
	public void close() throws IOException {
		if (urlConnection != null) {
			urlConnection.disconnect();
			urlConnection = null;
		}
	}

	@Override
	public boolean isConnected() {
		return urlConnection != null;
	}

	@Override
	public void connectionOpened(HttpURLConnection con) throws IOException {
		con.setConnectTimeout(connectTimeout);
		con.setReadTimeout(readTimeout);
		this.urlConnection = con;
	}

	public int getConnectTimeout() {
		return connectTimeout;
	}

	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public int getReadTimeout() {
		return readTimeout;
	}

	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}

	public URL getUrl() {
		return url;
	}

	public void setUrl(URL url) {
		this.url = url;
	}

	public HttpURLConnection getUrlConnection() {
		return urlConnection;
	}

	public HashMap<String, String> getHeaders() {
		return headers;
	}

	/**
	 * 提交数据
	 * 
	 * @param data
	 *            要提交的数据
	 * @throws IOException
	 */
	public ByteArrayOutputStream post(byte[] data) throws IOException {
		open();
		try {
			for (String k : headers.keySet())
				urlConnection.addRequestProperty(k, headers.get(k));
			urlConnection.setRequestMethod("POST");
			urlConnection.setDoOutput(true);
			urlConnection.setDoInput(true);
			// urlConnection.s
			urlConnection.getOutputStream().write(data);
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			byte[] b = new byte[1024];
			int len;
			while ((len = urlConnection.getInputStream().read(b)) > 0) {
				os.write(b, 0, len);
			}
			return os;
		} finally {
			close();
		}
	}
}
