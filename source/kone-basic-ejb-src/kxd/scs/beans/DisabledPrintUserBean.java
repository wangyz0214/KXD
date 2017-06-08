package kxd.scs.beans;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import kxd.engine.dao.BaseBean;
import kxd.remote.scs.beans.BaseDisabledPrintUser;
import kxd.remote.scs.interfaces.DisabledPrintUserBeanRemote;
import kxd.remote.scs.util.QueryResult;
import kxd.scs.dao.system.converters.DisablePrintUserDao;

@Stateless(name = "kxd-ejb-disabledPrintUserBean", mappedName = "kxd-ejb-disabledPrintUserBean")
public class DisabledPrintUserBean extends BaseBean implements
		DisabledPrintUserBeanRemote {

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public BaseDisabledPrintUser find(long id) {
		return DisablePrintUserDao.find(getDao(), id);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public long add(long loginUserId, BaseDisabledPrintUser o) {
		return DisablePrintUserDao.add(getDao(), loginUserId, o);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void delete(long loginUserId, long[] id) {
		DisablePrintUserDao.delete(getDao(), loginUserId, id);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void edit(long loginUserId, BaseDisabledPrintUser o) {
		DisablePrintUserDao.edit(getDao(), loginUserId, o);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public QueryResult<BaseDisabledPrintUser> queryDisabledPrintUser(
			boolean queryCount, long loginUserId, Integer filter,
			String filterContent, int firstResult, int maxResults) {
		return DisablePrintUserDao.queryDisabledPrintUser(getDao(), queryCount,
				loginUserId, filter, filterContent, firstResult, maxResults);
	}

}
