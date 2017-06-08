package kxd.scs.dao.fileservice;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import kxd.engine.cache.CachedHashMap;
import kxd.engine.cache.beans.ShortData;
import kxd.engine.cache.beans.sts.CachedFileOwner;
import kxd.engine.dao.Dao;
import kxd.engine.helper.CacheHelper;
import kxd.remote.scs.beans.BaseFileOwner;
import kxd.remote.scs.util.AppException;
import kxd.remote.scs.util.QueryResult;
import kxd.scs.dao.BaseDao;
import kxd.scs.dao.cache.converters.CachedFileOwnerConverter;
import kxd.scs.dao.fileservice.converters.BaseFileOwnerConverter;
import kxd.util.KeyValue;

public class FileOwnerDao extends BaseDao {
	final public static String SQL_QUERY_CACHED_FILEOWNER_BASIC = "select a.fileownerid,a.fileownerdesp,a.visitright"
			+ " from fileowner a";
	final public static String SQL_QUERY_ALL_CACHED_FILEOWNER = SQL_QUERY_CACHED_FILEOWNER_BASIC
			+ " order by a.fileownerid";
	final public static String SQL_QUERY_ALL_FILEOWNER_ID = "select a.fileownerid from"
			+ " fileowner a order by a.fileownerid";
	final public static String SQL_QUERY_CACHED_FILEOWNER_BYID = SQL_QUERY_CACHED_FILEOWNER_BASIC
			+ " where a.fileownerid=?1";
	final public static CachedFileOwnerConverter cachedFileOwnerConverter = new CachedFileOwnerConverter(
			CacheHelper.fileOwnerMap);

	/**
	 * 从数据库获取全部的应用数据，并存储至缓存服务器
	 * 
	 * @param dao
	 * @return 缓存应用的ID列表
	 */
	static public KeyValue<byte[], List<?>> getCachedFileOwnerHashMapKeys(
			Dao dao) {
		final CachedHashMap<Short, CachedFileOwner> map = CacheHelper.fileOwnerMap;
		map.setKeysLoading(true);
		try {
			List<CachedFileOwner> ls = dao.queryAndSetCache(
					CacheHelper.fileOwnerMap, cachedFileOwnerConverter,
					SQL_QUERY_ALL_CACHED_FILEOWNER);
			List<ShortData> idList = new ArrayList<ShortData>();
			for (Object o : ls) {
				CachedFileOwner r = (CachedFileOwner) o;
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
	static public List<?> getCachedFileOwnersList(Dao dao, List<?> keys) {
		StringBuffer sql = new StringBuffer(SQL_QUERY_CACHED_FILEOWNER_BASIC);
		if (keys.size() > 0) {
			sql.append(" where (");
			for (int i = 0; i < keys.size(); i++) {
				if (i > 0)
					sql.append(" or ");
				sql.append(" a.fileownerid=?" + (i + 1));
			}
			sql.append(")");
		}
		return dao.queryAndSetCache(CacheHelper.fileOwnerMap,
				cachedFileOwnerConverter, sql.toString(), keys);
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
	static public CachedFileOwner getCachedFileOwner(Dao dao, short id) {
		List<CachedFileOwner> ls = dao.queryAndSetCache(
				CacheHelper.fileOwnerMap, cachedFileOwnerConverter,
				SQL_QUERY_CACHED_FILEOWNER_BYID, id);
		if (ls.size() > 0) {
			return ls.get(0);
		} else
			return null;
	}

	final public static BaseFileOwnerConverter baseConverter = new BaseFileOwnerConverter();
	final public static BaseFileOwnerConverter converter = new BaseFileOwnerConverter();
	final private static String fieldDefs = "a.fileownerid,a.fileownerdesp,a.visitright";
	final private static String tableName = "fileowner";

	static private String jionWhereSql(boolean isDetail, String keyword) {
		String qlString = " from " + tableName + " a ";
		String whereString = "";
		if (keyword != null) {
			keyword = keyword.trim();
			if (keyword.length() > 0) {
				if (whereString.length() > 0)
					whereString += " and ";
				whereString = "a.fileownerdesp like '" + keyword + "%'";
			}
		}
		if (whereString.length() > 0)
			whereString = " where " + whereString;
		return qlString + whereString + " order by a.fileownerid";
	}

	static public List<BaseFileOwner> getFileOwnerList(Dao dao,
			long loginUserId, String keyword) {
		return dao.query(baseConverter,
				"select " + fieldDefs + jionWhereSql(false, keyword));
	}

	static public boolean fileOwnerExists(Dao dao, String fileOwnerDesp) {
		return !dao.query("select * from fileowner a where fileownerdesp=?1",
				fileOwnerDesp).isEmpty();
	}

	static public BaseFileOwner find(Dao dao, int fileOwner) {
		String sql = "select " + fieldDefs + " from fileowner a "
				+ "where fileownerid=?1";
		Iterator<BaseFileOwner> it = dao.query(converter, sql, fileOwner)
				.iterator();
		if (it.hasNext())
			return it.next();
		else
			return null;
	}

	static public short add(Dao dao, long loginUserId, BaseFileOwner fileOwner) {
		if (fileOwnerExists(dao, fileOwner.getFileOwnerDesp()))
			throw new AppException("文件属主描述[" + fileOwner.getFileOwnerDesp()
					+ "]已被占用.");
		dao.insert(fileOwner, converter);
		// 确保从数据库装入最新的数据
		CacheHelper.fileOwnerMap.deleteCheckSum();
		addUserLog(dao, loginUserId, "添加文件属主[" + fileOwner.getFileOwnerDesp()
				+ "]");
		return fileOwner.getId();
	}

	static public boolean hasFile(Dao dao, int id) {
		return !dao.queryPage("select * from filelist where fileownerid=?1", 0,
				1, id).isEmpty();
	}

	static public boolean hasUser(Dao dao, int id) {
		return !dao.queryPage("select * from fileuser where fileownerid=?1", 0,
				1, id).isEmpty();
	}

	static public void delete(Dao dao, long loginUserId, Short[] fileOwner) {
		for (int i = 0; i < fileOwner.length; i++) {
			BaseFileOwner u = find(dao, fileOwner[i]);
			if (u == null)
				throw new AppException("要删除的文件属主[" + fileOwner + "]不存在.");
			if (hasFile(dao, fileOwner[i]))
				throw new AppException("文件属主[" + u.getFileOwnerDesp()
						+ "]下还有文件，不能删除.");
			if (hasUser(dao, fileOwner[i]))
				throw new AppException("文件属主[" + u.getFileOwnerDesp()
						+ "]下还有用户，不能删除.");
			dao.delete(u, converter);
			addUserLog(dao, loginUserId, "删除文件属主[" + u.getFileOwnerDesp() + "]");
		}
		CacheHelper.fileOwnerMap.removeAll(fileOwner);
	}

	static public void edit(Dao dao, long loginUserId, BaseFileOwner fileOwner) {
		BaseFileOwner u = find(dao, fileOwner.getFileOwnerId());
		if (u == null)
			throw new AppException("要编辑的文件属主[" + fileOwner.getFileOwnerId()
					+ "]不存在.");
		if (!fileOwner.getFileOwnerDesp().equals(u.getFileOwnerDesp())
				&& fileOwnerExists(dao, fileOwner.getFileOwnerDesp()))
			throw new AppException("文件属主描述[" + fileOwner.getFileOwnerDesp()
					+ "]已被占用.");
		dao.update(fileOwner, converter);
		// 确保从数据库装入最新的数据
		CacheHelper.fileOwnerMap.remove(fileOwner.getId());
		addUserLog(dao, loginUserId, "编辑文件属主[" + fileOwner.getFileOwnerId()
				+ "]");
	}

	static public QueryResult<BaseFileOwner> queryFileOwner(Dao dao,
			boolean queryCount, long loginUserId, Integer filter,
			String filterContent, int firstResult, int maxResults) {
		return query(dao, converter, queryCount, fieldDefs,
				jionWhereSql(true, filterContent), firstResult, maxResults);
	}

}
