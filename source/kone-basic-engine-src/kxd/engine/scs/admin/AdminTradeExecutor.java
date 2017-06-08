package kxd.engine.scs.admin;

import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletResponse;

import kxd.net.HttpRequest;
import kxd.net.XMLExecutor;
import kxd.util.DataSecurity;
import kxd.util.KoneException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class AdminTradeExecutor implements XMLExecutor {
	AdminSessionObject session;
	public static String fileUserId;
	public static String filePwd;
	public static boolean notNeedVerifyCode = false;
	static ConcurrentHashMap<String, Class<?>> serviceMap = new ConcurrentHashMap<String, Class<?>>();
	private static Logger logger = Logger.getLogger(AdminTradeExecutor.class);

	public void init(Document doc) {
		try { // 装入交易配置
			NodeList list = doc.getElementsByTagName("client");
			if (list.getLength() > 0) {
				Element node = (Element) list.item(0);
				if (node.hasAttribute("verify")) {
					boolean value = Boolean
							.valueOf(node.getAttribute("verify"));
					notNeedVerifyCode = value;
				}
			}
			list = doc.getElementsByTagName("fileservice");
			if (list.getLength() > 0) {
				Element el = (Element) list.item(0);
				fileUserId = el.getAttribute("user");
				filePwd = DataSecurity.md5(el.getAttribute("password"));
			}
			list = doc.getElementsByTagName("service");
			for (int i = 0; i < list.getLength(); i++) {
				Element node = (Element) list.item(i);

				if (node.hasAttribute("name") && node.hasAttribute("class")) {
					String name = node.getAttribute("name");
					serviceMap.put(name,
							Class.forName(node.getAttribute("class")));
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public void uninit() {
		serviceMap.clear();
	}

	public Document execute(HttpRequest request, HttpServletResponse response,
			Document xmlDoc, Element content, Element result) throws Throwable {
		session = new AdminSessionObject(request, response);
		if (((AdminSessionObject) session).getLoginUser() == null) {
			throw new KoneException("请先登录,再执行本操作");
		}
		String service = request.getParameterDef("service", null);
		if (service == null) {
			service = "default";
		}
		Class<?> clazz = serviceMap.get(service);
		if (clazz == null)
			throw new KoneException("找不到交易服务[" + service + "]");
		try {
			((AdminTradeDriver) clazz.newInstance()).execute(session, request,
					response, xmlDoc, content, result);
		} catch (Throwable e) {
			logger.error("execute failure:", e);
			throw e;
		}
		return null;
	}
}
