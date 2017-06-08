package kxd.util;

import org.apache.log4j.Logger;

/**
 * 使数据对象具备写调试日志的能力。
 * <p>
 * 通过实现<code>Debugable</code>接口，使数据对象具备写调试日志的功能
 * </p>
 * 
 * @author 赵明
 * @version 1.0 , 2008/05/06
 */
public interface Debugable {

	/**
	 * 输出日志
	 * 
	 * @param logger
	 *            日志记录器
	 * @param prefix
	 *            前缀，先打印prefix，再打印内容
	 */
	public void debug(Logger logger, String prefix);

}
