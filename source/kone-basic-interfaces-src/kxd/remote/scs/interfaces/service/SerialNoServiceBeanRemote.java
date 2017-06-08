package kxd.remote.scs.interfaces.service;

import javax.ejb.Remote;

/**
 * 
 * @author 赵明
 */
@Remote
public interface SerialNoServiceBeanRemote {
	public void startMonitorTimer();

	/**
	 * 
	 * 获取日期序列号
	 * 
	 * @param key
	 *            与序列号关联的关键字
	 * @param day
	 *            当天日期
	 * @return 当天的下一个序列号
	 */
	public long getNextDaySerial(String key, long minValue, long maxValue);

	/**
	 * 
	 * 获取循环序列号
	 * 
	 * @param key
	 *            与序列号关联的关键字
	 * @param minValue
	 *            最小值
	 * @param maxValue
	 *            最大值得
	 * @return 下一序列号
	 */
	public long getNextCircleSerial(String key, long minValue, long maxValue);

}
