package kxd.scs.dao.fileservice;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import kxd.engine.cache.CachedHashMap;
import kxd.engine.cache.beans.ShortStringData;
import kxd.engine.cache.beans.sts.CachedFileUser;
import kxd.engine.dao.Dao;
import kxd.engine.helper.CacheHelper;
import kxd.remote.scs.beans.BaseFileUser;
import kxd.remote.scs.util.AppException;
import kxd.remote.scs.util.QueryResult;
import kxd.scs.dao.BaseDao;
import kxd.scs.dao.cache.converters.CachedFileUserConverter;
import kxd.scs.dao.fileservice.converters.BaseFileUserConverter;
import kxd.util.KeyValue;

public class FileUserDao extends BaseDao {
	final public static String SQL_QUERY_CACHED_FILEUSER_BASIC = "select a.fileusercode,a.fileownerid"
			+ ",a.fileuserpwd from fileuser a";
	final public static String SQL_QUERY_ALL_CACHED_FILEUSER = SQL_QUERY_CACHED_FILEUSER_BASIC
			+ " order by a.fileusercode";
	final public static String SQL_QUERY_ALL_FILEUSER_ID = "select a.fileusercode from"
			+ " fileuser a order by a.fileusercode";
	final public static String SQL_QUERY_CACHED_FILEUSER_BYID = SQL_QUERY_CACHED_FILEUSER_BASIC
			+ " where a.fileusercode=?1";
	final public static CachedFileUserConverter cachedFileUserConverter = new CachedFileUserConverter(
			CacheHelper.fileUserMap);

	/**
	 * 从数据库获取全部的应用数据，并存储至缓存服务器
	 * 
	 * @param dao
	 * @return 缓存应用的ID列表
	 */
	static public KeyValue<byte[], List<?>> getCachedFileUserHashMapKeys(Dao dao) {
		final CachedHashMap<String, CachedFileUser> map = CacheHelper.fileUserMap;
		map.setKeysLoading(true);
		try {
			List<CachedFileUser> ls = dao.queryAndSetCache(
					CacheHelper.fileUserMap, cachedFileUserConverter,
					SQL_QUERY_ALL_CACHED_FILEUSER);
			List<ShortStringData> idList = new ArrayList<ShortStringData>();
			for (Object o : ls) {
				CachedFileUser r = (CachedFileUser) o;
				idList.add(new ShortStringData(r.getId()));
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
	static public List<?> getCachedFileUsersList(Dao dao, List<?> keys) {
		StringBuffer sql = new StringBuffer(SQL_QUERY_CACHED_FILEUSER_BASIC);
		if (keys.size() > 0) {
			sql.append(" where (");
			for (int i = 0; i < keys.size(); i++) {
				if (i > 0)
					sql.append(" or ");
				sql.append(" a.fileusercode=?" + (i + 1));
			}
			sql.append(")");
		}
		return dao.queryAndSetCache(CacheHelper.fileUserMap,
				cachedFileUserConverter, sql.toString(), keys);
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
	static public CachedFileUser getCachedFileUser(Dao dao, String id) {
		List<CachedFileUser> ls = dao.queryAndSetCache(CacheHelper.fileUserMap,
				cachedFileUserConverter, SQL_QUERY_CACHED_FILEUSER_BYID, id);
		if (ls.size() > 0) {
			return ls.get(0);
		} else
			return null;
	}

	final public static BaseFileUserConverter baseConverter = new BaseFileUserConverter();
	final public static BaseFileUserConverter converter = new BaseFileUserConverter();
	final private static String fieldDefs = "a.fileusercode,a.fileownerid,fileownerdesp,a.fileuserpwd";
	final private static String tableName = "fileuser";

	static private String jionWhereSql(boolean isDetail, String keyword) {
		String qlString = " from " + tableName + " a,fileowner b ";
		String whereString = "a.fileownerid=b.fileownerid";
		if (keyword != null) {
			keyword = keyword.trim();
			if (keyword.length() > 0) {
				if (whereString.length() > 0)
					whereString += " and ";
				whereString = "a.fileuserdesp like '" + keyword + "%'";
			}
		}
		if (whereString.length() > 0)
			whereString = " where " + whereString;
		return qlString + whereString + " order by a.fileusercode";
	}

	static public List<BaseFileUser> getFileUserList(Dao dao, long loginUserId,
			String keyword) {
		return dao.query(baseConverter,
				"select " + fieldDefs + jionWhereSql(false, keyword));
	}

	static public BaseFileUser find(Dao dao, String fileUser) {
		String sql = "select " + fieldDefs + " from fileuser a,fileowner b "
				+ "where fileusercode=?1 and a.fileownerid=b.fileownerid";
		Iterator<BaseFileUser> it = dao.query(converter, sql, fileUser)
				.iterator();
		if (it.hasNext())
			return it.next();
		else
			return null;
	}

	static public void add(Dao dao, long loginUserId, BaseFileUser fileUser) {
		if (find(dao, fileUser.getId()) != null)
			throw new AppException("文件用户编码[" + fileUser.getId() + "]已被占用.");
		dao.insert(fileUser, converter);
		// 确保从数据库装入最新的数据
		CacheHelper.fileUserMap.deleteCheckSum();
		addUserLog(dao, loginUserId, "添加文件用户[" + fileUser.getId() + "]");
	}

	static public boolean hasFile(Dao dao, int id) {
		return !dao.queryPage("select * from filelist where categoryid=?1", 0,
				1, id).isEmpty();
	}

	static public void delete(Dao dao, long loginUserId, String[] fileUser) {
		for (int i = 0; i < fileUser.length; i++) {
			BaseFileUser u = find(dao, fileUser[i]);
			if (u == null)
				throw new AppException("要删除的文件用户[" + fileUser + "]不存在.");
			dao.delete(u, converter);
			addUserLog(dao, loginUserId, "删除文件用户[" + u.getId() + "]");
		}
		CacheHelper.fileUserMap.removeAll(fileUser);
	}

	static public void edit(Dao dao, long loginUserId, BaseFileUser fileUser) {
		BaseFileUser u = find(dao, fileUser.getFileUserId());
		if (u == null)
			throw new AppException("要编辑的文件用户[" + fileUser.getFileUserId()
					+ "]不存在.");
		dao.update(fileUser, converter);
		// 确保从数据库装入最新的数据
		CacheHelper.fileUserMap.remove(fileUser.getId());
		addUserLog(dao, loginUserId, "编辑文件用户[" + fileUser.getFileUserId() + "]");
	}

	static public QueryResult<BaseFileUser> queryFileUser(Dao dao,
			boolean queryCount, long loginUserId, Integer filter,
			String filterContent, int firstResult, int maxResults) {
		return query(dao, converter, queryCount, fieldDefs,
				jionWhereSql(true, filterContent), firstResult, maxResults);
	}

}
