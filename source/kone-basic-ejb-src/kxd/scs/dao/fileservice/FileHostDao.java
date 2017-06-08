package kxd.scs.dao.fileservice;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import kxd.engine.cache.CachedHashMap;
import kxd.engine.cache.beans.ShortData;
import kxd.engine.cache.beans.sts.CachedFileHost;
import kxd.engine.dao.Dao;
import kxd.engine.helper.CacheHelper;
import kxd.remote.scs.beans.BaseFileHost;
import kxd.remote.scs.util.AppException;
import kxd.remote.scs.util.QueryResult;
import kxd.scs.dao.BaseDao;
import kxd.scs.dao.cache.converters.CachedFileHostConverter;
import kxd.scs.dao.fileservice.converters.BaseFileHostConverter;
import kxd.util.KeyValue;

public class FileHostDao extends BaseDao {
	final public static String SQL_QUERY_CACHED_FILEHOST_BASIC = "select a.hostid,a.hostdesp,a.filerootdir"
			+ ",a.httpurlprefix,a.ftphost,a.ftpuser,a.ftppasswd,a.realhttpurlroot from filehost a";
	final public static String SQL_QUERY_ALL_CACHED_FILEHOST = SQL_QUERY_CACHED_FILEHOST_BASIC
			+ " order by a.hostid";
	final public static String SQL_QUERY_ALL_FILEHOST_ID = "select a.hostid from"
			+ " filehost a order by a.hostid";
	final public static String SQL_QUERY_CACHED_FILEHOST_BYID = SQL_QUERY_CACHED_FILEHOST_BASIC
			+ " where a.hostid=?1";
	final public static CachedFileHostConverter cachedFileHostConverter = new CachedFileHostConverter(
			CacheHelper.fileHostMap);

	/**
	 * 从数据库获取全部的应用数据，并存储至缓存服务器
	 * 
	 * @param dao
	 * @return 缓存应用的ID列表
	 */
	static public KeyValue<byte[], List<?>> getCachedFileHostHashMapKeys(Dao dao) {
		final CachedHashMap<Short, CachedFileHost> map = CacheHelper.fileHostMap;
		map.setKeysLoading(true);
		try {
			List<CachedFileHost> ls = dao.queryAndSetCache(
					CacheHelper.fileHostMap, cachedFileHostConverter,
					SQL_QUERY_ALL_CACHED_FILEHOST);
			List<ShortData> idList = new ArrayList<ShortData>();
			for (Object o : ls) {
				CachedFileHost r = (CachedFileHost) o;
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
	static public List<?> getCachedFileHostsList(Dao dao, List<?> keys) {
		StringBuffer sql = new StringBuffer(SQL_QUERY_CACHED_FILEHOST_BASIC);
		if (keys.size() > 0) {
			sql.append(" where (");
			for (int i = 0; i < keys.size(); i++) {
				if (i > 0)
					sql.append(" or ");
				sql.append(" a.hostid=?" + (i + 1));
			}
			sql.append(")");
		}
		return dao.queryAndSetCache(CacheHelper.fileHostMap,
				cachedFileHostConverter, sql.toString(), keys);
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
	static public CachedFileHost getCachedFileHost(Dao dao, short id) {
		List<CachedFileHost> ls = dao.queryAndSetCache(CacheHelper.fileHostMap,
				cachedFileHostConverter, SQL_QUERY_CACHED_FILEHOST_BYID, id);
		if (ls.size() > 0) {
			return ls.get(0);
		} else
			return null;
	}

	final public static BaseFileHostConverter baseConverter = new BaseFileHostConverter();
	final public static BaseFileHostConverter converter = new BaseFileHostConverter();
	final private static String fieldDefs = "a.hostid,a.hostdesp,a.filerootdir,"
			+ "a.httpurlprefix,a.ftphost,a.ftpuser,a.ftppasswd,a.realhttpurlroot";
	final private static String tableName = "filehost";

	static private String jionWhereSql(boolean isDetail, String keyword) {
		String qlString = " from " + tableName + " a ";
		String whereString = "";
		if (keyword != null) {
			keyword = keyword.trim();
			if (keyword.length() > 0) {
				if (whereString.length() > 0)
					whereString += " and ";
				whereString = "a.hostdesp like '" + keyword + "%'";
			}
		}
		if (whereString.length() > 0)
			whereString = " where " + whereString;
		return qlString + whereString + " order by a.hostid";
	}

	static public List<BaseFileHost> getFileHostList(Dao dao, long loginUserId,
			String keyword) {
		return dao.query(baseConverter,
				"select " + fieldDefs + jionWhereSql(false, keyword));
	}

	static public boolean fileHostExists(Dao dao, String fileHostDesp) {
		return !dao.query("select * from filehost a where hostdesp=?1",
				fileHostDesp).isEmpty();
	}

	static public BaseFileHost find(Dao dao, int fileHost) {
		String sql = "select " + fieldDefs + " from filehost a "
				+ "where hostid=?1";
		Iterator<BaseFileHost> it = dao.query(converter, sql, fileHost)
				.iterator();
		if (it.hasNext())
			return it.next();
		else
			return null;
	}

	static public short add(Dao dao, long loginUserId, BaseFileHost fileHost) {
		if (fileHostExists(dao, fileHost.getHostDesp()))
			throw new AppException("文件主机描述[" + fileHost.getHostDesp()
					+ "]已被占用.");
		dao.insert(fileHost, converter);
		// 确保从数据库装入最新的数据
		CacheHelper.fileHostMap.deleteCheckSum();
		addUserLog(dao, loginUserId, "添加文件主机[" + fileHost.getHostDesp() + "]");
		return fileHost.getId();
	}

	static public boolean hasFile(Dao dao, int id) {
		return !dao.queryPage("select * from filelist where hostid=?1", 0, 1,
				id).isEmpty();
	}

	static public void delete(Dao dao, long loginUserId, Short[] fileHost) {
		for (int i = 0; i < fileHost.length; i++) {
			BaseFileHost u = find(dao, fileHost[i]);
			if (u == null)
				throw new AppException("要删除的文件主机[" + fileHost + "]不存在.");
			if (hasFile(dao, fileHost[i]))
				throw new AppException("文件主机[" + u.getHostDesp()
						+ "]下还有文件，不能删除.");
			dao.delete(u, converter);
			addUserLog(dao, loginUserId, "删除文件主机[" + u.getHostDesp() + "]");
		}
		CacheHelper.fileHostMap.removeAll(fileHost);
	}

	static public void edit(Dao dao, long loginUserId, BaseFileHost fileHost) {
		BaseFileHost u = find(dao, fileHost.getHostId());
		if (u == null)
			throw new AppException("要编辑的文件主机[" + fileHost.getHostId() + "]不存在.");
		if (!fileHost.getHostDesp().equals(u.getHostDesp())
				&& fileHostExists(dao, fileHost.getHostDesp()))
			throw new AppException("文件主机描述[" + fileHost.getHostDesp()
					+ "]已被占用.");
		if (fileHost.getFtpPasswd() == null
				|| fileHost.getFtpPasswd().trim().isEmpty())
			fileHost.setFtpPasswd(u.getFtpPasswd());
		dao.update(fileHost, converter);
		// 确保从数据库装入最新的数据
		CacheHelper.fileHostMap.remove(fileHost.getId());
		addUserLog(dao, loginUserId, "编辑文件主机[" + fileHost.getHostId() + "]");
	}

	static public QueryResult<BaseFileHost> queryFileHost(Dao dao,
			boolean queryCount, long loginUserId, Integer filter,
			String filterContent, int firstResult, int maxResults) {
		return query(dao, converter, queryCount, fieldDefs,
				jionWhereSql(true, filterContent), firstResult, maxResults);
	}

}
