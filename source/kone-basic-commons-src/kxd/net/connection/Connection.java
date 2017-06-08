package kxd.net.connection;


/**
 * 连接接口
 * 
 * @author zhaom
 * 
 */
public interface Connection<E extends Throwable> {
	/**
	 * 打开连接
	 * 
	 */
	public void open() throws E;

	/**
	 * 关闭连接
	 */
	public void close() throws E;

	/**
	 * 返回是否已经连接
	 */
	public boolean isConnected();
}
