package kxd.util;
/**
 * 文件上载能力
 * @author 赵明
 *
 */
public interface Uploadable {
	/**
	 * 获取当前的文件名,含文件名和路径,路径是相对web根目录
	 */
	public String getFileName();
}
