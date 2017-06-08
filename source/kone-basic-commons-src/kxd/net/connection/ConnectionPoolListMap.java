package kxd.net.connection;

import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

import kxd.util.DateTime;

/**
 * 连接池列表映射，支持多个连接池列表访问
 * 
 * @author zhaom
 * 
 * @param <C>
 * @param <E>
 */
public class ConnectionPoolListMap<V extends ConnectionPoolList<?, ?>> extends
		ConcurrentHashMap<String, V> {
	private static final long serialVersionUID = 1L;
	private int watchInterval = 60;

	protected void startWatchThread() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				long lastTime = 0;
				while (true) {
					try {
						long now = System.currentTimeMillis();
						if (DateTime.secondsBetween(now, lastTime) > watchInterval) {
							lastTime = now;
							Enumeration<V> en = elements();
							while (en.hasMoreElements()) {
								try {
									en.nextElement().keepAlive();
								} catch (Throwable e) {
								}
							}
						}
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						break;
					} catch (Throwable e) {
					}
				}
			}
		}).start();
	}

	public ConnectionPoolListMap(int watchInterval) {
		super();
		this.watchInterval = watchInterval;
		startWatchThread();
	}

	public ConnectionPoolListMap() {
		super();
		watchInterval = 60;
		startWatchThread();
	}
}
