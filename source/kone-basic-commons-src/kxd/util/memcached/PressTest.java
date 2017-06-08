package kxd.util.memcached;

import java.io.IOException;


public class PressTest {

	static long starttime;

	static public void main(String[] args) throws ClassNotFoundException,
			InterruptedException, IOException {
		MemcachedConnectionFactory.loadConfig("memcached.xml");
		MemCachedClient mc = MemCachedClient.getInstance("session");
		mc.flushAll();
		// mc.set("aaa", "aaa");
		// System.out.println(mc.get("bbb"));
		// starttime = System.currentTimeMillis();
		// for (int i = 0; i < 30; i++) {
		// new Thread(new Runnable() {
		// @Override
		// public void run() {
		// String tn = Thread.currentThread().getName();
		// MemCachedClient mc;
		// try {
		// mc = MemCachedClient.getInstance("session");
		// String str =
		// "asdfsafdlskfdjsadf;lsakfdj;lsakfdj;salkjfd;sadlkjf;lsadfkjs;alkfdj;sadlfkj;saldfkj;sakjfd;lsadkjf;lsajf;lsakfdj;lsajf;lsakjfds;lakfdj;lsakjfd;saldjf;saldkjf;ldsa";
		// for (int i = 0; i < 2000; i++) {
		// String key = Double.toHexString(new Random()
		// .nextDouble());
		// mc.set(key, str);
		// mc.get(key);
		// }
		// /*
		// * String keys[] = new String[20]; for (int i = 0; i <
		// * keys.length; i++) { keys[i] = tn + "_a" + i; try {
		// * mc.set(keys[i], str); } catch (Throwable e) {
		// * e.printStackTrace(); } } for (int i = 0; i < 1000;
		// * i++) { try { for (int j = 0; j < keys.length; j++) {
		// * mc.get(keys[j]); } } catch (Throwable e) {
		// * e.printStackTrace(); } }
		// */
		// } catch (Throwable e) {
		// e.printStackTrace();
		// }
		// System.out.println(tn + " complete: "
		// + (System.currentTimeMillis() - starttime) + " 毫秒");
		// ConnectionFactory.printConnectionsStatus();
		// }
		//
		// }, "testthread" + i).start();
		// }
	}
}
