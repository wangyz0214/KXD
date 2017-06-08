package kxd.scs.dao.term;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.naming.NamingException;

import kxd.engine.cache.CachedHashMap;
import kxd.engine.cache.beans.IntegerData;
import kxd.engine.cache.beans.sts.CachedTermType;
import kxd.engine.dao.Dao;
import kxd.engine.helper.AdminHelper;
import kxd.engine.helper.CacheHelper;
import kxd.remote.scs.beans.BaseDevice;
import kxd.remote.scs.beans.BaseTermType;
import kxd.remote.scs.beans.BaseTermTypeDevice;
import kxd.remote.scs.beans.device.EditedDevice;
import kxd.remote.scs.beans.device.EditedTermType;
import kxd.remote.scs.beans.device.EditedTermTypeDevice;
import kxd.remote.scs.beans.right.LoginUser;
import kxd.remote.scs.util.AppException;
import kxd.remote.scs.util.QueryResult;
import kxd.scs.dao.BaseDao;
import kxd.scs.dao.cache.converters.CachedTermTypeConverter;
import kxd.scs.dao.device.DeviceDao;
import kxd.scs.dao.right.UserDao;
import kxd.scs.dao.term.converters.BaseTermTypeConverter;
import kxd.scs.dao.term.converters.BaseTermTypeDeviceConverter;
import kxd.scs.dao.term.converters.EditedTermTypeConverter;
import kxd.scs.dao.term.converters.EditedTermTypeDeviceConverter;
import kxd.util.KeyValue;

public class TermTypeDao extends BaseDao {
	final public static String SQL_QUERY_CACHED_TERMTYPE_BASIC = "select a.typeid,a.typecode,"
			+ "a.typedesp,a.fixtype,a.cashflag,a.appid,a.manufid,b.deviceid,"
			+ "b.port,b.extconfig from termtype a,termtypedevice b where a.typeid=b.typeid(+)";
	final public static String SQL_QUERY_ALL_CACHED_TERMTYPE = SQL_QUERY_CACHED_TERMTYPE_BASIC
			+ " order by a.typeid";
	final public static String SQL_QUERY_ALL_TERMTYPE_ID = "select a.typeid from"
			+ " termtype a order by a.typeid";
	final public static String SQL_QUERY_CACHED_TERMTYPE_BYID = SQL_QUERY_CACHED_TERMTYPE_BASIC
			+ " and a.typeid=?1";
	final public static CachedTermTypeConverter cachedTermTypeConverter = new CachedTermTypeConverter(
			CacheHelper.termTypeMap);

	/**
	 * 从数据库获取全部的应用数据，并存储至缓存服务器
	 * 
	 * @param dao
	 * @return 缓存应用的ID列表
	 */
	static public KeyValue<byte[], List<?>> getCachedTermTypeHashMapKeys(Dao dao) {
		final CachedHashMap<Integer, CachedTermType> map = CacheHelper.termTypeMap;
		map.setKeysLoading(true);
		try {
			List<CachedTermType> ls = dao.queryAndSetCache(
					CacheHelper.termTypeMap, cachedTermTypeConverter,
					SQL_QUERY_ALL_CACHED_TERMTYPE);
			List<IntegerData> idList = new ArrayList<IntegerData>();
			for (Object o : ls) {
				CachedTermType r = (CachedTermType) o;
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
	static public List<?> getCachedTermTypesList(Dao dao, List<?> keys) {
		StringBuffer sql = new StringBuffer(SQL_QUERY_CACHED_TERMTYPE_BASIC);
		if (keys.size() > 0) {
			sql.append(" and (");
			for (int i = 0; i < keys.size(); i++) {
				if (i > 0)
					sql.append(" or ");
				sql.append(" a.typeid=?" + (i + 1));
			}
			sql.append(")");
		}
		sql.append(" order by a.typeid");
		return dao.queryAndSetCache(CacheHelper.termTypeMap,
				cachedTermTypeConverter, sql.toString(), keys);
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
	static public CachedTermType getCachedTermType(Dao dao, int id) {
		List<CachedTermType> ls = dao.queryAndSetCache(CacheHelper.termTypeMap,
				cachedTermTypeConverter, SQL_QUERY_CACHED_TERMTYPE_BYID, id);
		if (ls.size() > 0) {
			return ls.get(0);
		} else
			return null;
	}

	final public static BaseTermTypeConverter baseConverter = new BaseTermTypeConverter();
	final public static EditedTermTypeConverter converter = new EditedTermTypeConverter();
	final private static String fields = "a.typeid,a.typedesp,a.manufid,manufname,a.typecode,a.fixtype,a.cashflag,a.appid,appdesp";

	final private static String tableName = "termtype";

	static private String jionWhereSql(boolean isDetail, LoginUser user,
			String keyword, String desp, Integer appId, Integer manufId,
			Integer cashFlag, Integer fixType) {
		String qlString = " from " + tableName + " a,manuf b ";
		String whereString = "a.manufid=b.manufid";
		if (isDetail) {
			qlString += ",app c ";
			whereString += " and a.appid=c.appid";
		}
		if (!user.getUserGroup().isSystemManager()) {
			if (user.getManuf().getId() == null)
				throw new AppException("对不起，您没有管理型号的权限");
			whereString += " and a.manufid=" + user.getManuf().getId();
		}
		if (keyword != null) {
			keyword = keyword.trim();
			if (keyword.length() > 0) {
				if (whereString.length() > 0)
					whereString += " and ";
				whereString += "a.typecode like '" + keyword + "%'";
			}
		}

		if (desp != null && !desp.trim().equals("")) {
			if (whereString.length() > 0)
				whereString += " and ";
			whereString = " a.typedesp like '" + desp + "%'";
		}

		if (appId != null) {
			if (whereString.length() > 0)
				whereString += " and ";
			whereString = " a.appid=" + appId;
		}

		if (manufId != null) {
			if (whereString.length() > 0)
				whereString += " and ";
			whereString = " a.manufid=" + manufId;
		}

		if (cashFlag != null) {
			if (whereString.length() > 0)
				whereString += " and ";
			whereString = " a.cashflag=" + cashFlag;
		}

		if (fixType != null) {
			if (whereString.length() > 0)
				whereString += " and ";
			whereString = " a.fixtype=" + fixType;
		}

		if (whereString.length() > 0)
			whereString = " where " + whereString;
		return qlString + whereString + " order by a.typeid";
	}

	static public List<BaseTermType> getTermTypeList(Dao dao, long loginUserId,
			String keyword) {
		return dao.query(
				baseConverter,
				"select a.typeid,a.typedesp,a.manufid,manufname"
						+ jionWhereSql(false,
								UserDao.getLoginUser(dao, loginUserId),
								keyword, "", null, null, null, null));
	}

	static public boolean termTypeCodeExists(Dao dao, String code) {
		return !dao.query("select * from termtype a where typecode=?1", code)
				.isEmpty();
	}

	static public boolean hasTerm(Dao dao, int id) {
		return !dao.queryPage("select * from term a where typeid=?1", 0, 1, id)
				.isEmpty();
	}

	static public EditedTermType find(Dao dao, int id) {
		String sql = "select "
				+ fields
				+ " from termtype a,manuf b,app c "
				+ "where a.typeid=?1 and a.manufid=b.manufid and a.appid=c.appid";
		Iterator<EditedTermType> it = dao.query(converter, sql, id).iterator();
		if (it.hasNext())
			return it.next();
		else
			return null;
	}

	static public int add(Dao dao, long loginUserId, EditedTermType o) {
		if (termTypeCodeExists(dao, o.getTypeCode()))
			throw new AppException("终端型号编码[" + o.getTypeCode() + "]已被占用.");
		dao.insert(o, converter);
		// 确保从数据库装入最新的数据
		CacheHelper.termTypeMap.deleteCheckSum();
		addUserLog(dao, loginUserId, "添加终端型号[" + o.getTypeDesp() + "]");
		return o.getId();
	}

	static public void delete(Dao dao, long loginUserId, Integer[] termTypeId) {
		for (int i = 0; i < termTypeId.length; i++) {
			EditedTermType u = find(dao, termTypeId[i]);
			if (u == null)
				throw new AppException("要删除的终端型号[" + termTypeId[i] + "]不存在.");
			if (hasTerm(dao, u.getId()))
				throw new AppException("有终端使用了终端型号[" + u.getTypeDesp()
						+ "]，不能删除.");
			dao.delete(u, converter);
			addUserLog(dao, loginUserId, "删除终端型号[" + u.getId() + "]");
		}
		CacheHelper.termTypeMap.removeAll(termTypeId);
	}

	static public void edit(Dao dao, long loginUserId, EditedTermType o) {
		EditedTermType u = find(dao, o.getId());
		if (u == null)
			throw new AppException("要编辑的终端型号[" + o.getId() + "]不存在.");
		if (!o.getTypeCode().equals(u.getTypeCode())
				&& termTypeCodeExists(dao, o.getTypeCode()))
			throw new AppException("终端型号编码[" + o.getTypeCode() + "]已被占用.");
		o.setManuf(u.getManuf());
		dao.update(o, converter);
		// 确保从数据库装入最新的数据
		CacheHelper.termTypeMap.remove(o.getId());
		addUserLog(dao, loginUserId, "编辑终端型号[" + o.getId() + "]");
	}

	static public QueryResult<EditedTermType> queryTermType(Dao dao,
			boolean queryCount, long loginUserId, Integer filter,
			String filterContent, int firstResult, int maxResults) {
		return query(
				dao,
				converter,
				queryCount,
				fields,
				jionWhereSql(true, UserDao.getLoginUser(dao, loginUserId),
						filterContent, "", null, null, null, null),
				firstResult, maxResults);
	}

	static public QueryResult<EditedTermType> queryTermType(Dao dao,
			boolean queryCount, long loginUserId, Integer filter,
			String filterContent, String desp, Integer appId, Integer manufId,
			Integer cashFlag, Integer fixType, int firstResult, int maxResults) {
		return query(
				dao,
				converter,
				queryCount,
				fields,
				jionWhereSql(true, UserDao.getLoginUser(dao, loginUserId),
						filterContent, desp, appId, manufId, cashFlag, fixType),
				firstResult, maxResults);
	}

	final public static BaseTermTypeDeviceConverter baseDeviceConverter = new BaseTermTypeDeviceConverter();
	final public static EditedTermTypeDeviceConverter deviceConverter = new EditedTermTypeDeviceConverter();
	final private static String deviceFieldDefs = "a.typeid,typedesp,a.deviceid,devicename,a.port,a.extconfig";

	static public EditedTermTypeDevice findDevice(Dao dao, int typeId,
			int deviceId) {
		String sql = "select " + deviceFieldDefs
				+ " from termtypedevice a,termtype b"
				+ ",device c where a.typeid=?1 and a.deviceid=?2"
				+ " and a.typeid=b.typeid and a.deviceid=c.deviceid";
		Iterator<EditedTermTypeDevice> it = dao.query(deviceConverter, sql,
				typeId, deviceId).iterator();
		if (it.hasNext())
			return it.next();
		else
			return null;
	}

	static public String findDeivceByDeviceTypeInTermType(Dao dao,
			int deviceType, int termTypeId) {
		Iterator<?> it = dao.query(
				"select b.devicename from termtypedevice a,device b,devicetype c "
						+ "where a.typeid=?1 and c.devicetype=?2 "
						+ "and a.deviceid=b.deviceid"
						+ " and b.devicetype=c.devicetype", termTypeId,
				deviceType).iterator();
		if (it.hasNext())
			return (String) it.next();
		else
			return null;
	}

	static public void addTermTypeDevice(Dao dao, long loginUserId,
			EditedTermTypeDevice device) {
		EditedTermTypeDevice d = findDevice(dao, device.getTermType().getId(),
				device.getDevice().getId());
		if (d != null)
			throw new AppException("型号[" + d.getTermType().getTypeDesp()
					+ "]中已经包含硬件设备[" + d.getDevice().getDeviceName() + "]，不能再添加");
		String sql = "select * from termtypedevice where typeid="
				+ device.getTermType().getIdString() + " and port="
				+ device.getPort();
		if (!dao.query(sql).isEmpty())
			throw new AppException("端口已经被占用，请选用其他端口");
		EditedDevice dd = DeviceDao.find(dao, device.getDevice().getId());
		if (dd == null)
			throw new AppException("要增加的硬件设备[" + device.getDevice().getId()
					+ "]不存在.");
		String deviceName = findDeivceByDeviceTypeInTermType(dao, dd
				.getDeviceType().getId(), device.getTermType().getId());
		if (deviceName != null) {
			throw new AppException("您要添加的硬件设备[" + dd.getDeviceName() + "]属于["
					+ dd.getDeviceType().getDeviceTypeDesp()
					+ "]模块,与该型号中已有硬件设备[" + deviceName + "]冲突.");
		}
		dao.insert(device, deviceConverter);
		dao.execute("insert into termdevicestatus(termid,deviceid,"
				+ "status,statusdesp) select termid,?1,0,'尚未上传状态'"
				+ " from term where typeid=?2", device.getDevice().getId(),
				device.getTermType().getId());
		try {
			AdminHelper.termTypeDeviceAdded(device.getTermType().getId(),
					device.getDevice().getId());
		} catch (NamingException e) {
		}
		CacheHelper.termTypeMap.remove(device.getTermType().getId());
		addUserLog(dao, loginUserId, "为终端型号添加硬件设备["
				+ device.getTermType().getTermType() + ","
				+ device.getDevice().getDeviceId() + "]");
	}

	static public void deleteTermTypeDevice(Dao dao, long loginUserId,
			int typeId, int[] deviceId) {
		for (int i = 0; i < deviceId.length; i++) {
			EditedTermTypeDevice d = new EditedTermTypeDevice();
			d.setDevice(new BaseDevice(deviceId[i]));
			d.setTermType(new BaseTermType(typeId));
			dao.delete(d, deviceConverter);
			dao.execute("delete from termdevicestatus a where a.termid"
					+ " in (select b.termid from term b where b.typeid=?1)"
					+ " and deviceid=?2", typeId, d.getDevice().getId());
			try {
				AdminHelper.termTypeDeviceRemoved(typeId, deviceId[i]);
			} catch (NamingException e) {
			}
			addUserLog(dao, loginUserId, "删除终端型号的硬件设备["
					+ d.getTermType().getId() + ","
					+ d.getDevice().getDeviceId() + "]");
		}
		CacheHelper.termTypeMap.remove(typeId);
	}

	static public void editTermTypeDevice(Dao dao, long loginUserId,
			EditedTermTypeDevice device) {
		EditedTermTypeDevice d = findDevice(dao, device.getTermType().getId(),
				device.getDevice().getId());
		if (d == null)
			throw new AppException("要编辑的硬件设备已经不存在或已经被删除");
		if (d.getPort() != device.getPort())
			if (!dao.query(
					"select * from termtypedevice where typeid="
							+ device.getTermType().getIdString() + " and port="
							+ device.getPort()).isEmpty())
				throw new AppException("端口已经被占用，请选用其他端口");
		dao.update(device, deviceConverter);
		CacheHelper.termTypeMap.remove(device.getTermType().getId());
		addUserLog(dao, loginUserId, "修改终端型号的硬件设备[" + d.getTermType().getId()
				+ "," + d.getDevice().getDeviceId() + "]");
	}

	static public List<BaseTermTypeDevice> getTermTypeDeviceList(Dao dao,
			long loginUserId, int typeId) {
		return dao.query(baseDeviceConverter, "select a.typeid,a.deviceid,"
				+ "devicetypedesp,devicename,a.port from "
				+ "termtypedevice a,device b,devicetype c where"
				+ " a.typeid=?1 and a.deviceid=b.deviceid "
				+ "and b.devicetype=c.devicetype", typeId);
	}
}
