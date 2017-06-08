package kxd.engine.helper;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.naming.NamingException;

import kxd.engine.scs.trade.TradeLogger;
import kxd.remote.scs.transaction.Request;
import kxd.remote.scs.transaction.Result;
import kxd.remote.scs.transaction.TradeCode;
import kxd.remote.scs.transaction.TradeError;
import kxd.remote.scs.util.AppException;
import kxd.remote.scs.util.emun.CounterOption;
import kxd.util.DataUnit;
import kxd.util.DateTime;
import kxd.util.KoneUtil;
import kxd.util.StringUnit;

import org.apache.log4j.Logger;

public class DaoHelper {
	private static Logger logger = Logger.getLogger(AdminHelper.class);
	private static final TradeLogger tradeLogger = new TradeLogger();

	/**
	 * 写交易日志，EJBDriver将不调用
	 * 
	 * @param driver
	 *            交易驱动对象
	 * @param req
	 *            交易请求
	 * @throws TradeError
	 * @throws NamingException
	 */
	static public void insertTradeDetail(Request req, Result result,
			boolean ingoreRepeat) throws TradeError, NamingException {
		TradeCode tradeCode = req.getTradeCode();
		// 如果中端交易流水号为空，不写交易日志
		if (!tradeCode.isTrade())
			return;
		if (result.getTradeEndTime() == null)
			result.setTradeEndTime(new Date());
		if (result.getResultInfo() == null
				|| result.getResultInfo().trim().isEmpty())
			result.setResultInfo(result.getResult().toString());
		String sql = "call "
				+ getInsertTradeDetailSql(ingoreRepeat, tradeCode, 0,
						CounterOption.NONE, result, tradeCode.getTradeTime(),
						null, tradeCode.getRedoParams(), req.getRemoteAddr(),
						req.getServerAddr()) + ";";
		try {
			getLogThread().addExecBeforeLog(sql);
			tradeLogger.insertTradeDetail(req, result, ingoreRepeat);
		} catch (Throwable e) {
			String msg = StringUnit.getExceptionMessage(e);
			if (req.getTradeCode().isTrade()) {
				getLogThread().addLog(sql);
			}
			// 加入短信告警
			MessageHelper.sendMsg("kxd.trade.error", logPrefix(tradeCode)
					+ "[insertTradeDetail]异常，" + msg);
			logger.error(logPrefix(tradeCode) + "[insertTradeDetail]异常，", e);
			throw new AppException(e);
		}
	}

	/**
	 * 更新交易日志
	 * 
	 * @throws NamingException
	 * 
	 * @throws TradeError
	 */
	public static void updateTradeDetail(Request req, Result result,
			boolean success, String message) {
		String sql = null;
		result.setTradeEndTime(new Date());
		TradeCode tradeCode = null;
		try {
			tradeCode = req.getTradeCode();
			// 如果交易流水为空和isLogged为false和isStated为false不进行交易日志更新
			if (!(tradeCode.isTrade() || tradeCode.isLogged() || tradeCode
					.isStated()))
				return;

			// 只有终端交易流水不为空才更新交易明细表
			if (req.getTradeCode().isTrade()) { // 更新交易明细
				sql = "call "
						+ getUpdateTradeDetailSql(tradeCode.getTermGlide(),
								tradeCode.getOrgId(), result) + ";";
				getLogThread().addExecBeforeLog(sql);
				tradeLogger.updateTradeDetail(req, result, success, message);
			} else { // 统计或写日志
				// String msg = success ? "交易成功" : "交易失败";
				// if (result != null) {
				// if (result.getResultInfo() == null
				// || result.getResultInfo().isEmpty()) {
				// if (result.getResult() != null)
				// msg = result.getResult().toString();
				// } else
				// msg = result.getResultInfo();
				// }
				Integer userOrgId = null;
				if (tradeCode.getUserCityCode() != null
						&& !tradeCode.getUserCityCode().trim().isEmpty()) {
					userOrgId = TermHelper.getUserNoOrgId(tradeCode
							.getUserCityCode());
				}
				if (userOrgId == null)
					userOrgId = -1;
				req.getTradeCode().setTradeTime(new Date());
				tradeLogger.insertTradeLog(req, result, userOrgId, success,
						message);
			}
		} catch (Throwable e) {
			if (req.getTradeCode().isTrade()) {
				getLogThread().addLog(sql);
			}
			// 加入短信告警
			MessageHelper.sendMsg("kxd.trade.error",
					logPrefix(tradeCode) + "[updateTradeDetail]异常，"
							+ StringUnit.getExceptionMessage(e));
			logger.error(logPrefix(tradeCode) + "[updateTradeDetail]异常，", e);
		}
	}

	/**
	 * 日志记录前缀，方便定位错误记录
	 * 
	 * @param tradeCode
	 * @return
	 */
	static private String logPrefix(TradeCode tradeCode) {
		String prefixe = "";
		try {
			if (null != tradeCode) {
				prefixe = "[" + tradeCode.getTradeCode() + ", " //
						+ tradeCode.getTermId() + ", " //
						+ tradeCode.getTermGlide() + "," //
						+ tradeCode.getUserno() + "]";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return prefixe;
	}

	static public String getUpdateTradeDetailSql(BigDecimal termGlide,
			int orgId, Result result) {
		StringBuffer sb = new StringBuffer();
		sb.append("kxd_self_trade.update_trade_detail(");
		sb.append(termGlide + "," + result.getPhase().getValue() + ","
				+ result.getResult().getValue());
		sb.append(",'" + DataUnit.checkBytesLength(result.getResultInfo(), 50)
				+ "','");
		sb.append(DataUnit.checkNull(result.getTradeGlide(), ""));
		sb.append("','" + DataUnit.checkNull(result.getBankGlide(), ""));
		sb.append("','" + DataUnit.checkNull(result.getExtData0(), ""));
		sb.append("','" + DataUnit.checkNull(result.getExtData1(), ""));
		sb.append("','" + DataUnit.checkNull(result.getExtData2(), ""));
		sb.append("','" + DataUnit.checkNull(result.getExtData3(), ""));
		sb.append("','" + DataUnit.checkNull(result.getExtData4(), ""));
		sb.append("','" + result.getPayStatus().getValue() + "','");
		sb.append(result.getTradeStatus().getValue()
				+ "',0,"
				+ DateTime.milliSecondsBetween(result.getTradeEndTime(),
						result.getTradeStartTime()) + ")");
		return sb.toString();
	}

	static public String getInsertTradeDetailSql(boolean ingoreRepeatError,
			TradeCode tradeCode, int adjustAmount, CounterOption counterOption,
			Result result, Date uploadTime, Integer userOrgId,
			String redoParams, String clientip, String serverip) {
		if (uploadTime == null)
			uploadTime = new Date(0);
		if (userOrgId == null) {
			if (tradeCode.getUserCityCode() != null
					&& !tradeCode.getUserCityCode().trim().isEmpty()) {
				userOrgId = TermHelper.getUserNoOrgId(tradeCode
						.getUserCityCode());
			}
		}
		if (userOrgId == null)
			userOrgId = -1;
		StringBuffer sb = new StringBuffer(
				"kxd_self_trade.insert_trade_detail(");
		sb.append((ingoreRepeatError ? "1" : "0") + ",'"
				+ tradeCode.getTermGlide() + "'");
		sb.append(",to_date('"
				+ new DateTime(tradeCode.getTradeTime())
						.format("yyyyMMddHHmmss") + "','YYYYMMDDHH24MISS')");
		sb.append("," + tradeCode.getOrgId());
		sb.append("," + tradeCode.getTermId());
		sb.append("," + tradeCode.getTradeCodeId());
		sb.append(",'" + DataUnit.checkNull(tradeCode.getUserno(), "") + "'");
		sb.append(",'" + tradeCode.getBankCardNo() + "'");
		sb.append("," + result.getPhase().getValue());
		sb.append(",to_date('"
				+ new DateTime(uploadTime).format("yyyyMMddHHmmss")
				+ "','YYYYMMDDHH24MISS')");
		sb.append("," + tradeCode.getAmount());
		sb.append("," + tradeCode.getTradeAmount());
		sb.append("," + adjustAmount);
		sb.append("," + result.getResult().getValue());
		sb.append(",'" + DataUnit.checkBytesLength(result.getResultInfo(), 50)
				+ "'");
		sb.append("," + counterOption.getValue());
		sb.append(",'" + DataUnit.checkNull(result.getTradeGlide(), "") + "'");
		sb.append(",'" + DataUnit.checkNull(result.getBankGlide(), "") + "'");
		sb.append(",'" + DataUnit.checkNull(result.getExtData0(), "") + "'");
		sb.append(",'" + DataUnit.checkNull(result.getExtData1(), "") + "'");
		sb.append(",'" + DataUnit.checkNull(result.getExtData2(), "") + "'");
		sb.append(",'" + DataUnit.checkNull(result.getExtData3(), "") + "'");
		sb.append(",'" + DataUnit.checkNull(result.getExtData4(), "") + "'");
		sb.append(",'" + DataUnit.checkNull(clientip, "") + "'");
		sb.append(",'" + DataUnit.checkNull(serverip, "") + "'");
		if (redoParams.getBytes().length > 500)
			redoParams = "";
		sb.append("," + userOrgId + ","
				+ Integer.toString(result.getPayStatus().getValue()) + ","
				+ Integer.toString(result.getTradeStatus().getValue()) + ",'"
				+ redoParams + "')");
		return sb.toString();
	}

	private static WriteLogThread logThread;

	public static synchronized WriteLogThread getLogThread() {
		if (logThread == null)
			logThread = new WriteLogThread();
		return logThread;
	}

	static class SqlLog {
		String sql;
		boolean isError;

		public SqlLog(boolean isError, String sql) {
			this.sql = sql;
			this.isError = isError;
		}
	}

	public static class WriteLogThread extends Thread {
		private ArrayBlockingQueue<SqlLog> logs = new ArrayBlockingQueue<SqlLog>(
				50000);

		public WriteLogThread() {
			super("数据库更新日志错误记录线程");
			this.start();
		}

		public boolean addExecBeforeLog(String log) {
			try {
				return logs.offer(new SqlLog(false, log), 30, TimeUnit.SECONDS);
			} catch (Throwable e) {
				logger.debug("添加日志失败");
				return false;
			}
		}

		public boolean addLog(String log) {
			try {
				return logs.offer(new SqlLog(true, log), 30, TimeUnit.SECONDS);
			} catch (Throwable e) {
				logger.debug("添加日志失败");
				return false;
			}
		}

		@Override
		public void run() {
			FileOutputStream err = null;
			FileOutputStream log = null;
			String day = null;
			String errdir = KoneUtil.getKoneHome() + "/logs/dataerror/";
			String logdir = KoneUtil.getKoneHome() + "/logs/trade/";
			new File(errdir).mkdirs();
			new File(logdir).mkdirs();
			while (!this.isInterrupted()) {
				try {
					SqlLog str = logs.take();
					if (str != null) {
						String today = new DateTime().format("yyyy-MM-dd");
						if (day == null || !today.equals(day)) {
							if (err != null) {
								err.close();
								err = null;
							}
							if (log != null) {
								log.close();
								log = null;
							}
							err = new FileOutputStream(errdir + today + ".sql",
									true);
							log = new FileOutputStream(logdir + today + ".sql",
									true);
						}
						try {
							while (str != null) {
								if (str.isError)
									err.write((str.sql + "\r\n").getBytes());
								else
									log.write((str.sql + "\r\n").getBytes());
								str = logs.poll(1, TimeUnit.SECONDS);
							}
						} catch (InterruptedException e) {
							throw e;
						} catch (Throwable e) {
							logger.debug("写交易日志失败：", e);
							if (err != null) {
								err.close();
								err = null;
							}
							if (log != null) {
								log.close();
								log = null;
							}
						}
					}
				} catch (InterruptedException e) {
					break;
				} catch (Throwable e) {
					logger.debug("写交易日志失败：", e);
				}
			}
		}
	}
}
