package kxd.scs.beans;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import kxd.engine.dao.BaseBean;
import kxd.remote.scs.beans.BaseAlarmCode;
import kxd.remote.scs.beans.BaseDeviceType;
import kxd.remote.scs.beans.device.EditedDeviceType;
import kxd.remote.scs.interfaces.DeviceTypeBeanRemote;
import kxd.remote.scs.util.QueryResult;
import kxd.scs.dao.device.DeviceTypeDao;

@Stateless(name = "kxd-ejb-deviceTypeBean", mappedName = "kxd-ejb-deviceTypeBean")
public class DeviceTypeBean extends BaseBean implements DeviceTypeBeanRemote {

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<BaseDeviceType> getDeviceTypeList(long loginUserId,
			String keyword) {
		return DeviceTypeDao.getDeviceTypeList(getDao(), loginUserId, keyword);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public EditedDeviceType find(int deviceTypeId) {
		return DeviceTypeDao.find(getDao(), deviceTypeId);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public int add(long loginUserId, EditedDeviceType deviceType) {
		return DeviceTypeDao.add(getDao(), loginUserId, deviceType);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void delete(long loginUserId, Integer[] deviceTypeId) {
		DeviceTypeDao.delete(getDao(), loginUserId, deviceTypeId);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void edit(long loginUserId, EditedDeviceType deviceType) {
		DeviceTypeDao.edit(getDao(), loginUserId, deviceType);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public QueryResult<EditedDeviceType> queryDeviceType(boolean queryCount,
			long loginUserId, Integer filter, String filterContent,
			int firstResult, int maxResults) {
		return DeviceTypeDao.queryDeviceType(getDao(), queryCount, loginUserId,
				filter, filterContent, firstResult, maxResults);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public QueryResult<EditedDeviceType> queryDeviceType(boolean queryCount,
			long loginUserId, Integer filter, String filterContent,
			String code, Integer DeviceTypeDriverId, int startRecord,
			int maxResult) {
		return DeviceTypeDao.queryDeviceType(getDao(), queryCount, loginUserId,
				filter, filterContent, code, DeviceTypeDriverId, startRecord,
				maxResult);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void addAlarmCode(long loginUserId, BaseAlarmCode o) {
		DeviceTypeDao.addAlarmCode(getDao(), loginUserId, o);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void deleteAlarmCode(long loginUserId, int deviceType, int[] id) {
		DeviceTypeDao.deleteAlarmCode(getDao(), loginUserId, deviceType, id);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void editAlarmCode(long loginUserId, BaseAlarmCode o) {
		DeviceTypeDao.editAlarmCode(getDao(), loginUserId, o);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<BaseAlarmCode> getAlarmCodeList(long loginUserId, int deviceType) {
		return DeviceTypeDao
				.getAlarmCodeList(getDao(), loginUserId, deviceType);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public BaseAlarmCode findAlarmCode(int deviceType, int alarmCode) {
		return DeviceTypeDao.findAlarmCode(getDao(), deviceType, alarmCode);
	}
}
