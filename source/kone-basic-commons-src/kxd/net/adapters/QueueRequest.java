package kxd.net.adapters;

import kxd.net.NetRequest;


/**
 * 队列请求包接口
 * 
 * @author zhaom
 * @since 4.1
 */
public interface QueueRequest extends NetRequest {
	/**
	 * 超时后，是否丢弃请求包，通常来说，都是丢弃(true)，因为此时用户端已经提示出错，但对于后台交易来说，则视具体情况而定，如冲正包
	 * ，则不应该丢弃(false)
	 * 
	 * @return true/false
	 */
	public boolean isTimeoutDiscarded();
}
