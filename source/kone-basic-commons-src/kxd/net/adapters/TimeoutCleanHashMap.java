package kxd.net.adapters;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.log4j.Logger;

/**
 * 具备自动清除过期元素的哈希列表
 * 
 * @author zhaom
 * 
 * @param <K>
 * @param <V>
 */
public class TimeoutCleanHashMap<K, V extends NetPack<?, ?>> extends
		ConcurrentHashMap<K, V> {
	private static final long serialVersionUID = 7435597829588818250L;
	private final int timeout;
	private long lastCheckTime = 0;
	static CleanRunnable thread;
	Logger logger;

	public static synchronized CleanRunnable getThread() {
		if (thread == null) {
			thread = new CleanRunnable();
			new Thread(thread, "TimeoutCleanHashMap thread").start();
		}
		return thread;
	}

	static class CleanRunnable implements Runnable {
		final CopyOnWriteArrayList<TimeoutCleanHashMap<?, ?>> list = new CopyOnWriteArrayList<TimeoutCleanHashMap<?, ?>>();

		public CleanRunnable() {
		}

		public void addWatchHashMap(TimeoutCleanHashMap<?, ?> map) {
			list.add(map);
		}

		@Override
		public void run() {
			while (!Thread.interrupted()) {
				try {
					Thread.sleep(60000);
					Iterator<TimeoutCleanHashMap<?, ?>> it = list.iterator();
					while (it.hasNext()) {
						it.next().cleanTimeout();
					}
				} catch (InterruptedException e) {
					break;
				} catch (Throwable e) {
				}
			}
		}
	}

	private void cleanTimeout() {
		long now = System.currentTimeMillis();
		if ((now - lastCheckTime) < timeout / 2)
			return;
		lastCheckTime = now;
		try {
			Enumeration<K> en = keys();
			while (en.hasMoreElements()) {
				K k = en.nextElement();
				V o = get(k);
				if ((now - o.getCreatedTime()) > timeout) {
					remove(k);
					logger.debug("key[" + k + "] data[" + o + "] removed");
				}
			}
		} catch (Throwable e) {
			logger.error("cleanTimeout error:", e);
		}
	}

	/**
	 * 创建哈希列表
	 * 
	 * @param timeout
	 *            超时时间，以毫秒单位。将自动移除哈希表中的超时该时间的元素
	 */
	public TimeoutCleanHashMap(Logger logger, int timeout) {
		super();
		this.timeout = timeout;
		this.logger = logger;
		getThread().addWatchHashMap(this);
	}

	public TimeoutCleanHashMap(Logger logger, int initialCapacity,
			float loadFactor, int concurrencyLevel, int timeout) {
		super(initialCapacity, loadFactor, concurrencyLevel);
		this.timeout = timeout;
		this.logger = logger;
		getThread().addWatchHashMap(this);
	}

	public TimeoutCleanHashMap(Logger logger, int initialCapacity,
			float loadFactor, int timeout) {
		super(initialCapacity, loadFactor);
		this.timeout = timeout;
		this.logger = logger;
		getThread().addWatchHashMap(this);
	}

	public TimeoutCleanHashMap(Logger logger, int initialCapacity, int timeout) {
		super(initialCapacity);
		this.timeout = timeout;
		this.logger = logger;
		getThread().addWatchHashMap(this);
	}

	public TimeoutCleanHashMap(Logger logger, Map<? extends K, ? extends V> m,
			int timeout) {
		super(m);
		this.timeout = timeout;
		this.logger = logger;
		getThread().addWatchHashMap(this);
	}

}
