package kxd.engine.scs.monitor;

import org.apache.log4j.Logger;

import kxd.engine.cache.ejb.beans.EjbCachedOrgNode;

public class TestOrgStatusEventListener implements OrgStatusEventListener {
	private static Logger logger = Logger
			.getLogger(TestTermStatusEventListener.class);

	@Override
	public void orgAdded(EjbCachedOrgNode org) {
		logger.debug("org added[id=" + org.getId() + "]");
	}

	@Override
	public void orgModified(EjbCachedOrgNode org) {
		logger.debug("org modified[id=" + org.getId() + "]");
	}

	@Override
	public void orgRemoved(EjbCachedOrgNode org) {
		logger.debug("org removed[id=" + org.getId() + "]");
	}

	@Override
	public void orgMoved(EjbCachedOrgNode org, EjbCachedOrgNode newParentOrg) {
		logger.debug("org moved[id=" + org.getId() + "]");
	}

}
