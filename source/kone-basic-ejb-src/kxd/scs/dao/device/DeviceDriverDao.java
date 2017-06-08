package kxd.scs.dao.device;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import kxd.engine.cache.CachedHashMap;
import kxd.engine.cache.beans.IntegerData;
import kxd.engine.cache.beans.sts.CachedDeviceDriver;
import kxd.engine.dao.Dao;
import kxd.engine.helper.CacheHelper;
import kxd.remote.scs.beans.BaseDeviceDriver;
import kxd.remote.scs.beans.BaseDeviceDriverFile;
import kxd.remote.scs.util.AppException;
import kxd.remote.scs.util.QueryResult;
import kxd.scs.dao.BaseDao;
import kxd.scs.dao.cache.converters.CachedDeviceDriverConverter;
import kxd.scs.dao.device.converters.BaseDeviceDriverConverter;
import kxd.scs.dao.device.converters.BaseDeviceDriverFileConverter;
import kxd.util.KeyValue;

public class DeviceDriverDao extends BaseDao {
	final public static String SQL_QUERY_CACHED_DEVICEDRIVER_BASIC = "select a.devicedriverid,a.devicedriverdesp,"
			+ "a.driverfile,b.devicedriverfileid,b.filename from devicedriver a,devicedriverfiles b "
			+ "where a.devicedriverid=b.devicedriverid(+)";
	final public static String SQL_QUERY_ALL_CACHED_DEVICEDRIVER = SQL_QUERY_CACHED_DEVICEDRIVER_BASIC
			+ " order by a.devicedriverid";
	final public static String SQL_QUERY_ALL_DEVICEDRIVER_ID = "select a.devicedriverid from"
			+ " devicedriver a order by a.devicedriverid";
	final public static String SQL_QUERY_CACHED_DEVICEDRIVER_BYID = SQL_QUERY_CACHED_DEVICEDRIVER_BASIC
			+ " and a.devicedriverid=?1";
	final public static CachedDeviceDriverConverter cachedDeviceDriverConverter = new CachedDeviceDriverConverter(
			CacheHelper.deviceDriverMap);

	/**
	 * 从数据库获取全部的应用数据，并存储至缓存服务器
	 * 
	 * @param dao
	 * @return 缓存应用的ID列表
	 */
	static public KeyValue<byte[], List<?>> getCachedDeviceDriverHashMapKeys(
			Dao dao) {
		final CachedHashMap<Integer, CachedDeviceDriver> map = CacheHelper.deviceDriverMap;
		map.setKeysLoading(true);
		try {
			List<CachedDeviceDriver> ls = dao.queryAndSetCache(
					CacheHelper.deviceDriverMap, cachedDeviceDriverConverter,
					SQL_QUERY_ALL_CACHED_DEVICEDRIVER);
			List<IntegerData> idList = new ArrayList<IntegerData>();
			for (Object o : ls) {
				CachedDeviceDriver r = (CachedDeviceDriver) o;
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
	static public List<?> getCachedDeviceDriversList(Dao dao, List<?> keys) {
		StringBuffer sql = new StringBuffer(SQL_QUERY_CACHED_DEVICEDRIVER_BASIC);
		if (keys.size() > 0) {
			sql.append(" and (");
			for (int i = 0; i < keys.size(); i++) {
				if (i > 0)
					sql.append(" or ");
				sql.append(" a.devicedriverid=?" + (i + 1));
			}
			sql.append(")");
		}
		sql.append(" order by a.devicedriverid");
		return dao.queryAndSetCache(CacheHelper.deviceDriverMap,
				cachedDeviceDriverConverter, sql.toString(), keys);
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
	static public CachedDeviceDriver getCachedDeviceDriver(Dao dao, int id) {
		List<CachedDeviceDriver> ls = dao.queryAndSetCache(
				CacheHelper.deviceDriverMap, cachedDeviceDriverConverter,
				SQL_QUERY_CACHED_DEVICEDRIVER_BYID, id);
		if (ls.size() > 0) {
			return ls.get(0);
		} else
			return null;
	}

	final public static BaseDeviceDriverConverter baseConverter = new BaseDeviceDriverConverter();
	final public static BaseDeviceDriverConverter converter = new BaseDeviceDriverConverter();
	final private static String fieldDefs = "a.devicedriverid,a.devicedriverdesp,a.driverfile";
	final private static String tableName = "devicedriver";

	static private String jionWhereSql(String keyword) {
		String qlString = " from " + tableName + " a ";
		String whereString = "";
		if (keyword != null) {
			keyword = keyword.trim();
			if (keyword.length() > 0) {
				whereString = "a.devicedriverdesp like '" + keyword + "%'";
			}
		}
		if (whereString.length() > 0)
			whereString = " where " + whereString;
		return qlString + whereString + " order by a.devicedriverid";
	}

	static public List<BaseDeviceDriver> getDeviceDriverList(Dao dao,
			long loginUserId, String keyword) {
		return dao.query(baseConverter,
				"select devicedriverid,devicedriverdesp,driverfile"
						+ jionWhereSql(keyword));
	}

	static public boolean alarmCategoryDespExists(Dao dao, String o) {
		return !dao.query(
				"select * from devicedriver a " + "where devicedriverdesp=?1",
				o).isEmpty();
	}

	static public boolean fileNameExists(Dao dao, String o) {
		return !dao.query(
				"select * from devicedriver a " + "where driverfile=?1", o)
				.isEmpty();
	}

	static public BaseDeviceDriver find(Dao dao, int id) {
		String sql = "select " + fieldDefs + " from devicedriver a "
				+ "where devicedriverid=?1";
		Iterator<BaseDeviceDriver> it = dao.query(converter, sql, id)
				.iterator();
		if (it.hasNext())
			return it.next();
		else
			return null;
	}

	static public BaseDeviceDriver find(Dao dao, String file) {
		String sql = "select " + fieldDefs + " from devicedriver a "
				+ "where driverfile=?1";
		Iterator<BaseDeviceDriver> it = dao.query(converter, sql, file)
				.iterator();
		if (it.hasNext())
			return it.next();
		else
			return null;
	}

	static public KeyValue<Integer, String> add(Dao dao, long loginUserId,
			BaseDeviceDriver o) {
		o.setDriverFile(o.getDriverFile().toLowerCase());
		if (alarmCategoryDespExists(dao, o.getDeviceDriverDesp()))
			throw new AppException("设备驱动描述[" + o.getDeviceDriverDesp()
					+ "]已被占用.");
		if (fileNameExists(dao, o.getDriverFile()))
			throw new AppException("驱动程序文件名[" + o.getDriverFile() + "]已被占用.");
		dao.insert(o, converter);
		// 确保从数据库装入最新的数据
		CacheHelper.deviceDriverMap.deleteCheckSum();
		addUserLog(dao, loginUserId, "添加设备驱动[" + o.getDeviceDriverDesp() + "]");
		return new KeyValue<Integer, String>(o.getId(), o.getFileName());
	}

	static public boolean hasDevice(Dao dao, int id) {
		return !dao.queryPage("select * from device where devicedriverid=?1",
				0, 1, id).isEmpty();
	}

	static public boolean hasDriverFile(Dao dao, int id) {
		return !dao.queryPage(
				"select * from devicedriverfiles where devicedriverid=?1", 0,
				1, id).isEmpty();
	}

	static public String[] delete(Dao dao, long loginUserId, Integer[] id) {
		ArrayList<String> ls = new ArrayList<String>();
		for (int i = 0; i < id.length; i++) {
			BaseDeviceDriver u = find(dao, id[i]);
			if (u == null)
				throw new AppException("要删除的设备驱动[" + id[i] + "]不存在.");
			if (hasDevice(dao, id[i]))
				throw new AppException("有设备使用了设备驱动[" + u.getDeviceDriverDesp()
						+ "]，不能删除.");
			if (hasDriverFile(dao, id[i]))
				throw new AppException("驱动[" + u.getDeviceDriverDesp()
						+ "]下还配置有文件，不能删除.");
			dao.delete(u, converter);
			addUserLog(dao, loginUserId, "删除设备驱动[" + u.getDeviceDriverId()
					+ "]");
			ls.add(u.getDriverFile());
		}
		CacheHelper.deviceDriverMap.removeAll(id);
		String[] r = new String[ls.size()];
		for (int i = 0; i < r.length; i++)
			r[i] = ls.get(i);
		return r;
	}

	static public String edit(Dao dao, long loginUserId, BaseDeviceDriver o) {
		BaseDeviceDriver u = find(dao, o.getDeviceDriverId());
		if (u == null)
			throw new AppException("要编辑的设备驱动[" + o.getDeviceDriverId()
					+ "]不存在.");
		if (!o.getDeviceDriverDesp().equals(u.getDeviceDriverDesp())
				&& alarmCategoryDespExists(dao, o.getDeviceDriverDesp()))
			throw new AppException("设备驱动描述[" + o.getDeviceDriverDesp()
					+ "]已被占用.");
		if (o.getDriverFile() != null) {
			if (!o.getDriverFile().equals(u.getDriverFile())
					&& fileNameExists(dao, o.getDriverFile()))
				throw new AppException("驱动程序文件名[" + o.getDriverFile()
						+ "]已被占用.");
		} else
			o.setDriverFile(u.getDriverFile());
		dao.update(o, converter);
		CacheHelper.deviceDriverMap.remove(o.getId());
		addUserLog(dao, loginUserId, "编辑设备驱动[" + o.getDeviceDriverId() + "]");
		return u.getDriverFile();
	}

	static public QueryResult<BaseDeviceDriver> queryDeviceDriver(Dao dao,
			boolean queryCount, long loginUserId, Integer filter,
			String filterContent, int firstResult, int maxResults) {
		return query(dao, converter, queryCount, fieldDefs,
				jionWhereSql(filterContent), firstResult, maxResults);
	}

	final public static BaseDeviceDriverFileConverter baseFileConverter = new BaseDeviceDriverFileConverter();
	final public static BaseDeviceDriverFileConverter fileConverter = new BaseDeviceDriverFileConverter();
	final private static String fileFieldDefs = "devicedriverfileid,filename,devicedriverid";

	static public BaseDeviceDriverFile findDriverFile(Dao dao, String fileName) {
		String sql = "select " + fileFieldDefs + " from devicedriverfiles a "
				+ "where filename=?1";
		Iterator<BaseDeviceDriverFile> it = dao.query(fileConverter, sql,
				fileName).iterator();
		if (it.hasNext())
			return it.next();
		else
			return null;
	}

	static public BaseDeviceDriverFile findDriverFile(Dao dao, long fileId) {
		String sql = "select " + fileFieldDefs + " from devicedriverfiles a "
				+ "where devicedriverfileid=?1";
		Iterator<BaseDeviceDriverFile> it = dao.query(fileConverter, sql,
				fileId).iterator();
		if (it.hasNext())
			return it.next();
		else
			return null;
	}

	static public int addDeviceDriverFile(Dao dao, long loginUserId,
			BaseDeviceDriverFile file) {

		if (findDriverFile(dao, file.getFileName()) != null)
			throw new AppException("[" + file.getFileName() + "]已经存在");
		BaseDeviceDriver d = find(dao, file.getFileName());
		if (d != null)
			throw new AppException("[" + file.getFileName() + "]与驱动程序["
					+ d.getDeviceDriverDesp() + "]的文件名相同");
		dao.insert(file, baseFileConverter);
		CacheHelper.deviceDriverMap.remove(file.getDeviceDriverId());
		return file.getId();
	}

	static public String deleteDeviceDriverFile(Dao dao, long loginUserId,
			int fileId) {
		BaseDeviceDriverFile f = findDriverFile(dao, fileId);
		if (f == null)
			throw new AppException("文件[id" + fileId + "]不存在");
		dao.delete(f, fileConverter);
		CacheHelper.deviceDriverMap.remove(f.getId());
		return f.getFileName();
	}

	static public List<BaseDeviceDriverFile> getDeviceDriverFiles(Dao dao,
			int driverId) {
		BaseDeviceDriver d = find(dao, driverId);
		if (d == null)
			throw new AppException("找不到驱动程序[id" + driverId + "]");
		return dao.query(baseFileConverter, "select " + fileFieldDefs
				+ " from devicedriverfiles where devicedriverid=?1", driverId);
	}

}
