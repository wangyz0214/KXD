package kxd.engine.scs.web.ui;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

public class EngineServlet extends HttpServlet {

	static Logger logger = Logger.getLogger(EngineServlet.class);
	private static final long serialVersionUID = 1L;

	public static void unload() {
	}

	public EngineServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest hrequest,
			HttpServletResponse response) throws ServletException, IOException {
		System.out.println(this.getServletContext().getRealPath("/"));
		String url = hrequest.getRequestURI();
		String ctxPath = hrequest.getContextPath();
		if (ctxPath != null) {
			if (url.startsWith(ctxPath)) {
				url = url.substring(ctxPath.length());
			}
		}
		int index = url.lastIndexOf(".go");
		if (index > -1) {
			url = url.substring(0, index) + ".jsp";
		}
		RequestDispatcher rd = getServletContext().getRequestDispatcher(url);
		if (rd == null) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"创建转发对象失败");
			return;
		}
		rd.forward(hrequest, response);
	}
}
