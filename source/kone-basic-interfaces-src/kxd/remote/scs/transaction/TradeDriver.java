package kxd.remote.scs.transaction;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

abstract public class TradeDriver {
	private static Logger logger = Logger.getLogger(TradeDriver.class);
	private Object config;
	private Object termConfig;

	public Object getTermConfig() {
		return termConfig;
	}

	public void setTermConfig(Object termConfig) {
		this.termConfig = termConfig;
	}

	public Object getConfig() {
		return config;
	}

	public void setConfig(Object config) {
		this.config = config;
	}

	public Logger getLogger() {
		return logger;
	}

	/**
	 * 保存当前交易驱动的配置项目
	 */
	ConcurrentHashMap<String, String> configMap;

	/**
	 * 做交易。
	 * 
	 * @param req
	 *            交易请求
	 * @param resp
	 *            交易响应。交易驱动如果有需求需要返回自己生成的Document，可以修改resp.document
	 * @return Result 交易返回
	 * @throws TradeError
	 *             交易失败时抛出的异常
	 */
	abstract public Result trade(Request req, Response resp) throws TradeError;

	public ConcurrentHashMap<String, String> getConfigMap() {
		return configMap;
	}

	public void setConfigMap(ConcurrentHashMap<String, String> configMap) {
		this.configMap = configMap;
	}

	/**
	 * 交易之前的处理动作
	 * 
	 */
	public void tradeStart(Request req) throws Exception {
	}

	/**
	 * 交易完成之后的处理动作，只要开始交易，不管交易是否成功，均会执行该函数，来达到清理的目的
	 */
	public void tradeComplete(Request req, Response resp) {
	}
}
