package kxd.engine.ui.core;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 保存会话数据的对象
 * 
 * @author zhaom
 * 
 */
public interface SessionObject {
	/**
	 * 获取会话是否已经登录
	 */
	public boolean isLogined();

	/**
	 * 判断当前会话用户是否拥有某种权限
	 * 
	 * @param right
	 *            权限ID
	 * @return true - 具备该权限；false - 不具备
	 */
	public boolean hasRight(int right);

	/**
	 * 接管请求，对某些请求实现独有的请求处理流程
	 * 
	 * @param servlet
	 *            当前的servlet对象
	 * @param request
	 *            http请求
	 * @param response
	 *            http响应
	 * @return true - 表示当前请求已经被接管，默认处理将来被取消<br>
	 *         false - 表示需要按默认方式处理
	 */
	public boolean handleRequest(String url, ControlServlet servlet,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException;
}
