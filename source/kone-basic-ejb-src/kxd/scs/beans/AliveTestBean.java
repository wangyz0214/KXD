package kxd.scs.beans;

import java.util.concurrent.atomic.AtomicLong;

import javax.ejb.Stateless;

import org.apache.log4j.Logger;

import kxd.net.naming.AliveTestBeanRemote;

@Stateless(name = "kxd-ejb-aliveTestBean", mappedName = "kxd-ejb-aliveTestBean")
public class AliveTestBean implements AliveTestBeanRemote {
	static AtomicLong testTimes = new AtomicLong(0);
	static Logger logger = Logger.getLogger(AliveTestBean.class);

	@Override
	public String test(String msg) {
		long l = testTimes.getAndAdd(1);
		if (l > Long.MAX_VALUE / 2) {
			l = 0;
			testTimes.set(0);
		}
		logger.debug("call alive test(" + msg + ") - times: " + l);
		return msg + "-" + l;
	}

	@Override
	public boolean isOk(String source) {
		logger.debug("call alive isOk(" + source + ")");
		return true;
	}

}
