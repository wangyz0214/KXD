package kxd.scs.beans;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import kxd.engine.dao.BaseBean;
import kxd.remote.scs.beans.BasePrintType;
import kxd.remote.scs.interfaces.PrintTypeBeanRemote;
import kxd.remote.scs.util.QueryResult;
import kxd.scs.dao.system.converters.PrintTypeDao;

@Stateless(name = "kxd-ejb-printTypeBean", mappedName = "kxd-ejb-printTypeBean")
public class PrintTypeBean extends BaseBean implements PrintTypeBeanRemote {

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<BasePrintType> getPrintTypeList(long loginUserId, String keyword) {
		return PrintTypeDao.getPrintTypeList(getDao(), loginUserId, keyword);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public BasePrintType find(int printTypeId) {
		return PrintTypeDao.find(getDao(), printTypeId);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void add(long loginUserId, BasePrintType printType) {
		PrintTypeDao.add(getDao(), loginUserId, printType);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void delete(long loginUserId, Short[] printTypeId) {
		PrintTypeDao.delete(getDao(), loginUserId, printTypeId);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void edit(long loginUserId, BasePrintType printType) {
		PrintTypeDao.edit(getDao(), loginUserId, printType);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public QueryResult<BasePrintType> queryPrintType(boolean queryCount,
			long loginUserId, Integer filter, String filterContent,
			int firstResult, int maxResults) {
		return PrintTypeDao.queryPrintType(getDao(), queryCount, loginUserId,
				filter, filterContent, firstResult, maxResults);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void addUserPrintTimes(String userno, int month, int printType,
			int times) {
		PrintTypeDao.addUserPrintTimes(getDao(), userno, month, printType,
				times);
	}
}
