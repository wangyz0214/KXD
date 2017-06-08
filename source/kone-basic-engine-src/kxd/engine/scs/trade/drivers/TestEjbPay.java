package kxd.engine.scs.trade.drivers;

import java.util.Hashtable;
import java.util.concurrent.atomic.AtomicLong;

import javax.naming.NamingException;

import kxd.engine.cache.beans.sts.CachedTerm;
import kxd.net.naming.Lookuper;
import kxd.net.naming.LoopNamingContext;
import kxd.net.naming.NamingContext;
import kxd.remote.scs.transaction.Request;
import kxd.remote.scs.transaction.TransactionBeanRemote;
import kxd.remote.scs.transaction.TransactionResponse;
import kxd.util.DateTime;

import org.apache.log4j.Logger;

public class TestEjbPay {
	private static Logger logger = Logger.getLogger(TestEjbPay.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			NamingContext context = new LoopNamingContext("db");
			final TransactionBeanRemote b = context.lookup(
					Lookuper.JNDI_TYPE_EJB,
					"kxd-ejb-testPayTransactionBean2-01",
					TransactionBeanRemote.class);
			final AtomicLong glide = new AtomicLong(0);
			final AtomicLong count = new AtomicLong(0);
			final long m = System.currentTimeMillis();
			for (int j = 0; j < 500; j++)
				new Thread(new Runnable() {

					@Override
					public void run() {
						for (int i = 0; i < 1000;) {
							try {
								long l = glide.getAndAdd(1);
								if (l >= 10000) {
									glide.set(0);
									l = 0;
								}
								String str = Long.toString(l);
								while (str.length() < 4)
									str = "0" + str;
								Hashtable<String, String> map = new Hashtable<String, String>();
								map.put("service", "pay");
								map.put("tradecode", "cashmobilepay");
								map.put("tradecodeid", "56");
								map.put("rigionid", "110");
								map.put("userno", "18633333333");
								map.put("code", "null");
								map.put("transactionid", "20000990409083049456");
								map.put("termglide",
										new DateTime().format("yyyyMMddHHmm")
												+ str);// "20000990409083049456");
								map.put("amount", "500");
								map.put("tradeamount", "500");
								map.put("chargeparty", "800");
								map.put("termcode", "111100009");
								map.put("tradetime",
										new DateTime().format("yyyyMMddHHmmss"));
								map.put("provinceid", "011");
								map.put("cityid", "011");
								map.put("termid", "9106");
								map.put("orgid", "7415");
								map.put("citycode", "110");
								Request req = new Request(map);
								req.getTradeCode().setTradeCodeId(56);
								req.setServerAddr("192.168.1.104");
								req.setRemoteAddr("192.168.1.104");
								req.setTerm(new CachedTerm(9106));
								TransactionResponse r = b.trade(req);
								logger.info(r.result.getResult() + "["
										+ count.addAndGet(1) + ","
										+ (System.currentTimeMillis() - m)
										+ "毫秒]");
								// logger.info(b.toUpper("aaa_" + i, 1000));
							} catch (Throwable e) {
								logger.error("error:[" + count.addAndGet(1)
										+ ","
										+ (System.currentTimeMillis() - m)
										+ "毫秒]:", e);
							}
						}
					}
				}).start();

		} catch (NamingException e) {
			e.printStackTrace();
		}
	}

}
