package kxd.remote.scs.interfaces.service;

import java.util.Map;

import javax.ejb.Remote;

/**
 * 消息服务接口
 * 
 * @author 赵明
 */
@Remote
public interface MessageServiceBeanRemote {
	/**
	 * 发送消息
	 * 
	 * @param transmitterName
	 *            消息传送器名称
	 * @param to
	 *            发送的地址列表
	 * @param message
	 *            消息内容
	 * @param params
	 *            参数列表，用于传送某些模板需要格式的变量
	 * @return 返回发送结果，一个哈希Map，包含每个地址的发送结果
	 */
	public Map<String, Object> sendMessage(String transmitterName, String[] to,
			String message, Object... params);
}
