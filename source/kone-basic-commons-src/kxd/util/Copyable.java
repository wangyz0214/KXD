package kxd.util;

/**
 * 数据具备复制能力
 * 
 */
public interface Copyable {
	public void copyData(Object src);

	public void copy(Object src);

	public Object createObject();

	public Object copy();

}
