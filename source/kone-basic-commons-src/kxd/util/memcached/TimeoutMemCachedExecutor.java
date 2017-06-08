package kxd.util.memcached;

import java.io.IOException;
import java.util.Date;

import kxd.util.DateTime;

/**
 * 具备数据超时能力的缓存执行器
 * 
 * @author 赵明
 * 
 */
public class TimeoutMemCachedExecutor extends MemCachedExecutor {
	/**
	 * 超时时间
	 */
	private int timeout;

	public TimeoutMemCachedExecutor() {
		super();
	}

	@Override
	public void initialize(MemcachedConnectionPoolList poolGroup) {
		super.initialize(poolGroup);
		timeout = Integer.valueOf(poolGroup.configMap.get("timeout"));
	}

	@Override
	public Object get(String key) throws IOException, InterruptedException {
		Long lastTime = (Long) super.get(key + ".time");
		if (lastTime != null) {
			long now = System.currentTimeMillis();
			if (DateTime.secondsBetween(lastTime, now) > timeout) {
				super.delete(key + ".time", null);
				super.delete(key, null);
				return null;
			} else {
				Object r = super.get(key);
				if (r == null) {
					super.delete(key + ".time", null);
				} else
					super.set("set", key + ".time", now, false, null);
				return r;
			}
		} else
			return null;
	}

	@Override
	public void set(String cmdname, String key, Object value, boolean compress,
			Date expiry) throws IOException, InterruptedException {
		super.set("set", key + ".time", System.currentTimeMillis(), false,
				expiry);
		super.set(cmdname, key, value, compress, expiry);
	}

	@Override
	public boolean delete(String key, Date expiry) throws IOException,
			InterruptedException {
		super.delete(key + ".time", expiry);
		return super.delete(key, expiry);
	}

}
