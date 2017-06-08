package kxd.scs.beans;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import kxd.engine.dao.BaseBean;
import kxd.remote.scs.beans.BaseOrgBusinessOpenClose;
import kxd.remote.scs.interfaces.OrgBusinessOpenCloseRemote;
import kxd.remote.scs.util.QueryResult;
import kxd.scs.dao.maint.OrgBusinessOpenCloseDao;

@Stateless(name = "kxd-ejb-orgBusinessOpenCloseBean", mappedName = "kxd-ejb-orgBusinessOpenCloseBean")
public class OrgBusinessOpenCloseBean extends BaseBean implements
		OrgBusinessOpenCloseRemote {

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public QueryResult<BaseOrgBusinessOpenClose> query(boolean queryCount,
			long loginUserId, Integer orgId, Integer filter,
			String filterContent, int firstResult, int maxResults) {
		return OrgBusinessOpenCloseDao.query(getDao(), queryCount, loginUserId,
				orgId, filter, filterContent, firstResult, maxResults);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public void delete(long loginUserId, Long[] ids) {
		OrgBusinessOpenCloseDao.delete(getDao(), loginUserId, ids);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public BaseOrgBusinessOpenClose find(long id) {
		return OrgBusinessOpenCloseDao.find(getDao(), id);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public long add(long loginUserId, BaseOrgBusinessOpenClose o) {
		return OrgBusinessOpenCloseDao.add(getDao(), loginUserId, o);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public void edit(long loginUserId, BaseOrgBusinessOpenClose o) {
		OrgBusinessOpenCloseDao.edit(getDao(), loginUserId, o);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public List<BaseOrgBusinessOpenClose> getList(long loginUserId,
			String keyword) {
		return OrgBusinessOpenCloseDao.getList(getDao(), loginUserId, keyword);
	}
}
