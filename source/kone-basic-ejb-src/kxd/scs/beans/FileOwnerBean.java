package kxd.scs.beans;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import kxd.engine.dao.BaseBean;
import kxd.remote.scs.beans.BaseFileOwner;
import kxd.remote.scs.interfaces.FileOwnerBeanRemote;
import kxd.remote.scs.util.QueryResult;
import kxd.scs.dao.fileservice.FileOwnerDao;

@Stateless(name = "kxd-ejb-fileOwnerBean", mappedName = "kxd-ejb-fileOwnerBean")
public class FileOwnerBean extends BaseBean implements FileOwnerBeanRemote {

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<BaseFileOwner> getFileOwnerList(long loginUserId, String keyword) {
		return FileOwnerDao.getFileOwnerList(getDao(), loginUserId, keyword);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public BaseFileOwner find(short fileOwnerId) {
		return FileOwnerDao.find(getDao(), fileOwnerId);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public int add(long loginUserId, BaseFileOwner fileOwner) {
		return FileOwnerDao.add(getDao(), loginUserId, fileOwner);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void delete(long loginUserId, Short[] fileOwnerId) {
		FileOwnerDao.delete(getDao(), loginUserId, fileOwnerId);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void edit(long loginUserId, BaseFileOwner fileOwner) {
		FileOwnerDao.edit(getDao(), loginUserId, fileOwner);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public QueryResult<BaseFileOwner> queryFileOwner(boolean queryCount,
			long loginUserId, Integer filter, String filterContent,
			int firstResult, int maxResults) {
		return FileOwnerDao.queryFileOwner(getDao(), queryCount, loginUserId,
				filter, filterContent, firstResult, maxResults);
	}
}
