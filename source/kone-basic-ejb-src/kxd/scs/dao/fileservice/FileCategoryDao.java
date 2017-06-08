package kxd.scs.dao.fileservice;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import kxd.engine.cache.CachedHashMap;
import kxd.engine.cache.beans.ShortData;
import kxd.engine.cache.beans.sts.CachedFileCategory;
import kxd.engine.dao.Dao;
import kxd.engine.helper.CacheHelper;
import kxd.remote.scs.beans.BaseFileCategory;
import kxd.remote.scs.util.AppException;
import kxd.remote.scs.util.QueryResult;
import kxd.scs.dao.BaseDao;
import kxd.scs.dao.cache.converters.CachedFileCategoryConverter;
import kxd.scs.dao.fileservice.converters.BaseFileCategoryConverter;
import kxd.util.KeyValue;

public class FileCategoryDao extends BaseDao {
	final public static String SQL_QUERY_CACHED_FILECATEGORY_BASIC = "select a.categoryid,a.categorydesp,a.cachedtype,a.filehost"
			+ " from filecategory a";
	final public static String SQL_QUERY_ALL_CACHED_FILECATEGORY = SQL_QUERY_CACHED_FILECATEGORY_BASIC
			+ " order by a.categoryid";
	final public static String SQL_QUERY_ALL_FILECATEGORY_ID = "select a.categoryid from"
			+ " filecategory a order by a.categoryid";
	final public static String SQL_QUERY_CACHED_FILECATEGORY_BYID = SQL_QUERY_CACHED_FILECATEGORY_BASIC
			+ " where a.categoryid=?1";
	final public static CachedFileCategoryConverter cachedFileCategoryConverter = new CachedFileCategoryConverter(
			CacheHelper.fileCategoryMap);

	/**
	 * 从数据库获取全部的应用数据，并存储至缓存服务器
	 * 
	 * @param dao
	 * @return 缓存应用的ID列表
	 */
	static public KeyValue<byte[], List<?>> getCachedFileCategoryHashMapKeys(
			Dao dao) {
		final CachedHashMap<Short, CachedFileCategory> map = CacheHelper.fileCategoryMap;
		map.setKeysLoading(true);
		try {
			List<CachedFileCategory> ls = dao.queryAndSetCache(
					CacheHelper.fileCategoryMap, cachedFileCategoryConverter,
					SQL_QUERY_ALL_CACHED_FILECATEGORY);
			List<ShortData> idList = new ArrayList<ShortData>();
			for (Object o : ls) {
				CachedFileCategory r = (CachedFileCategory) o;
				idList.add(new ShortData(r.getId()));
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
	static public List<?> getCachedFileCategorysList(Dao dao, List<?> keys) {
		StringBuffer sql = new StringBuffer(SQL_QUERY_CACHED_FILECATEGORY_BASIC);
		if (keys.size() > 0) {
			sql.append(" where (");
			for (int i = 0; i < keys.size(); i++) {
				if (i > 0)
					sql.append(" or ");
				sql.append(" a.categoryid=?" + (i + 1));
			}
			sql.append(")");
		}
		return dao.queryAndSetCache(CacheHelper.fileCategoryMap,
				cachedFileCategoryConverter, sql.toString(), keys);
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
	static public CachedFileCategory getCachedFileCategory(Dao dao, short id) {
		List<CachedFileCategory> ls = dao.queryAndSetCache(
				CacheHelper.fileCategoryMap, cachedFileCategoryConverter,
				SQL_QUERY_CACHED_FILECATEGORY_BYID, id);
		if (ls.size() > 0) {
			return ls.get(0);
		} else
			return null;
	}

	final public static BaseFileCategoryConverter baseConverter = new BaseFileCategoryConverter();
	final public static BaseFileCategoryConverter converter = new BaseFileCategoryConverter();
	final private static String fieldDefs = "a.categoryid,a.categorydesp,a.cachedtype,a.filehost";
	final private static String tableName = "filecategory";

	static private String jionWhereSql(boolean isDetail, String keyword) {
		String qlString = " from " + tableName + " a ";
		String whereString = "";
		if (keyword != null) {
			keyword = keyword.trim();
			if (keyword.length() > 0) {
				if (whereString.length() > 0)
					whereString += " and ";
				whereString = "a.categorydesp like '" + keyword + "%'";
			}
		}
		if (whereString.length() > 0)
			whereString = " where " + whereString;
		return qlString + whereString + " order by a.categoryid";
	}

	static public List<BaseFileCategory> getFileCategoryList(Dao dao,
			long loginUserId, String keyword) {
		return dao.query(baseConverter,
				"select " + fieldDefs + jionWhereSql(false, keyword));
	}

	static public boolean fileCategoryExists(Dao dao, String fileCategoryDesp) {
		return !dao.query("select * from filecategory a where categorydesp=?1",
				fileCategoryDesp).isEmpty();
	}

	static public BaseFileCategory find(Dao dao, int fileCategory) {
		String sql = "select " + fieldDefs + " from filecategory a "
				+ "where categoryid=?1";
		Iterator<BaseFileCategory> it = dao.query(converter, sql, fileCategory)
				.iterator();
		if (it.hasNext())
			return it.next();
		else
			return null;
	}

	static public short add(Dao dao, long loginUserId,
			BaseFileCategory fileCategory) {
		if (fileCategoryExists(dao, fileCategory.getFileCategoryDesp()))
			throw new AppException("文件分类描述["
					+ fileCategory.getFileCategoryDesp() + "]已被占用.");
		dao.insert(fileCategory, converter);
		// 确保从数据库装入最新的数据
		CacheHelper.fileCategoryMap.deleteCheckSum();
		addUserLog(dao, loginUserId,
				"添加文件分类[" + fileCategory.getFileCategoryDesp() + "]");
		return fileCategory.getId();
	}

	static public boolean hasFile(Dao dao, int id) {
		return !dao.queryPage("select * from filelist where categoryid=?1", 0,
				1, id).isEmpty();
	}

	static public void delete(Dao dao, long loginUserId, Short[] fileCategory) {
		for (int i = 0; i < fileCategory.length; i++) {
			BaseFileCategory u = find(dao, fileCategory[i]);
			if (u == null)
				throw new AppException("要删除的文件分类[" + fileCategory + "]不存在.");
			if (hasFile(dao, fileCategory[i]))
				throw new AppException("文件分类[" + u.getFileCategoryDesp()
						+ "]下还有文件，不能删除.");
			dao.delete(u, converter);
			addUserLog(dao, loginUserId, "删除文件分类[" + u.getFileCategoryDesp()
					+ "]");
		}
		CacheHelper.fileCategoryMap.removeAll(fileCategory);
	}

	static public void edit(Dao dao, long loginUserId,
			BaseFileCategory fileCategory) {
		BaseFileCategory u = find(dao, fileCategory.getFileCategoryId());
		if (u == null)
			throw new AppException("要编辑的文件分类["
					+ fileCategory.getFileCategoryId() + "]不存在.");
		if (!fileCategory.getFileCategoryDesp().equals(u.getFileCategoryDesp())
				&& fileCategoryExists(dao, fileCategory.getFileCategoryDesp()))
			throw new AppException("文件分类描述["
					+ fileCategory.getFileCategoryDesp() + "]已被占用.");
		dao.update(fileCategory, converter);
		// 确保从数据库装入最新的数据
		CacheHelper.fileCategoryMap.remove(fileCategory.getId());
		addUserLog(dao, loginUserId,
				"编辑文件分类[" + fileCategory.getFileCategoryId() + "]");
	}

	static public QueryResult<BaseFileCategory> queryFileCategory(Dao dao,
			boolean queryCount, long loginUserId, Integer filter,
			String filterContent, int firstResult, int maxResults) {
		return query(dao, converter, queryCount, fieldDefs,
				jionWhereSql(true, filterContent), firstResult, maxResults);
	}

}
