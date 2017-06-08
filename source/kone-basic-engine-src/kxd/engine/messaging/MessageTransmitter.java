package kxd.engine.messaging;

import java.util.Map;

public interface MessageTransmitter {
	/**
	 * 发送消息
	 * 
	 * @param to
	 *            发送地址列表
	 * @param msg
	 *            消息内容
	 * @param params
	 *            参数列表
	 * @return 返回发送结果，一个哈希Map，包含每个地址的发送结果
	 */
	public Map<String, Object> send(MessageTransmitterConfig config,
			String[] to, String msg, Object... params);
}
