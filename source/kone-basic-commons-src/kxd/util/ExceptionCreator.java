package kxd.util;

/**
 * 异常构造器
 * 
 * @author zhaom
 * 
 */
public interface ExceptionCreator<E extends Throwable> {

	/**
	 * 创建异常
	 * 
	 * @param msg
	 *            异常信息
	 * @param t
	 *            之前的异常，可以为null
	 * 
	 */
	public E createException(String msg, Throwable t);

}
