package kxd.scs.beans;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import kxd.engine.dao.BaseBean;
import kxd.remote.scs.beans.BaseBankTerm;
import kxd.remote.scs.beans.device.EditedBankTerm;
import kxd.remote.scs.interfaces.BankTermBeanRemote;
import kxd.remote.scs.util.QueryResult;
import kxd.scs.dao.term.BankTermDao;

@Stateless(name = "kxd-ejb-bankTermBean", mappedName = "kxd-ejb-bankTermBean")
public class BankTermBean extends BaseBean implements BankTermBeanRemote {

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public EditedBankTerm find(int termId) {
		return BankTermDao.find(getDao(), termId);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public int add(long loginUserId, EditedBankTerm term) {
		return BankTermDao.add(getDao(), loginUserId, term);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void delete(long loginUserId, Integer[] termId) {
		BankTermDao.delete(getDao(), loginUserId, termId);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void edit(long loginUserId, EditedBankTerm term) {
		BankTermDao.edit(getDao(), loginUserId, term);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<BaseBankTerm> getBankTermList(long loginUserId, String keyword) {
		return BankTermDao.getBankTermList(getDao(), loginUserId, keyword);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public QueryResult<EditedBankTerm> queryBankTerm(boolean queryCount,
			long loginUserId, Integer filter, String filterContent,
			int firstResult, int maxResults) {
		return BankTermDao.queryBankTerm(getDao(), queryCount, loginUserId,
				filter, filterContent, firstResult, maxResults);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public EditedBankTerm findbasebank(String termCode, String merchantaccount) {
		return BankTermDao.find(getDao(), termCode, merchantaccount);
	}
}