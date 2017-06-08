package kxd.engine.scs.admin;

import java.net.MalformedURLException;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import kxd.engine.ui.core.ControlServlet;
import kxd.engine.ui.tags.website.BaseTagSupport;
import kxd.net.XMLExecutorManager;
import kxd.util.KoneUtil;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class AdminContextListener implements ServletContextListener {

	static Logger logger = Logger.getLogger(AdminContextListener.class);
	static final ConcurrentHashMap<String, ConcurrentHashMap<String, String>> paramsConfig = new ConcurrentHashMap<String, ConcurrentHashMap<String, String>>();

	private void loadParamsConfig() {
		try { // 装入参数配置
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			String path = KoneUtil.getConfigPath();
			Document doc = builder.parse(path + "kone-admin.xml");
			NodeList list = doc.getElementsByTagName("paramsconfig");
			for (int i = 0; i < list.getLength(); i++) {
				Element node = (Element) list.item(i);
				NodeList ls = node.getElementsByTagName("group");
				for (int j = 0; j < ls.getLength(); j++) {
					node = (Element) ls.item(j);
					ConcurrentHashMap<String, String> map = new ConcurrentHashMap<String, String>();
					paramsConfig.put(node.getAttribute("name"), map);
					NodeList ls1 = node.getElementsByTagName("param");
					for (int k = 0; k < ls1.getLength(); k++) {
						node = (Element) ls1.item(k);
						if (node.hasAttribute("value"))
							map.put(node.getAttribute("name"),
									node.getAttribute("value"));
						else
							map.put(node.getAttribute("name"),
									node.getTextContent());
					}
				}
			}
			BaseTagSupport.configParams = paramsConfig.get("admin-web-tags");
		} catch (Throwable e) {
			logger.error("init error:", e);
		}
	}

	public void contextDestroyed(ServletContextEvent arg0) {
		paramsConfig.clear();
		XMLExecutorManager.uninit(arg0.getServletContext());
		ControlServlet.unload();
		AdminSessionObject.setFuncTreeNull();
	}

	public void contextInitialized(ServletContextEvent arg0) {
		try {
			XMLExecutorManager.init(arg0.getServletContext(), "kone-admin.xml");
			loadParamsConfig();
			String path = arg0.getServletContext().getResource("/").toString();
			if (path.endsWith("/")) {
				path = path.substring(0, path.length() - 1);
			}
			ControlServlet.webRootPath = path;
			ControlServlet.load("konefaces-config.xml");
		} catch (MalformedURLException ex) {
			logger.error(ex);
		}
	}
}
