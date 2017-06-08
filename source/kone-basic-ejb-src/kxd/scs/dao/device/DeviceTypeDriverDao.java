package kxd.scs.dao.device;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import kxd.engine.dao.Dao;
import kxd.engine.helper.CacheHelper;
import kxd.engine.cache.CachedHashMap;
import kxd.engine.cache.beans.IntegerData;
import kxd.engine.cache.beans.sts.CachedDeviceTypeDriver;
import kxd.remote.scs.beans.BaseDeviceTypeDriver;
import kxd.remote.scs.util.AppException;
import kxd.remote.scs.util.QueryResult;
import kxd.scs.dao.BaseDao;
import kxd.scs.dao.cache.converters.CachedDeviceTypeDriverConverter;
import kxd.scs.dao.device.converters.BaseDeviceTypeDriverConverter;
import kxd.util.KeyValue;

public class DeviceTypeDriverDao extends BaseDao {
	final public static String SQL_QUERY_CACHED_DEVICETYPEDRIVER_BASIC = "select a.devicetypedriverid,a.devicetypedriverdesp,a.driverfile"
			+ " from devicetypedriver a";
	final public static String SQL_QUERY_ALL_CACHED_DEVICETYPEDRIVER = SQL_QUERY_CACHED_DEVICETYPEDRIVER_BASIC
			+ " order by a.devicetypedriverid";
	final public static String SQL_QUERY_ALL_DEVICETYPEDRIVER_ID = "select a.devicetypedriverid from"
			+ " devicetypedriver a order by a.devicetypedriverid";
	final public static String SQL_QUERY_CACHED_DEVICETYPEDRIVER_BYID = SQL_QUERY_CACHED_DEVICETYPEDRIVER_BASIC
			+ " where a.devicetypedriverid=?1";
	final public static CachedDeviceTypeDriverConverter cachedDeviceTypeDriverConverter = new CachedDeviceTypeDriverConverter(
			CacheHelper.deviceTypeDriverMap);

	/**
	 * 从数据库获取全部的应用数据，并存储至缓存服务器
	 * 
	 * @param dao
	 * @return 缓存应用的ID列表
	 */
	static public KeyValue<byte[], List<?>> getCachedDeviceTypeDriverHashMapKeys(
			Dao dao) {
		final CachedHashMap<Integer, CachedDeviceTypeDriver> map = CacheHelper.deviceTypeDriverMap;
		map.setKeysLoading(true);
		try {
			List<CachedDeviceTypeDriver> ls = dao.queryAndSetCache(
					CacheHelper.deviceTypeDriverMap,
					cachedDeviceTypeDriverConverter,
					SQL_QUERY_ALL_CACHED_DEVICETYPEDRIVER);
			List<IntegerData> idList = new ArrayList<IntegerData>();
			for (Object o : ls) {
				CachedDeviceTypeDriver r = (CachedDeviceTypeDriver) o;
				idList.add(new IntegerData(r.getId()));
			}
			return new KeyValue<byte[], List<?>>(map.setKeys(idList), idList);
		} finally {
			map.setKeysLoading(false);
		}
	}

	/**
	 * 从数据库获取指定的应用数据，并存储至缓存服务器
	 * 
	 * @param dao
	 *            数据访问对象
	 * @param keys
	 *            要获取的应用ID列表
	 * @return 缓存应用列表
	 */
	static public List<?> getCachedDeviceTypeDriversList(Dao dao, List<?> keys) {
		StringBuffer sql = new StringBuffer(
				SQL_QUERY_CACHED_DEVICETYPEDRIVER_BASIC);
		if (keys.size() > 0) {
			sql.append(" where (");
			for (int i = 0; i < keys.size(); i++) {
				if (i > 0)
					sql.append(" or ");
				sql.append(" a.devicetypedriverid=?" + (i + 1));
			}
			sql.append(")");
		}
		sql.append(" order by a.devicetypedriverid");
		return dao.queryAndSetCache(CacheHelper.deviceTypeDriverMap,
				cachedDeviceTypeDriverConverter, sql.toString(), keys);
	}

	/**
	 * 通过应用ID，获取应用数据，并缓存至服务器
	 * 
	 * @param dao
	 *            数据访问对象
	 * @param id
	 *            应用ID
	 * @return 缓存应用对象
	 */
	static public CachedDeviceTypeDriver getCachedDeviceTypeDriver(Dao dao,
			int id) {
		List<CachedDeviceTypeDriver> ls = dao.queryAndSetCache(
				CacheHelper.deviceTypeDriverMap,
				cachedDeviceTypeDriverConverter,
				SQL_QUERY_CACHED_DEVICETYPEDRIVER_BYID, id);
		if (ls.size() > 0) {
			return ls.get(0);
		} else
			return null;
	}

	final public static BaseDeviceTypeDriverConverter baseConverter = new BaseDeviceTypeDriverConverter();
	final public static BaseDeviceTypeDriverConverter converter = new BaseDeviceTypeDriverConverter();
	final private static String fieldDefs = "a.devicetypedriverid,a.devicetypedriverdesp,a.driverfile";
	final private static String tableName = "devicetypedriver";

	static private String jionWhereSql(String keyword) {
		String qlString = " from " + tableName + " a ";
		String whereString = "";
		if (keyword != null) {
			keyword = keyword.trim();
			if (keyword.length() > 0) {
				whereString = "a.devicetypedriverdesp like '" + keyword + "%'";
			}
		}
		if (whereString.length() > 0)
			whereString = " where " + whereString;
		return qlString + whereString + " order by a.devicetypedriverid";
	}

	static public List<BaseDeviceTypeDriver> getDeviceTypeDriverList(Dao dao,
			long loginUserId, String keyword) {
		return dao.query(baseConverter,
				"select devicetypedriverid,devicetypedriverdesp,driverfile"
						+ jionWhereSql(keyword));
	}

	static public boolean alarmCategoryDespExists(Dao dao, String o) {
		return !dao.query(
				"select * from devicetypedriver a "
						+ "where devicetypedriverdesp=?1", o).isEmpty();
	}

	static public boolean fileNameExists(Dao dao, String o) {
		return !dao.query(
				"select * from devicetypedriver a " + "where driverfile=?1", o)
				.isEmpty();
	}

	static public BaseDeviceTypeDriver find(Dao dao, int id) {
		String sql = "select " + fieldDefs + " from devicetypedriver a "
				+ "where devicetypedriverid=?1";
		Iterator<BaseDeviceTypeDriver> it = dao.query(converter, sql, id)
				.iterator();
		if (it.hasNext())
			return it.next();
		else
			return null;
	}

	static public KeyValue<Integer, String> add(Dao dao, long loginUserId,
			BaseDeviceTypeDriver o) {
		o.setDriverFile(o.getDriverFile().toLowerCase());
		if (alarmCategoryDespExists(dao, o.getDeviceTypeDriverDesp()))
			throw new AppException("模块驱动描述[" + o.getDeviceTypeDriverDesp()
					+ "]已被占用.");
		if (fileNameExists(dao, o.getDriverFile()))
			throw new AppException("驱动程序文件名[" + o.getDriverFile() + "]已被占用.");
		dao.insert(o, converter);
		// 确保从数据库装入最新的数据
		CacheHelper.deviceTypeDriverMap.deleteCheckSum();
		addUserLog(dao, loginUserId, "添加模块驱动[" + o.getDeviceTypeDriverDesp()
				+ "]");
		return new KeyValue<Integer, String>(o.getId(), o.getFileName());
	}

	static public boolean hasDeviceType(Dao dao, int id) {
		return !dao.queryPage(
				"select * from devicetype where devicetypedriverid=?1", 0, 1,
				id).isEmpty();
	}

	static public String[] delete(Dao dao, long loginUserId, Integer[] id) {
		ArrayList<String> ls = new ArrayList<String>();
		for (int i = 0; i < id.length; i++) {
			BaseDeviceTypeDriver u = find(dao, id[i]);
			if (u == null)
				throw new AppException("要删除的模块驱动[" + id[i] + "]不存在.");
			if (hasDeviceType(dao, id[i]))
				throw new AppException("有模块使用了模块驱动["
						+ u.getDeviceTypeDriverDesp() + "]，不能删除.");
			dao.delete(u, converter);
			addUserLog(dao, loginUserId, "删除模块驱动[" + u.getDeviceTypeDriverId()
					+ "]");
			ls.add(u.getDriverFile());
		}
		CacheHelper.deviceTypeDriverMap.removeAll(id);
		String[] r = new String[ls.size()];
		for (int i = 0; i < r.length; i++)
			r[i] = ls.get(i);
		return r;
	}

	static public String edit(Dao dao, long loginUserId, BaseDeviceTypeDriver o) {
		BaseDeviceTypeDriver u = find(dao, o.getDeviceTypeDriverId());
		if (u == null)
			throw new AppException("要编辑的模块驱动[" + o.getDeviceTypeDriverId()
					+ "]不存在.");
		if (!o.getDeviceTypeDriverDesp().equals(u.getDeviceTypeDriverDesp())
				&& alarmCategoryDespExists(dao, o.getDeviceTypeDriverDesp()))
			throw new AppException("模块驱动描述[" + o.getDeviceTypeDriverDesp()
					+ "]已被占用.");
		if (o.getDriverFile() != null) {
			if (!o.getDriverFile().equals(u.getDriverFile())
					&& fileNameExists(dao, o.getDriverFile()))
				throw new AppException("驱动程序文件名[" + o.getDriverFile()
						+ "]已被占用.");
		} else
			o.setDriverFile(u.getDriverFile());
		dao.update(o, converter);
		CacheHelper.deviceTypeDriverMap.remove(o.getId());
		addUserLog(dao, loginUserId, "编辑模块驱动[" + o.getDeviceTypeDriverId()
				+ "]");
		return u.getDriverFile();
	}

	static public QueryResult<BaseDeviceTypeDriver> queryDeviceTypeDriver(
			Dao dao, boolean queryCount, long loginUserId, Integer filter,
			String filterContent, int firstResult, int maxResults) {
		return query(dao, converter, queryCount, fieldDefs,
				jionWhereSql(filterContent), firstResult, maxResults);
	}

}
