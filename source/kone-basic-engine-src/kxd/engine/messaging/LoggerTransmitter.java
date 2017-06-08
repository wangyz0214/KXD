package kxd.engine.messaging;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

public class LoggerTransmitter implements MessageTransmitter {
	Logger logger = Logger.getLogger(LoggerTransmitter.class);

	@Override
	public Map<String, Object> send(MessageTransmitterConfig config,
			String[] to, String msg, Object... params) {
		Map<String, Object> map = new HashMap<String, Object>();
		StringBuffer sb = new StringBuffer("  to:[");
		for (String o : to) {
			if (sb.length() > 0)
				sb.append(",");
			sb.append(o);
			map.put(o, true);
		}
		sb.append("]");
		logger.info(sb.toString());
		logger.info("    msg:[" + msg + "]");
		sb = new StringBuffer("  params:[");
		for (Object o : params) {
			if (sb.length() > 0)
				sb.append(",");
			sb.append(o.toString());
		}
		sb.append("]");
		logger.info(sb.toString());
		return map;
	}

}
