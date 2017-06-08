package kxd.scs.dao.appdeploy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import kxd.engine.cache.CachedHashMap;
import kxd.engine.cache.beans.IntegerData;
import kxd.engine.cache.beans.sts.CachedAppCategory;
import kxd.engine.dao.Dao;
import kxd.engine.helper.CacheHelper;
import kxd.remote.scs.beans.BaseAppCategory;
import kxd.remote.scs.beans.appdeploy.EditedAppCategory;
import kxd.remote.scs.util.AppException;
import kxd.remote.scs.util.QueryResult;
import kxd.scs.dao.BaseDao;
import kxd.scs.dao.appdeploy.converters.BaseAppCategoryConverter;
import kxd.scs.dao.appdeploy.converters.EditedAppCategoryConverter;
import kxd.scs.dao.cache.converters.CachedAppCategoryConverter;
import kxd.util.KeyValue;

public class AppCategoryDao extends BaseDao {
	final public static BaseAppCategoryConverter baseConverter = new BaseAppCategoryConverter();
	final public static EditedAppCategoryConverter converter = new EditedAppCategoryConverter();
	final public static CachedAppCategoryConverter cachedConverter = new CachedAppCategoryConverter(
			CacheHelper.appCategoryMap);
	final private static String fieldDefs = "appcategoryid,appcategorycode,appcategorydesp";
	final private static String tableName = "appcategory";

	/**
	 * 从数据库获取全部的应用数据，并存储至缓存服务器
	 * 
	 * @param dao
	 * @return 缓存应用的ID列表
	 */
	static public KeyValue<byte[], List<?>> getCachedHashMapKeys(Dao dao) {
		final CachedHashMap<Integer, CachedAppCategory> map = CacheHelper.appCategoryMap;
		map.setKeysLoading(true);
		try {
			List<CachedAppCategory> ls = dao
					.queryAndSetCache(CacheHelper.appCategoryMap,
							cachedConverter,
							"select appcategoryid,appcategorycode,appcategorydesp from appcategory");
			List<IntegerData> idList = new ArrayList<IntegerData>();
			for (Object o : ls) {
				CachedAppCategory r = (CachedAppCategory) o;
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
	static public List<?> getCachedList(Dao dao, List<?> keys) {
		StringBuffer sql = new StringBuffer(
				"select appcategoryid,appcategorycode,appcategorydesp from appcategory a");
		if (keys.size() > 0) {
			sql.append(" where (");
			for (int i = 0; i < keys.size(); i++) {
				if (i > 0)
					sql.append(" or ");
				sql.append(" a.appcategoryid=?" + (i + 1));
			}
			sql.append(")");
		}
		return dao.queryAndSetCache(CacheHelper.appCategoryMap,
				cachedConverter, sql.toString(), keys);
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
	static public CachedAppCategory getCached(Dao dao, int id) {
		List<CachedAppCategory> ls = dao
				.queryAndSetCache(
						CacheHelper.appCategoryMap,
						cachedConverter,
						"select appcategoryid,appcategorycode,appcategorydesp from appcategory a where appcategoryid=?1",
						id);
		if (ls.size() > 0) {
			return ls.get(0);
		} else
			return null;
	}

	static private String jionWhereSql(String keyword) {
		String qlString = " from " + tableName + " a ";
		String whereString = "";
		if (keyword != null) {
			keyword = keyword.trim();
			if (keyword.length() > 0) {
				whereString = "a.appcategorydesp like '" + keyword
						+ "%' or a.appcategorycode like '" + keyword + "%'";
			}
		}
		if (whereString.length() > 0)
			whereString = " where " + whereString;
		return qlString + whereString + " order by appcategoryid";
	}

	static public List<BaseAppCategory> getAppCategoryList(Dao dao,
			long loginUserId, String keyword) {
		return dao.query(baseConverter, "select appcategoryid,appcategorydesp"
				+ jionWhereSql(keyword));
	}

	static public boolean appCategoryCodeExists(Dao dao, String appCategoryCode) {
		return !dao.query(
				"select * from appcategory a where appcategorycode=?1",
				appCategoryCode).isEmpty();
	}

	static public EditedAppCategory find(Dao dao, int appCategoryId) {
		String sql = "select " + fieldDefs + " from appcategory a "
				+ "where appcategoryid=?1";
		Iterator<EditedAppCategory> it = dao.query(converter, sql,
				appCategoryId).iterator();
		if (it.hasNext())
			return it.next();
		else
			return null;
	}

	static public int add(Dao dao, long loginUserId,
			EditedAppCategory appCategory) {
		if (appCategoryCodeExists(dao, appCategory.getAppCategoryCode()))
			throw new AppException("应用分类编码[" + appCategory.getAppCategoryCode()
					+ "]已被占用.");
		dao.insert(appCategory, converter);
		CacheHelper.appCategoryMap.deleteCheckSum();
		addUserLog(dao, loginUserId,
				"添加应用分类[" + appCategory.getAppCategoryCode() + "]");
		return appCategory.getAppCategoryId();
	}

	static public boolean hasApp(Dao dao, int appCategory) {
		return !dao.queryPage("select * from app where appcategoryid=?1", 0, 1,
				appCategory).isEmpty();
	}

	static public void delete(Dao dao, long loginUserId, Integer[] appCategoryId) {
		for (int i = 0; i < appCategoryId.length; i++) {
			EditedAppCategory u = find(dao, appCategoryId[i]);
			if (u == null)
				throw new AppException("要删除的应用分类[" + appCategoryId + "]不存在.");
			if (hasApp(dao, appCategoryId[i]))
				throw new AppException("应用分类[" + u.getAppCategoryDesp()
						+ "]下定义了应用，不能删除.");
			dao.delete(u, converter);
			addUserLog(dao, loginUserId, "删除应用分类[" + u.getAppCategoryId() + "]");
		}
		CacheHelper.appCategoryMap.removeAll(appCategoryId);
	}

	static public void edit(Dao dao, long loginUserId,
			EditedAppCategory appCategory) {
		EditedAppCategory u = find(dao, appCategory.getAppCategoryId());
		if (u == null)
			throw new AppException("要编辑的应用分类[" + appCategory.getAppCategoryId()
					+ "]不存在.");
		if (!appCategory.getAppCategoryCode().equals(u.getAppCategoryCode())
				&& appCategoryCodeExists(dao, appCategory.getAppCategoryCode()))
			throw new AppException("应用分类编码[" + appCategory.getAppCategoryCode()
					+ "]已被占用.");
		dao.update(appCategory, converter);
		CacheHelper.appCategoryMap.remove(appCategory.getId());
		addUserLog(dao, loginUserId, "编辑应用分类[" + appCategory.getAppCategoryId()
				+ "]");
	}

	static public QueryResult<EditedAppCategory> queryAppCategory(Dao dao,
			boolean queryCount, long loginUserId, Integer filter,
			String filterContent, int firstResult, int maxResults) {
		return query(dao, converter, queryCount, fieldDefs,
				jionWhereSql(filterContent), firstResult, maxResults);
	}
}
