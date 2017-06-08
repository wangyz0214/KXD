package kxd.scs.beans.service;

import java.util.Date;
import java.util.Iterator;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import kxd.engine.dao.BaseBean;
import kxd.remote.scs.interfaces.service.SerialNoServiceBeanRemote;
import kxd.scs.beans.SerialFactory;

import org.apache.log4j.Logger;

@Stateless(name = "kxd-ejb-serialNoServiceBean", mappedName = "kxd-ejb-serialNoServiceBean")
public class SerialNoServiceBean extends BaseBean implements
		SerialNoServiceBeanRemote {
	static Logger logger = Logger.getLogger(SerialNoServiceBean.class);
	@Resource
	TimerService ts;

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void startMonitorTimer() {
		Iterator<?> it = ts.getTimers().iterator();
		while (it.hasNext()) {
			Timer timer = (Timer) it.next();
			String info = timer.getInfo().toString();
			if (info.equals("termmonitortimer")) {
				logger.info("timer exists!");
				return;
			}
		}
		ts.createTimer(new Date(), 300 * 1000, "termmonitortimer");
		logger.info("create timer.");
	}

	/**
	 * 更新报表的定时器
	 */
	//
	@Timeout
	public void timer(Timer timer) {
		monitorFaultTerms();
		logger.debug("update fault report.");
	}

	private void monitorFaultTerms() {
		// Enumeration<CachedTerm> en = getTermList().elements();
		// // 获取目前处于错误状态的终端
		// ArrayList<CachedTerm> ls = new ArrayList<CachedTerm>();
		// while (en.hasMoreElements()) {
		// CachedTerm term = en.nextElement();
		// if (term.getLastFaultTime() != null) {
		// ls.add(term);
		// }
		// }
		// Iterator<CachedTerm> it = ls.iterator();
		// DateTime now = new DateTime();
		// while (it.hasNext()) {
		// updateFualtTimes(it.next(), now);
		// }
	}

	// private void updateFualtTimes(CachedTerm term, DateTime now) {
	// Term t = em.find(Term.class, term.getId());
	// if (t == null)
	// return;
	// int month = Integer.valueOf(now.format("yyyyMM"));
	// int day = now.getDay();
	// int termId = term.getId();
	// int fault = 0, response = 0, restore = 0;
	// DateTime oldTime;
	// if (term.getLastFaultRecordTime() != null)
	// oldTime = new DateTime(term.getLastFaultRecordTime());
	// else
	// oldTime = new DateTime(term.getLastFaultTime());
	// fault = (int) now.secondsBetween(oldTime);
	// if (term.getLastProcessTime() == null) {
	// if (FaultProcFlag.PROCESSING.equals(term.getProcessFlag())) {
	// term.setLastProcessTime(now.getTime());
	// t.setLastProcessTime(now.getTime());
	// response = (int) new DateTime(term.getLastFaultTime())
	// .secondsBetween(now);
	// }
	// } else {
	// restore = fault;
	// }
	// term.setLastFaultRecordTime(now.getTime());
	// t.setLastFaultRecordTime(now.getTime());
	// em.merge(t);
	//
	// if (queryNative(
	// "select termid from faultreport where termid=" + termId
	// + " and month=" + month).isEmpty()) {
	// executeNative("insert into faultreport(termid,month,ft,ft" + day
	// + ",rt,rt" + day + ",ot,ot" + day + ") values(" + termId
	// + "," + month + "," + fault + "," + fault + "," + response
	// + "," + response + "," + restore + "," + restore + ")");
	//
	// } else {
	// executeNative("update faultreport set ft=ft+" + fault + ",ft" + day
	// + "=ft" + day + "+" + fault + ",rt=rt+" + response + ",rt"
	// + day + "=rt" + day + "+" + fault + ",ot=ot+" + response
	// + ",ot" + day + "=ot" + day + "+" + restore
	// + " where termid=" + termId + " and month=" + month);
	// }
	// }

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public long getNextDaySerial(String key, long minValue, long maxValue) {
		return SerialFactory.getNextSerial(getDao(), key, true, minValue,
				maxValue);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public long getNextCircleSerial(String key, long minValue, long maxValue) {
		return SerialFactory.getNextSerial(getDao(), key, false, minValue,
				maxValue);
	}
}
