package kxd.engine.ui.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import kxd.net.HttpRequest;
import kxd.util.DataUnit;
import kxd.util.KoneUtil;
import kxd.util.StringUnit;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

class ToRule {

	boolean redirect;
	String url;

	public ToRule(boolean redirect, String url) {
		super();
		this.redirect = redirect;
		this.url = url;
	}
}

class NavigateRule {
	public boolean saveQueryStringToAttribute;
	public String from;
	public String action;
	public String baseDir;
	public String firstBaseDir;
	public String actionClass;
	public boolean createActionAlways;
	public ConcurrentHashMap<String, ToRule> toRules = new ConcurrentHashMap<String, ToRule>();
}

public class ControlServlet extends HttpServlet {

	static Logger logger = Logger.getLogger(ControlServlet.class);
	private static final long serialVersionUID = 1L;
	static ConcurrentHashMap<String, NavigateRule> rules = new ConcurrentHashMap<String, NavigateRule>();
	public static String webRootPath;
	private static SessionObjectFactory sessionFactory;
	SessionObject session;

	private static void loadNavigateRules(Map<String, NavigateRule> rules,
			Document doc) {
		NodeList list = doc.getElementsByTagName("navigate-rule");
		for (int i = 0; i < list.getLength(); i++) {
			Element e = (Element) list.item(i);
			String from = e.hasAttribute("from") ? e.getAttribute("from")
					: null;
			String action = e.hasAttribute("action") ? e.getAttribute("action")
					: null;
			String actionClass = e.hasAttribute("actionClass") ? e
					.getAttribute("actionClass") : null;
			if (e.hasChildNodes() && !(from == null || from.trim().isEmpty())) {
				NavigateRule rule = rules.get(from);
				if (rule == null) {
					rule = new NavigateRule();
					rule.from = from;
					rules.put(from, rule);
					if (e.hasAttribute("basedir")) {
						rule.baseDir = e.getAttribute("basedir");
					} else
						rule.baseDir = "";
					rule.firstBaseDir = rule.baseDir;
				} else {
					if (e.hasAttribute("basedir")) {
						rule.baseDir = e.getAttribute("basedir");
					}
				}
				if (action != null)
					rule.action = action;
				if (actionClass != null)
					rule.actionClass = actionClass;
				if (e.hasAttribute("createActionAlways"))
					rule.createActionAlways = "true".equals(e
							.getAttribute("createActionAlways"));
				if (e.hasAttribute("saveQueryStringToAttribute"))
					rule.saveQueryStringToAttribute = "true".equals(e
							.getAttribute("saveQueryStringToAttribute"));
				NodeList ns = e.getElementsByTagName("to-rule");
				for (int j = 0; j < ns.getLength(); j++) {
					e = (Element) ns.item(j);
					String result = e.getAttribute("result");
					String to = e.getAttribute("to");
					if (!(result == null || result.trim().isEmpty()
							|| to == null || to.trim().isEmpty())) {
						String redirect = e.getAttribute("redirect");
						rule.toRules.put(result,
								new ToRule("true".equals(redirect), to));
					}
				}
			}
		}
	}

	public synchronized static void load(String configFile) {
		try {
			Map<String, NavigateRule> tempRules = new HashMap<String, NavigateRule>();
			String path = KoneUtil.getConfigPath();
			logger.debug("config file:[" + path + configFile + "]");
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(path + configFile);
			NodeList list = doc.getElementsByTagName("base");
			ArrayList<String> files = new ArrayList<String>();
			if (list.getLength() > 0) {
				Element e = (Element) list.item(0);
				String className = e.getAttribute("sessionfactoryclass");
				sessionFactory = (SessionObjectFactory) Class
						.forName(className).newInstance();
				NodeList ls = e.getElementsByTagName("customizefile");
				for (int i = 0; i < ls.getLength(); i++) {
					e = (Element) ls.item(i);
					files.add(e.getTextContent());
				}
			}
			loadNavigateRules(tempRules, doc);
			for (int i = 0; i < files.size(); i++) {
				File file = new File(path + files.get(i));
				if (file.exists()) {
					doc = builder.parse(file);
					loadNavigateRules(tempRules, doc);
				}
			}
			rules.clear();
			for (String key : tempRules.keySet()) {
				NavigateRule rule = tempRules.get(key);
				if (!rule.baseDir.isEmpty()) {
					Enumeration<ToRule> en = rule.toRules.elements();
					while (en.hasMoreElements()) {
						ToRule r = en.nextElement();
						r.url = rule.baseDir + r.url;
					}
				}
				if (rule.action != null && rule.action.trim().isEmpty())
					rule.action = null;
				if (rule.actionClass != null
						&& rule.actionClass.trim().isEmpty())
					rule.actionClass = null;
				rules.put(rule.baseDir + key, rule);
				rule.from = rule.baseDir + rule.from;
				if (!rule.baseDir.equals(rule.firstBaseDir))
					rules.put(rule.firstBaseDir + key, rule);
			}
			tempRules.clear();
			tempRules = null;
		} catch (Throwable e) {
			logger.error("Load config error:", e);
		}
	}

	public static void unload() {
		rules.clear();
	}

	public static boolean urlFileExists(String url) {
		File file = new File(webRootPath + url);
		return file.exists();
	}

	public void forward(HttpServletRequest request,
			HttpServletResponse response, String url, boolean redirect)
			throws IOException, ServletException {
		// logger.debug("forward to [" + url + "]");
		// if (false){//!urlFileExists(url)) {
		// response.sendError(HttpServletResponse.SC_BAD_REQUEST, "找不到文件["
		// + webRootPath + url + "]");
		// } else {
		if (redirect) {
			if (url.startsWith("/")) {
				url = request.getContextPath() + url;
			}
			if (url.endsWith(".jsp")) {
				url = url.substring(0, url.length() - 4) + ".go";
			} else if (url.indexOf(".jsp?") > 0) {
				url = url.replace(".jsp", ".go");
			}
			response.sendRedirect(url);
		} else {
			RequestDispatcher rd = getServletContext()
					.getRequestDispatcher(url);
			if (rd == null) {
				response.sendError(
						HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
						"创建转发对象失败");
				return;
			}
			rd.forward(request, response);
		}
		// }
	}

	public ControlServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest hrequest,
			HttpServletResponse response) throws ServletException, IOException {
		HttpRequest request = new HttpRequest(hrequest, "utf-8", false);
		request.setAttribute("fromkonefaces", true);
		// logger.debug("post starting...");
		try {
			session = sessionFactory.newInstance(request, response);
			request.setAttribute("sessionclient", session);
			String ctxPath = hrequest.getContextPath();
			String url = hrequest.getRequestURI();
			if (ctxPath != null) {
				if (url.startsWith(ctxPath)) {
					url = url.substring(ctxPath.length());
				}
			}
			if (session.handleRequest(url, this, hrequest, response)) {
				return;
			}
			int index = url.lastIndexOf(".go");
			if (index > -1) {
				url = url.substring(0, index) + ".jsp";
			}
			NavigateRule rule = null;
			String fromView = request.getParameterDef("konefacesfromid", null);
			if (logger.isDebugEnabled()) {
				logger.debug(hrequest.getRequestURI() + "["
						+ hrequest.getRemoteAddr() + "]: request parameters:{");
				Enumeration<?> en = request.getParameterNames();
				while (en.hasMoreElements()) {
					String n = (String) en.nextElement();
					String p = request.getParameter(n);
					logger.debug("\t" + n + "=" + p);
				}
				logger.debug("}");
			}
			boolean isFormSubmit = false;
			if (fromView != null && !fromView.isEmpty()) {// fromview
				url = new String(DataUnit.hexToBytes(fromView));
				if (ctxPath != null) {
					if (url.startsWith(ctxPath)) {
						url = url.substring(ctxPath.length());
					}
				}
				isFormSubmit = true;
			}
			rule = rules.get(url);
			if (rule != null && rule.saveQueryStringToAttribute) {
				Enumeration<?> en = request.getParameterNames();
				while (en.hasMoreElements()) {
					String n = (String) en.nextElement();
					String p = request.getParameter(n);
					request.setAttribute(n, p);
				}
			}
			if (rule != null) {
				if ((isFormSubmit || rule.createActionAlways)
						&& rule.action != null && rule.actionClass != null) {
					FacesAction action = (FacesAction) Class.forName(
							rule.actionClass).newInstance();
					request.setAttribute(rule.action, action);
					String forwardName;
					try {
						forwardName = action.execute(isFormSubmit, request,
								response);
					} catch (FacesError e) {
						if (logger.isDebugEnabled())
							logger.error("faces error:",e);
						forwardName = e.forward;
						request.setAttribute("faceserror", e);
					} catch (Throwable e) { // 默认指向error
						if (logger.isDebugEnabled())
							logger.error("error:",e);
						forwardName = "error";
						request.setAttribute("faceserror", new FacesError(null,
								StringUnit.getExceptionMessage(e)));
					}
					if (forwardName != null) {
						ToRule tourl = rule.toRules.get(forwardName);
						if (tourl == null) {
							String u = url;
							while (!u.isEmpty()
									&& (index = u.lastIndexOf("/")) >= 0) {
								u = u.substring(0, index);
								rule = rules.get(u + "/*");
								if (rule != null) {
									tourl = rule.toRules.get(forwardName);
									if (tourl != null) {
										break;
									}
								}
							}
						}
						if (tourl == null) {
							throw new Exception("找不到结果为[" + forwardName
									+ "]的目标页面！");
						}
						forward(hrequest, response, tourl.url, tourl.redirect);
						return;
					}
				}
				url = rule.from;
			}
			forward(hrequest, response, url, false);
		} catch (Throwable e) { // 如果出错，则直接定位到错误页面
			logger.error(hrequest.getRequestURI() + "处理失败:", e);
			if (!urlFileExists("error.jsp")) {
				response.sendError(
						HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "请求处理失败："
								+ StringUnit.getExceptionMessage(e));
			} else {
				request.setAttribute("faceserror", new FacesError(null,
						StringUnit.getExceptionMessage(e)));
				hrequest.getRequestDispatcher("/error.jsp").forward(hrequest,
						response);
			}
		}
	}
}
