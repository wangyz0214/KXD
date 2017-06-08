package kxd.scs.beans;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import kxd.engine.dao.BaseBean;
import kxd.remote.scs.beans.BaseFileUser;
import kxd.remote.scs.interfaces.FileUserBeanRemote;
import kxd.remote.scs.util.QueryResult;
import kxd.scs.dao.fileservice.FileUserDao;

@Stateless(name = "kxd-ejb-fileUserBean", mappedName = "kxd-ejb-fileUserBean")
public class FileUserBean extends BaseBean implements FileUserBeanRemote {

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<BaseFileUser> getFileUserList(long loginUserId, String keyword) {
		return FileUserDao.getFileUserList(getDao(), loginUserId, keyword);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public BaseFileUser find(String fileUserId) {
		return FileUserDao.find(getDao(), fileUserId);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void add(long loginUserId, BaseFileUser fileUser) {
		FileUserDao.add(getDao(), loginUserId, fileUser);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void delete(long loginUserId, String[] fileUserId) {
		FileUserDao.delete(getDao(), loginUserId, fileUserId);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void edit(long loginUserId, BaseFileUser fileUser) {
		FileUserDao.edit(getDao(), loginUserId, fileUser);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public QueryResult<BaseFileUser> queryFileUser(boolean queryCount,
			long loginUserId, Integer filter, String filterContent,
			int firstResult, int maxResults) {
		return FileUserDao.queryFileUser(getDao(), queryCount, loginUserId,
				filter, filterContent, firstResult, maxResults);
	}
}
