package kxd.net.adapters.http;

import java.io.IOException;
import java.net.Proxy;
import java.util.Date;

import kxd.net.NetRequest;
import kxd.net.NetResponse;
import kxd.net.adapters.NetAdapter;
import kxd.net.adapters.NetAdapterException;
import kxd.net.connection.http.HttpConnection;


/**
 * 基于HTTP的适配器
 * 
 * @author zhaom
 * 
 * @param <S>
 * @param <R>
 */
abstract public class HttpAdatper<S extends NetRequest, R extends NetResponse>
		extends HttpConnection implements NetAdapter<S, R> {

	public HttpAdatper(String url, Proxy proxy) throws IOException {
		super(url, proxy);
	}

	public HttpAdatper(String url) throws IOException {
		super(url);
	}

	@Override
	public boolean isAvailable() {
		return true;
	}

	abstract public R doExecute(S data) throws NetAdapterException,
			InterruptedException;

	@Override
	public R execute(S data) throws NetAdapterException, InterruptedException {
		data.setSendTime(new Date());
		R r = doExecute(data);
		r.setRecvTime(new Date());
		return r;
	}

}
