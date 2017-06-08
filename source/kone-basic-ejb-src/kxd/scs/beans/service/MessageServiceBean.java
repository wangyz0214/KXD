package kxd.scs.beans.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import kxd.engine.dao.BaseBean;
import kxd.engine.messaging.MessageTransmitter;
import kxd.engine.messaging.MessageTransmitterConfig;
import kxd.remote.scs.interfaces.service.MessageServiceBeanRemote;
import kxd.util.KoneUtil;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

@Stateless(name = "kxd-ejb-messageServiceBean", mappedName = "kxd-ejb-messageServiceBean")
public class MessageServiceBean extends BaseBean implements
		MessageServiceBeanRemote {
	static ConcurrentHashMap<String, MessageTransmitterConfig> transmitters = new ConcurrentHashMap<String, MessageTransmitterConfig>();
	static Logger logger = Logger.getLogger(MessageServiceBean.class);
	static {
		try {
			loadConfig(KoneUtil.getConfigPath() + "message-service-config.xml");
		} catch (Throwable e) {
			logger.error("load [message-service-config.xml] failed: ", e);
		}
	}

	static void loadConfig(String file) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(file);
			NodeList groups = doc.getElementsByTagName("transmitter");
			for (int j = 0; j < groups.getLength(); j++) {
				Element group = (Element) groups.item(j);
				String name = group.getAttribute("name");
				MessageTransmitterConfig transmitter = new MessageTransmitterConfig();
				transmitter.setName(name);
				transmitter.setTransmitterClassName(group
						.getAttribute("classname"));
				transmitter.setType(group.getAttribute("type"));
				NodeList list = group.getElementsByTagName("param");
				for (int i = 0; i < list.getLength(); i++) {
					Element node = (Element) list.item(i);
					transmitter.getParams().put(node.getAttribute("name"),
							node.getAttribute("value"));
				}
				transmitters.put(name, transmitter);
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Map<String, Object> sendMessage(String transmitterName, String[] to,
			String message, Object... params) {
		MessageTransmitterConfig config = transmitters.get(transmitterName);
		if (config != null) {
			try {
				MessageTransmitter t = (MessageTransmitter) Class.forName(
						config.getTransmitterClassName()).newInstance();
				return t.send(config, to, message, params);
			} catch (Throwable e) {
				logger.error("message sending failed:", e);
			}
			return null;
		} else
			return null;
	}

}
