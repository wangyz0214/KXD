package kxd.scs.beans;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import kxd.engine.dao.BaseBean;
import kxd.remote.scs.beans.BaseTermBusinessOpenClose;
import kxd.remote.scs.interfaces.TermBusinessOpenCloseRemote;
import kxd.remote.scs.util.QueryResult;
import kxd.scs.dao.maint.TermBusinessOpenCloseDao;

@Stateless(name = "kxd-ejb-termBusinessOpenCloseBean", mappedName = "kxd-ejb-termBusinessOpenCloseBean")
public class TermBusinessOpenCloseBean extends BaseBean implements
		TermBusinessOpenCloseRemote {

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public QueryResult<BaseTermBusinessOpenClose> query(boolean queryCount,
			long loginUserId, Integer orgId, Integer filter,
			String filterContent, int firstResult, int maxResults) {
		return TermBusinessOpenCloseDao.query(getDao(), queryCount,
				loginUserId, orgId, filter, filterContent, firstResult,
				maxResults);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public void delete(long loginUserId, Long[] ids) {
		TermBusinessOpenCloseDao.delete(getDao(), loginUserId, ids);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public BaseTermBusinessOpenClose find(long id) {
		return TermBusinessOpenCloseDao.find(getDao(), id);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public long add(long loginUserId, BaseTermBusinessOpenClose o) {
		return TermBusinessOpenCloseDao.add(getDao(), loginUserId, o);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public void edit(long loginUserId, BaseTermBusinessOpenClose o) {
		TermBusinessOpenCloseDao.edit(getDao(), loginUserId, o);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public List<BaseTermBusinessOpenClose> getList(long loginUserId,
			String keyword) {
		return TermBusinessOpenCloseDao.getList(getDao(), loginUserId, keyword);
	}
}
