package kxd.net.connection.webservice;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import kxd.net.connection.http.HttpConnection;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class WebServiceConnection extends HttpConnection {
	private static MessageFactory messageFactory;
	private Map<String, SOAPElement> idElementMap = new HashMap<String, SOAPElement>();

	protected WebServiceConnection() {
		super();
	}

	public WebServiceConnection(String url) throws IOException {
		super(url);
	}

	public WebServiceConnection(String url, Proxy proxy) throws IOException {
		super(url, proxy);
	}

	public Map<String, SOAPElement> getIdElementMap() {
		return idElementMap;
	}

	static public void setMessageFactory(MessageFactory v) {
		messageFactory = v;
	}

	static public void setAxisMessageFactory() {
		messageFactory = new org.apache.axis2.saaj.MessageFactoryImpl();
	}

	static public void setSunMessageFactory() {
		messageFactory = new com.sun.xml.internal.messaging.saaj.soap.ver1_1.SOAPMessageFactory1_1Impl();
	}

	private synchronized MessageFactory getMessageFactory()
			throws SOAPException {
		if (messageFactory == null) {
			setAxisMessageFactory();
			// messageFactory = MessageFactory.newInstance();
		}
		return messageFactory;
	}

	public SOAPMessage createSOAPMessage() throws IOException {
		try {
			return getMessageFactory().createMessage();
		} catch (SOAPException e) {
			throw new IOException(e);
		}

	}

	public SOAPMessage createSOAPMessage(MimeHeaders headers, InputStream stream)
			throws IOException {
		try {
			return getMessageFactory().createMessage(headers, stream);
		} catch (SOAPException e) {
			throw new IOException(e);
		}

	}

	public void getIdElements(SOAPElement el, Map<String, SOAPElement> map) {
		Iterator<?> it = el.getChildElements();
		while (it.hasNext()) {
			Object o = it.next();
			if (o instanceof SOAPElement) {
				SOAPElement e = (SOAPElement) o;
				Iterator<?> it1 = e.getAllAttributes();
				while (it1.hasNext()) {
					Name n = (Name) it1.next();
					if (n.getLocalName().equals("id")) {
						map.put(e.getAttributeValue(n), e);
					}
				}
				getIdElements(e, map);
			}
		}
	}

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
	public SOAPMessage call(SOAPMessage msg, String action) throws IOException,
			SOAPException {
		open();
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
			try {
				SOAPMessage rmsg = createSOAPMessage(null, con.getInputStream());
				if (rmsg.getSOAPBody().hasFault()) {
					throw new IOException(rmsg.getSOAPBody().getFault()
							.getFaultString());
				}
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
	public SOAPMessage call(String action) throws IOException, SOAPException {
		open();
		HttpURLConnection con = getUrlConnection();
		try {
			if (action != null)
				getHeaders().put("SOAPAction", action);
			else
				getHeaders().put("SOAPAction", "");
			for (String k : getHeaders().keySet())
				con.addRequestProperty(k, getHeaders().get(k));
			con.setDoInput(true);
			try {
				SOAPMessage rmsg = createSOAPMessage(null, con.getInputStream());
				if (rmsg.getSOAPBody().hasFault()) {
					throw new IOException(rmsg.getSOAPBody().getFault()
							.getFaultString());
				}
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

	public SOAPElement getElementByHref(SOAPElement el, String id) {
		Iterator<?> it = el.getChildElements();
		while (it.hasNext()) {
			Object o = it.next();
			if (o instanceof SOAPElement) {
				SOAPElement e = (SOAPElement) o;
				Iterator<?> it1 = e.getAllAttributes();
				while (it1.hasNext()) {
					Name n = (Name) it1.next();
					if (n.getLocalName().equals("id")) {
						if (e.getAttributeValue(n).equals(id))
							return e;
					}
				}
				e = getElementByHref(e, id);
				if (e != null)
					return e;
			}
		}
		return null;
	}

	public SOAPElement getSOAPHrefElement(SOAPBody body, SOAPElement el) {
		Iterator<?> it = el.getAllAttributes();
		while (it.hasNext()) {
			Name n = (Name) it.next();
			if (n.getLocalName().equals("href")) {
				String value = el.getAttributeValue(n).trim();
				if (value.startsWith("#")) {
					value = value.substring(1);
					return getElementByHref(body, value);
				}
				break;
			}
		}
		return el;
	}

	/**
	 * 获取节点下的第一个名为name节点
	 * 
	 * @param el
	 *            父结点
	 * @param name
	 *            子结点Tag
	 * @return
	 */
	public SOAPElement getFirstElement(SOAPBody body, String name) {
		NodeList ls = body.getElementsByTagName(name);
		for (int i = 0; i < ls.getLength(); i++) {
			Node node = ls.item(i);
			if (node instanceof SOAPElement) {
				return getSOAPHrefElement(body, (SOAPElement) node);
			}
		}
		return null;
	}

	public SOAPElement getFirstElement(SOAPBody body, String name,
			String namespaceURI) {
		NodeList ls = body.getElementsByTagNameNS(namespaceURI, name);
		for (int i = 0; i < ls.getLength(); i++) {
			Node node = ls.item(i);
			if (node instanceof SOAPElement) {
				return getSOAPHrefElement(body, (SOAPElement) node);
			}
		}
		return null;
	}

}
