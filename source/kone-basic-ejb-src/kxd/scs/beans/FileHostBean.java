package kxd.scs.beans;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import kxd.engine.dao.BaseBean;
import kxd.remote.scs.beans.BaseFileHost;
import kxd.remote.scs.interfaces.FileHostBeanRemote;
import kxd.remote.scs.util.QueryResult;
import kxd.scs.dao.fileservice.FileHostDao;

@Stateless(name = "kxd-ejb-fileHostBean", mappedName = "kxd-ejb-fileHostBean")
public class FileHostBean extends BaseBean implements FileHostBeanRemote {

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<BaseFileHost> getFileHostList(long loginUserId, String keyword) {
		return FileHostDao.getFileHostList(getDao(), loginUserId, keyword);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public BaseFileHost find(short fileHostId) {
		return FileHostDao.find(getDao(), fileHostId);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public int add(long loginUserId, BaseFileHost fileHost) {
		return FileHostDao.add(getDao(), loginUserId, fileHost);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void delete(long loginUserId, Short[] fileHostId) {
		FileHostDao.delete(getDao(), loginUserId, fileHostId);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void edit(long loginUserId, BaseFileHost fileHost) {
		FileHostDao.edit(getDao(), loginUserId, fileHost);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public QueryResult<BaseFileHost> queryFileHost(boolean queryCount,
			long loginUserId, Integer filter, String filterContent,
			int firstResult, int maxResults) {
		return FileHostDao.queryFileHost(getDao(), queryCount, loginUserId,
				filter, filterContent, firstResult, maxResults);
	}
}
