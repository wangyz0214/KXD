package kxd.scs.beans.cacheservices;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.naming.NamingException;

import kxd.engine.cache.ejb.beans.EjbCachedOrg;
import kxd.engine.cache.ejb.beans.EjbCachedOrgNode;
import kxd.engine.cache.ejb.interfaces.EjbCacheServiceBeanRemote;
import kxd.engine.dao.BaseBean;
import kxd.remote.scs.beans.right.EditedOrg;
import kxd.remote.scs.beans.right.QueryedOrg;
import kxd.scs.dao.cache.CacheMonitorDao;

@Stateless(name = "kxd-ejb-ejbCacheServiceBean", mappedName = "kxd-ejb-ejbCacheServiceBean")
public class EjbCacheServiceBean extends BaseBean implements
		EjbCacheServiceBeanRemote {
	static String lastOrgModified = UUID.randomUUID().toString();

	static synchronized void updateLastOrgModified() {
		lastOrgModified = UUID.randomUUID().toString();
	}

	static synchronized String getSLastOrgModified() {
		return lastOrgModified;
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<QueryedOrg> getOrgTreeItems(Integer parentOrgId, int depths,
			Integer selectedOrgId, boolean includeSelf, String keyword) {
		return CacheMonitorDao.getOrgTreeItems(getDao(), parentOrgId, depths,
				selectedOrgId, includeSelf, keyword);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public String getOrgTreePath(int orgId, String separator) {
		return CacheMonitorDao.getOrgTreePath(getDao(), orgId, separator);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void orgAdded(EditedOrg org) {
		CacheMonitorDao.orgAdded(getDao(), org);
		updateLastOrgModified();
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void orgModified(EditedOrg org) {
		CacheMonitorDao.orgModified(getDao(), org);
		updateLastOrgModified();
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<QueryedOrg> getParentOrgs(Integer orgId, boolean includeSelf)
			throws NamingException {
		return CacheMonitorDao.getParentOrgs(getDao(), orgId, includeSelf);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Map<?, ?> getOrgProvinceCity(List<?> orgIdList) {
		return CacheMonitorDao.getOrgProvinceCity(getDao(), orgIdList);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public void orgMoved(int orgId, int newParentOrgId) {
		CacheMonitorDao.orgMoved(getDao(), orgId, newParentOrgId);
		updateLastOrgModified();
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public String getLastOrgModified() {
		return getSLastOrgModified();
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public List<EjbCachedOrg> getOrgTree() {
		List<EjbCachedOrg> list = new ArrayList<EjbCachedOrg>();
		EjbCachedOrgNode root = CacheMonitorDao.getOrgTree(getDao());
		root.getAllChildrenData(list);
		return list;
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public void orgRemoved(int orgId) {
		CacheMonitorDao.orgRemoved(getDao(), orgId);
		updateLastOrgModified();
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public void initCacheOnly() {
		CacheMonitorDao.init(getDao());
	}
}
