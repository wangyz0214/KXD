package kxd.engine.scs.web;

import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import kxd.engine.scs.trade.AutoRePayRunnable;
import kxd.engine.scs.trade.drivers.SystemDriver;
import kxd.engine.scs.web.tags.BaseTag;
import kxd.net.XMLExecutorManager;
import kxd.net.connection.jndi.Lookuper;
import kxd.net.naming.LoopNamingContext;
import kxd.remote.scs.interfaces.AutoCreateTableBeanRemote;
import kxd.remote.scs.interfaces.service.SerialNoServiceBeanRemote;
import kxd.util.KoneUtil;
import kxd.util.StringUnit;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class WebContextListener implements ServletContextListener {
	private Thread autoRePayThread;
	static final ConcurrentHashMap<String, ConcurrentHashMap<String, String>> paramsConfig = new ConcurrentHashMap<String, ConcurrentHashMap<String, String>>();
	static final Logger logger = Logger.getLogger(WebContextListener.class);

	public void contextDestroyed(ServletContextEvent arg0) {
		XMLExecutorManager.uninit(arg0.getServletContext());
		if (autoRePayThread != null) {
			autoRePayThread.interrupt();
			autoRePayThread = null;
		}
		paramsConfig.clear();
	}

	private void loadParamsConfig() {
		try { // 装入参数配置
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			String path = KoneUtil.getConfigPath();
			Document doc = builder.parse(path + "kone-web.xml");
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
			BaseTag.sites = paramsConfig.get("tag-url-sites");
			list = doc.getElementsByTagName("silverlight");
			if (list.getLength() > 0) {
				Element el = (Element) list.item(0);
				if (el.hasAttribute("version"))
					SystemDriver.silverlightVersion = el
							.getAttribute("version");
				if (el.hasAttribute("enabledApps")) {
					SystemDriver.silverlightEnabledApps.addAll(StringUnit
							.splitToInt1(el.getAttribute("enabledApps"), ","));
				}
			}
			list = doc.getElementsByTagName("font");
			String fonts = "";
			for (int i = 0; i < list.getLength(); i++) {
				Element el = (Element) list.item(i);
				fonts += el.getAttribute("name") + "=";
				NodeList ls = el.getElementsByTagName("file");
				for (int j = 0; j < ls.getLength(); j++) {
					Element el1 = (Element) ls.item(j);
					if (j > 0)
						fonts += "&";
					fonts += el1.getAttribute("name") + "="
							+ el1.getAttribute("file");
				}
				fonts += ";";
			}
			if (!fonts.isEmpty())
				SystemDriver.fontsConfig = fonts;
		} catch (Throwable e) {
			logger.error("init error:", e);
		}
	}

	public void contextInitialized(ServletContextEvent arg0) {
		XMLExecutorManager.init(arg0.getServletContext(), "kone-web.xml");
		loadParamsConfig();
		if (autoRePayThread == null) {
			autoRePayThread = new Thread(new AutoRePayRunnable());
			autoRePayThread.start();
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					boolean b = false;
					try {
						LoopNamingContext context = new LoopNamingContext("db");
						b = true;
						try {
							AutoCreateTableBeanRemote bean = context.lookup(
									Lookuper.JNDI_TYPE_EJB,
									"kxd-ejb-autoCreateTableBean",
									AutoCreateTableBeanRemote.class);
							bean.startAutoCreateTimer();
						} finally {
							context.close();
						}
						break;
					} catch (Throwable e) {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
					}
					if (b)
						break;
				}
				while (true) {
					boolean b = false;
					try {
						LoopNamingContext context = new LoopNamingContext(
								"cache");
						b = true;
						try {
							SerialNoServiceBeanRemote bean = context.lookup(
									Lookuper.JNDI_TYPE_EJB,
									"kxd-ejb-serialNoServiceBean",
									SerialNoServiceBeanRemote.class);
							bean.startMonitorTimer();
						} finally {
							context.close();
						}
						break;
					} catch (Throwable e) {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
					}
					if (b)
						break;
				}
			}
		}).start();
	}

}
