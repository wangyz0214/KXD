package kxd.scs.beans;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import kxd.engine.dao.BaseBean;
import kxd.remote.scs.beans.BaseDevice;
import kxd.remote.scs.beans.device.EditedDevice;
import kxd.remote.scs.interfaces.DeviceBeanRemote;
import kxd.remote.scs.util.QueryResult;
import kxd.scs.dao.device.DeviceDao;

@Stateless(name = "kxd-ejb-deviceBean", mappedName = "kxd-ejb-deviceBean")
public class DeviceBean extends BaseBean implements DeviceBeanRemote {

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<BaseDevice> getDeviceList(long loginUserId,
			Integer exceptTermType, String keyword) {
		return DeviceDao.getDeviceList(getDao(), loginUserId, keyword);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public EditedDevice find(int deviceId) {
		return DeviceDao.find(getDao(), deviceId);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public int add(long loginUserId, EditedDevice device) {
		return DeviceDao.add(getDao(), loginUserId, device);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void delete(long loginUserId, Integer[] deviceId) {
		DeviceDao.delete(getDao(), loginUserId, deviceId);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void edit(long loginUserId, EditedDevice device) {
		DeviceDao.edit(getDao(), loginUserId, device);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public QueryResult<EditedDevice> queryDevice(boolean queryCount,
			long loginUserId, Integer filter, String filterContent,
			int firstResult, int maxResults) {
		return DeviceDao.queryDevice(getDao(), queryCount, loginUserId, filter,
				filterContent, firstResult, maxResults);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public QueryResult<EditedDevice> queryDevice(boolean queryCount,
			long loginUserId, Integer filter, String filterContent,
			Integer typeId, int firstResult, int maxResults) {
		return DeviceDao.queryDevice(getDao(), queryCount, loginUserId, filter,
				filterContent,typeId, firstResult, maxResults);
	}
}
