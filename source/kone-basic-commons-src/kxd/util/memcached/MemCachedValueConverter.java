package kxd.util.memcached;

import java.io.IOException;

public interface MemCachedValueConverter {
	MemCachedValue objectToBytes(Object value, boolean compress)
			throws IOException;

	/**
	 * 压缩数据
	 * 
	 * @param buf
	 *            要压缩数据
	 * @return null 数据不需要压缩；非null-压缩后的数据
	 * @throws IOException
	 */
	void compress(MemCachedValue value) throws IOException;

	/**
	 * 压缩数据
	 * 
	 * @param buf
	 *            要压缩数据
	 * @return null 数据不需要压缩；非null-压缩后的数据
	 * @throws IOException
	 */
	void decompress(MemCachedValue value) throws IOException;

	Object bytesToObject(MemCachedValue value) throws IOException;
}
