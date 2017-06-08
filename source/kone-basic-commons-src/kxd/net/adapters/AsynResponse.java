package kxd.net.adapters;

import kxd.net.NetResponse;


/**
 * 异步响应包
 * 
 * @author zhaom
 * @since 4.1
 */
public interface AsynResponse<K> extends NetResponse {
	/**
	 * 获取包ID
	 * 
	 * @return 包ID
	 */
	public K getId();
}
