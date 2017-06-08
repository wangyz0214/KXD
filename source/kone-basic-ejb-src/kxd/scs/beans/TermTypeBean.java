package kxd.scs.beans;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import kxd.engine.dao.BaseBean;
import kxd.remote.scs.beans.BaseTermType;
import kxd.remote.scs.beans.BaseTermTypeDevice;
import kxd.remote.scs.beans.device.EditedTermType;
import kxd.remote.scs.beans.device.EditedTermTypeDevice;
import kxd.remote.scs.interfaces.TermTypeBeanRemote;
import kxd.remote.scs.util.QueryResult;
import kxd.scs.dao.term.TermTypeDao;

@Stateless(name = "kxd-ejb-termTypeBean", mappedName = "kxd-ejb-termTypeBean")
public class TermTypeBean extends BaseBean implements TermTypeBeanRemote {

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<BaseTermType> getTermTypeList(long loginUserId, String keyword) {
		return TermTypeDao.getTermTypeList(getDao(), loginUserId, keyword);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public EditedTermType find(int termTypeId) {
		return TermTypeDao.find(getDao(), termTypeId);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public int add(long loginUserId, EditedTermType termType) {
		return TermTypeDao.add(getDao(), loginUserId, termType);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void delete(long loginUserId, Integer[] termTypeId) {
		TermTypeDao.delete(getDao(), loginUserId, termTypeId);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void edit(long loginUserId, EditedTermType termType) {
		TermTypeDao.edit(getDao(), loginUserId, termType);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public QueryResult<EditedTermType> queryTermType(boolean queryCount,
			long loginUserId, Integer filter, String filterContent,
			int firstResult, int maxResults) {
		return TermTypeDao.queryTermType(getDao(), queryCount, loginUserId,
				filter, filterContent, firstResult, maxResults);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public QueryResult<EditedTermType> queryTermType(boolean queryCount,
			long loginUserId, Integer filter, String filterContent,
			String desp, Integer appId, Integer manufId, Integer cashFlag,
			Integer fixType, int firstResult, int maxResults) {
		return TermTypeDao.queryTermType(getDao(), queryCount, loginUserId,
				filter, filterContent, desp, appId, manufId, cashFlag, fixType,
				firstResult, maxResults);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void addTermTypeDevice(long loginUserId, EditedTermTypeDevice device) {
		TermTypeDao.addTermTypeDevice(getDao(), loginUserId, device);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void deleteTermTypeDevice(long loginUserId, int typeId,
			int[] deviceId) {
		TermTypeDao.deleteTermTypeDevice(getDao(), loginUserId, typeId,
				deviceId);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void editTermTypeDevice(long loginUserId, EditedTermTypeDevice device) {
		TermTypeDao.editTermTypeDevice(getDao(), loginUserId, device);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<BaseTermTypeDevice> getTermTypeDeviceList(long loginUserId,
			int typeId) {
		return TermTypeDao.getTermTypeDeviceList(getDao(), loginUserId, typeId);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public EditedTermTypeDevice findDevice(long loginUserId, int typeId,
			int deviceId) {
		return TermTypeDao.findDevice(getDao(), typeId, deviceId);
	}
}
