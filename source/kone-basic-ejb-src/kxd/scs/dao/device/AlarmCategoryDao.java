package kxd.scs.dao.device;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import kxd.engine.cache.CachedHashMap;
import kxd.engine.cache.beans.IntegerData;
import kxd.engine.cache.beans.sts.CachedAlarmCategory;
import kxd.engine.dao.Dao;
import kxd.engine.helper.CacheHelper;
import kxd.remote.scs.beans.BaseAlarmCategory;
import kxd.remote.scs.util.AppException;
import kxd.remote.scs.util.QueryResult;
import kxd.scs.dao.BaseDao;
import kxd.scs.dao.cache.converters.CachedAlarmCategoryConverter;
import kxd.scs.dao.device.converters.BaseAlarmCategoryConverter;
import kxd.util.KeyValue;

public class AlarmCategoryDao extends BaseDao {
	final public static String SQL_QUERY_CACHED_ALARMCATEGORY_BASIC = "select a.alarmclassid,a.alarmclassdesp,"
			+ "a.alarmLevel from alarmcategory a";
	final public static String SQL_QUERY_ALL_CACHED_ALARMCATEGORY = SQL_QUERY_CACHED_ALARMCATEGORY_BASIC
			+ " order by a.alarmclassid";
	final public static String SQL_QUERY_ALL_ALARMCATEGORY_ID = "select a.alarmclassid from"
			+ " alarmcategory a order by a.alarmclassid";
	final public static String SQL_QUERY_CACHED_ALARMCATEGORY_BYID = SQL_QUERY_CACHED_ALARMCATEGORY_BASIC
			+ " where a.alarmclassid=?1";
	final public static CachedAlarmCategoryConverter cachedAlarmCategoryConverter = new CachedAlarmCategoryConverter(
			CacheHelper.alarmCategoryMap);

	/**
	 * 从数据库获取全部的告警分类数据，并存储至缓存服务器
	 * 
	 * @param dao
	 * @return 缓存告警分类的ID列表
	 */
	static public KeyValue<byte[], List<?>> getCachedAlarmCategoryHashMapKeys(
			Dao dao) {
		final CachedHashMap<Integer, CachedAlarmCategory> map = CacheHelper.alarmCategoryMap;
		map.setKeysLoading(true);
		try {
			List<CachedAlarmCategory> ls = dao.queryAndSetCache(map,
					cachedAlarmCategoryConverter,
					SQL_QUERY_ALL_CACHED_ALARMCATEGORY);
			List<IntegerData> idList = new ArrayList<IntegerData>();
			for (Object o : ls) {
				CachedAlarmCategory org = (CachedAlarmCategory) o;
				idList.add(new IntegerData(org.getId()));
			}
			return new KeyValue<byte[], List<?>>(map.setKeys(idList), idList);
		} finally {
			map.setKeysLoading(false);
		}
	}

	/**
	 * 从数据库获取指定的告警分类数据，并存储至缓存服务器
	 * 
	 * @param dao
	 *            数据访问对象
	 * @param keys
	 *            要获取的告警分类ID列表
	 * @return 缓存告警分类列表
	 */
	static public List<?> getCachedAlarmCategorysList(Dao dao, List<?> keys) {
		StringBuffer sql = new StringBuffer(
				SQL_QUERY_CACHED_ALARMCATEGORY_BASIC);
		if (keys.size() > 0) {
			sql.append(" where (");
			for (int i = 0; i < keys.size(); i++) {
				if (i > 0)
					sql.append(" or ");
				sql.append(" a.alarmclassid=?" + (i + 1));
			}
			sql.append(")");
		}
		return dao.queryAndSetCache(CacheHelper.alarmCategoryMap,
				cachedAlarmCategoryConverter, sql.toString(), keys);
	}

	/**
	 * 通过告警分类ID，获取告警分类数据，并缓存至服务器
	 * 
	 * @param dao
	 *            数据访问对象
	 * @param id
	 *            告警分类ID
	 * @return 缓存告警分类对象
	 */
	static public CachedAlarmCategory getCachedAlarmCategory(Dao dao, int id) {
		List<CachedAlarmCategory> ls = dao.queryAndSetCache(
				CacheHelper.alarmCategoryMap, cachedAlarmCategoryConverter,
				SQL_QUERY_CACHED_ALARMCATEGORY_BYID, id);
		if (ls.size() > 0) {
			return ls.get(0);
		} else
			return null;
	}

	final public static BaseAlarmCategoryConverter baseConverter = new BaseAlarmCategoryConverter();
	final public static BaseAlarmCategoryConverter converter = new BaseAlarmCategoryConverter();
	final private static String fieldDefs = "a.alarmclassid,a.alarmclassdesp,a.alarmlevel";
	final private static String tableName = "alarmcategory";

	static private String jionWhereSql(String keyword) {
		String qlString = " from " + tableName + " a ";
		String whereString = "";
		if (keyword != null) {
			keyword = keyword.trim();
			if (keyword.length() > 0) {
				whereString = "a.alarmclassdesp like '" + keyword + "%'";
			}
		}
		if (whereString.length() > 0)
			whereString = " where " + whereString;
		return qlString + whereString + " order by a.alarmclassid";
	}

	static public List<BaseAlarmCategory> getAlarmCategoryList(Dao dao,
			long loginUserId, String keyword) {
		return dao.query(baseConverter, "select alarmclassid,alarmclassdesp"
				+ jionWhereSql(keyword));
	}

	static public boolean alarmCategoryDespExists(Dao dao,
			String alarmCategoryCode) {
		return !dao.query(
				"select * from alarmcategory a where alarmclassdesp=?1",
				alarmCategoryCode).isEmpty();
	}

	static public BaseAlarmCategory find(Dao dao, int alarmCategoryId) {
		String sql = "select " + fieldDefs + " from alarmcategory a "
				+ "where alarmclassid=?1";
		Iterator<BaseAlarmCategory> it = dao.query(converter, sql,
				alarmCategoryId).iterator();
		if (it.hasNext())
			return it.next();
		else
			return null;
	}

	static public int add(Dao dao, long loginUserId,
			BaseAlarmCategory alarmCategory) {
		BaseAlarmCategory u = find(dao, alarmCategory.getAlarmCategoryId());
		if (u != null)
			throw new AppException("告警分类ID重复.");
		if (alarmCategoryDespExists(dao, alarmCategory.getAlarmCategoryDesp()))
			throw new AppException("告警分类描述["
					+ alarmCategory.getAlarmCategoryDesp() + "]已被占用.");
		dao.insert(alarmCategory, converter);
		// 确保从数据库装入最新的数据
		CacheHelper.alarmCategoryMap.deleteCheckSum();
		addUserLog(dao, loginUserId,
				"添加告警分类[" + alarmCategory.getAlarmCategoryDesp() + "]");
		return alarmCategory.getAlarmCategoryId();
	}

	static public boolean hasAlarmCode(Dao dao, int alarmCategory) {
		return !dao.queryPage("select * from alarmcode where alarmclassid=?1",
				0, 1, alarmCategory).isEmpty();
	}

	static public void delete(Dao dao, long loginUserId,
			Integer[] alarmCategoryId) {
		for (int i = 0; i < alarmCategoryId.length; i++) {
			BaseAlarmCategory u = find(dao, alarmCategoryId[i]);
			if (u == null)
				throw new AppException("要删除的告警分类[" + alarmCategoryId + "]不存在.");
			if (hasAlarmCode(dao, alarmCategoryId[i]))
				throw new AppException("告警分类[" + u.getAlarmCategoryDesp()
						+ "]下定义了告警代码，不能删除.");
			dao.delete(u, converter);
			addUserLog(dao, loginUserId, "删除告警分类[" + u.getAlarmCategoryId()
					+ "]");
		}
		CacheHelper.alarmCategoryMap.removeAll(alarmCategoryId);
	}

	static public void edit(Dao dao, long loginUserId,
			BaseAlarmCategory alarmCategory) {
		BaseAlarmCategory u = find(dao, alarmCategory.getAlarmCategoryId());
		if (u == null)
			throw new AppException("要编辑的告警分类["
					+ alarmCategory.getAlarmCategoryId() + "]不存在.");
		if (!alarmCategory.getAlarmCategoryDesp().equals(
				u.getAlarmCategoryDesp())
				&& alarmCategoryDespExists(dao,
						alarmCategory.getAlarmCategoryDesp()))
			throw new AppException("告警分类描述["
					+ alarmCategory.getAlarmCategoryDesp() + "]已被占用.");
		dao.update(alarmCategory, converter);
		CacheHelper.alarmCategoryMap.remove(alarmCategory.getId());
		addUserLog(dao, loginUserId,
				"编辑告警分类[" + alarmCategory.getAlarmCategoryId() + "]");
	}

	static public QueryResult<BaseAlarmCategory> queryAlarmCategory(Dao dao,
			boolean queryCount, long loginUserId, Integer filter,
			String filterContent, int firstResult, int maxResults) {
		return query(dao, converter, queryCount, fieldDefs,
				jionWhereSql(filterContent), firstResult, maxResults);
	}

}
