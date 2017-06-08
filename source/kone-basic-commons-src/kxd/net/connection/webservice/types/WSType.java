package kxd.net.connection.webservice.types;

/**
 * Web Service的数据值
 * 
 * @author zhaom
 * 
 */
public interface WSType<V> {
	/**
	 * 设置字符串值
	 * 
	 * @param value
	 */
	public void setString(String value);

	/**
	 * 获取字符串值
	 * 
	 * @return
	 */
	public String getString();

	/**
	 * 设置值
	 * 
	 * @param value
	 */
	public void setValue(V value);

	/**
	 * 获取值
	 * 
	 * @return
	 */
	public V getValue();

}
