package kxd.util.memcached;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import kxd.util.KeyValue;

public class Test {

	static long starttime;

	static public void main(String[] args) throws ClassNotFoundException,
			InterruptedException, IOException {
		// MemcachedConnectionFactory.loadConfig("memcached.xml");
		MemCachedClient mc = MemCachedClient.getInstance("terminal");
		starttime = System.currentTimeMillis();
		List<KeyValue<String, Object>> ls = new ArrayList<KeyValue<String, Object>>();
		for (int i = 0; i < 150000; i++) {
			ls.add(new KeyValue<String, Object>("key_" + i, i + "-asdfsafd-"
					+ i + "-asdf;kjsaf;saldfj;"));
		}
		try {
			mc = MemCachedClient.getInstance("terminal");
			mc.setAsync(100, 100, ls, false, null);
			System.out.println(" set complete: "
					+ (System.currentTimeMillis() - starttime) + " 毫秒");
			while (true) {
				for (int i = 0; i < 15000; i++) {
					try {
						System.out.println(mc.get("key_" + i));
					} catch (Throwable e) {
						e.printStackTrace();
					}
					Thread.sleep(300);
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		System.out.println(" complete: "
				+ (System.currentTimeMillis() - starttime) + " 毫秒");
		// MemcachedConnectionFactory.printConnectionsStatus();
	}
}
