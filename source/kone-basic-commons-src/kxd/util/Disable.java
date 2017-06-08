package kxd.util;

/**
 * 使数据对象具备禁止选择的能力。
 * <p>
 * 通过实现<code>Disable</code>接口，使数据对象具备禁止选择的功能
 * </p>
 * 
 * @author 赵明
 * @version 1.0 , 2008/05/06
 */
public interface Disable {
	/**
	 * 返回对象当前是否是禁止选择状态
	 */
	public boolean isDisabled();

	/**
	 * 设置对象是否禁止选择状态
	 */
	public void setDisabled(boolean newValue);
}
