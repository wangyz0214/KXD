package kxd.scs.beans;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import kxd.engine.dao.BaseBean;
import kxd.remote.scs.beans.BaseCommInterface;
import kxd.remote.scs.interfaces.CommInterfaceBeanRemote;
import kxd.remote.scs.util.QueryResult;
import kxd.scs.dao.appdeploy.CommInterfaceDao;

@Stateless(name = "kxd-ejb-commInterfaceBean", mappedName = "kxd-ejb-commInterfaceBean")
public class CommInterfaceBean extends BaseBean implements
		CommInterfaceBeanRemote {

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<BaseCommInterface> getCommInterfaceList(long loginUserId,
			String keyword) {
		return CommInterfaceDao.getCommInterfaceList(getDao(), loginUserId,
				keyword);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public BaseCommInterface find(short commInterfaceId) {
		return CommInterfaceDao.find(getDao(), commInterfaceId);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public short add(long loginUserId, BaseCommInterface commInterface) {
		return CommInterfaceDao.add(getDao(), loginUserId, commInterface);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void delete(long loginUserId, Short[] commInterfaceId) {
		CommInterfaceDao.delete(getDao(), loginUserId, commInterfaceId);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void edit(long loginUserId, BaseCommInterface commInterface) {
		CommInterfaceDao.edit(getDao(), loginUserId, commInterface);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public QueryResult<BaseCommInterface> queryCommInterface(
			boolean queryCount, long loginUserId, Integer filter,
			String filterContent, int firstResult, int maxResults) {
		return CommInterfaceDao.queryCommInterface(getDao(), queryCount,
				loginUserId, filter, filterContent, firstResult, maxResults);
	}
}
