package kxd.scs.beans;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import kxd.engine.dao.BaseBean;
import kxd.remote.scs.beans.BaseFileItem;
import kxd.remote.scs.interfaces.FileListBeanRemote;
import kxd.scs.dao.fileservice.FileListDao;

@Stateless(name = "kxd-ejb-fileListBean", mappedName = "kxd-ejb-fileListBean")
public class FileListBean extends BaseBean implements FileListBeanRemote {

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public BaseFileItem find(String fileItemId) {
		return FileListDao.find(getDao(), fileItemId);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void add(long loginUserId, BaseFileItem fileItem) {
		FileListDao.add(getDao(), loginUserId, fileItem);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public BaseFileItem delete(long loginUserId, String fileItemId) {
		return FileListDao.delete(getDao(), loginUserId, fileItemId);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public BaseFileItem edit(long loginUserId, String oldItemId,
			BaseFileItem fileItem) {
		return FileListDao.edit(getDao(), loginUserId, oldItemId, fileItem);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void updateVisit(String itemId, int visitTimes) {
		FileListDao.updateVisit(getDao(), itemId, visitTimes);
	}
}
