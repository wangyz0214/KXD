package kxd.net.adapters;

/**
 * 包ID生成器
 * 
 * @author zhaom
 * @since 4.1
 * @param <K>
 *            id类名称
 */
public interface IdGenerator<K> {
	/**
	 * 获取下一个包ID
	 * 
	 * @return 下一个包ID
	 */
	public K getNextId();
}
