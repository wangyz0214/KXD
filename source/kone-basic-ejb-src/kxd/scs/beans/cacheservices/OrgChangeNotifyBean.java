package kxd.scs.beans.cacheservices;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.log4j.Logger;

import kxd.engine.cache.ejb.interfaces.OrgChangeNotifyBeanRemote;
import kxd.engine.dao.BaseBean;
import kxd.remote.scs.beans.right.EditedOrg;
import kxd.scs.dao.cache.CacheMonitorDao;

@Stateless(name = "kxd-ejb-orgChangeNotifyBean", mappedName = "kxd-ejb-orgChangeNotifyBean")
public class OrgChangeNotifyBean extends BaseBean implements
		OrgChangeNotifyBeanRemote {
	private static Logger logger = Logger.getLogger(OrgChangeNotifyBean.class);

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void orgAdded(EditedOrg org) {
		logger.debug("org[id=" + org.getId() + "] added.");
		CacheMonitorDao.orgAdded(getDao(), org);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void orgModified(EditedOrg org) {
		logger.debug("org[id=" + org.getId() + "] modified.");
		CacheMonitorDao.orgModified(getDao(), org);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public void orgMoved(int orgId, int newParentOrgId) {
		logger.debug("org[id=" + orgId + "] moved.");
		CacheMonitorDao.orgMoved(getDao(), orgId, newParentOrgId);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public void orgRemoved(int orgId) {
		logger.debug("org[id=" + orgId + "] removed.");
		CacheMonitorDao.orgRemoved(getDao(), orgId);
	}

}
