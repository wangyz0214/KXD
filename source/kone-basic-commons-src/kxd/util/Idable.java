package kxd.util;

/**
 * 使数据对象具备表示一个ID关键字的能力。
 * <p>
 * 通过实现<code>Idable</code>接口，使数据对象具备表示一个ID关键字的能力
 * </p>
 * 
 * @author 赵明
 * @param E
 *            表示关键值的类型
 * @version 1.0 , 2008/05/06
 */
public interface Idable<E> {
	/**
	 * 返回对象的ID
	 */
	public E getId();

	/**
	 * 设置对象的ID
	 */
	public void setId(E newValue);

	/**
	 * 获取显示内容
	 */
	public String getDisplayLabel();

	/**
	 * 设置显示内容
	 */
	public void setDisplayLabel(String newValue);

	public void setIdString(String id);

	public String getIdString();

}
