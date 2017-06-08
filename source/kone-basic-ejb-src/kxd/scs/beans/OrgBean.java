package kxd.scs.beans;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import kxd.engine.dao.BaseBean;
import kxd.remote.scs.beans.right.EditedOrg;
import kxd.remote.scs.interfaces.OrgBeanRemote;
import kxd.scs.dao.right.OrgDao;

@Stateless(name = "kxd-ejb-orgBean", mappedName = "kxd-ejb-orgBean")
public class OrgBean extends BaseBean implements OrgBeanRemote {
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public int add(long loginUserId, EditedOrg org) {
		return OrgDao.add(getDao(), loginUserId, org);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void delete(long loginUserId, Integer[] orgId) {
		OrgDao.delete(getDao(), loginUserId, orgId);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void edit(long loginUserId, EditedOrg org) {
		OrgDao.edit(getDao(), loginUserId, org);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public EditedOrg find(int orgId) {
		return OrgDao.find(getDao(), orgId);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public void move(long loginUserId, int orgId, int newParentOrgId) {
		OrgDao.move(getDao(), loginUserId, orgId, newParentOrgId);
	}
}
