package kxd.scs.beans;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import kxd.engine.dao.BaseBean;
import kxd.remote.scs.beans.BaseTermMoveItem;
import kxd.remote.scs.beans.device.EditedTerm;
import kxd.remote.scs.beans.device.QueryedTerm;
import kxd.remote.scs.interfaces.TermBeanRemote;
import kxd.remote.scs.util.QueryResult;
import kxd.scs.dao.term.TermDao;

@Stateless(name = "kxd-ejb-termBean", mappedName = "kxd-ejb-termBean")
public class TermBean extends BaseBean implements TermBeanRemote {

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public EditedTerm find(int termId) {
		return TermDao.find(getDao(), termId);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public int add(long loginUserId, EditedTerm term) {
		return TermDao.add(getDao(), loginUserId, term);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void delete(long loginUserId, Integer[] termId) {
		TermDao.delete(getDao(), loginUserId, termId);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void edit(long loginUserId, EditedTerm term) {
		TermDao.edit(getDao(), loginUserId, term);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void move(long loginUserId, int termId, int orgId) {
		TermDao.move(getDao(), loginUserId, termId, orgId);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public QueryResult<QueryedTerm> queryTerm(String extTables,
			String extWheres, boolean queryCount, long loginUserId,
			Integer orgId, Integer manufId, Integer typeId, Integer filter,
			String filterContent, int firstResult, int maxResults) {
		return TermDao.queryTerm(getDao(), extTables, extWheres, queryCount,
				loginUserId, orgId, manufId, typeId, filter, filterContent,
				firstResult, maxResults);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<BaseTermMoveItem> getTermMoveList(long loginUserId,
			Integer termId) {
		return TermDao.getTermMoveList(getDao(), loginUserId, termId);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public EditedTerm findTerm(String manufTermCode) {
		return TermDao.find(getDao(), manufTermCode);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public QueryResult<QueryedTerm> queryTerm(String extTables,
			String extWheres, boolean queryCount, long loginUserId,
			Integer orgId, Integer manufId, Integer typeId, String bankTermNo,
			String manufNo, Integer filter, String filterContent,
			int firstResult, int maxResults) {
		return TermDao.queryTerm(getDao(), extTables, extWheres, queryCount,
				loginUserId, orgId, manufId, typeId, bankTermNo, manufNo,
				filter, filterContent, firstResult, maxResults);
	}
}