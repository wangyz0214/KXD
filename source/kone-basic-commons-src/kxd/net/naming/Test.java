package kxd.net.naming;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;

import javax.naming.NamingException;

import org.apache.log4j.Logger;

public class Test {
	private static Logger logger = Logger.getLogger(Test.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			for (MemoryPoolMXBean o : ManagementFactory.getMemoryPoolMXBeans()) {
				MemoryUsage u = o.getUsage();
				System.out.println(o.getName() + "==>[used:" + u.getUsed()
						+ ";submitted:" + u.getCommitted() + ";max:"
						+ u.getMax() + "]");
			}
			final NamingContext context = new LoopNamingContext("db");
			for (int j = 0; j < 20; j++)
				new Thread(new Runnable() {

					@Override
					public void run() {
						for (int i = 0; i < 100000;) {
							try {
								AliveTestBeanRemote b = context.lookup(
										Lookuper.JNDI_TYPE_EJB,
										"kxd-ejb-aliveTestBean",
										AliveTestBeanRemote.class);
								logger.info(b.test("aaa_" + i));
								Thread.sleep(1000);
							} catch (Throwable e) {
								logger.error("error:", e);
							}
						}
					}
				}).start();

		} catch (NamingException e) {
			e.printStackTrace();
		}
	}

}
