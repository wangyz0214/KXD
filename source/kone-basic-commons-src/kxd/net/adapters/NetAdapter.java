package kxd.net.adapters;

import kxd.net.NetRequest;
import kxd.net.NetResponse;

import org.apache.log4j.Logger;

/**
 * 网络通信适配器接口
 * 
 * @author zhaom
 * @since 4.1
 * @param <S>
 *            发送包类
 * @param <R>
 *            接收包类
 */
public interface NetAdapter<S extends NetRequest, R extends NetResponse> {

	/**
	 * 执行一次通信
	 * 
	 * @param data
	 *            请求包数据
	 * @return 返回包数据
	 * @throws NetAdapterException
	 *             通信失败时，抛出此异常
	 */
	public R execute(S data) throws NetAdapterException, InterruptedException;

	/**
	 * 返回适配器当前是否可用
	 * 
	 */
	public boolean isAvailable();

	/**
	 * 获取日志记录器
	 * 
	 * @return
	 */
	public Logger getLogger();

}
