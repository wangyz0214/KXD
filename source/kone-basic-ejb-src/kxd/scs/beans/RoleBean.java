package kxd.scs.beans;

import java.util.Collection;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import kxd.engine.dao.BaseBean;
import kxd.remote.scs.beans.BaseRole;
import kxd.remote.scs.interfaces.RoleBeanRemote;
import kxd.remote.scs.util.QueryResult;
import kxd.scs.dao.right.RoleDao;

@Stateless(name = "kxd-ejb-roleBean", mappedName = "kxd-ejb-roleBean")
public class RoleBean extends BaseBean implements RoleBeanRemote {

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public BaseRole find(int roleId) {
		return RoleDao.find(getDao(), roleId);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public int add(long loginUserId, BaseRole role) {
		return RoleDao.add(getDao(), loginUserId, role);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void delete(long loginUserId, int[] roleId) {
		RoleDao.delete(getDao(), loginUserId, roleId);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void edit(long loginUserId, BaseRole role) {
		RoleDao.edit(getDao(), loginUserId, role);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Collection<Integer> getFunction(int roleId) {
		return RoleDao.getFunction(getDao(), roleId);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public QueryResult<BaseRole> queryRole(boolean queryCount,
			long loginUserId, Integer filter, String filterContent,
			int firstResult, int maxResults) {
		return RoleDao.queryRole(getDao(), queryCount, loginUserId, filter,
				filterContent, firstResult, maxResults);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void setFunction(long loginUserId, int roleId,
			Collection<Integer> addList, Collection<Integer> removeList) {
		RoleDao.setFunction(getDao(), loginUserId, roleId, addList, removeList);
	}

}
