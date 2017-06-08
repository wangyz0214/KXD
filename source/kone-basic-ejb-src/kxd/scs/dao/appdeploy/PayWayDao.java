package kxd.scs.dao.appdeploy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import kxd.engine.cache.CachedHashMap;
import kxd.engine.cache.beans.ShortData;
import kxd.engine.cache.beans.sts.CachedPayWay;
import kxd.engine.dao.Dao;
import kxd.engine.helper.CacheHelper;
import kxd.remote.scs.beans.BasePayWay;
import kxd.remote.scs.util.AppException;
import kxd.remote.scs.util.QueryResult;
import kxd.scs.dao.BaseDao;
import kxd.scs.dao.appdeploy.converters.BasePayWayConverter;
import kxd.scs.dao.cache.converters.CachedPayWayConverter;
import kxd.util.KeyValue;

public class PayWayDao extends BaseDao {
	final public static String SQL_QUERY_CACHED_PAYWAY_BASIC = "select a.payway,a.paywaydesp,a.needtrade,a.type"
			+ " from payway a";
	final public static String SQL_QUERY_ALL_CACHED_PAYWAY = SQL_QUERY_CACHED_PAYWAY_BASIC
			+ " order by a.payway";
	final public static String SQL_QUERY_ALL_PAYWAY_ID = "select a.payway from"
			+ " payway a order by a.payway";
	final public static String SQL_QUERY_CACHED_PAYWAY_BYID = SQL_QUERY_CACHED_PAYWAY_BASIC
			+ " where a.payway=?1";
	final public static CachedPayWayConverter cachedPayWayConverter = new CachedPayWayConverter(
			CacheHelper.payWayMap);

	/**
	 * 从数据库获取全部的应用数据，并存储至缓存服务器
	 * 
	 * @param dao
	 * @return 缓存应用的ID列表
	 */
	static public KeyValue<byte[], List<?>> getCachedPayWayHashMapKeys(Dao dao) {
		final CachedHashMap<Short, CachedPayWay> map = CacheHelper.payWayMap;
		map.setKeysLoading(true);
		try {
			List<CachedPayWay> ls = dao.queryAndSetCache(CacheHelper.payWayMap,
					cachedPayWayConverter, SQL_QUERY_ALL_CACHED_PAYWAY);
			List<ShortData> idList = new ArrayList<ShortData>();
			for (Object o : ls) {
				CachedPayWay r = (CachedPayWay) o;
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
	static public List<?> getCachedPayWaysList(Dao dao, List<?> keys) {
		StringBuffer sql = new StringBuffer(SQL_QUERY_CACHED_PAYWAY_BASIC);
		if (keys.size() > 0) {
			sql.append(" where (");
			for (int i = 0; i < keys.size(); i++) {
				if (i > 0)
					sql.append(" or ");
				sql.append(" a.payway=?" + (i + 1));
			}
			sql.append(")");
		}
		return dao.queryAndSetCache(CacheHelper.payWayMap,
				cachedPayWayConverter, sql.toString(), keys);
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
	static public CachedPayWay getCachedPayWay(Dao dao, int id) {
		List<CachedPayWay> ls = dao.queryAndSetCache(CacheHelper.payWayMap,
				cachedPayWayConverter, SQL_QUERY_CACHED_PAYWAY_BYID, id);
		if (ls.size() > 0) {
			return ls.get(0);
		} else
			return null;
	}

	final public static BasePayWayConverter baseConverter = new BasePayWayConverter();
	final private static String fieldDefs = "a.payway,a.paywaydesp,a.needtrade,a.type";
	final private static String tableName = "payway";

	static private String jionWhereSql(boolean isDetail, String keyword) {
		String qlString = " from " + tableName + " a ";
		String whereString = "";
		if (keyword != null) {
			keyword = keyword.trim();
			if (keyword.length() > 0) {
				if (whereString.length() > 0)
					whereString += " and ";
				whereString = "a.paywaydesp like '" + keyword + "%'";
			}
		}

		if (whereString.length() > 0)
			whereString = " where " + whereString;
		return qlString + whereString + " order by a.payway";
	}

	static public List<BasePayWay> getPayWayList(Dao dao, long loginUserId,
			String keyword) {
		return dao.query(baseConverter,
				"select " + fieldDefs + jionWhereSql(false, keyword));
	}

	static public boolean payWayExists(Dao dao, String payWayDesp) {
		return !dao.query("select * from payway a where paywaydesp=?1",
				payWayDesp).isEmpty();
	}

	static public BasePayWay find(Dao dao, int payWay) {
		String sql = "select " + fieldDefs + " from payway a "
				+ "where payway=?1";
		Iterator<BasePayWay> it = dao.query(baseConverter, sql, payWay)
				.iterator();
		if (it.hasNext())
			return it.next();
		else
			return null;
	}

	static public short add(Dao dao, long loginUserId, BasePayWay payWay) {
		BasePayWay u = find(dao, payWay.getPayWayId());
		if (u != null)
			throw new AppException("收费渠道ID重复.");
		if (payWayExists(dao, payWay.getPayWayDesp()))
			throw new AppException("收费渠道描述[" + payWay.getPayWayDesp()
					+ "]已被占用.");
		dao.insert(payWay, baseConverter);
		// 确保从数据库装入最新的数据
		CacheHelper.payWayMap.deleteCheckSum();
		addUserLog(dao, loginUserId, "添加收费渠道[" + payWay.getPayWayDesp() + "]");
		return payWay.getId();
	}

	static public boolean hasTradeCode(Dao dao, int payway) {
		return !dao.queryPage("select * from tradecode where payway=?1", 0, 1,
				payway).isEmpty();
	}

	static public void delete(Dao dao, long loginUserId, Short[] payWay) {
		for (int i = 0; i < payWay.length; i++) {
			BasePayWay u = find(dao, payWay[i]);
			if (u == null)
				throw new AppException("要删除的收费渠道[" + payWay + "]不存在.");
			if (hasTradeCode(dao, payWay[i]))
				throw new AppException("收费渠道[" + u.getPayWayDesp()
						+ "]下还配置有交易代码，不能删除.");
			dao.delete(u, baseConverter);
			addUserLog(dao, loginUserId, "删除收费渠道[" + u.getPayWayDesp() + "]");
		}
		CacheHelper.payWayMap.removeAll(payWay);
	}

	static public void edit(Dao dao, long loginUserId, BasePayWay payWay) {
		BasePayWay u = find(dao, payWay.getPayWayId());
		if (u == null)
			throw new AppException("要编辑的收费渠道[" + payWay.getPayWayId() + "]不存在.");
		if (!payWay.getPayWayDesp().equals(u.getPayWayDesp())
				&& payWayExists(dao, payWay.getPayWayDesp()))
			throw new AppException("收费渠道描述[" + payWay.getPayWayDesp()
					+ "]已被占用.");
		dao.update(payWay, baseConverter);
		// 确保从数据库装入最新的数据
		CacheHelper.payWayMap.remove(payWay.getId());
		addUserLog(dao, loginUserId, "编辑收费渠道[" + payWay.getPayWayId() + "]");
	}

	static public QueryResult<BasePayWay> queryPayWay(Dao dao,
			boolean queryCount, long loginUserId, Integer filter,
			String filterContent, int firstResult, int maxResults) {
		return query(dao, baseConverter, queryCount, fieldDefs,
				jionWhereSql(true, filterContent), firstResult, maxResults);
	}
}
