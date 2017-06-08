package kxd.engine.scs.trade;

import kxd.net.connection.jndi.Lookuper;
import kxd.net.naming.LoopNamingContext;
import kxd.remote.scs.interfaces.service.TradeServiceBeanRemote;
import kxd.remote.scs.transaction.Request;
import kxd.remote.scs.transaction.Result;
import kxd.remote.scs.util.emun.CounterOption;
//
//class TradeLoggerPackage {
//	Throwable e = null;
//	boolean proccessed = false;
//}
//
///**
// * 插入交易
// * 
// * @author zhaom
// * 
// */
//class InsertTradeDetailPackage extends TradeLoggerPackage {
//	Request req;
//	Result result;
//	boolean ingoreRepeat;
//
//	public InsertTradeDetailPackage(Request req, Result result,
//			boolean ingoreRepeat) {
//		super();
//		this.req = req;
//		this.result = result;
//		this.ingoreRepeat = ingoreRepeat;
//	}
//
//	@Override
//	public String toString() {
//		return "InsertTradeDetailPackage [termglide="
//				+ req.getTradeCode().getTermGlide() + "]";
//	}
//
//}
//
///**
// * 更新交易
// * 
// * @author zhaom
// * 
// */
//class UpdateTradeDetailPackage extends TradeLoggerPackage {
//	Request req;
//	Result result;
//	boolean success;
//	String message;
//
//	public UpdateTradeDetailPackage(Request req, Result result,
//			boolean success, String message) {
//		super();
//		this.req = req;
//		this.result = result;
//		this.success = success;
//		this.message = message;
//	}
//
//	@Override
//	public String toString() {
//		return "UpdateTradeDetailPackage [termglide="
//				+ req.getTradeCode().getTermGlide() + "]";
//	}
//
//}
//
//class InsertTradeLogPackage extends TradeLoggerPackage {
//	Request req;
//	Result result;
//	boolean success;
//	String message;
//	Integer userOrgId;
//
//	public InsertTradeLogPackage(Request req, Result result, Integer userOrgId,
//			boolean success, String message) {
//		super();
//		this.req = req;
//		this.result = result;
//		this.success = success;
//		this.message = message;
//		this.userOrgId = userOrgId;
//	}
//
//	@Override
//	public String toString() {
//		return "InsertTradeLogPackage [termid="
//				+ req.getTradeCode().getTermId() + ",userno="
//				+ req.getParameterDef("userno", "") + "]";
//	}
//
//}
//
//class TradeLoggerRunnable implements Runnable {
//	ArrayBlockingQueue<TradeLoggerPackage> tradePackQueue;
//
//	public TradeLoggerRunnable(
//			ArrayBlockingQueue<TradeLoggerPackage> tradePackQueue) {
//		super();
//		this.tradePackQueue = tradePackQueue;
//	}
//
//	@Override
//	public void run() {
//		LoopNamingContext context = null;
//		TradeServiceBeanRemote bean = null;
//		while (!Thread.interrupted()) {
//			TradeLoggerPackage o = null;
//			try {
//				o = tradePackQueue.take();
//				synchronized (o) {
//					try {
//						if (context == null) {
//							TradeLogger.logger
//									.debug("jndi context is null, lookuping...");
//							context = new LoopNamingContext("db");
//							bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
//									"kxd-ejb-tradeServiceBean",
//									TradeServiceBeanRemote.class);
//						}
//						if (o instanceof InsertTradeDetailPackage) {
//							InsertTradeDetailPackage t = (InsertTradeDetailPackage) o;
//							bean.insertTradeDetail(t.req.getTradeCode(), 0,
//									CounterOption.NONE, t.result,
//									t.req.getRemoteAddr(),
//									t.req.getServerAddr(), t.ingoreRepeat);
//						} else if (o instanceof UpdateTradeDetailPackage) {
//							UpdateTradeDetailPackage t = (UpdateTradeDetailPackage) o;
//							bean.updateTradeDetail(t.req.getTradeCode()
//									.getTermGlide(), t.req.getTradeCode()
//									.getOrgId(), t.result);
//						} else if (o instanceof InsertTradeLogPackage) {
//							InsertTradeLogPackage t = (InsertTradeLogPackage) o;
//							bean.insertTradeLog(
//									t.req.getTradeCode().isStated(), t.req
//											.getTradeCode().isLogged(), t.req
//											.getTradeCode().getTermId(), t.req
//											.getTradeCode().getOrgId(),
//									t.userOrgId, t.req.getTradeCode()
//											.getTradeCodeId(), t.req
//											.getTradeCode().getTradeTime(),
//									t.success, t.message, t.req.getTradeCode()
//											.getUserno(),
//									t.req.getRemoteAddr(), t.req
//											.getServerAddr(), t.result);
//						}
//					} catch (Throwable e) {
//						o.e = e;
//						TradeLogger.logger.error("proc error{" + o + "}:", e);
//						if (context != null) {
//							try {
//								context.close();
//							} catch (NamingException e1) {
//							}
//							context = null;
//							bean = null;
//						}
//					} finally {
//						o.proccessed = true;
//						o.notifyAll();
//					}
//				}
//			} catch (InterruptedException e) {
//				break;
//			}
//		}
//	}
//
//}

/**
 * 交易日志记录器
 * 
 * @author zhaom
 * 
 */
public class TradeLogger {
	// static final Logger logger = Logger.getLogger(TradeLogger.class);

	/**
	 * 交易包队列
	 */
	// ArrayBlockingQueue<TradeLoggerPackage> tradePackQueue = new
	// ArrayBlockingQueue<TradeLoggerPackage>(
	// 50000);

	public TradeLogger() {
		// for (int i = 0; i < 20; i++) {
		// new Thread(new TradeLoggerRunnable(tradePackQueue),
		// "trade log thread[" + i + "]").start();
		// }
	}

	public void insertTradeDetail(Request req, Result result,
			boolean ingoreRepeat) throws Throwable {
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			TradeServiceBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-tradeServiceBean",
					TradeServiceBeanRemote.class);
			bean.insertTradeDetail(req.getTradeCode(), 0, CounterOption.NONE,
					result, req.getRemoteAddr(), req.getServerAddr(),
					ingoreRepeat);
		} finally {
			context.close();
		}
		// try {
		// InsertTradeDetailPackage o = new InsertTradeDetailPackage(req,
		// result, ingoreRepeat);
		// if (!tradePackQueue.offer(o, 60, TimeUnit.SECONDS)) {
		// throw new TradeError("TM", "插入交易明细操作超时");
		// }
		// synchronized (o) {
		// o.wait(60000);
		// if (!o.proccessed)
		// throw new TradeError("TM", "插入交易明细操作超时");
		// else if (o.e != null)
		// throw o.e;
		// }
		// } catch (InterruptedException e) {
		// }
	}

	public void updateTradeDetail(Request req, Result result, boolean success,
			String message) throws Throwable {
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			TradeServiceBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-tradeServiceBean",
					TradeServiceBeanRemote.class);
			bean.updateTradeDetail(req.getTradeCode().getTermGlide(), req
					.getTradeCode().getOrgId(), result);
		} finally {
			context.close();
		}
		// try {
		// UpdateTradeDetailPackage o = new UpdateTradeDetailPackage(req,
		// result, success, message);
		// if (!tradePackQueue.offer(o, 60, TimeUnit.SECONDS)) {
		// throw new TradeError("TM", "更新交易明细操作超时");
		// }
		// synchronized (o) {
		// o.wait(60000);
		// if (!o.proccessed)
		// throw new TradeError("TM", "更新交易明细操作超时");
		// else if (o.e != null)
		// throw o.e;
		// }
		// } catch (InterruptedException e) {
		// }
	}

	public void insertTradeLog(Request req, Result result, Integer userOrgId,
			boolean success, String message) throws Throwable {
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			TradeServiceBeanRemote bean = context.lookup(
					Lookuper.JNDI_TYPE_EJB, "kxd-ejb-tradeServiceBean",
					TradeServiceBeanRemote.class);
			bean.insertTradeLog(req.getTradeCode().isStated(), req
					.getTradeCode().isLogged(), req.getTradeCode().getTermId(),
					req.getTradeCode().getOrgId(), userOrgId, req
							.getTradeCode().getTradeCodeId(), req
							.getTradeCode().getTradeTime(), success, message,
					req.getTradeCode().getUserno(), req.getRemoteAddr(), req
							.getServerAddr(), result);
		} finally {
			context.close();
		}
		// try {
		// InsertTradeLogPackage o = new InsertTradeLogPackage(req, result,
		// userOrgId, success, message);
		// if (!tradePackQueue.offer(o, 60, TimeUnit.SECONDS)) {
		// throw new TradeError("TM", "插入交易日志操作超时");
		// }
		// synchronized (o) {
		// o.wait(60000);
		// if (!o.proccessed)
		// throw new TradeError("TM", "插入交易日志操作超时");
		// else if (o.e != null)
		// throw o.e;
		// }
		// } catch (InterruptedException e) {
		// }
	}

}
