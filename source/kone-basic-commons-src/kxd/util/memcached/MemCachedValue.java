package kxd.util.memcached;

import java.security.NoSuchAlgorithmException;

import kxd.util.DataSecurity;

public class MemCachedValue {
	/**
	 * 键值
	 */
	private byte[] value;
	/**
	 * 键操作标记
	 */
	private int flags;
	private String md5;

	public MemCachedValue() {
		super();
	}

	public MemCachedValue(byte[] value) {
		super();
		this.value = value;
	}

	public MemCachedValue(byte[] value, int flags) {
		super();
		this.value = value;
		this.flags = flags;
	}

	public MemCachedValue(byte[] value, int flags, String md5) {
		super();
		this.value = value;
		this.flags = flags;
		this.md5 = md5;
	}

	public byte[] getValue() {
		return value;
	}

	public void setValue(byte[] value) {
		this.value = value;
	}

	public int getFlags() {
		return flags;
	}

	public void setFlags(int flags) {
		this.flags = flags;
	}

	public String getMd5() throws MemCachedException {
		if (md5 == null)
			try {
				md5 = DataSecurity.md5(value);
			} catch (NoSuchAlgorithmException e) {
				throw new MemCachedException(e);
			}
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}
}
