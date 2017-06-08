package kxd.scs.dao.cache;

import java.util.List;

import kxd.engine.cache.beans.sts.CachedOrgCode;
import kxd.engine.dao.Dao;
import kxd.engine.helper.CacheHelper;
import kxd.scs.dao.cache.converters.CachedOrgCodeConverter;
import kxd.util.KeyValue;

public class OrgCodeDao {
	final public static String SQL_QUERY_CACHED_ORGCODE_BASIC = "select a.orgCode,a.orgId from Org a ";
	final public static String SQL_QUERY_CACHED_ORGCODE_DATA = SQL_QUERY_CACHED_ORGCODE_BASIC
			+ "where a.orgCode=?1";
	final public static CachedOrgCodeConverter cachedOrgCodeConverter = new CachedOrgCodeConverter(
			CacheHelper.orgCodeMap);

	/**
	 * 从数据库获取全部机构数据，并存储至缓存服务器
	 * 
	 * @param dao
	 * @return 缓存交易时间配置的ID列表
	 */
	static public KeyValue<byte[], List<?>> getCachedOrgCodeHashMapKeys(Dao dao) {
		throw new UnsupportedOperationException();
	}

	/**
	 * 从数据库获取指定的机构数据，并存储至缓存服务器
	 * 
	 * @param dao
	 *            数据访问对象
	 * @param keys
	 *            要获取的机构编码列表
	 * @return 缓存机构编码列表
	 */
	static public List<?> getCachedOrgCodeList(Dao dao, List<?> keys) {
		throw new UnsupportedOperationException();
	}

	/**
	 * 通过机构编码，获取机构信息数据，并缓存至服务器
	 * 
	 * @param dao
	 *            数据访问对象
	 * @param key
	 *            机构编码
	 * @return 机构信息
	 */
	static public CachedOrgCode getCachedOrgCode(Dao dao, String key) {
		return dao.querySingalAndSetCache(CacheHelper.orgCodeMap,
				new CachedOrgCode(key, true), 600, cachedOrgCodeConverter,
				SQL_QUERY_CACHED_ORGCODE_DATA, key);
	}

	static public void buildCache(Dao dao) {
		dao.queryAndSetCache(CacheHelper.orgCodeMap, cachedOrgCodeConverter,
				SQL_QUERY_CACHED_ORGCODE_BASIC);
	}
}
