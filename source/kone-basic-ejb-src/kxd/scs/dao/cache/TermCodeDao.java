package kxd.scs.dao.cache;

import java.util.List;

import kxd.engine.cache.beans.sts.CachedTermByCode;
import kxd.engine.dao.Dao;
import kxd.engine.helper.CacheHelper;
import kxd.scs.dao.cache.converters.CachedTermCodeConverter;
import kxd.util.KeyValue;

public class TermCodeDao {
	final public static String SQL_QUERY_CACHED_TERMCODE_BASIC = "select a.termCode,a.termId from term a ";
	final public static String SQL_QUERY_CACHED_TERMCODE_DATA = SQL_QUERY_CACHED_TERMCODE_BASIC
			+ "where a.termCode=?1";
	final public static CachedTermCodeConverter cachedTermCodeConverter = new CachedTermCodeConverter(
			CacheHelper.termCodeMap);

	/**
	 * 从数据库获取全部终端数据，并存储至缓存服务器
	 * 
	 * @param dao
	 * @return 缓存交易时间配置的ID列表
	 */
	static public KeyValue<byte[], List<?>> getCachedTermCodeHashMapKeys(Dao dao) {
		throw new UnsupportedOperationException();
	}

	/**
	 * 从数据库获取指定的终端数据，并存储至缓存服务器
	 * 
	 * @param dao
	 *            数据访问对象
	 * @param keys
	 *            要获取的终端编码列表
	 * @return 缓存终端编码列表
	 */
	static public List<?> getCachedTermCodeList(Dao dao, List<?> keys) {
		throw new UnsupportedOperationException();
	}

	/**
	 * 通过终端编码，获取终端信息数据，并缓存至服务器
	 * 
	 * @param dao
	 *            数据访问对象
	 * @param key
	 *            终端编码
	 * @return 终端信息
	 */
	static public CachedTermByCode getCachedTermCode(Dao dao, String key) {
		return dao.querySingalAndSetCache(CacheHelper.termCodeMap,
				new CachedTermByCode(key, true), 600, cachedTermCodeConverter,
				SQL_QUERY_CACHED_TERMCODE_DATA, key);
	}
}
