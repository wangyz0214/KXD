package kxd.net;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import kxd.util.StringUnit;

public class HttpRequest {
	HttpServletRequest request;
	HashMap<String, String> parameters = new HashMap<String, String>();

	/**
	 * 创建一个请求对象。由于HttpServerletRequest类并不处理查询字串的字符集，设计本类解决此问题
	 * 
	 * @param request
	 *            http request object
	 * @param charsetEncoding
	 *            请求编码字符集
	 * @param setQueryParametersToAttributes
	 *            是否将URL中的查询字串设置同步复制到request的变量中
	 * @throws UnsupportedEncodingException
	 */
	public HttpRequest(HttpServletRequest request, String charsetEncoding,
			boolean setQueryParametersToAttributes)
			throws UnsupportedEncodingException {
		this.request = request;
		request.setCharacterEncoding(charsetEncoding);
		String qstr = request.getQueryString();
		if (qstr != null) { // 处理查询字串的编码
			String[] ls = StringUnit.split(qstr, "&");
			for (int i = 0; i < ls.length; i++) {
				String p = ls[i];
				if (p != null && p.length() > 0) {
					String[] pp = StringUnit.split(p, "=");
					String value = URLDecoder.decode(pp[1], charsetEncoding);
					parameters.put(pp[0], value);
					if (setQueryParametersToAttributes)
						request.setAttribute(pp[0], value);
				}
			}
		}
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	private String inGetParameter(String name) {
		String ret = parameters.get(name);
		if (ret == null)
			return request.getParameter(name);
		else
			return ret;
	}

	private Object inGetAttribute(String name) {
		return request.getAttribute(name);
	}

	public String getParameter(String name) throws NoSuchFieldException {
		String ret = inGetParameter(name);
		if (ret == null)
			throw new NoSuchFieldException("缺少参数[" + name + "]");
		else
			return ret;
	}

	public String getParameterDef(String name, String def) {
		String ret = inGetParameter(name);
		if (ret == null)
			return def;
		else
			return ret;
	}

	public Boolean getParameterBoolean(String name) throws NoSuchFieldException {
		String ret = inGetParameter(name);
		if (ret == null)
			throw new NoSuchFieldException("缺少参数[" + name + "]");
		else
			return Boolean.valueOf(ret);
	}

	public Boolean getParameterBooleanDef(String name, Boolean def) {
		String ret = inGetParameter(name);
		if (ret == null)
			return def;
		else {
			try {
				return Boolean.valueOf(ret);
			} catch (Throwable e) {
				return def;
			}
		}
	}

	public Byte getParameterByte(String name) throws NoSuchFieldException {
		String ret = inGetParameter(name);
		if (ret == null)
			throw new NoSuchFieldException("缺少参数[" + name + "]");
		else
			return Byte.valueOf(ret);
	}

	public Byte getParameterByteDef(String name, Byte def) {
		String ret = inGetParameter(name);
		if (ret == null)
			return def;
		else {
			try {
				return Byte.valueOf(ret);
			} catch (Throwable e) {
				return def;
			}
		}
	}

	public Short getParameterShort(String name) throws NoSuchFieldException {
		String ret = inGetParameter(name);
		if (ret == null)
			throw new NoSuchFieldException("缺少参数[" + name + "]");
		else
			return Short.valueOf(ret);
	}

	public Short getParameterShortDef(String name, Short def) {
		String ret = inGetParameter(name);
		if (ret == null)
			return def;
		else {
			try {
				return Short.valueOf(ret);
			} catch (Throwable e) {
				return def;
			}
		}
	}

	public Integer getParameterInt(String name) throws NoSuchFieldException {
		String ret = inGetParameter(name);
		if (ret == null)
			throw new NoSuchFieldException("缺少参数[" + name + "]");
		else
			return Integer.valueOf(ret);
	}

	public Integer getParameterIntDef(String name, Integer def) {
		String ret = inGetParameter(name);
		if (ret == null)
			return def;
		else {
			try {
				return Integer.valueOf(ret);
			} catch (Throwable e) {
				return def;
			}
		}
	}

	public Float getParameterFloat(String name) throws NoSuchFieldException {
		String ret = inGetParameter(name);
		if (ret == null)
			throw new NoSuchFieldException("缺少参数[" + name + "]");
		else
			return Float.valueOf(ret);
	}

	public Float getParameterFloatDef(String name, Float def) {
		String ret = inGetParameter(name);
		if (ret == null)
			return def;
		else {
			try {
				return Float.valueOf(ret);
			} catch (Throwable e) {
				return def;
			}
		}
	}

	public Double getParameterDouble(String name) throws NoSuchFieldException {
		String ret = inGetParameter(name);
		if (ret == null)
			throw new NoSuchFieldException("缺少参数[" + name + "]");
		else
			return Double.valueOf(ret);
	}

	public Double getParameterDoubleDef(String name, Double def) {
		String ret = inGetParameter(name);
		if (ret == null)
			return def;
		else {
			try {
				return Double.valueOf(ret);
			} catch (Throwable e) {
				return def;
			}
		}
	}

	public Long getParameterLong(String name) throws NoSuchFieldException {
		String ret = inGetParameter(name);
		if (ret == null)
			throw new NoSuchFieldException("缺少参数[" + name + "]");
		else
			return Long.valueOf(ret);
	}

	public Long getParameterLongDef(String name, Long def) {
		String ret = inGetParameter(name);
		if (ret == null)
			return def;
		else {
			try {
				return Long.valueOf(ret);
			} catch (Throwable e) {
				return def;
			}
		}
	}

	public Object getAttribute(String name) throws NoSuchFieldException {
		Object ret = inGetAttribute(name);
		if (ret == null)
			throw new NoSuchFieldException("缺少变量[" + name + "]");
		else
			return ret;
	}

	public Object getAttributeDef(String name, Object value)
			throws NoSuchFieldException {
		Object ret = inGetAttribute(name);
		if (ret == null)
			return value;
		else
			return ret;
	}

	public void setAttribute(String name, Object value) {
		request.setAttribute(name, value);
	}

	public Enumeration<?> getParameterNames() {
		return request.getParameterNames();
	}

	public Enumeration<?> getAttributeNames() {
		return request.getAttributeNames();
	}
}
