package kxd.scs.dao.appdeploy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import kxd.engine.cache.CachedHashMap;
import kxd.engine.cache.beans.IntegerData;
import kxd.engine.cache.beans.sts.CachedTradeCode;
import kxd.engine.dao.Dao;
import kxd.engine.helper.CacheHelper;
import kxd.remote.scs.beans.BaseTradeCode;
import kxd.remote.scs.beans.appdeploy.EditedTradeCode;
import kxd.remote.scs.util.AppException;
import kxd.remote.scs.util.QueryResult;
import kxd.scs.dao.BaseDao;
import kxd.scs.dao.appdeploy.converters.BaseTradeCodeConverter;
import kxd.scs.dao.appdeploy.converters.EditedTradeCodeConverter;
import kxd.scs.dao.cache.converters.CachedTradeCodeConverter;
import kxd.util.KeyValue;

public class TradeCodeDao extends BaseDao {
	final public static String SQL_QUERY_CACHED_TRADECODE_BASIC = "select a.tradecodeid,a.tradecode,a.tradeservice"
			+ ",a.stated,a.logged,a.striketadecodeid,a.tradecodedesp,a.payway,a.payitem,a.refund_mode,a.redo_mode,"
			+ "a.cancel_refund_mode" + ",a.businessid from tradecode a";
	final public static String SQL_QUERY_ALL_CACHED_TRADECODE = SQL_QUERY_CACHED_TRADECODE_BASIC
			+ " order by a.tradecodeid";
	final public static String SQL_QUERY_ALL_TRADECODE_ID = "select a.tradecodeid from"
			+ " tradecode a order by a.tradecodeid";
	final public static String SQL_QUERY_CACHED_TRADECODE_BYID = SQL_QUERY_CACHED_TRADECODE_BASIC
			+ " where a.tradecodeid=?1";
	final public static CachedTradeCodeConverter cachedTradeCodeConverter = new CachedTradeCodeConverter(
			CacheHelper.tradeCodeMap);
	final public static String SQL_QUERY_TRADECODE_BYCODESERV = "select a.tradecodeid from tradecode a"
			+ " where a.tradecode=?1 and a.tradeservice=?2";

	/**
	 * 从数据库获取全部的应用数据，并存储至缓存服务器
	 * 
	 * @param dao
	 * @return 缓存应用的ID列表
	 */
	static public KeyValue<byte[], List<?>> getCachedTradeCodeHashMapKeys(
			Dao dao) {
		final CachedHashMap<Integer, CachedTradeCode> map = CacheHelper.tradeCodeMap;
		map.setKeysLoading(true);
		try {
			List<CachedTradeCode> ls = dao.queryAndSetCache(
					CacheHelper.tradeCodeMap, cachedTradeCodeConverter,
					SQL_QUERY_ALL_CACHED_TRADECODE);
			List<IntegerData> idList = new ArrayList<IntegerData>();
			for (Object o : ls) {
				CachedTradeCode r = (CachedTradeCode) o;
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
	static public List<?> getCachedTradeCodesList(Dao dao, List<?> keys) {
		StringBuffer sql = new StringBuffer(SQL_QUERY_CACHED_TRADECODE_BASIC);
		if (keys.size() > 0) {
			sql.append(" where (");
			for (int i = 0; i < keys.size(); i++) {
				if (i > 0)
					sql.append(" or ");
				sql.append(" a.tradecodeid=?" + (i + 1));
			}
			sql.append(")");
		}
		return dao.queryAndSetCache(CacheHelper.tradeCodeMap,
				cachedTradeCodeConverter, sql.toString(), keys);
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
	static public CachedTradeCode getCachedTradeCode(Dao dao, int id) {
		List<CachedTradeCode> ls = dao.queryAndSetCache(
				CacheHelper.tradeCodeMap, cachedTradeCodeConverter,
				SQL_QUERY_CACHED_TRADECODE_BYID, id);
		if (ls.size() > 0) {
			return ls.get(0);
		} else
			return null;
	}

	final public static BaseTradeCodeConverter baseConverter = new BaseTradeCodeConverter();
	final public static EditedTradeCodeConverter converter = new EditedTradeCodeConverter();
	final private static String fieldDefs = "a.tradecodeid,a.tradecodedesp,a.tradecode,"
			+ "a.tradeservice,a.businessid,businessdesp,a.payway,paywaydesp,a.payitem,payitemdesp,"
			+ "a.stated,a.logged,a.striketadecodeid,a.refund_mode,a.redo_mode,a.cancel_refund_mode";
	final private static String tableName = "tradecode";

	static private String jionWhereSql(boolean isDetail, String keyword) {
		String qlString = " from " + tableName + " a,business d ";
		String whereString = "a.businessid=d.businessid";
		if (isDetail) {
			qlString += ",payway b,payitem c";
			whereString += " and a.payway=b.payway(+) and a.payitem=c.payitem(+)";
		}
		if (keyword != null) {
			keyword = keyword.trim();
			if (keyword.length() > 0) {
				if (whereString.length() > 0)
					whereString += " and ";
				whereString += "a.tradecodedesp like '" + keyword + "%'";
			}
		}
		if (whereString.length() > 0)
			whereString = " where " + whereString;
		return qlString + whereString + " order by a.tradecodeid";
	}

	static public List<BaseTradeCode> getTradeCodeList(Dao dao,
			long loginUserId, String keyword) {
		return dao.query(
				baseConverter,
				"select a.tradecodeid,a.tradecodedesp,a.tradecode,a.tradeservice,"
						+ "a.businessid,businessdesp"
						+ jionWhereSql(false, keyword));
	}

	static public boolean tradecodeDespExists(Dao dao, String tradecodeCode) {
		return !dao.query("select * from tradecode a where tradecodedesp=?1",
				tradecodeCode).isEmpty();
	}

	static public EditedTradeCode find(Dao dao, int tradecodeId) {
		String sql = "select " + fieldDefs
				+ " from tradecode a,business d,payway b,payitem c "
				+ "where a.tradecodeid=?1 and a.businessid=d.businessid "
				+ "and a.payway=b.payway(+) and a.payitem=c.payitem(+)";
		Iterator<EditedTradeCode> it = dao.query(converter, sql, tradecodeId)
				.iterator();
		if (it.hasNext())
			return it.next();
		else
			return null;
	}

	static public int add(Dao dao, long loginUserId, EditedTradeCode tradecode) {
		if (tradecodeDespExists(dao, tradecode.getTradeCodeDesp()))
			throw new AppException("交易代码描述[" + tradecode.getTradeCodeDesp()
					+ "]已被占用.");
		dao.insert(tradecode, converter);
		// 确保从数据库装入最新的数据
		CacheHelper.tradeCodeMap.deleteCheckSum();
		addUserLog(dao, loginUserId, "添加交易代码[" + tradecode.getTradeCodeDesp()
				+ "]");
		return tradecode.getTradeCodeId();
	}

	static public boolean hasStrikeTradeCode(Dao dao, int tradecodeId) {
		return !dao.queryPage(
				"select * from tradecode where striketadecodeid=?1", 0, 1,
				tradecodeId).isEmpty();
	}

	static public void delete(Dao dao, long loginUserId, Integer[] tradecodeId) {
		for (int i = 0; i < tradecodeId.length; i++) {
			EditedTradeCode u = find(dao, tradecodeId[i]);
			if (u == null)
				throw new AppException("要删除的交易代码[" + tradecodeId + "]不存在.");
			if (hasStrikeTradeCode(dao, tradecodeId[i]))
				throw new AppException("交易代码[" + u.getTradeCodeDesp()
						+ "]下定义了冲正交易代码，不能删除.");
			dao.delete(u, converter);
			addUserLog(dao, loginUserId, "删除交易代码[" + u.getTradeCodeId() + "]");
		}
		CacheHelper.tradeCodeMap.removeAll(tradecodeId);
	}

	static public void edit(Dao dao, long loginUserId, EditedTradeCode tradecode) {
		BaseTradeCode u = find(dao, tradecode.getTradeCodeId());
		if (u == null)
			throw new AppException("要编辑的交易代码[" + tradecode.getTradeCodeId()
					+ "]不存在.");
		if (!tradecode.getTradeCodeDesp().equals(u.getTradeCodeDesp())
				&& tradecodeDespExists(dao, tradecode.getTradeCodeDesp()))
			throw new AppException("交易代码描述[" + tradecode.getTradeCodeDesp()
					+ "]已被占用.");
		dao.update(tradecode, converter);
		CacheHelper.tradeCodeMap.remove(tradecode.getId());
		addUserLog(dao, loginUserId, "编辑交易代码[" + tradecode.getTradeCodeId()
				+ "]");
	}

	static public QueryResult<EditedTradeCode> queryTradeCode(Dao dao,
			boolean queryCount, long loginUserId, Integer filter,
			String filterContent, int firstResult, int maxResults) {
		return query(dao, converter, queryCount, fieldDefs,
				jionWhereSql(true, filterContent), firstResult, maxResults);
	}

}
