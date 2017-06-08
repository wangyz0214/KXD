package kxd.scs.beans;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import kxd.engine.dao.BaseBean;
import kxd.remote.scs.beans.BaseDeviceTypeDriver;
import kxd.remote.scs.interfaces.DeviceTypeDriverBeanRemote;
import kxd.remote.scs.util.QueryResult;
import kxd.scs.dao.device.DeviceTypeDriverDao;
import kxd.util.KeyValue;

@Stateless(name = "kxd-ejb-deviceTypeDriverBean", mappedName = "kxd-ejb-deviceTypeDriverBean")
public class DeviceTypeDriverBean extends BaseBean implements
		DeviceTypeDriverBeanRemote {

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<BaseDeviceTypeDriver> getDeviceTypeDriverList(long loginUserId,
			String keyword) {
		return DeviceTypeDriverDao.getDeviceTypeDriverList(getDao(),
				loginUserId, keyword);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public BaseDeviceTypeDriver find(int deviceId) {
		return DeviceTypeDriverDao.find(getDao(), deviceId);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public KeyValue<Integer, String> add(long loginUserId,
			BaseDeviceTypeDriver o) {
		return DeviceTypeDriverDao.add(getDao(), loginUserId, o);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public String[] delete(long loginUserId, Integer[] id) {
		return DeviceTypeDriverDao.delete(getDao(), loginUserId, id);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public String edit(long loginUserId, BaseDeviceTypeDriver o) {
		return DeviceTypeDriverDao.edit(getDao(), loginUserId, o);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public QueryResult<BaseDeviceTypeDriver> queryDeviceTypeDriver(
			boolean queryCount, long loginUserId, Integer filter,
			String filterContent, int firstResult, int maxResults) {
		return DeviceTypeDriverDao.queryDeviceTypeDriver(getDao(), queryCount,
				loginUserId, filter, filterContent, firstResult, maxResults);
	}
}
