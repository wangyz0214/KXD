package kxd.scs.dao.fileservice;

import java.util.ArrayList;
import java.util.List;

import kxd.engine.dao.Dao;
import kxd.engine.helper.CacheHelper;
import kxd.engine.cache.CachedHashMap;
import kxd.engine.cache.beans.ShortStringData;
import kxd.engine.cache.beans.sts.CachedFileContentType;
import kxd.scs.dao.cache.converters.CachedFileContentTypeConverter;
import kxd.util.KeyValue;

public class FileContentTypeDao {
	final public static String SQL_QUERY_CACHED_FILECONTENTTYPE_BASIC = "select a.extname,a.contenttype"
			+ " from filecontenttype a";
	final public static String SQL_QUERY_ALL_CACHED_FILECONTENTTYPE = SQL_QUERY_CACHED_FILECONTENTTYPE_BASIC
			+ " order by a.extname";
	final public static String SQL_QUERY_ALL_FILECONTENTTYPE_ID = "select a.extname from"
			+ " filecontenttype a order by a.extname";
	final public static String SQL_QUERY_CACHED_FILECONTENTTYPE_BYID = SQL_QUERY_CACHED_FILECONTENTTYPE_BASIC
			+ " where a.extname=?1";
	final public static CachedFileContentTypeConverter cachedFileContentTypeConverter = new CachedFileContentTypeConverter(
			CacheHelper.fileContentTypeMap);

	/**
	 * 从数据库获取全部的应用数据，并存储至缓存服务器
	 * 
	 * @param dao
	 * @return 缓存应用的ID列表
	 */
	static public KeyValue<byte[], List<?>> getCachedFileContentTypeHashMapKeys(
			Dao dao) {
		final CachedHashMap<String, CachedFileContentType> map = CacheHelper.fileContentTypeMap;
		map.setKeysLoading(true);
		try {
			List<CachedFileContentType> ls = dao.queryAndSetCache(
					CacheHelper.fileContentTypeMap,
					cachedFileContentTypeConverter,
					SQL_QUERY_ALL_CACHED_FILECONTENTTYPE);
			List<ShortStringData> idList = new ArrayList<ShortStringData>();
			for (Object o : ls) {
				CachedFileContentType r = (CachedFileContentType) o;
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
	static public List<?> getCachedFileContentTypesList(Dao dao, List<?> keys) {
		StringBuffer sql = new StringBuffer(
				SQL_QUERY_CACHED_FILECONTENTTYPE_BASIC);
		if (keys.size() > 0) {
			sql.append(" where (");
			for (int i = 0; i < keys.size(); i++) {
				if (i > 0)
					sql.append(" or ");
				sql.append(" a.extname='" + keys.get(i) + "'");
			}
			sql.append(")");
		}
		return dao.queryAndSetCache(CacheHelper.fileContentTypeMap,
				cachedFileContentTypeConverter, sql.toString());
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
	static public CachedFileContentType getCachedFileContentType(Dao dao,
			String id) {
		List<CachedFileContentType> ls = dao.queryAndSetCache(
				CacheHelper.fileContentTypeMap, cachedFileContentTypeConverter,
				SQL_QUERY_CACHED_FILECONTENTTYPE_BYID, id);
		if (ls.size() > 0) {
			return ls.get(0);
		} else
			return null;
	}

}
