package kxd.engine.scs.monitor;

import org.apache.log4j.Logger;

public class TestTermStatusEventListener implements TermStatusEventListener {

	private static Logger logger = Logger
			.getLogger(TestTermStatusEventListener.class);

	@Override
	public void statusChange(CachedMonitoredTerm term) {
		logger.debug("term status change[termid=" + term.getTermId() + "]");
	}

	@Override
	public void termAdded(CachedMonitoredTerm term) {
		logger.debug("term added[termid=" + term.getTermId() + "]");
	}

	@Override
	public void termModified(CachedMonitoredTerm term, int oldParentOrgId) {
		logger.debug("term modified[termid=" + term.getTermId() + "]");
	}

	@Override
	public void termRemoved(CachedMonitoredTerm term) {
		logger.debug("term removed[termid=" + term.getTermId() + "]");
	}

	@Override
	public void termDeviceAdded(CachedMonitoredTerm term, int deviceId) {
		logger.debug("term device added[termid=" + term.getTermId() + "]");
	}

	@Override
	public void termDeviceRemoved(CachedMonitoredTerm term, int deviceId) {
		logger.debug("term device removed[termid=" + term.getTermId() + "]");
	}

	@Override
	public void termLogined(CachedMonitoredTerm term) {
		logger.debug("term logined[termid=" + term.getTermId() + "]");
	}

	@Override
	public void termActived(CachedMonitoredTerm term) {
		logger.debug("term actived[termid=" + term.getTermId() + "]");
	}

	@Override
	public void termPauseResume(CachedMonitoredTerm term, boolean pause) {
		logger.debug("term pause/resume[termid=" + term.getTermId() + "]");
	}

}
