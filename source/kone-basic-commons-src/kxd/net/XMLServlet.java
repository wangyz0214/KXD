package kxd.net;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/**
 * 执行XML交易指令的Servlet
 * <p>
 * 本Servler通常用Ajax或其他的与XML返回相关的HTML交易中。通过调用<code>XMLTradeManager.trade()</code>
 * 来写成一个交易
 * </p>
 * 
 * @author 赵明
 * @see kxd.web.servlets#XMLTradeManager
 * @version 1.0
 * 
 */
public class XMLServlet extends HttpServlet {
	private static final long serialVersionUID = 6132775917766784894L;
	static Logger logger = Logger.getLogger(XMLServlet.class);

	public XMLServlet() {
		super();
	}

	public void destroy() {
		super.destroy();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/xml");
		try {
			logger.debug("xmlservlet: " + request.getRequestURI() + "?"
					+ request.getQueryString());
			XMLExecutorManager.execute(
					new HttpRequest(request, "utf-8", false), response);
		} catch (NoSuchFieldException e) {
			if (logger.isDebugEnabled())
				e.printStackTrace();
		} catch (InstantiationException e) {
			if (logger.isDebugEnabled())
				e.printStackTrace();
		} catch (IllegalAccessException e) {
			if (logger.isDebugEnabled())
				e.printStackTrace();
		}
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	public void init() throws ServletException {
	}

}
