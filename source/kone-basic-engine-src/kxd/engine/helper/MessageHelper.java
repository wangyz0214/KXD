package kxd.engine.helper;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.naming.NamingException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import kxd.engine.messaging.MessageDestination;
import kxd.engine.messaging.MessageSender;
import kxd.net.connection.jndi.Lookuper;
import kxd.net.naming.LoopNamingContext;
import kxd.net.naming.NamingContext;
import kxd.remote.scs.interfaces.service.MessageServiceBeanRemote;
import kxd.util.KoneUtil;
import kxd.util.StringUnit;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class MessageHelper {
	static Logger logger = Logger.getLogger(MessageHelper.class);
	static ConcurrentHashMap<String, MessageSender> senders = new ConcurrentHashMap<String, MessageSender>();
	static {
		try {
			loadConfig(KoneUtil.getConfigPath() + "message-service-config.xml");
		} catch (Throwable e) {
			logger.error("load [message-service-config.xml] failed: ", e);
		}
	}

	public static void loadConfig(String file) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(file);
			NodeList groups = doc.getElementsByTagName("sender");
			for (int j = 0; j < groups.getLength(); j++) {
				Element group = (Element) groups.item(j);
				String name = group.getAttribute("name");
				MessageSender sender = new MessageSender();
				sender.setName(name);
				NodeList list = group.getElementsByTagName("to");
				for (int i = 0; i < list.getLength(); i++) {
					Element node = (Element) list.item(i);
					MessageDestination d = new MessageDestination();
					d.setTransmitter(node.getAttribute("transmitter-name"));
					d.setTo(StringUnit.split(node.getTextContent(), ","));
					sender.getDestinations().add(d);
				}
				senders.put(name, sender);
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	/**
	 * 发送消息
	 * 
	 * @param sender
	 *            发送者名称，在后台的message-service-config.xml中配置，包含有发送的电话号码
	 * @param message
	 *            消息内容
	 * @param params
	 *            参数列表，用于传送某些模板需要格式的变量
	 * @return 返回发送结果，一个哈希Map，包含每个地址的发送结果
	 * @throws NamingException
	 */
	static public Map<String, Object> sendMsg(String sender, String message,
			Object... params) {
		Map<String, Object> map = new HashMap<String, Object>();
		MessageSender o = senders.get(sender);
		if (o != null) {
			for (MessageDestination d : o.getDestinations()) {
				Map<String, Object> m = sendMsg(d.getTransmitter(), d.getTo(),
						message, params);
				if (m != null)
					map.putAll(m);
			}
			return map;
		} else {
			logger.warn("[" + sender
					+ "] not configured, Message sending failed.");
			return null;
		}
	}

	/**
	 * 发送消息
	 * 
	 * @param transmitterName
	 *            消息传器名称
	 * @param phoneList
	 *            发送的地址列表
	 * @param message
	 *            消息内容
	 * @param params
	 *            参数列表，用于传送某些模板需要格式的变量
	 * @throws NamingException
	 */
	static public Map<String, Object> sendMsg(String transmitterName,
			String[] to, String message, Object... params) {
		try {
			NamingContext context = new LoopNamingContext("message");
			try {
				MessageServiceBeanRemote bean = context.lookup(
						Lookuper.JNDI_TYPE_EJB, "kxd-ejb-messageServiceBean",
						MessageServiceBeanRemote.class);
				return bean.sendMessage(transmitterName, to, message, params);
			} finally {
				context.close();
			}
		} catch (Throwable e) {
			logger.error("消息发送失败[" + transmitterName + "]：", e);
			return null;
		}
	}

}
