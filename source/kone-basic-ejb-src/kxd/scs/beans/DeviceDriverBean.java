package kxd.scs.beans;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import kxd.engine.dao.BaseBean;
import kxd.remote.scs.beans.BaseDeviceDriver;
import kxd.remote.scs.beans.BaseDeviceDriverFile;
import kxd.remote.scs.interfaces.DeviceDriverBeanRemote;
import kxd.remote.scs.util.QueryResult;
import kxd.scs.dao.device.DeviceDriverDao;
import kxd.util.KeyValue;

@Stateless(name = "kxd-ejb-deviceDriverBean", mappedName = "kxd-ejb-deviceDriverBean")
public class DeviceDriverBean extends BaseBean implements
		DeviceDriverBeanRemote {

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<BaseDeviceDriver> getDeviceDriverList(long loginUserId,
			String keyword) {
		return DeviceDriverDao.getDeviceDriverList(getDao(), loginUserId,
				keyword);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public BaseDeviceDriver find(int deviceId) {
		return DeviceDriverDao.find(getDao(), deviceId);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public KeyValue<Integer, String> add(long loginUserId, BaseDeviceDriver o) {
		return DeviceDriverDao.add(getDao(), loginUserId, o);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public String[] delete(long loginUserId, Integer[] id) {
		return DeviceDriverDao.delete(getDao(), loginUserId, id);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public String edit(long loginUserId, BaseDeviceDriver o) {
		return DeviceDriverDao.edit(getDao(), loginUserId, o);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public QueryResult<BaseDeviceDriver> queryDeviceDriver(boolean queryCount,
			long loginUserId, Integer filter, String filterContent,
			int firstResult, int maxResults) {
		return DeviceDriverDao.queryDeviceDriver(getDao(), queryCount,
				loginUserId, filter, filterContent, firstResult, maxResults);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public int addDeviceDriverFile(long loginUserId, BaseDeviceDriverFile file) {
		return DeviceDriverDao.addDeviceDriverFile(getDao(), loginUserId, file);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public String deleteDeviceDriverFile(long loginUserId, int fileId) {
		return DeviceDriverDao.deleteDeviceDriverFile(getDao(), loginUserId,
				fileId);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<BaseDeviceDriverFile> getDeviceDriverFiles(int driverId) {
		return DeviceDriverDao.getDeviceDriverFiles(getDao(), driverId);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public BaseDeviceDriverFile findDriverFile(long fileId) {
		return DeviceDriverDao.findDriverFile(getDao(), fileId);
	}
}
