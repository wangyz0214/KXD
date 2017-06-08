package kxd.scs.beans;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import kxd.engine.dao.BaseBean;
import kxd.remote.scs.beans.BasePrintAdCategory;
import kxd.remote.scs.interfaces.PrintAdCategoryBeanRemote;
import kxd.remote.scs.util.QueryResult;
import kxd.scs.dao.publish.PrintAdCategoryDao;

@Stateless(name = "kxd-ejb-printAdCategoryBean", mappedName = "kxd-ejb-printAdCategoryBean")
public class PrintAdCategoryBean extends BaseBean implements
		PrintAdCategoryBeanRemote {

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<BasePrintAdCategory> getPrintAdCategoryList(long loginUserId,
			String keyword) {
		return PrintAdCategoryDao.getPrintAdCategoryList(getDao(), loginUserId,
				keyword);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public BasePrintAdCategory find(short printAdCategoryId) {
		return PrintAdCategoryDao.find(getDao(), printAdCategoryId);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void add(long loginUserId, BasePrintAdCategory printAdCategory) {
		PrintAdCategoryDao.add(getDao(), loginUserId, printAdCategory);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void delete(long loginUserId, short[] printAdCategoryId) {
		PrintAdCategoryDao.delete(getDao(), loginUserId, printAdCategoryId);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void edit(long loginUserId, BasePrintAdCategory printAdCategory) {
		PrintAdCategoryDao.edit(getDao(), loginUserId, printAdCategory);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public QueryResult<BasePrintAdCategory> queryPrintAdCategory(
			boolean queryCount, long loginUserId, Integer filter,
			String filterContent, int firstResult, int maxResults) {
		return PrintAdCategoryDao.queryPrintAdCategory(getDao(), queryCount,
				loginUserId, filter, filterContent, firstResult, maxResults);
	}
}
