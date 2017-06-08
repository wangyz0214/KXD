package kxd.net.adapters;

import java.io.Serializable;

/**
 * 网络通信包
 * 
 * @author zhaom
 * @since 4.1
 * @param <K>
 *            包ID类
 * @param <V>
 *            包数据类
 */
public class NetPack<K extends Serializable, V extends Serializable> implements
		Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * 包ID，唯一ID
	 */
	K id;
	/**
	 * 包数据
	 */
	V value;
	/**
	 * 包创建时间
	 */
	final long createdTime = System.currentTimeMillis();
	/**
	 * 指示当前包是否可用
	 */
	boolean available = true;

	/**
	 * 网络通信包构造器
	 * 
	 * @param id
	 *            包ID
	 * @param value
	 *            包值
	 */
	public NetPack(K id, V value) {
		super();
		this.id = id;
		this.value = value;
	}

	public NetPack(K id) {
		super();
		this.id = id;
	}

	public K getId() {
		return id;
	}

	public void setId(K id) {
		this.id = id;
	}

	public V getValue() {
		return value;
	}

	public void setValue(V value) {
		this.value = value;
	}

	/**
	 * 包创建时间
	 * 
	 * @return 包创建时间
	 */
	public synchronized long getCreatedTime() {
		return createdTime;
	}

	@Override
	public String toString() {
		return "NetPack [createdTime=" + createdTime + ", id=" + id
				+ ", value=" + value + "]";
	}

	public synchronized boolean isAvailable() {
		return available;
	}

	public synchronized void setAvailable(boolean available) {
		this.available = available;
	}

}
