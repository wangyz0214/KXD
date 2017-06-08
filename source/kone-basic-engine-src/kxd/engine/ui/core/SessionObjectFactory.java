package kxd.engine.ui.core;

import javax.servlet.http.HttpServletResponse;

import kxd.net.HttpRequest;

/**
 * 会话对象工厂接口，用于生成具体的会话对象
 * 
 * @author zhaom
 * @version 4.1
 */
public interface SessionObjectFactory {
	/**
	 * 获取一个新的SessionObject对象
	 * 
	 * @param request
	 *            http请求对象
	 * @param response
	 *            http响应对象
	 */
	public SessionObject newInstance(HttpRequest request,
			HttpServletResponse response);
}
