package kxd.net.adapters.webservice;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.util.Date;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import kxd.net.NetResponse;
import kxd.net.SimpleNetResponse;
import kxd.net.adapters.NetAdapter;
import kxd.net.adapters.NetAdapterException;
import kxd.net.adapters.NetAdapterResult;
import kxd.net.connection.webservice.WebServiceConnection;
import kxd.net.connection.webservice.WebServiceException;

abstract public class WebServiceAdapter extends WebServiceConnection implements
		NetAdapter<WebServiceRequest, NetResponse> {

	public WebServiceAdapter(String url, Proxy proxy) throws IOException {
		super(url, proxy);
	}

	public WebServiceAdapter(String url) throws IOException {
		super(url);
	}

	@Override
	public boolean isAvailable() {
		return true;
	}

	private NetAdapterResult result = NetAdapterResult.NOTARRIVALED;
	private WebServiceRequest req;

	/**
	 * 通过XML形式调用WebService服务
	 * 
	 * @param msg
	 *            发送信息
	 * @param action
	 * @return
	 * @throws IOException
	 * @throws SOAPException
	 */
	@Override
	public SOAPMessage call(SOAPMessage msg, String action) throws IOException,
			SOAPException {
		open();
		req.setConnectedTime(new Date());
		result = NetAdapterResult.TIMEOUT;
		HttpURLConnection con = getUrlConnection();
		try {
			getHeaders().put("Content-Type", "text/xml;charset=utf-8");
			if (action != null)
				getHeaders().put("SOAPAction", action);
			else
				getHeaders().put("SOAPAction", "");
			for (String k : getHeaders().keySet())
				con.addRequestProperty(k, getHeaders().get(k));
			con.setRequestMethod("POST");
			con.setDoOutput(true);
			con.setDoInput(true);
			try {
				msg.writeTo(con.getOutputStream());
			} catch (SOAPException e) {
				throw new IOException(e);
			}
			if (req != null)
				req.setSendTime(new Date());
			try {
				SOAPMessage rmsg = createSOAPMessage(null, con.getInputStream());
				if (rmsg.getSOAPBody().hasFault()) {
					throw new IOException(rmsg.getSOAPBody().getFault()
							.getFaultString());
				}
				getIdElementMap().clear();
				getIdElements(rmsg.getSOAPBody(), getIdElementMap());
				return rmsg;
			} catch (IOException e) {
				if (con.getErrorStream() != null) {
					SOAPMessage message = createSOAPMessage(null,
							con.getErrorStream());
					try {
						throw new WebServiceException(message.getSOAPBody()
								.getFault().getFaultString(), message);
					} catch (SOAPException ex) {
						throw new IOException(ex);
					}
				}
				throw e;
			}
		} finally {
			close();
		}
	}

	/**
	 * 直接通过URL调用WebService接口
	 * 
	 * @param action
	 * @return
	 * @throws IOException
	 * @throws SOAPException
	 */
	@Override
	public SOAPMessage call(String action) throws IOException, SOAPException {
		open();
		req.setConnectedTime(new Date());
		result = NetAdapterResult.TIMEOUT;
		HttpURLConnection con = getUrlConnection();
		try {
			if (action != null)
				getHeaders().put("SOAPAction", action);
			else
				getHeaders().put("SOAPAction", "");
			for (String k : getHeaders().keySet())
				con.addRequestProperty(k, getHeaders().get(k));
			con.setDoInput(true);
			if (req != null)
				req.setSendTime(new Date());
			try {
				SOAPMessage rmsg = createSOAPMessage(null, con.getInputStream());
				if (rmsg.getSOAPBody().hasFault()) {
					result = NetAdapterResult.FAILURE;
					throw new IOException(rmsg.getSOAPBody().getFault()
							.getFaultString());
				}
				getIdElementMap().clear();
				getIdElements(rmsg.getSOAPBody(), getIdElementMap());
				return rmsg;
			} catch (IOException e) {
				if (con.getErrorStream() != null) {
					SOAPMessage message = createSOAPMessage(null,
							con.getErrorStream());
					try {
						result = NetAdapterResult.FAILURE;
						throw new WebServiceException(message.getSOAPBody()
								.getFault().getFaultString(), message);
					} catch (SOAPException ex) {
						throw new IOException(ex);
					}
				}
				throw e;
			}
		} finally {
			close();
		}
	}

	/**
	 * 执行一个WebService操作
	 * 
	 * @param data
	 *            请求数据
	 * @return 响应数据，为null，则表示不支持的操作
	 * @throws Exception
	 */
	abstract protected Object doExecute(WebServiceRequest data)
			throws Exception;

	@Override
	synchronized public SimpleNetResponse execute(WebServiceRequest data)
			throws NetAdapterException, InterruptedException {
		try {
			req = data;
			Object r = doExecute(data);
			if (r == null)
				throw new NetAdapterException(NetAdapterResult.FAILURE,
						"未知的WebService操作[" + data.getOperation() + "]");
			return new SimpleNetResponse(r, new Date());
		} catch (InterruptedException e) {
			throw e;
		} catch (NetAdapterException e) {
			throw e;
		} catch (Exception e) {
			throw new NetAdapterException(result, e);
		}
	}

}