package kxd.scs.dao.device;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import kxd.engine.cache.CachedHashMap;
import kxd.engine.cache.beans.IntegerData;
import kxd.engine.cache.beans.sts.CachedDevice;
import kxd.engine.dao.Dao;
import kxd.engine.helper.CacheHelper;
import kxd.remote.scs.beans.BaseDevice;
import kxd.remote.scs.beans.device.EditedDevice;
import kxd.remote.scs.util.AppException;
import kxd.remote.scs.util.QueryResult;
import kxd.scs.dao.BaseDao;
import kxd.scs.dao.cache.converters.CachedDeviceConverter;
import kxd.scs.dao.device.converters.BaseDeviceConverter;
import kxd.scs.dao.device.converters.EditedDeviceConverter;
import kxd.util.KeyValue;

public class DeviceDao extends BaseDao {
	final public static String SQL_QUERY_CACHED_DEVICE_BASIC = "select a.deviceid,a.devicedriverid,"
			+ "a.devicename,a.devicetype,a.addconfig,a.manufdesp from device a";
	final public static String SQL_QUERY_ALL_CACHED_DEVICE = SQL_QUERY_CACHED_DEVICE_BASIC
			+ " order by a.deviceid";
	final public static String SQL_QUERY_ALL_DEVICE_ID = "select a.deviceid from"
			+ " device a order by a.deviceid";
	final public static String SQL_QUERY_CACHED_DEVICE_BYID = SQL_QUERY_CACHED_DEVICE_BASIC
			+ " where a.deviceid=?1";
	final public static CachedDeviceConverter cachedDeviceConverter = new CachedDeviceConverter(
			CacheHelper.deviceMap);

	/**
	 * 从数据库获取全部的设备数据，并存储至缓存服务器
	 * 
	 * @param dao
	 * @return 缓存设备的ID列表
	 */
	static public KeyValue<byte[], List<?>> getCachedDeviceHashMapKeys(Dao dao) {
		final CachedHashMap<Integer, CachedDevice> map = CacheHelper.deviceMap;
		map.setKeysLoading(true);
		try {
			List<CachedDevice> ls = dao.queryAndSetCache(CacheHelper.deviceMap,
					cachedDeviceConverter, SQL_QUERY_ALL_CACHED_DEVICE);
			List<IntegerData> idList = new ArrayList<IntegerData>();
			for (Object o : ls) {
				CachedDevice r = (CachedDevice) o;
				idList.add(new IntegerData(r.getId()));
			}
			return new KeyValue<byte[], List<?>>(map.setKeys(idList), idList);
		} finally {
			map.setKeysLoading(false);
		}
	}

	/**
	 * 从数据库获取指定的设备数据，并存储至缓存服务器
	 * 
	 * @param dao
	 *            数据访问对象
	 * @param keys
	 *            要获取的设备ID列表
	 * @return 缓存设备列表
	 */
	static public List<?> getCachedDevicesList(Dao dao, List<?> keys) {
		StringBuffer sql = new StringBuffer(SQL_QUERY_CACHED_DEVICE_BASIC);
		if (keys.size() > 0) {
			sql.append(" where (");
			for (int i = 0; i < keys.size(); i++) {
				if (i > 0)
					sql.append(" or ");
				sql.append(" a.deviceid=?" + (i + 1));
			}
			sql.append(")");
		}
		sql.append(" order by a.deviceid");
		return dao.queryAndSetCache(CacheHelper.deviceMap,
				cachedDeviceConverter, sql.toString(), keys);
	}

	/**
	 * 通过设备ID，获取设备数据，并缓存至服务器
	 * 
	 * @param dao
	 *            数据访问对象
	 * @param id
	 *            设备ID
	 * @return 缓存设备对象
	 */
	static public CachedDevice getCachedDevice(Dao dao, int id) {
		List<CachedDevice> ls = dao.queryAndSetCache(CacheHelper.deviceMap,
				cachedDeviceConverter, SQL_QUERY_CACHED_DEVICE_BYID, id);
		if (ls.size() > 0) {
			return ls.get(0);
		} else
			return null;
	}

	final public static BaseDeviceConverter baseConverter = new BaseDeviceConverter();
	final public static EditedDeviceConverter converter = new EditedDeviceConverter();
	final private static String fieldDefs = "a.deviceid,"
			+ "a.devicename,manufdesp,a.devicedriverid,devicedriverdesp,a.devicetype,"
			+ "devicetypedesp,a.addconfig";
	final private static String tableName = "device";

	static private String jionWhereSql(boolean isDetail, String keyword,
			Integer typeId) {
		String qlString = " from " + tableName + " a,devicedriver b ";
		String whereString = "a.devicedriverid=b.devicedriverid";
		if (isDetail) {
			qlString += ",devicetype c ";
			whereString += " and a.devicetype=c.devicetype";
		}
		if (keyword != null) {
			keyword = keyword.trim();
			if (keyword.length() > 0) {
				whereString += " and a.devicename like '" + keyword + "%'";
			}
		}

		if (typeId != null) {
			if (whereString.length() > 0)
				whereString += " and ";
			whereString += " a.devicetype=" + typeId;

		}
		if (whereString.length() > 0)
			whereString = " where " + whereString;
		return qlString + whereString + " order by a.deviceid";
	}

	static public List<BaseDevice> getDeviceList(Dao dao, long loginUserId,
			String keyword) {
		return dao.query(baseConverter, "select a.deviceid,a.devicename"
				+ jionWhereSql(false, keyword, null));
	}

	static public boolean deviceNameExists(Dao dao, String o) {
		return !dao.query("select * from device a " + "where devicename=?1", o)
				.isEmpty();
	}

	static public EditedDevice find(Dao dao, int id) {
		String sql = "select " + fieldDefs
				+ " from device a,devicedriver b,devicetype c "
				+ "where deviceid=?1 and a.devicedriverid=b.devicedriverid"
				+ " and a.devicetype=c.devicetype";
		Iterator<EditedDevice> it = dao.query(converter, sql, id).iterator();
		if (it.hasNext())
			return it.next();
		else
			return null;
	}

	static public int add(Dao dao, long loginUserId, EditedDevice o) {
		if (deviceNameExists(dao, o.getDeviceName()))
			throw new AppException("设备编码[" + o.getDeviceName() + "]已被占用.");
		dao.insert(o, converter);
		// 确保从数据库装入最新的数据
		CacheHelper.deviceMap.deleteCheckSum();
		addUserLog(dao, loginUserId, "添加设备[" + o.getDeviceName() + "]");
		return o.getId();
	}

	static public boolean hasTermType(Dao dao, int id) {
		return !dao.queryPage("select * from termtypedevice where deviceid=?1",
				0, 1, id).isEmpty();
	}

	static public void delete(Dao dao, long loginUserId, Integer[] id) {
		for (int i = 0; i < id.length; i++) {
			EditedDevice u = find(dao, id[i]);
			if (u == null)
				throw new AppException("要删除的设备[" + id[i] + "]不存在.");
			if (hasTermType(dao, id[i]))
				throw new AppException("还有终端型号包含了该设备[" + u.getDeviceName()
						+ "]，不能删除.");
			dao.delete(u, converter);
			addUserLog(dao, loginUserId, "删除设备[" + u.getDeviceId() + "]");
		}
		CacheHelper.deviceMap.removeAll(id);
	}

	static public void edit(Dao dao, long loginUserId, EditedDevice o) {
		EditedDevice u = find(dao, o.getDeviceId());
		if (u == null)
			throw new AppException("要编辑的设备[" + o.getDeviceId() + "]不存在.");
		if (!o.getDeviceName().equals(u.getDeviceName())
				&& deviceNameExists(dao, o.getDeviceName()))
			throw new AppException("设备编码[" + o.getDeviceName() + "]已被占用.");
		dao.update(o, converter);
		CacheHelper.deviceMap.remove(o.getId());
		addUserLog(dao, loginUserId, "编辑设备[" + o.getDeviceId() + "]");
	}

	static public QueryResult<EditedDevice> queryDevice(Dao dao,
			boolean queryCount, long loginUserId, Integer filter,
			String filterContent, int firstResult, int maxResults) {
		return query(dao, converter, queryCount, fieldDefs,
				jionWhereSql(true, filterContent, null), firstResult,
				maxResults);
	}

	static public QueryResult<EditedDevice> queryDevice(Dao dao,
			boolean queryCount, long loginUserId, Integer filter,
			String filterContent, Integer typeId, int firstResult,
			int maxResults) {
		return query(dao, converter, queryCount, fieldDefs,
				jionWhereSql(true, filterContent, typeId), firstResult,
				maxResults);
	}

}
