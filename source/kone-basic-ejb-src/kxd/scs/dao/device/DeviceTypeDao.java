package kxd.scs.dao.device;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import kxd.engine.cache.CachedHashMap;
import kxd.engine.cache.beans.IntegerData;
import kxd.engine.cache.beans.sts.CachedDeviceType;
import kxd.engine.dao.Dao;
import kxd.engine.helper.CacheHelper;
import kxd.remote.scs.beans.BaseAlarmCode;
import kxd.remote.scs.beans.BaseDeviceType;
import kxd.remote.scs.beans.device.EditedDeviceType;
import kxd.remote.scs.util.AppException;
import kxd.remote.scs.util.QueryResult;
import kxd.scs.dao.BaseDao;
import kxd.scs.dao.cache.converters.CachedDeviceTypeConverter;
import kxd.scs.dao.device.converters.BaseAlarmCodeConverter;
import kxd.scs.dao.device.converters.BaseDeviceTypeConverter;
import kxd.scs.dao.device.converters.EditedDeviceTypeConverter;
import kxd.util.KeyValue;

public class DeviceTypeDao extends BaseDao {
	final public static String SQL_QUERY_CACHED_DEVICETYPE_BASIC = "select a.devicetype,a.alarmnotifyoption,a.alarmsendform,a.devicetypecode,"
			+ "a.devicetypedesp,a.devicetypedriverid,a.faultnotifyoption,a.faultpromptoption,"
			+ "a.falutsendform,b.alarmcode,b.alarmclassid,b.alarmcodedesp "
			+ "from devicetype a,alarmcode b where a.devicetype=b.devicetype(+)";
	final public static String SQL_QUERY_ALL_CACHED_DEVICETYPE = SQL_QUERY_CACHED_DEVICETYPE_BASIC
			+ " order by a.devicetype";
	final public static String SQL_QUERY_ALL_DEVICETYPE_ID = "select a.devicetype from"
			+ " devicetype a order by a.devicetype";
	final public static String SQL_QUERY_CACHED_DEVICETYPE_BYID = SQL_QUERY_CACHED_DEVICETYPE_BASIC
			+ " and a.devicetype=?1";
	final public static CachedDeviceTypeConverter cachedDeviceTypeConverter = new CachedDeviceTypeConverter(
			CacheHelper.deviceTypeMap);

	/**
	 * 从数据库获取全部的应用数据，并存储至缓存服务器
	 * 
	 * @param dao
	 * @return 缓存应用的ID列表
	 */
	static public KeyValue<byte[], List<?>> getCachedDeviceTypeHashMapKeys(
			Dao dao) {
		final CachedHashMap<Integer, CachedDeviceType> map = CacheHelper.deviceTypeMap;
		map.setKeysLoading(true);
		try {
			List<CachedDeviceType> ls = dao.queryAndSetCache(
					CacheHelper.deviceTypeMap, cachedDeviceTypeConverter,
					SQL_QUERY_ALL_CACHED_DEVICETYPE);
			List<IntegerData> idList = new ArrayList<IntegerData>();
			for (Object o : ls) {
				CachedDeviceType r = (CachedDeviceType) o;
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
	static public List<?> getCachedDeviceTypesList(Dao dao, List<?> keys) {
		StringBuffer sql = new StringBuffer(SQL_QUERY_CACHED_DEVICETYPE_BASIC);
		if (keys.size() > 0) {
			sql.append(" and (");
			for (int i = 0; i < keys.size(); i++) {
				if (i > 0)
					sql.append(" or ");
				sql.append(" a.devicetype=?" + (i + 1));
			}
			sql.append(")");
		}
		sql.append(" order by a.devicetype");
		return dao.queryAndSetCache(CacheHelper.deviceTypeMap,
				cachedDeviceTypeConverter, sql.toString(), keys);
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
	static public CachedDeviceType getCachedDeviceType(Dao dao, int id) {
		List<CachedDeviceType> ls = dao.queryAndSetCache(
				CacheHelper.deviceTypeMap, cachedDeviceTypeConverter,
				SQL_QUERY_CACHED_DEVICETYPE_BYID, id);
		if (ls.size() > 0) {
			return ls.get(0);
		} else
			return null;
	}

	final public static BaseDeviceTypeConverter baseConverter = new BaseDeviceTypeConverter();
	final public static EditedDeviceTypeConverter converter = new EditedDeviceTypeConverter();
	final private static String fieldDefs = "a.devicetype,"
			+ "a.devicetypecode,a.devicetypedesp,a.devicetypedriverid,devicetypedriverdesp,"
			+ "a.faultpromptoption,a.alarmnotifyoption,a.faultnotifyoption,"
			+ "a.alarmsendform,a.falutsendform";
	final private static String tableName = "devicetype";

	static private String jionWhereSql(String keyword,String code,Integer deviceTypeDriverId) {
		String qlString = " from " + tableName + " a,devicetypedriver b ";
		String whereString = "a.devicetypedriverid=b.devicetypedriverid";
		if (keyword != null) {
			keyword = keyword.trim();
			if (keyword.length() > 0) {
				whereString += " and a.devicetypedesp like '" + keyword + "%'";
			}
		}
		
		if(code!=null&&!code.trim().equals("")){
			if (whereString.length() > 0)
				whereString += " and ";
			whereString += " a.devicetypecode like '" + code + "%'";
		}
		
		if(deviceTypeDriverId!=null){
			if (whereString.length() > 0)
				whereString += " and ";
			whereString += " a.devicetypedriverid ="+deviceTypeDriverId;
		}		
		
		if (whereString.length() > 0)
			whereString = " where " + whereString;
		return qlString + whereString + " order by a.devicetype";
	}

	static public List<BaseDeviceType> getDeviceTypeList(Dao dao,
			long loginUserId, String keyword) {
		return dao.query(baseConverter,
				"select a.devicetype,a.devicetypecode,a.devicetypedesp,"
						+ "a.devicetypedriverid,devicetypedriverdesp"
						+ jionWhereSql(keyword,null,null));
	}

	static public boolean codeDespExists(Dao dao, String o) {
		return !dao.query(
				"select * from devicetype a " + "where devicetypedesp=?1", o)
				.isEmpty();
	}

	static public EditedDeviceType find(Dao dao, int id) {
		String sql = "select "
				+ fieldDefs
				+ " from devicetype a,devicetypedriver b "
				+ "where devicetype=?1 and a.devicetypedriverid=b.devicetypedriverid";
		Iterator<EditedDeviceType> it = dao.query(converter, sql, id)
				.iterator();
		if (it.hasNext())
			return it.next();
		else
			return null;
	}

	static public int add(Dao dao, long loginUserId, EditedDeviceType o) {
		if (codeDespExists(dao, o.getDeviceTypeCode()))
			throw new AppException("模块编码[" + o.getDeviceTypeDesp() + "]已被占用.");
		dao.insert(o, converter);
		// 确保从数据库装入最新的数据
		CacheHelper.deviceTypeMap.deleteCheckSum();
		addUserLog(dao, loginUserId, "添加模块[" + o.getDeviceTypeDesp() + "]");
		return o.getId();
	}

	static public boolean hasDevice(Dao dao, int id) {
		return !dao.queryPage("select * from device where devicetype=?1", 0, 1,
				id).isEmpty();
	}

	static public boolean hasAlarmCode(Dao dao, int id) {
		return !dao.queryPage("select * from alarmcode where devicetype=?1", 0,
				1, id).isEmpty();
	}

	static public void delete(Dao dao, long loginUserId, Integer[] id) {
		for (int i = 0; i < id.length; i++) {
			EditedDeviceType u = find(dao, id[i]);
			if (u == null)
				throw new AppException("要删除的模块[" + id[i] + "]不存在.");
			if (hasDevice(dao, id[i]))
				throw new AppException("该模块[" + u.getDeviceTypeDesp()
						+ "]下配置了硬件设备，不能删除.");
			if (hasAlarmCode(dao, id[i]))
				throw new AppException("该模块[" + u.getDeviceTypeDesp()
						+ "]下定义了告警代码，不能删除.");
			dao.delete(u, converter);
			addUserLog(dao, loginUserId, "删除模块[" + u.getDeviceTypeId() + "]");
		}
		CacheHelper.deviceTypeMap.removeAll(id);
	}

	static public void edit(Dao dao, long loginUserId, EditedDeviceType o) {
		EditedDeviceType u = find(dao, o.getDeviceTypeId());
		if (u == null)
			throw new AppException("要编辑的模块[" + o.getDeviceTypeId() + "]不存在.");
		if (!o.getDeviceTypeCode().equals(u.getDeviceTypeCode())
				&& codeDespExists(dao, o.getDeviceTypeCode()))
			throw new AppException("模块编码[" + o.getDeviceTypeCode() + "]已被占用.");
		dao.update(o, converter);
		CacheHelper.deviceTypeMap.remove(o.getId());
		addUserLog(dao, loginUserId, "编辑模块[" + o.getDeviceTypeId() + "]");
	}

	static public QueryResult<EditedDeviceType> queryDeviceType(Dao dao,
			boolean queryCount, long loginUserId, Integer filter,
			String filterContent, int firstResult, int maxResults) {
		return query(dao, converter, queryCount, fieldDefs,
				jionWhereSql(filterContent, null, null), firstResult,
				maxResults);
	}

	static public QueryResult<EditedDeviceType> queryDeviceType(Dao dao,
			boolean queryCount, long loginUserId, Integer filter,
			String filterContent, String code, Integer DeviceTypeDriverId,
			int firstResult, int maxResults) {
		return query(dao, converter, queryCount, fieldDefs,
				jionWhereSql(filterContent, code, DeviceTypeDriverId),
				firstResult, maxResults);
	}

	final public static BaseAlarmCodeConverter baseAlarmConverter = new BaseAlarmCodeConverter();
	final public static BaseAlarmCodeConverter alarmConverter = new BaseAlarmCodeConverter();
	final private static String alarmFieldDefs = "a.devicetype,a.alarmcode,a.alarmclassid,alarmclassdesp,alarmcodedesp";

	static public BaseAlarmCode findAlarmCode(Dao dao, int deviceType,
			int alarmCode) {
		Iterator<BaseAlarmCode> it = dao.query(
				baseAlarmConverter,
				"select " + alarmFieldDefs
						+ " from alarmcode a,alarmcategory b"
						+ " where a.alarmclassid=b.alarmclassid "
						+ "and a.devicetype=?1 and a.alarmcode=?2", deviceType,
				alarmCode).iterator();
		if (it.hasNext())
			return it.next();
		else
			return null;
	}

	static public void addAlarmCode(Dao dao, long loginUserId, BaseAlarmCode o) {
		if (findAlarmCode(dao, o.getDeviceType(), o.getAlarmCode()) != null)
			throw new AppException("该模块中已经包含告警代码[" + o.getAlarmCode() + "]");
		dao.insert(o, alarmConverter);
		CacheHelper.deviceTypeMap.remove(o.getDeviceType());
	}

	static public void deleteAlarmCode(Dao dao, long loginUserId,
			int deviceType, int[] id) {
		for (int i = 0; i < id.length; i++) {
			BaseAlarmCode u = findAlarmCode(dao, deviceType, id[i]);
			if (u != null) {
				dao.delete(u, alarmConverter);
			}
		}
		CacheHelper.deviceTypeMap.remove(deviceType);
	}

	static public void editAlarmCode(Dao dao, long loginUserId, BaseAlarmCode o) {
		dao.update(o, alarmConverter);
		CacheHelper.deviceTypeMap.remove(o.getDeviceType());
	}

	static public List<BaseAlarmCode> getAlarmCodeList(Dao dao,
			long loginUserId, int deviceType) {
		BaseDeviceType d = find(dao, deviceType);
		if (d == null)
			throw new AppException("找不到模块[id=" + deviceType + "]");
		return dao.query(baseAlarmConverter, "select " + alarmFieldDefs
				+ " from alarmcode a,alarmcategory b where devicetype=?1"
				+ " and a.alarmclassid=b.alarmclassid", deviceType);
	}

}
